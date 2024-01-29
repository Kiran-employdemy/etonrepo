//-----------------------------------
// Eaton: Responsive Images
//-----------------------------------
'use strict';

let App = App || {};
App.renditions = (function() {

  // let resizeTimeout = null;

  const init = () => {
    addEventListeners();
    updateImagesSrc();
  };


  /**
  * Bind All event listeners
  */
  const addEventListeners = () => {

    const lazyResize = App.global.utils.throttle(updateImagesSrc, 300);
    $(window).resize(lazyResize);

  };


  /**
  * Update image Source for the Current MediaQuery / Breakpoint
  */
  let updateImagesSrc = () => {

    let $renditionImages = $('.rendition img, .rendition-bg');
    // let checkVal = parseInt($respImgs.first().css('min-width'), 10);
    let mq = 'desktop';

    // Determine Current Breakpoint
    //--------------
    if (window.matchMedia && window.matchMedia(App.global.constants.MEDIA_QUERIES.MOBILE).matches) {
      mq = 'mobile';
    }

    else if (window.matchMedia && window.matchMedia(App.global.constants.MEDIA_QUERIES.TABLET).matches) {
      mq = 'tablet';
    }

    App.global.utils.forEach($renditionImages, (index, element) => {
      let curr = $(element);
      let currSrc = element.src;
      let currData = curr.data();

      if (!curr.is('img')) {
        currSrc = curr.css('background-image');
        if (mq === 'desktop' && currData.desktopRendition && currSrc !== currData.desktopRendition) {
          // curr.attr('src', currData.desktopRendition);
          curr.css('background-image', "url('" + currData.desktopRendition + "')");
        }
        else if (mq === 'tablet' && currData.tabletRendition && currSrc !== currData.tabletRendition) {
          // curr.attr('src', currData.tabletRendition);
          curr.css('background-image', "url('" + currData.tabletRendition + "')");
        }
        else if (mq === 'mobile' && currData.mobileRendition && currSrc !== currData.mobileRendition) {
          // curr.attr('src', currData.mobileRendition);
          curr.css('background-image', "url('" + currData.mobileRendition + "')");
        }
      }
      else {
        if (mq === 'desktop' && currData.desktopRendition && currSrc !== currData.desktopRendition) {
          curr.attr('src', currData.desktopRendition);
        }
        else if (mq === 'tablet' && currData.tabletRendition && currSrc !== currData.tabletRendition) {
          curr.attr('src', currData.tabletRendition);
        }
        else if (mq === 'mobile' && currData.mobileRendition && currSrc !== currData.mobileRendition) {
          curr.attr('src', currData.mobileRendition);
        }
      }
    });
  };


  // Autoinitialize on Runtime
  init();

  return {
    updateImagesSrc
  };

}());
