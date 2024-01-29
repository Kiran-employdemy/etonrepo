//-----------------------------------
// Component M-40: Product Tabs shared module
//-----------------------------------
'use strict';

let App = window.App || {};
App.productTabs = (function() {

  // Cached DOM Elements
  //--------------
  const $componentClass = $('.product-tabs__component');
  const $headerEl = $('.eaton-header');
  const $headerUtilityNav = $('.header-utility-nav');
  const $window = $(window);
  const $tabsMainLinks = $componentClass.find('.product-tabs__main-links');
  const $tabsDescription = $componentClass.find('.product-tabs__description');
  const $tabsTitles = $componentClass.find('.product-tabs__tab-title');
  const $tabsDropdown = $componentClass.find('.dropdown');          // Bootstrap Dropdown Container
  const $tabsDropdownMenu = $componentClass.find('.dropdown-menu'); // Bootstrap Dropdown Element

  // Handle "Cookie Acceptance" Component / Behaviors
  //--------------
  const $cookieAcceptance = $('#onetrust-banner-sdk');
  let isCookieAcceptanceVisibile = false; // Default values
  let cookieAcceptanceHeight = 0; // Default values

  // CSS Selectors
  const componentFixedClass = 'product-tabs--fixed';

  // Utility Detect AEM's Author / Edit Mode
 // const isAEMAuthorMode = App.global.utils.isAEMAuthorMode();

  // Constants - Keyboard Codes
  const KEYCODES = {
    ENTER: 13
  };

  /**
  * Initialize
  */
  const init = () => {
    addEventListeners();
  };

  /**
   * Bind All Event Listeners
   */
  const addEventListeners = () => {

    // Listener that is fired when the cookie acceptance component GETS CLOSED
    $(document).on(App.global.constants.EVENTS.HEADER.COOKIE_SET, () => {
      isCookieAcceptanceVisibile = false;
      cookieAcceptanceHeight = 0;
    });

    // Handle Accessibilty - "Graphic Toggle" View - Keyboard Navigation
    //--------------
    if ($componentClass.hasClass('product-tabs--view-toggle')) {
      $tabsTitles.on('keyup', (event) => {

        // Detect if the pressed key is the "Enter" key
        if (event.which === KEYCODES.ENTER) {
          event.preventDefault();

          // Set the Radio button to be "Checked" in order to make the tab Active/The content visible.
          $('#' + event.target.htmlFor).click();
        }
      });
    }


    // Handle Scroll/Resize
    //--------------
    const lazyResize = App.global.utils.throttle(onWindowResize, 200);
    $window.on('resize', lazyResize);

    const onScroll = (event) => {
      const scrollTop = $window.scrollTop();
      const isMobile = $window.innerWidth() < App.global.constants.GRID.MD;
      const headerHeight = $headerEl.outerHeight();
      const tabsDescriptionHeight = $tabsDescription.outerHeight();
      const tabsMainLinksHeight = $tabsMainLinks.outerHeight();
      const utilityNavOffset = $headerUtilityNav.length > 0 ? $headerUtilityNav.offset().top : 0;
      const utilityNavHeight = $headerUtilityNav.outerHeight();
      const isHeaderFixed = scrollTop > (utilityNavOffset + utilityNavHeight);
      let topOffsetCookieAcceptance = null;

      // [EATON-780] FIX - Product family and SKU Tabs disappear when user scrolls on an Iphone (vs Scroll on Desktop)
      let componentOffset = (isMobile)
        ? tabsDescriptionHeight
        : tabsMainLinksHeight;

      // If "Cookie Acceptance" component was hidden on page load but it became visible
      // (After testing if the LocalStorage item exists)
      if (!isCookieAcceptanceVisibile && $cookieAcceptance.is(':visible')) {
        isCookieAcceptanceVisibile = true;
        cookieAcceptanceHeight = $cookieAcceptance.outerHeight();
      }

      const isTop = (scrollTop === 0);
      const scrollOffset = headerHeight + componentOffset + cookieAcceptanceHeight;
      let shouldBeFixed = (isHeaderFixed && !isTop);
      shouldBeFixed = isMobile ? (scrollTop > scrollOffset) : shouldBeFixed;
      // console.log('shouldBeFixed:', shouldBeFixed);

      // Handle Header Sticky Behavior in DESKTOP Breakpoint when the "Cookie Acceptance" Component is visible.
      // NOTE: For Mobile breakpoins, If the Header & The Product Tabs component are both in the same page,
      // The Header will have the "Sticky/Fixed" behavior disabled and only the Product Tabs component can be "Sticky".
      if (!isMobile && shouldBeFixed && cookieAcceptanceHeight > 0) {
        topOffsetCookieAcceptance = cookieAcceptanceHeight - 20;
        $componentClass.css({ marginTop: topOffsetCookieAcceptance + 'px' });
      } else {
        $componentClass.css({ marginTop: '' });
      }

      // EATON-619: Dropdown should close when user has it open then scrolls down.
      closeDropdown();

      $componentClass.toggleClass(componentFixedClass, shouldBeFixed);

    };

    const lazyScroll = App.global.utils.throttle(onScroll, 15);
    $window.on('scroll', lazyScroll);

  };


  /**
  * It closes "How to Buy" Dropdown
  */
  const closeDropdown = () => {
    if ( $tabsDropdown.hasClass('open') ) {

      // Call Bootstrap's Method to toggle the state of the dropdown
      $tabsDropdownMenu.dropdown('toggle');
    }
  };


  /**
  * OnWindowResize
  * @param {Object} event - jQuery's Window-Resize event
  */
  const onWindowResize = () => {

    // It recalculates the height of the CookieAcceptance component when the window is resized.
    if (isCookieAcceptanceVisibile) {
      cookieAcceptanceHeight = $cookieAcceptance.outerHeight();
    }

  };


  /**
  * If containing DOM element is found, Initialize and Expose public methods
  */
  if ($componentClass.length > 0) {
    init();
  }

}());
