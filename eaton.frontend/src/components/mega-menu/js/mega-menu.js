$(document).ready(function() {

  $('.primary-navigation__items a').click(function() {

    const lengthOfCol = $('.mega-menu__content--active').find('.mega-menu-custom-col').find('.mega-menu-custom-col-2').find('.links-list__col').length;
    const lengthOfCol2 = $('.mega-menu__content--active').find('.mega-menu-custom-col').find('.mega-menu-custom-col-2').length;
    const customFlex = lengthOfCol * 25 + '%';
    const customFlex2 = 100 / lengthOfCol2 + '%';

    $('.mega-menu__content--active').find('.mega-menu-custom-col').css('flex-basis',customFlex);
    $('.mega-menu__content--active').find('.mega-menu-custom-col').css('-webkit-flex-basis',customFlex);
    $('.mega-menu__content--active').find('.mega-menu-custom-col').css('-ms-flex-preferred-size',customFlex);
    $('.mega-menu__content--active').find('.mega-menu-custom-col').find('.mega-menu-custom-col-2').css('flex-basis',customFlex2);
    $('.mega-menu__content--active').find('.mega-menu-custom-col').find('.mega-menu-custom-col-2').css('-webkit-flex-basis',customFlex2);
    $('.mega-menu__content--active').find('.mega-menu-custom-col').find('.mega-menu-custom-col-2').css('-ms-flex-preferred-size',customFlex2);
    $('.mega-menu__content--active').find('.mega-menu-custom-col').find('.mega-menu-custom-col-2').last().addClass('mega-menu__col--flex');

  });

  $('.menuoverlay.mega-menu').removeClass('mega-menu');
});

/* Remove index from card */

$(document).ready(function() {
  $('.card-list__list li').removeAttr('tabindex');
  $('.card-list__list li a').removeAttr('tabindex');
});
