//-----------------------------------
// Component : Asset Bulk Download Email Component
//-----------------------------------

/* eslint-disable no-unused-vars, no-undef*/
let newTabEventTriggered = false;
let bulkDownloadSelf = '';
let assetLink = '';
let bulkDownloadFlag = 0;
let selectedFilesForBDE = '';

class BulkDownload {
  constructor(bulkDownloadActivated) {
    this.mainContainer = bulkDownloadActivated;
    this.container = document.getElementById('bulkDownload');
    this.bulkDownloadLink = document.getElementById('bulkDownload-link');
    this.bulkDownloadEmailTo = document.getElementById('bulkDownload-email-send-btn');
    this.bulkDownloadDrawer = document.getElementById('idbulkdownload');
    this.fileCount = document.getElementById('selecteddownload');
    this.fixedDrawerToggleButton = document.querySelector('.icon.fixed-drawer__down');
    this.popup = document.getElementById('maxlimit-exceed');
    this.viewChanged = false;
    this.expiryTime = this.container.dataset.cacheDuration; // in seconds
    this.localStorage = 'Eaton-bulk-download';
    this.bulkDownloadFilePath = [];
    this.trackDownloadItems = [];
    this.cacheFile = {};
    this.estimatedZipSize = 0;
    this.zipSizeLimit = this.container.dataset.zipSizeLimit;
    this.toolTipText = this.container.dataset.bulkdownloadTooltipText;
    this.fileNamePrefix = this.container.dataset.fileNamePrefix;
    this.backToTopButton = this.mainContainer.getElementsByClassName('back-to-top');
    this.backToTopButton = this.backToTopButton.length > 0 ? this.backToTopButton[0] : null;
    this.floatingDocumentCount = this.mainContainer.getElementsByClassName('floating-document-count');
    this.floatingDocumentCount = this.floatingDocumentCount.length > 0 ? this.floatingDocumentCount[0] : null;
    this.floatingDownloadButton = this.mainContainer.getElementsByClassName('floating-document-download');
    this.floatingDownloadButton = this.floatingDownloadButton.length > 0 ? this.floatingDownloadButton[0] : null;
    this.urlAjax = this.mainContainer.dataset.resource;
    this.newTabEventTriggered = false;
  }

