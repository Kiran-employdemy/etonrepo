/* eslint-disable no-undef */
const Mustache = require('../src/global/js/vendors/mustache.min');
const {withConstants} = require('../__test-fixtures__/global/AppFixture');
const {advancedSearchFromDashboardGridAuthenticated, advancedSearchFromDashboardListAuthenticated,
  advancedSearchFromDashboardListNotAuthenticated, advancedSearchFromDashboardGridNotAuthenticated,
  advancedSearchWithoutGridListViewDefaultGrid, advancedSearchWithoutGridListViewDefaultList, noExtraOptionSelectedGrid,
  noExtraOptionSelectedList
} = require('../__test-fixtures__/AdvancedSearchFixtures');
const {advancedSearchInitialJsonResponse, advancedSearchBuildingsJsonResponse, advancedSearchRelevanceAjaxRequest,
  advancedSearchRelevanceBuildingsAjaxRequest, advancedSearchAscendingBuildingsAjaxRequest,
  advancedSearchAscendingFullAjaxRequest, advancedSearchRelevanceByDateAjaxRequest,
  advancedSearchRelevanceLoadMoreAjaxRequest, advancedSearchMultipleFacetsAjaxRequest,
  advancedSearchPresntationsFacetsAjaxRequest, advancedSearchPriceListsFacetsAjaxRequest,
  advancedSearchWithRadioFacetsJsonResponse, advancedSearchWithSearchStringAjaxRequest, advancedSearchEmptyJsonResponse,
  advancedSearchRelevanceLoadMoreAjaxResponse, advancedSearchInitialJsonResponseNonAuthenticated,
  advancedSearchBrochuresJsonResponse, advancedSearchSecureOnlyResponse,
  advancedSearchInitialJsonResponseWithoutBulkDownloadAuthenticated,
  advancedSearchInitialJsonResponseWithoutBulkDownloadNonAuthenticated, advancedSearchRelevanceBrochuresAjaxRequest
} = require('../__test-fixtures__/AdvancedSearchAjaxFixtures');
require('../src/components/advanced-search/js/advanced-search-constants');
const {AdvancedSearch} = require('../src/components/advanced-search/js/advanced-search');

let advancedSearchObject = {};
let bulkDownloadContainerSpy = {};
let callWrapperMock = {};

function expectAjaxCallToReturn(initialJsonResponse) {
  externalCallWrapper.makeCall = jest.fn().mockReturnValue(new Promise((resolve) => resolve(initialJsonResponse)));
}

const prepareTests = async (innerHTML, initialJsonResponse = advancedSearchInitialJsonResponse()) => {
  window.innerWidth = 1669;
  document.body.innerHTML = innerHTML;
  sessionStorage.removeItem('selectedFiles');
  // eslint-disable-next-line no-global-assign
  externalCallWrapper = {};
  expectAjaxCallToReturn(initialJsonResponse);
  advancedSearchObject = new AdvancedSearch();
  callWrapperMock = jest.spyOn(externalCallWrapper, 'makeCall');
  // invoking method that is called after ajax success with mocked data before we call init, because the init method calls the mocked out version
  await document.dispatchEvent(new Event('DOMContentLoaded', {bubbles: true}));
  let clearFilters = document.querySelector('.faceted-navigation-header__action-link--clear-filters');
  await clearFilters.click();
  let bulkDownloadContainer = document.querySelector('.bde-activated');
  if (bulkDownloadContainer) {
    bulkDownloadContainerSpy = jest.spyOn(bulkDownloadContainer, 'dispatchEvent');
  }
  let facetsSection = document.querySelector('.faceted-navigation__filter-container');
  expect(facetsSection.style.display).toBe('');
  addRemovefileForBDE = jest.fn();
};

const selectBuildingsAndReturnSortAscendingAfterClickingIt = async (authenticated) => {
  expectAjaxCallToReturn(authenticated ? advancedSearchBuildingsJsonResponse() : advancedSearchBrochuresJsonResponse());
  callWrapperMock = jest.spyOn(externalCallWrapper, 'makeCall');
  let buildingsFacet = Array.from(document.querySelectorAll('.filter-selected')).find(facet => facet.getAttribute('data-title').indexOf(authenticated ? 'Buildings' : 'Brochures') > -1);
  await buildingsFacet.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchRelevanceBuildingsAjaxRequest(authenticated));

  let sortOptionsContainer = document.querySelector('.faceted-navigation-header__sort-options');
  let sortOptions = sortOptionsContainer.querySelectorAll('.faceted-navigation-header__sort-link.select-sorting');
  let sortFromAtoZ = Array.from(sortOptions).find(option => {
    return option.getAttribute('data-value').indexOf('asc') > -1;
  });
  await sortFromAtoZ.click();
  return sortFromAtoZ;
};

const dateRangeTest = async (authenticated) => {
  let applyDateButton = document.querySelector('.apply-date');
  let startDateInput = document.querySelector('.start-date');
  let endDateInput = document.querySelector('.end-date');
  startDateInput.value = '20-11-2022';
  endDateInput.value = '20-11-2023';
  await applyDateButton.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchRelevanceByDateAjaxRequest(authenticated));
};

const dateRangeTestNoResults = async () => {
  let applyDateButton = document.querySelector('.apply-date');
  let startDateInput = document.querySelector('.start-date');
  let endDateInput = document.querySelector('.end-date');
  startDateInput.value = '20-11-2022';
  endDateInput.value = '20-11-2023';
  await applyDateButton.click();
  advancedSearchObject.handleData(advancedSearchEmptyJsonResponse());
  expect(document.querySelector('.no-results-after-text-search').classList).toContain('hide');
};

const resetDateRangeTest = async authenticated => {
  let applyDateButton = document.querySelector('.apply-date');
  let startDateInput = document.querySelector('.start-date');
  let endDateInput = document.querySelector('.end-date');
  startDateInput.value = '20-11-2022';
  endDateInput.value = '20-11-2023';
  await applyDateButton.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchRelevanceByDateAjaxRequest(authenticated));
  let resetDateButton = document.querySelector('.reset-date');
  await resetDateButton.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchRelevanceAjaxRequest(authenticated));
  expect(startDateInput.value).toBe('');
  expect(endDateInput.value).toBe('');
};

const multipleFacetTest = async authenticated => {
  let buildingsFacet = Array.from(document.querySelectorAll('.filter-selected')).find(facet => facet.getAttribute('data-title').indexOf(authenticated ? 'Buildings' : 'Brochures') > -1);
  let dataCentersFacet = Array.from(document.querySelectorAll('.filter-selected')).find(facet => facet.getAttribute('data-title').indexOf(authenticated ? 'Data centers' : 'Catalogs') > -1);
  await buildingsFacet.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchRelevanceBuildingsAjaxRequest(authenticated));
  dataCentersFacet.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchMultipleFacetsAjaxRequest(authenticated));
};

const unselectFacetTest = async authenticated => {
  let buildingsFacet = Array.from(document.querySelectorAll('.filter-selected')).find(facet => facet.getAttribute('data-title').indexOf(authenticated ? 'Buildings' : 'Brochures') > -1);
  let dataCentersFacet = Array.from(document.querySelectorAll('.filter-selected')).find(facet => facet.getAttribute('data-title').indexOf(authenticated ? 'Data centers' : 'Catalogs') > -1);
  await buildingsFacet.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchRelevanceBuildingsAjaxRequest(authenticated));
  dataCentersFacet.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchMultipleFacetsAjaxRequest(authenticated));
  dataCentersFacet.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchRelevanceBuildingsAjaxRequest(authenticated));
};

