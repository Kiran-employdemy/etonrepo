//-----------------------------------
// List Component - Download Cad
// -----------------------------------
'use strict';

let cadResult = '';
let ajaxTriggerFlag = '';
let cadInvalidPlid = '';
let cadXMLURL = '';
/* eslint-disable no-unused-vars  */
let cadResult1 = '';
let callCount = 0;
class DownloadCad {

  constructor() { }

  init() {
    this.showFilters();
    this.addCadListner();
    if (document.querySelector('generate-cad-btn') !== null) {
      document.getElementById('generate-cad-btn').tabIndex = '0';
    }

  }

  showFilters() {


    if (document.querySelector('#filter-data-to-render') === null) {
      return false;
    } else {
      let json = document.querySelector('#filter-data-to-render').innerHTML;
      let obj = JSON.parse(json);
      // $('#filter-data-to-render').remove();
      let filterData = [];
      let filterCat = [];
      let displayFilter = '';
      let countFilter = 0;
      let hideClass = '';
      let filterTempArray = [];
      let filterLabel = '';
      filterData = obj.format;
      filterCat = filterData.map(item => item.cad).filter((value, index, self) => self.indexOf(value) === index);

      for (let i = 0; i < filterCat.length; i++) {
        countFilter = 0;
        filterTempArray = [];
        displayFilter += '<div class="filter-group list-' + filterCat[i] + '"><button class="faceted-navigation__header button--reset position-r"  data-toggle="collapse" data-target="#' + filterCat[i] + '" aria-expanded="true" aria-controls="' + filterCat[i] + '">';
        displayFilter += filterCat[i].toUpperCase() + ' Formats';
        displayFilter += '<i class="icon icon-sign-minus" aria-hidden="true"></i><i class="icon icon-sign-plus faceted-navigation__icon-sign-plus" aria-hidden="true"></i></button>';
        displayFilter += '<div id="' + filterCat[i] + '" class="collapse in" aria-expanded="true" ><fieldset><ul class="faceted-navigation__list faceted-navigation__list--unfiltered ">';
        for (let j = 0; j < filterData.length; j++) {
          hideClass = '';
          if (filterData[j].cad === filterCat[i]) {
            if (countFilter > 5) {
              hideClass = 'd-none';
            } else {
              hideClass = '';
            }
            countFilter++;

            if (filterTempArray.indexOf(filterData[j].name) !== -1) {
              filterLabel = filterData[j].qualifier;
            } else {
              filterLabel = filterData[j].name;
            }
            filterTempArray.push(filterData[j].name);
            displayFilter += '<li class="faceted-navigation__list-item ' + hideClass + '"><div class="faceted-navigation__facet-value"><label class="faceted-navigation__facet-value-label">';
            displayFilter += '<input type="checkbox" class="input cad-filter-checkbox" value="' + filterData[j].qualifier + '" name="' + filterData[j].cad + '" class="input cad-filter-checkbox" >';
            displayFilter += '<span class="inner"><bdi>' + filterLabel + '</bdi></span>';
            displayFilter += '</label></div></li>';
          }
        }

        displayFilter += '</ul></fieldset>';

        if (countFilter > 2) {
          displayFilter += '<span class="more-less-link view-more-list" id="list-' + filterCat[i] + '"> ' + $('#filters-div-holder').attr('data-view-more-caption') + ' (' + countFilter + ')</span>';
        } else {
          displayFilter += '';
        }

        displayFilter += '</div></div>';

      }


      $('#filters-div-holder').html(displayFilter);
    }

  }

