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

/****
 * SKU details light developed for lite-sku component.
 */

$(document).ready(function () {
  $('.light-sku-default-view-container label.tab-label').on('click', function () {
    $('.light-sku-default-view-container label.tab-label').removeClass('tab-active');
    $(this).addClass('tab-active');
  });

  /** windows scroll to mark tab active-tab */
  $(window).on('scroll', function () {
    if ($('.sku-details-specifications').length > 0) {
      if ($(window).scrollTop() <= $('.sku-details-specifications').offset().top - 150) {
        $('.light-sku-default-view-container label.tab-label').removeClass('tab-active');
        $('#sku-details-tab-0').addClass('tab-active');
      } else {
        $('.light-sku-default-view-container label.tab-label').removeClass('tab-active');
        $('#sku-details-tab-1').addClass('tab-active');
      }
    }
  });
});