const searchAndBinocularTest = async authenticated => {
  let searchBox = document.getElementById('adv-site-search-box');
  let searchButton = document.querySelector('.icon-search');
  searchBox.value = 'text to search';
  await searchButton.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchWithSearchStringAjaxRequest(authenticated));
  expect(document.querySelector('.faceted-navigation-header__action-link--clear-filters').classList).not.toContain('filterNone');
};

const searchAndEnterTest = async authenticated => {
  let searchBox = document.getElementById('adv-site-search-box');
  searchBox.value = 'text to search';
  await searchBox.dispatchEvent(new KeyboardEvent('keyup', {code: 't'}));
  await searchBox.dispatchEvent(new KeyboardEvent('keyup', {code: 'e'}));
  await searchBox.dispatchEvent(new KeyboardEvent('keyup', {code: 'Enter'}));
  expect(callWrapperMock).toBeCalledWith(advancedSearchWithSearchStringAjaxRequest(authenticated));
};

const noResultTest = async authenticated => {
  let searchBox = document.getElementById('adv-site-search-box');
  let facetsSection = document.querySelector('.faceted-navigation__filters');
  searchBox.value = 'text to search';
  await searchBox.dispatchEvent(new KeyboardEvent('keyup', {code: 'Enter'}));
  expect(callWrapperMock).toBeCalledWith(advancedSearchWithSearchStringAjaxRequest(authenticated));
  advancedSearchObject.handleData(advancedSearchEmptyJsonResponse());
  let emptyResponseBlock = document.querySelector('.search-results__no-result');
  let emptyResponseText = document.getElementById('noresult');
  expect(emptyResponseText.innerHTML).toBe('text to search');
  expect(emptyResponseBlock.classList).not.toContain('hide');
  expect(facetsSection.style.display).toBe('none');
  expect(document.querySelector('.no-results-after-text-search').classList).not.toContain('hide');
};

const ACTUAL_AUTHENTICATED_RESPONSE = '[{"image":"/content/dam/eaton/products/conduit-cable-and-wire-management/crouse-hinds/instruction-sheets/crouse-hinds-if1417-instruction-sheet.pdf.thumb.1280.1280.png","downloadEnabled":true,"publishDate":"08/20/2021","description":"","newBadgeVisible":false,"language":"Multilingual","trackDownload":"true","secure":false,"title":"IF 1417 - TECK cable fittings","url":"/content/dam/eaton/products/conduit-cable-and-wire-management/crouse-hinds/instruction-sheets/crouse-hinds-if1417-instruction-sheet.pdf","Language":"Multilingual","fileSize":"110620","dcFormat":"application/pdf","id":"cf7c37dd","contentType":"","fileType":"application/pdf","fileTypeAndSize":"PDF 110KB","epochPublishDate":"1629490954994","status":"","fileName":"crouse-hinds-if1417-instruction-sheet.pdf"}]';
const ACTUAL_UNAUTHENTICATED_RESPONSE = '[{"image":"/content/dam/eaton/products/electrical-circuit-protection/fuses/data-sheets/bus-ele-ds-1115-t300-series.pdf.thumb.1280.1280.png","downloadEnabled":true,"publishDate":"07/05/2019","description":"","newBadgeVisible":false,"language":"Multilingual","trackDownload":"","title":" Bussmann series 300V Class T fuse blocks data sheet No. 1115  ","secure":false,"url":"/content/dam/eaton/products/electrical-circuit-protection/fuses/data-sheets/bus-ele-ds-1115-t300-series.pdf","Language":"Multilingual","fileSize":"823777","dcFormat":"application/pdf","id":"a8cba602","contentType":"","fileType":"application/pdf","fileTypeAndSize":"PDF 823KB","status":"","epochPublishDate":"1562342700000"}]';
const bulkDownloadBasicTest = async (authenticated) => {
  let downloadItem = document.querySelectorAll('input[name=toDownload]')[0];
  await downloadItem.click();
  expect(sessionStorage.getItem('selectedFiles')).toBe(
    authenticated ?
      ACTUAL_AUTHENTICATED_RESPONSE
  :
  ACTUAL_UNAUTHENTICATED_RESPONSE);
};

const bulkDownloadWithSelectingOtherFacetTest = async (authenticated, forGrid) => {
  let downloadItem = document.querySelectorAll('input[name=toDownload]')[0];
  await downloadItem.click();
  expect(sessionStorage.getItem('selectedFiles')).toBe(
    authenticated ?
    ACTUAL_AUTHENTICATED_RESPONSE
  :
  ACTUAL_UNAUTHENTICATED_RESPONSE);
  selectBuildingsAndReturnSortAscendingAfterClickingIt(authenticated).then(() => {
    let listClass = forGrid ? 'advanced-search__result' : 'list-view';
    expect(document.getElementsByClassName(listClass).length).toBe(authenticated ? 17 : 61);
  });

};

const bulkDownloadWithLoadMoreTest = async (authenticated, forGrid, withGridListbutton = true) => {
  let downloadItem = document.querySelectorAll('input[name=toDownload]')[0];
  await downloadItem.click();
  expect(sessionStorage.getItem('selectedFiles')).toBe(
    authenticated ?
    ACTUAL_AUTHENTICATED_RESPONSE
  :
  ACTUAL_UNAUTHENTICATED_RESPONSE);
  await loadMoreTest(authenticated, forGrid);
  if ( forGrid ) {
    expect(document.querySelectorAll('.advanced-search__result').length).toBe(120);
  } else {
    expect(document.querySelector('tbody').querySelectorAll('.list-view').length).toBe(120);
  }
  downloadItem = document.querySelectorAll('input[name=toDownload]')[90];
  await downloadItem.click();
  expect(Array.from(JSON.parse(sessionStorage.getItem('selectedFiles'))).length).toBe(2);
  await selectBuildingsAndReturnSortAscendingAfterClickingIt(authenticated);
  expect(Array.from(JSON.parse(sessionStorage.getItem('selectedFiles'))).length).toBe(2);
  expect(document.querySelectorAll('input[name=toDownload]:checked').length).toBe(2);
  if (withGridListbutton) {
    await viewChangesFromGridToListAndViceVersaTest(forGrid, authenticated ? 18 : 62);
    expect(document.querySelectorAll('input[name=toDownload]:checked').length).toBe(2);
  }
};

const facetViewMoreButtonTest = () => {
  let marketingResourcesFacet = document.getElementById('resources_marketing-resources');
  let marketingResourcesFacetViewMore = marketingResourcesFacet.querySelector('.faceted-navigation__view-more-values');
  expect(marketingResourcesFacet.querySelectorAll('li.hide').length).toBeGreaterThan(0);
  marketingResourcesFacetViewMore.click();
  expect(marketingResourcesFacet.querySelectorAll('li.hide').length).toBe(0);
};

const facetViewLessButtonTest = () => {
  let marketingResourcesFacet = document.getElementById('facet-resources_marketing-resources');
  let marketingResourcesFacetViewMore = marketingResourcesFacet.querySelector('.faceted-navigation__view-more-values');
  let marketingResourcesFacetViewLess = marketingResourcesFacet.querySelector('.faceted-navigation__view-less-values');
  marketingResourcesFacetViewMore.click();
  expect(marketingResourcesFacet.querySelectorAll('li.hide').length).toBe(0);
  marketingResourcesFacetViewLess.click();
  expect(marketingResourcesFacet.querySelectorAll('li.hide').length).toBeGreaterThan(0);
};

