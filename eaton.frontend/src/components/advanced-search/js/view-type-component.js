/* eslint-disable no-undef */
// noinspection JSConstantReassignment

if (typeof require !== 'undefined') {
  const globalConstants = require('../../../global/js/etn-new-global-constants');
  pathsTo = globalConstants.pathsTo;
}

class ViewTypeComponent {
  constructor(viewType, clickHandler) {
    this.viewType = viewType;
    this.clickHandler = clickHandler;
  }

  initializeViewTypeButton() {
    let listButton = document.getElementById(elementIdOf.listButton);
    let gridButton = document.getElementById(elementIdOf.gridButton);
    if (!listButton || !gridButton) {
      return;
    }
    let activeButton = this.viewType === viewTypes.grid ? gridButton : listButton;
    activeButton.classList.add(elementClasses.active);
    gridButton.querySelector(querySelectorFor.image).setAttribute(elementAttributes.source, this.viewType === viewTypes.grid ? pathsTo.icons.ativeGrid : pathsTo.icons.disabledGrid);
    listButton.querySelector(querySelectorFor.image).setAttribute(elementAttributes.source, this.viewType === viewTypes.list ? pathsTo.icons.activeList : pathsTo.icons.disabledList);
    let self = this;
    listButton.onclick = () => {
      self.handleViewTypeClick(viewTypes.list);
    };
    gridButton.onclick = () => {
      self.handleViewTypeClick(viewTypes.grid);
    };
  }

  handleViewTypeClick(viewType) {
    this.viewType = viewType;
    this.initializeViewTypeButton();
    this.clickHandler.dispatchEvent(customEvents.VIEW_TYPE_CLICKED);
  }
}

const createViewTypeComponent = (viewType, clickHandler) => {
  return new ViewTypeComponent(viewType, clickHandler);
};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = {createViewTypeComponent};
}
