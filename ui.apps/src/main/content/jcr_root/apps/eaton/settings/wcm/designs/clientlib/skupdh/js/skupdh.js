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


'use strict';

/**
 * For SKUPDH redirecting for heating skupage.xxx.pdf
 */
function preloadSKUPDHRediectFunc() {
  if (location.pathname.includes('SKUPDH')) {
    var originVal = location.origin;
    var pathVal = location.pathname;
    var skuID = '';
    var skuPage = 'skuPage';
    var pathValArray = pathVal.split('.');
    var tempVal = '';
    var contentEaton = '';
    if (pathVal.includes('content/eaton')) {
      contentEaton = '/content/eaton/';
    } else {
      contentEaton = '/';
    }
    var langCode = '';
    var countryCode = '';
    var urlToCheck = '';

    tempVal = pathVal.split('.');
    skuID = tempVal[1];
    if (location.search !== '' && !pathVal.includes('editor.html')) {
      var params = new URLSearchParams(location.search);
      if (params.get('lang') !== null) {
        tempVal = params.get('lang').split('?');
        langCode = tempVal[0];
        tempVal = tempVal[0].split('-');
        countryCode = tempVal[1];
        pathVal = pathVal.substring(0, pathVal.lastIndexOf('/') + 1);
        urlToCheck = originVal + contentEaton + countryCode + '/' + langCode + '/' + skuPage + '.' + skuID + '.pdf';
      } else {
        urlToCheck = originVal + pathValArray[0] + '.' + pathValArray[1] + '.pdf';
      }

      window.location.href = urlToCheck;
    }
  }
}
window.onpaint = preloadSKUPDHRediectFunc();