const facetSuggestionBoxTest = (authenticated) => {
  document.dispatchEvent(new Event('DOMContentLoaded', {bubbles: true}));
  let marketingResourcesFacet = document.getElementById('resources_marketing-resources');
  let marketingResourcesFacetAutoSearchText = marketingResourcesFacet.querySelector('input.auto-search');
  marketingResourcesFacetAutoSearchText.value = 'b';
  marketingResourcesFacetAutoSearchText.dispatchEvent(new Event('keyup', {key: 'b'}));
  marketingResourcesFacetAutoSearchText.value = 'br';
  marketingResourcesFacetAutoSearchText.dispatchEvent(new Event('keyup', {key: 'r'}));
  let autoSuggestionsList = marketingResourcesFacet.querySelectorAll('.li-div.show');
  expect(autoSuggestionsList.length).toBe(1);
  autoSuggestionsList[0].querySelector('.filter-selected').click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchRelevanceBrochuresAjaxRequest(authenticated));
};

const secureIconTest = (authenticated, forGrid, expected = 1) => {
  let selectors = `.${ forGrid ? 'advanced-search__title' : 'advanced-search__result--list--name' } .icon-secure-lock`;
  if (authenticated) {
    expect(document.querySelectorAll(selectors).length).toBe(expected);
  } else {
    expect(document.querySelectorAll(selectors).length).toBe(0);
  }
};

const checkIfSecureIconIsCorrectlyInserted = () => {
  let securedFilter = document.getElementById('facet-eaton-secure_attributes');
  let innerSpan = securedFilter.querySelector('.inner');
  expect(innerSpan.innerHTML).toBe('<i class="icon icon-secure-lock secure__eaton"></i><bdi>secure-only</bdi>');
};

const clickSecureFilterTest = async (forGrid) => {
  let secureFacetFilter = Array.from(document.querySelectorAll('.filter-selected')).find(facet => facet.getAttribute('data-title').indexOf('secure-only') > -1);
  checkIfSecureIconIsCorrectlyInserted();
  expectAjaxCallToReturn(advancedSearchSecureOnlyResponse());
  await secureFacetFilter.click();
  secureIconTest(true, forGrid, 60);
};

const sortByDropDownTest = () => {
  let advancedSearchContainer = document.querySelector('.eaton-advanced-search');
  let defaultSortOption = advancedSearchContainer.getAttribute('data-default-sort-option');
  let sortOptionsContainer = document.querySelector('.faceted-navigation-header__sort-options');
  let sortOptions = sortOptionsContainer.querySelectorAll('.faceted-navigation-header__sort-link.select-sorting');
  sortOptions.forEach((option) => {
    if (option.getAttribute('data-value') === defaultSortOption) {
      expect(option.classList).toContain('faceted-navigation-header__sort-options--selected');
    } else {
      expect(option.classList).not.toContain('faceted-navigation-header__sort-options--selected');
    }
  });
};

const sortByOptionKeptAfterClearFiltersTest = async authenticated => {
  let sortFromAtoZ = await selectBuildingsAndReturnSortAscendingAfterClickingIt(authenticated);
  expect(callWrapperMock).toBeCalledWith(advancedSearchAscendingBuildingsAjaxRequest(authenticated));
  let resultsCount = document.getElementById('final-result');
  expect(resultsCount.innerHTML).toBe(authenticated ? '16' : '1708');
  let clearFilters = document.querySelector('.faceted-navigation-header__action-link--clear-filters');
  clearFilters.click();
  expect(document.querySelectorAll('#selectedFilter button').length).toBe(0);
  expect(callWrapperMock).toBeCalledWith(advancedSearchAscendingFullAjaxRequest(authenticated));
  expect(sortFromAtoZ.classList).toContain('faceted-navigation-header__sort-options--selected');
};

const sortByOptionKeptAfterFilterDeselected = async authenticated => {
  let sortFromAtoZ = await selectBuildingsAndReturnSortAscendingAfterClickingIt(authenticated);
  sortFromAtoZ.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchAscendingBuildingsAjaxRequest(authenticated));
  let activeFilters = document.querySelector('.faceted-navigation-header__active-filter .icon');
  activeFilters.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchAscendingFullAjaxRequest(authenticated));
  expect(sortFromAtoZ.classList).toContain('faceted-navigation-header__sort-options--selected');
};

const clickOnActiveFilterNotCrossTest = async authenticated => {
  let sortFromAtoZ = await selectBuildingsAndReturnSortAscendingAfterClickingIt(authenticated);
  sortFromAtoZ.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchAscendingBuildingsAjaxRequest(authenticated));
  let activeFilters = document.querySelector('.faceted-navigation-header__active-filter button');
  activeFilters.click();
  expect(callWrapperMock).toHaveBeenLastCalledWith(advancedSearchAscendingBuildingsAjaxRequest(authenticated));
  expect(sortFromAtoZ.classList).toContain('faceted-navigation-header__sort-options--selected');
};

const viewChangesFromGridToListAndViceVersaTest = async (forGrid, expectedSizeOfList) => {
  let otherViewButton = forGrid ? 'listbtn' : 'gridbtn';
  let otherListClass = forGrid ? 'list-view' : 'advanced-search__result';
  let listButton = document.getElementById(otherViewButton);
  listButton.click();
  let resultsDisplayed = document.getElementsByClassName(otherListClass);
  if (otherViewButton === 'listbtn') {
    document.querySelectorAll('.advance__search-list-view th').forEach((th, index) => {
      if (index >= 2 ) {
        expect(th.innerHTML).not.toBe('');
      }
    });
  }
  expect(resultsDisplayed.length).toBe(expectedSizeOfList);
};

const loadMoreTest = async (authenticated, forGrid) => {
  listClass = forGrid ? 'advanced-search__result' : 'list-view';
  let loadMoreButton = document.querySelector('.load__more');
  expectAjaxCallToReturn(advancedSearchRelevanceLoadMoreAjaxResponse());
  callWrapperMock = jest.spyOn(externalCallWrapper, 'makeCall');
  await loadMoreButton.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchRelevanceLoadMoreAjaxRequest(authenticated));
  if ( forGrid ) {
    expect(document.querySelectorAll('.' + listClass).length).toBe(120);
  } else {
    expect(document.querySelector('tbody').querySelectorAll('.' + listClass).length).toBe(120);
  }
};

const basicTrackDownloadTest = (forGrid, authenticated) => {
  let listClass = forGrid ? 'advanced-search__result' : 'list-view';
  let downloadTrackable = Array.from(document.querySelectorAll(`.${ listClass } a`)).find(link => link.href.indexOf(authenticated ? 'crouse-hinds-if1417-instruction-sheet.pdf' : 'crouse-hinds-pro-apf-l-852g-s-irgl-8-inch-led-datasheet.pdf') > -1);
  let trackExceed = document.getElementById('track-exceed');
  downloadTrackable.dispatchEvent(new MouseEvent('mousedown', {button: 0}));
  if (authenticated) {
    expect(trackExceed.classList).not.toContain('hide');
    let okayButton = document.querySelector('.okay-track');
    okayButton.click();
    expect(trackExceed.classList).toContain('hide');
    expect(bulkDownloadContainerSpy).toBeCalledWith(expect.objectContaining({assetLink: '/content/dam/eaton/products/conduit-cable-and-wire-management/crouse-hinds/instruction-sheets/crouse-hinds-if1417-instruction-sheet.pdf'}));
  } else {
    expect(trackExceed.classList).toContain('hide');
    expect(bulkDownloadContainerSpy).not.toBeCalledWith(expect.objectContaining({assetLink: '/content/dam/eaton/products/conduit-cable-and-wire-management/crouse-hinds/instruction-sheets/crouse-hinds-pro-apf-l-852g-s-irgl-8-inch-led-datasheet.pdf'}));
  }
};