  init() {
    this.addEventListeners();
  }
  /*
   * Method to listen the events
   *
   */
  addEventListeners() {
    let self = this;

    self.minimizeMaximizeDocumentDrawer();

    /*
     * Method to capture viewChange(grid/list) change event
     *
     */
    this.mainContainer.addEventListener('viewChange', function (event) {
      self.viewChanged = true;
    });

    /*
     * Listener for  responseRendered  event
     * responseRendered event is dispatched in advanced-search js
     *
     */
    this.mainContainer.addEventListener('responseRendered', function (event) {
      const downloadCheckBox = document.querySelectorAll('input[name=toDownload]');
      // eslint-disable-next-line no-eq-null, eqeqeq
      if (downloadCheckBox.length > 0) {
        downloadCheckBox.forEach(el => el.addEventListener('click', event => {
          let size = event.target.getAttribute('data-size');
          let url = event.target.getAttribute('data-url');
          let id = event.target.getAttribute('id');
          let trackDownloadFlag = event.target.getAttribute('track-download');
          if (event.target.checked) {
            // self.setEstimatedZipSize(size);
            if (self.estimatedZipFilesCount()) {
              self.addFilePathAndId(url, id);
            } else {
              // show max limit exceeded pop up7
              self.showMaxLimitExceedPopUp();
              el.click();
            }
            self.addTrackDownloadItems(url, trackDownloadFlag);
          } else {
            // self.setEstimatedZipSize(size, 'sub');
            self.removeFilePathAndId(url, id);
            self.removeTrackDownloadFilePath(url);
          }
          self.updateSelectedFileCount();
          if (!self.estimatedZipFilesCount()) {
            self.bulkDownloadLink.classList.add('disabledbutton');
          } else {
            self.bulkDownloadLink.classList.remove('disabledbutton');
          }

          addRemovefileForBDE();
        }));
      }

      self.resetBulkDownload();
      self.disableDownloadedFile();

    });


    /*
        * Listener for  responseRendered  event
        * responseRendered event is dispatched in advanced-search js
        *
        */
    this.mainContainer.addEventListener('triggerNewTabEvent', function (event) {
      window.newTabEventTriggered = true;
      window.assetLink = event.assetLink;
      self.addTrackDownloadItems(event.assetLink, 'yes');
      window.bulkDownloadSelf.callTrackDownloadAPI();
    });

    /*
     * Listener responseRendered  event
     * responseRendered event is dispatched in advanced-search js
     *
     */
    let downloadElementsArray = [self.bulkDownloadLink, self.floatingDownloadButton, self.bulkDownloadDrawer];

    downloadElementsArray.forEach(function (elem) {
      if (elem === null) {
        return;
      }
      elem.addEventListener('click', function (event) {
        event.target.id === 'idbulkdownload' && toggleBulkDownloadLoading(true, false);
        let zipFileName = self.getFileName().concat('.zip');
        let requestParams = {
          documentLinks: self.bulkDownloadFilePath
        };
        let loader = $('.loader');
        loader.addClass('loader-active');
        $.ajax({
          type: 'POST',
          url: '/eaton/content/assetzipdownload.zip',
          data: JSON.stringify(requestParams),
          headers: { 'Content-Type': 'application/zip' },
          xhrFields: { responseType: 'blob' },
          success: function (resultData) {
            loader.removeClass('loader-active');
            self.callTrackDownloadAPI();
            if (resultData) {
              let a = document.createElement('a');
              let url = window.URL.createObjectURL(resultData);
              a.href = url;
              a.download = zipFileName;
              document.body.append(a);
              a.click();
              a.remove();
              window.URL.revokeObjectURL(url);
            }
            event.target.id === 'idbulkdownload' && toggleBulkDownloadLoading(false, true);
          },
          error: function () {
            loader.removeClass('loader-active');
            event.target.id === 'idbulkdownload' && toggleBulkDownloadLoading(false, true);
          }
        });
      });
    });

    /* Email event */
    let emailElementsArray = [self.bulkDownloadEmailTo];
    emailElementsArray.forEach(function (elem2) {
      elem2.addEventListener('click', function (event) {
        if ($('#bulkDownload-email-to').val() === '') {
          // do nothing
          $('.email-valid-msg-div').addClass('text-danger');
          $('.email-valid-msg-div').html($('#bulkDownload-email-to').attr('data-valid-error'));
          $('#bulkDownload-email-to').focus();
          return false;
        } else {
          if (validateEmail($('#bulkDownload-email-to').val())) {
            $('.email-valid-msg-div').removeClass('text-danger');
            $('.email-valid-msg-div').html('');
            let emailId = document.getElementById('bulkDownload-email-to').value;
            let selectedFilesFromSession = JSON.parse(sessionStorage.getItem('selectedFiles'));
            let domainName = window.location.origin;
            let selectedFilesForEmail = selectedFilesFromSession.map(function (a) {
              return a.url;
            });
            let requestParams = '{"dataValue": [{"emailTo": "' + emailId + '","domain": "' + domainName + '","documentLinks": ' + JSON.stringify(selectedFilesForEmail) + '}]}';
            let loader = $('.loader');
            loader.addClass('loader-active');
            $.ajax({
              type: 'POST',
              url: '/eaton/content/bulkassetemail',
              data: requestParams,
              headers: { 'Content-Type': 'application/json' },
              success: function (response) {

              },
              error: function (xhr) {
                if (xhr.status === 200) {
                  loader.removeClass('loader-active');
                  $('.email-valid-msg-div').removeClass('text-danger');
                  $('.email-valid-msg-div').addClass('text-success');
                  $('#bulkDownload-email-to').addClass('classDN');
                  $('#bulkDownload-email-send-btn').addClass('classDN');
                  $('.email-valid-msg-div').html('<h3>' + $('#bulkDownload-email-to').attr('data-email-send') + '</h3>');
                }
                else {
                  loader.removeClass('loader-active');
                  $('.email-valid-msg-div').removeClass('text-success');
                  $('.email-valid-msg-div').addClass('text-danger');
                  $('.email-valid-msg-div').html($('#bulkDownload-email-to').attr('data-email-data-email-error'));
                }
              }
            });
          } else {
            $('.email-valid-msg-div').addClass('text-danger');
            $('.email-valid-msg-div').html($('#bulkDownload-email-to').attr('data-valid-error'));
            $('#bulkDownload-email-to').focus();
            return false;
          }
        }
      });
    });
    /* -Email event end--*/

    /*
     * pop up close click event
     *
     */
    let popupClose = document.getElementById('maxlimit-close');
    popupClose.addEventListener('click', function (event) {
      self.popup.classList.add('hidden');

    });
    /*
     * scroll to top
     *
     */
    if (self.backToTopButton !== null)
      {self.backToTopButton.addEventListener('click', () => {
        window.scroll({
          top: 0,
          behavior: 'smooth'
        });
      });}

  }

