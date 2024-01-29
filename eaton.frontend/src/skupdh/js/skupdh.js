/**
 * For SKUPDH redirecting for heating skupage.xxx.pdf
 */
function preloadSKUPDHRediectFunc() {
  if (location.pathname.includes('SKUPDH')) {
    let originVal = location.origin;
    let pathVal = location.pathname;
    let skuID = '';
    let skuPage = 'skuPage';
    let pathValArray = pathVal.split('.');
    let tempVal = '';
    let contentEaton = '';
    if (pathVal.includes('content/eaton')) {
      contentEaton = '/content/eaton/';
    } else {
      contentEaton = '/';
    }
    let langCode = '';
    let countryCode = '';
    let urlToCheck = '';

    tempVal = pathVal.split('.');
    skuID = tempVal[1];
    if (location.search !== '' && !pathVal.includes('editor.html')) {
      let params = new URLSearchParams(location.search);
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