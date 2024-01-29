module.exports = {
  countrySelectionButton: (host) => {
    return `
  <div class="header-utility-nav__toggle-selector">
    <a href="http://${ host }/country.html" class="open-country-selector">
      <i class="icon icon-globe" aria-hidden="true"></i>
      <div class="header-utility-nav__label">
        <span class="header-utility-nav__country">France</span>
        <span class="header-utility-nav__language">selectCountry</span>
      </div>
    </a>
  </div>
  `;
  }
};
