/* eslint-disable no-undef */
// noinspection JSConstantReassignment

if (typeof require !== 'undefined') {
  const globalConstants = require('../../../global/js/etn-new-global-constants');
  constants = require('./advanced-search-constants');
  searchPrefixes = constants.searchPrefixes;
  suffixes = globalConstants.suffixes;
}
class AdvancedSearchUrlBuilder {
  constructor(baseUrl) {
    this.baseUrl = baseUrl;
    this.searchTerm = '';
    this.sortBy = '';
    this.facet = '';
    this.startDate = '';
    this.endDate = '';
    this.loadMore = '';
  }

  withSearchTerm(searchTerm) {
    this.searchTerm = searchTerm;
    return this;
  }

  withSortBy(sortBy) {
    this.sortBy = sortBy;
    return this;
  }

  withStartDate(startDate) {
    this.startDate = startDate;
    return this;
  }
  withEndDate(endDate) {
    this.endDate = endDate;
    return this;
  }
  withLoadMore(loadMore) {
    this.loadMore = loadMore;
    return this;
  }
  addFacet(facet) {
    this.facet += '$' + facet;
    return this;
  }

  removeFacet(facet) {
    this.facet = this.facet.replace('$' + facet, '');
    return this;
  }

  resetExceptSortBy() {
    this.searchTerm = '';
    this.facet = '';
    this.startDate = '';
    this.endDate = '';
    this.loadMore = '';
    return this;
  }

  constructUrl() {
    let constructedUrl = this.baseUrl
      + searchPrefixes.SEARCH_TERM + this.searchTerm
      + searchPrefixes.SORT_BY + this.sortBy
      + searchPrefixes.FACET + this.facet
      + searchPrefixes.START_DATE + this.startDate + searchPrefixes.END_DATE + this.endDate
      + searchPrefixes.LOAD_MORE + this.loadMore + suffixes.NOCACHED_JSON;
    constructedUrl = constructedUrl.replace('$$', '$');
    constructedUrl = constructedUrl.replace('NaN', '');
    return constructedUrl;
  }
}

const createUrlBuilder = (baseUrl, authenticated) => {
  return new AdvancedSearchUrlBuilder(baseUrl, authenticated);
};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = {createUrlBuilder};
}
