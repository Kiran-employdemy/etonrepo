/* jshint esversion: 6 */

class PartnerProgramTable {

  constructor() {

    this.partnerProgramTableEle = document.querySelector('.partner-program-table table');
    this.wcmEditMode = document.querySelector('.partner-program-table.wcm-edit-mode');
    this.ajaxUrl = '';

  }

  init() {
    if (this.partnerProgramTableEle && !this.wcmEditMode) {
      // Get partner program data when the partner program table is present and not in edit mode
      this.ajaxUrl = this.partnerProgramTableEle.dataset.resource + '.nocache.json';
      this.getData();
    }
  }

  /**
   * Calls the partner program lookup servlet
   * response data of successful calls is added to the partner program table
   * when call fails, the accordion above the partner program table is hidden and error is logged
   */
  getData () {

    let self = this;

    $.ajax({
      type: 'GET',
      url: this.ajaxUrl,
      cache: false,
      headers: { 'Content-Type': 'application/json' },
      success: self.handleSuccess,
      error: self.handleError
    });

  }

  /**
   * Add partner program data to table
   * @param response
   */
  handleSuccess (response) {

    if (response.data) {
      let data = response.data;
      for (let i = 0; i < data.length; i++) {

        let item = data[i];

        let newRow = document.createElement('tr');
        let newProgramNameCell = document.createElement('td');

        if (item.includes('|')) {

          let programName = data[i].split('|')[0];

          if (programName) {
            newProgramNameCell.textContent = programName;
            newRow.appendChild(newProgramNameCell);

            let tierLevel = data[i].split('|')[1];

            if (tierLevel) {

              let newTierLevelCell = document.createElement('td');
              newTierLevelCell.textContent = tierLevel;
              newRow.appendChild(newTierLevelCell);

            }
          }

        } else {

          newProgramNameCell.textContent = data[i];
          newRow.appendChild(newProgramNameCell);
          let newBlankTierLevelCell = document.createElement('td');
          newRow.appendChild(newBlankTierLevelCell);

        }

        document.querySelector('.partner-program-table table tbody').appendChild(newRow);

      }
    }

  }

  /**
   * Upon error to servlet
   * Hide the accordion element with a descendant partner program table element
   */
  handleError () {
    document.querySelector('.partner-program-table table tbody').closest('.accordion-component').style.display = 'none';
  }

}

document.addEventListener('DOMContentLoaded', () => {

  let partnerProgramTable = new PartnerProgramTable();
  partnerProgramTable.init();

});


if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  // eslint-disable-next-line no-global-assign
  $ = require('jquery');

  module.exports = PartnerProgramTable;

}
