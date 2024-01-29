const CMD_VEHICLE = 'vehicle';
$(document).ready(function() {

  if (performance.navigation.type === 2) {
    location.reload(true);
  }

  $('.reset').on('click', function() {
    location.reload(true);
  });

  $('.guide-header-bar-wrapper, .guide-mobile-navigator, .wizard-navigators').hide();
  $('.ctabutton').addClass('container');
  $('.form-parsys > .ctabutton').removeClass('container');


  let placeholderValue = $('#emptyValue').html();

  Array.prototype.remove = function(value) {
    let idx = this.indexOf(value);
    if (idx !== -1) {
      return this.splice(idx, 1);
    }
    return false;
  };

  function arraySearch(nameKey, ArrayName) {
    for (let i = 0; i < ArrayName.length; i++) {
      if (ArrayName[i].formElementName === nameKey) {
        return i;
      }
    }
  }

  let blockListPropSub = [];
  let axleDataAr = [];

  $("select[id^='guideContainer-rootPanel-panel'],select[id^='guideContainer-rootPanel-guidedropdownlist']").on('change', function(e) {

    $(this).attr('checked', 'true');
    let formName = $(this).parent().attr('data-info-type');
    let formElementName = $(this).parent().attr('id');
    let formElementValue = $(this).val();

    let data = {};
    let insertObj = {
      formElementName: formElementName,
      formElementValue: formElementValue
    };
    if (formName === 'axle') {
      let tempResult = arraySearch(formElementName, axleDataAr);
      axleDataAr[tempResult] = insertObj;
      data = {
        blockListPropSub: JSON.stringify(axleDataAr)
      };
    } else {
      let tempResult = arraySearch(formElementName, blockListPropSub);
      blockListPropSub[tempResult] = insertObj;
      data = {
        blockListPropSub: JSON.stringify(blockListPropSub)
      };
    }
    $.ajax({
      url: '/eaton/vgproductselector/fetchoptions',
      data: data,
      success: function(data) {
        appendOptions(data,formName);
      },
      error: function(data) {
      },

      timeout: 30000,
      type: 'POST'
    });

    function appendOptions(data,cmd) {
      if (typeof (data) !== undefined && typeof (data.response) !== undefined && data.response && data.response.status === 'Success'
       && typeof (data.response.binning) !== undefined && typeof (data.response.binning.binningSet) !== undefined ) {
        for (let _i in data.response.binning.binningSet) {
          let binningJsonObject = data.response.binning.binningSet[_i];
          if (typeof (binningJsonObject) !== undefined && formElementName !== binningJsonObject.bsId && binningJsonObject.bsId !== 'Portfolio_Rating') {
            let element = $('#' + binningJsonObject.bsId);
            $(element).children().empty();
            let _isElementChecked = $(element).children().attr('checked');

            if (_isElementChecked !== 'checked') {
              $(element).children().append("<option selected='selected'>" + placeholderValue + '</option>');
            }
            if (typeof (binningJsonObject.bin) !== undefined) {
              for (let _j in binningJsonObject.bin) {
                let _isValueAppened = 'false';
                if (binningJsonObject.bin[_j].label !== undefined) {
                  let selectOption = document.createElement('option');
                  selectOption.text = binningJsonObject.bin[_j].label;

                     /* Checking already selected dropdown elements */
                  let filteredElementsArray = [];
                  if (cmd === CMD_VEHICLE) {
                    selectOption.value = binningJsonObject.bin[_j].label + binningJsonObject.bsId;
                    filteredElementsArray = blockListPropSub;
                  } else {
                    selectOption.value = binningJsonObject.bin[_j].value;
                    filteredElementsArray = axleDataAr;
                  }
                  if (typeof (filteredElementsArray) !== undefined) {
                    for (let _k in filteredElementsArray) {
                      if (cmd === CMD_VEHICLE && (filteredElementsArray[_k].formElementValue === binningJsonObject.bin[_j].label)) {
                        $(element).children().append(selectOption);
                        _isValueAppened = 'true';
                      } else if (cmd !== CMD_VEHICLE && (filteredElementsArray[_k].formElementValue === binningJsonObject.bin[_j].value)) {
                        $(element).children().append(selectOption);
                        _isValueAppened = 'true';
                      }
                    }
                  }
                  if (_isValueAppened !== 'true') {
                    $(element).children().append(selectOption);
                  }
                }
              }
            }

          }
        }
      }
    }

  });


  $("select[id^='guideContainer-rootPanel-panel'],select[id^='guideContainer-rootPanel-guidedropdownlist']").each(function() {

    let formElementName = $(this).parent().attr('id');
    let formName = $(this).parent().attr('data-info-type');
    let formElementValue = $(this).val();
    let tempObj = {
      formElementName: formElementName,
      formElementValue: formElementValue
    };
        //
    if (formName === 'axle') {
      axleDataAr.push(tempObj);
    } else {
      blockListPropSub.push(tempObj);

    }
  });

});
