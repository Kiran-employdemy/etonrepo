/**
 *
 *
 *
 * - THIS IS AN AUTOGENERATED FILE. DO NOT EDIT THIS FILE DIRECTLY -
 * - Generated by Gulp (gulp-babel).
 *
 *
 *
 *
 */


'use strict';

$(document).ready(function () {
  if (window.location.href.indexOf('skuPage') > 0) {

    if ($('.skuSpinnerImage').data('src') !== undefined) {
      if ($('.skuSpinnerImage').data('src').includes('eaton.sirv')) {
        $('.staticSkuImage').addClass('hide');
      }
    } else {
      $('.skuSpinnerImage').addClass('hide');
    }
  }
});

$(document).ready(function () {
  jQuery(function ($) {
    var speed = 1000;

    $('a[href*="#"]').filter(function (i, a) {
      return a.getAttribute('href').startsWith('#') || a.href.startsWith(location.href + '#');
    }).unbind('click.smoothScroll').bind('click.smoothScroll', function (event) {
      var targetId = event.currentTarget.getAttribute('href').split('#')[1];
      var targetElement = document.getElementById(targetId);

      if (targetElement) {
        event.preventDefault();
        $('html, body').animate({ scrollTop: $(targetElement).offset().top - 200 }, speed);
      }
    });
  });
});