//-----------------------------------
// Component : App.CookieAcceptance
//-----------------------------------
'use strict';

let App = window.App || {};

App.cookieAcceptance = (function() {

  // Variable Declarations
  const componentClass = '#onetrust-banner-sdk';
  const $componentElement = $(componentClass);
  const $bodyEl = $('body');
  const cssOpenClass = 'cookie-acceptance-open';

  // Set up Eloqua reference to let it know if user opts in
  let _elqQ = _elqQ || [];

  /**
  * Init
  */
  const init = () => {
    // If not in AEM Author Mode & component exists on page - initialize scripts
    addEventListeners();
  };


  const readStatus = () => {
    if ($.cookie('eatoncookies') === undefined) {
      $componentElement.show();
      $bodyEl.addClass(cssOpenClass);
    } else {
      $componentElement.remove();
      $bodyEl.removeClass(cssOpenClass);
    }
  };

  /**
  * @property {function} publishCookieSet - fire event to signal a cookie choice has been made
  */
  const publishCookieSet = () => {
    $componentElement.remove();
    $bodyEl.removeClass(cssOpenClass);
    $(document).trigger( App.global.constants.EVENTS.HEADER.COOKIE_SET);
  };

  /**
   * Bind All Event Listeners
   */
  const addEventListeners = () => {

    readStatus();
    const acceptcookie = $('#onetrust-accept-btn-handler');
    const allowbtn = $('#accept-recommended-btn-handler');
    const confirmcookie = $('.save-preference-btn-handler');
    $(acceptcookie , allowbtn , confirmcookie).on('click',function(e) {
      e.preventDefault();
      $.cookie('eatoncookies', 'true');
      _elqQ.push(['elqOptIn']);
      publishCookieSet();
    });
  };


  /**
  * If containing DOM element is found, Initialize and Expose public methods
  */
  if ($componentElement.length > 0) {
    init();
  }

}());
