//-----------------------------------
// TTIL: Upsell Products
//-----------------------------------
'use strict';

let App = window.App || {};
App.upSellProducts = function () {

  const $carousel = $('.upsell-products__slides');

  const init = () => {
    initCarousel();
  };

  /**
   * Initialize Slick Carousel
   */
  const initCarousel = () => {

    $carousel.each(function (index) {

      $(this).slick({
        slidesToShow: 3,
        slidesToScroll: 3,
        dots: true,
        dotsClass: 'eaton-slider__dots',
        prevArrow: $('.upsell-products__prev-arrow')[index],
        nextArrow: $('.upsell-products__next-arrow')[index],
        responsive: [{
          breakpoint: 991,
          settings: {
            slidesToShow: 3,
            slidesToScroll: 3
          }
        }, {
          breakpoint: 768,
          settings: {
            slidesToShow: 1,
            slidesToScroll: 1
          }
        }]
      });
    });
  };
  /**
   * If containing DOM element is found, Initialize and Expose public methods
   */
  if ($carousel.length > 0) {
    init();
  }

}();

$(document).ready(function () {
  $('.grid-cross-btn').addClass('hides');
  if ( $('#gridcross li').length > 8 ) {
    $('.grid-cross-btn').removeClass('hides');
  }

  $('.grid-cross li').addClass('hides');
  $('.grid-cross li:nth-child(-n+8)').removeClass('hides');
  $('.grid-cross-btn a.less-text').addClass('hides');

  $('.grid-cross-btn a.more-text').click(function () {
    $('.grid-cross li').removeClass('hides');
    $(this).addClass('hides');
    $('.grid-cross-btn a.less-text').removeClass('hides');
    $('.grid-cross li').removeClass('hides');
  });

  $('.grid-cross-btn a.less-text').click(function () {
    $('.grid-cross li').addClass('hides');
    $('.grid-cross li:nth-child(-n+8)').removeClass('hides');
    $(this).addClass('hides');
    $('.grid-cross-btn a.more-text').removeClass('hides');
  });
});


$(document).ready(function () {
  $('.grid-upshell-btn').addClass('hides');
  if ( $('#gridupshell li').length > 8 ) {
    $('.grid-upshell-btn').removeClass('hides');
  }

  $('.grid-upshell li').addClass('hides');
  $('.grid-upshell li:nth-child(-n+8)').removeClass('hides');
  $('.grid-upshell-btn a.less-text').addClass('hides');

  $('.grid-upshell-btn a.more-text').click(function () {
    $('.grid-upshell li').removeClass('hides');
    $(this).addClass('hides');
    $('.grid-upshell-btn a.less-text').removeClass('hides');
    $('.grid-upshell li').removeClass('hides');
  });

  $('.grid-upshell-btn a.less-text').click(function () {
    $('.grid-upshell li').addClass('hides');
    $('.grid-upshell li:nth-child(-n+8)').removeClass('hides');
    $(this).addClass('hides');
    $('.grid-upshell-btn a.more-text').removeClass('hides');
  });
});