  /*
   * function to minimize and maximize .bulk-download-fixed-drawer
   */
  minimizeMaximizeDocumentDrawer() {
    this.fixedDrawerToggleButton.addEventListener('click', () => {
      document.querySelector('#assets-bulkdownload-email-div .main__box').classList.toggle('hide');
      document.querySelector('#assets-bulkdownload-email-div .fixed-drawer__footer').classList.toggle('hide');
      this.fixedDrawerToggleButton.classList.toggle('icon-chevron-up');
      this.fixedDrawerToggleButton.classList.toggle('icon-chevron-down');
    });
  }

  /*
   * function to check if the selected file count is with in the max download limit
   * returns true if this.estimatedZipSize is within the max limit
   * returns false if this.estimatedZipSize exceed  the max limit
   */
  fileIsEligible(size) {
    return this.estimatedZipSize > 0 && this.estimatedZipSize < parseInt(this.zipSizeLimit);
  }

  /*
   * function to check if the selected file count is with in the max download count
   * returns true if this.estimatedZipFilesCount is within the max limit
   * returns false if this.estimatedZipFilesCount exceed  the max limit
   */
  estimatedZipFilesCount() {
    let tempCount = document.querySelectorAll('input[name=toDownload]:checked').length;
    let actualLimitSet = parseInt($('#selected-files-drawer-box').attr('data-download-file-count-limit'));
    return actualLimitSet >= tempCount;
  }

  /*
   * function to calculate the size of the selected files
   * file size count will be set in the variable this.estimatedZipSize
   *
   */
  setEstimatedZipSize(size, operation) {
    let tempSize = 0;
    const selectedFiles = document.querySelectorAll('input[name=toDownload]:checked');
    // Re-checking size of each file and then setting the estimated zip file.
    selectedFiles.forEach((checkbox) => {
      tempSize += parseInt(checkbox.getAttribute('data-size'));
    });
    this.estimatedZipSize = tempSize;
    if (operation === 'sub') {
      this.estimatedZipSize = this.estimatedZipSize >= parseInt(size) ? this.estimatedZipSize - parseInt(size) : this.estimatedZipSize;
    }
  }
  /*
   * function to add file path and id to the array when the user selects the record
   * this.bulkDownloadFilePath
   * window.bulkDownloadId
   */
  addFilePathAndId(url, id) {
    if (this.bulkDownloadFilePath.indexOf(url) === -1) {
      this.bulkDownloadFilePath.push(url);
    }
    if (window.bulkDownloadId.indexOf(id) === -1) {
      window.bulkDownloadId.push(id);
    }
  }


  /*
   * function to add file path and id to the array when the user selects the record
   * this.bulkDownloadFilePath
   * window.bulkDownloadId
   */
  addTrackDownloadItems(url, downloadYesOrNo) {
    if (this.trackDownloadItems.indexOf(url) === -1 && (downloadYesOrNo === 'yes' || downloadYesOrNo === 'true')) {
      this.trackDownloadItems.push(url);
    }
  }
  /*
   * function to remove file path and id from the array when the user uncheck the record
   * this.bulkDownloadFilePath
   * window.bulkDownloadId
   */
  removeFilePathAndId(url, id) {
    let urlIndex = this.bulkDownloadFilePath.indexOf(url);
    if (urlIndex > -1) {
      this.bulkDownloadFilePath.splice(urlIndex, 1);
    }
    let idIndex = window.bulkDownloadId.indexOf(id);
    if (idIndex > -1) {
      window.bulkDownloadId.splice(idIndex, 1);
    }
  }

