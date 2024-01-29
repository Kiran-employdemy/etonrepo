const US_HOME_FULL_PATH = 'http://www-local.eaton.com/us/en-us.html';
const US_HOME_PATH_NAME = '/us/en-us.html';
const US_ENGINE_SOLUTIONS_FULL_PATH = 'http://www-local.eaton.com/content/eaton/us/en-us/products/engine-solutions.html';
const US_ENGINE_SOLUTIONS_PATH_NAME = '/content/eaton/us/en-us/products/engine-solutions.html';
const COUNTRY_SELECTION_FULL_PATH = 'http://www-local.eaton.com/country.html';
const COUNTRY_SELECTION_PATH_NAME = '/country.html';
const LOGIN_COUNTRY_SELECTION_FULL_PATH = 'https://www-local-login.eaton.com/country.html';
const LOGIN_PAGE_FULL_PATH = 'https://www-local-login.eaton.com/us/en-us/login.html';
const LOGIN_PAGE_PATH = '/login.html';
const EATON_5SC_UPS_FULL_PATH = 'https://www-local.eaton.com/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.html';
const EATON_5SC_UPS_PATH = '/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.html';
const location = (fullPath, pathName) => {
  return {
    value: {
      href: fullPath,
      pathname: pathName,
      hashValue: '',
      set hash(hashValue) {
        this.hashValue = '#' + hashValue;
        this.href = this.href.indexOf('#') > -1 ? (this.href.substring(0, this.href.indexOf('#')) + this.hashValue) : (this.href + this.hashValue);
      },
      get hash() { return this.hashValue; }
    },
    writable: true
  };
};

module.exports = {
  US_HOME_PATH_NAME,
  US_ENGINE_SOLUTIONS_PATH_NAME,
  LOGIN_PAGE_PATH,
  usHomeLocation: () => {
    return location(US_HOME_FULL_PATH, US_HOME_PATH_NAME);
  },
  usEngineSolutionLocation: () => {
    return location(US_ENGINE_SOLUTIONS_FULL_PATH, US_ENGINE_SOLUTIONS_PATH_NAME);
  },
  countrySelectionLocation: () => {
    return location(COUNTRY_SELECTION_FULL_PATH, COUNTRY_SELECTION_PATH_NAME);
  },
  loginCountrySelectionLocation: () => {
    return location(LOGIN_COUNTRY_SELECTION_FULL_PATH, COUNTRY_SELECTION_PATH_NAME);
  },
  loginPageLocation: () => {
    return location(LOGIN_PAGE_FULL_PATH, LOGIN_PAGE_PATH);
  },
  fiveSCUpsLocation: (hash) => {
    let fiveSCUPS = location(EATON_5SC_UPS_FULL_PATH, EATON_5SC_UPS_PATH);
    fiveSCUPS.hash = hash;
    return fiveSCUPS;
  }
};
