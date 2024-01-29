/* eslint-disable no-undef */
const {fakeDocumentCookie} = require('../__test-fixtures__/global/CookieFixtures');
const {searchBox} = require('../__test-fixtures__/SearchFixtures');
require('../src/components/country-selector/js/country-selector-redirect');
const {
  countrySelectionLocation,
  US_ENGINE_SOLUTIONS_PATH_NAME, loginCountrySelectionLocation
} = require('../__test-fixtures__/global/WindowLocationFixtures');
const {asiaCountrySelection, loginCountrySelection} = require('../__test-fixtures__/CountryListFixtures');
const {setMatchMediaOnJSDOM} = require('../__test-fixtures__/global/MatchMediaFixtures');
const {nowPlus400Days} = require('../__test-fixtures__/global/DateFixtures');


describe('Country selection not starting from login', () => {
  beforeEach(() => {
    document.body.innerHTML = asiaCountrySelection();
    Object.defineProperty(document, 'cookie', fakeDocumentCookie);
    document.cookie = '';
    setMatchMediaOnJSDOM();
    document.dispatchEvent(new Event('DOMContentLoaded', {bubbles: true}));
  });

  test('When no country selection links exist on the page, the code does not break', () => {
    document.body.innerHTML = searchBox();
    expect(document.cookie).not.toContain('etn_redirect_cookie=%2Fgb%2Fen-gb');
    expect(window.location.href).toBe('http://localhost/');
  });

  test('If the eatoncookies cookie is not present, then the etn_redirect_cookie is not set, and user is redirected', () => {
    Object.defineProperty(window, 'location', countrySelectionLocation());
    const countrySelector = document.getElementsByClassName('country_selector-country')[0];
    countrySelector.click();
    expect(document.cookie).not.toContain('etn_redirect_cookie=%2Fcn%2Fzh-cn');
    expect(window.location).toBe('http://www-local.eaton.com/content/eaton/cn/zh-cn.html');
  });

  test('If the deep-link-redirect set but the eatoncookies cookie is not present, then the etn_redirect_cookie is not set, and user is redirected to the redirect page in the selected language', () => {
    Object.defineProperty(window, 'location', countrySelectionLocation());
    document.cookie = `deep-link-redirect=${ US_ENGINE_SOLUTIONS_PATH_NAME };path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT`;
    const countrySelector = document.getElementsByClassName('country_selector-country')[0];
    countrySelector.click();
    expect(document.cookie).not.toContain('etn_redirect_cookie=%2Fcn%2Fzh-cn');
    expect(window.location).toBe('http://www-local.eaton.com/content/eaton/cn/zh-cn/products/engine-solutions.html');
  });

  test('If the eatoncookies cookie is present, then the etn_redirect_cookie is set, and user is redirected', () => {
    document.cookie = 'eatoncookies=true;path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT';
    Object.defineProperty(window, 'location', countrySelectionLocation());
    const countrySelector = document.getElementsByClassName('country_selector-country')[0];
    countrySelector.click();
    expect(document.cookie).toContain(`etn_redirect_cookie=%2Fcn%2Fzh-cn;domain=.eaton.com;path=/;expires=${ nowPlus400Days().toUTCString().substring(0, 16) }`);
    expect(window.location).toBe('http://www-local.eaton.com/content/eaton/cn/zh-cn.html');
  });

  test('If the eatoncookies cookie is present, the deep-link-redirect set, then the etn_redirect_cookie is set, and user is redirected to the redirect page in the selected language', () => {
    document.cookie = 'eatoncookies=true;path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT';
    document.cookie = `deep-link-redirect=${ US_ENGINE_SOLUTIONS_PATH_NAME };path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT`;
    Object.defineProperty(window, 'location', countrySelectionLocation());
    const countrySelector = document.getElementsByClassName('country_selector-country')[0];
    countrySelector.click();
    expect(document.cookie).toContain(`etn_redirect_cookie=%2Fcn%2Fzh-cn;domain=.eaton.com;path=/;expires=${ nowPlus400Days().toUTCString().substring(0, 16) }`);
    expect(window.location).toBe('http://www-local.eaton.com/content/eaton/cn/zh-cn/products/engine-solutions.html');
  });

  test('If the link is an external url, redirects the user to that url.', () => {
    document.cookie = 'eatoncookies=true;path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT';
    Object.defineProperty(window, 'location', countrySelectionLocation());
    const countrySelector = document.getElementsByClassName('country_selector-country')[2];
    countrySelector.click();
    expect(document.cookie).toContain(`etn_redirect_cookie=%2Fcn%2Fhongkong;domain=.eaton.com;path=/;expires=${ nowPlus400Days().toUTCString().substring(0, 16) }`);
    expect(window.location).toBe('https://www.eaton.com.cn/cn/hongkong.html');
  });

  test('If the user is, on a deep link, and the link is an external url, redirects the user to that url on the deep link.', () => {
    document.cookie = 'eatoncookies=true;path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT';
    document.cookie = `deep-link-redirect=${ US_ENGINE_SOLUTIONS_PATH_NAME };path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT`;
    Object.defineProperty(window, 'location', countrySelectionLocation());
    const countrySelector = document.getElementsByClassName('country_selector-country')[2];
    countrySelector.click();
    expect(document.cookie).toContain(`etn_redirect_cookie=%2Fcn%2Fhongkong;domain=.eaton.com;path=/;expires=${ nowPlus400Days().toUTCString().substring(0, 16) }`);
    expect(window.location).toBe('https://www.eaton.com.cn/content/eaton/cn/hongkong/products/engine-solutions.html');
  });

  test('If in desktop, extends the region accordion', () => {
    document.cookie = 'eatoncookies=true;path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT';
    document.cookie = `deep-link-redirect=${ US_ENGINE_SOLUTIONS_PATH_NAME };path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT`;
    Object.defineProperty(window, 'location', countrySelectionLocation());
    const countrySelector = document.getElementsByClassName('country_selector-country')[1];
    countrySelector.click();
    let countryBlock = document.querySelector('.country-block');
    let countrySelectIcon = document.querySelector('.country-selector__icons');
    expect(countryBlock.classList).toContain('collapse');
    expect(countryBlock.classList).toContain('in');
    expect(countrySelectIcon.classList).not.toContain('icon-sign-plus');
    expect(countrySelectIcon.classList).toContain('icon-sign-minus');
  });
});

