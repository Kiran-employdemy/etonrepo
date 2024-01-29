//-----------------------------------
// Backshell Component
//-----------------------------------

$(document).ready(function () {
  let backshellContainer = $('.backshell-config');
  $('.backshell-config, #part-number-meta, .bs-tolerance, .part-number-display-banner').addClass('u-hide');
  $('select').niceSelect();

  // Component ID
  let idComponentProduct = '#edit-product';
  let idComponentSeries = '#edit-series';
  let idComponentPartNumber = '#edit-part-number';
  let technicalDrawing = $('.part-image-drawing');

  // Variables to build part number
  let finalProduct = '';
  let finalProductId = '';
  let finalSeries = '';
  let finalPartNumber = '';
  let finalPartNumberId = '';
  let finalTolarence = '';

  // Helper variables
  let finalLeftMetaArray = [];
  let finalPartNumberArray = [];
  let flagNote = false;

  $.ajax({
    type: 'GET',
    url: `${ backshellContainer.attr('data-resource') }.json`,
    success: function (response) {
      if (response) {
        let searchResult = '';
        if (typeof response === 'string') {
          searchResult = JSON.parse(response);
        } else {
          searchResult = response;
        }
        showFormUI(searchResult);
      }
    },
    error: function (response) {
      console.log('ERROR', response);
    }
  });

  function showFormUI(response) {
    backshellContainer.removeClass('u-hide');
    $('#default-message').addClass('u-hide');
    let submitButton = $('#souriau-toolkit-backshell-main-form');
    let productsJson = response && response.products ? response.products : [];
    let partNumberComponentResponseJson = [];

    // Populate product list
    setDropdownOptions(idComponentProduct, productsJson, 'code', 'name');

    $('select').on('change', function () {
      onChangeCheckElement(this.id, this.value);
    });

    function onChangeCheckElement(id, value) {

      // Product
      if (`#${ id }` === idComponentProduct) {
        if (value && value.length && Number(value) !== -1) {
          if (value !== finalProduct) {
            removeSelectOptions(idComponentSeries);
            removeSelectOptions(idComponentPartNumber);
            resetPartNumberComponentSection();
          }
          finalProduct = value;
          let selectedObj = getObjectBy(productsJson, 'code', value);
          finalProductId = selectedObj.id;
          let seriesList = selectedObj.series;
          setDropdownOptions(idComponentSeries, seriesList, 'code', 'name'); // Fill series
        } else {
          removeSelectOptions(idComponentSeries);
          removeSelectOptions(idComponentPartNumber);
          resetPartNumberComponentSection();
        }
      }

      // Series
      if (`#${ id }` === idComponentSeries) {
        if (value && value.length && Number(value) !== -1) {
          if (value !== finalSeries) {
            removeSelectOptions(idComponentPartNumber);
            resetPartNumberComponentSection();
          }
          finalSeries = value;
          let seriesList = getObjectBy(productsJson, 'code', finalProduct).series;
          let partNumberList = getObjectBy(seriesList, 'code', value).basicPartNumbers;
          setDropdownOptions(idComponentPartNumber, partNumberList, 'part_id', 'part_name'); // Fill part number
        } else {
          removeSelectOptions(idComponentPartNumber);
          resetPartNumberComponentSection();
        }
      }

      // Part number
      if (`#${ id }` === idComponentPartNumber) {
        if (value && value.length && Number(value) !== -1) {
          let option = $(`#${ id }`).find('option:selected');
          let text = option.text();// to get <option>Text</option> content
          if (value !== finalPartNumber) {
            resetPartNumberComponentSection();
          }
          finalPartNumberId = value;
          finalPartNumber = text;
          let seriesList = getObjectBy(productsJson, 'code', finalProduct).series;
          let partNumberList = getObjectBy(seriesList, 'code', finalSeries).basicPartNumbers;
          let selectedPartNumberObject = getObjectBy(partNumberList, 'part_id', value);
          finalTolarence = selectedPartNumberObject.useToleranceMessage;
          getSecondJson(finalPartNumberId);
        } else {
          resetPartNumberComponentSection();
        }
      }
    }

    // Helper functions

    function removeSpaceFromString(str) {
      return str ? `${ str }`.split(' ').join('') : str;
    }

    function sortByPositionAscendingOrder(comp) {
      return comp.sort((a, b) => parseFloat(a.componentPosition) - parseFloat(b.componentPosition));
    }

    function getObjectBy(array, identifier, value) {
      return array.find(item => removeSpaceFromString(item[identifier]) === value);
    }

    function removeSelectOptions(id) {
      $(`${ id } option:not(:first)`).remove();
      $(`${ id }`).niceSelect('destroy');
      $(`${ id }`).niceSelect();
    }

    function setDropdownOptions(id, array, value, description) {
      $(id + ' option:not(:first)').remove();
      let op = array.map(item => `<option value=${ item[value] ? removeSpaceFromString(item[value]) : '' }>${ item[description] }</option>`).join('\n');
      $(id).append(op);
      $(`${ id }`).niceSelect('update');
    }

    function setDropdownOptionsForPartNumberComponent(id, array) {
      return `<select id=${ id } class='cc-part-num-comp wide'>
        <option value='-1' selected='selected'>- Select -</option>
        ${ array ? array.map(item => item.value ? `<option value=${ removeSpaceFromString(item.valueId) }>${ item.valueDisplay }</option>` : '').join('\n') : '' }
      </select>`;
    }

    function getSelectedValueOfPartNumberComponentJson(fieldId, optionId) {
      let parentObj = partNumberComponentResponseJson.componentDefinitions.find((item) => item.componentId === fieldId);
      let optionObj = parentObj ? parentObj.optionItems.find(op => Number(op.valueId) === Number(optionId)) : '';

      // For backshell.partId$179.json - If part ID is present in optionItems, then reset 'SERIES' & 'PART NUMBER'
      if (optionObj.partId && (Number(finalProductId) === 5)) { // "Protective cover"
        let findSeries = productsJson.find(el => el.id === 5).series;
        let findObjectOfBasicPartNumber = findSeries.find(element => element.basicPartNumbers.find(el => el.part_id === optionObj.partId));

        if (findObjectOfBasicPartNumber) {
          // Update series
          $(idComponentSeries).val(findObjectOfBasicPartNumber.code).trigger('change');
          $(idComponentSeries).niceSelect('update');
          finalSeries = findObjectOfBasicPartNumber.code;

          // Update part number
          $(idComponentPartNumber).val(optionObj.partId).trigger('change');
          $(idComponentPartNumber).niceSelect('update');
          let option = $(idComponentPartNumber).find('option:selected');
          let text = option.text() ;// to get <option>Text</option> content
          finalPartNumberId = optionObj.partId;
          finalPartNumber = text;
        }
      }

      // For backshell.partId$122.json  - If part ID is present in optionItems, then reset 'PART NUMBER' dropdown value with the value present in part ID.
      else if (optionObj.partId) {
        $(idComponentPartNumber).val(optionObj.partId);
        $(idComponentPartNumber).niceSelect('update');
        let option = $(idComponentPartNumber).find('option:selected');
        let text = option.text() ;// to get <option>Text</option> content
        finalPartNumberId = optionObj.partId;
        finalPartNumber = text;
      }

      return optionObj ? optionObj.value : '';
    }

    function resetPartNumberComponentSection() {
      partNumberComponentResponseJson = [];
      $('#part-number-components-section').addClass('u-hide');
      resetResultSection();
    }

    function resetResultSection() {
      finalLeftMetaArray = [];
      finalPartNumberArray = [];
      $('#part-number-meta, .part-number-display-banner, .bs-tolerance, #notes-section, #backshell-product-card').addClass('u-hide');
      $('#calculate').addClass('b-button__primary--primary-disabled');
      resetWarningNote();
      resetTechnicalDrawing();
    }

    function resetTechnicalDrawing() {
      technicalDrawing.empty();
    }

    function resetWarningNote() {
      $('.warning-msg')[0].innerHTML = '';
    }

    function showHideErrorBanner(task, data) {
      let errorBanner = $('#error-banner-backshell');
      if (task === 'show') {
        errorBanner[0].innerHTML = data;
        $('.u-error').css({ display: 'flex', opacity: '1' });
      } else {
        errorBanner[0].innerHTML = '';
        $('.u-error').css({ display: 'none', opacity: '0' });
      }
    }

    /**
     *
     * JSON 2
     *
     */

    function getSecondJson(partId) {
      $.ajax({
        type: 'GET',
        url: `${ backshellContainer.attr('data-resource') }.partId$${ partId }.json`,
        success: function (response) {
          let searchResult = '';
          if (typeof response === 'string') {
            searchResult = JSON.parse(response);
          } else {
            searchResult = response;
          }
          if (searchResult) {
            checkPartNumberComponentApi(searchResult);
          }
        },
        error: function (response) {
          console.log('ERROR', response);
        }
      });
    }

    function checkPartNumberComponentApi(response) {
      if (response && response !== {} && !($.isEmptyObject(response))) {
        $('#part-number-components-section').removeClass('u-hide');
        showPartNumberComponentsUi(response);
        partNumberComponentResponseJson = response;
        if (response.componentDefinitions.length === 0) {
          let disabledButtonClass = 'b-button__primary--primary-disabled';
          $('#calculate').removeClass(disabledButtonClass);

        }
      }
    }

    function showPartNumberComponentsUi(response) {
      let fieldPartNumberComponents = iterateJson(response);
      $('#part-number-components-section').html(fieldPartNumberComponents);
      $('select').niceSelect();

      // Based on 'selectedValue', pre-select the value in dropdown on load
      response.componentDefinitions.forEach(comp => {
        if (comp.selectedValue) {
          partNumberComponentResponseJson = response;
          let showOnLoad = comp.optionItems.find((obj) => obj.value === comp.selectedValue);
          $(`#${ comp.componentId }`).val(showOnLoad.valueId).trigger('change');
          $(`#${ comp.componentId }`).niceSelect('update');
        }
      });
    }

    function iterateJson(components) {
      let addDescription = addDescriptionToJson(components.componentDefinitions);
      let sortedByPostion = sortByPositionAscendingOrder(addDescription);
      let filterShowList = sortedByPostion.filter(item => item.showList !== 0);
      let filterDropdownOptions = filterShowList.filter(item => item.temp_options_first_load = item.relationComponents.length > 0 ? [] : item.optionItems);
      return filterDropdownOptions.map(comp => `<div class='form-item form-item__b-margin'>
                                                  <label class='form-item-label'>${ comp.componentDisplayName }</label>
                                                  ${ setDropdownOptionsForPartNumberComponent(comp.componentId, comp.temp_options_first_load) }
                                                  ${ comp.description ? `<span class="description-note"> ${ comp.description } </span>` : '' }
                                                  ${ `<span class='error-note error-note-${ comp.componentId }'></span>` }
                                                </div>`).join('\n');
    }

    function addDescriptionToJson(secondJson) {
      // Add description property
      secondJson.forEach(element => element.description = '');
      // If 'showList' = 0 , append its 'componentDisplayName' as 'description' to the previous object which has 'showList' = 1
      return secondJson.reduce(function(acc, currentObj) {
        if (currentObj.showList === 0) {
          let prevObj = findPrevObjectWithShowListEqualToOne(acc, currentObj.componentId);
          if (prevObj) {
            prevObj.description = prevObj.description + currentObj.componentDisplayName;
           // Find obj and replace
            let targeAcc = acc.find((obj) => obj.componentId === prevObj.componentId);
            Object.assign(targeAcc, prevObj);
          }
        } else {
          acc.push(currentObj);
        }
        return acc;
      }, []);
    }

    function findPrevObjectWithShowListEqualToOne(acc, currentId) {
      for (let i = currentId; i < 1000; i--) {
        let findPrevObj = acc.find(el => el.componentId === i && el.showList === 1);
        if (findPrevObj) {return findPrevObj;}
      }
      return true;
    }

    // On change of part number component section (2nd JSON Section)
    $('#part-number-components-section').on('change', '.cc-part-num-comp', function (event) {
      if (partNumberComponentResponseJson && partNumberComponentResponseJson.componentDefinitions) {
        let selectedId = Number(this.id);
        let filterByRelationComponents = partNumberComponentResponseJson.componentDefinitions.filter(item => item.relationComponents.includes(Number(selectedId)));

        if (this.value && this.value.length && Number(this.value) !== -1) {
          // Check for relational components and populate the dropdown.
          let selectedValue = getSelectedValueOfPartNumberComponentJson(selectedId, this.value);
          populateDropdownBasedOnRelationalComponent(filterByRelationComponents, selectedValue);
          // Check if errorValue is selected
          showErrorValueInTheFieldIfPresentInJson(selectedValue, selectedId);
        } else {
          // relationComponents - Reset the fields that are dependent on the changed field
          filterByRelationComponents.forEach(item => removeSelectOptions(`#${ item.componentId }`));
        }
        resetResultSection();
        // Check if all fields are filled
        checkIfAllInputFieldsAreFilled(event);
      }
    });

    function populateDropdownBasedOnRelationalComponent(filterByRelationComponents, selectedValue) {
      // relationComponents -- It is the 'componentId' based on which the current 'optionItems' has to be filtered with values present in 'optionItems.groupValue'
      filterByRelationComponents.forEach(item => {
        let optionsByGroupValue = item.optionItems.filter(i => {
          return [].concat.apply([], i.groupValue).includes(selectedValue.toString() || selectedValue.toUpperCase());
        });
        setDropdownOptions(`#${ item.componentId }`, optionsByGroupValue, 'valueId', 'valueDisplay');
      });
    }

    function showErrorValueInTheFieldIfPresentInJson(selectedValue, selectedId) {
      let selectedObject = partNumberComponentResponseJson.componentDefinitions.filter(item => Number(item.componentId) === selectedId);
      let selectorForErrorNoteOnField = $(`.error-note-${ selectedId }`);
      if (selectedObject && selectedObject[0].errorValue && selectedObject[0].errorValue === selectedValue) {
        let errorData = backshellContainer.attr('data-component-error-message');
        selectorForErrorNoteOnField[0].innerHTML = errorData;
        showHideErrorBanner('show', errorData);
      } else {
        selectorForErrorNoteOnField[0].innerHTML = '';
        $('.error-note').text() ? '' : showHideErrorBanner('hide', '');
      }
    }

    function checkIfAllInputFieldsAreFilled(e) {
      let disabledButtonClass = 'b-button__primary--primary-disabled';
      let chosenFlag = false;
      let countEmptyField = $('select').filter(function () { return Number($(this).val()) === -1; }); // Count empty fields
      chosenFlag = e.target.selectedIndex === 0 ? false : true;

      // Enable submit button
      if (chosenFlag && countEmptyField && countEmptyField.length === 0) {
        $('#calculate').removeClass(disabledButtonClass);
      } else {
        $('#calculate').addClass(disabledButtonClass);
        resetResultSection();
      }
    }

    function populatePartNotes() {
      if (partNumberComponentResponseJson.partNotes && partNumberComponentResponseJson.partNotes.length && partNumberComponentResponseJson.partNotes[0] !== null) {
        $('#notes-section').removeClass('u-hide');
        flagNote = true;
        let liHTML = '';
        partNumberComponentResponseJson.partNotes.forEach(x => liHTML += '<li>' + x.message + '</li>');
        $('#notes').html(liHTML);
      } else {
        flagNote = false;
      }
    }

    // Print
    $('#prints').click(function () {
      const config = {
        printable: 'backshell-config',
        targetStyles: ['*'],
        scanStyles: false,
        type: 'html',
        ignoreElements: ['souriau-toolkit-backshell-main-form', 'prints', 'backshell-contact-btn', 'backshell-product-card', `${ flagNote ? 'notes-sections' : 'notes-section' }`, `${ finalTolarence === 'a' ? 'message-b' : finalTolarence === 'b' ? 'message-a' : '' }`],
        style: 'body { margin: 0; font-size: 16px; } #print-part-number {font-weight: bold!important; margin-bottom: 50px!important} #part-number-meta{ margin-top: 50px!important} .left-meta-data{float: left!important; width: 50%!important;} .right-meta-data{float: left!important; width: 40%!important;padding-left:5px!important;} .bs-tolerance{margin-top: 40px!important; display: flex!important; width:100%!important} #notes-section{margin-top: 40px!important} .two-indent{padding-left: 14px;} .three-indent{padding-left: 26px;} .warning-msg{margin-bottom: 40px!important}'
      };
      // eslint-disable-next-line no-undef
      printJS(config);
    });

    // Warning message above the technical drawing
    function showWarningMessage(errorCondition) {

      let getValueBasedOnRelationIdOne = errorCondition.relation_id_1 > 0 ? getSelectedValueOfPartNumberComponentJson(errorCondition.relation_id_1, $(`#${ errorCondition.relation_id_1 }`).val()) : '';

      let getValueBasedOnRelationIdTwo = errorCondition.relation_id_2 > 0 ? getSelectedValueOfPartNumberComponentJson(errorCondition.relation_id_2, $(`#${ errorCondition.relation_id_2 }`).val()) : '';
      getValueBasedOnRelationIdTwo = /[a-z]/i.test(getValueBasedOnRelationIdTwo) ? getValueBasedOnRelationIdTwo : Number(getValueBasedOnRelationIdTwo).toString();

      let getValueBasedOnRelationIdThree = errorCondition.relation_id_3 > 0 ? getSelectedValueOfPartNumberComponentJson(errorCondition.relation_id_3, $(`#${ errorCondition.relation_id_3 }`).val()) : '';

      let getValueOfErrorComponent = errorCondition.componentId ? getSelectedValueOfPartNumberComponentJson(errorCondition.componentId, $(`#${ errorCondition.componentId }`).val()) : '';

      let getValueBasedOnConfigComponent = errorCondition.config_component > 0 ? getSelectedValueOfPartNumberComponentJson(errorCondition.config_component, $(`#${ errorCondition.config_component }`).val()) : '';

      let getValueBasedOnLengthComponent = errorCondition.length_component > 0 ? getSelectedValueOfPartNumberComponentJson(errorCondition.length_component, $(`#${ errorCondition.length_component }`).val()) : '';
      getValueBasedOnLengthComponent = getValueBasedOnLengthComponent && getValueBasedOnLengthComponent !== ' ';

      let conditionalResultComponentValue = '';
      // let conditionalResultRelationalValue = '';

      if (errorCondition.relation_id_3 > 0) {
        errorCondition.errorGroupValue.filter(el => {
          return el.componentId === errorCondition.componentId && el.relationValue === getValueBasedOnRelationIdThree;
        });
      }

      else if (errorCondition.relation_id_2 > 0) {
        let errorValue = errorCondition.errorGroupValue.find(el => {
          return el.componentId === errorCondition.componentId && el.relationValue === getValueBasedOnRelationIdTwo;
        });
        /* let errorGroup = errorCondition.errorGroup.find(el => {
          return el.componentId === errorCondition.componentId && el.relationValue === getValueBasedOnRelationIdOne;
        });*/
        conditionalResultComponentValue = errorValue ? errorValue.componentValue : '';
        // conditionalResultRelationalValue = errorGroup ? errorGroup.relationValue : '';
      }

      else if (errorCondition.relation_id_1 > 0) {
        errorCondition.errorGroupValue.filter(el => {
          el.componentId === errorCondition.componentId &&
            (el.relationValue === getValueBasedOnRelationIdOne || el.relationValue === getValueBasedOnRelationIdTwo || el.relationValue === getValueBasedOnRelationIdThree);
        });
      }

      // Error compare (getValueOfErrorComponent -- 'operator':error_compare  --  conditionalResultComponentValue) // If TRUE, then its an ERROR CASE
      let errMessage = '';
      if (conditionalResultComponentValue) {
        // Proceed only if conditionalResultComponentValue have any value
        if (errorCondition.error_compare === '>' ?
          Number(getValueOfErrorComponent) > Number(conditionalResultComponentValue) :
          errorCondition.error_compare === '<' ? Number(getValueOfErrorComponent) < Number(conditionalResultComponentValue) :
            Number(getValueOfErrorComponent) === Number(conditionalResultComponentValue)) {

          if (getValueBasedOnConfigComponent === errorCondition.exceptionsConfigS) {
            if (getValueBasedOnLengthComponent < errorCondition.min_length_value) {
              errMessage = backshellContainer.attr('data-component-error-message1');
              errMessage = errMessage.replace('${1}', errorCondition.min_length_value / 2);
              errMessage = errMessage.replace('${2}', errorCondition.min_length_value);
            } else {
              errMessage = backshellContainer.attr('data-component-error-message2');
            }
          }

          else if (getValueBasedOnConfigComponent === '' && Number(finalPartNumberId) === 2) {
            errMessage = backshellContainer.attr('data-component-error-message3');
          }

          else if (finalProductId > errorCondition.exceptionsReferenceId) {
            errMessage = backshellContainer.attr('data-component-error-message4');
          }

          else {
            errMessage = backshellContainer.attr('data-component-error-message5');
          }
        }
        return errMessage;
      }
    }


    /**
     *
     * Submit
     *
     */

    $(submitButton).on('submit', function (e) {
      e.preventDefault();

      if (!$('#calculate').hasClass('b-button__primary--primary-disabled')) {
        $('.message-a, .message-b').addClass('u-hide');
        $('#part-number-meta, .bs-tolerance, .part-number-display-banner').removeClass('u-hide');

        /*
        ** Prepare for final part number
        ** partBuildPosition -- Position of the selected value in the final part number.
        ** useValueForPartNumberBuild -- If true, use the value in the final part number.
        */
        // Loop the 2nd json to create the part number
        finalPartNumberArray = [];
        partNumberComponentResponseJson.componentDefinitions.forEach(compObj => {
          if (compObj.partBuildPosition && compObj.useValueForPartNumberBuild) {
            let selectedValue = getSelectedValueOfPartNumberComponentJson(compObj.componentId, $(`#${ compObj.componentId }`).val());
            let specialCharacter = compObj.optionItems.find(op => op.value.toString() === selectedValue.toString() && op.specialPrefix && op.specialPrefix !== 'NULL');
            finalPartNumberArray[compObj.partBuildPosition] = { data: specialCharacter ? specialCharacter.specialPrefix + selectedValue : selectedValue };
          }
        });

        if (Number(finalProductId) === 1 && partNumberComponentResponseJson.componentDefinitions.length === 1) { // "Self seating circular" & have only one component
          finalPartNumberArray.splice(0, 1, { data: finalSeries });
          finalPartNumberArray.splice(1, 0, { data: '-' + finalPartNumber });
        }
        else if (Number(finalProductId) === 2) { // "Rectangular Accessories"
          finalPartNumberArray.splice(0, 0, { data: finalPartNumber });
        }
        else if (Number(finalProductId) === 3) { // "Composite Accessories"
          finalPartNumberArray.splice(0, 1);
          finalPartNumberArray.splice(0, 0, { data: finalPartNumber.substring(0, 2) });
          finalPartNumberArray.splice(3, 0, { data: finalPartNumber.slice(2) });
        }
        else if (Number(finalProductId) === 4) { // "AS85049"
          finalPartNumberArray.splice(0, 0, { data: finalPartNumber });
        }
        else if (Number(finalProductId) === 5) { // "Protective cover"
          finalPartNumberArray.splice(0, 0, { data: finalPartNumber });
        }
        else {
          finalPartNumberArray.splice(0, 1, { data: finalSeries });
          finalPartNumberArray.splice(2, 0, { data: finalPartNumber });
        }
        let newString = '';
        finalPartNumberArray.forEach(item => newString += item.data.toString().toUpperCase());

        $('#calc-result')[0].innerHTML = newString;

        // Show part note
        populatePartNotes();

        // Show tolerance
        if (finalTolarence === 'a') {
          $('.message-a').removeClass('u-hide');
        } else if (finalTolarence === 'b') {
          $('.message-b').removeClass('u-hide');
        } else {
          $('.message-a, .message-b').removeClass('u-hide');
          $('.message-a, .message-b').addClass('u-hide');
        }

        /*
        ** SHOW LEFT META DATA
        ** useForPartDesc - If true, use the object for left meta data
        ** prefixDescription -  Use the 'label' as a heading in left meta data
        ** suffixDescription - Suffix it after the value
        ** useValueAndTextForMetaData - Show 'value , text' as value
        ** useDisplayTextForMetadata - Show selected dropdown option display value as value
        ** displayLevel - number of indentation while displaying the result
        */
        if (partNumberComponentResponseJson && partNumberComponentResponseJson.productLeftHandMetadataDefinitions && partNumberComponentResponseJson.productLeftHandMetadataDefinitions.length) {
          finalLeftMetaArray = [];
          partNumberComponentResponseJson.productLeftHandMetadataDefinitions.forEach(item => {

            if (item.useForPartDesc || item.prefixDescription) {
              let compId = $(`#${ item.componentId }`);
              let selectedValue = getSelectedValueOfPartNumberComponentJson(item.componentId, compId.val());
              let selectedText = compId.find('option:selected').text();
              let addDescriptionToJsons = addDescriptionToJson(partNumberComponentResponseJson.componentDefinitions);
              let selectedObjectWithDescription = addDescriptionToJsons.find(partObj => removeSpaceFromString(Number(partObj.componentId) === item.componentId) && partObj.description);
              let calculatedLengthSuffix = '';
              if (selectedObjectWithDescription && selectedValue && selectedValue !== ' ') {
                calculatedLengthSuffix = `(${ (Number(selectedText) * 0.5) } Inches)`;
              }
              let suffix = item.suffixDescription ? item.suffixDescription : '';
              let key = item.label;
              let value = '';

              if (item.prefixDescription) {
                value = item.prefixDescription;
                key = '';
              } else if (item.useValueAndTextForMetaData) {
                value = selectedValue.toString().toUpperCase() + ', ' + selectedText;
              } else if (!item.useValueAndTextForMetaData && !item.useDisplayTextForMetadata) {
                value = selectedValue.toString().toUpperCase() + suffix;
              } else if (item.useDisplayTextForMetadata) {
                value = selectedText + ' ' + (calculatedLengthSuffix || suffix);
                // key = '';
              }

              let displayIndent = '';
              if (Number(item.displayLevel) === 2) {
                displayIndent = 'two-indent';
              } else if (Number(item.displayLevel) === 3) {
                displayIndent = 'three-indent';
              }
              finalLeftMetaArray.push({ name: key, data: value, addClass: displayIndent });
            }
          });

          if (Number(finalProductId) > 3) {
            // Show part number
            finalLeftMetaArray.splice(0, 0, { name: 'Product', data: finalPartNumber.toUpperCase(), addClass: '' });
          } else {
            // Show series
            finalLeftMetaArray.splice(0, 0, { name: 'Product Series', data: finalSeries.toUpperCase(), addClass: '' });
            finalLeftMetaArray.splice(Number(finalProductId) === 2 ? 1 : 2, 0, { name: 'Basic Part Number', data: finalPartNumber, addClass: '' });
          }

          let leftMetaData = finalLeftMetaArray.map(item => {
            if (item.name) {
              return `<div class=${ item.addClass }>${ item.name } = ${ item.data }</div>`;
            } else {
              return `<div class=${ item.addClass }>${ item.data }</div>`;
            }
          });
          $('.left-meta-data').html(leftMetaData);
        }

          // show left meta data if productLeftHandMetadataDefinitions.length === 0

        if (partNumberComponentResponseJson && partNumberComponentResponseJson.productLeftHandMetadataDefinitions && partNumberComponentResponseJson.productLeftHandMetadataDefinitions.length === 0) {
          finalLeftMetaArray = [];

          if (Number(finalProductId) > 3) {
              // Show part number
            finalLeftMetaArray.splice(0, 0, { name: 'Product', data: finalPartNumber.toUpperCase(), addClass: '' });
          } else {
              // Show series
            finalLeftMetaArray.splice(0, 0, { name: 'Product Series', data: finalSeries.toUpperCase(), addClass: '' });
            finalLeftMetaArray.splice(Number(finalProductId) === 2 ? 1 : 2, 0, { name: 'Basic Part Number', data: finalPartNumber, addClass: '' });
          }

          let leftMetaData = finalLeftMetaArray.map(item => {
            if (item.name) {
              return `<div class=${ item.addClass }>${ item.name } = ${ item.data }</div>`;
            } else {
              return `<div class=${ item.addClass }>${ item.data }</div>`;
            }
          });
          $('.left-meta-data').html(leftMetaData);

        }

        // Show right meta data
        if (partNumberComponentResponseJson && partNumberComponentResponseJson.productRightHandMetadataDefinitions && partNumberComponentResponseJson.productRightHandMetadataDefinitions.length) {
          let rightMetaData = partNumberComponentResponseJson.productRightHandMetadataDefinitions.map(item => {
            let selectedValue = getSelectedValueOfPartNumberComponentJson(item.componentId, $(`#${ item.componentId }`).val());
            // displayLevel - number of indentation while displaying the result
            // isDimension - If false then no value is displayed
            let displayIndent = '';
            if (Number(item.displayLevel) === 2) {
              displayIndent = 'two-indent';
            } else if (Number(item.displayLevel) === 3) {
              displayIndent = 'three-indent';
            }
            if (item.metaDataValues && item.metaDataValues.length && item.metaDataValues[0] !== null) {
              let filterMeta = item.metaDataValues.filter(data => data.dimensionValue === selectedValue);
              let mData = filterMeta && filterMeta.length ? filterMeta[0].value : '';
              return `<div class=${ displayIndent }>${ item.label } = ${ mData }</div>`;
            } else {
              return `<div class=${ displayIndent }>${ item.label }</div>`;
            }
          });

          rightMetaData.push('<div>ALL DIMENSIONS ARE IN INCHES </div>');
          $('.right-meta-data').html(rightMetaData);
        }

            // show right blank, if right section is blank

        if (partNumberComponentResponseJson && partNumberComponentResponseJson.productRightHandMetadataDefinitions && partNumberComponentResponseJson.productRightHandMetadataDefinitions.length === 0) {
          let rightMetaData = [];
          rightMetaData.push('<div> </div>');
          $('.right-meta-data').html(rightMetaData);
        }


        // Warning message
        if (partNumberComponentResponseJson && partNumberComponentResponseJson.errorConditions && partNumberComponentResponseJson.errorConditions.length && partNumberComponentResponseJson.errorConditions[0] !== null) {
          let errMessage = showWarningMessage(partNumberComponentResponseJson.errorConditions[0]);
          errMessage ? $('.warning-msg')[0].innerHTML = errMessage : '';
        }

        // Show technical drawing
        resetTechnicalDrawing();
        let hasNumber = /\d/;
        let fileName = '';
        if (Number(finalProductId) > 3) {
          fileName = finalPartNumber.replace('/','_');
        } else {
          fileName = hasNumber.test(finalSeries) ? (finalSeries + finalPartNumber).toLowerCase() : finalPartNumber.toLowerCase();
        }
        technicalDrawing.prepend(`<img id='tech-img' src='/content/dam/eaton/interactive/tools-and-applications/product-configurators/backshell/technical-drawings/${ fileName }.gif' />`);

        // Scroll
        $('.scroll-here')[0].scrollIntoView({
          behavior: 'smooth', inline: 'center', block: 'center'
        });
      }

      // TODO: PRODUCT CARD : Revisit and integrate this code once the Souriau PDH migration completes.
      // $('#backshell-product-card').removeClass('u-hide');

    });
  }

});