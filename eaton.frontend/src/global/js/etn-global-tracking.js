/* jshint esversion: 6 */

//-----------------------------------
//  Global Tracking
//-----------------------------------
'use strict';

document.addEventListener('DOMContentLoaded', function() {

  let currentUrl = new URL(document.location.href);
  setSourceTrackingParameters(currentUrl);
  setPagePathTrackingParameters(currentUrl);

});

/**
 * Append the source tracking parameters to all elements with data-source-tracking=true
 * Source tracking parameters include: percolateContentId and any url parameters in the current url
 * @param currentUrl
 */
const setSourceTrackingParameters = (currentUrl) => {
  appendCurrentUrlParams(currentUrl);
  appendPercolateContentIdParam();
};

const appendCurrentUrlParams = (currentUrl) => {

  let currentUrlSearchParams = currentUrl.searchParams;
  if (currentUrlSearchParams !== '') {
    currentUrlSearchParams.forEach((value, key) => {
      setSearchParamOnAllTrackingElements('source-tracking', key, value);
    });
  }

};

const appendPercolateContentIdParam = () => {
  // eslint-disable-next-line no-undef
  if (typeof dataLayer.percolateContentId !== 'undefined') {

    // eslint-disable-next-line no-undef
    let percolateContentIdVal = dataLayer.percolateContentId;
    if (percolateContentIdVal !== '') {
      setSearchParamOnAllTrackingElements('source-tracking', 'percolateContentId', percolateContentIdVal);
    }
  }
};

const setPagePathTrackingParameters = (currentUrl) => {

  let currentPath = currentUrl.pathname.split('.html')[0];
  setSearchParamOnAllTrackingElements('page-path-tracking', 'productPagePath', currentPath);

};

/**
 * Append a url parameter on all elements with a data-{trackingType} attribute set to true
 * @param trackingType - data attribute name for the type of tracking to find (i.e. page-path-tracking, source-tracking)
 * @param searchParamName - url parameter name
 * @param searchParamValue - url parameter value
 */
const setSearchParamOnAllTrackingElements = (trackingType, searchParamName, searchParamValue) => {

  let elements = document.querySelectorAll(`[data-${ trackingType }="true"]`);
  elements.forEach((element) => {
    let elementUrl = new URL(element.href);
    elementUrl.searchParams.set(searchParamName, searchParamValue);
    element.href = elementUrl.href;
  });


};


if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {

  window.dataLayer = [];
  window.dataLayer.percolateContentId = '22002200';

  module.exports = {
    setSourceTrackingParameters,
    appendCurrentUrlParams,
    appendPercolateContentIdParam,
    setPagePathTrackingParameters,
    setSearchParamOnAllTrackingElements
  };

}
