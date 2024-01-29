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


//-----------------------------------
// Eaton: Utility Functions
//-----------------------------------
'use strict';

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var App = App || {};
App.global = App.global || {};

App.global.utils = function () {

  // const isAEMTouchUI = (
  //   getCookie('cq-editor-layer.page') === 'Edit'
  //   && getCookie('cq-authoring-mode') === 'TOUCH'
  // )
  //   ? true
  //   : false;

  // TODO: Move This behavior to its own "Back to Search Module
  // localStorage.setItem('backToSearch','false');

  var isAEMClassicUI = false;

  /*
  * Helper forEach
  * eg: App.global.utils.forEach( document.querySelectorAll('.cards'), (index, element) { ... } )
  * ----
  * NOTE: What you get back from querySelectorAll() isn't an array,
  * it's a (non-live) NodeList, and not all browsers support the method .forEach on NodeList's
  */
  function forEach(array, callback, scope) {
    for (var i = 0; i < array.length; i++) {
      callback.call(scope, i, array[i]);
    }
  }

  /**
  * Get Cookie Value
  * @param { String } cookieName
  */
  function getCookie(cookieName) {
    var match = document.cookie.match(new RegExp('(^| )' + cookieName + '=([^;]+)'));

    if (match) {
      return match[2];
    }
  }

  /**
   * Set local storage item
   * @param { String } itemKey name of local storage item to set
   * @param { String } itemValue of local storage item to set
   */
  var setLocalStorage = function setLocalStorage(itemKey, itemValue) {
    window.localStorage.setItem(itemKey, itemValue);
  };

  /**
   * Get local storage item
   * @param { String } itemKey name of local storage item to get
   * @return { String }
   */
  var getLocalStorage = function getLocalStorage(itemKey) {
    return window.localStorage.getItem(itemKey);
  };

  /**
   * Remove local storage item
   * @param { String } itemKey name of local storage item to remove
   */
  var removeLocalStorage = function removeLocalStorage(itemKey) {
    window.localStorage.removeItem(itemKey);
  };

  /**
  * AEM's Author mode
  */
  function isAEMAuthorMode() {
    return isAEMClassicUI ? true : false;
  }

  /**
   * Extract i18n Strings from the HTML attribute "data-i18n" for the give Element.
   * eg: <div data-i18n="{ "close": "Close Overlay" }">
   * @param { DOMElement }
   * @return { Object }
   */
  var loadI18NStrings = function loadI18NStrings(element) {
    var varI18nData = element[0].dataset;
    var i18nData = null;
    if (varI18nData === undefined || varI18nData === null) {
      i18nData = element[0].getAttribute('data-i18n');
    } else {
      i18nData = varI18nData.i18n;
    }

    // Save i18n Strings as an Object in a global variable
    return JSON.parse(i18nData) ? JSON.parse(i18nData) : {};
  };

  /**
  * Helper: Throttle Functions
  */
  function throttle(fn, threshhold, scope) {
    threshhold = threshhold || 250;
    var last = void 0;
    var deferTimer = void 0;

    return function () {
      var context = scope || this;
      var now = +new Date();
      var args = arguments;

      if (last && now < last + threshhold) {
        // hold on to it
        clearTimeout(deferTimer);
        deferTimer = setTimeout(function () {
          last = now;
          fn.apply(context, args);
        }, threshhold);
      } else {
        last = now;
        fn.apply(context, args);
      }
    };
  }

  function hashString(str) {
    var hash = 0;
    if (str.length === 0) {
      return hash;
    }
    for (var i = 0; i < str.length; i++) {
      var char = str.charCodeAt(i);
      hash = (hash << 5) - hash + char;
      hash = hash & hash; // Convert to 32bit integer
    }
    return hash;
  }

  /** @function parseIfJson
   *  Either return the param parsed as json or the string itself if it cannot be parsed as json. */
  function parseIfJson(str) {
    try {
      return JSON.parse(str);
    } catch (e) {
      return str;
    }
  }

  /** @function unescapeAttr
   *  Unescape the passed invalue after retrieving it from a data attribute that has been escaped with the escapeAttr method. */
  function unescapeAttr(escapedAttr) {
    return parseIfJson(decodeURIComponent(typeof escapedAttr === 'undefined' ? '' : escapedAttr));
  }

  /** @function escapeAttr
   *  Escape the passed in value for safe use in data attributes. */
  function escapeAttr(attr) {
    return encodeURIComponent((typeof attr === 'undefined' ? 'undefined' : _typeof(attr)) === 'object' ? JSON.stringify(attr) : typeof attr === 'undefined' ? '' : attr);
  }

  /**
   * @function encodeSelector
   * @param value The string value to encode for safe usage as an AEM selector.
   * @returns An encoded string for safe usage as an AEM selector that can be decoded with decodeSelector.
   */
  function encodeSelector(value) {
    return value.replace(/\//g, '{}').replace(/=/g, '[]').replace(/\$/g, '<>').replace(/&/g, '&amp;').replace(/\./g, '::');
  }

  /**
   * @function decodeSelector
   * @param encodedValue
   * @returns
   */
  function decodeSelector(encodedValue) {
    return encodedValue.replace(/{}/g, '/').replace(/\[\]/g, '=').replace(/<>/g, '$').replace(/&amp;/g, '&').replace(/::/g, '.');
  }

  /**
   * Watches for a class to be added to an element.
   *
   * @returns A promise that resolves when the class is added to the element.
   */
  function watchForClass(element, className) {
    return new Promise(function (resolve) {
      var observer = new MutationObserver(function (event) {
        if (element.classList.contains(className)) {
          resolve();
          observer.disconnect();
        }
      });

      observer.observe(element, {
        attributes: true,
        attributeFilter: ['class'],
        childList: false,
        characterData: false
      });
    });
  }

  /**
   * Given a delay in milleseconds and a function, this will return a function
   * that is throttled so that it will run at most once per given amount of milleseconds.
   *
   * @returns The throttled version of the provided function.
   */
  function throttled(delay, fn) {
    var lastCall = 0;
    return function () {
      var now = new Date().getTime();
      if (now - lastCall < delay) {
        return;
      }
      lastCall = now;
      return fn.apply(undefined, arguments);
    };
  }

  // Public Methods
  return {
    forEach: forEach,
    getCookie: getCookie,
    isAEMAuthorMode: isAEMAuthorMode,
    loadI18NStrings: loadI18NStrings,
    throttle: throttle,
    setLocalStorage: setLocalStorage,
    getLocalStorage: getLocalStorage,
    removeLocalStorage: removeLocalStorage,
    hashString: hashString,
    escapeAttr: escapeAttr,
    unescapeAttr: unescapeAttr,
    watchForClass: watchForClass,
    throttled: throttled,
    encodeSelector: encodeSelector,
    decodeSelector: decodeSelector
  };
}();