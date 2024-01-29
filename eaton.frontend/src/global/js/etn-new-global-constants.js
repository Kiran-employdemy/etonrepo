/* eslint-disable no-global-assign */
// noinspection JSConstantReassignment

const viewTypes = {
  grid: 'grid',
  list: 'list'
};
const literals = {
  BUTTON_SHORT: 'btn',
  UNDEFINED: 'undefined',
  JQUERY: 'jquery',
  VIEW_ICON: '-view-icon-',
  STRING: 'string',
  TRUE: 'true',
  FALSE: 'false',
  YES: 'yes',
  BINARY: 'binary'
};
const booleans = {
  isTrue: (value) => {
    return value === literals.TRUE || value === literals.YES;
  }
};
const rootPaths = {
  CLIENT_LIB_IMAGES: '/etc.clientlibs/eaton/settings/wcm/designs/clientlib/clientlib-all/resources/images/'
};
const suffixes = {
  NOCACHED_JSON: '.nocache.json',
  JSON: '.json',
  PNG: '.png',
  HTML: '.html'
};
const colors = {
  BLUE: 'blue',
  GREY: 'grey'
};
const pathsTo = {
  icons: {
    ativeGrid: rootPaths.CLIENT_LIB_IMAGES + viewTypes.grid + literals.VIEW_ICON + colors.BLUE + suffixes.PNG,
    disabledGrid: rootPaths.CLIENT_LIB_IMAGES + viewTypes.grid + literals.VIEW_ICON + colors.GREY + suffixes.PNG,
    activeList: rootPaths.CLIENT_LIB_IMAGES + viewTypes.list + literals.VIEW_ICON + colors.BLUE + suffixes.PNG,
    disabledList: rootPaths.CLIENT_LIB_IMAGES + viewTypes.list + literals.VIEW_ICON + colors.GREY + suffixes.PNG
  },
  EATON_ROOT: '/content/eaton',
  LOGIN_ROOT: '/content/login',
  LOGIN_PATH: '/login'
};
const httpMethods = {
  GET: 'GET',
  POST: 'POST'
};
const displayStyles = {
  none: 'none',
  block: 'block'
};
const keyCodes = {
  enter: 'Enter'
};
const eventListeners = {
  CLICK: 'click',
  MOUSE_DOWN: 'mousedown',
  KEY_UP: 'keyup',
  CHANGE: 'change',
  BLUR: 'blur'
};
const sessionStorageKeys = {
  selectedFiles: 'selectedFiles'
};
const htmlTags = {
  image: 'img',
  icon: 'i',
  tableBody: 'tbody',
  listItem: 'li',
  bdi: 'bdi'
};
const dates = {
  nowPlus400Days: () => {
    let now = new Date();
    let nowAtMidnight = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0,0,0);
    return new Date(nowAtMidnight.setDate(nowAtMidnight.getDate() + 400));}
};
const cookies = {
  acceptance: 'eatoncookies',
  isPresent: (cookieName) => { return document.cookie.indexOf(cookieName) > -1;},
  extractValueFor: (cookieName) => {
    let indexOfRedirectCookie = document.cookie.indexOf(cookieName);
    let stringFromCookie = document.cookie.substring(indexOfRedirectCookie);
    let lengthOfCookie = stringFromCookie.indexOf(';');
    let redirectCookie = document.cookie.substring(indexOfRedirectCookie, indexOfRedirectCookie + lengthOfCookie);
    return redirectCookie.split('=')[1];
  },
  deepLinkRedirect: {
    name: 'deep-link-redirect',
    cookie: (url, forRemoval) => {return `deep-link-redirect=${ url };path=/${ forRemoval ? ';expires=Thu, 01 Jan 1970 00:00:00 UTC' : '' }`;}
  },
  eatonRedirect: {
    name: 'etn_redirect_cookie',
    cookie: (countryLanguage, expiryDate) => {return `etn_redirect_cookie=${ encodeURIComponent(countryLanguage) };domain=${ window.location.href.indexOf('etn.com') > -1 ? '.etn' : '.eaton' }.com;path=/;${ expiryDate ? 'expires=' + dates.nowPlus400Days().toUTCString() : '' }` ; }
  }
};
const elementAttributes = {
  source: 'src',
  reference: 'href',
  ariaExpanded: 'aria-expanded'
};
const urls = {
  isExternalLink: (url) => {return url.startsWith('http://') || url.startsWith('https://'); }
};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = {literals, keyCodes, displayStyles, sessionStorageKeys, eventListeners, suffixes,
    httpMethods, pathsTo, booleans, viewTypes, htmlTags, cookies, dates, elementAttributes, urls};
}
