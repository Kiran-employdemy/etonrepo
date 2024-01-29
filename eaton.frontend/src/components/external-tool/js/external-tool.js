let App = window.App || {};

App.externalTool = (function () {
  let minMobileWidth;
  let $rotateDeviceMessage;
  let $iframe;

  const $componentEl = document.querySelectorAll('.external-tool');
  /**
  * @property {function} init - check if the current window width is greater than the minimum mobile width defined by the author
  */
  const init = () => {
    let windowResized = App.global.utils.throttle(showRotationMessage, 200);

    minMobileWidth = $componentEl[0].dataset.minWidth ? $componentEl[0].dataset.minWidth : 0;
    $rotateDeviceMessage = $componentEl[0].querySelectorAll('.external-tool__message__rotate')[0];
    $iframe = $componentEl[0].querySelectorAll('iframe')[0];

    window.onresize = windowResized;
    showRotationMessage();
  };
  /**
  * @property {function} showRotationMessage - check if the current window width is greater than the minimum mobile width defined by the author
  */
  const showRotationMessage = () => {
    // if window width is wider, hide the rotate device message and show the iframe
    if (minMobileWidth > window.innerWidth) {
      $iframe.classList.add('hidden');
      $rotateDeviceMessage.classList.add('show');
      // show iframe, hide message
    } else {
      // if window width is narrower, show the rotate device message and hide the iframe
      $iframe.classList.remove('hidden');
      $rotateDeviceMessage.classList.remove('show');
    }
  };
  /**
   * Only initialize if component exists and it requires landscape mode on mobile and iframe should not be hidden completely on mobile
   */
  if ($componentEl.length > 0 && $componentEl[0].dataset.requiresLandscapeMode && !$componentEl[0].dataset.hiddenOnMobile) {
    init();
  }
}());
