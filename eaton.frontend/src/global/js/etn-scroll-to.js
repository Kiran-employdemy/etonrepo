//-----------------------------------
// Component: Example Component
//-----------------------------------
'use strict';

let App = window.App || {};
App.scroll = (function() {

  // Cached DOM Elements
  //--------------
  const $componentSelector = $('[data-scroll-to]');
  const $htmlEl = $('html, body');
  const headerEl = document.querySelector('.eaton-header');

  const HEADER_HEIGHT_WHEN_STICKY = 84;
  const HEADER_HEIGHT = (headerEl)
    ? HEADER_HEIGHT_WHEN_STICKY
    : 0;

  const init = () => {
    addEventListeners();
  };


  /**
   * Bind All Event Listeners
   */
  const addEventListeners = () => {

    // Bind click behavior for all elements with data-attr
    $componentSelector.on('click', fromClickedElement);

  };



  /**
  * Scroll the window to the given selector
  * @param { String } cssSelector. ID or Class of the element to scroll to
  */
  const toElementSelector = (cssSelector, duration, isOffsetEnabled) => {
    duration = duration || 500;
    isOffsetEnabled = (isOffsetEnabled) ? true : false;

    let positionTop = $(cssSelector).eq(0).offset().top;
    const TITLE_HEIGHT = 120;

    // NOTE: There are components like the Header that can have "Position: Fixed".
    // It adds these components height as an offset to the scroll behavior
    if (isOffsetEnabled) {

      // Remove Header height from the scroll position
      positionTop -= HEADER_HEIGHT;
      positionTop -= TITLE_HEIGHT;

    }

    $htmlEl.animate({
      scrollTop: positionTop
    }, duration);

  };



  /**
  * Scroll the window to clicked element with a custom "data-attr"
  * @param { Object } event - Click event object
  */
  const fromClickedElement = (event) => {

    let varDataSet = event.currentTarget.dataset;
    let varScrollTo = null;
    let varScrollToOffsetEnabled = null;
    if (varDataSet === undefined || varDataSet === null) {// IE 10 did not recognize 'dataset' property.

      varScrollTo = $(this).attr('data-scroll-to');
      varScrollToOffsetEnabled = $(this).attr('data-scroll-to-offset-enabled');
    }
    else {
      varScrollTo = varDataSet.scrollTo;
      varScrollToOffsetEnabled = varDataSet.scrollToOffsetEnabled;
    }

    // Enable offset when The Header or other position fixed top components are in the current page
    let offsetEnabled = varScrollTo && varScrollToOffsetEnabled === 'true' ? true : false;

    // If the current element is <a> instead of a <button>
    event.preventDefault();

    let cssSelector = varScrollTo;
    toElementSelector(cssSelector, 500, offsetEnabled);

  };


  /**
  * If containing DOM element is found, Initialize and Expose public methods
  */
  if ($componentSelector.length > 0) {
    init();

    // Public Methods
    return {
      toElementSelector
    };
  }

}());