const trackDownloadRightClickTest = (forGrid, authenticated) => {
  let listClass = forGrid ? 'advanced-search__result' : 'list-view';
  let downloadTrackable = Array.from(document.querySelectorAll(`.${ listClass } a`)).find(link => link.href.indexOf(authenticated ? 'crouse-hinds-if1417-instruction-sheet.pdf' : 'crouse-hinds-pro-apf-l-852g-s-irgl-8-inch-led-datasheet.pdf') > -1);
  downloadTrackable.dispatchEvent(new MouseEvent('mousedown', {button: 2}));
  if (authenticated) {
    expect(bulkDownloadContainerSpy).not.toBeCalledWith(expect.objectContaining({assetLink: '/content/dam/eaton/products/conduit-cable-and-wire-management/crouse-hinds/instruction-sheets/crouse-hinds-if1417-instruction-sheet.pdf'}));
  } else {
    expect(bulkDownloadContainerSpy).not.toBeCalledWith(expect.objectContaining({assetLink: '/content/dam/eaton/products/conduit-cable-and-wire-management/crouse-hinds/instruction-sheets/crouse-hinds-pro-apf-l-852g-s-irgl-8-inch-led-datasheet.pdf'}));
  }
};

const correctlyInitializedTest = (forGrid, authenticated) => {
  let listClass = forGrid ? 'advanced-search__result' : 'list-view';
  let resultsCount = document.getElementById('final-result');
  let resultsDisplayed = document.getElementsByClassName(listClass);
  expect(callWrapperMock).toBeCalledWith(advancedSearchRelevanceAjaxRequest(authenticated));
  expect(resultsCount.innerHTML).toBe(authenticated ? '3841' : '10503');
  expect(resultsDisplayed.length).toBe(60);
};

const singleFacetTest = authenticated => {
  expect(callWrapperMock).toBeCalledWith(advancedSearchRelevanceAjaxRequest(authenticated));
  let presentationsFacet = Array.from(document.querySelectorAll('.filter-selected')).find(facet => facet.getAttribute('data-title').indexOf('Presentations') > -1);
  let priceListsFacet = Array.from(document.querySelectorAll('.filter-selected')).find(facet => facet.getAttribute('data-title').indexOf('Price lists') > -1);
  presentationsFacet.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchPresntationsFacetsAjaxRequest(authenticated));
  priceListsFacet.click();
  expect(callWrapperMock).toBeCalledWith(advancedSearchPriceListsFacetsAjaxRequest(authenticated));
};

beforeEach(() => {
  window.Mustache = Mustache;
  window.App = withConstants();
  jest.restoreAllMocks();
});

async function clickClearFilters() {
  let clearFilters = document.querySelector('.faceted-navigation-header__action-link--clear-filters');
  await clearFilters.click();
}

describe('Advanced Search starting in grid mode authenticated', () => {
  beforeEach(async () => {
    await prepareTests(advancedSearchFromDashboardGridAuthenticated());
  });
  it('should have a fileName attribute in search result', async () => {
    let firstResult = document.querySelectorAll('.advanced-search__results a')[0];
    expect(firstResult.getAttribute('filename')).toBe('crouse-hinds-if1417-instruction-sheet.pdf');
  });
  it('should have a correctly initialized result list after init', async () => {
    await correctlyInitializedTest(true, true);
  });

  it('should have correctly inserted the secure icon if authenticated, if not, none should be present', async () => {
    await secureIconTest(true, true);
  });

  it('should have a sort by dropdown with the default sorting option selected', async () => {
    await sortByDropDownTest();
  });

  it('should keep the selected sort order when clear filters is clicked after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterClearFiltersTest(true);
  });

  it('should keep the selected sort order when active filter is deselected after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterFilterDeselected(true);
  });

  it('should not call ajax when the active filter button itself is clicked', async () => {
    await clickOnActiveFilterNotCrossTest(true);
  });

  it('should change the view to the view type opposed to the current one when clicking on one of the view buttons', async () => {
    await viewChangesFromGridToListAndViceVersaTest(true, 60);
  });

  it('should call the API with loadMoreOffset 60 when the load more button is clicked and append the list with ', async () => {
    await loadMoreTest(true, true);
  });

  it('should keep the loaded list after load more when switchig view type', async () => {
    await loadMoreTest(true, true);
    await viewChangesFromGridToListAndViceVersaTest(true, 120);
  });

  it('should call an event on bulk download when a left click happens on a link that has track-download to true ', async () => {
    await basicTrackDownloadTest(true, true);
  });

  it('should not call an event on bulk download when a right click happens on a link that has track-download to true ', async () => {
    await trackDownloadRightClickTest(true, true);
  });
  it('should make a call with date range when user enters date range', async () => {
    await dateRangeTest(true);
    expect(document.querySelector('.faceted-navigation-header__action-link--clear-filters').classList).not.toContain('filterNone');
  });

  it('should hide text with search term when no result return after searching for date range', async () => {
    await dateRangeTestNoResults(true);
    expect(document.querySelector('.faceted-navigation-header__action-link--clear-filters').classList).not.toContain('filterNone');
  });

  it('should reset the dates when click on reset dates after date range use', async () => {
    await resetDateRangeTest(true);
  });

  it('should add the facet when multiple facets are clicked', async () => {
    await multipleFacetTest(true);
  });

  it('should unselect the facet when facets is clicked again', async () => {
    await unselectFacetTest(true);
  });

  it('should search for the typed text in the search box when binoculars are clicked', async () => {
    await searchAndBinocularTest(true);
    expect(document.querySelector('.faceted-navigation-header__action-link--clear-filters').classList).not.toContain('filterNone');
  });

  it('should search for the typed text in the search box when enter is typed', async () => {
    await searchAndEnterTest(true);
    expect(document.querySelector('.faceted-navigation-header__action-link--clear-filters').classList).not.toContain('filterNone');
  });

  it('should clear search box when clear filters is clicked', async () => {
    await searchAndEnterTest(true);
    await clickClearFilters();
    let searchBox = document.getElementById('adv-site-search-box');
    expect(searchBox.value).toEqual('');
    expect(document.querySelector('.faceted-navigation-header__action-link--clear-filters').classList).toContain('filterNone');
  });

  it('should display no results section when there is no result from search', async () => {
    await noResultTest(true);
  });

  it('should add the item from resultslist corresponding to the selected item when clicking on a download checkbox', async () => {
    await bulkDownloadBasicTest(true);
  });

  it('when clicking on downloadable item again, the selected files list in session storage should be empty', async () => {
    await bulkDownloadBasicTest(true);
    let downloadItem = document.querySelectorAll('input[name=toDownload]')[0];
    await downloadItem.click();
    expect(sessionStorage.getItem('[]')).toBeNull();
  });

  it('should be able to click on load more after adding an item to bulk download, should not change the outcome', async () => {
    await bulkDownloadWithLoadMoreTest(true, true);
  });

  it('should show the item in bulkdownload in the result list when other filter is selected', async () => {
    await bulkDownloadWithSelectingOtherFacetTest(true, true);
  });

  it('should keep the list of items from bulkdownload when other view type is selected', async () => {
    await bulkDownloadBasicTest(true);
  });

  it('should keep the list of items from bulkdownload when search is done without results, after clear filters, should also remain', async () => {
    let downloadItem = document.querySelectorAll('input[name=toDownload]')[0];
    downloadItem.click();
    let searchBox = document.getElementById('adv-site-search-box');
    searchBox.value = 'text to search';
    expectAjaxCallToReturn(advancedSearchEmptyJsonResponse());
    await searchBox.dispatchEvent(new KeyboardEvent('keyup', {code: 'Enter'}));
    expect(sessionStorage.getItem('selectedFiles')).toBe(ACTUAL_AUTHENTICATED_RESPONSE);
    expectAjaxCallToReturn(advancedSearchInitialJsonResponse());
    await clickClearFilters();
    expect(document.querySelectorAll('input[name=toDownload]:checked').length).toBeGreaterThan(0);
  });

  it('should be possible to click add other items to the bulk download drawer after changing view type', async () => {
    let downloadItem = document.querySelectorAll('input[name=toDownload]')[0];
    downloadItem.click();
    await viewChangesFromGridToListAndViceVersaTest(true, 60);
    downloadItem = document.querySelectorAll('input[name=toDownload]')[1];
    downloadItem.click();
    expect(sessionStorage.getItem('selectedFiles')).toBe('[{"image":"/content/dam/eaton/products/conduit-cable-and-wire-management/crouse-hinds/instruction-sheets/crouse-hinds-if1417-instruction-sheet.pdf.thumb.1280.1280.png","downloadEnabled":true,"publishDate":"08/20/2021","description":"","newBadgeVisible":false,"language":"Multilingual","trackDownload":"true","secure":false,"title":"IF 1417 - TECK cable fittings","url":"/content/dam/eaton/products/conduit-cable-and-wire-management/crouse-hinds/instruction-sheets/crouse-hinds-if1417-instruction-sheet.pdf","Language":"Multilingual","fileSize":"110620","dcFormat":"application/pdf","id":"cf7c37dd","contentType":"","fileType":"application/pdf","fileTypeAndSize":"PDF 110KB","epochPublishDate":"1629490954994","status":"","fileName":"crouse-hinds-if1417-instruction-sheet.pdf"},{"image":"/content/dam/eaton/products/conduit-cable-and-wire-management/crouse-hinds/instruction-sheets/crouse-hinds-if1183-instruction-sheet.pdf.thumb.1280.1280.png","downloadEnabled":true,"publishDate":"08/20/2021","description":"","newBadgeVisible":false,"language":"Multilingual","trackDownload":"","secure":false,"title":"IF 1183 - Terminator cable fittings","url":"/content/dam/eaton/products/conduit-cable-and-wire-management/crouse-hinds/instruction-sheets/crouse-hinds-if1183-instruction-sheet.pdf","Language":"Multilingual","fileSize":"749337","dcFormat":"application/pdf","id":"c23e1522","contentType":"","fileType":"application/pdf","fileTypeAndSize":"PDF 749KB","epochPublishDate":"1629490900504","status":"","fileName":"crouse-hinds-if1183-instruction-sheet.pdf"}]');
  });

  it('should expand the facets when view more button is clicked', async () => {
    await facetViewMoreButtonTest();
  });

  it('should colapse the facets when view less button is clicked', async () => {
    await facetViewLessButtonTest();
  });

  it('should show the box of suggestions when search is used for facets', async () => {
    await facetSuggestionBoxTest(true);
  });
  it('should change the facet on click when single facets are clicked', async () => {
    await prepareTests(advancedSearchFromDashboardGridAuthenticated(), advancedSearchWithRadioFacetsJsonResponse());
    singleFacetTest(true);
  });
  it('should have called API for secure only when secure only filter is clicked', async () => {
    await clickSecureFilterTest(true);
  });
});