  /*
     * function to remove file path and id from the array when the user uncheck the record
     * this.bulkDownloadFilePath
     * window.bulkDownloadId
     */
  removeTrackDownloadFilePath(url) {
    let urlIndex = this.trackDownloadItems.indexOf(url);
    if (urlIndex > -1) {
      this.trackDownloadItems.splice(urlIndex, 1);
    }

  }
  /*
   * function to reset bulkdownload  and floating buttons after successful download
   *
   */
  resetBulkDownload() {
    let selectedItem = window.bulkDownloadId;
    this.fileCount.innerHTML = '(0)';
    window.bulkDownloadId = [];
    this.bulkDownloadFilePath = [];
    this.estimatedZipSize = 0;
    if (this.bulkDownloadLink) {
      this.bulkDownloadLink.classList.add('disabledbutton');
    }
    if (this.floatingDocumentCount !== null) {
      this.floatingDocumentCount.getElementsByTagName('span')[0].innerHTML = '(0)';
      this.floatingDocumentCount.setAttribute('disabled', true);
    }
    if (this.floatingDownloadButton !== null) {
      this.floatingDownloadButton.setAttribute('disabled', true);
    }
    if (this.viewChanged) {
      selectedItem.forEach(function (item, index) {
        const element = document.getElementById(item);
        if (element !== null && element !== undefined) {
          element.click();
        }
      });
      this.viewChanged = false;
    }
  }
  /*
   * function to disable already downloaded files
   * if  file id exist in local storage and its not expired then those files will be disabled
   *
   */
  disableDownloadedFile() {
    this.getCache();
    if (this.cacheFile && Object.keys(this.cacheFile).length !== 0) {
      this.cacheFile.id.forEach(function (item, index) {
        const element = document.getElementById(item);
        if (element !== null && element !== undefined) {
          if (bulkDownloadFlag === 1) {
            element.checked = false;
            element.classList.remove('disabledbutton');
            sessionStorage.removeItem('selectedFiles');
            localStorage.removeItem('Eaton-bulk-download');
          } else {
            // Use code comment in bracket - when need to mainatin downloaded file disabled <element.checked = true; element.classList.add('disabledbutton');>
          }
        }
      });
    }
  }
  /*
   * function to update selected file count
   * Disable/enable the floating button download and file count based on the number of files selected
   *
   */
  updateSelectedFileCount() {
    let length = window.bulkDownloadId.length;
    this.updateFileCount(length);
    this.updateFloatingDocumentCount(length);
    this.updateFloatingDocumentButton(length);
  }

  updateFileCount(length) {
    if (this.fileCount) {
      this.fileCount.innerHTML = length > 0 ? `(${ length })` : '(0)';
    }
  }
  updateFloatingDocumentCount(length) {
    if (this.floatingDocumentCount) {
      this.floatingDocumentCount.querySelector('span').innerHTML = length > 0 ? length : '(0)';
      if (length > 0) {
        this.floatingDocumentCount.removeAttribute('disabled');
      } else {
        this.floatingDocumentCount.setAttribute('disabled', true);
      }
    }
  }

  updateFloatingDocumentButton(length) {
    if (this.floatingDownloadButton) {
      if (length > 0 ) {
        this.floatingDownloadButton.removeAttribute('disabled');
      } else {
        this.floatingDocumentCount.setAttribute('disabled', true);
      }
    }
  }


