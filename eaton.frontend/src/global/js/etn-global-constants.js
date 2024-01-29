//-----------------------------------
// Eaton: Constants
//-----------------------------------
'use strict';

let App = App || {};
App.global = App.global || {};

App.global.constants = (function() {

  // Private Methods go here...


  // Public Constants
  return {

    MEDIA_QUERIES: {
      MOBILE: '(max-width: 767px)',
      TABLET: '(min-width: 768px) and (max-width: 991px)',
      MOBILE_AND_TABLET: '(max-width: 991px)',
      DESKTOP: '(min-width: 992px)',
      DESKTOP_LG: '(min-width: 1200px)'
    },

    // Breakpoints
    GRID: {
      // XS: 0,    // (0px to 767px)
      SM: 768,  // pixels
      MD: 992,  // pixels
      LG: 1200  // pixels
    },

   // Events
    EVENTS: {
      HEADER: {
        COOKIE_SET: 'eaton.cookie.set',
        MEGAMENU_OPEN: 'eaton.megamenu.open'
      }
    }

  };
})();