describe('Advanced Search starting in grid mode not authenticated', () => {
  beforeEach(() => {
    prepareTests(advancedSearchFromDashboardGridNotAuthenticated(), advancedSearchInitialJsonResponseNonAuthenticated());
  });
  it('should have a correctly initialized result list after init', async () => {
    await correctlyInitializedTest(true, false);
  });

  it('should have correctly inserted the secure icon if authenticated, if not, none should be present', async () => {
    await secureIconTest(false, true);
  });

  it('should have a sort by dropdown with the default sorting option selected', async () => {
    await sortByDropDownTest();
  });

  it('should keep the selected sort order when clear filters is clicked after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterClearFiltersTest(false);
  });

  it('should keep the selected sort order when active filter is deselected after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterFilterDeselected(false);
  });

  it('should not call ajax when the active filter button itself is clicked', async () => {
    await clickOnActiveFilterNotCrossTest(false);
  });

  it('should change the view to the view type opposed to the current one when clicking on one of the view buttons', async () => {
    await viewChangesFromGridToListAndViceVersaTest(true, 60);
  });

  it('should call the API with loadMoreOffset 60 when the load more button is clicked and append the list with ', async () => {
    await loadMoreTest(false, true);
  });

  it('should call an event on bulk download when a left click happens on a link that has track-download to true ', async () => {
    await basicTrackDownloadTest(true, false);
  });

  it('should not call an event on bulk download when a right click happens on a link that has track-download to true ', async () => {
    await trackDownloadRightClickTest(true, false);
  });
  it('should make a call with date range when user enters date range', async () => {
    await dateRangeTest(false);
  });

  it('should reset the dates when click on reset dates after date range use', async () => {
    await resetDateRangeTest(false);
  });

  it('should add the facet when multiple facets are clicked', async () => {
    await multipleFacetTest(false);
  });

  it('should unselect the facet when facets is clicked again', async () => {
    await unselectFacetTest(false);
  });

  it('should search for the typed text in the search box when binoculars are clicked', async () => {
    await searchAndBinocularTest(false);
  });

  it('should search for the typed text in the search box when enter is typed', async () => {
    await searchAndEnterTest(false);
  });

  it('should display no results section when there is no result from search', async () => {
    await noResultTest(false);
  });

  it('should add the item from resultslist corresponding to the selected item when clicking on a download checkbox', async () => {
    await bulkDownloadBasicTest(false);
  });

  it('should be able to click on load more after adding an item to bulk download, should not change the outcome', async () => {
    await bulkDownloadWithLoadMoreTest(false, true);
  });

  it('should show the item in bulkdownload in the result list when other filter is selected', async () => {
    await bulkDownloadWithSelectingOtherFacetTest(false, true);
  });

  it('should expand the facets when view more button is clicked', async () => {
    await facetViewMoreButtonTest();
  });

  it('should colapse the facets when view less button is clicked', async () => {
    await facetViewLessButtonTest();
  });

  it('should show the box of suggestions when search is used for facets', async () => {
    await facetSuggestionBoxTest(false);
  });
  it('should change the facet on click when single facets are clicked', async () => {
    await prepareTests(advancedSearchFromDashboardGridNotAuthenticated(), advancedSearchWithRadioFacetsJsonResponse());
    singleFacetTest(false);
  });
});

