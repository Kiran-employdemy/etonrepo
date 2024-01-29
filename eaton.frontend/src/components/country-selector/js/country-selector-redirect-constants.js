const countrySelectionConstants = {
  elementClasses: {
    collapse: 'collapse',
    inwards: 'in',
    iconMinus: 'icon-sign-minus',
    iconPlus: 'icon-sign-plus'
  },
  pathsTo: {
    selectionPage: '/country.html'
  },
  querySelectors: {
    allLinks: '.country-selector__item-language > a',
    countryBlock: '.country-block',
    countrySelectorIcons: '.country-selector__icons'
  }
};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = {countrySelectionConstants};
}
