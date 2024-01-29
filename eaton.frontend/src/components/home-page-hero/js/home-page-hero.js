//-----------------------------------
// Compnent: TTIL
//-----------------------------------
'use strict';

let App = window.App || {};
App.homePageHero = function () {

  const $componentEl = $('.home-page-hero__component');
  const $componentSlidesEl = $componentEl.find('.home-page-hero__slide-list');
  const $arrows = $componentEl.find('.home-page-hero__arrow');

  const init = () => {
    initCarousels();
  };


  /**
   * Initialize Bootstrap Carousel
   */
  const initCarousels = () => {
    $componentSlidesEl.slick({
      slidesToShow: 1,
      slidesToScroll: 1,
      autoplay: true,
      autoplaySpeed: 5000,
      dots: true,
      dotsClass: 'home-page-hero__dots',
      prevArrow: $componentEl.find('.home-page-hero__arrow--prev'),
      nextArrow: $componentEl.find('.home-page-hero__arrow--next')
    });

    $arrows.hover(() => {
      $componentSlidesEl.slick('slickPause');
    });
  };




  /**
   * If containing DOM element is found, Initialize and Expose public methods
   */
  if ($componentEl.length > 0) {
    init();
  }


}();
