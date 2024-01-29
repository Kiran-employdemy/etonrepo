//-----------------------------------
// Eaton: Utility Functions
//-----------------------------------
'use strict';

let App = App || {};
App.global = App.global || {};

App.global.utils = (function() {

  // const isAEMTouchUI = (
  //   getCookie('cq-editor-layer.page') === 'Edit'
  //   && getCookie('cq-authoring-mode') === 'TOUCH'
  // )
  //   ? true
  //   : false;

  // TODO: Move This behavior to its own "Back to Search Module
  // localStorage.setItem('backToSearch','false');

  const isAEMClassicUI = false;


  /*
  * Helper forEach
  * eg: App.global.utils.forEach( document.querySelectorAll('.cards'), (index, element) { ... } )
  * ----
  * NOTE: What you get back from querySelectorAll() isn't an array,
  * it's a (non-live) NodeList, and not all browsers support the method .forEach on NodeList's
  */
  function forEach (array, callback, scope) {
    for (let i = 0; i < array.length; i++) {
      callback.call(scope, i, array[i]);
    }
  }


  /**
  * Get Cookie Value
  * @param { String } cookieName
  */
  function getCookie(cookieName) {
    const match = document.cookie.match( new RegExp('(^| )' + cookieName + '=([^;]+)') );

    if (match) {
      return match[2];
    }
  }

  /**
   * Set local storage item
   * @param { String } itemKey name of local storage item to set
   * @param { String } itemValue of local storage item to set
   */
  const setLocalStorage = (itemKey, itemValue) => {
    window.localStorage.setItem(itemKey, itemValue);
  };

  /**
   * Get local storage item
   * @param { String } itemKey name of local storage item to get
   * @return { String }
   */
  const getLocalStorage = (itemKey) => {
    return window.localStorage.getItem(itemKey);
  };

  /**
   * Remove local storage item
   * @param { String } itemKey name of local storage item to remove
   */
  const removeLocalStorage = (itemKey) => {
    window.localStorage.removeItem(itemKey);
  };


  /**
  * AEM's Author mode
  */
  function isAEMAuthorMode() {
    return (isAEMClassicUI) ? true : false;
  }


  /**
   * Extract i18n Strings from the HTML attribute "data-i18n" for the give Element.
   * eg: <div data-i18n="{ "close": "Close Overlay" }">
   * @param { DOMElement }
   * @return { Object }
   */
  const loadI18NStrings = function(element) {
    let varI18nData = element[0].dataset;
    let i18nData = null;
    if (varI18nData === undefined || varI18nData === null)
	{
      i18nData = element[0].getAttribute('data-i18n');
    }
    else
	{
      i18nData = varI18nData.i18n;
    }

    // Save i18n Strings as an Object in a global variable
    return (JSON.parse(i18nData))
      ? JSON.parse(i18nData)
      : {};

  };


  /**
  * Helper: Throttle Functions
  */
  function throttle (fn, threshhold, scope) {
    threshhold = threshhold || 250;
    let last;
    let deferTimer;

    return function () {
      let context = scope || this;
      let now = +new Date();
      let args = arguments;

      if (last && now < last + threshhold) {
        // hold on to it
        clearTimeout(deferTimer);
        deferTimer = setTimeout(function () {
          last = now;
          fn.apply(context, args);
        }, threshhold);
      }

      else {
        last = now;
        fn.apply(context, args);
      }
    };
  }

  function hashString(str) {
    let hash = 0;
    if (str.length === 0) {
      return hash;
    }
    for (let i = 0; i < str.length; i++) {
      let char = str.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
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
    return parseIfJson(
      decodeURIComponent(
        typeof escapedAttr === 'undefined'
          ? ''
          : escapedAttr));
  }

  /** @function escapeAttr
   *  Escape the passed in value for safe use in data attributes. */
  function escapeAttr(attr) {
    return encodeURIComponent(
      typeof attr === 'object'
        ? JSON.stringify(attr)
        : typeof attr === 'undefined'
          ? ''
          : attr);
  }

  /**
   * @function encodeSelector
   * @param value The string value to encode for safe usage as an AEM selector.
   * @returns An encoded string for safe usage as an AEM selector that can be decoded with decodeSelector.
   */
  function encodeSelector(value) {
    return value
      .replace(/\//g, '{}')
      .replace(/=/g, '[]')
      .replace(/\$/g, '<>')
      .replace(/&/g, '&amp;')
      .replace(/\./g, '::');
  }

  /**
   * @function decodeSelector
   * @param encodedValue
   * @returns
   */
  function decodeSelector(encodedValue) {
    return encodedValue
      .replace(/{}/g, '/')
      .replace(/\[\]/g, '=')
      .replace(/<>/g, '$')
      .replace(/&amp;/g, '&')
      .replace(/::/g, '.');
  }

  /**
   * Watches for a class to be added to an element.
   *
   * @returns A promise that resolves when the class is added to the element.
   */
  function watchForClass(element, className) {
    return new Promise(resolve => {
      let observer = new MutationObserver(event => {
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
    let lastCall = 0;
    return function (...args) {
      const now = (new Date).getTime();
      if (now - lastCall < delay) {
        return;
      }
      lastCall = now;
      return fn(...args);
    };
  }

  // Public Methods
  return {
    forEach,
    getCookie,
    isAEMAuthorMode,
    loadI18NStrings,
    throttle,
    setLocalStorage,
    getLocalStorage,
    removeLocalStorage,
    hashString,
    escapeAttr,
    unescapeAttr,
    watchForClass,
    throttled,
    encodeSelector,
    decodeSelector
  };

})();
