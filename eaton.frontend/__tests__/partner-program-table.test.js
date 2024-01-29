/* jshint esversion: 6 */

const PartnerProgramTable = require('../src/components/partner-program-table/js/partner-program-table');

const { partnerProgramBlankTable,
  partnerProgramFilledTableRows,
  partnerProgramTestData,
  additionalAccordions,
  wcmEditMode,
  partnerProgramLookupAjaxRequest } = require('../__test-fixtures__/PartnerProgramTableFixtures');

const $ = require('jquery');

let ajaxSpy = {};
let getDataSpy, handleSuccessSpy, handleErrorSpy;
let partnerProgramTable;
let tableSel, tableBodySel;
const accordionComponentSel = '.accordion-component';

const createPartnerProgramTableAndSpies = () => {

  partnerProgramTable = new PartnerProgramTable();

  getDataSpy = jest.spyOn(partnerProgramTable, 'getData');
  handleSuccessSpy = jest.spyOn(partnerProgramTable, 'handleSuccess');
  handleErrorSpy = jest.spyOn(partnerProgramTable, 'handleError');

};

beforeEach(() => {
  jest.restoreAllMocks();

  document.body.innerHTML = partnerProgramBlankTable;

  ajaxSpy = jest.spyOn($, 'ajax');

  tableSel = '.partner-program-table';
  tableBodySel = tableSel + ' tbody';

});


describe('Partner program table functionality:', () => {

  it('should add new rows to table with response data', () => {

    createPartnerProgramTableAndSpies();
    partnerProgramTable.handleSuccess(partnerProgramTestData());
    expect(document.querySelector(tableBodySel).innerHTML.trim()).toBe(partnerProgramFilledTableRows().trim());

  });

  it('should hide accordion element ancestor of the partner program table but not other accordion elements', () => {

    document.body.innerHTML += additionalAccordions();

    createPartnerProgramTableAndSpies();
    partnerProgramTable.handleError();

    let partnerProgramAccordion = document.querySelector(tableSel).closest(accordionComponentSel);
    let testAccordionOne = document.querySelector('#accordion-918678258').closest(accordionComponentSel);
    let testAccordionTwo = document.querySelector('#accordion-918678259').closest(accordionComponentSel);

    expect(partnerProgramAccordion.style.display).toBe('none');
    expect(testAccordionOne.style.display).not.toBe('none');
    expect(testAccordionTwo.style.display).not.toBe('none');


  });

  it('should call get data when table is present and not in wcm edit mode', () => {

    createPartnerProgramTableAndSpies();
    partnerProgramTable.init();

    expect(getDataSpy).toHaveBeenCalled();

  });

  it('should not call get data when table is not present and not in wcm edit mode', () => {

    document.body.innerHTML = additionalAccordions();

    createPartnerProgramTableAndSpies();
    partnerProgramTable.init();

    expect(getDataSpy).not.toHaveBeenCalled();

  });

  it('should not call get data when table is present and page is in wcm edit mode', () => {

    document.body.innerHTML += wcmEditMode();

    createPartnerProgramTableAndSpies();
    partnerProgramTable.init();

    expect(getDataSpy).not.toHaveBeenCalled();

  });

  it('should not call get data when table is not present and page is in wcm edit mode', () => {

    document.body.innerHTML = additionalAccordions();
    document.body.innerHTML += wcmEditMode();

    createPartnerProgramTableAndSpies();
    partnerProgramTable.init();

    expect(getDataSpy).not.toHaveBeenCalled();

  });
});

describe('Partner program lookup servlet successful scenarios: ', () => {

  it('should add table rows with servlet\'s successful response', () => {

    createPartnerProgramTableAndSpies();

    ajaxSpy.mockImplementationOnce(options => {

      if (typeof options.success === 'function') {

        let response = {
          data: [
            'Electrical Wholesaler|Registered',
            'Electrical Distributor|Authorized',
            'Electrical Customer'
          ],
          status: 200
        };

        options.success(response);

      }

    });

    partnerProgramTable.init();

    expect(ajaxSpy).toBeCalledWith(partnerProgramLookupAjaxRequest());
    expect(handleSuccessSpy).toHaveBeenCalled();
    expect(handleErrorSpy).not.toHaveBeenCalled();
    expect(document.querySelector(tableBodySel).innerHTML.trim()).toBe(partnerProgramFilledTableRows().trim());

  });

});

describe('Partner program lookup servlet failed scenarios: ', () => {

  beforeEach(() => {
    document.body.innerHTML += additionalAccordions();
    createPartnerProgramTableAndSpies();
  });

  afterEach(() => {

    partnerProgramTable.init();
    expect(ajaxSpy).toBeCalledWith(partnerProgramLookupAjaxRequest());
    expect(handleSuccessSpy).not.toHaveBeenCalled();
    expect(handleErrorSpy).toHaveBeenCalled();
    expect(document.querySelector(tableSel).closest(accordionComponentSel).style.display).toBe('none');

  });

  it('should hide the entire accordion when servlet returns 404 user doesn\'t have partner program data', () => {

    ajaxSpy.mockImplementationOnce(options => {
      if (typeof options.error === 'function') {

        options.error({
          message: '',
          status: 404
        });

      }
    });

  });

  it('should hide the entire accordion when servlet returns 401 cannot find user profile from authentication token', () => {

    ajaxSpy.mockImplementationOnce(options => {
      if (typeof options.error === 'function') {

        options.error({
          message: 'Cannot find user profile from authentication token.',
          status: 401
        });

      }
    });

  });

  it('should hide the entire accordion when servlet returns 401 authorization token not found in sling request', () => {

    ajaxSpy.mockImplementationOnce(options => {
      if (typeof options.error === 'function') {

        options.error({
          message: 'Authorization token not found in sling request.',
          status: 401
        });

      }
    });

  });

});
