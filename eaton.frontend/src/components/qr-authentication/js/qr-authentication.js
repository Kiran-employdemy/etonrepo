/* eslint-disable no-mixed-spaces-and-tabs */
'use strict';

$(document).ready(function () {

  let serialSessionObject = {};

  let _endIndexSkuPage = $(location).attr('pathname').indexOf('skuPage.') + 8;
  let catalogNumber = $(location).attr('pathname').substring(_endIndexSkuPage, $(location).attr('pathname').indexOf('.', _endIndexSkuPage));
  let catlogNumberCompare;
  function retrieve() {
    if (sessionStorage.getItem('serial_auth_verified') === null) {
      return;
    } else {
      $('#correctAuthCodeMessage').removeClass('hide');
      let retrievedObject = JSON.parse(sessionStorage.getItem('serial_auth_verified'));
      catlogNumberCompare = retrievedObject.catalogNum;
      let sessionAuthenticationCode = retrievedObject.authentication_code;
      $('#serial_auth_verified').text(sessionAuthenticationCode);
    }
  }


  let urlTab = window.location.href;
  let matchUrl = urlTab.includes('resources');
  if (matchUrl === true) {
    retrieve();
    $('#serial__verified').addClass('hide');
    if (catlogNumberCompare !== catalogNumber) {
      sessionStorage.clear();
      $('#correctAuthCodeMessage').addClass('hide');

    }

  }

  if (matchUrl === false) {
    $('#serial__verified').addClass('hide');
  }

  if ($('.eaton-qr-authenticated').length) {
    if (document.getElementById('authenticate')) {
      $('.eaton-product-tabs__tab-panel ').css('display', 'block');
    }
    const qrValidationFlag = $('.secondary-content-accordion').data('qrValidationFlag');
    const serialAuthFlag = $('.secondary-content-accordion').data('serialAuthFlag');
    if (qrValidationFlag) {
      $('#authenticateflag').addClass('hide');
      $('#main__authcode').removeClass('hide');
      if (serialAuthFlag === false) {
        $('#authentication_code_check').addClass('hide');
        $('.automatic_success_message').removeClass('hide');
        $('#serialNumberSubmitButton').click(function () {
          $('.serial_number_verified').addClass('hide');
        });
      } else {
        $('#collapse-authenticate').collapse('show');
      }
    }
    let catalogNumber = '';
    const captchaRequired = $('.secondary-content-accordion').data('captchaRequired');
    $('.report-issue--close').click(function () {
      $('#report-close').addClass('hide');
      $('#report-form').addClass('hide');
      $('#success__message').addClass('hide');
    });

    $('#resetSerialNumber').click(function () {
      $('#authenticateflag').removeClass('hide');
      $('#main__authcode').addClass('hide');
      $('#authentication_code').val('');
      $('#correctAuthCodeMessage').addClass('hide');
      $('#report-form').addClass('hide');
      $('#success__message').addClass('hide');
      $('#authCodeText').val('');
    });

    $('#serialNumberSubmitButton').click(function () {
      let serialNumber = $('#authentication_code').val().trim();
      if (null === serialNumber || serialNumber === '') {
        serialNumberError();
        $('#report-form').removeClass('hide');
      } else {
        let endIndexSkuPage = $(location).attr('pathname').indexOf('skuPage.') + 8;
        catalogNumber = $(location).attr('pathname').substring(endIndexSkuPage, $(location).attr('pathname').indexOf('.', endIndexSkuPage));
        $.ajax({
          url: '/eaton/qr/epasSoaServiceValidation.json',
          data: 'serialNumber=' + serialNumber + '&catalogNumber=' + catalogNumber + '&action=validateSerialManualFlow',
          success: function success(response) {
            document.getElementById('eventId').value = response.eventId;
            if (response.errorCode === 'ERRCD00') {
              let authnumber = document.getElementById('authentication_code').value;
              document.getElementById('serial_verified_number').innerHTML = authnumber;
              if ($('.secondary-content-accordion').data('serialAuthFlag') === true) {
                $('#authenticateflag').addClass('hide');
              } else if ($('.secondary-content-accordion').data('serialAuthFlag') === false) {
                $('#correctAuthentication').removeClass('hide');
                $('#collapse-authenticate').collapse('hide');
                $('#authenticateflag').removeClass('hide');
                $('#authentication_code').val('');
                $('#main__authcode').addclass('hide');
              }
              $('#main__authcode').removeClass('hide');
              $('#invalidSerialErrorMsg').addClass('hide');
              $('#suspectSerialMessage').addClass('hide');
              $('#SerialCatalogMismatch').addClass('hide');
              $('#SerialCatalogMismatchMessage').addClass('hide');
              $('#report-form').addClass('hide');
            } else if (response.errorCode === 'ERRCD01') {
              serialNumberError();
              $('#report-form').removeClass('hide');
            } else if (response.errorCode === 'ERRCD02') {
              $('#main__authcode').addClass('hide');
              $('#suspectSerialMessage').removeClass('hide');
              $('#invalidSerialErrorMsg').addClass('hide');
              $('#SerialCatalogMismatch').addClass('hide');
              $('#SerialCatalogMismatchMessage').addClass('hide');
              $('#report-form').removeClass('hide');
            } else if (response.errorCode === 'ERRCD03') {
              $('#main__authcode').addClass('hide');
              $('#SerialCatalogMismatch').removeClass('hide');
              $('#SerialCatalogMismatchMessage').removeClass('hide');
              $('#serialNumCatalogMatch').attr('href', response.url);
              $('#suspectSerialMessage').addClass('hide');
              $('#invalidSerialErrorMsg').addClass('hide');
              $('#correctAuthentication').addClass('hide');
              $('#report-form').removeClass('hide');
            } else {
              $('#invalidSerialErrorMsg').removeClass('hide');
              $('#main__authcode').addClass('hide');
              $('#suspectSerialMessage').addClass('hide');
              $('#SerialCatalogMismatch').addClass('hide');
              $('#SerialCatalogMismatchMessage').addClass('hide');
              $('#correctAuthentication').addClass('hide');
              $('#report-form').removeClass('hide');
            }
          },
          error: function error(response) {
            console.log('exception in QRServlet Response');
          },

          type: 'POST'
        });
      }
    });

    $('#authCodeCheck').click(function () {
      /* eslint-disable no-undef*/
      let inValidCaptcha = false;
      if (captchaRequired) {
        if (typeof grecaptcha.getResponse() !== undefined) {
          let responseCaptcha = grecaptcha.getResponse();
          if (responseCaptcha === '' || responseCaptcha === undefined || responseCaptcha.length === 0) {
            inValidCaptcha = true;
            $('#recaptcha_error').removeClass('hide');
          } else {
            $('#recaptcha_error').hide();
          }
        }
      }
      /* eslint-enable no-undef, new-cap*/
      let authCode = $('#authCodeText').val().trim();
      let serialNumber = $('#authentication_code').val().trim();
      let otp = $('#authCodeText').val().trim();
      if (qrValidationFlag) {
        serialNumber = $('#qr_serial_number').text();
      }
      if (null === authCode || authCode === '' || otp === null) {
        authenticationError();
        authCodeError();
        $('#report-form').removeClass('hide');
      } else if (!captchaRequired || (captchaRequired && !inValidCaptcha)) {
        $.ajax({
          url: '/eaton/qr/epasSoaServiceValidation.json',
          data: 'serialNumber=' + serialNumber + '&catalogNumber=' + catalogNumber + '&authCode=' + authCode + '&action=validateAuthCodeManualFlow',
          success: function success(response) {
            document.getElementById('eventId').value = response.eventId;
            if (response.errorCode === 'ERRCD00') {
              let endIndexSkuPage = $(location).attr('pathname').indexOf('skuPage.') + 8;
              let catalogNumber = $(location).attr('pathname').substring(endIndexSkuPage, $(location).attr('pathname').indexOf('.', endIndexSkuPage));
              let authnumber = document.getElementById('authentication_code').value;
              serialSessionObject.authentication_code = document.getElementById('authentication_code').value;
              serialSessionObject.catalogNum = catalogNumber;
              sessionStorage.setItem('serial_auth_verified', JSON.stringify(serialSessionObject));
              document.getElementById('serial_auth_verified').innerHTML = authnumber;
              if (captchaRequired && inValidCaptcha) {
                $('#recaptcha_error').removeClass('hide');
                $('#correctAuthentication').addClass('hide');
                $('#correctAuthCodeMessage').addClass('hide');
                $('#authentication_number_wrong').addClass('hide');
                $('#serial_number_wrong').addClass('hide');
                $('#other_error').addClass('hide');
                $('#auth_blank').addClass('hide');
              } else {
                $('#recaptcha_error').hide();
                $('#correctAuthentication').addClass('hide');
                $('#correctAuthCodeMessage').removeClass('hide');
                $('#collapse-authenticate').collapse('hide');
                $('#authentication_number_wrong').addClass('hide');
                $('#serial_number_wrong').addClass('hide');
                $('#other_error').addClass('hide');
                $('#auth_blank').addClass('hide');
                $('#report-form').addClass('hide');
              }
            } else if (response.errorCode === 'ERRCD01') {
              if (captchaRequired && inValidCaptcha) {
                $('#recaptcha_error').removeClass('hide');
                $('#serial_number_wrong').removeClass('hide');
                $('#correctAuthentication').addClass('hide');
                $('#correctAuthCodeMessage').addClass('hide');
                $('#authentication_number_wrong').addClass('hide');
                $('#other_error').addClass('hide');
                $('#auth_blank').addClass('hide');
              } else {
                $('#recaptcha_error').hide();
                $('#report-form').removeClass('hide');
                $('#serial_number_wrong').removeClass('hide');
                $('#correctAuthentication').addClass('hide');
                $('#correctAuthCodeMessage').addClass('hide');
                $('#authentication_number_wrong').addClass('hide');
                $('#other_error').addClass('hide');
                $('#auth_blank').addClass('hide');
                authenticationError();
              }
            } else if (response.errorCode === 'ERRCD04') {
              if (captchaRequired && inValidCaptcha) {
                $('#recaptcha_error').removeClass('hide');
                $('#authentication_number_wrong').removeClass('hide');
                $('#correctAuthentication').addClass('hide');
                $('#correctAuthCodeMessage').addClass('hide');
                $('#serial_number_wrong').addClass('hide');
                $('#other_error').addClass('hide');
                $('#auth_blank').addClass('hide');
              } else {
                $('#report-form').removeClass('hide');
                $('#recaptcha_error').hide();
                $('#authentication_number_wrong').removeClass('hide');
                $('#correctAuthentication').addClass('hide');
                $('#correctAuthCodeMessage').addClass('hide');
                $('#serial_number_wrong').addClass('hide');
                $('#other_error').addClass('hide');
                $('#auth_blank').addClass('hide');
                authenticationError();
              }
            } else {
              if (captchaRequired && inValidCaptcha) {
                $('#recaptcha_error').removeClass('hide');
                $('#other_error').removeClass('hide');
                $('#correctAuthentication').addClass('hide');
                $('#correctAuthCodeMessage').addClass('hide');
                $('#authentication_number_wrong').addClass('hide');
                $('#serial_number_wrong').addClass('hide');
                $('#auth_blank').addClass('hide');
              } else {
                $('#recaptcha_error').hide();
                $('#other_error').removeClass('hide');
                $('#correctAuthentication').addClass('hide');
                $('#correctAuthCodeMessage').addClass('hide');
                $('#authentication_number_wrong').addClass('hide');
                $('#serial_number_wrong').addClass('hide');
                $('#auth_blank').addClass('hide');
                authenticationError();
                $('#report-form').removeClass('hide');
              }
            }
          },
          error: function error(response) {
            console.log('exception in QRServlet Response for auth code');
          },
          type: 'POST'
        });
      } else {
        $('#recaptcha_error').removeClass('hide');
      }
    });
  }
});

