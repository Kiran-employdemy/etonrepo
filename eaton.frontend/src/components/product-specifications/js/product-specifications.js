const currentDate = new Date();
let specificationTab;
let resourceTab;
let overviewTab;
let skunumber;

class Prodspecification {
  constructor() {}


  init() {
    skunumber = $('.eaton-product-tabs__description').children('.b-heading-h5').text();
    // eslint-disable-next-line no-unused-vars
    let dataSheetName = $('.eaton-product-tabs__description').children('.b-heading-h5').text().trim() + '.xlsx';
    // Do not append the SKU no. so just bypass - $('#export-btn').append(dataSheetName);
    $('#sku-number').attr('data-sku-number', skunumber);
    $('#sku-number').attr('data-sku-extension', 'Excel (.XLSX)');
    $('#sku-number').html($('#sku-number').attr('data-sku-file-anchor-text') + ' ' + $('#sku-number').attr('data-sku-extension'));
    this.finalTable();
  }

  specificationData() {
    specificationTab = $('.module-table__row').map(function () {
      let issue = $(this);
      let tdline = issue.find('.module-table__col').map(function () {
        return '<td data-f-bold = "true"><b>' + $(this).parent().parent().parent().find('.module-table__head').text() + '</b></td><td>' + $(this).text();
      }).get().join('</td>');
      return '<tr>' + tdline + '</td>';
    }).get().join('</tr>');
  }

  resourceData() {
    resourceTab = $('.excel-title-resource-tab').map(function () {
      let issue = $(this);
      let tdline = issue.find('.resource-list__title-link').map(function () {
        return '<td data-f-bold = "true"><b>' + $(this).attr('name') + '</b></td><td data-underline="true" data-f-color="0000FF" data-hyperlink= "' + $(this).attr('href') + '"><a href="' + $(this).attr('href') + '" >' + $(this).text();
      }).get().join('</td>');
      return '<tr>' + tdline + '</td>';
    }).get().join('</tr>');
  }

  overviewData() {
    overviewTab = $('.module-product-detail-card').map(function () {
      let issue = $(this);
      let tdline = issue.find('.excel_class').map(function () {
        return '<td>' + $(this).text();
      }).get().join('</td>');
      return '<tr>' + tdline + '</td>';
    }).get().join('</tr>');
  }

  removeDuplicates() {
    let tablelen = $('#excel-table tr').length;
    let tableRows = $('#excel-table').find('tbody tr');

    for (let i = 1;i < tablelen;i++) {
      let tRow = $(tableRows[i]);
      let cell1Content = tRow.find('td:nth-child(3)').text();
      let cell2Content = tRow.find('td:nth-child(1)').text();
      if (cell1Content === cell2Content) {
        tRow.find('td:nth-child(3)').remove();
      }
    }

    let seen = {};
    $('#excel-table tr td').each(function() {
      let subheaderTxt = $(this).text();
      if (seen[subheaderTxt]) {
        $(this).text('');
      }
      else {
        seen[subheaderTxt] = true;
      }
    });

  }

  tablePreparations() {
    let date = '<tr><td data-f-bold = "true"><b>Date</b></td><td>' + currentDate.toDateString() + '</td></tr>';
    specificationTab = '<table id="excel-table" data-cols-width="30,40,50">' + overviewTab + specificationTab + resourceTab + date + '</tr></table>';
    $('#excelconverttable').append(specificationTab);

    let prodspec = new Prodspecification();
    prodspec.removeDuplicates();

    $('#excel-table').find('tr').each(function (i, el) {
      $(this).find('td').attr('data-a-wrap','true');
      $(this).find('td').attr('data-a-v','middle');

    });
  }

  finalTable() {
    $('.excel__convert-download').removeClass('hide');
    const $accordion = $('.secondary-content-accordion__panel');
    if ($accordion && $accordion.length > 0) {
      $accordion.on('hidden.bs.collapse', function (e) {
        $(e.target).prev('.panel-heading').find('.secondary-content-accordion-datasheet__icons').toggleClass('icon-sign-plus icon-sign-minus');
      });
      $accordion.on('shown.bs.collapse', function (e) {
        $(e.target).prev('.panel-heading').find('.secondary-content-accordion-datasheet__icons').toggleClass('icon-sign-plus icon-sign-minus');
      });
    }
    $('#export-btn').on('click', function(e) {
      let prodspec = new Prodspecification();
      prodspec.overviewData();
      prodspec.specificationData();
      prodspec.resourceData();
      prodspec.tablePreparations();
      // eslint-disable-next-line no-undef
      TableToExcel.convert(document.getElementById('excel-table'), {
        name: $.trim(skunumber) + '.xlsx',
        sheet: {
          name: $.trim(skunumber)
        }
      });
      $('#excel-table').remove();
    });
  }
}
(function() {
  const prodspec = $('#excelconverttable').length;
  if (prodspec > 0) {
    new Prodspecification().init();
  }
}());

/** Script to handle page scroll in sku page. */
$(document).ready(function() {
  let target = window.location.hash;
  if (target === '#Specifications' || target === '#Resources') {
    $('div#Specifications').css('margin-top','60px');
    $('div#Specifications').css('margin-bottom','60px');
    $('div#Resources').css('margin-top','60px');
  }
  let offset = 200; // You can change this value as per your need.
  if ($(target).length > 0) {
    $('html, body').animate({
      scrollTop: $(target).offset().top - offset
    }, 600);
  }
});

