/* jshint esversion: 6 */

const { setSourceTrackingParameters,
  appendCurrentUrlParams,
  appendPercolateContentIdParam,
  setPagePathTrackingParameters} = require('../src/global/js/etn-global-tracking.js');


const { templateCtaButton } = require('../__test-fixtures__/GlobalTrackingFixtures');

let currentUrl;
let templateCtaButtonEle;

beforeEach(() => {

  jest.restoreAllMocks();

  currentUrl = new URL('http://localhost:4502/content/eaton/us/en-us/catalog/differentials/eaton-elocker4-differential.html');

  window.dataLayer = [];
  window.dataLayer.percolateContentId = '22002200';

  document.body.innerHTML = templateCtaButton;

  templateCtaButtonEle = document.querySelector('.product-family-card__cta a');


});

describe('source tracking', () => {

  it('should append the current page\'s url parameters to the cta\'s url search parameters when exist',  () => {

    currentUrl.searchParams.append('testKey', 'testValue');
    currentUrl.searchParams.append('testKey2', 'testValue2');

    appendCurrentUrlParams(currentUrl);

    let actualUrl = new URL(templateCtaButtonEle.href);
    let actualSearchParams = actualUrl.searchParams;


    expect(actualSearchParams.has('testKey')).toBeTruthy();
    expect(actualSearchParams.has('testKey2')).toBeTruthy();
    expect(actualSearchParams.has('testKey3')).toBeFalsy();

    expect(actualSearchParams.get('testKey')).toBe('testValue');
    expect(actualSearchParams.get('testKey2')).toBe('testValue2');


  });

  it('should not append any url parameters if the current page doesn\'t have any', () => {

    appendCurrentUrlParams(currentUrl);

    let actualUrl = new URL(templateCtaButtonEle.href);
    expect(Array.from(actualUrl.searchParams).length).toEqual(0);

  });

  it('should append the percolate content id to the cta\'s url search parameters',  () => {

    appendPercolateContentIdParam();
    let actualUrl = new URL(templateCtaButtonEle.href);
    expect(actualUrl.searchParams.has('percolateContentId')).toBeTruthy();
    expect(actualUrl.searchParams.get('percolateContentId')).toBe('22002200');


  });

  it('should not append percolate content id when no percolate content id is set in data layer', () => {

    window.dataLayer.percolateContentId = '';
    appendPercolateContentIdParam();
    let actualUrl = new URL(templateCtaButtonEle.href);
    expect(actualUrl.searchParams.has('percolateContentId')).toBeFalsy();

  });

  it('should not append any url parameters or percolate content id when source tracking is disabled', () => {

    templateCtaButtonEle.dataset.sourceTracking = 'false';
    appendPercolateContentIdParam();
    setSourceTrackingParameters(currentUrl);

    let actualUrl = new URL(templateCtaButtonEle.href);
    expect(actualUrl.searchParams.has('percolateContentId')).toBeFalsy();
    expect(Array.from(actualUrl.searchParams).length).toEqual(0);

  });

});

describe('page path tracking', () => {

  it('should add the current page\'s path as url parameter for cta link when page path tracking is enabled', () => {

    setPagePathTrackingParameters(currentUrl);

    let actualUrl = new URL(templateCtaButtonEle.href);
    expect(actualUrl.searchParams.has('productPagePath')).toBeTruthy();
    expect(actualUrl.searchParams.get('productPagePath')).toBe('/content/eaton/us/en-us/catalog/differentials/eaton-elocker4-differential');


  });

  it('should not append current page\'s path to cta link as url parameter when page path tracking is disabled', () => {

    templateCtaButtonEle.dataset.pagePathTracking = 'false';

    setPagePathTrackingParameters(currentUrl);

    let actualUrl = new URL(templateCtaButtonEle.href);
    expect(actualUrl.searchParams.has('productPagePath')).toBeFalsy();

  });

});