  /*
   * function to read local storage
   * localstorage name: Eaton-bulk-download
   * If local storage exist and not expired then sets localstorage value in the cachefile variable
   * If local storage is expired then removes it
   */
  getCache() {
    const cachedfiles = localStorage.getItem(this.localStorage);
    let expired = false;
    if (cachedfiles) {

      const cachedFileIds = JSON.parse(cachedfiles);
      expired = (new Date(cachedFileIds.expires) < new Date());
      if (!expired) {
        this.cacheFile = JSON.parse(cachedfiles);
      } else {
        this.unsetCache();
      }
    }
  }
  /*
   * function to update downloaded file ids in the local storage
   * localstorage name: Eaton-bulk-download
   * if local storage doesn't exist then its creates
   */
  updateCache() {
    this.getCache();
    if (this.cacheFile
      && Object.keys(this.cacheFile).length !== 0 && window.bulkDownloadId.length >= 1) {
      this.cacheFile.id = this.cacheFile.id.concat(window.bulkDownloadId);
      this.unsetCache();
      localStorage.setItem(this.localStorage, JSON.stringify(this.cacheFile));
    } else {
      this.setCache();
    }
  }

  /*
   * function to set downloaded file ids in the local storage
   * localstorage name: Eaton-bulk-download
   *
   */
  setCache() {
    if (window.bulkDownloadId.length >= 1) {
      let localStorageObj = {};
      localStorageObj.expires = new Date(new Date().getTime() + (this.expiryTime * 1000));
      localStorageObj.id = window.bulkDownloadId;
      this.unsetCache();
      localStorage.setItem(this.localStorage, JSON.stringify(localStorageObj));
    }
  }
  /*
   * function to remove local storage
   * localstorage name: Eaton-bulk-download
   *
   */
  unsetCache() {
    localStorage.removeItem(this.localStorage);
  }
  /*
   *function to show max limit exceeded popup
   *
   */
  showMaxLimitExceedPopUp() {
    this.popup.classList.remove('hidden');
  }

  callTrackDownloadAPI() {
    let trackurl = this.urlAjax + '.nocache.json';
    $.ajax({
      type: 'POST',
      async: false,
      url: trackurl,
      data: JSON.stringify(this.trackDownloadItems),
      success: function success(data) {
        console.log(data);
      }
    });

  }

  /**
   * @function fileName - The file name based on the authored prefix and the current time.
   */
  getFileName() {
    const today = new Date();
    let dd = today.getDate();
    let mm = today.getMonth() + 1;
    let yyyy = today.getFullYear();
    if (dd < 10) { dd = '0' + dd; }
    if (mm < 10) { mm = '0' + mm; }

    return this.fileNamePrefix + mm + '_' + dd + '_' + yyyy;
  }
}

(function () {
  const bulkDownload = document.getElementsByClassName('bde-activated');
  if (bulkDownload.length > 0) {
    window.bulkDownloadId = [];
    window.bulkDownloadSelf = new BulkDownload(bulkDownload[0]);
    bulkDownloadSelf.init();
  }
}());

document.addEventListener('DOMContentLoaded', function() {
  $('.okay-track').click(function () {
    if (window.newTabEventTriggered) {
      window.bulkDownloadSelf.callTrackDownloadAPI();
      window.open(window.assetLink, '_blank');
      window.newTabEventTriggered = false; // Reset
      window.assetLink = ''; // Reset
    }
  });
});