  addCadListner() {
    $('#generate-cad-btn').click(function () {

      // get data and remove extra spaces
      let x = document.querySelector('.cad-download-textarea').value.replace(/\s/g, ',');

      // Sanitzing string for ,",/,',.,",\,[,],|,{,},),(,!,$,%,*,|,~,`,?,#,& characters.
      x = x.replace(/["\/\'\.\"\\\[\]\|\{\}\)\(\!\$\%\*\|\~\`\?\#\&\!\@\^\=]/g, '');
      let ajaxInputValue = [];
      let maxInputFilter = '';

      // convert string to string array.
      ajaxInputValue = x.split(',');

      // Sanitize string array by removing duplicate value
      ajaxInputValue = ajaxInputValue.reduce((noDupArr, entry) => {
        if (noDupArr.includes(entry)) {
          return noDupArr;
        } else {
          return [...noDupArr, entry];
        }
      }, []);

      // Sanitize string array by removing null, undefine & blank values.
      ajaxInputValue = ajaxInputValue.filter(function (el) {
        if (el !== null || el !== null || el !== undefined) {
          return el;
        }
      });

      let ajaxFilterValue = [];
      // array for selected filters.
      $('.download-cad-filter-wrapper input[type=checkbox]:checked').each(function () {
        ajaxFilterValue.push($(this).val());
      });

      if (ajaxFilterValue.length === 0 && ajaxInputValue.length === 0) {
        ajaxTriggerFlag = 'no-filter-input-err-msg';
      } else {
        if (ajaxFilterValue.length === 0) {
          ajaxTriggerFlag = 'no-filter-err-msg';
        } else {
          if (ajaxInputValue.length === 0) {
            ajaxTriggerFlag = 'no-input-err-msg';
          } else {
            maxInputFilter = sanitizeTextArea();
            if (maxInputFilter === 'max-article-err-msg') {
              ajaxTriggerFlag = 'max-article-err-msg';
            } else {
              ajaxTriggerFlag = 'NA';
            }
          }
        }
      }

      if (ajaxTriggerFlag === 'NA') {
        $('#show-err-msg').html('');
        $('#show-err-msg').addClass('d-none');
      } else {
        $('#show-err-msg').removeClass('d-none');
        $('#show-err-msg').html($('#show-err-msg').attr('data-' + ajaxTriggerFlag));
        return false;
      }

      $('.loader-model').removeClass('d-none');
      $.ajax({
        type: 'POST',
        url: '/eaton/generateCadDataServlet',
        // Sample data string data: { value: "{'format':'[\"JPG2D\",\"JPEGFILE\"]','articleNo': '[\"104401\",\"276550\"]'}" },
        data: { value: "{'articleNo':'[" + ajaxInputValue + "]','format': '[" + ajaxFilterValue + "]'}" },
        success: function (msg) {
          cadResult = msg;
          // Get invalid article no.
          if (cadResult.invalidPlid !== 'undefined' && cadResult.invalidPlid !== undefined && cadResult.invalidPlid !== null && cadResult.invalidPlid !== '\n' && cadResult.invalidPlid !== '') {
            cadInvalidPlid = cadResult.invalidPlid;
          } else {
            cadInvalidPlid = 'NA';
          }

          // Re-initalizing as response get changes. Hence taking cadResult one level dipper to work with further code.
          if (cadResult.cadXMLURL === ' ' || cadResult.cadXMLURL === 'undefined' || cadResult.cadXMLURL === undefined) {
            cadXMLURL = 'NODATA';
          } else {
            cadXMLURL = cadResult.cadXMLURL;
          }



          setTimeout(function () {
            clearPrevCadData();
            if (cadXMLURL === 'NODATA') {
              displayResultFromXML(cadXMLURL);
            } else {
              callCount = 0;
              showResultFromXML(cadXMLURL);
            }

          }, 6000);
        },
        error: function () {
          setTimeout(function () {
            clearPrevCadData();
            displayResultFromXML('NODATA');
          }, 3000);
        }

      });

    });


    $('.download-cad-filter-wrapper input[type=checkbox]').click(function () {
      let tempVar = sanitizeForMaxCheckboxSelection();
      if (tempVar === 'max-filtertype-error') {
        $('#show-err-msg').removeClass('d-none');
        $('#show-err-msg').html($('#show-err-msg').attr('data-' + tempVar));
        $('#cadArticleTA').focus();
        return false;
      } else {
        $('#show-err-msg').html('');
        $('#show-err-msg').addClass('d-none');
      }
    });

    $('.cad-download-textarea').blur(function () {
      let tempVar = sanitizeTextArea();
      if (tempVar === 'max-article-err-msg') {
        $('#show-err-msg').removeClass('d-none');
        $('#show-err-msg').html($('#show-err-msg').attr('data-' + tempVar));
        return false;
      } else {
        $('#show-err-msg').html('');
        $('#show-err-msg').addClass('d-none');
      }
    });

    function sanitizeForMaxCheckboxSelection() {
      let countCheckBox = 0;
      let retunValue = '';
      $('.download-cad-filter-wrapper input[type=checkbox]:checked').each(function () {
        countCheckBox++;
      });

      if (countCheckBox > 5) {
        $(this).prop('checked', false);
        retunValue = 'max-filtertype-error';
      } else {
        retunValue = 'NA';
      }

      return retunValue;
    }


    function sanitizeTextArea() {
      // get data and remove extra spaces
      let x = document.querySelector('.cad-download-textarea').value.replace(/\s/g, ',');
      x = x.replace(/["\/\'\.\"\\\[\]\|\{\}\)\(\!\$\%\*\|\~\`\?\#\&\!\@\^\=]/g, '');
      let ajaxInputValueOnBlur = [];
      let retunValue = '';

      // convert string to string array.
      ajaxInputValueOnBlur = x.split(',');

      // Sanitize string array by removing duplicate value
      ajaxInputValueOnBlur = ajaxInputValueOnBlur.reduce((noDupArr, entry) => {
        if (noDupArr.includes(entry)) {
          return noDupArr;
        } else {
          return [...noDupArr, entry];
        }
      }, []);

      // Sanitize string array by removing null, undefine & blank values.
      ajaxInputValueOnBlur = ajaxInputValueOnBlur.filter(function (el) {
        if (el !== null || el !== null || el !== undefined) {
          return el;
        }
      });

      document.querySelector('.cad-download-textarea').value = ajaxInputValueOnBlur;

      if (ajaxInputValueOnBlur.length > 5) {
        retunValue = 'max-article-err-msg';
      } else {
        retunValue = 'NA';
      }
      return retunValue;
    }


    function clearPrevCadData() {
      $('#cad-download-result-row').html('');
      $('.response-msg-value').html('');
      $('#zip-link-name-url').html('');
      $('#no-data-err-msg').addClass('d-none');
      $('.cad-result-div').addClass('d-none');
      $('.reponse-message').addClass('d-none');
      $('#zip-link-name-url').addClass('d-none');
      $('.download-link-text').addClass('d-none');
      $('.result-table').addClass('d-none');
      $('.cad-result-div').addClass('d-none');
      $('.result-table').addClass('d-none');
      $('.cad-result-div').addClass('d-none');
    }

    /**
     * Function to read data from xml received in ajax call.
     */
    function showResultFromXML(cadResultXML) {
      $.ajax({
        type: 'GET',
        url: cadResultXML,
        dataType: 'xml',
        success: function (xml) {
          displayResultFromXML(xml);
        },
        error: function error() {
          holdAndReadXMLAgain(cadResultXML);
        }
      });
    }

    function holdAndReadXMLAgain(xmlURL) {


      setTimeout(function () {
        callCount++;


        showResultFromXML(xmlURL);
      }, 20000);
    }

    function displayResultFromXML(cadResult1) {

      if (cadResult1 === 'NODATA' || callCount > 5) {
        $('.cad-result-div').addClass('d-none');
        $('#show-err-msg').removeClass('d-none');
        $('#show-err-msg').html($('#show-err-msg').attr('data-no-data-err-msg'));
        $('.loader-model').addClass('d-none');
        return false;
      } else {

        $('#show-err-msg').addClass('d-none');
        $('#show-err-msg').html('');
        // When invalid id received.
        if (cadInvalidPlid === 'NA' || cadInvalidPlid === '') {
          $('.reponse-message').addClass('d-none');
          $('.response-msg-value').html('');
        } else {
          $('.reponse-message').removeClass('d-none');
          $('.response-msg-value').html(cadInvalidPlid.replace(/,*$/, ''));
        }

        if (cadResult1.getElementsByTagName('ZIPFILE').length === 0) {
          $('#zip-link-name-url').html('');
          $('#zip-link-name-url').addClass('d-none');
          $('.download-link-text').addClass('d-none');
        } else {
          let hrefVal = cadXMLURL.replace('download.xml', '');
          hrefVal = hrefVal + '/' + cadResult1.getElementsByTagName('ZIPFILE')[0].childNodes[0].nodeValue;
          $('#zip-link-name-url').html('<a href="' + hrefVal + '" title="' + cadResult1.getElementsByTagName('ZIPFILE')[0].childNodes[0].nodeValue + '" id="zip-link-url"> <i class="icon icon-download" aria-hidden="true"></i> <span id="">' + cadResult1.getElementsByTagName('ZIPFILE')[0].childNodes[0].nodeValue + '</span></a>');
          $('#zip-link-name-url').removeClass('d-none');
          $('.download-link-text').removeClass('d-none');
        }

        let displayTable = '';
        // excute loop if length not undefined. undfined means single row data or no data.
        if (cadResult1.getElementsByTagName('PART').length !== 0) {
          for (let i = 0; i < cadResult1.getElementsByTagName('PART').length; i++) {
            // start row
            displayTable += '<tr>';
            // Article no.
            displayTable += '<td>' + cadResult1.getElementsByTagName('NB')[i].childNodes[0].nodeValue + '</td>';
            // Name
            displayTable += '<td>' + cadResult1.getElementsByTagName('NN')[i].childNodes[0].nodeValue + '</td>';
            //	Part no.
            displayTable += '<td>' + cadResult1.getElementsByTagName('VIEW')[i].childNodes[0].nodeValue + '</td>';
            // File name
            displayTable += '<td>' + cadResult1.getElementsByTagName('FILENAME')[i].childNodes[0].nodeValue + '</td>';
            // End row.
            displayTable += '<tr>';
          }
          $('.result-table').removeClass('d-none');
          $('#cad-download-result-row').html(displayTable);
          $('.cad-result-div').removeClass('d-none');
        } else {
          // If for single row data available
          if (cadResult1.getElementsByTagName('PART').length === 1) {
            // start row
            displayTable += '<tr>';
            // Article no.
            displayTable += '<td>' + cadResult1.getElementsByTagName('NB')[0].childNodes[0].nodeValue + '</td>';
            // Name
            displayTable += '<td>' + cadResult1.getElementsByTagName('NN')[0].childNodes[0].nodeValue + '</td>';
            //	Part no.
            displayTable += '<td>' + cadResult1.getElementsByTagName('VIEW')[0].childNodes[0].nodeValue + '</td>';
            // File name
            displayTable += '<td>' + cadResult1.getElementsByTagName('FILENAME')[0].childNodes[0].nodeValue + '</td>';
            // End row.
            displayTable += '<tr>';
            $('.result-table').removeClass('d-none');
            $('#cad-download-result-row').html(displayTable);
            $('.cad-result-div').removeClass('d-none');
          } else {
            $('.result-table').addClass('d-none');
            $('.cad-result-div').addClass('d-none');
            $('#show-err-msg').removeClass('d-none');
            $('#show-err-msg').html($('#show-err-msg').attr('data-no-data-err-msg'));
          }
        }

      }

      $('.loader-model').addClass('d-none');
      return false;
    }

    function showResult(cadResult) {
      if (cadResult === 'NA') {
        $('.cad-result-div').addClass('d-none');
        $('#show-err-msg').removeClass('d-none');
        $('#show-err-msg').html($('#show-err-msg').attr('data-no-data-err-msg'));
        return false;
      } else {
        $('#show-err-msg').addClass('d-none');
        $('#show-err-msg').html('');
        // When invalid id received.
        if (cadInvalidPlid === 'NA') {
          $('.reponse-message').addClass('d-none');
        } else {
          $('.reponse-message').removeClass('d-none');
          $('.response-msg-value').html(cadInvalidPlid.replace(/,*$/, ''));
        }

        if (cadResult.ORDERNO === undefined || cadResult.ORDERNO === ' ' || cadResult.ORDERNO === '' || cadResult.ORDERNO === 'undefined') {
          $('#zip-link-name-url').html('');
          $('#zip-link-name-url').addClass('d-none');
          $('.download-link-text').addClass('d-none');
        } else {
          let hrefVal = 'https://webapi.partcommunity.com/partserver/preview/' + cadResult.ORDERNO + '/' + cadResult.SECTION.ZIPFILE;
          $('#zip-link-name-url').html('<a href="' + hrefVal + '" title="' + cadResult.SECTION.ZIPFILE + '" id="zip-link-url"> <i class="icon icon-download" aria-hidden="true"></i> <span id="">' + cadResult.SECTION.ZIPFILE + '</span></a>');
          $('#zip-link-name-url').removeClass('d-none');
          $('.download-link-text').removeClass('d-none');
        }

        let displayTable = '';
        // excute loop if length not undefined. undfined means single row data or no data.
        if (cadResult.SECTION.CONTENTS.PART.length !== undefined) {
          for (let i = 0; i < cadResult.SECTION.CONTENTS.PART.length; i++) {
            // start row
            displayTable += '<tr>';
            // Article no.
            displayTable += '<td>' + cadResult.SECTION.CONTENTS.PART[i].NB + '</td>';
            // Name
            displayTable += '<td>' + cadResult.SECTION.CONTENTS.PART[i].NN + '</td>';
            //	Part no.
            displayTable += '<td>' + cadResult.SECTION.CONTENTS.PART[i].VIEW + '</td>';
            // File name
            displayTable += '<td>' + cadResult.SECTION.CONTENTS.PART[i].FILENAME + '</td>';
            // End row.
            displayTable += '<tr>';
          }
          $('.result-table').removeClass('d-none');
          $('#cad-download-result-row').html(displayTable);
          $('.cad-result-div').removeClass('d-none');
        } else {
          // If for single row data available
          if (cadResult.SECTION.CONTENTS.PART !== undefined) {
            // start row
            displayTable += '<tr>';
            // Article no.
            displayTable += '<td>' + cadResult.SECTION.CONTENTS.PART.NB + '</td>';
            // Name
            displayTable += '<td>' + cadResult.SECTION.CONTENTS.PART.NN + '</td>';
            //	Part no.
            displayTable += '<td>' + cadResult.SECTION.CONTENTS.PART.VIEW + '</td>';
            // File name
            displayTable += '<td>' + cadResult.SECTION.CONTENTS.PART.FILENAME + '</td>';
            // End row.
            displayTable += '<tr>';
            $('.result-table').removeClass('d-none');
            $('#cad-download-result-row').html(displayTable);
            $('.cad-result-div').removeClass('d-none');
          } else {
            $('.result-table').addClass('d-none');
            $('.cad-result-div').addClass('d-none');
            $('#show-err-msg').removeClass('d-none');
            $('#show-err-msg').html($('#show-err-msg').attr('data-no-data-err-msg'));
          }
        }

      }

    }

    $('.more-less-link').click(function () {
      let listName = $(this).attr('id');
      if ($(this).hasClass('view-more-list')) {
        $(this).removeClass('view-more-list');
        $(this).addClass('view-less-list');
        $('.' + listName + ' ul > li').removeClass('d-none');
        $(this).html($('#filters-div-holder').attr('data-view-less-caption'));
      } else {
        $(this).removeClass('view-less-list');
        $(this).addClass('view-more-list');
        $('.' + listName + ' ul > li').addClass('d-none');
        $('.' + listName + ' ul > li:lt(5)').removeClass('d-none');
        let countFilter = $('.' + listName + ' ul > li.d-none').length;
        $(this).html($('#filters-div-holder').attr('data-view-more-caption') + ' (' + countFilter + ')');
        $('#cadArticleTA').focus();
      }

    });

  }

}

(function () {
  new DownloadCad().init();
}());

