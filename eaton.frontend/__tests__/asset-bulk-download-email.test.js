/* jshint esversion: 6 */

const { toggleBulkDownloadLoading } = require('../src/components/asset-bulk-download-email/js/asset-bulk-download-email.js');

const { filledAssetBulkDownloadDrawer } = require('../__test-fixtures__/AssetBulkDownloadEmailFixtures');


let downloadButtonIcon, downloadButtonSpinner;
describe('download button behavior', () => {

  beforeEach(() => {
    document.body.innerHTML = filledAssetBulkDownloadDrawer;
    downloadButtonIcon = document.querySelector('#idbulkdownload .icon-download');
    downloadButtonSpinner = document.querySelector('#idbulkdownload .load-spinner');
  });

  it('should hide the download icon and show the loading spinner',  () => {
    toggleBulkDownloadLoading(true, false);
    expect(downloadButtonIcon.hidden).toBeTruthy();
    expect(downloadButtonSpinner.style.display).toBe('inline-block');
  });

  it('should show the download icon and hide the loading spinner',  () => {

    downloadButtonIcon.hidden = true;
    downloadButtonSpinner.style.display = 'inline-block';

    toggleBulkDownloadLoading(false, true);

    expect(downloadButtonIcon.hidden).toBeFalsy();
    expect(downloadButtonSpinner.style.display).toBe('none');

  });

});
