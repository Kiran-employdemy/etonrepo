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

/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

var App = window.App || {};

App.thankYouParseParameters = function () {
  var $componentEl = document.querySelectorAll('.thank-you-text');

  /**
   * Parses string, replacing placeholders inside {{}} with parameters from
   * query parameters
   * @param text
   * @returns {string} original text with all placeholders replaced with
   * appropriate parameters
   */
  var parseText = function parseText(text) {
    var re = /{{([^}}]+)?}}/g;
    var match = void 0;

    while ((match = re.exec(text)) !== null) {
      var urlParams = new URLSearchParams(window.location.search);

      if (urlParams.has(match[1])) {
        text = text.replace(match[0], urlParams.get(match[1]));
      }
    }
    return text;
  };

  var init = function init() {
    initload();
  };

  var $parseEl = [];
  var initload = function initload() {
    $componentEl.forEach(function ($thankYouEl) {
      $parseEl = $thankYouEl.querySelector('.thank-you-parse');
      $parseEl.innerHTML = parseText($parseEl.innerHTML);
    });
  };
  /* eslint-disable  */
  if (window.location.href.indexOf('confirmation=failure') > 0) {
    $('#idofthedivtohide').find('p:nth-child(1)').hide();
    $('#idofthedivtohide').find('p:nth-child(2)').hide();
  } else if (window.location.href.indexOf('confirmation=success') > 0) {
    $('#idofthedivtohide').find('p:nth-child(2)').hide();
    $('#idofthedivtohide').find('p:nth-child(3)').hide();
  } else {
    $('#idofthedivtohide').find('p:nth-child(3)').hide();
  }
  /* eslint-enable  */
  /**
   * Only initialize if component exists
   */
  if ($componentEl.length > 0) {
    init();
  }
}();