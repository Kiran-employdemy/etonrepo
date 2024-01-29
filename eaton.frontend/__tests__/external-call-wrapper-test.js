/* eslint-disable no-undef */
// noinspection JSConstantReassignment

const {externalCallWrapper} = require('../src/global/js/external-call-wrapper');
const constants = require('../src/components/advanced-search/js/advanced-search-constants');
literals = constants.literals;

beforeEach(() => {
  // eslint-disable-next-line no-global-assign
  fetch = require('node-fetch');
});

describe('The external call wrapper', () => {
  it('Should return a json response when a successful call is made to a url returning a json', async () => {
    let data = await externalCallWrapper.makeCall('https://api.agify.io/?name=meelad');
    expect(data).toEqual({age: 33, count: 21, name: 'meelad'});
  });
  it('Should throw an error if the call was not successful', async () => {
    await externalCallWrapper.makeCall('https://not-correct.json').catch((error) => {
      expect(error).toEqual( 'An error occurred while calling the url https://not-correct.json');
    });
  });
  it('should post to the given url with data', async () => {
    let response = await externalCallWrapper.post('https://reqres.in/api/users', '{\n' +
        '    "name": "morpheus",\n' +
        '    "job": "leader"\n' +
        '}');
    expect(response).not.toBeNull();
  });
  it('should throw an error if the post was not successful', async () => {
    await externalCallWrapper.post('https://not-correct.json', 'blah').catch((error) => {
      expect(error).toEqual( 'An error occurred while calling the url https://not-correct.json');
    });
  });
});
