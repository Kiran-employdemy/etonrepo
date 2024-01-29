/* eslint-disable no-undef */
// noinspection JSConstantReassignment

const comparisonWidgetClasses = {
  compareBasketButtonDisabled: 'compare-basket__button-disbaled',
  hide: 'hide',
  hideForBlank: 'hide-for-blank',
  loaderActive: 'loader-active'
};
const comparisonWidgetQuerySelectors = {
  comparisonWidget: '.comparision',
  comparisonWidgetItemLabel: '.comparision__box .compare__font',
  clearSelection: '.clear-selection',
  comparisonWidgetItemCountDiv: '.comp__count',
  compareButton: '.compare-basket',
  productGridResults: '.product-grid-results',
  productGridResultsContainer: '.product-grid-results.product-grid-results--sku > .container',
  comparisonResults: '.comparision-result',
  comparisonResultsTableHead: '#tableheadComp #heading',
  comparisonResultsTableBody: '#prdComp',
  errorModal: '#no_data-popup',
  minItemsModal: '#comp_table',
  maxItemsModal: '#myModalDT',
  loader: '.loader-product__compare',
  goBackButton: '.comparision__table-go-back-label',
  compareDTTool: '#compare__DTtool'
};
const comparisonMustacheElements = {
  mustacheTemplate: {
    heading: 'comparison-result-table-heading',
    body: 'comparison-result-table-body'
  }
};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = {comparisonWidgetQuerySelectors, comparisonWidgetClasses, comparisonMustacheElements};
}
