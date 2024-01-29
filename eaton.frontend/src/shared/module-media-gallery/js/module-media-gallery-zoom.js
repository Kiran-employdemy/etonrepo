//-----------------------------------
// M-48: Module Media Gallery
// ZoomBehaviors for Desktop and Mobile/Tablet Breakpoints
//-----------------------------------
'use strict';

let App = window.App || {};
App.mediaGalleryZoom = function () {

  let zoom = {};

  const componentClass = '.module-media-gallery';
  const $componentEl = $(componentClass);

  // Placeholder variable for Multilanguage strings
  let i18nStrings = {};

  // Cached DOM Elements
  //--------------
  const $bodyEl = $('body');
  const $slidesCarousel = $componentEl.find('.module-media-gallery__slide-list');


  /**
  * Initialize Media Gallery Zoom Behavior
  */
  const init = () => {

    // Get i18n strings from a DOM-Element
    i18nStrings = App.global.utils.loadI18NStrings($componentEl);

    initilizeZoom();
    sliderAndZoomConflicts();

  };


  /**
  * Initialize Zoom Behavior
  */
  const initilizeZoom = () => {

    // Zoom Behavior - defaults/config
    zoom = {
      eventDesktop: 'click',
      // eventMobile: 'overlay', // Is not being used
      scaleMobile: 2, // The image will be scaled 200%.
      scaleDesktop: 1, // This value is multiplied against the full size of the zoomed image. The default value is 1, meaing the zoomed image should be at 100% of its natural width and height.

      state: {
        isDragging: null,
        isLoading: null
      },

      $overlayEl: null,
      $overlayImageEl: null,
      $overlayCloseEl: null,
      $imagesEl: null,

      cssClasses: {
        zoomOverlayOpen: 'zoom-overlay-open',
        zoomInlineOpen: 'zoom-inline-open'
      },

      // Keyboard Codes (event.which)
      keyCodes: {
        ESC: 27
      },

      templates: {
        zoomInline: '<div class="zoom-inline"></div>',
        zoomOverlay: `<div class="zoom-overlay">
          <button class="zoom-overlay__close button--reset">
            <span class="sr-only">${ i18nStrings.closeOverlay }</span>
            <i class="icon icon-close" aria-hidden="true"></i>
          </button>
          <div class="zoom-overlay__image"></div>
        </div>`
      }

    };


    // Save DOM Elements used By the Zoom Behavior
    //--------------
    zoom.$overlayEl = $(zoom.templates.zoomOverlay);
    zoom.$overlayImageEl = zoom.$overlayEl.find('.zoom-overlay__image');
    zoom.$overlayCloseEl = zoom.$overlayEl.find('.zoom-overlay__close');
    zoom.$previewImagesEl = $slidesCarousel.find('[data-zoom-url]');

    // Append Zoom Overlay to the DOM
    $bodyEl.append(zoom.$overlayEl);

    // Bind Event Listeners
    addEventListeners();

  };


  /**
  * Add zoom funtionallity for images on the carousel
  * @param { String } imageSrc - ImageURL that will be displayed in the overlay
  */
  const addZoomDesktop = () => {

    // Documentation Zoom Library: http://www.jacklmoore.com/zoom/
    //--------------
    App.global.utils.forEach(zoom.$previewImagesEl, (index, item) => {

      const $currentItem = $(item);

      // NOTE: From http://www.jacklmoore.com/zoom/
      // To use zoom with img elements, they will need to be wrapped with another element.
      // It is impossible to read some layout related CSS styles from JavaScript (percent-based width and height, margins set to auto, etc.)
      // so the safe thing to do is to defer this change to individual site owners.
      if (!$currentItem.parent().hasClass('zoom-inline')) {
        $currentItem.wrap(zoom.templates.zoomInline);
      }

      $currentItem
      .parent()
      .zoom({
        on: zoom.eventDesktop,
        magnify: zoom.scaleDesktop,
        url: item.dataset === undefined ? item.getAttribute('data-zoomUrl') : item.dataset.zoomUrl,
        touch: false, // Disable touch events in order to avoid conflicts with the swipe behavior of the slider

        // A function to be called when the image has zoomed in. Inside the function, `this` references the image element.
        onZoomIn() {
          $(this).closest(componentClass).addClass(zoom.cssClasses.zoomInlineOpen);
        },

        // A function to be called when the image has zoomed out. Inside the function, `this` references the image element.
        onZoomOut() {
          $(this).closest(componentClass).removeClass(zoom.cssClasses.zoomInlineOpen);
        }

      });

    });
  };


  /**
  * Destroy Zoom Behavior used in a Desktop Breakpoint
  * Documentation Zoom Library: http://www.jacklmoore.com/zoom/
  */
  const destroyZoomDesktop = () => {
    $componentEl.find('.zoom-inline').trigger('zoom.destroy');
  };


  /**
  * Show Modal/Overlay with the given Image URL
  * @param { String } imageSrc - ImageURL that will be displayed in the overlay
  */
  const openOverlayMobile = (imageSrc) => {

    // Update Background image in the overlay
    const style = `background-image: url("${ imageSrc }")`;
    zoom.$overlayImageEl.attr('style', style);

    // Prevent scrolling in the page
    $bodyEl.addClass(zoom.cssClasses.zoomOverlayOpen);

    // It makes zoom-overlay visible
    zoom.$overlayEl.fadeIn(() => {

      // Focus the Close Overlay button as soon as the overlay is visible
      zoom.$overlayCloseEl.focus();
    });

    // Add Listener that Closes the Overlay on Key Press (ESC Key)
    $bodyEl.on('keydown.zoom', closeOverlayMobile);

  };


  /**
  * Function/Method description
  * @param { Object } event - the event object
  */
  const closeOverlayMobile = (event) => {

    // Close Overlay on Key Press. If the pressed key is not the ESC Key, Ignore
    if (event && event.type === 'keydown' && event.which !== zoom.keyCodes.ESC ) { return; }

    // Hide Overlay and remove Background Image
    zoom.$overlayEl.fadeOut(() => {
      zoom.$overlayImageEl.removeAttr('style');
      $bodyEl.removeClass(zoom.cssClasses.zoomOverlayOpen);
    });

    // Remove event Listeners to close the Overlay on Key Press
    $bodyEl.off('keydown.zoom', closeOverlayMobile);

  };


  /**
  * Handle Zoom Mobile
  * @param { Object } event - the event object
  */
  const handleImageEvents = (event) => {

    let imageEl = null;

    // If Mobile/Tablet Breakpoint
    if (window.innerWidth < App.global.constants.GRID.MD) {

      // Open Overlay for Mobile
      imageEl = event.currentTarget.querySelector('img');
      openOverlayMobile(imageEl.src);
    }
  };


  /**
  * Handle Zoom Behavior inside the Overlay
  * @param { Object } event - the event object
  */
  const handleZoomOverlay = (event) => {

    let zoomedImage = event.currentTarget;


    // TouchStart / MouseDown
    //--------------
    if (event.type === 'touchstart' || event.type === 'mousedown') {
      // console.log('zoomStart', event.type);
      zoomedImage.style.backgroundSize = `${ zoom.scaleMobile * 100 }%`;
      zoom.state.isDragging = true;
    }


    // TouchMove / MouseMove
    //--------------
    else if (zoom.state.isDragging && (event.type === 'touchmove' || event.type === 'mousemove')) {

      // console.log('zoomMove', event.type);
      event.preventDefault();

      // getBoundingClientReact gives us various information about the position of the element.
      let dimentions = zoomedImage.getBoundingClientRect();
      let eventX = event.clientX || (event.touches && event.touches[0].clientX);
      let eventY = event.clientY || (event.touches && event.touches[0].clientY);

      // Calculate the position of the cursor inside the element (in pixels).
      let x = eventX - dimentions.left;
      let y = eventY - dimentions.top;

      // Calculate the position of the cursor as a percentage of the total size of the element.
      let xPercentage = Math.round(100 / (dimentions.width / x));
      let yPercentage = Math.round(100 / (dimentions.height / y));

      // Update the background position of the image.
      zoomedImage.style.backgroundPosition = `${ xPercentage }% ${ yPercentage }%`;

    }

    // TouchEnd / MouseUp
    //--------------
    else if (event.type === 'touchend' || event.type === 'mouseup') {
      // console.log('zoomEnd', event.type);
      zoomedImage.style.backgroundSize = 'contain';
      zoomedImage.style.backgroundPosition = 'center';
      zoom.state.isDragging = false;
    }
  };


  /**
  * AddEventListeners used by the Zoom behavior
  */
  const addEventListeners = () => {

    // JavaScript MediaQueries
    let mqDesktop = window.matchMedia(App.global.constants.MEDIA_QUERIES.DESKTOP);

    // Update Zoom behaviors when the breakpoint changes from Mobile/Tablet to Desktop
    mqDesktop.addListener(onBreakpointChange);

    // At Runtime (When is loaded for the first time) Execute the necessary behaviors for the current breakpoint
    onBreakpointChange(mqDesktop);

    // Zoom Desktop - When the user clicks the zoom-icon button,
    // trigger the click event in the image so the Zoom behavior gets enabled.
    $componentEl.find('.module-media-gallery__zoom--desktop').on('click', (event) => {
      setTimeout(() => {
        $(event.target).closest('.module-media-gallery').find('.slick-current .zoom-inline').click();
      }, 10);
    });

    // Zoom Mobile - Bind Zoom Behavior for Mobile Breakpoints
    $componentEl.find('.module-media-gallery__image').on('click', handleImageEvents);

    // Zoom Mobile - Close Overlay
    zoom.$overlayCloseEl.on('click', closeOverlayMobile);

    // Zoom Mobile - Overlay - Handle Image Zoom
    zoom.$overlayImageEl.on('touchstart touchmove touchend mousedown mousemove mouseup', handleZoomOverlay);

  };


  /**
  * Breakpoint Change Callback Function
  * @param {Object} event - MatchMedia Event Object
  */
  const onBreakpointChange = (event) => {

    // Desktop BP
    //--------------
    if (event.matches) {

      // Close the overlay when switching from Mobile/Tablet to Desktop Breakpoint
      closeOverlayMobile();

      // Re-initialize Desktop Zoom Behavior
      addZoomDesktop();

    }

    // Mobile/Tablet BP
    //--------------
    else {
      destroyZoomDesktop();
    }

  };



  /**
  * FIX: EATON-686 Defect.
  *
  * In a Desktob breakpoint if the zoom behavior is enabled and the user drags/swipes
  * the slider in order to move to another slide, the Zoom behavior needs to be restored / Disabled
  * This avoids conflicts with the swipe event and click/tap event.
  */
  const sliderAndZoomConflicts = () => {

    // Docs Slider Library:
    // http://kenwheeler.github.io/slick/
    $slidesCarousel.on('beforeChange', (event, slick, currentSlide, nextSlide) => {
      let $currentSlider = null;

      if (window.innerWidth >= App.global.constants.GRID.MD) {
        $currentSlider = slick.$slider.closest(componentClass);

        // Set the opacity of the ZoomedImage to 0
        // in order to reset/disable the zoom-behavior when the Slider is animating/updating the active slide
        $currentSlider.find('.zoomImg').css({ opacity: 0 });
      }
    });


    $slidesCarousel.on('afterChange', (event, slick, currentSlide) => {
      let $currentSlider = null;

      if (window.innerWidth >= App.global.constants.GRID.MD) {
        $currentSlider = slick.$slider.closest(componentClass);
        $currentSlider.removeClass(zoom.cssClasses.zoomInlineOpen);
      }
    });
  };


  /**
  * If containing DOM element is found, Initialize and Expose public methods
  */
  if ($componentEl.length > 0) {
    init();
  }

}();