describe('Advanced Search starting in list mode authenticated', () => {
  beforeEach(() => {
    prepareTests(advancedSearchFromDashboardListAuthenticated());
  });
  it('should have a correctly initialized result list after init', async () => {
    await correctlyInitializedTest(false, true);
  });

  it('should have correctly inserted the secure icon if authenticated, if not, none should be present', async () => {
    await secureIconTest(true, false);
  });

  it('should have a sort by dropdown with the default sorting option selected', async () => {
    await sortByDropDownTest();
  });

  it('should keep the selected sort order when clear filters is clicked after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterClearFiltersTest(true);
  });

  it('should keep the selected sort order when active filter is deselected after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterFilterDeselected(true);
  });

  it('should not call ajax when the active filter button itself is clicked', async () => {
    await clickOnActiveFilterNotCrossTest(true);
  });

  it('should change the view to the view type opposed to the current one when clicking on one of the view buttons', async () => {
    await viewChangesFromGridToListAndViceVersaTest(false, 60);
  });

  it('should call the API with loadMoreOffset 60 when the load more button is clicked and append the list with ', async () => {
    await loadMoreTest(true, false);
  });

  it('should call an event on bulk download when a left click happens on a link that has track-download to true ', async () => {
    await basicTrackDownloadTest(false, true);
  });

  it('should not call an event on bulk download when a right click happens on a link that has track-download to true ', async () => {
    await trackDownloadRightClickTest(false, true);
  });
  it('should make a call with date range when user enters date range', async () => {
    await dateRangeTest(true);
  });

  it('should reset the dates when click on reset dates after date range use', async () => {
    await resetDateRangeTest(true);
  });

  it('should add the facet when multiple facets are clicked', async () => {
    await multipleFacetTest(true);
  });

  it('should unselect the facet when facets is clicked again', async () => {
    await unselectFacetTest(true);
  });

  it('should search for the typed text in the search box when binoculars are clicked', async () => {
    await searchAndBinocularTest(true);
  });

  it('should search for the typed text in the search box when enter is typed', async () => {
    await searchAndEnterTest(true);
  });

  it('should display no results section when there is no result from search', async () => {
    await noResultTest(true);
  });

  it('should add the item from resultslist corresponding to the selected item when clicking on a download checkbox', async () => {
    await bulkDownloadBasicTest(true);
  });

  it('should be able to click on load more after adding an item to bulk download, should not change the outcome', async () => {
    await bulkDownloadWithLoadMoreTest(true, false);
  });

  it('should show the item in bulkdownload in the result list when other filter is selected', async () => {
    await bulkDownloadWithSelectingOtherFacetTest(true, false);
  });

  it('should expand the facets when view more button is clicked', async () => {
    await facetViewMoreButtonTest();
  });

  it('should colapse the facets when view less button is clicked', async () => {
    await facetViewLessButtonTest();
  });

  it('should show the box of suggestions when search is used for facets', async () => {
    await facetSuggestionBoxTest(true);
  });
  it('should change the facet on click when single facets are clicked', async () => {
    await prepareTests(advancedSearchFromDashboardListAuthenticated(), advancedSearchWithRadioFacetsJsonResponse());
    singleFacetTest(true);
  });
  it('should have called API for secure only when secure only filter is clicked', async () => {
    await clickSecureFilterTest(false);
  });
});

describe('Advanced Search starting in list mode not authenticated', () => {
  beforeEach(() => {
    prepareTests(advancedSearchFromDashboardListNotAuthenticated(), advancedSearchInitialJsonResponseNonAuthenticated());
  });
  it('should have a correctly initialized result list after init', async () => {
    await correctlyInitializedTest(false, false);
  });

  it('should have correctly inserted the secure icon if authenticated, if not, none should be present', async () => {
    await secureIconTest(false, false);
  });

  it('should have a sort by dropdown with the default sorting option selected', async () => {
    await sortByDropDownTest();
  });

  it('should keep the selected sort order when clear filters is clicked after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterClearFiltersTest(false);
  });

  it('should keep the selected sort order when active filter is deselected after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterFilterDeselected(false);
  });

  it('should not call ajax when the active filter button itself is clicked', async () => {
    await clickOnActiveFilterNotCrossTest(false);
  });

  it('should change the view to the view type opposed to the current one when clicking on one of the view buttons', async () => {
    await viewChangesFromGridToListAndViceVersaTest(false, 60);
  });

  it('should call the API with loadMoreOffset 60 when the load more button is clicked and append the list with ', async () => {
    await loadMoreTest(false, false);
  });

  it('should call an event on bulk download when a left click happens on a link that has track-download to true ', async () => {
    await basicTrackDownloadTest(false, false);
  });

  it('should not call an event on bulk download when a right click happens on a link that has track-download to true ', async () => {
    await trackDownloadRightClickTest(false, false);
  });
  it('should make a call with date range when user enters date range', async () => {
    await dateRangeTest(false);
  });

  it('should reset the dates when click on reset dates after date range use', async () => {
    await resetDateRangeTest(false);
  });

  it('should add the facet when multiple facets are clicked', async () => {
    await multipleFacetTest(false);
  });

  it('should unselect the facet when facets is clicked again', async () => {
    await unselectFacetTest(false);
  });

  it('should search for the typed text in the search box when binoculars are clicked', async () => {
    await searchAndBinocularTest(false);
  });

  it('should search for the typed text in the search box when enter is typed', async () => {
    await searchAndEnterTest(false);
  });

  it('should display no results section when there is no result from search', async () => {
    await noResultTest(false);
  });

  it('should add the item from resultslist corresponding to the selected item when clicking on a download checkbox', async () => {
    await bulkDownloadBasicTest(false);
  });

  it('should be able to click on load more after adding an item to bulk download, should not change the outcome', async () => {
    await bulkDownloadWithLoadMoreTest(false, false);
  });

  it('should show the item in bulkdownload in the result list when other filter is selected', async () => {
    await bulkDownloadWithSelectingOtherFacetTest(false, false);
  });

  it('should expand the facets when view more button is clicked', async () => {
    await facetViewMoreButtonTest();
  });

  it('should collapse the facets when view less button is clicked', async () => {
    await facetViewLessButtonTest();
  });

  it('should show the box of suggestions when search is used for facets', async () => {
    await facetSuggestionBoxTest(false);
  });
  it('should change the facet on click when single facets are clicked', async () => {
    await prepareTests(advancedSearchFromDashboardListNotAuthenticated(), advancedSearchWithRadioFacetsJsonResponse());
    singleFacetTest(false);
  });
});

describe('AdvancedSearch in mobile view', () => {
  let secureFilter = {};
  beforeEach(() => {
    window.innerWidth = 896;
    prepareTests(advancedSearchFromDashboardListNotAuthenticated(), advancedSearchInitialJsonResponseNonAuthenticated());
    secureFilter = document.getElementById('secure-filter');
  });
  it('should display the popup for filters when clicked on filters button', async () => {
    expect(document.body.classList).not.toContain('facets-open');
    secureFilter.click();
    expect(document.body.classList).toContain('facets-open');
  });
  it('should expand the facets when clicked on button to expand a facet', async () => {
    secureFilter.click();
    let marketingResourcesFilter = Array.from(document.querySelectorAll('.faceted-navigation__header.button--reset')).find(facetButton => facetButton.innerHTML.indexOf('Marketing resources') > -1);
    marketingResourcesFilter.click();
    expect(document.getElementById('resources_marketing-resources').classList[0]).toBe('collapse');
    expect(document.getElementById('resources_marketing-resources').classList[1]).toBe('in');
  });
  it('should close the popup for filters when clicked on close icon', async () => {
    secureFilter.click();
    expect(document.body.classList).toContain('facets-open');
    let closeButton = document.querySelector('.icon-close-filter');
    closeButton.click();
    expect(document.body.classList).not.toContain('facets-open');
  });
});

