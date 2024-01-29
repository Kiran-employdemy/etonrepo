//-----------------------------------
// Component M-1: Header
//-----------------------------------
'use strict';

let App = window.App || {};

App.header = (function() {

  // Variable Declarations
  const componentClass = $('.eaton-header');
  const bodyEl = $('body');
  const windowEl = $(window);

  const primaryLinks = componentClass.find('.primary-navigation__child-list a');
  const megaMenu = componentClass.find('.mega-menu');
  const megaMenuSections = componentClass.find('.mega-menu__content');
  const megaMenuTitle = componentClass.find('.mega-menu-title__level1-link');
  const closeMegaMenuBtn = componentClass.find('.mega-menu-title__close-menu');
  const toggleMobileMenuBtn = $('.header-primary-nav__toggle-mobile-menu');
  const openSearchDropdownBtn = $('.header-primary-nav__open-search');
  const signOutBtn = $('#signOut,#signOutMob');

  // Media Breakpoint
  let mediumScreenWidth = App.global.constants.GRID.MD;

  // Check AEM Author Mode
  const isAEMAuthorMode = App.global.utils.isAEMAuthorMode();
  /**
  * Init
  */
  const init = () => {
    // If not in AEM Author Mode - initialize scripts
    if (!isAEMAuthorMode) {
      addEventListeners();

      // Subscribe - Cookie Acceptance
      $(document).on( App.global.constants.EVENTS.HEADER.COOKIE_SET, function() {
        updateHeaderLayoutMobile();
      });

      // Handle Resizing scenarios for the Header Layout
      let lazyResize = App.global.utils.throttle(updateHeaderLayoutMobile, 200);
      windowEl.on('resize', lazyResize);
    }
    // stack header for microsite on load
    updateHeaderStack(false);
  };

  /**
  * handle stacking/alignment in header for microsite with single/double logo authored
  * checkSticky-true if second logo also authored
  **/
  const updateHeaderStack = (checkSticky) => {
    if ($('.microsite').length > 0) {
      let rowWidth = $('.eaton-header').find('.row').eq(0).width();
      let headerContentWidth = $('.eaton-header').find('.header-primary-nav__logo_container').width() + $('.eaton-header').find('.header-primary-nav__container').width();

      if (checkSticky) {
      // calculation to be done individually for two logos only in IE
        if ((/MSIE \d|Trident.*rv:/.test(navigator.userAgent)) && $('.eaton-header').find('.header-primary-nav__logo--secondary').length > 0) {
          headerContentWidth = $('.eaton-header').find('.header-primary-nav__logo').width() + $('.eaton-header').find('.header-primary-nav__logo--secondary').width() + $('.eaton-header').find('.header-primary-nav__container').width();

        }
        if (headerContentWidth - rowWidth > 45 ) {
          $('.eaton-header').find('.header-primary-nav').addClass('header-sticky');
        }
      }
      else {
        if (headerContentWidth - rowWidth > 0 ) {
          $('.eaton-header').find('.header-primary-nav').addClass('header-stacked');
        }
      }
    }
  };

  /**
  * Handle height updation of Header Modules / components
  * Includes Top offset - Mega Menu, Links container, Search
  */
  const updateHeaderLayoutMobile = () => {
    const headerEl = $('.eaton-header');
    const headerHeight = headerEl.height() + 'px';
    // visible primary nav height calculation wrt visible screen height
    const visiblePrimaryNavHeight = (window.innerHeight - $('.header-utility-nav').height() - headerEl.height()) + 'px';

    if (windowEl.outerWidth() < mediumScreenWidth) {
      headerEl.find('.mega-menu, .primary-navigation, .header-search').css('top', headerHeight);
      // set max height of primary nav based on visible portion to get scrollable menu
      headerEl.find('.primary-navigation').css('max-height', visiblePrimaryNavHeight);

    } else {
      // Reset the top offset for the elements - Desktop
      headerEl.find('.mega-menu, .primary-navigation, .header-search').css('top', '');
      // Reset max height of primary nav- Desktop
      headerEl.find('.primary-navigation').css('max-height', '');
    }
  };

  /**
  * Handle Page Scroll - Sticky Navigation Behaviors
  */
  const handleScroll = (event) => {

    const scrollTop = windowEl.scrollTop();
    // const utilityNavOffset = $('.header-utility-nav').offset().top;
    const utilityNavHeight = $('.header-utility-nav').outerHeight();
    let isMegaMenuOpen = bodyEl.hasClass('level-2-open');
    const FIXED_HEADER_HEIGHT = 81; // Height of the sticky navigation - header
    const FIXED_HEADER_OFFSET = FIXED_HEADER_HEIGHT + utilityNavHeight;

    if ( scrollTop ) {
      bodyEl.css({ paddingTop: FIXED_HEADER_OFFSET + 'px' });
      bodyEl.addClass('header-fixed');

      // stack header for microsite when fixed/sticky
      updateHeaderStack(true);
      // If Mega Menu is Open in DESKTOP breakpoint
      // EATON-793 Mega Menu - Scroll Bar displays in mega menu when content exceeds a certain height
      if (isMegaMenuOpen
        && windowEl.outerWidth() >= mediumScreenWidth
        && scrollTop > (megaMenu.outerHeight() + utilityNavHeight)
        && scrollTop > (megaMenu.outerHeight())
      ) {

        // Close the Mega Menu when the page has scrolled beyond the mega-menu component
        closeMegaMenu(event);
      }

    } else {
      bodyEl.css({ paddingTop: '' });
      bodyEl.removeClass('header-fixed');
    }
  };

  /**
  * Open Mega Menu - Behaviors when click Primary Links
  */
  const openMegaMenu = (event) => {

    let activeCategory = '';

    event.preventDefault ? event.preventDefault() : event.returnValue = false;

    // Close Search if open
    closeSearch(event);
    updateHeaderLayoutMobile();
    // Publish - Mega Menu Open
    $(document).trigger( App.global.constants.EVENTS.HEADER.MEGAMENU_OPEN);

    // Highlight only the active Link
    primaryLinks.removeClass('active');
    primaryLinks.attr('aria-expanded', false);

    $(event.currentTarget).addClass('active');
    $(event.currentTarget).attr('aria-expanded', true);
    activeCategory = $(event.currentTarget).attr('data-menu-category');

    if (isMegaMenuSectionEmpty(activeCategory)) {
      closeActiveMegaMenuSections();
      window.open(event.currentTarget.dataset.linkValue, '_self');
    } else {

      bodyEl.addClass('nav-open level-2-open nav-is-animating');

      // Highlight the active mega-menu section
      megaMenu.find(`[data-target="${ activeCategory }"]`)
      .addClass('mega-menu__content--active')
      .siblings().removeClass('mega-menu__content--active');
      megaMenu.find(`[data-target="${ activeCategory }"]`).find('a').eq(0).focus();
      windowEl.scrollTop(0,0);

    }

  };

  /**
  * Close Mega Menu - Behaviors when click close Btn
  */
  const closeMegaMenu = (event) => {

    event.preventDefault ? event.preventDefault() : event.returnValue = false;

    primaryLinks.removeClass('active');
    megaMenuSections.removeClass('mega-menu__content--active');
    bodyEl.removeClass('nav-open level-2-open nav-is-animating');
  };

  /**
  * Mobile Menu - Open Close Interactions
  */
  const mobileMenuInteractions = (event) => {

    event.preventDefault ? event.preventDefault() : event.returnValue = false;

    // Close Search if open
    closeSearch(event);

    updateHeaderLayoutMobile();

    if (bodyEl.hasClass('nav-open')) {

      // Check if Level 2 - open/close
      if (bodyEl.hasClass('level-2-open')) {
        // Close Level-2
        bodyEl.removeClass('nav-open');
        bodyEl.removeClass('level-2-open nav-is-animating');

      } else {
        bodyEl.removeClass('nav-open');
      }
    } else {
      bodyEl.addClass('nav-open');
      primaryLinks.eq(0).focus();
    }
  };

  /**
  * Handle Click behaviors - for Title - Desktop & Mobile
  */
  const handleTitleClick = (event) => {
    const activeLink = primaryLinks.filter('.active');
    if (windowEl.width() < mediumScreenWidth) {
      event.preventDefault ? event.preventDefault() : event.returnValue = false;

      bodyEl.removeClass('level-2-open');
      activeLink.focus();
    }
  };

  /**
  * Handle Click behaviors - for Search - Desktop & Mobile
  */
  const handleSearch = (event) => {

    event.preventDefault ? event.preventDefault() : event.returnValue = false;
    closeMegaMenu(event);
    updateHeaderLayoutMobile();

    bodyEl.toggleClass('search-open');

    if ( $(event.currentTarget).attr('aria-expanded') === 'true' ) {
      $(event.currentTarget).attr('aria-expanded', false);
    } else {
      $(event.currentTarget).attr('aria-expanded', true);

      // EATON-811: Focus the search field (<textarea>) as soon as the search opens
      handleFocusInputSearch();
    }

    // Reset search inputBox
    bodyEl.find('.eaton-search input').val('');
    bodyEl.find('.eaton-search--default__result-list').html('');
    bodyEl.find('.eaton-search--default__results').removeClass('active');
  };

  /**
  * Handle Click behaviors - for Search - Desktop & Mobile
  */
  const closeSearch = (event) => {

    event.preventDefault ? event.preventDefault() : event.returnValue = false;
    bodyEl.removeClass('search-open');
    // set aria expanded to false when search Closed
    openSearchDropdownBtn.attr('aria-expanded',false);
  };



  /**
  * EATON-811: Focus the Search field once the CSS Transition is completed
  */
  const handleFocusInputSearch = () => {
    componentClass.find('.header-search').on('transitionend', (event) => {
      componentClass.find('.eaton-search--default__form-input').val('');
      componentClass.find('.eaton-search--default__form-input').focus();
      componentClass.find('.header-search').off('transitionend');
    });
  };


 /**
  * Handle signout sunction
  */
  const handleSignOut = () => {
    $('.dropdown').css('pointer-events', 'none');
    // Clears What's New content from Local Storage
    // eslint-disable-next-line no-undef
    new WhatsNew().unsetWhatsNew();
    let signOutRedirect = getSignOutRedirectParameter();
    $(location).attr('href','/eaton/signout?redirect=' + signOutRedirect);
  };

  /**
  * Breakpoint Change Callback Function
  * @param { Object} event - MatchMedia Event Object
  */
  const onBreakpointChange = (event) => {
    // Close Menu & Search
    closeMegaMenu(event);
    closeSearch(event);
  };

  /**
   * Bind All Event Listeners
   */
  const addEventListeners = () => {

    let mqDesktop = null;

    // Handle Scroll - Sticky Navigation Behaviors
    const lazyScroll = App.global.utils.throttle(handleScroll, 15);
    windowEl.on('scroll', lazyScroll);

    // Handle Mega Menu Behaviors - Open Mega-Menu
    primaryLinks.on('click', openMegaMenu);

    // Handle Mega Menu Behaviors - Close Mega-Menu (Desktop)
    closeMegaMenuBtn.on('click', closeMegaMenu);

    // Handle Mobile Menu Behaviors - Open/Close
    toggleMobileMenuBtn.on('click', mobileMenuInteractions);

    // Handle click on Mega Menu Title - across breakpoints
    megaMenuTitle.on('click', handleTitleClick);

    // Handle click on Search Icon
    openSearchDropdownBtn.on('click', handleSearch);

    signOutBtn.on('click', handleSignOut);




    // JavaScript MediaQueries
    //--------------
    if (window.matchMedia) {

      // min-width 992px
      mqDesktop = window.matchMedia(App.global.constants.MEDIA_QUERIES.DESKTOP);

      // EventListener that gets fired when the Breakpoint changes from Mobile to Desktop / Desktop to Mobile
      mqDesktop.addListener(onBreakpointChange);
    }
  };

  /**
  * If containing DOM element is found, Initialize and Expose public methods
  */
  if (componentClass.length > 0) {
    init();
  }

});

