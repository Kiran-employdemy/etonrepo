//-----------------------------------
// List Component - Content Tile SLider
//-----------------------------------
'use strict';

let App = window.App || {};
App.relatedProducts = function () {

  const $carousel = $('.module-related-content__slides');

  const init = () => {
    initCarousel();
    charLimit();
  };

  const charLimit = () => {
    for (let i = 0; i < $('.related-family').length; i++) {
      let desc = $('#related-family-' + i).text().trim();
      if (desc.startsWith('<p>')) {
        desc = $(desc).text();
      }
      $('#related-family-' + i).html(desc);
    }
  };

  /**
   * Initialize Slick Carousel
   */
  const initCarousel = () => {
    for (let i = 0; i < $carousel.length; i++) {

      $carousel.eq(i).addClass('module-related-content__slides--' + i);

      $carousel.eq(i).slick({
        slidesToShow: 3,
        slidesToScroll: 3,
        dots: true,
        dotsClass: 'eaton-slider__dots',
        prevArrow: $('.module-related-products__prev-arrow'),
        nextArrow: $('.module-related-products__next-arrow'),
        responsive: [
          {
            breakpoint: 991,
            settings: {
              slidesToShow: 3,
              slidesToScroll: 3
            }
          },
          {
            breakpoint: 768,
            settings: {
              slidesToShow: 1,
              slidesToScroll: 1
            }
          }
        ]
      });
    }

    let sliderTiles = document.querySelectorAll('.module-related-products__info-text');
    let heightArray = [];
    // eslint-disable-next-line no-eq-null, eqeqeq
    if (sliderTiles.length != 0) {
      sliderTiles.forEach(element => {
        heightArray.push(element.offsetHeight);
      });

      let maxHeight = Math.max(...heightArray);

      sliderTiles.forEach(element => {
        element.style.height = maxHeight + 'px';
      });
    }

  };


  /**
  * If containing DOM element is found, Initialize and Expose public methods
  */
  if ($carousel.length > 0) {
    init();
  }

}();
