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

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

/* eslint-disable no-undef */
// noinspection JSConstantReassignment

if (typeof require !== 'undefined') {
  var globalConstants = require('../../../global/js/etn-new-global-constants');
  pathsTo = globalConstants.pathsTo;
}

var ViewTypeComponent = function () {
  function ViewTypeComponent(viewType, clickHandler) {
    _classCallCheck(this, ViewTypeComponent);

    this.viewType = viewType;
    this.clickHandler = clickHandler;
  }

  _createClass(ViewTypeComponent, [{
    key: 'initializeViewTypeButton',
    value: function initializeViewTypeButton() {
      var listButton = document.getElementById(elementIdOf.listButton);
      var gridButton = document.getElementById(elementIdOf.gridButton);
      if (!listButton || !gridButton) {
        return;
      }
      var activeButton = this.viewType === viewTypes.grid ? gridButton : listButton;
      activeButton.classList.add(elementClasses.active);
      gridButton.querySelector(querySelectorFor.image).setAttribute(elementAttributes.source, this.viewType === viewTypes.grid ? pathsTo.icons.ativeGrid : pathsTo.icons.disabledGrid);
      listButton.querySelector(querySelectorFor.image).setAttribute(elementAttributes.source, this.viewType === viewTypes.list ? pathsTo.icons.activeList : pathsTo.icons.disabledList);
      var self = this;
      listButton.onclick = function () {
        self.handleViewTypeClick(viewTypes.list);
      };
      gridButton.onclick = function () {
        self.handleViewTypeClick(viewTypes.grid);
      };
    }
  }, {
    key: 'handleViewTypeClick',
    value: function handleViewTypeClick(viewType) {
      this.viewType = viewType;
      this.initializeViewTypeButton();
      this.clickHandler.dispatchEvent(customEvents.VIEW_TYPE_CLICKED);
    }
  }]);

  return ViewTypeComponent;
}();

var createViewTypeComponent = function createViewTypeComponent(viewType, clickHandler) {
  return new ViewTypeComponent(viewType, clickHandler);
};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = { createViewTypeComponent: createViewTypeComponent };
}