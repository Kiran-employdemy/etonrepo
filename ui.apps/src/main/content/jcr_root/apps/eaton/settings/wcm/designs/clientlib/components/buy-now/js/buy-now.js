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


/* eslint-disable no-undef */

'use strict';

var App = window.App || {};
App.buyNow = function () {
  var _this = this;

  var logDelim = ' :: ';
  var modalLoggerPrefix = 'Buy Now Modal' + logDelim;

  var buyNowSelPrefix = '#buyNow';

  var htbBtnSel = '#dHowtoBuy';
  var htbListSel = htbBtnSel + 'List';

  var modalSel = buyNowSelPrefix + 'Modal';

  var triggerClass = 'buy-now__modal__trigger';

  var geolocationSel = buyNowSelPrefix + 'Geolocation';
  var locationSel = buyNowSelPrefix + 'Location';
  var locationSearchSel = locationSel + 'Search';
  var specialChars = /[`!@#$%^&*()_+\-=\[\]{};':"\\|.<>\/?~]/;

  var filterSel = '.buy-now__filter';
  var filterInputSel = filterSel + ' .form-check-input';
  var filterInputCheckedSel = filterInputSel + ':checked';

  var msgSel = buyNowSelPrefix + 'Msg';
  var alertErrorClass = 'alert-danger';
  var alertWarnClass = 'alert-warning';
  var alertInfoClass = 'alert-info';

  var tryAgainLater = ' Please try again later.';
  var loadError = 'Service is temporary unavailable.' + tryAgainLater;
  var locationMissingWarn = 'Please enter a location in the search box';
  var locationNotFoundWarn = 'The location entered cannot be found.';
  var geolocationFindError = 'Unable to find your current location.' + tryAgainLater;
  var geolocationBrowserWarn = 'Current location service is not supported by your browser.';
  var resultsNoneError = '0 results found';
  var configSetupError = 'Configuration setup failed. Cannot find ';
  var locationSpecialCharsWarn = locationMissingWarn + ' without special characters (commas are allowed).';

  var tableSel = buyNowSelPrefix + 'Table';
  var tableBodySel = tableSel + 'Body';
  var tableHeaderSel = tableSel + 'Header';
  var tableRowSel = '.distributor-table-row';
  var tableRowHiddenSel = tableRowSel + ':hidden';
  var tableRowVisibleSel = tableRowSel + ':visible';
  var diPickShipFlagSel = '.distributor-pick-ship-flag';

  var loadBtnSel = buyNowSelPrefix + 'Load';
  var loadSpinSel = loadBtnSel + 'Spin';
  var loadTextSel = loadBtnSel + 'Text';

  var setMouserCountry = '';

  /*
  * object for storing request parameters for apigee request
  */
  var apigeeRequestParams = {
    endpoint: null,
    latitude: null,
    longitude: null,
    radius: null,
    startIndex: null,
    pageSize: null
  };

  /*
  * object for storing configurations for apigee and google maps requests
  */
  var requestsConfig = {
    apigeeServletPath: null,
    apigeeServiceTimeout: null,
    googleServletPath: null,
    googleServiceTimeout: null,
    diEndpointPrefix: null,
    diEndpointSuffix: null,
    loadSize: null
  };

  /*
  * object for storing configurations for modal
  */
  var modalConfig = {
    loadText: null,
    loadTextAlt: null,
    tableRowLinkText: null,
    tableRowLinkDefault: null
  };

  /*
  * init
  */
  var init = function init() {

    try {
      /* eslint-disable no-undef */
      if (typeof dataLayer.productSku === 'undefined' || dataLayer.productSku.length === 0) {
        throw 'dataLayer.productSku';
      }
      /* eslint-enable no-undef */
      if (typeof $(modalSel) === 'undefined') {
        throw 'modal element';
      }
    } catch (err) {
      console.error([modalLoggerPrefix, configSetupError, err].join(logDelim));
    }

    if (isEnabled) {

      // move modal to top-level of body
      $(modalSel).appendTo('body');

      checkDIproduct();
      setConfig();
      bindListeners();
    }
  };

  // check Distributor inventory product

  function checkDIproduct() {

    if (productAbl && productAbl !== {} && !$.isEmptyObject(productAbl)) {
      var diproduct = productAbl.product.Availability;

      if (diproduct === 'yes') {
        setMouserCountry = mousCountry;
      } else {

        return false;
      }
    } else {

      return false;
    }
  }

  var isEnabled = function isEnabled() {

    var hasDropdownOpts = $(htbBtnSel).data('toggle') === 'dropdown';
    if (hasDropdownOpts) {

      $(htbListSel).each(function (index, element) {
        if ($(this).hasClass(triggerClass)) {
          return true;
        }
      });
    }
  };

  /*
  * set most* configurable properties
  */
  var setConfig = function setConfig() {

    requestsConfig.diEndpointPrefix = $(modalSel).data('di-endpoint-prefix');
    requestsConfig.diEndpointSuffix = $(modalSel).data('di-endpoint-suffix');
    requestsConfig.apigeeServletPath = $(modalSel).data('apigee-servlet-path');
    requestsConfig.googleServletPath = $(modalSel).data('google-servlet-path');
    requestsConfig.loadSize = $(loadBtnSel).data('load-size');
    requestsConfig.apigeeServiceTimeout = $(modalSel).data('apigee-service-timeout');
    requestsConfig.googleServiceTimeout = $(modalSel).data('google-service-timeout');

    /* eslint-disable no-undef */
    apigeeRequestParams.endpoint = requestsConfig.diEndpointPrefix + dataLayer.productSku + requestsConfig.diEndpointSuffix;
    /* eslint-enable no-undef */
    apigeeRequestParams.radius = $(modalSel).data('default-radius');
    apigeeRequestParams.pageSize = $(modalSel).data('page-size');

    /* adding some Parameter for DI */
    if (setMouserCountry) {
      if (setMouserCountry === 'us') {
        apigeeRequestParams.countryId = setMouserCountry;
        apigeeRequestParams['X-Process-Flow'] = 'souriau';
      } else {
        apigeeRequestParams.countryId = setMouserCountry;
        apigeeRequestParams['X-Process-Flow'] = 'souriau-row';
      }
    }

    modalConfig.loadText = $(loadTextSel).data('load-text');
    modalConfig.loadTextAlt = $(loadTextSel).data('load-text-alt');
    modalConfig.tableRowLinkText = $(modalSel).data('table-row-link-text');
    modalConfig.tableRowLinkDefault = $(modalSel).data('table-row-link-default');
  };

  /*
  * bind functions to buttons and window
  */
  var bindListeners = function bindListeners() {

    $(locationSearchSel).on('click', doSearchLocation);
    $(geolocationSel).on('click', doGeolocation);
    $(loadBtnSel).on('click', doLoadBtnOps);
    $(filterInputSel).change(doFilterPickShipFlag);
  };

  /*
  * display an alert message
  * @param
  */
  var displayMsg = function displayMsg(alertClass, msg) {

    $(msgSel).text(msg);
    $(msgSel).hasClass(alertErrorClass) && $(msgSel).removeClass(alertErrorClass);
    $(msgSel).hasClass(alertWarnClass) && $(msgSel).removeClass(alertWarnClass);
    $(msgSel).hasClass(alertInfoClass) && $(msgSel).removeClass(alertInfoClass);
    $(msgSel).addClass(alertClass);
    $(msgSel).show();
  };

  /*
  * fetch a new batch of data from apigee/di
  */
  var fetchData = function fetchData() {

    $.ajax({
      type: 'get',
      url: requestsConfig.apigeeServletPath,
      async: true,
      cache: false,
      timeout: requestsConfig.apigeeServiceTimeout * 1000,
      data: apigeeRequestParams,
      beforeSend: function beforeSend() {
        $(loadBtnSel).is(':visible') && $(loadBtnSel).hide();
        $(loadBtnSel).text(modalConfig.loadText);
        $(loadSpinSel).is(':hidden') && $(loadSpinSel).show();
        $(msgSel).is(':visible') && $(msgSel).hide();
        $(filterInputCheckedSel).length > 0 && $(filterInputCheckedSel).prop('checked', false);
      },
      success: function success(response) {

        if (response.errors) {
          displayMsg(alertErrorClass, loadError);
          stopLoading(false, modalConfig.loadTextAlt);
        } else if (response === '') {
          displayMsg(alertInfoClass, resultsNoneError);
          stopLoading(false, modalConfig.loadText);
        } else if (!response.distributors) {
          displayMsg(alertErrorClass, loadError);
          stopLoading(false, modalConfig.loadTextAlt);
        } else {

          generateRows(response);
          if ($(tableBodySel).children().length > 0) {
            $(tableHeaderSel).is(':hidden') && $(tableHeaderSel).show();
            var amtToShow = $(tableRowHiddenSel).length >= requestsConfig.loadSize ? requestsConfig.loadSize : $(tableRowHiddenSel).length;
            $(tableRowHiddenSel).slice(0, amtToShow).show();
            stopLoading(true, modalConfig.loadText);
          } else {
            $(tableHeaderSel).is(':visible') && $(tableHeaderSel).hide();
            stopLoading(false, modalConfig.loadText);
            displayMsg(alertInfoClass, resultsNoneError);
          }
        }
      },
      error: function error(jqXHR, status, _error) {
        displayMsg(alertErrorClass, loadError);
        console.error([loadError, status, _error].join(logDelim));
        stopLoading(false, modalConfig.loadTextAlt);
      }
    });
  };

  /*
  * Create new table rows with response from apigee/di
  */
  var generateRows = function generateRows(response) {

    var cnt = 0;

    $.each(response.distributors, function (index, element) {

      if (element.participationScore !== 0) {

        cnt += 1;

        var urlMod = null;
        if (element.URL !== null) {
          urlMod = element.URL;
        } else if (element.diURL !== null) {
          urlMod = element.diURL;
        } else {
          urlMod = modalConfig.tableRowLinkDefault;
        }

        var pickShipFlagMod = element.pickShipFlag === null ? '' : element.pickShipFlag;

        var locationMod = element.city !== null || element.state !== null ? [element.city, element.state].join(', ') : '';

        $(tableBodySel).append(['<tr hidden class="distributor-table-row" id="distributor-table-row_', cnt, '">', '<td class="distributor-name-location">', '<p class="distributor-name">', element.name, '</p>', '<p class="distributor-location">', locationMod, '</p>', '</td>', '<td class="distributor-quantity buy-now__mobile">', element.totalQuantityOnHand, '</td>', '<td class="distributor-pick-ship-flag buy-now__mobile">', pickShipFlagMod, '</td>', '<td class="distributor-url buy-now__mobile">', '<a href="', urlMod, '" target="_blank">', '<i class="icon icon-link-external d-inline" aria-hidden="true"></i>', '<span class="d-inline">', modalConfig.tableRowLinkText, '</span></a></td>', '</tr>'].join(''));
      }
    });
  };

  /*
  * Do actions needed when a process is complete
  */
  var stopLoading = function stopLoading(showLoad, loadText) {

    // Load Button
    $(loadTextSel).text(loadText);
    showLoad ? $(loadBtnSel).show() : $(loadBtnSel).hide();

    // Spinner
    $(loadSpinSel).is(':visible') && $(loadSpinSel).hide();
  };

  /*
  * load button handler
  */
  var doLoadBtnOps = function doLoadBtnOps(event) {

    event.preventDefault();

    if ($(tableRowHiddenSel).length <= requestsConfig.loadSize) {
      $(tableRowHiddenSel).slice(0, $(tableRowHiddenSel).length).show();
      stopLoading(false, modalConfig.loadText);
    } else {
      $(tableRowHiddenSel).slice(0, requestsConfig.loadSize).show();
      stopLoading(true, modalConfig.loadText);
    }
  };

  /*
  * search for distributors by location keywords
  * -uses google geocoding servlet and google service to get coordinates from keywords
  */
  var doSearchLocation = function doSearchLocation(event) {

    event.preventDefault();

    var location = $(locationSel).val();
    if (!location) {
      clearTable();
      $(loadBtnSel).is(':visible') && $(loadBtnSel).hide();
      displayMsg(alertWarnClass, locationMissingWarn);
    } else {

      var containsSpecialChars = specialChars.test(location);

      if (containsSpecialChars) {
        clearTable();
        $(loadBtnSel).is(':visible') && $(loadBtnSel).hide();
        displayMsg(alertWarnClass, locationSpecialCharsWarn);
      } else {

        $.ajax({
          type: 'get',
          url: requestsConfig.googleServletPath,
          async: true,
          cache: false,
          timeout: requestsConfig.googleServiceTimeout * 1000,
          data: {
            address: location
          },
          beforeSend: function beforeSend() {
            clearTable();
            $(loadBtnSel).is(':visible') && $(loadBtnSel).hide();
            $(loadSpinSel).is(':hidden') && $(loadSpinSel).show();
            $(msgSel).is(':visible') && $(msgSel).hide();
          },
          success: function success(response) {

            if (response === '') {
              displayMsg(alertWarnClass, locationNotFoundWarn);
            } else {

              var responseJson = JSON.parse(response);
              if (responseJson.lat && responseJson.lng) {
                apigeeRequestParams.latitude = responseJson.lat;
                apigeeRequestParams.longitude = responseJson.lng;
                apigeeRequestParams.startIndex = 0;
                fetchData();
              }
            }
          },
          error: function error(jqXHR, status, _error2) {
            displayMsg(alertErrorClass, loadError);
            console.error([loadError, status, _error2].join(logDelim));
            stopLoading(false, modalConfig.loadTextAlt);
          }
        });
      }
    }
  };

  /*
  * Remove table header and all rows (hidden and visible)
  */
  var clearTable = function clearTable() {
    typeof $(tableRowSel) !== 'undefined' && $(tableRowSel).remove();
    $(tableHeaderSel).is(':visible') && $(tableHeaderSel).hide();
  };

  /*
  * Callback function for navigator service
  */
  var geolocationSuccess = function geolocationSuccess(pos) {

    clearTable();
    apigeeRequestParams.latitude = pos.coords.latitude;
    apigeeRequestParams.longitude = pos.coords.longitude;
    apigeeRequestParams.startIndex = 0;
    fetchData();
  };

  /*
  * Callback function for navigator service
  */
  var geolocationError = function geolocationError(err) {
    clearTable();
    displayMsg(alertErrorClass, geolocationFindError);
  };

  /*
  * Requests user's current location
  */
  var doGeolocation = function doGeolocation(event) {

    event.preventDefault();

    clearTable();
    $(locationSel).val('');
    $(loadBtnSel).is(':visible') && $(loadBtnSel).hide();
    $(loadSpinSel).is(':hidden') && $(loadSpinSel).show();
    $(msgSel).is(':visible') && $(msgSel).hide();

    if (!navigator.geolocation) {
      displayMsg(alertWarnClass, geolocationBrowserWarn);
    } else {
      navigator.geolocation.getCurrentPosition(geolocationSuccess, geolocationError);
    }
  };

  /*
  * Distributor inventory api doesn't have pickShipFlag as an api parameter upon creation.
  * This function provides a workaround for filtering by pickShipFlag until api parameter is implemented.
  */

  var doFilterPickShipFlag = function doFilterPickShipFlag() {

    if (!apigeeRequestParams.latitude || !apigeeRequestParams.longitude) {
      displayMsg(alertWarnClass, locationMissingWarn);
    } else {

      $(_this).prop('checked', !$(_this).prop('checked'));

      if ($(filterInputCheckedSel).length === 0) {

        $(tableRowSel).show();
      } else {
        $(tableRowSel).hide();

        $(filterInputCheckedSel).each(function () {

          var value = $(this).data('filter-value');
          $(diPickShipFlagSel).each(function (index, element) {
            if (element.innerText.toLowerCase().includes(value)) {
              $(element).parent().show();
            }
          });
        });
      }

      if ($(tableRowVisibleSel).length === 0) {
        displayMsg(alertInfoClass, resultsNoneError);
        $(tableHeaderSel).is(':visible') && $(tableHeaderSel).hide();
      } else {
        $(msgSel).hide();
        $(tableHeaderSel).is(':hidden') && $(tableHeaderSel).show();
      }
      stopLoading(false, modalConfig.loadText);
    }
  };

  // eslint-disable-next-line no-undef
  dataLayer.pageType === 'product sku' && init();
}();