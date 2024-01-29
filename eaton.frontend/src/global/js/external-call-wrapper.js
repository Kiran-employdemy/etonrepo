/* eslint-disable no-undef */
/* eslint-disable no-global-assign */
// noinspection JSConstantReassignment

if (typeof require !== 'undefined') {
  const globalConstants = require('./etn-new-global-constants');
  literals = globalConstants.literals;
  $ = require(literals.JQUERY);
  httpMethods = globalConstants.httpMethods;
}

let externalCallWrapper = {
  makeCall: (url) => {
    return new Promise((resolve, reject) => {
      $.ajax({
        type: httpMethods.GET,
        async: false,
        url: url,
        success: (data) => {
          resolve(data);
        },
        error: () => {
          reject(`An error occurred while calling the url ${ url }`);
        }
      });
    });
  },
  post: (url, data) => {
    return new Promise((resolve, reject) => {
      $.ajax({
        type: httpMethods.POST,
        contentType: 'application/json',
        async: false,
        data: data,
        url: url,
        success: (data) => {
          resolve(data);
        },
        error: () => {
          reject(`An error occurred while calling the url ${ url }`);
        }
      });
    });
  }
};


if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = {externalCallWrapper};
}
