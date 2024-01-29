

//-----------------------------------
// free-Sample-Stock-Availability
// -----------------------------------

let addNewbutton = document.getElementById('addNewbutton');
let mousCountry = '';
let productAbl = '';
let newcart = document.getElementById('newcart');
let _endIndexSkuPage = $(location).attr('pathname').indexOf('skuPage.') + 8;
let catalogNumber = $(location).attr('pathname').substring(_endIndexSkuPage, $(location).attr('pathname').indexOf('.', _endIndexSkuPage));


// catalog API

function catalogAPI() {
  $.ajax({
    type: 'GET',
    url: '/eaton/services/freeSampleStockAvailability.catalogId$' + catalogNumber + '.html',
    async: false,
    success: function success(response) {
      if (response) {
        if (typeof response === 'string') {
          productAbl = JSON.parse(response);
        } else {
          productAbl = response;
        }
      }

      checkCountry(productAbl);
    },
    error: function error(response) {
      console.log('ERROR', response);
    }
  });

}

if (addNewbutton) {

  catalogAPI();
  findCountry();

  // For user cart checking

  const linkattr = document.querySelector('a.free-btn');
  let hasattr = linkattr.hasAttribute('data-cta-link');

  if (hasattr) {
    let attrValue = linkattr.getAttribute('data-cta-link');
    linkattr.setAttribute('href', attrValue);
  }

  else {
    linkattr.setAttribute('id', 'checkUser');
    linkattr.setAttribute('href', 'javascript:void(0)');
  }

  let checkUser = document.getElementById('checkUser');

  if (checkUser) {
    document.getElementById('checkUser').addEventListener('click', function () {
      checkUserCart();
    });
  }
  // Create a new Cart for product

  newcart.onclick = function () {
    deleteCart();
  };


}

// Check  mouser Country for product

function checkCountry(productAbl) {

  if (productAbl && productAbl !== {} && !$.isEmptyObject(productAbl)) {

    let vals = productAbl.product.Availability;

    if (vals === 'yes') {

      if (mousCountry === 'us' || mousCountry === 'ca' || mousCountry === 'mx' || mousCountry === 'jp' || mousCountry === 'cn') {

        $('.sample-btn a').removeClass('hide');
      } else {

        $('.sample-btn a').removeClass('hide');
      }
    } else {

      return false;
    }
  } else {

    return false;
  }
}

// Find country from URL

function findCountry() {
  let pathVal = location.pathname;
  let tempVal = '';
  tempVal = pathVal.split('.');
  tempVal = tempVal[0].split('-');
  mousCountry = tempVal[1].split('/')[0];
}

// Check user has cart or not

function checkUserCart() {
  $.ajax({
    type: 'GET',
    url: '/eaton/secure/ecommerce/cart',
    success: function success(response) {
      if (response) {
        let overview = '';
        if (typeof response === 'string') {
          overview = JSON.parse(response);
        } else {
          overview = response;
        }
        checkProductPrice(overview);
      }
    },
    error: function error(response) {
      console.log('ERROR', response);
    }
  });
}

// Check product price and same catalog number

function checkProductPrice(response) {
  if (!response.cartItems) {
    return false;
  }

  let unitprice = response.cartItems.map(function (e) {
    return e.unitPrice;
  });

  let catalogNumCart = response.cartItems.map(function (e) {
    return e.catalogNumber;
  });
  let cartitemCount = response.cartItems.length;

  if (cartitemCount === 0) {
    sendCart();
    redirectPage();
  } else {

    if (unitprice[0] === 0) {
      if (catalogNumCart.indexOf(catalogNumber) !== -1) {
        $('#same-prod').modal('show');
      } else {
        sendCart();
        redirectPage();
      }
    } else {
      $('#prod-sold').modal('show');
    }
  }
}

function redirectPage() {

  let path = window.location.href.split('/').slice(0, -1).join('/');
  window.location = path + '/secure/shopping-cart.html';
  // update cart count value after adding a cart
  let getCookieValue = $.cookie('etn-cart-count');
  if (getCookieValue) {
    let newCount = parseInt(getCookieValue) + 1;
    $.cookie('etn-cart-count', newCount, { path: '/', domain: window.location.hostname });
  }

}

function sendCart() {

  let customerNumber = '000123';
  let erpsystem = 'MFGSOR';
  let productpath = location.pathname;

  let value2 = {
    customerNumber: customerNumber,
    cartType: 'Sample',
    salesOrg: {
      org: '',
      division: '',
      channel: ''
    },
    cartItems: [
      {
        productId: catalogNumber,
        productIdQualifier: 'CAT',
        unitPrice: 0,
        quantity: 1,
        productPath: productpath
      }
    ]
  };

  $.ajax({
    type: 'POST',
    url: '/eaton/secure/ecommerce/cart?operation=addtocart&erpSystem=' + erpsystem,
    data: JSON.stringify(value2),
    dataType: 'json',
    headers: { 'Content-Type': 'application/json' },
    success: function (response) {
      console.log(response);
    }

  });

}

function deleteCart() {
  $.ajax({
    type: 'DELETE',
    url: '/eaton/secure/ecommerce/cart?operation=deleteentirecart',
    success: function success(response) {
      console.log(response);
    }

  });
  $.removeCookie('etn-cart-count');
  sendCart();
  redirectPage();
}