document.addEventListener('DOMContentLoaded', () => {
  App.header();
});

/*eslint-disable*/
function redirectToCountrySelector() {
  let countryCookie = $.cookie('etn_country_selector_country');
  if (window.location.href.indexOf('login.html') > 0 && typeof countryCookie === 'undefined') {
    window.location = '/country.html';
  } else {
    if (window.location.href.indexOf('login.html') > 0 && $.cookie('etn_country_selector_country') === undefined) {
      window.location = '/country.html';
    }
  }

  // For Eaton.com
  if (window.location.href.indexOf('eaton') && typeof countryCookie === 'undefined') {
    window.location = '/country.html';
  } else {
    if (window.location.href.indexOf('eaton') && $.cookie('etn_country_selector_country') === undefined) {
      window.location = '/country.html';
    }
  }
}
/*eslint-enable*/

/**
* checks if mega menu section doesn't have navigation links beneath it
* excludes primary navigation link
*/
const isMegaMenuSectionEmpty = (menuCategory) => {
  return document.querySelectorAll('[data-target="' + menuCategory + '"] a.links-list__link').length === 0;
};

/**
* Closes any active mega menu overlays
*/
const closeActiveMegaMenuSections = () => {
  let activeMenus = document.getElementsByClassName('mega-menu__content--active');
  for (let i = 0; i < activeMenus.length; i++) {
    activeMenus[i].classList.remove('mega-menu__content--active');
  }
};

/**
 * Determine the redirect url used after user signs out
 * @returns {string}
 */
const getSignOutRedirectParameter = () => {

  let currentPath = window.location.pathname;
  const securePathPart = '/secure/';

  if (currentPath.includes(securePathPart)) {
    return currentPath.split(securePathPart)[0] + '.html';
  } else {
    return currentPath;
  }

};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = { isMegaMenuSectionEmpty, closeActiveMegaMenuSections, getSignOutRedirectParameter };
}
