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
  $('.vanity-cta a').click(function (e) {
    e.preventDefault();
    var dataAttrParam = $(this).data('attrParam');
    var anchorUrl = $(this).attr('href');
    var target = $(this).attr('target') ? $(this).attr('target') : '_self';
    var str = window.location.search;
    var objURL = {};
    str.replace(new RegExp('([^?=&]+)(=([^&]*))?', 'g'), function ($0, $1, $2, $3) {
      objURL[$1] = $3;
    });

    if (dataAttrParam && objURL.hasOwnProperty(dataAttrParam)) {
      window.open(objURL[dataAttrParam], target);
      return false;
    } else {
      window.open(anchorUrl, target);
      return false;
    }
  });
});
/* This function is added to remove the percolateContentId from URL when coming back to previous page*/
$('.category-hero__cta, .eaton-breadcrumb__list-item').click(function (e) {

  if (window.location.href.indexOf('percolateContentId') > -1) {
    var url = parent.location.href;
    url = url.split('?')[0];
    history.pushState(url, 'page', url);
  }
});