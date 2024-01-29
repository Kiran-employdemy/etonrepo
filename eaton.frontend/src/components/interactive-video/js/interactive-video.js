/* interactive video component*/

/* eslint-disable */
if ($('.immersive-rooms-body').length > 0 ) {
  let videoproperties = $('.immersive-rooms-body').attr('videoComponentData').replace(/[\r\n]\s*/g, '\n');
  let videodata = JSON.parse(videoproperties);

  (function e(t,n,r) {function s(o,u) {if (!n[o]) {if (!t[o]) {let a = typeof require === 'function' && require;if (!u && a) {return a(o,!0);} if (i) {return i(o,!0);} let f = new Error("Cannot find module '" + o + "'");throw f.code = 'MODULE_NOT_FOUND',f;} let l = n[o] = {exports: {}};t[o][0].call(l.exports,function(e) {let n = t[o][1][e];return s(n ? n : e);},l,l.exports,e,t,n,r);} return n[o].exports;} var i = typeof require === 'function' && require;for (let o = 0;o < r.length;o++) {s(r[o]);} return s;})({1: [function(require,module,exports) {
    module.exports = {
      verticalPageRedirect: videodata.redirectLink,
      room: 'bedroom',

      scenes: [
        {
          pauseTime: 7,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title1,
          paragraph: videodata.intro1,
          button: videodata.buttontext1,
          href: videodata.buttonlink1,
          id: 'bedroom-overlay__',
          mediaOrInteractiveElement: null,
          interactiveElementFunction: null
        },
        {
          pauseTime: 12,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title2,
          paragraph: videodata.intro2,
          button: videodata.buttontext2,
          href: videodata.buttonlink2,
          id: 'bedroom-overlay__',
          mediaOrInteractiveElement: 'keepingTrackIphone',
          interactiveElementFunction: 'showNoniteractiveMedia(keepingTrackIphone)'
        },
        {
          pauseTime: 17,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title3,
          paragraph: videodata.intro3,
          button: videodata.buttontext3,
          href: videodata.buttonlink3,
          id: 'bedroom-overlay__',
          mediaOrInteractiveElement: 'drggableSwitchContainer',
          interactiveElementFunction: 'draggable.pulsateDragHideSwitch(draggableSwitch, switchArrows, drggableSwitchContainer)'
        },
        {
          pauseTime: 21,
          specialPauseTime: 30,
          hasBeenPaused: false,
          heading: videodata.title4,
          paragraph: videodata.intro4,
          button: videodata.buttontext4,
          href: videodata.buttonlink4,
          id: 'bedroom-overlay__',
          mediaOrInteractiveElement: null,
          interactiveElementFunction: 'waterSteamBackground()'
        },
        {
          pauseTime: 37,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title5,
          paragraph: videodata.intro5,
          button: videodata.buttontext5,
          href: videodata.buttonlink5,
          id: 'bedroom-overlay__',
          mediaOrInteractiveElement: null,
          interactiveElementFunction: null
        },
        {
          pauseTime: 39,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title6,
          paragraph: videodata.intro6,
          button: videodata.buttontext6,
          href: videodata.buttonlink6,
          id: 'bedroom-overlay__',
          mediaOrInteractiveElement: 'standbyButtonOn',
          interactiveElementFunction: 'showNoniteractiveMedia(standbyButtonOn)'
        }
      ]

    };

  },{}],2: [function(require,module,exports) {
    module.exports = {
      verticalPageRedirect: videodata.redirectLink,
      room: 'garage',
      scenes: [
        {

          pauseTime: 11,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title1,
          paragraph: videodata.intro1,
          button: videodata.buttontext1,
          href: videodata.buttonlink1,
          id: 'garage-overlay__',
          mediaOrInteractiveElement: null,
          interactiveElementFunction: null
        },
        {

          pauseTime: 14,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title2,
          paragraph: videodata.intro2,
          button: videodata.buttontext2,
          href: videodata.buttonlink2,
          id: 'garage-overlay__',
          mediaOrInteractiveElement: 'thermometer',
          interactiveElementFunction: 'showNoniteractiveMedia(thermometer)'
        },
        {

          pauseTime: 19,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title3,
          paragraph: videodata.intro3,
          button: videodata.buttontext3,
          href: videodata.buttonlink3,
          id: 'garage-overlay__',
          mediaOrInteractiveElement: null,
          interactiveElementFunction: 'showNoniteractiveMedia(peaceOfMindAuth)'
        },
        {
          pauseTime: 22,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title4,
          paragraph: videodata.intro4,
          button: videodata.buttontext4,
          href: videodata.buttonlink4,
          id: 'garage-overlay__',
          mediaOrInteractiveElement: null,
          interactiveElementFunction: 'fireAlarmOverlay()'
        },
        {

          pauseTime: 27,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title5,
          paragraph: videodata.intro5,
          button: videodata.buttontext5,
          href: videodata.buttonlink5,
          id: 'garage-overlay__',
          mediaOrInteractiveElement: null,
          interactiveElementFunction: 'showNoniteractiveMedia(peaceOfMindIpad)'
        },
        {

          pauseTime: 29,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title6,
          paragraph: videodata.intro6,
          button: videodata.buttontext6,
          href: videodata.buttonlink6,
          id: 'garage-overlay__',
          mediaOrInteractiveElement: 'standbyButtonOn',
          interactiveElementFunction: 'showNoniteractiveMedia(standbyButtonOn)'
        }
      ]

    };

  },{}],3: [function(require,module,exports) {
    module.exports = {
      verticalPageRedirect: videodata.redirectLink,
      room: 'livingroom',

      scenes: [
        {

          pauseTime: 5,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title1,
          paragraph: videodata.intro1,
          button: videodata.buttontext1,
          href: videodata.buttonlink1,
          id: 'livingroom-overlay__',
          mediaOrInteractiveElement: 'wirelssSignal',
          interactiveElementFunction: 'showNoniteractiveMedia(wirelssSignal)'
        },
        {

          pauseTime: 10,
          specialPauseTime: 17,
          hasBeenPaused: false,
          heading: videodata.title2,
          paragraph: videodata.intro2,
          button: videodata.buttontext2,
          href: videodata.buttonlink2,
          id: 'livingroom-overlay__',
          mediaOrInteractiveElement: 'entertaintmentTv',
          interactiveElementFunction: null
        },
        {

          pauseTime: 21,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title3,
          paragraph: videodata.intro3,
          button: videodata.buttontext3,
          href: videodata.buttonlink3,
          id: 'livingroom-overlay__',
          mediaOrInteractiveElement: 'lifestyleIpad',
          interactiveElementFunction: 'showNoniteractiveMedia(lifestyleIpad)'
        },
        {
          pauseTime: 27,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title4,
          paragraph: videodata.intro4,
          button: videodata.buttontext4,
          href: videodata.buttonlink4,
          id: 'livingroom-overlay__',
          mediaOrInteractiveElement: 'thermometer',
          interactiveElementFunction: 'showNoniteractiveMedia(thermometer)'
        },
        {

          pauseTime: 31,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title5,
          paragraph: videodata.intro5,
          button: videodata.buttontext5,
          href: videodata.buttonlink5,
          id: 'livingroom-overlay__',
          mediaOrInteractiveElement: null,
          interactiveElementFunction: 'showNoniteractiveMedia(dimmerSwitch)'
        },
        {

          pauseTime: 35,
          specialPauseTime: null,
          hasBeenPaused: false,
          heading: videodata.title6,
          paragraph: videodata.intro6,
          button: videodata.buttontext6,
          href: videodata.buttonlink6,
          id: 'livingroom-overlay__',
          mediaOrInteractiveElement: 'standbyButtonOn',
          interactiveElementFunction: 'showNoniteractiveMedia(standbyButtonOn)'
        }
      ]

    };

  },{}],4: [function(require,module,exports) {
// window.jQuery = window.$ = require("./../../node_modules/jquery"); //comment out when uploading to server
// require('./../../node_modules/jquery-ui'); //comment out when uploading to server
    let velocity = require('./lib/velocity.min.js');

    let roomVideo = require('./video.js');
    let draggable = require('./draggableSwitch.js');
    let thermometer = require('./thermometer.js');
    let carousel = require('./immersive-carousel.js');

    $(document).ready(function() {
      let drggableSwitchContainer = $('#js-draggable-switch-container');
      let draggableSwitch = $('.js-draggable-switch');
      let switchArrows = $('.js-switch-arrow');
      let thermometerElement = $('#js-thermometer');
      let video = $('#js-video');
      let connSpeed;


      if (video) {
        roomVideo.playAllScenes(drggableSwitchContainer,switchArrows,draggableSwitch);
      }


      if (thermometerElement) {
        thermometer.useThermometer(thermometerElement);
      }



    });

  },{'./draggableSwitch.js': 5,'./immersive-carousel.js': 6,'./lib/velocity.min.js': 7,'./thermometer.js': 8,'./video.js': 9}],5: [function(require,module,exports) {
    'use strict';
    module.exports = (function() {
      let velocity = require('./lib/velocity.min.js');
      let draggableSwitch = null;
      let switchArrows = null;
      let drggableSwitchContainer = null;

      function dragSwitch() {
        draggableSwitch.draggable({
          containment: drggableSwitchContainer
        });

      }


      function hideSwitchArrows() {
        switchArrows.droppable();
        switchArrows.on('dropover', function(event, ui) {
          event.target.style.visibility = 'hidden';
        })
    .on('dropout', function(event, ui) {
      event.target.style.visibility = 'visible';
    });
      }


      function pulsateDragHideSwitch(lightSwitch, arrows, container) {
        draggableSwitch = lightSwitch;
        switchArrows = arrows;
        drggableSwitchContainer = container;

        switchArrows.css('display', 'block');
        draggableSwitch.css('display', 'block');
        drggableSwitchContainer.css('display', 'inline-block').css('visibility', 'visible');

        $.Velocity.hook(container, 'opacity', 0);
        container.velocity({ opacity: 1}, { delay: 1600, duration: 1400});

        dragSwitch();
        hideSwitchArrows();
      }

      return { pulsateDragHideSwitch: pulsateDragHideSwitch };

    })();

  },{'./lib/velocity.min.js': 7}],6: [function(require,module,exports) {
    let carousel = (function($) {
      return {
        windowWidth: $(window).width(),
        windowHeight: $(window).height(),
        isComplete: true,

        init: function() {
          this.bindEvents();
          this.bindWindowEvents();
          this.mobileSlide();
        },
        bindEvents: function() {
          let _this = this;

            // prev link click
          $('.carousel-prev').unbind('click').bind('click', function(e) {
            e.preventDefault();

            if (_this.isComplete)
            {_this.slideCarousel('prev');}
          });

            // next link click
          $('.carousel-next').unbind('click').bind('click', function(e) {
            e.preventDefault();

            if (_this.isComplete)
            {_this.slideCarousel('next');}
          });
        },
        bindWindowEvents: function() {
          let _this = this;

            // On Resize
          $(window).on('resize', function() {
            _this.windowWidth = $(window).width();
            _this.windowHeight = $(window).height();
          });

            // On Orientation
          $(window).on('orientationchange', function(event) {

          });

            // On Scroll
          $(window).on('scroll', function() {

          });
        },
        slideCarousel: function(direction) {
          let _this = this;
          _this.isComplete = false;

          let lifestyle = $('.bg-lifestyle');
          let peace = $('.bg-peace');
          let energy = $('.bg-energy');

          if (direction === 'next') {

            firstList = $('.carousel-container ul > li:first-child');
            currentActiveList = $('.carousel-container ul > li.active');
            nextActiveList = $('.carousel-container ul > li.active').next('li');

            if (nextActiveList.length === 0) {
              nextActiveList = firstList;
            }

            var currentBgIndex = nextActiveList.index() + 1;

            if (lifestyle.length) {
              console.log('lifestyle');
              if (currentBgIndex === 1) {
                $('.bg-wrapper').css('background-position', '10% center');
              } else if (currentBgIndex === 2) {
                $('.bg-wrapper').css('background-position', '30% center');
              } else if (currentBgIndex === 3) {
                $('.bg-wrapper').css('background-position', '44% center');
              } else if (currentBgIndex === 4) {
                $('.bg-wrapper').css('background-position', '62% center');
              } else if (currentBgIndex === 5) {
                $('.bg-wrapper').css('background-position', '77% center');
              } else if (currentBgIndex === 6) {
                $('.bg-wrapper').css('background-position', '100% center');
              } else {
                $('.bg-wrapper').css('background-position', '0 center');
              }
            } else if (peace.length) {
              console.log('peace');
              if (currentBgIndex === 1) {
                $('.bg-wrapper').css('background-position', '10% center');
              } else if (currentBgIndex === 2) {
                $('.bg-wrapper').css('background-position', '46% center');
              } else if (currentBgIndex === 3) {
                $('.bg-wrapper').css('background-position', '52% center');
              } else if (currentBgIndex === 4) {
                $('.bg-wrapper').css('background-position', '64% center');
              } else if (currentBgIndex === 5) {
                $('.bg-wrapper').css('background-position', '75% center');
              } else if (currentBgIndex === 6) {
                $('.bg-wrapper').css('background-position', '100% center');
              } else {
                $('.bg-wrapper').css('background-position', '0 center');
              }
            } else if (energy.length) {
              console.log('energy');
              if (currentBgIndex === 1) {
                $('.bg-wrapper').css('background-position', '36% center');
              } else if (currentBgIndex === 2) {
                $('.bg-wrapper').css('background-position', '48% center');
              } else if (currentBgIndex === 3) {
                $('.bg-wrapper').css('background-position', '52% center');
              } else if (currentBgIndex === 4) {
                $('.bg-wrapper').css('background-position', '68% center');
              } else if (currentBgIndex === 5) {
                $('.bg-wrapper').css('background-position', '95% center');
              } else if (currentBgIndex === 6) {
                $('.bg-wrapper').css('background-position', '100% center');
              } else {
                $('.bg-wrapper').css('background-position', '0 center');
              }
            }

            currentActiveList.removeClass('active');
            nextActiveList.addClass('active');

            _this.isComplete = true;

          } else if (direction == 'prev') {
            lastList = $('.carousel-container ul > li:last-child');
            currentActiveList = $('.carousel-container ul > li.active');
            prevActiveList = $('.carousel-container ul > li.active').prev('li');

            if (prevActiveList.length == 0) {
              prevActiveList = lastList;
            }

            var currentBgIndex = prevActiveList.index() + 1;

            if (lifestyle.length) {
              console.log('lifestyle');
              if (currentBgIndex === 1) {
                $('.bg-wrapper').css('background-position', '10% center');
              } else if (currentBgIndex === 2) {
                $('.bg-wrapper').css('background-position', '30% center');
              } else if (currentBgIndex === 3) {
                $('.bg-wrapper').css('background-position', '44% center');
              } else if (currentBgIndex === 4) {
                $('.bg-wrapper').css('background-position', '62% center');
              } else if (currentBgIndex === 5) {
                $('.bg-wrapper').css('background-position', '77% center');
              } else if (currentBgIndex === 6) {
                $('.bg-wrapper').css('background-position', '100% center');
              } else {
                $('.bg-wrapper').css('background-position', '0 center');
              }
            } else if (peace.length) {
              console.log('peace');
              if (currentBgIndex === 1) {
                $('.bg-wrapper').css('background-position', '10% center');
              } else if (currentBgIndex === 2) {
                $('.bg-wrapper').css('background-position', '46% center');
              } else if (currentBgIndex === 3) {
                $('.bg-wrapper').css('background-position', '52% center');
              } else if (currentBgIndex === 4) {
                $('.bg-wrapper').css('background-position', '64% center');
              } else if (currentBgIndex === 5) {
                $('.bg-wrapper').css('background-position', '75% center');
              } else if (currentBgIndex === 6) {
                $('.bg-wrapper').css('background-position', '100% center');
              } else {
                $('.bg-wrapper').css('background-position', '0 center');
              }
            } else if (energy.length) {
              console.log('energy');
              if (currentBgIndex === 1) {
                $('.bg-wrapper').css('background-position', '36% center');
              } else if (currentBgIndex === 2) {
                $('.bg-wrapper').css('background-position', '48% center');
              } else if (currentBgIndex === 3) {
                $('.bg-wrapper').css('background-position', '52% center');
              } else if (currentBgIndex === 4) {
                $('.bg-wrapper').css('background-position', '68% center');
              } else if (currentBgIndex === 5) {
                $('.bg-wrapper').css('background-position', '95% center');
              } else if (currentBgIndex === 6) {
                $('.bg-wrapper').css('background-position', '100% center');
              } else {
                $('.bg-wrapper').css('background-position', '0 center');
              }
            }

            currentActiveList.removeClass('active');
            prevActiveList.addClass('active');

            _this.isComplete = true;
          }
        },
        mobileSlide: function() {
          let _this = this;

          let x1 = 0;
          let endCoords = {};

          $(document).bind('touchstart', function(event) {
                // alert(baseClasses);
            endCoords = event.originalEvent.targetTouches[0];
            x1 = endCoords.pageX;
          });

          $(document).bind('touchend', function(event) {
            let x2 = endCoords.pageX;
            let diff = parseInt(x2) - parseInt(x1);

            if (diff > 0 && diff > 70) {
              $('.carousel-prev').trigger('click');
            } else if (diff < 0 && diff < -70) {
              $('.carousel-next').trigger('click');
            }
          });
        }

      };
    })(jQuery);

// document on load
    jQuery(document).ready(function($) {
      carousel.init();
    });

  },{}],7: [function(require,module,exports) {
/* ! VelocityJS.org (1.2.3). (C) 2014 Julian Shapiro. MIT @license: en.wikipedia.org/wiki/MIT_License */
/* ! VelocityJS.org jQuery Shim (1.0.1). (C) 2014 The jQuery Foundation. MIT @license: en.wikipedia.org/wiki/MIT_License. */
    !function(a) {function b(a) {let b = a.length,d = c.type(a);return 'function' === d || c.isWindow(a) ? !1 : 1 === a.nodeType && b ? !0 : 'array' === d || 0 === b || 'number' === typeof b && b > 0 && b - 1 in a;} if (!a.jQuery) {var c = function(a,b) {return new c.fn.init(a,b);};c.isWindow = function(a) {return null != a && a == a.window;},c.type = function(a) {return null == a ? a + '' : 'object' === typeof a || 'function' === typeof a ? e[g.call(a)] || 'object' : typeof a;},c.isArray = Array.isArray || function(a) {return 'array' === c.type(a);},c.isPlainObject = function(a) {let b;if (!a || 'object' !== c.type(a) || a.nodeType || c.isWindow(a)) {return !1;} try {if (a.constructor && !f.call(a,'constructor') && !f.call(a.constructor.prototype,'isPrototypeOf')) {return !1;}} catch (d) {return !1;} for (b in a) {} return void 0 === b || f.call(a,b);},c.each = function(a,c,d) {let e,f = 0,g = a.length,h = b(a);if (d) {if (h) {for (;g > f && (e = c.apply(a[f],d),e !== !1);f++) {}} else {for (f in a) {if (e = c.apply(a[f],d),e === !1) {break;}}}} else if (h) {for (;g > f && (e = c.call(a[f],f,a[f]),e !== !1);f++) {}} else {for (f in a) {if (e = c.call(a[f],f,a[f]),e === !1) {break;}}} return a;},c.data = function(a,b,e) {if (void 0 === e) {var f = a[c.expando],g = f && d[f];if (void 0 === b) {return g;} if (g && b in g) {return g[b];}} else if (void 0 !== b) {var f = a[c.expando] || (a[c.expando] = ++c.uuid);return d[f] = d[f] || {},d[f][b] = e,e;}},c.removeData = function(a,b) {let e = a[c.expando],f = e && d[e];f && c.each(b,function(a,b) {delete f[b];});},c.extend = function() {let a,b,d,e,f,g,h = arguments[0] || {},i = 1,j = arguments.length,k = !1;for ('boolean' === typeof h && (k = h,h = arguments[i] || {},i++),'object' !== typeof h && 'function' !== c.type(h) && (h = {}),i === j && (h = this,i--);j > i;i++) {if (null != (f = arguments[i])) {for (e in f) {a = h[e],d = f[e],h !== d && (k && d && (c.isPlainObject(d) || (b = c.isArray(d))) ? (b ? (b = !1,g = a && c.isArray(a) ? a : []) : g = a && c.isPlainObject(a) ? a : {},h[e] = c.extend(k,g,d)) : void 0 !== d && (h[e] = d));}}} return h;},c.queue = function(a,d,e) {function f(a,c) {let d = c || [];return null != a && (b(Object(a)) ? !function(a,b) {for (var c = +b.length,d = 0,e = a.length;c > d;) {a[e++] = b[d++];} if (c !== c) {for (;void 0 !== b[d];) {a[e++] = b[d++];}} return a.length = e,a;}(d,'string' === typeof a ? [a] : a) : [].push.call(d,a)),d;} if (a) {d = (d || 'fx') + 'queue';let g = c.data(a,d);return e ? (!g || c.isArray(e) ? g = c.data(a,d,f(e)) : g.push(e),g) : g || [];}},c.dequeue = function(a,b) {c.each(a.nodeType ? [a] : a,function(a,d) {b = b || 'fx';let e = c.queue(d,b),f = e.shift();'inprogress' === f && (f = e.shift()),f && ('fx' === b && e.unshift('inprogress'),f.call(d,function() {c.dequeue(d,b);}));});},c.fn = c.prototype = {init: function(a) {if (a.nodeType) {return this[0] = a,this;} throw new Error('Not a DOM node.');},offset: function() {let b = this[0].getBoundingClientRect ? this[0].getBoundingClientRect() : {top: 0,left: 0};return {top: b.top + (a.pageYOffset || document.scrollTop || 0) - (document.clientTop || 0),left: b.left + (a.pageXOffset || document.scrollLeft || 0) - (document.clientLeft || 0)};},position: function() {function a() {for (var a = this.offsetParent || document;a && 'html' === !a.nodeType.toLowerCase && 'static' === a.style.position;) {a = a.offsetParent;} return a || document;} var b = this[0],a = a.apply(b),d = this.offset(),e = /^(?:body|html)$/i.test(a.nodeName) ? {top: 0,left: 0} : c(a).offset();return d.top -= parseFloat(b.style.marginTop) || 0,d.left -= parseFloat(b.style.marginLeft) || 0,a.style && (e.top += parseFloat(a.style.borderTopWidth) || 0,e.left += parseFloat(a.style.borderLeftWidth) || 0),{top: d.top - e.top,left: d.left - e.left};}};var d = {};c.expando = 'velocity' + (new Date).getTime(),c.uuid = 0;for (var e = {},f = e.hasOwnProperty,g = e.toString,h = 'Boolean Number String Function Array Date RegExp Object Error'.split(' '),i = 0;i < h.length;i++) {e['[object ' + h[i] + ']'] = h[i].toLowerCase();}c.fn.init.prototype = c.fn,a.Velocity = {Utilities: c};}}(window),function(a) {'object' === typeof module && 'object' === typeof module.exports ? module.exports = a() : 'function' === typeof define && define.amd ? define(a) : a();}(function() {return function(a,b,c,d) {function e(a) {for (var b = -1,c = a ? a.length : 0,d = [];++b < c;) {let e = a[b];e && d.push(e);} return d;} function f(a) {return p.isWrapped(a) ? a = [].slice.call(a) : p.isNode(a) && (a = [a]),a;} function g(a) {let b = m.data(a,'velocity');return null === b ? d : b;} function h(a) {return function(b) {return Math.round(b * a) * (1 / a);};} function i(a,c,d,e) {function f(a,b) {return 1 - 3 * b + 3 * a;} function g(a,b) {return 3 * b - 6 * a;} function h(a) {return 3 * a;} function i(a,b,c) {return ((f(b,c) * a + g(b,c)) * a + h(b)) * a;} function j(a,b,c) {return 3 * f(b,c) * a * a + 2 * g(b,c) * a + h(b);} function k(b,c) {for (let e = 0;p > e;++e) {let f = j(c,a,d);if (0 === f) {return c;} let g = i(c,a,d) - b;c -= g / f;} return c;} function l() {for (let b = 0;t > b;++b) {x[b] = i(b * u,a,d);}} function m(b,c,e) {let f,g,h = 0;do {g = c + (e - c) / 2,f = i(g,a,d) - b,f > 0 ? e = g : c = g;} while (Math.abs(f) > r && ++h < s);return g;} function n(b) {for (var c = 0,e = 1,f = t - 1;e != f && x[e] <= b;++e) {c += u;}--e;let g = (b - x[e]) / (x[e + 1] - x[e]),h = c + g * u,i = j(h,a,d);return i >= q ? k(b,h) : 0 == i ? h : m(b,c,c + u);} function o() {y = !0,(a != c || d != e) && l();} var p = 4,q = .001,r = 1e-7,s = 10,t = 11,u = 1 / (t - 1),v = 'Float32Array' in b;if (4 !== arguments.length) {return !1;} for (let w = 0;4 > w;++w) {if ('number' !== typeof arguments[w] || isNaN(arguments[w]) || !isFinite(arguments[w])) {return !1;}}a = Math.min(a,1),d = Math.min(d,1),a = Math.max(a,0),d = Math.max(d,0);var x = v ? new Float32Array(t) : new Array(t),y = !1,z = function(b) {return y || o(),a === c && d === e ? b : 0 === b ? 0 : 1 === b ? 1 : i(n(b),c,e);};z.getControlPoints = function() {return [{x: a,y: c},{x: d,y: e}];};let A = 'generateBezier(' + [a,c,d,e] + ')';return z.toString = function() {return A;},z;} function j(a,b) {let c = a;return p.isString(a) ? t.Easings[a] || (c = !1) : c = p.isArray(a) && 1 === a.length ? h.apply(null,a) : p.isArray(a) && 2 === a.length ? u.apply(null,a.concat([b])) : p.isArray(a) && 4 === a.length ? i.apply(null,a) : !1,c === !1 && (c = t.Easings[t.defaults.easing] ? t.defaults.easing : s),c;} function k(a) {if (a) {let b = (new Date).getTime(),c = t.State.calls.length;c > 1e4 && (t.State.calls = e(t.State.calls));for (let f = 0;c > f;f++) {if (t.State.calls[f]) {let h = t.State.calls[f],i = h[0],j = h[2],n = h[3],o = !!n,q = null;n || (n = t.State.calls[f][3] = b - 16);for (var r = Math.min((b - n) / j.duration,1),s = 0,u = i.length;u > s;s++) {var w = i[s],y = w.element;if (g(y)) {let z = !1;if (j.display !== d && null !== j.display && 'none' !== j.display) {if ('flex' === j.display) {let A = ['-webkit-box','-moz-box','-ms-flexbox','-webkit-flex'];m.each(A,function(a,b) {v.setPropertyValue(y,'display',b);});}v.setPropertyValue(y,'display',j.display);}j.visibility !== d && 'hidden' !== j.visibility && v.setPropertyValue(y,'visibility',j.visibility);for (let B in w) {if ('element' !== B) {var C,D = w[B],E = p.isString(D.easing) ? t.Easings[D.easing] : D.easing;if (1 === r) {C = D.endValue;} else {let F = D.endValue - D.startValue;if (C = D.startValue + F * E(r,j,F),!o && C === D.currentValue) {continue;}} if (D.currentValue = C,'tween' === B) {q = C;} else {if (v.Hooks.registered[B]) {var G = v.Hooks.getRoot(B),H = g(y).rootPropertyValueCache[G];H && (D.rootPropertyValue = H);} let I = v.setPropertyValue(y,B,D.currentValue + (0 === parseFloat(C) ? '' : D.unitType),D.rootPropertyValue,D.scrollData);v.Hooks.registered[B] && (g(y).rootPropertyValueCache[G] = v.Normalizations.registered[G] ? v.Normalizations.registered[G]('extract',null,I[1]) : I[1]),'transform' === I[0] && (z = !0);}}}j.mobileHA && g(y).transformCache.translate3d === d && (g(y).transformCache.translate3d = '(0px, 0px, 0px)',z = !0),z && v.flushTransformCache(y);}}j.display !== d && 'none' !== j.display && (t.State.calls[f][2].display = !1),j.visibility !== d && 'hidden' !== j.visibility && (t.State.calls[f][2].visibility = !1),j.progress && j.progress.call(h[1],h[1],r,Math.max(0,n + j.duration - b),n,q),1 === r && l(f);}}}t.State.isTicking && x(k);} function l(a,b) {if (!t.State.calls[a]) {return !1;} for (var c = t.State.calls[a][0],e = t.State.calls[a][1],f = t.State.calls[a][2],h = t.State.calls[a][4],i = !1,j = 0,k = c.length;k > j;j++) {var l = c[j].element;if (b || f.loop || ('none' === f.display && v.setPropertyValue(l,'display',f.display),'hidden' === f.visibility && v.setPropertyValue(l,'visibility',f.visibility)),f.loop !== !0 && (m.queue(l)[1] === d || !/\.velocityQueueEntryFlag/i.test(m.queue(l)[1])) && g(l)) {g(l).isAnimating = !1,g(l).rootPropertyValueCache = {};var n = !1;m.each(v.Lists.transforms3D,function(a,b) {let c = /^scale/.test(b) ? 1 : 0,e = g(l).transformCache[b];g(l).transformCache[b] !== d && new RegExp('^\\(' + c + '[^.]').test(e) && (n = !0,delete g(l).transformCache[b]);}),f.mobileHA && (n = !0,delete g(l).transformCache.translate3d),n && v.flushTransformCache(l),v.Values.removeClass(l,'velocity-animating');} if (!b && f.complete && !f.loop && j === k - 1) {try {f.complete.call(e,e);} catch (o) {setTimeout(function() {throw o;},1);}}h && f.loop !== !0 && h(e),g(l) && f.loop === !0 && !b && (m.each(g(l).tweensContainer,function(a,b) {/^rotate/.test(a) && 360 === parseFloat(b.endValue) && (b.endValue = 0,b.startValue = 360),/^backgroundPosition/.test(a) && 100 === parseFloat(b.endValue) && '%' === b.unitType && (b.endValue = 0,b.startValue = 100);}),t(l,'reverse',{loop: !0,delay: f.delay})),f.queue !== !1 && m.dequeue(l,f.queue);}t.State.calls[a] = !1;for (let p = 0,q = t.State.calls.length;q > p;p++) {if (t.State.calls[p] !== !1) {i = !0;break;}}i === !1 && (t.State.isTicking = !1,delete t.State.calls,t.State.calls = []);} var m,n = function() {if (c.documentMode) {return c.documentMode;} for (let a = 7;a > 4;a--) {let b = c.createElement('div');if (b.innerHTML = '<!--[if IE ' + a + ']><span></span><![endif]-->',b.getElementsByTagName('span').length) {return b = null,a;}} return d;}(),o = function() {let a = 0;return b.webkitRequestAnimationFrame || b.mozRequestAnimationFrame || function(b) {let c,d = (new Date).getTime();return c = Math.max(0,16 - (d - a)),a = d + c,setTimeout(function() {b(d + c);},c);};}(),p = {isString: function(a) {return 'string' === typeof a;},isArray: Array.isArray || function(a) {return '[object Array]' === Object.prototype.toString.call(a);},isFunction: function(a) {return '[object Function]' === Object.prototype.toString.call(a);},isNode: function(a) {return a && a.nodeType;},isNodeList: function(a) {return 'object' === typeof a && /^\[object (HTMLCollection|NodeList|Object)\]$/.test(Object.prototype.toString.call(a)) && a.length !== d && (0 === a.length || 'object' === typeof a[0] && a[0].nodeType > 0);},isWrapped: function(a) {return a && (a.jquery || b.Zepto && b.Zepto.zepto.isZ(a));},isSVG: function(a) {return b.SVGElement && a instanceof b.SVGElement;},isEmptyObject: function(a) {for (let b in a) {return !1;} return !0;}},q = !1;if (a.fn && a.fn.jquery ? (m = a,q = !0) : m = b.Velocity.Utilities,8 >= n && !q) {throw new Error('Velocity: IE8 and below require jQuery to be loaded before Velocity.');} if (7 >= n) {return void (jQuery.fn.velocity = jQuery.fn.animate);} var r = 400,s = 'swing',t = {State: {isMobile: /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent),isAndroid: /Android/i.test(navigator.userAgent),isGingerbread: /Android 2\.3\.[3-7]/i.test(navigator.userAgent),isChrome: b.chrome,isFirefox: /Firefox/i.test(navigator.userAgent),prefixElement: c.createElement('div'),prefixMatches: {},scrollAnchor: null,scrollPropertyLeft: null,scrollPropertyTop: null,isTicking: !1,calls: []},CSS: {},Utilities: m,Redirects: {},Easings: {},Promise: b.Promise,defaults: {queue: '',duration: r,easing: s,begin: d,complete: d,progress: d,display: d,visibility: d,loop: !1,delay: !1,mobileHA: !0,_cacheValues: !0},init: function(a) {m.data(a,'velocity',{isSVG: p.isSVG(a),isAnimating: !1,computedStyle: null,tweensContainer: null,rootPropertyValueCache: {},transformCache: {}});},hook: null,mock: !1,version: {major: 1,minor: 2,patch: 2},debug: !1};b.pageYOffset !== d ? (t.State.scrollAnchor = b,t.State.scrollPropertyLeft = 'pageXOffset',t.State.scrollPropertyTop = 'pageYOffset') : (t.State.scrollAnchor = c.documentElement || c.body.parentNode || c.body,t.State.scrollPropertyLeft = 'scrollLeft',t.State.scrollPropertyTop = 'scrollTop');var u = function() {function a(a) {return -a.tension * a.x - a.friction * a.v;} function b(b,c,d) {let e = {x: b.x + d.dx * c,v: b.v + d.dv * c,tension: b.tension,friction: b.friction};return {dx: e.v,dv: a(e)};} function c(c,d) {let e = {dx: c.v,dv: a(c)},f = b(c,.5 * d,e),g = b(c,.5 * d,f),h = b(c,d,g),i = 1 / 6 * (e.dx + 2 * (f.dx + g.dx) + h.dx),j = 1 / 6 * (e.dv + 2 * (f.dv + g.dv) + h.dv);return c.x = c.x + i * d,c.v = c.v + j * d,c;} return function d(a,b,e) {let f,g,h,i = {x: -1,v: 0,tension: null,friction: null},j = [0],k = 0,l = 1e-4,m = .016;for (a = parseFloat(a) || 500,b = parseFloat(b) || 20,e = e || null,i.tension = a,i.friction = b,f = null !== e,f ? (k = d(a,b),g = k / e * m) : g = m;;) {if (h = c(h || i,g),j.push(1 + h.x),k += 16,!(Math.abs(h.x) > l && Math.abs(h.v) > l)) {break;}} return f ? function(a) {return j[a * (j.length - 1) | 0];} : k;};}();t.Easings = {linear: function(a) {return a;},swing: function(a) {return.5 - Math.cos(a * Math.PI) / 2;},spring: function(a) {return 1 - Math.cos(4.5 * a * Math.PI) * Math.exp(6 * -a);}},m.each([['ease',[.25,.1,.25,1]],['ease-in',[.42,0,1,1]],['ease-out',[0,0,.58,1]],['ease-in-out',[.42,0,.58,1]],['easeInSine',[.47,0,.745,.715]],['easeOutSine',[.39,.575,.565,1]],['easeInOutSine',[.445,.05,.55,.95]],['easeInQuad',[.55,.085,.68,.53]],['easeOutQuad',[.25,.46,.45,.94]],['easeInOutQuad',[.455,.03,.515,.955]],['easeInCubic',[.55,.055,.675,.19]],['easeOutCubic',[.215,.61,.355,1]],['easeInOutCubic',[.645,.045,.355,1]],['easeInQuart',[.895,.03,.685,.22]],['easeOutQuart',[.165,.84,.44,1]],['easeInOutQuart',[.77,0,.175,1]],['easeInQuint',[.755,.05,.855,.06]],['easeOutQuint',[.23,1,.32,1]],['easeInOutQuint',[.86,0,.07,1]],['easeInExpo',[.95,.05,.795,.035]],['easeOutExpo',[.19,1,.22,1]],['easeInOutExpo',[1,0,0,1]],['easeInCirc',[.6,.04,.98,.335]],['easeOutCirc',[.075,.82,.165,1]],['easeInOutCirc',[.785,.135,.15,.86]]],function(a,b) {t.Easings[b[0]] = i.apply(null,b[1]);});var v = t.CSS = {RegEx: {isHex: /^#([A-f\d]{3}){1,2}$/i,valueUnwrap: /^[A-z]+\((.*)\)$/i,wrappedValueAlreadyExtracted: /[0-9.]+ [0-9.]+ [0-9.]+( [0-9.]+)?/,valueSplit: /([A-z]+\(.+\))|(([A-z0-9#-.]+?)(?=\s|$))/gi},Lists: {colors: ['fill','stroke','stopColor','color','backgroundColor','borderColor','borderTopColor','borderRightColor','borderBottomColor','borderLeftColor','outlineColor'],transformsBase: ['translateX','translateY','scale','scaleX','scaleY','skewX','skewY','rotateZ'],transforms3D: ['transformPerspective','translateZ','scaleZ','rotateX','rotateY']},Hooks: {templates: {textShadow: ['Color X Y Blur','black 0px 0px 0px'],boxShadow: ['Color X Y Blur Spread','black 0px 0px 0px 0px'],clip: ['Top Right Bottom Left','0px 0px 0px 0px'],backgroundPosition: ['X Y','0% 0%'],transformOrigin: ['X Y Z','50% 50% 0px'],perspectiveOrigin: ['X Y','50% 50%']},registered: {},register: function() {for (var a = 0;a < v.Lists.colors.length;a++) {let b = 'color' === v.Lists.colors[a] ? '0 0 0 1' : '255 255 255 1';v.Hooks.templates[v.Lists.colors[a]] = ['Red Green Blue Alpha',b];} let c,d,e;if (n) {for (c in v.Hooks.templates) {d = v.Hooks.templates[c],e = d[0].split(' ');let f = d[1].match(v.RegEx.valueSplit);'Color' === e[0] && (e.push(e.shift()),f.push(f.shift()),v.Hooks.templates[c] = [e.join(' '),f.join(' ')]);}} for (c in v.Hooks.templates) {d = v.Hooks.templates[c],e = d[0].split(' ');for (var a in e) {let g = c + e[a],h = a;v.Hooks.registered[g] = [c,h];}}},getRoot: function(a) {let b = v.Hooks.registered[a];return b ? b[0] : a;},cleanRootPropertyValue: function(a,b) {return v.RegEx.valueUnwrap.test(b) && (b = b.match(v.RegEx.valueUnwrap)[1]),v.Values.isCSSNullValue(b) && (b = v.Hooks.templates[a][1]),b;},extractValue: function(a,b) {let c = v.Hooks.registered[a];if (c) {let d = c[0],e = c[1];return b = v.Hooks.cleanRootPropertyValue(d,b),b.toString().match(v.RegEx.valueSplit)[e];} return b;},injectValue: function(a,b,c) {let d = v.Hooks.registered[a];if (d) {let e,f,g = d[0],h = d[1];return c = v.Hooks.cleanRootPropertyValue(g,c),e = c.toString().match(v.RegEx.valueSplit),e[h] = b,f = e.join(' ');} return c;}},Normalizations: {registered: {clip: function(a,b,c) {switch (a) {case 'name':return 'clip';case 'extract':var d;return v.RegEx.wrappedValueAlreadyExtracted.test(c) ? d = c : (d = c.toString().match(v.RegEx.valueUnwrap),d = d ? d[1].replace(/,(\s+)?/g,' ') : c),d;case 'inject':return 'rect(' + c + ')';}},blur: function(a,b,c) {switch (a) {case 'name':return t.State.isFirefox ? 'filter' : '-webkit-filter';case 'extract':var d = parseFloat(c);if (!d && 0 !== d) {let e = c.toString().match(/blur\(([0-9]+[A-z]+)\)/i);d = e ? e[1] : 0;} return d;case 'inject':return parseFloat(c) ? 'blur(' + c + ')' : 'none';}},opacity: function(a,b,c) {if (8 >= n) {switch (a) {case 'name':return 'filter';case 'extract':var d = c.toString().match(/alpha\(opacity=(.*)\)/i);return c = d ? d[1] / 100 : 1;case 'inject':return b.style.zoom = 1,parseFloat(c) >= 1 ? '' : 'alpha(opacity=' + parseInt(100 * parseFloat(c),10) + ')';}} else {switch (a) {case 'name':return 'opacity';case 'extract':return c;case 'inject':return c;}}}},register: function() {9 >= n || t.State.isGingerbread || (v.Lists.transformsBase = v.Lists.transformsBase.concat(v.Lists.transforms3D));for (var a = 0;a < v.Lists.transformsBase.length;a++) {!function() {let b = v.Lists.transformsBase[a];v.Normalizations.registered[b] = function(a,c,e) {switch (a) {case 'name':return 'transform';case 'extract':return g(c) === d || g(c).transformCache[b] === d ? /^scale/i.test(b) ? 1 : 0 : g(c).transformCache[b].replace(/[()]/g,'');case 'inject':var f = !1;switch (b.substr(0,b.length - 1)) {case 'translate':f = !/(%|px|em|rem|vw|vh|\d)$/i.test(e);break;case 'scal':case 'scale':t.State.isAndroid && g(c).transformCache[b] === d && 1 > e && (e = 1),f = !/(\d)$/i.test(e);break;case 'skew':f = !/(deg|\d)$/i.test(e);break;case 'rotate':f = !/(deg|\d)$/i.test(e);} return f || (g(c).transformCache[b] = '(' + e + ')'),g(c).transformCache[b];}};}();} for (var a = 0;a < v.Lists.colors.length;a++) {!function() {let b = v.Lists.colors[a];v.Normalizations.registered[b] = function(a,c,e) {switch (a) {case 'name':return b;case 'extract':var f;if (v.RegEx.wrappedValueAlreadyExtracted.test(e)) {f = e;} else {let g,h = {black: 'rgb(0, 0, 0)',blue: 'rgb(0, 0, 255)',gray: 'rgb(128, 128, 128)',green: 'rgb(0, 128, 0)',red: 'rgb(255, 0, 0)',white: 'rgb(255, 255, 255)'};/^[A-z]+$/i.test(e) ? g = h[e] !== d ? h[e] : h.black : v.RegEx.isHex.test(e) ? g = 'rgb(' + v.Values.hexToRgb(e).join(' ') + ')' : /^rgba?\(/i.test(e) || (g = h.black),f = (g || e).toString().match(v.RegEx.valueUnwrap)[1].replace(/,(\s+)?/g,' ');} return 8 >= n || 3 !== f.split(' ').length || (f += ' 1'),f;case 'inject':return 8 >= n ? 4 === e.split(' ').length && (e = e.split(/\s+/).slice(0,3).join(' ')) : 3 === e.split(' ').length && (e += ' 1'),(8 >= n ? 'rgb' : 'rgba') + '(' + e.replace(/\s+/g,',').replace(/\.(\d)+(?=,)/g,'') + ')';}};}();}}},Names: {camelCase: function(a) {return a.replace(/-(\w)/g,function(a,b) {return b.toUpperCase();});},SVGAttribute: function(a) {let b = 'width|height|x|y|cx|cy|r|rx|ry|x1|x2|y1|y2';return (n || t.State.isAndroid && !t.State.isChrome) && (b += '|transform'),new RegExp('^(' + b + ')$','i').test(a);},prefixCheck: function(a) {if (t.State.prefixMatches[a]) {return [t.State.prefixMatches[a],!0];} for (let b = ['','Webkit','Moz','ms','O'],c = 0,d = b.length;d > c;c++) {var e;if (e = 0 === c ? a : b[c] + a.replace(/^\w/,function(a) {return a.toUpperCase();}),p.isString(t.State.prefixElement.style[e])) {return t.State.prefixMatches[a] = e,[e,!0];}} return [a,!1];}},Values: {hexToRgb: function(a) {let b,c = /^#?([a-f\d])([a-f\d])([a-f\d])$/i,d = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i;return a = a.replace(c,function(a,b,c,d) {return b + b + c + c + d + d;}),b = d.exec(a),b ? [parseInt(b[1],16),parseInt(b[2],16),parseInt(b[3],16)] : [0,0,0];},isCSSNullValue: function(a) {return 0 == a || /^(none|auto|transparent|(rgba\(0, ?0, ?0, ?0\)))$/i.test(a);},getUnitType: function(a) {return /^(rotate|skew)/i.test(a) ? 'deg' : /(^(scale|scaleX|scaleY|scaleZ|alpha|flexGrow|flexHeight|zIndex|fontWeight)$)|((opacity|red|green|blue|alpha)$)/i.test(a) ? '' : 'px';},getDisplayType: function(a) {let b = a && a.tagName.toString().toLowerCase();return /^(b|big|i|small|tt|abbr|acronym|cite|code|dfn|em|kbd|strong|samp|var|a|bdo|br|img|map|object|q|script|span|sub|sup|button|input|label|select|textarea)$/i.test(b) ? 'inline' : /^(li)$/i.test(b) ? 'list-item' : /^(tr)$/i.test(b) ? 'table-row' : /^(table)$/i.test(b) ? 'table' : /^(tbody)$/i.test(b) ? 'table-row-group' : 'block';},addClass: function(a,b) {a.classList ? a.classList.add(b) : a.className += (a.className.length ? ' ' : '') + b;},removeClass: function(a,b) {a.classList ? a.classList.remove(b) : a.className = a.className.toString().replace(new RegExp('(^|\\s)' + b.split(' ').join('|') + '(\\s|$)','gi'),' ');}},getPropertyValue: function(a,c,e,f) {function h(a,c) {function e() {j && v.setPropertyValue(a,'display','none');} let i = 0;if (8 >= n) {i = m.css(a,c);} else {var j = !1;if (/^(width|height)$/.test(c) && 0 === v.getPropertyValue(a,'display') && (j = !0,v.setPropertyValue(a,'display',v.Values.getDisplayType(a))),!f) {if ('height' === c && 'border-box' !== v.getPropertyValue(a,'boxSizing').toString().toLowerCase()) {let k = a.offsetHeight - (parseFloat(v.getPropertyValue(a,'borderTopWidth')) || 0) - (parseFloat(v.getPropertyValue(a,'borderBottomWidth')) || 0) - (parseFloat(v.getPropertyValue(a,'paddingTop')) || 0) - (parseFloat(v.getPropertyValue(a,'paddingBottom')) || 0);return e(),k;} if ('width' === c && 'border-box' !== v.getPropertyValue(a,'boxSizing').toString().toLowerCase()) {let l = a.offsetWidth - (parseFloat(v.getPropertyValue(a,'borderLeftWidth')) || 0) - (parseFloat(v.getPropertyValue(a,'borderRightWidth')) || 0) - (parseFloat(v.getPropertyValue(a,'paddingLeft')) || 0) - (parseFloat(v.getPropertyValue(a,'paddingRight')) || 0);return e(),l;}} let o;o = g(a) === d ? b.getComputedStyle(a,null) : g(a).computedStyle ? g(a).computedStyle : g(a).computedStyle = b.getComputedStyle(a,null),'borderColor' === c && (c = 'borderTopColor'),i = 9 === n && 'filter' === c ? o.getPropertyValue(c) : o[c],('' === i || null === i) && (i = a.style[c]),e();} if ('auto' === i && /^(top|right|bottom|left)$/i.test(c)) {let p = h(a,'position');('fixed' === p || 'absolute' === p && /top|left/i.test(c)) && (i = m(a).position()[c] + 'px');} return i;} let i;if (v.Hooks.registered[c]) {let j = c,k = v.Hooks.getRoot(j);e === d && (e = v.getPropertyValue(a,v.Names.prefixCheck(k)[0])),v.Normalizations.registered[k] && (e = v.Normalizations.registered[k]('extract',a,e)),i = v.Hooks.extractValue(j,e);} else if (v.Normalizations.registered[c]) {let l,o;l = v.Normalizations.registered[c]('name',a),'transform' !== l && (o = h(a,v.Names.prefixCheck(l)[0]),v.Values.isCSSNullValue(o) && v.Hooks.templates[c] && (o = v.Hooks.templates[c][1])),i = v.Normalizations.registered[c]('extract',a,o);} if (!/^[\d-]/.test(i)) {if (g(a) && g(a).isSVG && v.Names.SVGAttribute(c)) {if (/^(height|width)$/i.test(c)) {try {i = a.getBBox()[c];} catch (p) {i = 0;}} else {i = a.getAttribute(c);}} else {i = h(a,v.Names.prefixCheck(c)[0]);}} return v.Values.isCSSNullValue(i) && (i = 0),t.debug >= 2 && console.log('Get ' + c + ': ' + i),i;},setPropertyValue: function(a,c,d,e,f) {let h = c;if ('scroll' === c) {f.container ? f.container['scroll' + f.direction] = d : 'Left' === f.direction ? b.scrollTo(d,f.alternateValue) : b.scrollTo(f.alternateValue,d);} else if (v.Normalizations.registered[c] && 'transform' === v.Normalizations.registered[c]('name',a)) {v.Normalizations.registered[c]('inject',a,d),h = 'transform',d = g(a).transformCache[c];} else {if (v.Hooks.registered[c]) {let i = c,j = v.Hooks.getRoot(c);e = e || v.getPropertyValue(a,j),d = v.Hooks.injectValue(i,d,e),c = j;} if (v.Normalizations.registered[c] && (d = v.Normalizations.registered[c]('inject',a,d),c = v.Normalizations.registered[c]('name',a)),h = v.Names.prefixCheck(c)[0],8 >= n) {try {a.style[h] = d;} catch (k) {t.debug && console.log('Browser does not support [' + d + '] for [' + h + ']');}} else {g(a) && g(a).isSVG && v.Names.SVGAttribute(c) ? a.setAttribute(c,d) : a.style[h] = d;}t.debug >= 2 && console.log('Set ' + c + ' (' + h + '): ' + d);} return [h,d];},flushTransformCache: function(a) {function b(b) {return parseFloat(v.getPropertyValue(a,b));} let c = '';if ((n || t.State.isAndroid && !t.State.isChrome) && g(a).isSVG) {let d = {translate: [b('translateX'),b('translateY')],skewX: [b('skewX')],skewY: [b('skewY')],scale: 1 !== b('scale') ? [b('scale'),b('scale')] : [b('scaleX'),b('scaleY')],rotate: [b('rotateZ'),0,0]};m.each(g(a).transformCache,function(a) {/^translate/i.test(a) ? a = 'translate' : /^scale/i.test(a) ? a = 'scale' : /^rotate/i.test(a) && (a = 'rotate'),d[a] && (c += a + '(' + d[a].join(' ') + ') ',delete d[a]);});} else {let e,f;m.each(g(a).transformCache,function(b) {return e = g(a).transformCache[b],'transformPerspective' === b ? (f = e,!0) : (9 === n && 'rotateZ' === b && (b = 'rotate'),void (c += b + e + ' '));}),f && (c = 'perspective' + f + ' ' + c);}v.setPropertyValue(a,'transform',c);}};v.Hooks.register(),v.Normalizations.register(),t.hook = function(a,b,c) {let e = d;return a = f(a),m.each(a,function(a,f) {if (g(f) === d && t.init(f),c === d) {e === d && (e = t.CSS.getPropertyValue(f,b));} else {let h = t.CSS.setPropertyValue(f,b,c);'transform' === h[0] && t.CSS.flushTransformCache(f),e = h;}}),e;};var w = function() {function a() {return h ? B.promise || null : i;} function e() {function a() {function a(a,b) {let c = d,e = d,g = d;return p.isArray(a) ? (c = a[0],!p.isArray(a[1]) && /^[\d-]/.test(a[1]) || p.isFunction(a[1]) || v.RegEx.isHex.test(a[1]) ? g = a[1] : (p.isString(a[1]) && !v.RegEx.isHex.test(a[1]) || p.isArray(a[1])) && (e = b ? a[1] : j(a[1],h.duration),a[2] !== d && (g = a[2]))) : c = a,b || (e = e || h.easing),p.isFunction(c) && (c = c.call(f,y,x)),p.isFunction(g) && (g = g.call(f,y,x)),[c || 0,e,g];} function l(a,b) {let c,d;return d = (b || '0').toString().toLowerCase().replace(/[%A-z]+$/,function(a) {return c = a,'';}),c || (c = v.Values.getUnitType(a)),[d,c];} function n() {let a = {myParent: f.parentNode || c.body,position: v.getPropertyValue(f,'position'),fontSize: v.getPropertyValue(f,'fontSize')},d = a.position === I.lastPosition && a.myParent === I.lastParent,e = a.fontSize === I.lastFontSize;I.lastParent = a.myParent,I.lastPosition = a.position,I.lastFontSize = a.fontSize;let h = 100,i = {};if (e && d) {i.emToPx = I.lastEmToPx,i.percentToPxWidth = I.lastPercentToPxWidth,i.percentToPxHeight = I.lastPercentToPxHeight;} else {let j = g(f).isSVG ? c.createElementNS('http://www.w3.org/2000/svg','rect') : c.createElement('div');t.init(j),a.myParent.appendChild(j),m.each(['overflow','overflowX','overflowY'],function(a,b) {t.CSS.setPropertyValue(j,b,'hidden');}),t.CSS.setPropertyValue(j,'position',a.position),t.CSS.setPropertyValue(j,'fontSize',a.fontSize),t.CSS.setPropertyValue(j,'boxSizing','content-box'),m.each(['minWidth','maxWidth','width','minHeight','maxHeight','height'],function(a,b) {t.CSS.setPropertyValue(j,b,h + '%');}),t.CSS.setPropertyValue(j,'paddingLeft',h + 'em'),i.percentToPxWidth = I.lastPercentToPxWidth = (parseFloat(v.getPropertyValue(j,'width',null,!0)) || 1) / h,i.percentToPxHeight = I.lastPercentToPxHeight = (parseFloat(v.getPropertyValue(j,'height',null,!0)) || 1) / h,i.emToPx = I.lastEmToPx = (parseFloat(v.getPropertyValue(j,'paddingLeft')) || 1) / h,a.myParent.removeChild(j);} return null === I.remToPx && (I.remToPx = parseFloat(v.getPropertyValue(c.body,'fontSize')) || 16),null === I.vwToPx && (I.vwToPx = parseFloat(b.innerWidth) / 100,I.vhToPx = parseFloat(b.innerHeight) / 100),i.remToPx = I.remToPx,i.vwToPx = I.vwToPx,i.vhToPx = I.vhToPx,t.debug >= 1 && console.log('Unit ratios: ' + JSON.stringify(i),f),i;} if (h.begin && 0 === y) {try {h.begin.call(o,o);} catch (r) {setTimeout(function() {throw r;},1);}} if ('scroll' === C) {let u,w,z,A = /^x$/i.test(h.axis) ? 'Left' : 'Top',D = parseFloat(h.offset) || 0;h.container ? p.isWrapped(h.container) || p.isNode(h.container) ? (h.container = h.container[0] || h.container,u = h.container['scroll' + A],z = u + m(f).position()[A.toLowerCase()] + D) : h.container = null : (u = t.State.scrollAnchor[t.State['scrollProperty' + A]],w = t.State.scrollAnchor[t.State['scrollProperty' + ('Left' === A ? 'Top' : 'Left')]],z = m(f).offset()[A.toLowerCase()] + D),i = {scroll: {rootPropertyValue: !1,startValue: u,currentValue: u,endValue: z,unitType: '',easing: h.easing,scrollData: {container: h.container,direction: A,alternateValue: w}},element: f},t.debug && console.log('tweensContainer (scroll): ',i.scroll,f);} else if ('reverse' === C) {if (!g(f).tweensContainer) {return void m.dequeue(f,h.queue);}'none' === g(f).opts.display && (g(f).opts.display = 'auto'),'hidden' === g(f).opts.visibility && (g(f).opts.visibility = 'visible'),g(f).opts.loop = !1,g(f).opts.begin = null,g(f).opts.complete = null,s.easing || delete h.easing,s.duration || delete h.duration,h = m.extend({},g(f).opts,h);var E = m.extend(!0,{},g(f).tweensContainer);for (let F in E) {if ('element' !== F) {let G = E[F].startValue;E[F].startValue = E[F].currentValue = E[F].endValue,E[F].endValue = G,p.isEmptyObject(s) || (E[F].easing = h.easing),t.debug && console.log('reverse tweensContainer (' + F + '): ' + JSON.stringify(E[F]),f);}}i = E;} else if ('start' === C) {var E;g(f).tweensContainer && g(f).isAnimating === !0 && (E = g(f).tweensContainer),m.each(q,function(b,c) {if (RegExp('^' + v.Lists.colors.join('$|^') + '$').test(b)) {let e = a(c,!0),f = e[0],g = e[1],h = e[2];if (v.RegEx.isHex.test(f)) {for (let i = ['Red','Green','Blue'],j = v.Values.hexToRgb(f),k = h ? v.Values.hexToRgb(h) : d,l = 0;l < i.length;l++) {let m = [j[l]];g && m.push(g),k !== d && m.push(k[l]),q[b + i[l]] = m;} delete q[b];}}});for (let H in q) {let K = a(q[H]),L = K[0],M = K[1],N = K[2];H = v.Names.camelCase(H);let O = v.Hooks.getRoot(H),P = !1;if (g(f).isSVG || 'tween' === O || v.Names.prefixCheck(O)[1] !== !1 || v.Normalizations.registered[O] !== d) {(h.display !== d && null !== h.display && 'none' !== h.display || h.visibility !== d && 'hidden' !== h.visibility) && /opacity|filter/.test(H) && !N && 0 !== L && (N = 0),h._cacheValues && E && E[H] ? (N === d && (N = E[H].endValue + E[H].unitType),P = g(f).rootPropertyValueCache[O]) : v.Hooks.registered[H] ? N === d ? (P = v.getPropertyValue(f,O),N = v.getPropertyValue(f,H,P)) : P = v.Hooks.templates[O][1] : N === d && (N = v.getPropertyValue(f,H));var Q,R,S,T = !1;if (Q = l(H,N),N = Q[0],S = Q[1],Q = l(H,L),L = Q[0].replace(/^([+-\/*])=/,function(a,b) {return T = b,'';}),R = Q[1],N = parseFloat(N) || 0,L = parseFloat(L) || 0,'%' === R && (/^(fontSize|lineHeight)$/.test(H) ? (L /= 100,R = 'em') : /^scale/.test(H) ? (L /= 100,R = '') : /(Red|Green|Blue)$/i.test(H) && (L = L / 100 * 255,R = '')),/[\/*]/.test(T)) {R = S;} else if (S !== R && 0 !== N) {if (0 === L) {R = S;} else {e = e || n();let U = /margin|padding|left|right|width|text|word|letter/i.test(H) || /X$/.test(H) || 'x' === H ? 'x' : 'y';switch (S) {case '%':N *= 'x' === U ? e.percentToPxWidth : e.percentToPxHeight;break;case 'px':break;default:N *= e[S + 'ToPx'];} switch (R) {case '%':N *= 1 / ('x' === U ? e.percentToPxWidth : e.percentToPxHeight);break;case 'px':break;default:N *= 1 / e[R + 'ToPx'];}}} switch (T) {case '+':L = N + L;break;case '-':L = N - L;break;case '*':L = N * L;break;case '/':L = N / L;}i[H] = {rootPropertyValue: P,startValue: N,currentValue: N,endValue: L,unitType: R,easing: M},t.debug && console.log('tweensContainer (' + H + '): ' + JSON.stringify(i[H]),f);} else {t.debug && console.log('Skipping [' + O + '] due to a lack of browser support.');}}i.element = f;}i.element && (v.Values.addClass(f,'velocity-animating'),J.push(i),'' === h.queue && (g(f).tweensContainer = i,g(f).opts = h),g(f).isAnimating = !0,y === x - 1 ? (t.State.calls.push([J,o,h,null,B.resolver]),t.State.isTicking === !1 && (t.State.isTicking = !0,k())) : y++);} var e,f = this,h = m.extend({},t.defaults,s),i = {};switch (g(f) === d && t.init(f),parseFloat(h.delay) && h.queue !== !1 && m.queue(f,h.queue,function(a) {t.velocityQueueEntryFlag = !0,g(f).delayTimer = {setTimeout: setTimeout(a,parseFloat(h.delay)),next: a};}),h.duration.toString().toLowerCase()) {case 'fast':h.duration = 200;break;case 'normal':h.duration = r;break;case 'slow':h.duration = 600;break;default:h.duration = parseFloat(h.duration) || 1;}t.mock !== !1 && (t.mock === !0 ? h.duration = h.delay = 1 : (h.duration *= parseFloat(t.mock) || 1,h.delay *= parseFloat(t.mock) || 1)),h.easing = j(h.easing,h.duration),h.begin && !p.isFunction(h.begin) && (h.begin = null),h.progress && !p.isFunction(h.progress) && (h.progress = null),h.complete && !p.isFunction(h.complete) && (h.complete = null),h.display !== d && null !== h.display && (h.display = h.display.toString().toLowerCase(),'auto' === h.display && (h.display = t.CSS.Values.getDisplayType(f))),h.visibility !== d && null !== h.visibility && (h.visibility = h.visibility.toString().toLowerCase()),h.mobileHA = h.mobileHA && t.State.isMobile && !t.State.isGingerbread,h.queue === !1 ? h.delay ? setTimeout(a,h.delay) : a() : m.queue(f,h.queue,function(b,c) {return c === !0 ? (B.promise && B.resolver(o),!0) : (t.velocityQueueEntryFlag = !0,void a(b));}),'' !== h.queue && 'fx' !== h.queue || 'inprogress' === m.queue(f)[0] || m.dequeue(f);} let h,i,n,o,q,s,u = arguments[0] && (arguments[0].p || m.isPlainObject(arguments[0].properties) && !arguments[0].properties.names || p.isString(arguments[0].properties));if (p.isWrapped(this) ? (h = !1,n = 0,o = this,i = this) : (h = !0,n = 1,o = u ? arguments[0].elements || arguments[0].e : arguments[0]),o = f(o)) {u ? (q = arguments[0].properties || arguments[0].p,s = arguments[0].options || arguments[0].o) : (q = arguments[n],s = arguments[n + 1]);var x = o.length,y = 0;if (!/^(stop|finish|finishAll)$/i.test(q) && !m.isPlainObject(s)) {let z = n + 1;s = {};for (let A = z;A < arguments.length;A++) {p.isArray(arguments[A]) || !/^(fast|normal|slow)$/i.test(arguments[A]) && !/^\d/.test(arguments[A]) ? p.isString(arguments[A]) || p.isArray(arguments[A]) ? s.easing = arguments[A] : p.isFunction(arguments[A]) && (s.complete = arguments[A]) : s.duration = arguments[A];}} var B = {promise: null,resolver: null,rejecter: null};h && t.Promise && (B.promise = new t.Promise(function(a,b) {B.resolver = a,B.rejecter = b;}));var C;switch (q) {case 'scroll':C = 'scroll';break;case 'reverse':C = 'reverse';break;case 'finish':case 'finishAll':case 'stop':m.each(o,function(a,b) {g(b) && g(b).delayTimer && (clearTimeout(g(b).delayTimer.setTimeout),g(b).delayTimer.next && g(b).delayTimer.next(),delete g(b).delayTimer),'finishAll' !== q || s !== !0 && !p.isString(s) || (m.each(m.queue(b,p.isString(s) ? s : ''),function(a,b) {p.isFunction(b) && b();}),m.queue(b,p.isString(s) ? s : '',[]));});var D = [];return m.each(t.State.calls,function(a,b) {b && m.each(b[1],function(c,e) {let f = s === d ? '' : s;return f === !0 || b[2].queue === f || s === d && b[2].queue === !1 ? void m.each(o,function(c,d) {d === e && ((s === !0 || p.isString(s)) && (m.each(m.queue(d,p.isString(s) ? s : ''),function(a,b) {p.isFunction(b) && b(null,!0);
    }),m.queue(d,p.isString(s) ? s : '',[])),'stop' === q ? (g(d) && g(d).tweensContainer && f !== !1 && m.each(g(d).tweensContainer,function(a,b) {b.endValue = b.currentValue;}),D.push(a)) : ('finish' === q || 'finishAll' === q) && (b[2].duration = 1));}) : !0;});}),'stop' === q && (m.each(D,function(a,b) {l(b,!0);}),B.promise && B.resolver(o)),a();default:if (!m.isPlainObject(q) || p.isEmptyObject(q)) {if (p.isString(q) && t.Redirects[q]) {var E = m.extend({},s),F = E.duration,G = E.delay || 0;return E.backwards === !0 && (o = m.extend(!0,[],o).reverse()),m.each(o,function(a,b) {parseFloat(E.stagger) ? E.delay = G + parseFloat(E.stagger) * a : p.isFunction(E.stagger) && (E.delay = G + E.stagger.call(b,a,x)),E.drag && (E.duration = parseFloat(F) || (/^(callout|transition)/.test(q) ? 1e3 : r),E.duration = Math.max(E.duration * (E.backwards ? 1 - a / x : (a + 1) / x),.75 * E.duration,200)),t.Redirects[q].call(b,b,E || {},a,x,o,B.promise ? B : d);}),a();} let H = 'Velocity: First argument (' + q + ') was not a property map, a known action, or a registered redirect. Aborting.';return B.promise ? B.rejecter(new Error(H)) : console.log(H),a();}C = 'start';} var I = {lastParent: null,lastPosition: null,lastFontSize: null,lastPercentToPxWidth: null,lastPercentToPxHeight: null,lastEmToPx: null,remToPx: null,vwToPx: null,vhToPx: null},J = [];m.each(o,function(a,b) {p.isNode(b) && e.call(b);});var K,E = m.extend({},t.defaults,s);if (E.loop = parseInt(E.loop),K = 2 * E.loop - 1,E.loop) {for (let L = 0;K > L;L++) {let M = {delay: E.delay,progress: E.progress};L === K - 1 && (M.display = E.display,M.visibility = E.visibility,M.complete = E.complete),w(o,'reverse',M);}} return a();}};t = m.extend(w,t),t.animate = w;var x = b.requestAnimationFrame || o;return t.State.isMobile || c.hidden === d || c.addEventListener('visibilitychange',function() {c.hidden ? (x = function(a) {return setTimeout(function() {a(!0);},16);},k()) : x = b.requestAnimationFrame || o;}),a.Velocity = t,a !== b && (a.fn.velocity = w,a.fn.velocity.defaults = t.defaults),m.each(['Down','Up'],function(a,b) {t.Redirects['slide' + b] = function(a,c,e,f,g,h) {let i = m.extend({},c),j = i.begin,k = i.complete,l = {height: '',marginTop: '',marginBottom: '',paddingTop: '',paddingBottom: ''},n = {};i.display === d && (i.display = 'Down' === b ? 'inline' === t.CSS.Values.getDisplayType(a) ? 'inline-block' : 'block' : 'none'),i.begin = function() {j && j.call(g,g);for (let c in l) {n[c] = a.style[c];let d = t.CSS.getPropertyValue(a,c);l[c] = 'Down' === b ? [d,0] : [0,d];}n.overflow = a.style.overflow,a.style.overflow = 'hidden';},i.complete = function() {for (let b in n) {a.style[b] = n[b];}k && k.call(g,g),h && h.resolver(g);},t(a,l,i);};}),m.each(['In','Out'],function(a,b) {t.Redirects['fade' + b] = function(a,c,e,f,g,h) {let i = m.extend({},c),j = {opacity: 'In' === b ? 1 : 0},k = i.complete;i.complete = e !== f - 1 ? i.begin = null : function() {k && k.call(g,g),h && h.resolver(g);},i.display === d && (i.display = 'In' === b ? 'auto' : 'none'),t(this,j,i);};}),t;}(window.jQuery || window.Zepto || window,window,document);});
  },{}],8: [function(require,module,exports) {
    'use strict';
    module.exports = (function() {
      let video = $('#js-video');
      let thermometerElement = null;
      let room = video.data('room');
      let garage = room === 'garage';
      let livingroom = room === 'livingroom';

      function detectIE() {
        let ua = window.navigator.userAgent;
        let msie = ua.indexOf('MSIE ');
        let edge = ua.indexOf('Edge/');
        let trident = ua.indexOf('Trident/');
        if (msie > 0 || trident > 0 || edge > 0) {
          return true;
        }
        return false;
      }


      function alterBackgroundFilter(value) {
        if (detectIE() === false) { // if not IE
          if (livingroom) {
            video.css({
              filter: 'saturate(' + value + ')',
              '-webkit-filter': 'saturate(' + value + ')'
            });
          } else if (garage) {
            video.css({
              filter: 'hue-rotate(' + value + 'deg)',
              '-webkit-filter': 'hue-rotate(' + value + 'deg)'
            });
          }
        }
      }


      function alterFilterWithThermometer() {
	  thermometerElement.slider({
      orientation: "vertical",
      range: "min",
      min: 0,
      max: 100,
      value: 15, //sets the default height of the mercury in the thermometer
      slide: function(e, ui) {
        $(".ui-slider-handle").html;
        var temp = ui.value;
        if (livingroom) {
          return temp >= 0 && temp < 25 ? alterBackgroundFilter(1) :
           temp >= 25 && temp < 50 ? alterBackgroundFilter(2) :
           temp >= 50 && temp < 75 ? alterBackgroundFilter(3) :
           alterBackgroundFilter(4);
        } else if (garage) {
           return temp >= 0 && temp < 25 ? alterBackgroundFilter(15) :
           temp >= 25 && temp < 50 ? alterBackgroundFilter(25) :
           temp >= 50 && temp < 75 ? alterBackgroundFilter(40) :
           alterBackgroundFilter(55);
        }
      }
    });
  }

      function useThermometer(thermometer) {
        thermometerElement = thermometer;
        alterFilterWithThermometer();
      }

      return { useThermometer: useThermometer,
        detectIE: detectIE
      };

    })();

  },{}],9: [function(require,module,exports) {
    'use strict';
    module.exports = (function() {
      let velocity = require('./lib/velocity.min.js');
      let bedroomData = require('./../data/bedroom.json');
      let garageData = require('./../data/garage.json');
      let livingroomData = require('./../data/livingroom.json');
      let draggable = require('./draggableSwitch.js');
      let thermo = require('./thermometer.js');

      let video = $('#js-video'),
        overlay = $('#js-overlay-container'),
        overlayBody = $('#js-overlay--body'),
        overlayMessage = $('#js-overlay--message'),
        letThereBeLightImg = $('.js-let-there-be-light-img'),
        messageHeadingContainer = $('#js-message-heading-container'),
        messageIcons = $('.js-overlay--message-icons'),
        callToAction = $('#js-overlay--message-cta'),
        callToActionText = $('.js-overlay--message-cta-text'),
        messageHeading = $('.js-message-heading'),
        messageParagraph = $('#js-message-paragraph'),
        overlayMessageBorder = $('#js-overlay--message-border'),
        overlayMessageContainer = $('.js-overlay--message-container'),
        standbyButtonOn = $('.js-standby-button-on'),
        keepingTrackIphone = $('.js-keeping-track-iphone'),
        drggableSwitchContainer = $('#js-draggable-switch-container'),
        draggableSwitch = $('.js-draggable-switch'),
        switchArrows = $('.js-switch-arrow'),
        peaceOfMindAuth = $('.js-peace-of-mind-authority'),
        peaceOfMindIpad = $('.js-peace-of-mind-ipad'),
        lifestyleIpad = $('.js-lifestyle-ipad'),
        wirelssSignal = $('.js-wireless-signal'),
        thermometer = $('.thermometer-container'),
        dimmerSwitch = $('.js-dimmer-switch'),
        nextVideoNav = $('.js-video-navigation-next'),
        prevVideoNav = $('.js-video-navigation-prev'),
        fireAlarm = $('.js-fire-alarm'),
        whiteLoader = $('.js-white--loader'),
        JSONdata,
        specialCurrentTime,
        autoRedirectIfNoAction;


    // The core function, it adds an event to the video and loops through the json data for various manipulations
      function showOverlay(data) {
        JSONdata = data;

        video.on('timeupdate', function() {
      // currentTime has to be whole number as decimal points are fired at random so can't pause at a specific decimal
          let currentTime = Math.round(video[0].currentTime);
          let duration = Math.round(video[0].duration);
          let numberOfScenes = data.scenes.length;

          whiteLoader.velocity({backgroundColorAlpha: 0}, {duration: 3000});
          for (var i = 0; i < numberOfScenes; i++) {


            let actions = function() {
              data.scenes[i].hasBeenPaused = true;
              overlay.velocity({opacity: 1}, {visibility: 'visible', duration: 1000});
              overlayMessage.css('display', 'block');
              updateOverlayMessageContent(data.scenes[i].heading, data.scenes[i].paragraph, data.scenes[i].button, data.scenes[i].href);
              showMediaOrInteractiveElement(data.scenes[i].interactiveElementFunction);
              animateMessageOverlay();
              moveToScene();
              lastScene(i, numberOfScenes, data.verticalPageRedirect, currentTime, duration);
            };

            if (currentTime === data.scenes[i].specialPauseTime && data.scenes[i].hasBeenPaused === false) {
              actions();
            }

            if (currentTime === data.scenes[i].pauseTime && data.scenes[i].hasBeenPaused === false) {
              addSpecificOverlayId(data.scenes[i].id,i);
              hidePrevAtFirstScene(i);

              if (specialSceneIs('livingroom-overlay__2')) {
                specialOverlays(6100);

              } else if (specialSceneIs('bedroom-overlay__4')) {
                specialOverlays(9100);

              } else {
                video.get(0).pause();
              }

              actions();
            }
          }
        });
      }

      function moveToPreviousOverlay(videoCurrentTime) {
        let now;
        let time = specialCurrentTime || videoCurrentTime;

        for (let i = 0; i < JSONdata.scenes.length; i++) {
          if (time === JSONdata.scenes[i].pauseTime) {
            JSONdata.scenes[i].hasBeenPaused = false;

            let previous = i - 1;
            if (previous >= 0) {
              JSONdata.scenes[previous].hasBeenPaused = false;
              now = JSONdata.scenes[previous].specialPauseTime || JSONdata.scenes[previous].pauseTime;
              video.get(0).currentTime = now;
            }
          }
        }
      }


      function specialSceneIs(scene) {
        return overlayMessageContainer.attr('id').indexOf(scene) > -1;
      }

  // These are special scenes where the overlay is visible while video is playing
  // TV section of livingroom (2nd), Shower in bedroom (4th), 'let there be light' (5th) in livingroom
      function specialOverlays(durationOverlayIsSeen) {
        specialCurrentTime = Math.round(video[0].currentTime);
        setTimeout(function() {
          video.get(0).pause();
        }, durationOverlayIsSeen);
      }


  // if there is an image or an interactive element next to overlay message, show it
      function showMediaOrInteractiveElement(func) {
        if (func !== null) {
          eval(func);
        }
      }


  // if there is an image or an interactive element next to overlay message, hide it before the next scene is rendered
      function hideMediaOrInteractiveElement() {
        let noninteractiveMedia = [
          keepingTrackIphone,
          standbyButtonOn,
          peaceOfMindAuth,
          peaceOfMindIpad,
          lifestyleIpad,
          wirelssSignal,
          thermometer,
          dimmerSwitch,
          fireAlarm,
          letThereBeLightImg
        ];

        returnDraggableSwitchToInitialState();
    // for the water steam (condensation effect)
        overlayMessage.css('background-image', 'none').attr('id', null);
    // for Let there be light in the living room (#5)

    // thermometer background colour change, if not IE, filter is not supported in IE
        let videoStyle = video[0].style;
        if (thermo.detectIE() === false && videoStyle.filter !== null) {
          videoStyle.filter = null;
          videoStyle.setProperty('-webkit-filter', null);
        }

        return noninteractiveMedia.forEach(function(media) {
          media.css('visibility', 'hidden');
        });
      }

  // After leaving the draggableSwitch secene (bedroom, 3rd), hide the switch, return the position of the switch to the center (initial position) and make visible any arrows that may have been hidden by the switch being over them
      function returnDraggableSwitchToInitialState() {
        if (drggableSwitchContainer.length) {
          drggableSwitchContainer.css('display', 'none');
    // returns the switch to original position after clicking next
          draggableSwitch.animate({
            left: draggableSwitch.data('left'),
            top: draggableSwitch.data('top')
          });
          draggableSwitch.data('left', draggableSwitch.position().left).data('top', draggableSwitch.position().top);
      // if any arow(s) is hidden due to the switch being over it, make it visible when returning to the scene
          switchArrows.css('visibility', 'visible');
        }
      }

  // being called in JSON files
      function showNoniteractiveMedia(media) {
        media.css('visibility', 'visible');
        $.Velocity.hook(media, 'opacity', 0);
        media.velocity({ opacity: 1}, { delay: 1600, duration: 1400});
        if (media === dimmerSwitch) {
          letThereBeLight();
        }
      }

  // being called in JSON file (bedroom.json)
      function waterSteamBackground(img) {
        overlayMessage.attr('id', 'overlay--message__steam');
      }

  // being called in JSON file (garage.json)
      function fireAlarmOverlay() {
        fireAlarm.css('background', 'radial-gradient(rgba(255, 0, 0, 0.701961), rgba(255, 0, 0, 0.2))');
        $.Velocity.hook(fireAlarm, 'opacity', 0);
        $.Velocity.hook(fireAlarm, 'visibility', 'hidden');
        fireAlarm.velocity({ opacity: 1}, { visibility: 'visible', duration: 800, loop: true });
      }


      function animateMessageOverlay() {
        let customBezierCurve = [0.33, 0.69, 0.24, 1.02];
        let hook = $.Velocity.hook;
        let initialOverlayBuild = overlayMessageBorder.velocity({ width: '350px' }, {
          duration: 900, easing: customBezierCurve});

        overlayBody.css('display', 'block');

        $.when(initialOverlayBuild).then(function() {
          overlayMessage.velocity({ minHeight: '400px', paddingTop: '40px', paddingBottom: '40px'}, {
            duration: 850, easing: customBezierCurve});

          hook(messageHeadingContainer, 'translateY', '100%');
          hook(messageHeadingContainer, 'opacity', 0);
          hook(messageParagraph, 'translateY', '100%');
          hook(messageParagraph, 'opacity', 0);
          messageHeadingContainer.velocity({ translateY: '0%', opacity: 1}, {
            visibility: 'visible', duration: 1300, easing: customBezierCurve});
          messageParagraph.velocity({ translateY: '0%', opacity: 1}, {
            visibility: 'visible', duration: 1300, easing: customBezierCurve});

          if (specialSceneIs('livingroom-overlay__2')) {
            messageIcons.velocity({opacity: 1}, {
              visibility: 'visible', delay: 1000, duration: 1600, easing: customBezierCurve});
                                 // ctaDel, ctaDur, navDel, navDur
            alternativeAnimationTimes(1250, 1300, 5100, 920, customBezierCurve);
          } else if (specialSceneIs('bedroom-overlay__4')) {
            alternativeAnimationTimes(900, 1100, 8450, 920, customBezierCurve);
          } else { // all non-special touchpoints
            alternativeAnimationTimes(900, 1100, 1250, 920, customBezierCurve);
          }
        });
      }


      function alternativeAnimationTimes(ctaDelay, ctaDuration, navDelay, navDuration, easing) {
        callToAction.velocity({opacity: 1}, {
          visibility: 'visible', delay: ctaDelay, duration: ctaDuration, easing: easing});
        nextVideoNav.velocity({opacity: 1}, {
          visibility: 'visible', delay: navDelay, duration: navDuration, easing: easing});
        prevVideoNav.velocity({opacity: 1}, {
          visibility: 'visible', delay: navDelay, duration: navDuration, easing: easing});
      }

      function getInitialStateOfAnimation() {
        let hook = $.Velocity.hook;
        hook(overlayMessageBorder, 'width', 0);
        hook(overlayMessage, 'paddingTop', 0);
        hook(overlayMessage, 'paddingBottom', 0);
        hook(overlayMessage, 'minHeight', 0);
        hook(messageHeadingContainer, 'translateY', '100%');
        hook(messageHeadingContainer, 'visibility', 'hidden');
        hook(messageParagraph, 'translateY', '100%');
        hook(messageParagraph, 'visibility', 'hidden');
        hook(messageIcons, 'visibility', 'hidden');
        hook(messageIcons, 'opacity', 0);
        hook(callToAction, 'opacity', 0);
        hook(callToAction, 'visibility', 'hidden');
        hook(nextVideoNav, 'visibility', 'hidden');
        hook(nextVideoNav, 'opacity', 0);
        hook(prevVideoNav, 'visibility', 'hidden');
        hook(prevVideoNav, 'opacity', 0);
        hook(overlay, 'opacity', 0);
        hook(overlay, 'visibility', 'hidden');
      }

      function moveToScene() {
        function actionesUponClick() {
          hideOverlay();
          hideMediaOrInteractiveElement();
          getInitialStateOfAnimation();
        }

      // need to unbind first due to issue in IE
        nextVideoNav.unbind('click').bind('click', function() {
          actionesUponClick();
          video.get(0).play();
          specialCurrentTime = null;
        });

        prevVideoNav.unbind('click').bind('click', function() {
          clearTimeout(autoRedirectIfNoAction);
          actionesUponClick();
          moveToPreviousOverlay(Math.round(video.get(0).currentTime));
          specialCurrentTime = null;
        });
      }

  // hide next btn & show turn off btn instead at last scene
  // redirect to another page at end of video, either when user clicks on switch or after *some* time
  // if user clicks the back nav before the auto redirect takes place, then the autoredirect gets canceled in moveToScene()
      function lastScene(index, length, verticalPageRedirect, current, videoLength) {
        function hideAndResumeVideo() {
          hideOverlay();
          standbyButtonOn.css('visibility', 'hidden');
          video.get(0).play();
        }

        if (index === length - 1) {
          nextVideoNav.hide();
        // need to unbind first due to issue in IE
          standbyButtonOn.unbind('click').bind('click', function() {
            hideAndResumeVideo();
            if (current === videoLength) {
              window.location = verticalPageRedirect;
            }
          });

          autoRedirectIfNoAction = setTimeout(function() {
            hideAndResumeVideo();
            window.location = verticalPageRedirect;
          }, 6500);

          return autoRedirectIfNoAction;
        } else {
          nextVideoNav.show();
        }
      }

  // hide the previous button at the first scene
      function hidePrevAtFirstScene(index) {
        return index === 0 ? prevVideoNav.hide() : prevVideoNav.show();
      }

  // 'let there be light' is the title of the 5th scene in the livingroom, where the switch is clicked to 'fake' the effect of turning on the light, in reality, the video resumes at the moment the light comes on in the video
      function letThereBeLight(url) {
        let switchHasntBeenClicked = true;
        dimmerSwitch.unbind('click').bind('click', function() {
          if (switchHasntBeenClicked) {
            dimmerSwitch.addClass('dimmer-switch_active');
            letThereBeLightImg.css('visibility', 'visible');
            switchHasntBeenClicked = false;
          }
          else {
            letThereBeLightImg.css('visibility', 'hidden');
            hideOverlay();
            video.get(0).play();
            dimmerSwitch.css('visibility', 'hidden');
            dimmerSwitch.removeClass('dimmer-switch_active');
            getInitialStateOfAnimation();
          }
        });
      }


      function updateOverlayMessageContent(h1, p, cta, href) {
        messageHeading.html(h1);
        messageParagraph.html(p);
        callToActionText.html(cta).attr('href', href);
      }

    // use for specific positiong of overlay msg boxes
      function addSpecificOverlayId(idName,index) {
        let id = idName + (index + 1);
        overlayMessageContainer.attr('id', null).attr('id', id);
      }

      function hideOverlay() {
        overlay.css('visibility', 'hidden');
        overlayMessage.css('display', 'none');
      }


 // this function can be used to target .5 of a second, to target more specificly,
 // found this too late to refactor and make full advantage of it
 // setInterval(function(){
//   console.log(Math.round(video.get(0).currentTime*2)/2);
// }, 100);


      function playAllScenes(dragContainer, dragArrows, dragSwitch) {
        drggableSwitchContainer = dragContainer;
        draggableSwitch = dragSwitch;
        switchArrows = dragArrows;

        return video.data('room') === 'bedroom' ? showOverlay(bedroomData) :
         video.data('room') === 'livingroom' ? showOverlay(livingroomData) :
         showOverlay(garageData);

      }

      return { playAllScenes: playAllScenes };

    })();

  },{'./../data/bedroom.json': 1,'./../data/garage.json': 2,'./../data/livingroom.json': 3,'./draggableSwitch.js': 5,'./lib/velocity.min.js': 7,'./thermometer.js': 8}]},{},[4]);


  let carousel = (function($) {
    return {
      windowWidth: $(window).width(),
      windowHeight: $(window).height(),
      isComplete: true,

      init: function() {
        this.bindEvents();
        this.bindWindowEvents();
        this.mobileSlide();
      },
      bindEvents: function() {
        let _this = this;

            // prev link click
        $('.carousel-prev').unbind('click').bind('click', function(e) {
          e.preventDefault();

          if (_this.isComplete)
            {_this.slideCarousel('prev');}
        });

            // next link click
        $('.carousel-next, .carousel-quit').unbind('click').bind('click', function(e) {
          e.preventDefault();

          if (_this.isComplete)
            {_this.slideCarousel('next');}
        });
      },
      bindWindowEvents: function() {
        let _this = this;

            // On Resize
        $(window).on('resize', function() {
          _this.windowWidth = $(window).width();
          _this.windowHeight = $(window).height();
        });

            // On Orientation
        $(window).on('orientationchange', function(event) {

        });

            // On Scroll
        $(window).on('scroll', function() {

        });
      },
      slideCarousel: function(direction) {
        let _this = this;
        _this.isComplete = false;

        let lifestyle = $('.bg-lifestyle');
        let peace = $('.bg-peace');
        let energy = $('.bg-energy');
        let myURL = document.location.origin;

        if (direction === 'next') {

         var firstList = $('.carousel-container ul > li:first-child');
          var currentActiveList = $('.carousel-container ul > li.active');
          var nextActiveList = $('.carousel-container ul > li.active').next('li');

          if (nextActiveList.length === 0) {
                    // nextActiveList = firstList;
          }

          var currentBgIndex = nextActiveList.index() + 1;

          if (lifestyle.length) {
            if (currentBgIndex === 1) {
              $('.bg-wrapper').css('background-position', '10% center');
              $('.carousel-prev').hide();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 2) {
              $('.bg-wrapper').css('background-position', '30% center');
			  $('.carousel-prev').css('display','inline-block');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 3) {
              $('.bg-wrapper').css('background-position', '44% center');
			  $('.carousel-prev').css('display','inline-block');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 4) {
              $('.bg-wrapper').css('background-position', '62% center');
			  $('.carousel-prev').css('display','inline-block');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 5) {
              $('.bg-wrapper').css('background-position', '77% center');
			  $('.carousel-prev').css('display','inline-block');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 6) {
              $('.bg-wrapper').css('background-position', '100% center');
              $('.carousel-prev').hide();
              $('.carousel-next').hide();
			  $('.carousel-quit').css('display', 'inline-block');
              $('.carousel-quit').show();
              setTimeout(function() {
                document.location = myURL + videodata.redirectLink;
              }, 4000);
            } else {
              document.location = myURL + videodata.redirectLink;
            }
          } else if (peace.length) {
            if (currentBgIndex == 1) {
              $('.bg-wrapper').css('background-position', '10% center');
              $('.carousel-prev').hide();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 2) {
              $('.bg-wrapper').css('background-position', '46% center');
			  $('.carousel-prev').css('display','inline-block');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 3) {
              $('.bg-wrapper').css('background-position', '52% center');
			  $('.carousel-prev').css('display','inline-block');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 4) {
              $('.bg-wrapper').css('background-position', '64% center');
			  $('.carousel-prev').css('display','inline-block');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 5) {
              $('.bg-wrapper').css('background-position', '75% center');
			  $('.carousel-prev').css('display','inline-block');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 6) {
              $('.bg-wrapper').css('background-position', '100% center');
              $('.carousel-prev').hide();
              $('.carousel-next').hide();
			  $('.carousel-quit').css('display', 'inline-block');
              $('.carousel-quit').show();
              setTimeout(function() {
                document.location = myURL + videodata.redirectLink;
              }, 4000);
            } else {
              document.location = myURL + videodata.redirectLink;
            }
          } else if (energy.length) {
            if (currentBgIndex === 1) {
              $('.bg-wrapper').css('background-position', '36% center');
              $('.carousel-prev').hide();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 2) {
              $('.bg-wrapper').css('background-position', '48% center');
			  $('.carousel-prev').css('display','inline-block');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 3) {
              $('.bg-wrapper').css('background-position', '52% center');
			  $('.carousel-prev').css('display','inline-block');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 4) {
              $('.bg-wrapper').css('background-position', '68% center');
			  $('.carousel-prev').css('display','inline-block');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 5) {
              $('.bg-wrapper').css('background-position', '95% center');
			  $('.carousel-prev').css('display','inline-block');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 6) {
              $('.bg-wrapper').css('background-position', '100% center');
              $('.carousel-prev').hide();
              $('.carousel-next').hide();
			  $('.carousel-quit').css('display', 'inline-block');
              $('.carousel-quit').show();
              setTimeout(function() {
                document.location = myURL + videodata.redirectLink;
				
              }, 4000);
            } else {
              document.location = myURL + videodata.redirectLink;
            }
          }

          currentActiveList.removeClass('active');
          nextActiveList.addClass('active');

                // currentActiveList.addClass('left');
                // nextActiveList.addClass('next');
                // setTimeout(function() {
                //     nextActiveList.addClass('left');
                // }, 100);
                // setTimeout(function() {
                //     currentActiveList.removeClass('active left');
                //     nextActiveList.removeClass('next left');
                //     nextActiveList.addClass('active');
                //
                //     _this.isComplete = true;
                // }, 1000);

          setTimeout(function() {
            _this.isComplete = true;
          }, 1000);

        } else if (direction == 'prev') {
          var lastList = $('.carousel-container ul > li:last-child');
          var currentActiveList = $('.carousel-container ul > li.active');
          var prevActiveList = $('.carousel-container ul > li.active').prev('li');

          if (prevActiveList.length == 0) {
            prevActiveList = currentActiveList;
                    // window.location.reload();
          }

          var currentBgIndex = prevActiveList.index() + 1;

          if (lifestyle.length) {
            if (currentBgIndex === 1) {
              $('.bg-wrapper').css('background-position', '10% center');
              $('.carousel-prev').hide();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 2) {
              $('.bg-wrapper').css('background-position', '30% center');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 3) {
              $('.bg-wrapper').css('background-position', '44% center');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide(); 
            } else if (currentBgIndex === 4) {
              $('.bg-wrapper').css('background-position', '62% center');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 5) {
              $('.bg-wrapper').css('background-position', '77% center');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 6) {
              $('.bg-wrapper').css('background-position', '100% center');
              $('.carousel-next').hide();
              $('.carousel-prev').hide();
              $('.carousel-quit').show();
            } else {
                        // document.location = myURL + "/teknologi/";
            }
          } else if (peace.length) {
            if (currentBgIndex === 1) {
              $('.bg-wrapper').css('background-position', '10% center');
              $('.carousel-prev').hide();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 2) {
              $('.bg-wrapper').css('background-position', '46% center');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 3) {
              $('.bg-wrapper').css('background-position', '52% center');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 4) {
              $('.bg-wrapper').css('background-position', '64% center');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 5) {
              $('.bg-wrapper').css('background-position', '75% center');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 6) {
              $('.bg-wrapper').css('background-position', '100% center');
              $('.carousel-next').hide();
              $('.carousel-prev').hide();
              $('.carousel-quit').show();
            } else {
                        // document.location = myURL + "/sikkerhet-og-trygghet/";
            }
          } else if (energy.length) {
            if (currentBgIndex === 1) {
              $('.bg-wrapper').css('background-position', '36% center');
              $('.carousel-prev').hide();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 2) {
              $('.bg-wrapper').css('background-position', '48% center');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 3) {
              $('.bg-wrapper').css('background-position', '52% center');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 4) {
              $('.bg-wrapper').css('background-position', '68% center');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 5) {
              $('.bg-wrapper').css('background-position', '95% center');
              $('.carousel-prev').show();
              $('.carousel-next').show();
              $('.carousel-quit').hide();
            } else if (currentBgIndex === 6) {
              $('.bg-wrapper').css('background-position', '100% center');
              $('.carousel-next').hide();
              $('.carousel-prev').hide();
              $('.carousel-quit').show();
            } else {
                        // document.location = myURL + "/klima/";
            }
          }

          currentActiveList.removeClass('active');
          prevActiveList.addClass('active');
          setTimeout(function() {
            _this.isComplete = true;
          }, 1000);
        }
      },
      mobileSlide: function() {
        let _this = this;

        let x1 = 0;
        let endCoords = {};

        $(document).bind('touchstart', function(event) {
          endCoords = event.originalEvent.targetTouches[0];
          x1 = event.originalEvent.touches[0].clientX;
        });

        $(document).bind('touchmove', function(event) {
          let x2 = event.originalEvent.touches[0].clientX;
          let diff = parseInt(x2) - parseInt(x1);

          if (diff > 0 && diff > 70) {
            $('.carousel-prev').trigger('click');
          } else if (diff < 0 && diff < -70) {
            $('.carousel-next').trigger('click');
          }
        });
      }
    };
  })(jQuery);

// document on load
  jQuery(document).ready(function($) {
    carousel.init();

    if (jQuery(window).width() > 1024) {
      let vid = document.getElementById('js-video');
      vid.ontimeupdate = function() {
        jQuery('.immersive-rooms-body .loader').hide();
      };
    } else {
      jQuery('.immersive-rooms-body .loader').hide();
    }
  });
}
/* eslint-enable */