describe('Advanced Search default grid view without grid or list view', () => {
  beforeEach(() => {
    prepareTests(advancedSearchWithoutGridListViewDefaultGrid(), advancedSearchInitialJsonResponseNonAuthenticated());
  });
  it('should have a correctly initialized result list after init', async () => {
    await correctlyInitializedTest(true, false);
  });

  it('should have correctly inserted the secure icon if authenticated, if not, none should be present', async () => {
    await secureIconTest(false, true);
  });

  it('should have a sort by dropdown with the default sorting option selected', async () => {
    await sortByDropDownTest();
  });

  it('should keep the selected sort order when clear filters is clicked after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterClearFiltersTest(false);
  });

  it('should keep the selected sort order when active filter is deselected after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterFilterDeselected(false);
  });

  it('should not call ajax when the active filter button itself is clicked', async () => {
    await clickOnActiveFilterNotCrossTest(false);
  });

  it('should call the API with loadMoreOffset 60 when the load more button is clicked and append the list with ', async () => {
    await loadMoreTest(false, true);
  });

  it('should call an event on bulk download when a left click happens on a link that has track-download to true ', async () => {
    await basicTrackDownloadTest(true, false);
  });

  it('should not call an event on bulk download when a right click happens on a link that has track-download to true ', async () => {
    await trackDownloadRightClickTest(true, false);
  });
  it('should make a call with date range when user enters date range', async () => {
    await dateRangeTest(false);
  });

  it('should reset the dates when click on reset dates after date range use', async () => {
    await resetDateRangeTest(false);
  });

  it('should add the facet when multiple facets are clicked', async () => {
    await multipleFacetTest(false);
  });

  it('should unselect the facet when facets is clicked again', async () => {
    await unselectFacetTest(false);
  });

  it('should search for the typed text in the search box when binoculars are clicked', async () => {
    await searchAndBinocularTest(false);
  });

  it('should search for the typed text in the search box when enter is typed', async () => {
    await searchAndEnterTest(false);
  });

  it('should display no results section when there is no result from search', async () => {
    await noResultTest(false);
  });

  it('should add the item from resultslist corresponding to the selected item when clicking on a download checkbox', async () => {
    await bulkDownloadBasicTest(false);
  });

  it('should be able to click on load more after adding an item to bulk download, should not change the outcome', async () => {
    await bulkDownloadWithLoadMoreTest(false, true, false);
  });

  it('should show the item in bulkdownload in the result list when other filter is selected', async () => {
    await bulkDownloadWithSelectingOtherFacetTest(false, true);
  });

  it('should expand the facets when view more button is clicked', async () => {
    await facetViewMoreButtonTest();
  });

  it('should colapse the facets when view less button is clicked', async () => {
    await facetViewLessButtonTest();
  });

  it('should show the box of suggestions when search is used for facets', async () => {
    await facetSuggestionBoxTest(false);
  });
  it('should change the facet on click when single facets are clicked', async () => {
    await prepareTests(advancedSearchWithoutGridListViewDefaultGrid(), advancedSearchWithRadioFacetsJsonResponse());
    await singleFacetTest(false);
  });
});

describe('Advanced Search default list without grid or list view', () => {
  beforeEach(() => {
    prepareTests(advancedSearchWithoutGridListViewDefaultList(), advancedSearchInitialJsonResponseNonAuthenticated());
  });
  it('should have a correctly initialized result list after init', async () => {
    await correctlyInitializedTest(false, false);
  });

  it('should have correctly inserted the secure icon if authenticated, if not, none should be present', async () => {
    await secureIconTest(false, false);
  });

  it('should have a sort by dropdown with the default sorting option selected', async () => {
    await sortByDropDownTest();
  });

  it('should keep the selected sort order when clear filters is clicked after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterClearFiltersTest(false);
  });

  it('should keep the selected sort order when active filter is deselected after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterFilterDeselected(false);
  });

  it('should not call ajax when the active filter button itself is clicked', async () => {
    await clickOnActiveFilterNotCrossTest(false);
  });

  it('should call the API with loadMoreOffset 60 when the load more button is clicked and append the list with ', async () => {
    await loadMoreTest(false, false);
  });

  it('should call an event on bulk download when a left click happens on a link that has track-download to true ', async () => {
    await basicTrackDownloadTest(false, false);
  });

  it('should not call an event on bulk download when a right click happens on a link that has track-download to true ', async () => {
    await trackDownloadRightClickTest(false, false);
  });
  it('should make a call with date range when user enters date range', async () => {
    await dateRangeTest(false);
  });

  it('should reset the dates when click on reset dates after date range use', async () => {
    await resetDateRangeTest(false);
  });

  it('should add the facet when multiple facets are clicked', async () => {
    await multipleFacetTest(false);
  });

  it('should unselect the facet when facets is clicked again', async () => {
    await unselectFacetTest(false);
  });

  it('should search for the typed text in the search box when binoculars are clicked', async () => {
    await searchAndBinocularTest(false);
  });

  it('should search for the typed text in the search box when enter is typed', async () => {
    await searchAndEnterTest(false);
  });

  it('should display no results section when there is no result from search', async () => {
    await noResultTest(false);
  });

  it('should add the item from resultslist corresponding to the selected item when clicking on a download checkbox', async () => {
    await bulkDownloadBasicTest(false);
  });

  it('should be able to click on load more after adding an item to bulk download, should not change the outcome', async () => {
    await bulkDownloadWithLoadMoreTest(false, false, false);
  });

  it('should show the item in bulkdownload in the result list when other filter is selected', async () => {
    await bulkDownloadWithSelectingOtherFacetTest(false, false);
  });

  it('should expand the facets when view more button is clicked', async () => {
    await facetViewMoreButtonTest();
  });

  it('should colapse the facets when view less button is clicked', async () => {
    await facetViewLessButtonTest();
  });

  it('should show the box of suggestions when search is used for facets', async () => {
    await facetSuggestionBoxTest(false);
  });
  it('should change the facet on click when single facets are clicked', async () => {
    await prepareTests(advancedSearchWithoutGridListViewDefaultList(), advancedSearchWithRadioFacetsJsonResponse());
    await singleFacetTest(false);
  });
});

