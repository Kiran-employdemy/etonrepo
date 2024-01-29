/* eslint-disable no-undef */
require('../src/shared/module-product-tabs-v2/js/module-product-tabs-v2');
const {productTabs} = require('../__test-fixtures__/ProductTabsV2Fixtures');
const {fiveSCUpsLocation} = require('../__test-fixtures__/global/WindowLocationFixtures');

beforeEach(() => {
  Object.defineProperty(window, 'location', fiveSCUpsLocation());
  global.scroll = (options) => {
    window.scrollY = options.top;
  };
});

describe('Module product tabs version 2', () => {
  it('should not add hash to current url on tab label click when product-tabs-v2 is not present', () => {
    document.body.innerHTML = productTabs('');
    window.dispatchEvent(new Event('load', {bubbles: true}));
    expect(document.querySelectorAll('a[href*="#tab-1"]').length).toBe(0);
    let tab1Label = document.querySelector('label[for="tab-1"]');
    tab1Label.click();
    expect(window.location.href).not.toContain('#tab-1');
  });
  it('should add hash to current url on tab label click when product-tabs-v2 is present', () => {
    document.body.innerHTML = productTabs('-v2');
    document.dispatchEvent(new Event('DOMContentLoaded', {bubbles: true}));
    window.dispatchEvent(new Event('load', {bubbles: true}));
    let tab1Label = document.querySelector('label[for="tab-1"]');
    tab1Label.click();
    expect(window.location.href).toBe('https://www-local.eaton.com/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.html#tab-1');
  });
  it('should change hrefs in link elements to add hash to the current url when clicked on tab label', () => {
    document.body.innerHTML = productTabs('-v2');
    window.dispatchEvent(new Event('load', {bubbles: true}));
    expect(document.querySelectorAll('a[href*="#tab-2"]').length).toBe(0);
    let tab1Label = document.querySelector('label[for="tab-2"]');
    tab1Label.click();
    expect(document.querySelectorAll('a[href*="#tab-2"]').length).toBe(10);
  });
  it('should change all data-value attributes in button to add hash to the current url when clicked on tab label', () => {
    document.body.innerHTML = productTabs('-v2');
    window.dispatchEvent(new Event('load', {bubbles: true}));
    expect(document.querySelectorAll('div[data-value*="#tab-2"]').length).toBe(0);
    let tab1Label = document.querySelector('label[for="tab-2"]');
    tab1Label.click();
    expect(document.querySelectorAll('div[data-value*="#tab-2"]').length).toBe(2);
  });
  it('should change hrefs in link elements to add hash to the current url when it contains a hash', () => {
    Object.defineProperty(window, 'location', fiveSCUpsLocation('tab-1'));
    document.body.innerHTML = productTabs('-v2');
    window.dispatchEvent(new Event('load', {bubbles: true}));
    expect(document.querySelectorAll('a[href*="#tab-2"]').length).toBe(0);
    let tab1Label = document.querySelector('label[for="tab-2"]');
    tab1Label.click();
    expect(document.querySelectorAll('a[href*="#tab-2"]').length).toBe(10);
  });
  it('should change all data-value attributes in button elements to add hash to the current url when it contains a hash', () => {
    Object.defineProperty(window, 'location', fiveSCUpsLocation('tab-1'));
    document.body.innerHTML = productTabs('-v2');
    window.dispatchEvent(new Event('load', {bubbles: true}));
    expect(document.querySelectorAll('div[data-value*="#tab-2"]').length).toBe(0);
    expect(document.querySelectorAll('button[data-filter-value*="#tab-2"]').length).toBe(0);
    let tab1Label = document.querySelector('label[for="tab-2"]');
    tab1Label.click();
    expect(document.querySelectorAll('div[data-value*="#tab-2"]').length).toBe(2);
    expect(document.querySelectorAll('button[data-filter-value*="#tab-2"]').length).toBe(2);
  });
});
