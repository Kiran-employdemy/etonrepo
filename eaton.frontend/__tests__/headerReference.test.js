const { isMegaMenuSectionEmpty,
  closeActiveMegaMenuSections,
  getSignOutRedirectParameter } = require('../src/components/headerReference/js/headerReference.js');

const { megaMenuSectionEmpty,
  megaMenuSectionFull } = require('../__test-fixtures__/HeaderReferenceFixtures');

describe("Mega menu behavior", () => {

  beforeEach(() => {
    document.body.innerHTML = megaMenuSectionFull;
  });

  it("should verify mega menu section with no sub-navigation links is empty", () => {

    document.body.innerHTML = megaMenuSectionEmpty;

    let primaryNavEle = document.querySelector('.primary-navigation__menu');
    let primaryNavMenuCategory  = primaryNavEle.dataset.menuCategory;
    expect(isMegaMenuSectionEmpty(primaryNavMenuCategory)).toBeTruthy();
  });

  it("should verify mega menu section with sub-navigation links is not empty", () => {

    let primaryNavEle = document.querySelector('.primary-navigation__menu');
    let primaryNavMenuCategory  = primaryNavEle.dataset.menuCategory;
    expect(isMegaMenuSectionEmpty(primaryNavMenuCategory)).toBeFalsy();
  });

  it("should verify active overlays are hidden when mega menu section has active overlay", () => {

    closeActiveMegaMenuSections();
    let megaMenuContentEle = document.querySelector('.mega-menu__content');
    expect(megaMenuContentEle.classList.contains('mega-menu__content--active')).toBeFalsy();
  });

});



const setWindowLocationPathname = (path) => {

  window = Object.create(window);
  Object.defineProperty(window, 'location', {
    value: {
      pathname: path
    },
    writable: true
  });
};

describe("Sign out behavior", () => {

  it("should return the country language homepage as the sign out redirect when on a secure page", () => {

    setWindowLocationPathname('/us/en-us/secure/profile.html');
    expect(getSignOutRedirectParameter()).toBe('/us/en-us.html');

  });

  it ("should return the page the user was on as the sign out redirect when not on a secure page", () => {

    let path = '/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5px-ups.html';
    setWindowLocationPathname(path);
    expect(getSignOutRedirectParameter()).toBe(path);

  });

});



