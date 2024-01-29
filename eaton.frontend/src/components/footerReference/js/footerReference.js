//-----------------------------------
// Component : Search
//-----------------------------------
'use strict';

let App = window.App || {};

App.search = (function() {

  // Variable Declarations
  const componentClass = '.back-to-top';
  const $componentElement = $(componentClass);
  const isAEMAuthorMode = App.global.utils.isAEMAuthorMode();
  let lastScrollTop = 0;
  let hideTimerID = 0;

  /**
  * Init
  */
  const init = () => {
    // If not in AEM Author Mode & component exists on page - initialize scripts
    if (!isAEMAuthorMode) {
      // console.log('Initialize Search');
      addEventListeners();
    }
  };

  /**
   * Bind All Event Listeners
   */
  const addEventListeners = () => {
    const trident = !!navigator.userAgent.match(/Trident\/7.0/);
    const rv = navigator.userAgent.indexOf('rv:11.0');

    // detection of IE11
    if (trident && rv !== -1) {
      // use mousewheel event on IE11 because scroll event
      $(window).on('mousewheel', (event) => {

        const scrollTop = $(window).scrollTop();
        let isGoingDown = (event.originalEvent.wheelDelta < 0);

        if (scrollTop === 0) {
          isGoingDown = true;
        }

        $componentElement.toggleClass('visible', !isGoingDown);

        if (isGoingDown) { return; }

        // hide the back to top buton after 3 seconds
        clearTimeout(hideTimerID);
        hideTimerID = setTimeout(function() {
          $componentElement.toggleClass('visible', false);
        }, 4000);
      });
        // for IE key-up event
      $(window).on('keydown', (event) => {

        if (event.keyCode === 38) {
          $componentElement.toggleClass('visible');
        }
        // hide the back to top buton after 3 seconds
        clearTimeout(hideTimerID);
        hideTimerID = setTimeout(function () {
          $componentElement.toggleClass('visible', false);
        }, 4000);
      });

      $(window).on('scroll', (event) => {
        const scrollTop = $(window).scrollTop();


        if (scrollTop === 0) {
          $componentElement.toggleClass('visible', false);
        }


      });
    } else {
      $(window).on('scroll', (event) => {
        const scrollTop = $(window).scrollTop();
        const isGoingUp = (scrollTop < lastScrollTop) && (scrollTop > 0);

        $componentElement.toggleClass('visible', isGoingUp);
        lastScrollTop = scrollTop;

        if (!isGoingUp) { return; }

        // hide the back to top buton after 3 seconds
        clearTimeout(hideTimerID);
        hideTimerID = setTimeout(function() {
          $componentElement.toggleClass('visible', false);
        }, 3000);
      });
    }


  };

  /**
  * If containing DOM element is found, Initialize and Expose public methods
  */
  if ($componentElement.length > 0) {
    init();
  }

}());

document.addEventListener('DOMContentLoaded', () => {
  if ($('#okta-sign-in').length > 0 || $('.help-list').length > 0) {
    $('.footer-top').remove();
    $('.footer-bottom').css('position','fixed');
    $('.footer-bottom').css('bottom','0px');
    $('.footer-bottom').css('width','100%');
  }
});


$(document).ready(function() {
  const signoutconfirmationBox = document.getElementById('signoutLabel');
  if (signoutconfirmationBox) {
    let logOutValue = document.getElementById('signoutLabel').getAttribute('data-value');
    let signoutchange = $('#signOut').text().replace('Sign Out ', logOutValue);
    let signoutMobchange = $('#signOutMob').text().replace('Sign Out ', logOutValue);
    document.getElementById('signOut').innerHTML = signoutchange;
    document.getElementById('signOutMob').innerHTML = signoutMobchange;
  }

});
