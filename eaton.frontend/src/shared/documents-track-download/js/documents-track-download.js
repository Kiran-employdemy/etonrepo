//-----------------------------------
// Track Download - These functions are responsible to track the download events of each assets when it's marked as "trackDownload = yes"
//-----------------------------------
'use strict';

let App = window.App || {};
let trackDownloadItems = [];
/**
* AJAX call to track the download events submits to backend servlet.
*/
App.trkDownload = function () {
  let trackDownload = function trackDownload() {
    let srvPath = '/eaton/secure/track-download.nocache.json';
    $.ajax({
      type: 'POST',
      async: false,
      url: srvPath,
      data: JSON.stringify(trackDownloadItems),
      success: function success(data) {
      }
    });
  };

  /**
  * Checks if the URL is valid.
  Ex : https://localhost:4502/content/dam/eaton/example.pdf vs /content/dam/eaton/example.pdf.
  If the url contains a protocol, return the path from the url.
  If doesn't contains, returns the same path.
  */
  function isValidUrl(urlString) {
    try {
      const url = new URL(urlString);
      return url.pathname;
    } catch (_) {
      return urlString;
    }
  }

  /**
  * Registers the global event "data-track-download".
  */
  $( document ).ready(function() {
    $('.track_download').click(function() {
      trackDownloadItems = [];
      const isTracked = $(this).attr('data-track-download');
      if ((isTracked === 'yes' || isTracked === 'true') && $(this).attr('href') !== undefined) {
        trackDownloadItems.push(isValidUrl($(this).attr('href')));
        trackDownload();
      } else {
        // Do Nothing ..
      }
    });
  });
}();


