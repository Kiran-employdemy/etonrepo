$(document).ready(function () {
  $('#agent').prop('selectedIndex',0);
  $('#agent').on('change', function () {
    const divisionIds = $(this).val();
    const items = divisionIds.split(',');
    $('#division').empty();
    const selectYourDivisionText = $('.agent-report-division').attr('data-select-your-division-label');
    $('#division').append('<option value="selectYourDivision">' + selectYourDivisionText + '</option>');
    $.each(items, function (key, value) {
      const vSplit = value.split(':');
      if (vSplit[1] !== undefined) {
        $('#division').append('<option value="' + vSplit[0] + '">' + vSplit[1] + '</option>');
      }
    });
    $('#reports').prop('disabled', true);
    $('.fileDownload').empty();
    $('#reports-data').hide();
    $('#no-rows-alert').hide();
    $('.agent-loader').show();

  });

  $('#reports').click(function (e) {
    e.preventDefault();

    $.ajax({
      type: 'POST',
      url: '/eaton/agentreports/getReports',
      data: {
        agentId: $('#agent option:selected').html(),
        division: $('#division').val()
      },
      success: function success(result) {
        $('.agent-loader').show();
        if (!$.trim(result)) {
          $('#no-rows-alert').show();

        } else {
          $('#no-rows-alert').hide();
          let html = void 0;
          $.each(result, function (index, value) {
            html += '<tr>' + '<td class="data-td"> <a id="' + value.DDocName + '" data-value="' + value.DDocName + '">' + value.DOriginalName + '</a></td>' + '<td>' + value.DInDate + '</td>' + '</tr>';
          });

          $('.table-striped tbody').html(html);

        }
        $('.agent-loader').hide();
      }


    });
    $('#reports-data').show();

  });

  $('.fileDownload').click(function (e) {
    e.preventDefault();
    let docName = e.target.id;
    let fileName = $(event.target).text();
    $.ajax({
      type: 'POST',
      url: '/eaton/agentreports/getFileDownload',
      data: {
        docName: docName
      },
      xhrFields: {
        responseType: 'blob'
      },
      success: function success(result) {
        let blob = new Blob([result]);
        let link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = fileName;
        link.click();
      }
    });
  });
  $('#division').on('change', function() {
    if ($(this).val() === 'selectYourDivision') {
      $('#reports').prop('disabled', true);
    } else {
      $('#reports').prop('disabled', false);
    }
    $('.fileDownload').empty();
    $('#reports-data').hide();
    $('#no-rows-alert').hide();
    $('.agent-loader').show();
  });


  function sortTable(f,n) {
    let rows = $('#agent-table tbody  tr').get();

    rows.sort(function(a, b) {

      let A = getVal(a);
      let B = getVal(b);

      if (A < B) {
        return -1 * f;
      }
      if (A > B) {
        return 1 * f;
      }
      return 0;
    });

    function getVal(elm) {
      let v = $(elm).children('td').eq(n).text().toUpperCase();
      if ($.isNumeric(v)) {
        v = parseInt(v,10);
      }
      return v;
    }

    $.each(rows, function(index, row) {
      $('#agent-table').children('tbody').append(row);
    });
  }
  /* eslint-disable */
  let f_sl = 1;
  let f_nm = 1;
  $('#sl').click(function() {
    f_sl *= -1;
    let n = $(this).prevAll().length;
    sortTable(f_sl,n);
  });
  $('#nm').click(function() {
    f_nm *= -1;
    let n = $(this).prevAll().length;
    sortTable(f_nm,n);
  });
/* eslint-disable */
});
