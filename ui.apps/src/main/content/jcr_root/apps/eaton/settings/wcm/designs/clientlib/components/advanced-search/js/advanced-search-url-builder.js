/**
 *
 *
 *
 * - THIS IS AN AUTOGENERATED FILE. DO NOT EDIT THIS FILE DIRECTLY -
 * - Generated by Gulp (gulp-babel).
 *
 *
 *
 *
 */


'use strict';

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

/* eslint-disable no-undef */
// noinspection JSConstantReassignment

if (typeof require !== 'undefined') {
  var globalConstants = require('../../../global/js/etn-new-global-constants');
  constants = require('./advanced-search-constants');
  searchPrefixes = constants.searchPrefixes;
  suffixes = globalConstants.suffixes;
}

var AdvancedSearchUrlBuilder = function () {
  function AdvancedSearchUrlBuilder(baseUrl) {
    _classCallCheck(this, AdvancedSearchUrlBuilder);

    this.baseUrl = baseUrl;
    this.searchTerm = '';
    this.sortBy = '';
    this.facet = '';
    this.startDate = '';
    this.endDate = '';
    this.loadMore = '';
  }

  _createClass(AdvancedSearchUrlBuilder, [{
    key: 'withSearchTerm',
    value: function withSearchTerm(searchTerm) {
      this.searchTerm = searchTerm;
      return this;
    }
  }, {
    key: 'withSortBy',
    value: function withSortBy(sortBy) {
      this.sortBy = sortBy;
      return this;
    }
  }, {
    key: 'withStartDate',
    value: function withStartDate(startDate) {
      this.startDate = startDate;
      return this;
    }
  }, {
    key: 'withEndDate',
    value: function withEndDate(endDate) {
      this.endDate = endDate;
      return this;
    }
  }, {
    key: 'withLoadMore',
    value: function withLoadMore(loadMore) {
      this.loadMore = loadMore;
      return this;
    }
  }, {
    key: 'addFacet',
    value: function addFacet(facet) {
      this.facet += '$' + facet;
      return this;
    }
  }, {
    key: 'removeFacet',
    value: function removeFacet(facet) {
      this.facet = this.facet.replace('$' + facet, '');
      return this;
    }
  }, {
    key: 'resetExceptSortBy',
    value: function resetExceptSortBy() {
      this.searchTerm = '';
      this.facet = '';
      this.startDate = '';
      this.endDate = '';
      this.loadMore = '';
      return this;
    }
  }, {
    key: 'constructUrl',
    value: function constructUrl() {
      var constructedUrl = this.baseUrl + searchPrefixes.SEARCH_TERM + this.searchTerm + searchPrefixes.SORT_BY + this.sortBy + searchPrefixes.FACET + this.facet + searchPrefixes.START_DATE + this.startDate + searchPrefixes.END_DATE + this.endDate + searchPrefixes.LOAD_MORE + this.loadMore + suffixes.NOCACHED_JSON;
      constructedUrl = constructedUrl.replace('$$', '$');
      constructedUrl = constructedUrl.replace('NaN', '');
      return constructedUrl;
    }
  }]);

  return AdvancedSearchUrlBuilder;
}();

var createUrlBuilder = function createUrlBuilder(baseUrl, authenticated) {
  return new AdvancedSearchUrlBuilder(baseUrl, authenticated);
};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = { createUrlBuilder: createUrlBuilder };
}