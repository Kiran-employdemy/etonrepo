/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

let App = window.App || {};

App.thankYouParseParameters = function () {
  const $componentEl = document.querySelectorAll('.thank-you-text');

  /**
   * Parses string, replacing placeholders inside {{}} with parameters from
   * query parameters
   * @param text
   * @returns {string} original text with all placeholders replaced with
   * appropriate parameters
   */
  const parseText = text => {
    const re = /{{([^}}]+)?}}/g;
    let match;

    while ((match = re.exec(text)) !== null) {
      const urlParams = new URLSearchParams(window.location.search);

      if (urlParams.has(match[1])) {
        text = text.replace(match[0], urlParams.get(match[1]));
      }
    }
    return text;
  };

  const init = () => {
    initload();
  };

  let $parseEl = [];
  const initload = () => {
    $componentEl.forEach(function($thankYouEl) {
      $parseEl = $thankYouEl.querySelector('.thank-you-parse') || {};
      $parseEl.innerHTML = parseText($parseEl.innerHTML);
    });
  };
  /* eslint-disable  */
  if (window.location.href.indexOf('confirmation=failure')>0) {
    $('#idofthedivtohide').find('p:nth-child(1)').hide();
    $('#idofthedivtohide').find('p:nth-child(2)').hide();
  } else if (window.location.href.indexOf('confirmation=success')>0) {
    $('#idofthedivtohide').find('p:nth-child(2)').hide();
    $('#idofthedivtohide').find('p:nth-child(3)').hide();
  }
  else {
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
