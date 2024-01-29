/* eslint-disable no-undef */
const {fakeDocumentCookie} = require('../__test-fixtures__/global/CookieFixtures');
const {loginButton} = require('../__test-fixtures__/LoginButtonFixtures');
const {searchBox} = require('../__test-fixtures__/SearchFixtures');
const {usHomeLocation, usEngineSolutionLocation, US_ENGINE_SOLUTIONS_PATH_NAME, US_HOME_PATH_NAME, loginPageLocation,
  LOGIN_PAGE_PATH
} = require('../__test-fixtures__/global/WindowLocationFixtures');
const {countrySelectionButton} = require('../__test-fixtures__/CountrySelctionButtonFixtures');
require('../src/components/full-page-drawer/js/full-page-drawer');
const {log} = require('gulp-util');

beforeEach(() => {
  Object.defineProperty(document, 'cookie', fakeDocumentCookie);
});

test("if the link with class open-sign-in and open-country-selector is not present, the code won't break", () => {
  document.body.innerHTML = searchBox();
  document.dispatchEvent(new Event('DOMContentLoaded', {bubbles: true}));
  expect(document.cookie).not.toContain(`login-page-redirect=${ US_ENGINE_SOLUTIONS_PATH_NAME }`);
  expect(window.location.href).toBe('http://localhost/');
});

describe('Full page drawer in home page', () => {
  beforeEach(() => {
    Object.defineProperty(window, 'location', usHomeLocation());
    document.body.className = 'page home-page focus-ring-enabled';
    document.body.innerHTML = countrySelectionButton('www-local.eaton.com') + loginButton();
    document.dispatchEvent(new Event('DOMContentLoaded', {bubbles: true}));
  });
  it('Should not set the deep-link-redirect cookie, but navigate to link in sign-in link when clicked on', () => {
    const link = document.getElementsByClassName('open-sign-in');
    link[0].click();
    expect(document.cookie).not.toContain(`login-page-redirect=${ US_HOME_PATH_NAME }`);
    expect(window.location).toBe('http://www-login-local.eaton.com/us/en-us/login.html');
  });
  it('Should not set the deep-link-redirect cookie, but navigates to url in link when clicked on country selector', () => {
    const link = document.getElementsByClassName('open-country-selector');
    link[0].click();
    expect(document.cookie).not.toContain(`login-page-redirect=${ US_HOME_PATH_NAME }`);
    expect(window.location).toBe('http://www-local.eaton.com/country.html');
  });
  it('Should set the etn_redirect_cookie to locale from link, when clicked on sign in button', () => {
    const link = document.getElementsByClassName('open-sign-in');
    link[0].click();
    expect(document.cookie).not.toContain(`login-page-redirect=${ US_HOME_PATH_NAME }`);
    expect(document.cookie).toContain('etn_redirect_cookie=%2Fus%2Fen-us');
    expect(window.location).toBe('http://www-login-local.eaton.com/us/en-us/login.html');
  });
});

describe('Full page drawer in login page', () => {
  beforeEach(() => {
    Object.defineProperty(window, 'location', loginPageLocation());
    document.body.className = 'page login-page focus-ring-enabled';
    document.body.innerHTML = countrySelectionButton('www-login-local.eaton.com') + loginButton();
    document.dispatchEvent(new Event('DOMContentLoaded', {bubbles: true}));
  });
  it('Should not set the deep-link-redirect cookie, but navigates to url in link when clicked on country selector', () => {
    const link = document.getElementsByClassName('open-country-selector');
    link[0].click();
    expect(document.cookie).not.toContain(`login-page-redirect=${ LOGIN_PAGE_PATH }`);
    expect(window.location).toBe('http://www-login-local.eaton.com/country.html');
  });
});

describe('Full page drawer in other than home or login page', () => {
  beforeEach(() => {
    Object.defineProperty(window, 'location', usEngineSolutionLocation());
    document.body.className = 'page category-page focus-ring-enabled';
    document.body.innerHTML = countrySelectionButton('www-local.eaton.com') + loginButton();
    document.dispatchEvent(new Event('DOMContentLoaded', {bubbles: true}));
  });
  it('should set deep-link-redirect cookie to current page and redirect to url in link of sign in button', () => {
    const link = document.getElementsByClassName('open-sign-in');
    link[0].click();
    expect(document.cookie).toContain(`deep-link-redirect=${ US_ENGINE_SOLUTIONS_PATH_NAME }`);
    expect(window.location).toBe('http://www-login-local.eaton.com/us/en-us/login.html');
  });

  it('should set deep-link-redirect cookie to current page and redirects to url in link of country selector button', () => {
    const link = document.getElementsByClassName('open-country-selector');
    link[0].click();
    expect(document.cookie).toContain(`deep-link-redirect=${ US_ENGINE_SOLUTIONS_PATH_NAME }`);
    expect(window.location).toBe('http://www-local.eaton.com/country.html');
  });
});



