/* eslint-disable no-undef */
/* eslint-disable no-global-assign */
// noinspection JSConstantReassignment

if (typeof require !== 'undefined') {
  const constants = require('../../../global/js/etn-new-global-constants');
  suffixes = constants.suffixes;
  pathsTo = constants.pathsTo;
  cookies = constants.cookies;
  dates = constants.dates;
  elementAttributes = constants.elementAttributes;
  urls = constants.urls;
}
const setCookieAndRedirectToHrefFromLink = (event) => {
  if (document.body.className.indexOf('home-page') === -1 && document.body.className.indexOf('login-page') === -1) {
    document.cookie = cookies.deepLinkRedirect.cookie(window.location.pathname);
  }
  window.location = event.currentTarget.getAttribute('href');
  event.preventDefault();
};

function extractCountryLanaguage(pathname) {
  return pathname.replace(pathsTo.EATON_ROOT, '').replace(pathsTo.LOGIN_ROOT, '').replace(pathsTo.LOGIN_PATH, '').replace(suffixes.HTML, '');
}

const bindCookieAndRedirectToHrefFromLinkToSignInLink = () => {
  const signInLink = document.getElementsByClassName('open-sign-in');
  if (signInLink.length === 1) {
    let link = signInLink[0];
    link.onclick = (event) => {
      let href = new URL(event.currentTarget.getAttribute('href'));
      document.cookie = cookies.eatonRedirect.cookie(extractCountryLanaguage(href.pathname));
      setCookieAndRedirectToHrefFromLink(event);
    };
  }

  let signFreeBtn = document.getElementsByClassName('free-btn');
  if (signFreeBtn === null) {
    return false;
  } else {
    if (signFreeBtn.length === 1) {
      let freeLink = signFreeBtn[0];
      freeLink.onclick = function (event) {
        setCookieAndRedirectToHrefFromLink(event);
      };
    }
  }

};

const bindCookieAndRedirectToHrefFromLinkToCountrySelectionLink = () => {
  const countrySelectionLink = document.getElementsByClassName('open-country-selector');
  if (countrySelectionLink.length === 1) {
    let link = countrySelectionLink[0];
    link.onclick = (event) => {
      setCookieAndRedirectToHrefFromLink(event);
    };
  }
};

const ifLoginPageSetEtnRedirectCookieToCountryOfLoginPage = () => {
  if (document.body.className.indexOf('login-page') !== -1) {
    document.cookie = cookies.eatonRedirect.cookie(extractCountryLanaguage(window.location.pathname));
  }
};

document.addEventListener('DOMContentLoaded', () => {
  bindCookieAndRedirectToHrefFromLinkToSignInLink();
  bindCookieAndRedirectToHrefFromLinkToCountrySelectionLink();
  ifLoginPageSetEtnRedirectCookieToCountryOfLoginPage();
});