$('#repIssueSubmitBtn').click(function () {

  let fullname = document.getElementById('fullname').value;
  let email = document.getElementById('email_text').value;
  let comments = document.getElementById('comments').value;
  let serialNumber = document.getElementById('authentication_code').value;
  let authCode = document.getElementById('authCodeText').value;
  let repIssueEmail = document.getElementById('repIssueEmail').value;
  let endIndexSkuPage = $(location).attr('pathname').indexOf('skuPage.') + 8;
  let eventId = document.getElementById('eventId').value;
  let catalogNumber = $(location).attr('pathname').substring(endIndexSkuPage, $(location).attr('pathname').indexOf('.', endIndexSkuPage));
  $('#report-close').removeClass('hide');
  $('#email_text').focus(function () {
    $('#emailmandatory').addClass('hide');
  });
  let reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
  if (email === null || email === '' || reg.test(email) === false) {
    $('#emailmandatory').removeClass('hide');
  } else {
    $.ajax({
      type: 'POST',
      url: '/eaton/qr/epasSoaServiceValidation.json',
      data: 'serialNumber=' + serialNumber + '&catalogNumber=' + catalogNumber + '&fullName=' + fullname + '&email=' + email + '&comments=' + comments + '&authcode=' + authCode + '&repIssueEmail=' + repIssueEmail + '&action=reportIssue' + '&eventId=' + eventId,
      success: function success(response) {
        if (response.errorCode === 'ERRCD00') {
          $('#success__message').removeClass('hide');
          $('#report-form').addClass('hide');
          $('#fullname').val('');
          $('#email_text').val('');
          $('#comments').val('');
        } else if (response.errorCode === 'ERRCD01') {
          console.log('');
        }
      }
    });
  }
});

