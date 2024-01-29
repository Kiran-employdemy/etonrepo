//-----------------------------------
// List Component - Default View
//-----------------------------------
'use strict';

let App = window.App || {};
App.cardList = function () {

  const $componentSlides = $('.js-list-slider__slides');

  const init = () => {
    initCarousel();
    addEventListeners();
  };


  const addEventListeners = () => {
    if (window.matchMedia) {

      // max-width 768px
      let mqMobileOnly = window.matchMedia(App.global.constants.MEDIA_QUERIES.MOBILE);

      // min-width 992px
      let mqDesktopOnly = window.matchMedia(App.global.constants.MEDIA_QUERIES.DESKTOP);

      // Listener: MediaQuery Change
      mqMobileOnly.addListener(onBreakpointChange);
      mqDesktopOnly.addListener(onBreakpointChange);
    }
  };


  /**
  * Breakpoint Change Callback Function
  * @param {Object} event - MatchMedia Event Object
  */
  const onBreakpointChange = (event) => {

    // Trigger the "resize" event every time the breakpoint changes
    // in order to fix layout issues when switching between breakpoints (Mobile, Tablet, Desktop)
    $componentSlides.slick('resize');
  };


  /**
  * Count how many slides are in the Given Element / Slider-Container
  * @param {jQueryElement} $sliderContainerEl
  */
  const getSlidesLenght = ($sliderContainerEl) => {
    return $sliderContainerEl.find('.js-list-slider__slide-item').length;
  };


  /**
   * Initialize Slick Carousel
   */
  const initCarousel = () => {
    let totalSlides;
    let numSlides;
    let $item;
    let $itemParent;
    let settingsDesktop;

    App.global.utils.forEach($componentSlides, (index, item) => {
      $item = $(item);
      $itemParent = $item.closest('.js-list-slider');
      totalSlides = getSlidesLenght($item);
      numSlides = (totalSlides === 2)
        ? 2
        : 3;

      // NOTE: In Desktop Breakpoint, the slider should be enabled only
      // if there are more than 3 cards, else it will cause layout issues
      settingsDesktop = (totalSlides <= 3)
        ? 'unslick'
        : {
          slidesToShow: numSlides,
          slidesToScroll: numSlides
        };

      $item.not('.slick-initialized').slick({
        arrows: true,
        dots: true,
        dotsClass: 'eaton-slider__dots',
        prevArrow: $itemParent.find('.js-list-slider__prev-arrow'),
        nextArrow: $itemParent.find('.js-list-slider__next-arrow'),
        mobileFirst: true,
        responsive: [
          {
            breakpoint: 0,
            settings: {
              slidesToShow: 1,
              slidesToScroll: 1
            }
          },
          {
            // Slider is always disabled in Tablet Breakpoint
            breakpoint: 767,
            settings: 'unslick'
          },
          {
            breakpoint: 991,
            settings: settingsDesktop
          }
        ]
      });

    });

  };


  /**
   * If containing DOM element is found, Initialize and Expose public methods
   */
  if ($componentSlides.length > 0) {
    init();
  }

}();

$( document ).ready(function() {
  if (($('div').hasClass('module-product-detail-card__replacement') === true) && ($('div').hasClass('module-product-detail-card__badge-discontinued') === true)) {
    $('.module-product-detail-card__actions').remove();
    $('.module-product-detail-card__price').remove();
  }

});