describe('Advanced Search default grid without extra options authenticated', () => {
  beforeEach(() => {
    prepareTests(noExtraOptionSelectedGrid(true), advancedSearchInitialJsonResponseWithoutBulkDownloadAuthenticated());
  });
  it('should have a correctly initialized result list after init', async () => {
    await correctlyInitializedTest(true, true);
  });

  it('should have correctly inserted the secure icon if authenticated, if not, none should be present', async () => {
    await secureIconTest(true, true);
  });

  it('should have a sort by dropdown with the default sorting option selected', async () => {
    await sortByDropDownTest();
  });

  it('should keep the selected sort order when clear filters is clicked after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterClearFiltersTest(true);
  });

  it('should keep the selected sort order when active filter is deselected after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterFilterDeselected(true);
  });

  it('should not call ajax when the active filter button itself is clicked', async () => {
    await clickOnActiveFilterNotCrossTest(true);
  });

  it('should call the API with loadMoreOffset 60 when the load more button is clicked and append the list with ', async () => {
    await loadMoreTest(true, true);
  });

  it('should not have any checkbox to bulk download ', async () => {
    expect(document.querySelectorAll('input[name=toDownload]').length).toBe(0);
  });

  it('should not have the possibility to select date range', async () => {
    expect(document.querySelectorAll('.apply-date').length).toBe(0);
    expect(document.querySelectorAll(document.querySelector('.start-date')).length).toBe(0);
    expect(document.querySelectorAll(document.querySelector('.end-date')).length).toBe(0);
  });

  it('should add the facet when multiple facets are clicked', async () => {
    await multipleFacetTest(true);
  });

  it('should unselect the facet when facets is clicked again', async () => {
    await unselectFacetTest(true);
  });

  it('should search for the typed text in the search box when binoculars are clicked', async () => {
    await searchAndBinocularTest(true);
  });

  it('should search for the typed text in the search box when enter is typed', async () => {
    await searchAndEnterTest(true);
  });

  it('should display no results section when there is no result from search', async () => {
    await noResultTest(true);
  });

  it('should expand the facets when view more button is clicked', async () => {
    await facetViewMoreButtonTest();
  });

  it('should colapse the facets when view less button is clicked', async () => {
    await facetViewLessButtonTest();
  });

  it('should show the box of suggestions when search is used for facets', async () => {
    await facetSuggestionBoxTest(true);
  });
  it('should change the facet on click when single facets are clicked', async () => {
    await prepareTests(noExtraOptionSelectedGrid(true), advancedSearchWithRadioFacetsJsonResponse());
    await singleFacetTest(true);
  });
});
describe('Advanced Search default grid without extra options not authenticated', () => {
  beforeEach(() => {
    prepareTests(noExtraOptionSelectedGrid(false), advancedSearchInitialJsonResponseWithoutBulkDownloadNonAuthenticated());
  });
  it('should have a correctly initialized result list after init', async () => {
    await correctlyInitializedTest(true, false);
  });

  it('should have correctly inserted the secure icon if authenticated, if not, none should be present', async () => {
    await secureIconTest(false, true);
  });

  it('should have a sort by dropdown with the default sorting option selected', async () => {
    await sortByDropDownTest();
  });

  it('should keep the selected sort order when clear filters is clicked after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterClearFiltersTest(false);
  });

  it('should keep the selected sort order when active filter is deselected after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterFilterDeselected(false);
  });

  it('should not call ajax when the active filter button itself is clicked', async () => {
    await clickOnActiveFilterNotCrossTest(false);
  });

  it('should call the API with loadMoreOffset 60 when the load more button is clicked and append the list with ', async () => {
    await loadMoreTest(false, true);
  });

  it('should not have any checkbox to bulk download ', async () => {
    expect(document.querySelectorAll('input[name=toDownload]').length).toBe(0);
  });

  it('should not have the possibility to select date range', async () => {
    expect(document.querySelectorAll('.apply-date').length).toBe(0);
    expect(document.querySelectorAll(document.querySelector('.start-date')).length).toBe(0);
    expect(document.querySelectorAll(document.querySelector('.end-date')).length).toBe(0);
  });

  it('should add the facet when multiple facets are clicked', async () => {
    await multipleFacetTest(false);
  });

  it('should unselect the facet when facets is clicked again', async () => {
    await unselectFacetTest(false);
  });

  it('should search for the typed text in the search box when binoculars are clicked', async () => {
    await searchAndBinocularTest(false);
  });

  it('should search for the typed text in the search box when enter is typed', async () => {
    searchAndEnterTest(false);
  });

  it('should display no results section when there is no result from search', async () => {
    await noResultTest(false);
  });

  it('should expand the facets when view more button is clicked', async () => {
    await facetViewMoreButtonTest();
  });

  it('should colapse the facets when view less button is clicked', async () => {
    await facetViewLessButtonTest();
  });

  it('should show the box of suggestions when search is used for facets', async () => {
    await facetSuggestionBoxTest(false);
  });
  it('should change the facet on click when single facets are clicked', async () => {
    await prepareTests(noExtraOptionSelectedGrid(false), advancedSearchWithRadioFacetsJsonResponse());
    await singleFacetTest(false);
  });
});
describe('Advanced Search default list without extra options authenticated', () => {
  beforeEach(() => {
    prepareTests(noExtraOptionSelectedList(true), advancedSearchInitialJsonResponseWithoutBulkDownloadAuthenticated());
  });
  it('should have a correctly initialized result list after init', async () => {
    await correctlyInitializedTest(false, true);
  });

  it('should have correctly inserted the secure icon if authenticated, if not, none should be present', async () => {
    await secureIconTest(true, false);
  });

  it('should have a sort by dropdown with the default sorting option selected', async () => {
    await sortByDropDownTest();
  });

  it('should keep the selected sort order when clear filters is clicked after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterClearFiltersTest(true);
  });

  it('should keep the selected sort order when active filter is deselected after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterFilterDeselected(true);
  });

  it('should not call ajax when the active filter button itself is clicked', async () => {
    await clickOnActiveFilterNotCrossTest(true);
  });

  it('should call the API with loadMoreOffset 60 when the load more button is clicked and append the list with ', async () => {
    await loadMoreTest(true, false);
  });

  it('should not have any checkbox to bulk download ', async () => {
    await expect(document.querySelectorAll('input[name=toDownload]').length).toBe(0);
  });

  it('should not have the possibility to select date range', async () => {
    expect(document.querySelectorAll('.apply-date').length).toBe(0);
    expect(document.querySelectorAll(document.querySelector('.start-date')).length).toBe(0);
    expect(document.querySelectorAll(document.querySelector('.end-date')).length).toBe(0);
  });

  it('should add the facet when multiple facets are clicked', async () => {
    await multipleFacetTest(true);
  });

  it('should unselect the facet when facets is clicked again', async () => {
    await unselectFacetTest(true);
  });

  it('should search for the typed text in the search box when binoculars are clicked', async () => {
    await searchAndBinocularTest(true);
  });

  it('should search for the typed text in the search box when enter is typed', async () => {
    await searchAndEnterTest(true);
  });

  it('should display no results section when there is no result from search', async () => {
    await noResultTest(true);
  });

  it('should expand the facets when view more button is clicked', async () => {
    await facetViewMoreButtonTest();
  });

  it('should colapse the facets when view less button is clicked', async () => {
    facetViewLessButtonTest();
  });

  it('should show the box of suggestions when search is used for facets', async () => {
    await facetSuggestionBoxTest(true);
  });
  it('should change the facet on click when single facets are clicked', async () => {
    await prepareTests(noExtraOptionSelectedList(true), advancedSearchWithRadioFacetsJsonResponse());
    await singleFacetTest(true);
  });
});
describe('Advanced Search default list without extra options not authenticated', () => {
  beforeEach(() => {
    prepareTests(noExtraOptionSelectedList(false), advancedSearchInitialJsonResponseWithoutBulkDownloadNonAuthenticated());
  });
  it('should have a correctly initialized result list after init', async () => {
    await correctlyInitializedTest(false, false);
  });

  it('should have correctly inserted the secure icon if authenticated, if not, none should be present', async () => {
    await secureIconTest(false, false);
  });

  it('should have a sort by dropdown with the default sorting option selected', async () => {
    await sortByDropDownTest();
  });

  it('should keep the selected sort order when clear filters is clicked after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterClearFiltersTest(false);
  });

  it('should keep the selected sort order when active filter is deselected after selecting filters and other sort order other than default', async () => {
    await sortByOptionKeptAfterFilterDeselected(false);
  });

  it('should not call ajax when the active filter button itself is clicked', async () => {
    await clickOnActiveFilterNotCrossTest(false);
  });

  it('should call the API with loadMoreOffset 60 when the load more button is clicked and append the list with ', async () => {
    await loadMoreTest(false, false);
  });

  it('should not have any checkbox to bulk download ', async () => {
    expect(document.querySelectorAll('input[name=toDownload]').length).toBe(0);
  });

  it('should not have the possibility to select date range', async () => {
    expect(document.querySelectorAll('.apply-date').length).toBe(0);
    expect(document.querySelectorAll(document.querySelector('.start-date')).length).toBe(0);
    expect(document.querySelectorAll(document.querySelector('.end-date')).length).toBe(0);
  });

  it('should add the facet when multiple facets are clicked', async () => {
    await multipleFacetTest(false);
  });

  it('should unselect the facet when facets is clicked again', async () => {
    await unselectFacetTest(false);
  });

  it('should search for the typed text in the search box when binoculars are clicked', async () => {
    await searchAndBinocularTest(false);
  });

  it('should search for the typed text in the search box when enter is typed', async () => {
    await searchAndEnterTest(false);
  });

  it('should display no results section when there is no result from search', async () => {
    await noResultTest(false);
  });

  it('should expand the facets when view more button is clicked', async () => {
    await facetViewMoreButtonTest();
  });

  it('should colapse the facets when view less button is clicked', async () => {
    await facetViewLessButtonTest();
  });

  it('should show the box of suggestions when search is used for facets', async () => {
    await facetSuggestionBoxTest(false);
  });
  it('should change the facet on click when single facets are clicked', async () => {
    await prepareTests(noExtraOptionSelectedList(false), advancedSearchWithRadioFacetsJsonResponse());
    await singleFacetTest(false);
  });
});
