//-----------------------------------
// Resources List
//-----------------------------------
'use strict';

let App = window.App || {};
App.resourceList = function () {

  const $accordion = $('.panel-group');
  const $component = $('.secondary-content-accordion');
  const $link = $('[data-scroll-to]');

  const init = () => {
    $accordion.on('hidden.bs.collapse', toggleIcon);
    $accordion.on('shown.bs.collapse', toggleIcon);
    $accordion.on('hidden.bs.collapse', toggleIconQR);
    $accordion.on('shown.bs.collapse', toggleIconQR);
    $link.on('click', expand);
  };

  let resourceDownloadAll = $('.resource_download_all');
  if (resourceDownloadAll.length > 0) {
    $('.disclaimer').show();
  }


  resourceDownloadAll.click(function () {
    let category = $(this).attr('data-category-name');
    let productName = $(this).attr('data-product-family');
    let categoryId = location.pathname.split('.')[1];
    let zipFileName = category.concat('-').concat(productName).concat('-').concat(categoryId).concat('.zip');
    zipFileName = zipFileName.replace(/\s/g, '-');
    let requestParams = {
      documentLinks: JSON.parse(this.dataset.resourceLinks)
    };
    let loader = $('.loader');

    loader.addClass('loader-active');

    $.ajax({
      type: 'POST',
      url: '/eaton/content/assetzipdownload.zip',
      data: JSON.stringify(requestParams),
      headers: { 'Content-Type': 'application/zip' },
      success: function (resultData) {
        loader.removeClass('loader-active');

        if (resultData) {
          let byteCharacters = atob(resultData);
          let byteNumbers = new Array(byteCharacters.length);
          for (let i = 0; i < byteCharacters.length; i++) {
            byteNumbers[i] = byteCharacters.charCodeAt(i);
          }
          let byteArray = new Uint8Array(byteNumbers);
          let blob;
          let fileType = 'application/zip';
          let fileNameExt = zipFileName;
          if (window.navigator.msSaveBlob) {
            blob = new Blob([byteArray], { type: fileType });
            window.navigator.msSaveBlob(blob, fileNameExt);
          } else {
            let blob = new Blob([byteArray], { type: fileType });
            let link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = fileNameExt;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
          }
        }
      },
      error: function () {
        loader.removeClass('loader-active');
      }
    });
  });

  /**
   * Toggle class icon
   */
  const toggleIcon = (event) => {

    $(event.target)
      .prev('.panel-heading')
      .find('.icon')
      .toggleClass('icon-sign-plus icon-sign-minus');

  };
  const toggleIconQR = (event) => {

    $(event.target)
      .prev('.panel')
      .find('.icon')
      .toggleClass('icon-sign-plus icon-sign-minus');

  };

  /**
   * Expand accordion when link is clicked
   */
  const expand = (event) => {

    let idHeading = $(event.target).closest('.module-anchor-links__list-link').attr('data-scroll-to');
    $accordion.find($(idHeading)).next().collapse('show');

  };

  /**
   * Initialize and Expose public methods
   */

  if ($component.length > 0) {
    init();
  }


};

/**
 * jQuery for custom resource list design.
 */

$(document).ready(function () {
  App.resourceList();
  let customResourcePopup = $('.custom-resource-popup ');
  $('.popup-content').on('click', function () {
    let a = $(this).closest('li');
    let bigImage = $(a).attr('data-rendition-big-image') || $(a).attr('data-rendition-319x319');
    let getSecureIcon = $(a).find('.resource-list__secure-icon').html() || '';
    $('.custom-resource-popup .resource-big-image').attr('src', bigImage);
    $('.custom-resource-popup .resource-big-title').html($(this).attr('data-title'));
    $('.custom-resource-popup .resource-big-description').html($(this).attr('data-desc'));
    $('.custom-resource-popup .resource-big-secure').html(getSecureIcon);
    $('.custom-resource-popup .resource-big-downloadlink').attr('href', bigImage);
    $('.custom-resource-popup .resource-big-div').css('background-image', 'url(' + bigImage + ')');
    customResourcePopup.removeClass('hide');
    customResourcePopup.addClass('show');
  });

  $('.resource-big-close').on('click', function () {
    $('.custom-resource-popup .resource-big-image').attr('src', '');
    $('.custom-resource-popup .resource-big-title').html('');
    $('.custom-resource-popup .resource-big-description').html('');
    $('.custom-resource-popup .resource-big-downloadlink').attr('href', '#');
    $('.custom-resource-popup .resource-big-secure').html('');
    $('.custom-resource-popup .resource-big-div').css('background-image', 'url("")');
    customResourcePopup.removeClass('show');
    customResourcePopup.addClass('hide');
  });

  let resourceListContainer = document.getElementsByClassName('resource-list');
  if (resourceListContainer.length > 0) {
    const bulkDownloadContainer = document.getElementsByClassName('bde-activated');
    if (bulkDownloadContainer.length > 0) {
      bulkDownloadContainer[0].dispatchEvent(new CustomEvent('responseRendered'));
    }
    $(document).on('click', 'input[name=toDownload]', function () {

      let checkedInputs = document.querySelectorAll('input[name=toDownload]:checked');
      let tempCount = checkedInputs.length;
      let actualLimitSet = parseInt($('#selected-files-drawer-box').attr('data-download-file-count-limit'));
        // For size limit, check this condition: "checkSize > maxZipLimit" instaed of 'actualLimitSet < tempCount' in folloing if.
      if (actualLimitSet < tempCount) {
        return false;
      }
      let allSelectedFiles = [];
      $(checkedInputs).each(function () {
        if (!$(this).hasClass('disabledbutton')) {
            // check id and if matched pushed it to selected queue.
          allSelectedFiles.push({
            id: $(this).attr('id'),
            title: $(this).attr('title'),
            url: $(this).attr('data-url')
          });
        }
      });
      sessionStorage.setItem('selectedFiles', JSON.stringify(allSelectedFiles));

        // Check and set the counter
      let selectionCount = 0;
      $(checkedInputs).each(function () {
        if (!$(this).hasClass('disabledbutton')) {
          selectionCount++;
        }
      });
      if (selectionCount === 0) {
        $('#bulkDownloadContainer').addClass('disabledbutton');
        $('#selecteddownload').html('(0)');
      } else {
        $('#bulkDownloadContainer').removeClass('disabledbutton');
        $('#selecteddownload').html('(' + selectionCount + ')');
      }
        // Function to add element in drawer for bulk download.funtion defination in asset-bulk-download-email.js
        /* eslint-disable no-undef */
      addRemovefileForBDE();

    });
  }
});