document.addEventListener('DOMContentLoaded', function() {
  $('.bulk-download-fixed-drawer').removeClass('classDN');
  $('.bulk-download-fixed-drawer').hide();
  adjustdrawer();

  $('.bulk-download-fixed-drawer').on('click', 'a.call-fd-model', function () {
    $('#' + $(this).attr('id') + '-model').show();
    $('.loader').removeClass('loader-active');
    // Clean up email model
    $('.email-valid-msg-div').removeClass('text-success');
    $('.email-valid-msg-div').removeClass('text-danger');
    $('.email-valid-msg-div').html('');
    $('#bulkDownload-email-to').val('');
    $('#bulkDownload-email-to').removeClass('classDN');
    $('#bulkDownload-email-send-btn').removeClass('classDN');
  });

  $('.bulk-download-fixed-drawer').on('click', 'button.fd-close-popup', function () {
    $('.loader').removeClass('loader-active');
    $('.fd-model').hide();

    // Clean up email model
    $('.email-valid-msg-div').removeClass('text-success');
    $('.email-valid-msg-div').removeClass('text-danger');
    $('.email-valid-msg-div').html('');
    $('#bulkDownload-email-to').val('');
    $('#bulkDownload-email-to').removeClass('classDN');
    $('#bulkDownload-email-send-btn').removeClass('classDN');
  });

  $('.bulk-download-fixed-drawer').on('click', 'a.clear-selection', function () {
    $('.fixed-drawer__box').each(function () {
      $('#' + $(this).attr('data-selected-file-id')).click();
      $(this).remove();
    });
    // hide entire panel
    adjustdrawer();
  });

  $('.bulk-download-fixed-drawer').on('click', 'i.fixed-drawer__close', function () {
    $(this).closest('.fixed-drawer__box').remove();
    $('#' + $(this).attr('data-file-id')).click();
    adjustdrawer();
  });

  $('.bulk-download-fixed-drawer').on('click', 'a.view-more-less', function () {
    let viewML = '';
    let viewMLCaption = '';
    let countML = '';
    if ($('#selected-files-drawer-box').hasClass('viewmore')) {
      $('.default-hide').removeClass('classDN');
      $('#selected-files-drawer-box').removeClass('viewmore');
      $('#selected-files-drawer-box').addClass('viewless');
      viewMLCaption = $('#selected-files-drawer-box').attr('data-viewless');
      countML = selectedFilesForBDE.length - $('#selected-files-drawer-box').attr('data-view-limit');
      viewML = '<a class="view-more-less">' + viewMLCaption + '</a>';
    } else {
      if ($('#selected-files-drawer-box').hasClass('viewless')) {
        $('.default-hide').addClass('classDN');
        $('#selected-files-drawer-box').removeClass('viewless');
        $('#selected-files-drawer-box').addClass('viewmore');
        viewMLCaption = $('#selected-files-drawer-box').attr('data-viewmore');
        countML = selectedFilesForBDE.length - $('#selected-files-drawer-box').attr('data-view-limit');
        viewML = '<a class="view-more-less">' + viewMLCaption + '<span class="viewmorecount">(' + countML + ')</span></a>';
      } else {
        $('.default-hide').remove();
        $('#selected-files-drawer-box').removeClass('viewless');
        $('#selected-files-drawer-box').removeClass('viewmore');
        viewML = '';
      }
    }
    $('.view-more-less').remove();
    $('#selected-files-drawer-box').append(viewML);

  });


  $('.bulk-download-active').on('click', function () {
    let fileCount = 0;
    fileCount = $('.fixed-drawer__box').length + 1;
    let selectedFile = '<div class="fixed-drawer__box" id="selected-file-' + fileCount + '"><p class="fixed-drawer__font" id="1">EASY222-DN DeviceNet Slave Interface</p><i class="icon close fixed-drawer__close icon-close"></i></div>';
    $('.main__box').append(selectedFile);
    adjustdrawer();
  });


  $('#bulkDownload-email-to').on('blur', function () {
    if ($('#bulkDownload-email-to').val() === '') {
      $('.email-valid-msg-div').addClass('text-danger');
      $('.email-valid-msg-div').html($('#bulkDownload-email-to').attr('data-valid-error'));
    } else {
      if (validateEmail($('#bulkDownload-email-to').val())) {
        $('.email-valid-msg-div').removeClass('text-danger');
        $('.email-valid-msg-div').html('');
      } else {
        $('.email-valid-msg-div').addClass('text-danger');
        $('.email-valid-msg-div').html($('#bulkDownload-email-to').attr('data-valid-error'));
      }
    }

  });



});