describe('Country selection starting from login', () => {
  beforeEach(() => {
    document.body.innerHTML = loginCountrySelection();
    Object.defineProperty(document, 'cookie', fakeDocumentCookie);
    document.cookie = '';
    setMatchMediaOnJSDOM();
    document.dispatchEvent(new Event('DOMContentLoaded', {bubbles: true}));
  });

  test('When no country selection links exist on the page, the code does not break', () => {
    document.body.innerHTML = searchBox();
    expect(document.cookie).not.toContain('etn_redirect_cookie');
  });

  test('If the eatoncookies cookie is not present, then the etn_redirect_cookie is not set, and user is redirected', () => {
    Object.defineProperty(window, 'location', loginCountrySelectionLocation());
    const countrySelector = document.getElementsByClassName('country_selector-country')[0];
    countrySelector.click();
    expect(document.cookie).not.toContain('etn_redirect_cookie=%2Fca%2Ffr-ca');
    expect(window.location).toBe('https://www-local-login.eaton.com/ca/fr-ca/login.html');
  });

  test('If the eatoncookies cookie is present, then the etn_redirect_cookie is set, and user is redirected', () => {
    document.cookie = 'eatoncookies=true;path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT';
    Object.defineProperty(window, 'location', loginCountrySelectionLocation());
    const countrySelector = document.getElementsByClassName('country_selector-country')[0];
    countrySelector.click();
    expect(document.cookie).toContain(`etn_redirect_cookie=%2Fca%2Ffr-ca;domain=.eaton.com;path=/;expires=${ nowPlus400Days().toUTCString().substring(0, 16) }`);
    expect(window.location).toBe('https://www-local-login.eaton.com/ca/fr-ca/login.html');
  });

  test('If in desktop, extends the region accordion', () => {
    document.cookie = 'eatoncookies=true;path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT';
    document.cookie = `deep-link-redirect=${ US_ENGINE_SOLUTIONS_PATH_NAME };path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT`;
    Object.defineProperty(window, 'location', loginCountrySelectionLocation());
    const countrySelector = document.getElementsByClassName('country_selector-country')[1];
    countrySelector.click();
    let countryBlock = document.querySelector('.country-block');
    let countrySelectIcon = document.querySelector('.country-selector__icons');
    expect(countryBlock.classList).toContain('collapse');
    expect(countryBlock.classList).toContain('in');
    expect(countrySelectIcon.classList).not.toContain('icon-sign-plus');
    expect(countrySelectIcon.classList).toContain('icon-sign-minus');
  });
});