let serialNumberError = () => {
  $('#main__authcode').addClass('hide');
  $('#invalidSerialErrorMsg').removeClass('hide');
  $('#suspectSerialMessage').addClass('hide');
  $('#SerialCatalogMismatch').addClass('hide');
  $('#SerialCatalogMismatchMessage').addClass('hide');
  $('#correctAuthentication').addClass('hide');
};

let authCodeError = () => {
  $('#authentication_number_wrong').addClass('hide');
  $('#auth_blank').removeClass('hide');
  $('#other_error').addClass('hide');
  $('#recaptcha_error').addClass('hide');
  $('#correctAuthentication').addClass('hide');
  $('#correctAuthCodeMessage').addClass('hide');
  $('#serial_number_wrong').addClass('hide');
};

let authenticationError = () => {
  document.getElementById('authCodeText').style.borderColor = '#DA291C';
};

$('#authCodeText').focus(function () {
  document.getElementById('authCodeText').style.removeProperty('border');
});

/* eslint-disable no-unused-vars, no-undef*/
let reportIssueFunction = () => {
  $('#report-form').removeClass('hide');
  $('#report-form').show();
  $('#success__message').addClass('hide');
};
window.reportIssueFunction = reportIssueFunction;

let serialNumberauth = () => {
  let authvalue = document.getElementById('authentication_code').value;
  document.getElementById('serial__num').innerHTML = authvalue;
};
window.serialNumberauth = serialNumberauth;

