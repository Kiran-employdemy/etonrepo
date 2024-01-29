/* eslint-disable no-undef */
/* eslint-disable no-global-assign */
// noinspection JSConstantReassignment

if (typeof require !== 'undefined') {
  const constants = require('../../../global/js/etn-new-global-constants');
  const selectorConstants = require('./country-selector-redirect-constants');
  countrySelectionConstants = selectorConstants.countrySelectionConstants;
  suffixes = constants.suffixes;
  pathsTo = constants.pathsTo;
  cookies = constants.cookies;
  dates = constants.dates;
  elementAttributes = constants.elementAttributes;
  urls = constants.urls;
}

const extractCountryLanguageFromHrefUrl = (href, isExternalLink) => {
  let countryLanguage = href.replace(pathsTo.EATON_ROOT, '').replace(pathsTo.LOGIN_ROOT, '').replace(pathsTo.LOGIN_PATH, '').replace(suffixes.HTML, '');
  if (isExternalLink) {
    let url = new URL(href);
    countryLanguage = url.pathname.substring(0, url.pathname.indexOf(suffixes.HTML));
  }
  return countryLanguage;
};

const setEtnRedirectCookieIfCookiesAreAccepted = countryLanguage => {
  if (document.cookie.indexOf(cookies.acceptance) > -1) {
    document.cookie = cookies.eatonRedirect.cookie(countryLanguage, true);
  }
};

const extractUrlFromRedirectCookie = (countryLanguage) => {
  let redirectCookieValue = cookies.extractValueFor(cookies.deepLinkRedirect.name);
  return redirectCookieValue.replace(/(\/[a-z]{2}\/[a-z]{2}-[a-z]{2}|hongkong)/, countryLanguage);
};

const bindToEachCountrySelectionLinkCookieSettingClickEvent = () => {
  document.querySelectorAll(countrySelectionConstants.querySelectors.allLinks).forEach(link => {
    link.onclick = (event) => {
      event.preventDefault();
      let href = link.getAttribute(elementAttributes.reference);
      let isExternalLink = urls.isExternalLink(href);
      let countryLanguage = extractCountryLanguageFromHrefUrl(href, isExternalLink);
      setEtnRedirectCookieIfCookiesAreAccepted(countryLanguage);
      if (cookies.isPresent(cookies.deepLinkRedirect.name)) {
        href = extractUrlFromRedirectCookie(countryLanguage);
        document.cookie = cookies.deepLinkRedirect.cookie(cookies.extractValueFor(cookies.deepLinkRedirect.name), true);
        if (isExternalLink) {
          window.location = link.getAttribute(elementAttributes.reference).replace(countryLanguage + suffixes.HTML, href);
          return;
        }
      }
      if (isExternalLink) {
        window.location = href;
        return;
      }
      window.location = window.location.href.replace(countrySelectionConstants.pathsTo.selectionPage, '') + href;
    };
  });
};

document.addEventListener('DOMContentLoaded', () => {
  bindToEachCountrySelectionLinkCookieSettingClickEvent();
  const elementClasses = countrySelectionConstants.elementClasses;
  const querySelectors = countrySelectionConstants.querySelectors;
  const collapseDesktop = x => {
    if (x.matches) {
      document.querySelectorAll(querySelectors.countryBlock).forEach((countryBlock) => {
        countryBlock.classList.add(elementClasses.collapse);
        countryBlock.classList.add(elementClasses.inwards);
      });
      document.querySelectorAll(querySelectors.countrySelectorIcons).forEach((countrySelectorIcon) => {
        countrySelectorIcon.classList.add(elementClasses.iconMinus);
        countrySelectorIcon.classList.remove(elementClasses.iconPlus);
      });
    }
  };
  let x = window.matchMedia('(min-width: 992px)');
  collapseDesktop(x);
  x.addEventListener('change', collapseDesktop);
});
