// tracking vars
let elqTrackingTimerId = null;
const elqCookieTrackingUrl = 'securetracking.eaton.com';
const elqSiteIdProduction = '1521';
const elqSiteIdSandbox = '1975530148';

// GUID vars
const elqGuidTimeoutSel = '#eloquaGuidTimeout';
let elqGuidTimeout = $(elqGuidTimeoutSel).val();
const elqGuidTimeoutHalf = elqGuidTimeout / 2;
const elqGuidTimerIntervalMs = 1000;
let elqGuidTimerId = null;
const elqGuidCookiePat = '=(.*)';
const elqGuidCookieName = 'ELOQUA';
const elqGuidSuffix = '&FPCVISITED';
const eatonElqFirstParty = 'First Party';
const eatonElqThirdParty = 'Third Party';

/**
* Set eloqua site id, secure tracking url, and pageview tracking
*/
const setEloquaTracking = () => {

  window._elqQ = window._elqQ || [];

  // eslint-disable-next-line no-undef
  if (typeof OptanonActiveGroups !== 'undefined') {

    clearInterval(elqTrackingTimerId);

    // eslint-disable-next-line no-undef
    if (OptanonActiveGroups.includes(',C0003,') && !window.location.href.includes('@')) {

      try {

        let hostname = new URL(window.location.href).hostname.match(/\.(.*?)\.com/gm)[0];
        let elqSiteId = hostname.includes('eaton') ? elqSiteIdProduction : elqSiteIdSandbox;
        window._elqQ.push(['elqSetSiteId', elqSiteId]);
        hostname.includes('eaton') && window._elqQ.push(['elqUseFirstPartyCookie', elqCookieTrackingUrl]);
        window._elqQ.push(['elqTrackPageView', window.location.href]);
        elqGuidTimerId = setInterval(doElqGuidOps, elqGuidTimerIntervalMs);

      } catch (err) {
        console.error(err);
      }
    }
  }
};


/**
 * Load the Eloqua configuration script
 */
// eslint-disable-next-line camelcase
const async_load = () => {
  let s = document.createElement('script');
  s.type = 'text/javascript';
  s.async = true;
  s.src = '//img.en25.com/i/elqCfg.min.js';
  let x = document.getElementsByTagName('script')[0];
  x.parentNode.insertBefore(s, x);
};

/**
  * Append guid to eloqua form
  */
const appendElqGuid = (guid) => {
  $('.elq-form').append($('<input>').attr({type: 'hidden', name: 'elqCookieWrite', value: '0'}));
  $('.elq-form').append( $('<input>').attr({type: 'hidden', name: 'elqCustomerGUID', value: guid}));
};

/*
* Try getting the first party cookie for the first half of timer duration and third party for second half
* If a guid is found, it is appended to the data layer and form (if eloqua form page for the latter)
*/
const doElqGuidOps = () => {

  let elqGuid = null;
  // eslint-disable-next-line no-unused-vars
  let elqGuidType = null;

  if (elqGuidTimeout > elqGuidTimeoutHalf) {

    // first party
    elqGuid = $.cookie(elqGuidCookieName);
    if (typeof elqGuid !== 'undefined') {
      let matchPat = elqGuid.includes(elqGuidSuffix) ? elqGuidCookiePat + elqGuidSuffix : elqGuidCookiePat;
      elqGuid = elqGuid.match(matchPat)[1];
      elqGuidType = eatonElqFirstParty;
    }

  } else if (elqGuidTimeout <= elqGuidTimeoutHalf && elqGuidTimeout > 0) {

    // third party

    /* eslint-disable no-undef */
    window._elqQ = window._elqQ || [];
    window._elqQ.push(['elqGetCustomerGUID']);
    /* eslint-enable no-undef */

    /* eslint-disable no-undef, new-cap*/
    if (typeof GetElqCustomerGUID === 'function') {
      elqGuid = GetElqCustomerGUID();
      elqGuidType = eatonElqThirdParty;
    }
    /* eslint-enable no-undef, new-cap*/

  } else {

    // timeout
    console.error('First and third party eloqua guids not found.');
    clearInterval(elqGuidTimerId);
    return;

  }

  if (elqGuid) {

    // add to data layer and form
    /* eslint-disable no-undef*/
    dataLayer.eatonElqGUID = elqGuid;
    dataLayer.form === 'yes' && appendElqGuid(elqGuid);
    typeof LookupIdVisitor !== 'undefined' && window._elqQ.push(['elqDataLookup', escape(LookupIdVisitor), '']);
    /* eslint-enable no-undef*/
    clearInterval(elqGuidTimerId);
    return;

  }

  elqGuidTimeout -= 1;
};


$(document).ready(function() {
  // eslint-disable-next-line camelcase
  async_load();
  elqTrackingTimerId = setInterval(setEloquaTracking, 500);
});
