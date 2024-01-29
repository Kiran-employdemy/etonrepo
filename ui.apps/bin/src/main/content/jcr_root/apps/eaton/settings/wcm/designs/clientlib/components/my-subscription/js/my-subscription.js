/**
 *
 *
 *
 * - THIS IS AN AUTOGENERATED FILE. DO NOT EDIT THIS FILE DIRECTLY -
 * - Generated by Gulp (gulp-babel).
 *
 *
 *
 *
 */


//-----------------------------------
// Table with sorting and search
//-----------------------------------
'use strict';

var App = window.App || {};
App.dynamicTable = function () {
  var _this = this;

  var loadMoreLimit = $('.dynamic-table-section').data('load-more-limit');
  var initialTable = void 0;
  var subsTableHead = document.querySelectorAll('.my-subscription__table-head th');
  var itr = 0;
  var subsBatches = []; // maintaining array of number of 10s and remainder in total number of rows
  var subsTableBody = document.querySelector('.my-subscription__table-body') || {};
  var subscriptionLength = void 0; // get length of the table rows
  var subsTableRows = void 0;
  var subsInputBox = document.getElementById('subscriptionSearchBox');
  var loadMoreSubsBtn = document.querySelector('.my-subscription__load-more > button') || {};
  var clearSearchBtn = document.getElementById('clearMySubscription');
  var searchSubsBtn = document.getElementById('searchMySubscription');
  var searchSubsFlag = false; // to flag status of search result, to sort searched result only when true
  var errorPara = document.querySelector('.errorPara') || {};
  var noSearchErr = document.querySelector('.my-subscription__no-search-err') || {};
  var errorText = void 0;
  var firstName = document.getElementById('firstName');
  var lastName = document.getElementById('lastName');
  var expDate = document.getElementById('expDate');
  var countryName = document.getElementById('countryName');
  var cityName = document.getElementById('cityName');
  var stateName = document.getElementById('stateName');
  var postalCode = document.getElementById('postalCode');
  var toggleSwitch = document.querySelectorAll('.toggle input');
  var inputs = document.querySelectorAll('#resubmitForm input');
  var dateRegex = new RegExp('(0[1-9]|1[0-2])/?([0-9]{4}|[0-9]{2})$');
  var txtRegex = new RegExp('^[a-zA-Z ]*$');
  var creditCardTable = document.getElementById('creditCardTableBody');
  var creditcardarr = void 0;
  var submitSelectedCC = document.querySelector('#submitSelectedCC');
  var today = new Date();
  var countryList = [];
  var stateList = [];
  var selectedCC = void 0;
  var selectedEntitlementRow = void 0;
  var resubmitModalBtn = document.querySelectorAll('.resubmitModalBtn');

  var stripeReset = function stripeReset() {
    var stripeFlag = true;

    for (var i = 0; i < subsTableRows.length; i++) {
      if (subsTableRows[i].style.display !== 'none' && subsTableRows[i].classList.value === 'my-subscription__table-row') {
        if (stripeFlag) {
          subsTableRows[i].style.background = '#f8f8f8';
          stripeFlag = false;
        } else {
          subsTableRows[i].style.background = 'none';
          stripeFlag = true;
        }
      }
    }
  };

  // sorting
  var getCellValue = function getCellValue(tr, idx) {
    if (idx === 0) {
      return new Date(tr.children[idx].innerText) || new Date(tr.children[idx].textContent); // return date as a Date and not string
    } else {
      return tr.children[idx].innerText || tr.children[idx].textContent;
    }
  };

  var comparer = function comparer(idx, asc) {
    return function (a, b) {
      return function (v1, v2) {
        return v1 !== '' && v2 !== '' && !isNaN(v1) && !isNaN(v2) ? v1 - v2 : v1.toString().localeCompare(v2);
      }(getCellValue(asc ? a : b, idx), getCellValue(asc ? b : a, idx));
    };
  };

  var realSort = function realSort(x, trow) {
    Array.from(trow).sort(comparer(x, _this.asc = !_this.asc)).forEach(function (tr) {
      return subsTableBody.appendChild(tr);
    });
  };

  var sortMySubscription = function sortMySubscription(e) {
    if (e.target) {
      var el = e.target;
      var tgt = Array.from(el.parentNode.children).indexOf(el);
      el.classList.toggle('asc');
      for (var i = 0; i < subsTableHead.length; i++) {
        if (i !== tgt) {
          subsTableHead[i].className = '';
        }
      }

      var trow = document.querySelectorAll('.my-subscription__table-row');
      if (!searchSubsFlag) {
        trow.forEach(function (tr) {
          return tr.classList.remove('hide');
        });
        realSort(tgt, trow);
        for (var _i = subsBatches[itr]; _i < subscriptionLength; _i++) {
          subsTableRows[_i].classList.add('hide');
        }
      } else {
        realSort(tgt, trow);
      }
      stripeReset();
    }
  };

  // Load More
  var loadMore = function loadMore() {
    // remove display none from the rows
    for (var i = subsBatches[itr]; i < subsBatches[itr + 1]; i++) {
      subsTableRows[i].classList.remove('hide');
    }

    stripeReset();

    itr++; // maintaining a flag current position in iterations

    // once position reaches the end hide the button
    if (subsBatches[itr] >= subscriptionLength) {
      loadMoreSubsBtn.style.display = 'none';
    }
  };

  var clearMySubscription = function clearMySubscription() {
    if (searchSubsFlag) {
      subsInputBox.value = '';
      subsTableBody.innerHTML = initialTable;
      if (loadMoreSubsBtn) {
        loadMoreSubsBtn.style.display = '';
      }
      searchSubsFlag = false;
      itr = 0;
      searchSubsBtn.classList.remove('hide');
      clearSearchBtn.classList.add('hide');
      checkSearchResult();
    }
  };

  var checkSearchResult = function checkSearchResult() {
    var totalRows = document.querySelectorAll('.my-subscription__table-body > tr');
    var hiddenRows = document.querySelectorAll('.my-subscription__table-body > .hide');
    if (totalRows.length === hiddenRows.length) {
      noSearchErr.classList.remove('hide');
      errorPara.innerText = errorText.replace(/{{searchedText}}/g, '"' + subsInputBox.value + '"');
    } else {
      noSearchErr.className = 'my-subscription__no-search-err hide';
    }
  };

  var searchMySubscription = function searchMySubscription() {
    if (subsInputBox.value) {
      var filter = subsInputBox.value.toUpperCase().trim();
      for (var i = 0; i < subsTableRows.length; i++) {
        var actvId = subsTableRows[i].querySelectorAll('td')[2];
        var partNum = subsTableRows[i].querySelectorAll('td')[3];
        if (actvId || partNum) {
          var txtValue = actvId.textContent || actvId.innerText;
          var txtValue2 = partNum.textContent || partNum.innerText;
          if (txtValue.toUpperCase().indexOf(filter) > -1 || txtValue2.toUpperCase().indexOf(filter) > -1) {
            subsTableRows[i].classList.remove('hide');
          } else {
            subsTableRows[i].classList.add('hide');
          }
        }
      }
      searchSubsFlag = true;
      stripeReset();
      if (loadMoreSubsBtn) {
        loadMoreSubsBtn.style.display = 'none';
      }
      searchSubsBtn.classList.add('hide');
      clearSearchBtn.classList.remove('hide');
      checkSearchResult();
    }
  };

  var loadInitialData = function loadInitialData() {

    if (subsTableBody) {
      subscriptionLength = subsTableBody.rows.length;
      subsTableRows = document.querySelector('.my-subscription__table-body').getElementsByTagName('tr') || {};
      // capture initial table set, to restore when search is cleared
      initialTable = subsTableBody.innerHTML;
      errorText = errorPara.innerText;
    }

    // create sets of data
    for (var g = 1; g <= subscriptionLength / loadMoreLimit; g++) {
      subsBatches.push(loadMoreLimit * g);
    }

    if (subscriptionLength % loadMoreLimit > 0) {
      subsBatches.push(subsBatches[subsBatches.length - 1] + subscriptionLength % loadMoreLimit); // push the remainder in the array at the end
    }
  };

  // renewal and submit below

  // ON resubmit button CLICK This will collect row data
  var selectedEntitlement = function selectedEntitlement(e) {
    getCreditCards();
    document.getElementById('resubmitForm').reset();
    selectedEntitlementRow = {
      activationid: e.target.dataset.activationid,
      productid: e.target.dataset.productid,
      quantity: e.target.dataset.quantity,
      duration: e.target.dataset.duration
    };
    countryStates();
  };

  // post sync order

  var syncorder = function syncorder(payload) {
    console.log(payload);
    document.querySelector('.resubmit-success').classList.add('hidden');
    document.querySelector('.resubmit-failed').classList.add('hidden');
    $.ajax({
      type: 'POST',
      url: '/eaton/secure/ecommerce/syncorder',
      data: payload,
      success: function success(response) {
        document.querySelector('#confim-modal').style = 'display:block';
        document.querySelector('.resubmit-success').classList.remove('hidden');
      },
      error: function error(_error) {
        document.querySelector('#confim-modal').style = 'display:block';
        document.querySelector('.resubmit-failed').classList.remove('hidden');
      }
    });
  };
  // submitting with cc list
  var submitwexisting = function submitwexisting(e) {
    var payload = {
      productid: selectedEntitlementRow.productid,
      productidqualifier: 'CAT',
      quantity: selectedEntitlementRow.quantity,
      activationid: selectedEntitlementRow.activationId,
      transactiontype: 'renew',
      duration: selectedEntitlementRow.duration,
      payementtoken: selectedCC.paymentToken
    };
    syncorder(payload);
  };

  // Object to check form fields has values
  var inputValidator = {
    firstName: false,
    lastName: false,
    expDate: false,
    addressLineOne: false,
    countryName: false,
    stateName: false,
    cityName: false,
    postalCode: false
  };

  // state value changed
  var changeState = function changeState(e) {
    inputValidator.stateName = true;
    submitEnabler();
  };

  // country value changed
  var changeCountry = function changeCountry(e) {
    inputValidator.countryName = true;
    submitEnabler();
    var select = document.getElementById('stateName');
    select.innerHTML = '<option disabled>-Select-</option>'; // remove states from previous selection
    var userCountry = e.target.options[e.target.selectedIndex];
    var stateOptns = stateList.filter(function (o) {
      /*eslint-disable */
      return o.countryId == userCountry.dataset.countryId;
      /*eslint-enable */
    });
    for (var i = 0; i < stateOptns.length; i++) {
      var el = document.createElement('option');
      el.innerText = stateOptns[i].stateName;
      el.dataset.stateId = stateOptns[i].stateId;
      el.dataset.countryId = stateOptns[i].countryId;
      el.dataset.stateCode = stateOptns[i].stateCode;
      select.appendChild(el);
    }
  };

  // loads countryList
  var countryStates = function countryStates() {
    var url = '/eaton/my-eaton/fields';
    var select = document.getElementById('countryName');
    $.ajax({
      url: url,
      type: 'GET',
      dataType: 'json', // added data type
      success: function success(res) {
        countryList = res.validCountries;
        stateList = res.validStates;
        for (var i = 0; i < countryList.length; i++) {
          var el = document.createElement('option');
          el.innerText = countryList[i].countryName;
          el.dataset.countryCode = countryList[i].countryCode;
          el.dataset.countryId = countryList[i].countryId;
          select.appendChild(el);
        }
      },
      error: function error(err) {
        console.log(err);
      }
    });
  };

  // checked input fields and enables form submit
  var submitEnabler = function submitEnabler() {
    var buttonSend = document.getElementById('resubmitFormSubmit');
    var allTrue = Object.keys(inputValidator).every(function (item) {
      return inputValidator[item] === true;
    });
    console.log(allTrue);
    if (allTrue) {
      buttonSend.disabled = false;
    } else {
      buttonSend.disabled = true;
    }
  };

  // marks input fields as per filled or not
  var validator = function validator(e) {
    var name = e.target.getAttribute('name');
    if (e.target.value.length > 0) {
      inputValidator[name] = true;
    } else {
      inputValidator[name] = false;
    }
    submitEnabler();
  };

  var validate = function validate() {
    var userMonth = expDate.value;
    var today = new Date();
    var currentMonth = new Date(userMonth.slice(3), userMonth.slice(0, 2), 1);

    if (!dateRegex.test(expDate.value) || currentMonth < today || !txtRegex.test(firstName.value) || !txtRegex.test(lastName.value) || !txtRegex.test(cityName.value) || isNaN(postalCode.value)) {

      if (!dateRegex.test(expDate.value) || currentMonth < today) {
        expDate.style = 'border: 1px solid #ff0000';
      }
      if (!txtRegex.test(firstName.value)) {
        firstName.style = 'border: 1px solid #ff0000';
      }
      if (!txtRegex.test(lastName.value)) {
        lastName.style = 'border: 1px solid #ff0000';
      }
      if (!txtRegex.test(cityName.value)) {
        cityName.style = 'border: 1px solid #ff0000';
      }
      if (isNaN(postalCode.value)) {
        postalCode.style = 'border: 1px solid #ff0000';
      }
      return false;
    } else {
      return true;
    }
  };

  var autorenewal = function autorenewal(change, e) {
    var url = '/eaton/entitlement/update.nocache.json';
    var payload = {
      activationId: e.target.parentNode.parentNode.parentNode.parentNode.parentNode.children[3].textContent,
      AutoRenewal: change,
      AutoRenewalStatus: ''
    };

    $.ajax({
      type: 'POST',
      url: url,
      data: payload,
      success: function success(response) {
        console.log(response);
      },
      error: function error(_error2) {
        console.log(_error2);
        if (change === 'ON') {
          e.target.parentElement.parentNode.parentNode.parentNode.childNodes[3].classList.remove('hidden');
          e.target.parentElement.parentNode.parentNode.classList.add('hidden');
        }
        if (change === 'OFF') {
          e.target.checked = true;
        }
      }
    });
  };

  var onoff = function onoff(e) {
    // if true turn on auto renew else off
    e.target.checked ? autorenewal('ON', e) : autorenewal('OFF', e);
  };

  var selectThisCC = function selectThisCC(e) {
    creditcardarr = creditcardarr.creditCards;
    selectedCC = creditcardarr.find(function (o) {
      return o.creditCardNumber === e.target.dataset.cc;
    });
    document.querySelector('#submitSelectedCC').disabled = false;
  };

  var deleteThisCC = function deleteThisCC(e) {
    creditcardarr = creditcardarr.creditCards;
    creditcardarr = creditcardarr.find(function (o) {
      return o.creditCardNumber === e.target.dataset.cc;
    });

    var url = '/eaton/secure/ecommerce/creditcard?creditCardToken=';

    $.ajax({
      url: url + creditcardarr.paymentToken,
      type: 'DELETE',
      success: function success(result) {
        console.log(result);
        // delete Table rows here and call get cc
        document.querySelector('#creditCardTableBody').innerHTML = '';
        getCreditCards();
      },
      error: function error(_error3) {
        console.log(_error3);
      }
    });
  };

  var loadcc = function loadcc(cc) {
    var creditCards = cc.creditCards;
    creditCards.forEach(function (c) {
      var fullDate = new Date(c.expirationMonth, c.expirationYear, 1);

      var el = document.createElement('tr');

      var radioCell = el.insertCell(0);
      var radioCC = document.createElement('input');
      radioCC.setAttribute('type', 'radio');
      radioCC.setAttribute('name', 'radio');
      radioCC.setAttribute('class', 'cards-radio');
      radioCC.dataset.cc = c.creditCardNumber;
      radioCC.addEventListener('change', selectThisCC);
      radioCell.appendChild(radioCC);

      var iconCell = el.insertCell(-1);
      iconCell.innerHTML = '<span class="glyphicon glyphicon-credit-card"></span>';

      var creditCardTypeCell = el.insertCell(-1);
      creditCardTypeCell.setAttribute('class', 'capitalize');

      if (fullDate < today) {
        creditCardTypeCell.innerHTML = c.creditCardType + ' ending with ' + c.creditCardNumber + '<br> <span style="color: #f00">' + c.expirationMonth + '/' + c.expirationYear + ' Expired</span>';
      } else {
        creditCardTypeCell.innerHTML = c.creditCardType + ' ending with ' + c.creditCardNumber + '<br>' + c.expirationMonth + '/' + c.expirationYear;
      }

      var delCell = el.insertCell(-1);
      var del = document.createElement('a');
      del.setAttribute('href', 'javascript:void(0)');
      del.setAttribute('class', 'delete');
      del.textContent = 'Delete card';
      del.dataset.cc = c.creditCardNumber;
      del.addEventListener('click', deleteThisCC);
      delCell.appendChild(del);

      creditCardTable.appendChild(el);
      radioCC;
    });
  };
  var getCreditCards = function getCreditCards() {
    var ccurl = '/eaton/secure/ecommerce/creditcard';
    $.ajax({
      url: ccurl,
      type: 'GET',
      dataType: 'json', // added data type
      success: function success(res) {
        creditcardarr = res;
        loadcc(res); // To create table cells and push data in it
      },
      error: function error(err) {
        console.log(err);
      }
    });
  };

  var onSubmit = function onSubmit(token, ccBrand) {
    var addressLine2 = document.getElementById('addressLineTwo');
    if (addressLine2.value === null) {
      addressLine2 = document.getElementById('addressLineTwo').value;
    } else {
      addressLine2 = '';
    }

    var addr = {
      addressLine1: document.querySelector('#addressLineOne').value,
      addressLine2: addressLine2,
      city: document.querySelector('#cityName').value,
      state: document.querySelector('#stateName').options[document.querySelector('#stateName').selectedIndex].dataset.stateCode,
      postalCode: document.querySelector('#postalCode').value,
      country: document.querySelector('#countryName').options[document.querySelector('#countryName').selectedIndex].dataset.countryCode
    };
    var paymentInfoFormSubmit = {
      addCreditCard: {
        firstName: document.querySelector('#firstName').value,
        lastName: document.querySelector('#lastName').value,
        creditCardNumber: token.content.paymentInformation.card.number.maskedValue.slice(-4),
        creditCardType: ccBrand,
        expirationMonth: document.querySelector('#expDate').value.split('/')[0],
        expirationYear: document.querySelector('#expDate').value.split('/')[1],
        creditCardTransientToken: token.jti,
        creditCardBillingAddress: addr
      }
    };
    console.log(paymentInfoFormSubmit);
    // ajax post save

    $.ajax({
      type: 'POST',
      url: '/eaton/secure/ecommerce/creditcard',
      data: paymentInfoFormSubmit,
      success: function success(response) {
        console.log(response);
        var payload = {
          productId: selectedEntitlementRow.productid,
          productIdQualifier: 'CAT',
          quantity: selectedEntitlementRow.quantity,
          activationId: selectedEntitlementRow.activationId,
          transactionType: 'renew',
          duration: selectedEntitlementRow.duration,
          paymentToken: response.paymentToken
        };
        syncorder(payload);
      },
      error: function error(_error4) {
        console.log(_error4);
      }
    });
  };

  var parseJwt = function parseJwt(token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function (c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
  };

  var initializeFlex = function initializeFlex() {
    $.ajax({
      type: 'GET',
      url: '/eaton/secure/ecommerce/cyb/capturecontext',
      success: function success(response) {
        var captureContext = response.keyId;

        var myStyles = {
          input: {
            'font-size': '16px',
            'font-family': 'helvetica, tahoma, calibri, sans-serif',
            color: '#555'
          },
          ':focus': { color: 'blue' },
          ':disabled': { cursor: 'not-allowed' },
          valid: { color: '#3c763d' },
          invalid: { color: '#a94442' }
        };
        /*eslint-disable */
        var flex = new Flex(captureContext);
        /*eslint-enable */
        var microform = flex.microform({ styles: myStyles });
        var cNumber = microform.createField('number', { placeholder: '' });
        var securityCode = microform.createField('securityCode', { placeholder: '•••' });
        var ccBrand = void 0;
        cNumber.load('#number-container');
        securityCode.load('#securityCode-container');
        cNumber.on('change', function (data) {
          if (data.card.length > 0) {
            ccBrand = data.card[0].brandedName;
          }
        });
        document.querySelector('#resubmitFormSubmit').addEventListener('click', function () {
          if (validate() && document.querySelector('#rc').checked) {
            var options = {
              expirationMonth: document.querySelector('#expDate').value.split('/')[0],
              expirationYear: document.querySelector('#expDate').value.split('/')[1]
            };
            microform.createToken(options, function (err, token) {
              if (err) {
                console.log(err);
              } else {
                var TransientToken = JSON.stringify(token);
                TransientToken = parseJwt(TransientToken);
                console.log(TransientToken);
                onSubmit(TransientToken, ccBrand);
              }
            });
          }
        });
      },
      error: function error(_error5) {
        console.log(_error5);
      }
    });
  };

  var bindEvents = function bindEvents() {
    if (subsTableBody) {
      if (subscriptionLength > loadMoreLimit) {
        loadMoreSubsBtn.addEventListener('click', loadMore);
      }
      clearSearchBtn.addEventListener('click', clearMySubscription);
      searchSubsBtn.addEventListener('click', searchMySubscription);
      subsTableHead.forEach(function (el) {
        return el.addEventListener('click', sortMySubscription);
      });
      if (toggleSwitch) {
        toggleSwitch.forEach(function (ts) {
          return ts.addEventListener('change', onoff);
        });
      }
      initializeFlex();
    }
    inputs.forEach(function (input) {
      input.addEventListener('keyup', validator);
    });
    resubmitModalBtn.forEach(function (resubmit) {
      resubmit.addEventListener('click', selectedEntitlement);
    });
    if (submitSelectedCC) {
      submitSelectedCC.addEventListener('click', submitwexisting);
    }
    countryName.addEventListener('change', changeCountry);
    stateName.addEventListener('change', changeState);
  };

  var init = function init() {
    loadInitialData();
    bindEvents();
  };

  /**
   * If containing DOM element is found, Initialize and Expose public methods
   */
  if ($('.my-subscription__table-container').length > 0) {
    init();
  }
}();