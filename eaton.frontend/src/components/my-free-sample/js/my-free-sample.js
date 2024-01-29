//-----------------------------------
// My Free Samples Component
//-----------------------------------

$(document).ready(function () {

  // # ids
  let orderTable = document.getElementById('order-table');
  let orderId = '';

  if (orderTable) {
    $.ajax({
      type: 'GET',
      url: '/eaton/services/freeSampleOrders?endpoint=/order-center/order-management/v1/orders&orderType=Sample&orderStatus=SUBMITTED&offset=0&limit=100',
      success: function success(response) {

        if (response) {
          let tableResult = '';
          if (typeof response === 'string') {
            tableResult = JSON.parse(response);
          } else {
            tableResult = response;
          }
          ordertableUI(tableResult);
        }
      },
      error: function error(response) {
        console.log('ERROR', response);
      }
    });


  } else {
    return false;
  }




  // First table UI code
  function ordertableUI(tableResult) {

    if (tableResult && tableResult !== {} && !$.isEmptyObject(tableResult)) {

      let arr = tableResult.orders;

      arr.forEach(function (element) {

        let tr = document.createElement('tr');

        tr.innerHTML = '<td>' + element.orderStatus + '</td>' + '<td>' + "<a href='javascript:void(0)' value='" + element.orderId + "'>" + element.orderId + '</a>' + '</td>' + '<td>' + element.orderSubmittedDate + '</td>' + '<td>' + element.orderLastModifiedDate + '</td>' + '<td>' + element.orderSubmittedDate + '</td>';
        if (document.getElementById('order-table') === null) {
          return false;
        } else {
          document.getElementById('order-table').appendChild(tr);
        }
      });
    } else {
      $(orderTable).html('<p class=\'error-msg\'>You did not place any free-sample order yet.</p>');
    }

    // get the order id from here
    $(function () {
      $('#order-table a').click(function () {
        orderId = $(this).attr('value');
        getIdvalue(orderId);
        $('.order-details').removeClass('hide');
        $('.free-sample').addClass('hide');
      });
    });
  }

  // back button

  $('a.free-sample-back').click(function () {
    $('.order-details').addClass('hide');
    $('.free-sample').removeClass('hide');

  });

  // get the data of particular id
  function getIdvalue(orderId) {
    console.log(orderId);
    $.ajax({
      type: 'GET',
      url: '/eaton/services/freeSampleOrders?endpoint=/order-center/order-management/v1/orders&orderId=' + orderId,
      success: function success(response) {

        if (response) {
          let orderResult = '';
          if (typeof response === 'string') {
            orderResult = JSON.parse(response);
          } else {
            orderResult = response;
          }

          orderIdtable(orderResult);
        }
      },
      error: function error(response) {
        console.log('ERROR', response);
      }
    });
  }

  // created table of order id detail
  function orderIdtable(orderResult) {

    if (orderResult && orderResult !== {} && !$.isEmptyObject(orderResult)) {

      let data3 = orderResult.orders[0];

      $('#singleOrder li:last-child').remove();

      let li = document.createElement('li');

      li.innerHTML =
        '<span>' + data3.orderId + '</span>' +
        '<span>' + data3.orderStatus + '</span>' +
        '<span>' + data3.orderSubmittedDate + '</span>' +
        '<span>' + data3.orderLastModifiedDate + '</span>' +
        '<span>' + data3.orderSubmittedDate + '</span>';
      document.getElementById('singleOrder').appendChild(li);

      let data2 = orderResult.orders[0].orderLineItems;

      $('#idData tr').remove();

      data2.forEach(function (ele) {

        let tr = document.createElement('tr');

        tr.innerHTML = '<td>' + ele.lineItemId + '</td>' + '<td>' + ele.productId + '</td>' + '<td>' + ele.quantity + '</td>';

        document.getElementById('idData').appendChild(tr);
      });


    } else {
      $(orderTable).html('');
    }
  }
});