function addRemovefileForBDE() {
  selectedFilesForBDE = sessionStorage.selectedFiles === undefined ? '' : JSON.parse(sessionStorage.selectedFiles);
  let selectedFileString = '';
  let viewMoreLessCaption = '';
  let countMoreLess = '';
  let viewMoreLess = '';
  // When no file selected.
    // Proceed further for
  for (let i = 0; i < selectedFilesForBDE.length; i++) {
    if (i < $('#selected-files-drawer-box').attr('data-view-limit')) {
      selectedFileString += '<div class="fixed-drawer__box" id="selected-file-' + selectedFilesForBDE[i].id + '" data-selected-file-id="' + selectedFilesForBDE[i].id + '"><p class="fixed-drawer__font" id="file-title-' + i + '">' + selectedFilesForBDE[i].title + '</p><i class="icon close fixed-drawer__close icon-close" data-file-id="' + selectedFilesForBDE[i].id + '"></i></div>';
    } else {
      selectedFileString += '<div class="fixed-drawer__box default-hide" id="selected-file-' + selectedFilesForBDE[i].id + '" data-selected-file-id="' + selectedFilesForBDE[i].id + '"><p class="fixed-drawer__font" id="file-title-' + i + '">' + selectedFilesForBDE[i].title + '</p><i class="icon close fixed-drawer__close icon-close" data-file-id="' + selectedFilesForBDE[i].id + '"></i></div>';
    }
  }

  if ((selectedFilesForBDE.length > $('#selected-files-drawer-box').attr('data-view-limit'))) {
    if ($('#selected-files-drawer-box').hasClass('viewmore')) {
      viewMoreLessCaption = $('#selected-files-drawer-box').attr('data-viewmore');
      countMoreLess = selectedFilesForBDE.length - $('#selected-files-drawer-box').attr('data-view-limit');
      viewMoreLess = '<a class="view-more-less">' + viewMoreLessCaption + '<span class="viewmorecount">(' + countMoreLess + ')</span></a>';
    } else {
      if ($('#selected-files-drawer-box').hasClass('viewless')) {
        viewMoreLessCaption = $('#selected-files-drawer-box').attr('data-viewless');
        countMoreLess = selectedFilesForBDE.length - $('#selected-files-drawer-box').attr('data-view-limit');
        viewMoreLess = '<a class="view-more-less">' + viewMoreLessCaption + '</a>';
      } else {
        viewMoreLess = '';
        $('#selected-files-drawer-box').removeClass('viewless');
        $('#selected-files-drawer-box').addClass('viewmore');
      }
    }
  } else {
    viewMoreLess = '';
    $('.view-more-less').remove();
    $('#selected-files-drawer-box').removeClass('viewless');
    $('#selected-files-drawer-box').addClass('viewmore');

  }
  $('#selected-files-drawer-box').html(selectedFileString);
  if ($('#selected-files-drawer-box').hasClass('viewmore')) {
    $('.default-hide').addClass('classDN');
  } else {
    $('.default-hide').removeClass('classDN');
  }
  $('.view-more-less').remove();
  $('#selected-files-drawer-box').append(viewMoreLess);
  adjustdrawer();
}

function adjustdrawer() {
  if ($('.fixed-drawer__box').length > 0) {
    $('.bulk-download-fixed-drawer').show();
  } else {
    $('.bulk-download-fixed-drawer').hide();
  }
  $('.fixed-drawer__count').html($('.fixed-drawer__box').length || 0);
}

/**
 * function for validating email address.
 */
function validateEmail(inputText) {
  let mailformat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
  if (mailformat.test(inputText)) {
    return true;
  } else {
    return false;
  }
}

/**
 * function for validating email address.
 */
function getFileNameForBulkDownloadZip() {
  const today = new Date();
  let dd = today.getDate();
  let mm = today.getMonth() + 1;
  let yyyy = today.getFullYear();
  if (dd < 10) { dd = '0' + dd; }
  if (mm < 10) { mm = '0' + mm; }
  let tempBObj = new BulkDownload();

  let zipFileNamePrefix = tempBObj.container.dataset.fileNamePrefix || '';
  return zipFileNamePrefix + mm + '_' + dd + '_' + yyyy;
}


/**
 * Toggle loading state for the bulk download button
 * @param hideDownloadIcon - boolean to hide/show download icon
 * @param hideSpinner - boolean to hide/show loading spinner
 */
const toggleBulkDownloadLoading = (hideDownloadIcon, hideSpinner) => {
  document.querySelector('#idbulkdownload .icon-download').hidden = hideDownloadIcon;
  document.querySelector('#idbulkdownload .load-spinner').style.display = hideSpinner ? 'none' : 'inline-block';
};


if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {

  module.exports = { toggleBulkDownloadLoading };

}
