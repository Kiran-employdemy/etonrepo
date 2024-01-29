//-----------------------------------
// Arinc600 Component
//-----------------------------------

$(document).ready(function () {
  let aricContainer = $('.arinc-600-config');
  $('#valid-result, #failed-result', '.arinc-600-config').addClass('u-hide');
  $('select').niceSelect();

  $.ajax({
    type: 'GET',
    url: `${ aricContainer.attr('data-resource') }.json`,
    success: function (response) {
      if (response) {
        let searchResult = '';
        if (typeof response === 'string') {
          searchResult = JSON.parse(response);
        } else {
          searchResult = response;
        }
        showUi(searchResult);
      }
    },
    error: function (err) {
      console.log('ERROR', err);
    }
  });

  function showUi(response) {
    $('.arinc-600-config').removeClass('u-hide');
    $('#default-message').addClass('u-hide');
    let submitButton = $('#souriau-toolkit-arinc-main-form');
    let components = response && response.components ? response.components : [];
    let mountingOptions = response && response.mountingOptions ? response.mountingOptions : [];

    // Variables to build part number
    let finalBasicSeries = '';
    let finalConnectorClass = '';
    let finalShellSize = '';
    let finalShellType = '';
    let finalContactMountingAndRelease = '';
    let finalMountingStyle = '';
    let finalInsertArrangementPowerSourceCodeCavityCF = '';
    let finalContactType = '';
    let finalInsertArrangementPowerSourceCodeCavityABDE = '';
    let finalPolarizationCode = '';
    let finalPackageingCode = '';
    let checkTF = '';
    let validFlag2 = '';

    // Field value variables
    let valueConnector = '';
    let packagingConnectorRadio = '';

    // Section ID
    let idSectionShell = '#shell';
    let idSectionPolarization = '#polarization';
    let idSectionCavityConfig = '#insert-config';

    // Constants from JSON
    let jsonConstantSealedConnector = 'sealed';
    let jsonConstantComponentGroupShell = 'Shell';
    let jsonConstantComponentGroupPolarization = 'Polarization';
    let jsonConstantComponentGroupInsertConfiguration = 'Insert Configuration';
    let jsonConstantFieldText = 'text';
    let jsonConstantFieldRadio = 'radio';
    let jsonConstantFieldCheckbox = 'checkbox';
    let jsonConstantFieldDropdown = 'dropdown';

    // Unique ID from JSON
    let jsonConstantComponentUniqueIdSeries = 'arinc_series';
    let jsonConstantComponentUniqueIdShellSize = 'arinc_signal_insert_code';
    let jsonConstantComponentUniqueIdShellType = 'arinc_shell_type';
    let jsonConstantComponentUniqueIdConnector = 'arinc_sealing_type_connector';
    let jsonConstantComponentUniqueIdContactType = 'arinc_contact_type';
    let jsonConstantComponentUniqueIdMountingRelease = 'arinc_mounting_release';
    let jsonConstantComponentUniqueIdMountingStyle = 'arinc_mounting_style';
    let jsonConstantComponentUniqueIdPolarization = 'arinc_polarization';
    let jsonConstantComponentUniqueIdCavitySignalSourceCode = 'arinc_signal_source_code';
    let jsonConstantComponentUniqueIdCavityPowerSourceCode = 'arinc_power_source_code';
    let jsonConstantComponentUniqueIdConnectorClass = 'arinc_connector_class';


    // JSON manipulation
    getAllShellBoxFields();
    function getAllShellBoxFields() {
      let fieldShell = iterateJson(components.filter(item => item.componentGroup === jsonConstantComponentGroupShell));
      $(idSectionShell).html(fieldShell);
      $('.check-box-wrapper').parent().addClass('form-item__hide');

      let fieldPolarization = iterateJson(components.filter(item => item.componentGroup === jsonConstantComponentGroupPolarization));
      $(idSectionPolarization).html(fieldPolarization);

      let fieldInsertConfig = iterateJson(components.filter(item => item.componentGroup === jsonConstantComponentGroupInsertConfiguration));
      $(idSectionCavityConfig).html(fieldInsertConfig);
      $('.cavity-shell-size-3').parent().addClass('form-item__hide');


      $('select').niceSelect();
    }

    function iterateJson(components) {
      let sortedByPostion = sortByPositionAscendingOrder(components);
      return sortedByPostion.map(comp => `<div class='form-item form-item__b-margin'>
                                                <label class='form-item-label'>${ comp.componentType.name }</label>
                                                ${ comp.formElementType !== jsonConstantFieldCheckbox ? "<span class='form-required'>*</span>" : '' } 
                                                ${ checkInputType(comp, comp.formElementType) }
                                              </div>`).join('\n');
    }

    // Check input type
    function checkInputType(component, type) {
      if (type === jsonConstantFieldText) {
        let optionItems = getOptionItems(component);
        return setTextOptions(component.id, component.componentUniqueID, optionItems);
      } else if (type === jsonConstantFieldRadio) {
        let optionItems = getOptionItems(component);
        return setRadioOptions(component.id, optionItems, component.formElementType);
      } else if (type === jsonConstantFieldDropdown) {
        let optionItems = getOptionItems(component);
        return setDropdownOptions(component.id, component.componentUniqueID, component.componentUniqueID === jsonConstantComponentUniqueIdShellSize ? optionItems : '', component.componentGroup, component.isDefault);
      } else if (type === jsonConstantFieldCheckbox) {
        // let optionItems = getOptionItems(component);
        return setCheckboxOptions(component.id, []);
      }
    }

    // Sealing type checkbox
    $('.check-box-wrapper').on('click', ':checkbox', function () {
      let obj = getObjectById($('.check-box-wrapper').attr('id'));
      resetSelectedFields(obj.resetPositionIDs);
      populateMountingStyle(); // Fill mounting style
      populateDisabledTextField(); // Disabled text field
    });

    // On change of element, check for reset
    $('input[type="radio"]').on('change', function () {
      checkElement(this.name, this.value, '');
    });

    $('select').on('change', function () {
      checkElement(this.id, this.value, $(this).attr('class'));
    });

    function checkElement(id, value) {
      let obj = getObjectById(id);

      if (obj.formElementType === jsonConstantFieldDropdown) {

        // Shell size
        if (obj.componentUniqueID === jsonConstantComponentUniqueIdShellSize) {

          if (value && value.length) {
            finalShellSize = value;
            removeErrorSpan(obj.id);

            populateSelectOptions(jsonConstantComponentUniqueIdShellType, ''); // Fill shell type
            $('.cavity-shell-size-3').parent().addClass('form-item__hide');
            $('.cavity-field').each(function () {
              populateSelectOptionForInsertConfiguration($(this).attr('id')); // Fill cavity
            });
            if (Number(value) === 3) {
              $('.cavity-shell-size-3').parent().removeClass('form-item__hide');
            }
          } else {
            finalShellSize = '';
            $('.cavity-shell-size-3').length > 0 ? $('.cavity-shell-size-3').parent().addClass('form-item__hide') : '';
            $('.cavity-field').each(function () {
              obj.resetPositionIDs.push(Number(this.id));
            });
            resetSelectedFields(obj.resetPositionIDs);
          }

          // Reset packaging
          $("input[type='radio'][value='without-contacts']").attr('checked', true).trigger('change');
          $('input[name=contacts-flag]').prop('checked', false);
          $('#packaging-source-code').val('');
        }

        // Shell type
        if (obj.componentUniqueID === jsonConstantComponentUniqueIdShellType) {
          if (value && value.length) {
            finalShellType = value.toUpperCase();
            removeErrorSpan(obj.id);

            let contactTypeObj = getObjectByUniqueId(jsonConstantComponentUniqueIdContactType);
            populateSelectOptions(jsonConstantComponentUniqueIdMountingRelease); // Fill mounting & release
            populateCheckboxOptions($('.check-box-wrapper')[0].id); // Populate checkbox

            if (value.toLowerCase() === 'F'.toLowerCase()) {
              removeErrorSpan(contactTypeObj.id);
              $(`#${ contactTypeObj.id }`).val('S');
              finalContactType = 'S';
            } else {
              $(`#${ contactTypeObj.id }`).val('P');
              finalContactType = 'P';
            }
          } else {
            finalShellType = '';
            resetSelectedFields(obj.resetPositionIDs);
          }
        }

        // Mounting and Release
        if (obj.componentUniqueID === jsonConstantComponentUniqueIdMountingRelease) {
          if (value && value.length) {
            finalContactMountingAndRelease = value.toUpperCase();
            removeErrorSpan(obj.id);
            populateMountingStyle(); // Fill mounting style
          } else {
            finalContactMountingAndRelease = '';
            resetSelectedFields(obj.resetPositionIDs);
          }
        }

        // Mounting style
        if (obj.componentUniqueID === jsonConstantComponentUniqueIdMountingStyle) {
          if (value && value.length) {
            finalMountingStyle = value.toUpperCase();
            removeErrorSpan(obj.id);
            populateSelectOptions(jsonConstantComponentUniqueIdPolarization); // Fill polarization
          } else {
            finalMountingStyle = '';
            resetSelectedFields(obj.resetPositionIDs);
          }
        }

        // Polarization
        if (obj.componentUniqueID === jsonConstantComponentUniqueIdPolarization) {
          if (value && value.length) {
            finalPolarizationCode = value;
            removeErrorSpan(obj.id);
          } else {
            finalPolarizationCode = '';
            resetSelectedFields(obj.resetPositionIDs);
          }
        }
      }

      if (obj.formElementType === jsonConstantFieldRadio) {
        // Connector
        if (obj.componentUniqueID === jsonConstantComponentUniqueIdConnector) {
          valueConnector = value;

          populateSelectOptions(jsonConstantComponentUniqueIdMountingRelease); // Fill mounting & release based on sealed and unsealed

          if (value && value.length && value === jsonConstantSealedConnector) {
            let checkboxId = $('.check-box-wrapper')[0].id;

            // Default show sealing-type checkbox options for only 'F' shell type if shell type is empty
            if (!finalShellType) {
              let obj = getObjectById(checkboxId);
              let allowedOptionItems = obj.optionItems.filter(item => item.additionalRestriction === 'F');
              let op = setCheckboxOptions(checkboxId, allowedOptionItems);
              $(`#${ checkboxId }`).html(op);
            } else {
              populateCheckboxOptions(checkboxId); // Populate checkbox based on selected shell type
            }

            $('.check-box-wrapper').parent().removeClass('form-item__hide');
          } else {
            $('.check-box-wrapper').parent().addClass('form-item__hide');
          }
        }

        // Common radio button of shell box
        if (value && value.length) {
          removeErrorSpan(obj.id);
        }

      }

      // Disabled text field
      populateDisabledTextField();
    }

    // MOUNTING STYLE - Populate Dropdown
    $('.cc-depend-mounting-style').change(function () {
      // Depends on shell size, shell type, connector, sealing type, plating, grounding, mounting & release.
      populateMountingStyle();
    });

    // CAVITY FIELD - On change
    $('.cavity-field').change(function () {
      $(`#${ this.id }`).val() ? removeErrorSpan(this.id) : '';
      populateDisabledTextField();
    });


    function populateMountingStyle() {
      populateSelectOptions(jsonConstantComponentUniqueIdMountingStyle); // Fill mounting style
    }

    // POPULATE DISABLED TEXT FIELD
    function populateDisabledTextField() {
      let textFieldUniqueId = [jsonConstantComponentUniqueIdCavitySignalSourceCode, jsonConstantComponentUniqueIdCavityPowerSourceCode, jsonConstantComponentUniqueIdConnectorClass];
      textFieldUniqueId.forEach(item => {
        let obj = getObjectByUniqueId(item);
        let filteredObject = checkAllowedCombinations(obj, true);
        let value = filteredObject && filteredObject.length ? filteredObject[0].value : '';
        $(`#${ obj.id }`).val(value);

        // SIGNAL SOURCE CODE
        if (item === jsonConstantComponentUniqueIdCavitySignalSourceCode) {
          finalInsertArrangementPowerSourceCodeCavityABDE = value;
          value ? removeErrorSpan(obj.id) : '';

          // Check values of cavity a b d e
          let allCavityAbdeIsFilled = [];
          $(`#${ obj.id }`).parent().prevAll('.form-item.form-item__b-margin').not('.form-item__hide').each(function () {
            allCavityAbdeIsFilled.push($(this).find('option:selected').val());
          });

          // If cavity A B D E is filled and signal source code value is empty - show codification message
          if (value && $.inArray('', allCavityAbdeIsFilled) === -1) { // If all cavity a b d e fields are filled i.e -1
            removeErrorSpanFromDisabledTextInput(obj.id);
          }
          if ($.inArray('', allCavityAbdeIsFilled) === -1 && !value) {
            removeErrorSpanFromDisabledTextInput(obj.id);
            $(`#${ obj.id }`).after(`<span class='error-msg'>  ${ aricContainer.attr('data-CodificationMessage') } </span>`);
          } else {
            removeErrorSpanFromDisabledTextInput(obj.id);
          }
        }

        // POWER SOURCE CODE
        if (item === jsonConstantComponentUniqueIdCavityPowerSourceCode) {
          finalInsertArrangementPowerSourceCodeCavityCF = value;
          value ? removeErrorSpan(obj.id) : '';

          // Check values of cavity c f
          let signalSourceCodeObj = getObjectByUniqueId(jsonConstantComponentUniqueIdCavitySignalSourceCode);
          let allCavityCfIsFilled = [];
          $(`#${ signalSourceCodeObj.id }`).parent().nextAll('.form-item.form-item__b-margin').not('.form-item__hide').find('select').each(function () {
            allCavityCfIsFilled.push($(this).find('option:selected').val());
          });

          // If cavity C F is filled and signal source code value is empty - show codification message
          if (value && $.inArray('', allCavityCfIsFilled) === -1) { // If all cavity c f fields are filled i.e -1
            removeErrorSpanFromDisabledTextInput(obj.id);
          }
          if ($.inArray('', allCavityCfIsFilled) === -1 && !value) {
            removeErrorSpanFromDisabledTextInput(obj.id);
            $(`#${ obj.id }`).after(`<span class='error-msg'>  ${ aricContainer.attr('data-CodificationMessage') } </span>`);
          } else {
            removeErrorSpanFromDisabledTextInput(obj.id);
          }
        }

        // CONNECTOR CLASS
        if (item === jsonConstantComponentUniqueIdConnectorClass) {
          finalConnectorClass = value;
          value ? removeErrorSpan(obj.id) : '';
          // If Shell size, Shell type, connector, plating, grounding are selected but not in the allowed combination of connector class then show the error message.
          if (finalShellSize && finalShellType && valueConnector) {
            let allRadioValuesInShellSection = $('.cc-depend-mounting-style :radio:checked').map(function () {
              return $(this).val();
            }).get();
            if (allRadioValuesInShellSection && allRadioValuesInShellSection.length === 3) {
              // If connector class have coding error, the same must be present in signal-source-code & power-source-code
              // let signalSourceCodeObj = getObjectByUniqueId(jsonConstantComponentUniqueIdCavitySignalSourceCode);
              // let powerSourceCodeObj = getObjectByUniqueId(jsonConstantComponentUniqueIdCavityPowerSourceCode);
              if (value) {
                removeErrorSpanFromDisabledTextInput(obj.id);
                // removeErrorSpanFromDisabledTextInput(powerSourceCodeObj.id);
              } else {
                removeErrorSpanFromDisabledTextInput(obj.id);
                // removeErrorSpanFromDisabledTextInput(powerSourceCodeObj.id);
                $(`#${ obj.id }`).after(`<span class='error-msg'>  ${ aricContainer.attr('data-CodificationMessage') } </span>`);
                // $(`#${ powerSourceCodeObj.id }`).after(`<span class='error-msg'>  ${ aricContainer.attr('data-CodificationMessage') } </span>`);
              }
            }
          }
        }
      });
    }

    /*
    *
    * HELPER FUNCTION JSON 1
    *
    */

    function setTextOptions(id, uniqueId, optionArray) {
      if (uniqueId === jsonConstantComponentUniqueIdSeries) {
        finalBasicSeries = optionArray[0].value;
        return `<input type="text" value=${ optionArray[0].value } id=${ id } disabled class="u-show" />`;
      } else {
        return `<input type="text" value='' id=${ id } disabled class="u-show" />`;
      }
    }

    function setRadioOptions(id, optionArray, fieldType) {
      let radioClass = fieldType === jsonConstantFieldRadio ? 'radio-wrapper cc-depend-mounting-style' : 'radio-wrapper';
      return `<div id=${ id } class='${ radioClass }'>
                ${ optionArray.map(item => `<div class='form-item form-type-radio form-type-radio__inline'>
                    <input type='radio' id=${ item.value ? removeSpaceFromStringAndChangeToLowercase(item.value) : '' } name =${ id } value=${ item.value ? removeSpaceFromStringAndChangeToLowercase(item.value) : '' }>
                    <label class='option' for=${ item.value ? removeSpaceFromStringAndChangeToLowercase(item.value) : '' }>${ item.value } </label>
                  </div>`).join('\n') }
                </div>`;
    }

    function setDropdownOptions(id, uniqueId, optionArray, componentGroup, isDefault) {
      let cavityClass = componentGroup === jsonConstantComponentGroupInsertConfiguration && isDefault ? 'cavity-field wide' : componentGroup === jsonConstantComponentGroupInsertConfiguration && !isDefault ? 'cavity-field cavity-shell-size-3 wide' : 'wide';
      return `<select id=${ id } class='${ cavityClass }'>
                  <option value='' selected='selected'>- Select -</option>
                  ${ optionArray ? optionArray.map(item => `<option value=${ removeSpaceFromStringAndChangeToLowercase(item.value) }>${ item.description }</option>`).join('\n') : '' }
                </select>`;
    }

    function setCheckboxOptions(id, optionArray) {
      return `<div class='check-box-wrapper' id=${ id }>
                ${ optionArray.map(item => `<label class='eaton-checkbox-custom'>${ item.value }
                  <input type='checkbox' checked='checked' id='sealing-type' value=${ item.value }>
                  <span class='checkmark'></span>
                  </label>`).join('\n') }
                  </div>`;
    }

    function getOptionItems(obj) {
      return obj && obj.optionItems && obj.optionItems.length ? obj.optionItems : [];
    }

    function sortByPositionAscendingOrder(comp) {
      return comp.sort((a, b) => parseFloat(a.position) - parseFloat(b.position));
    }

    function getSelectedValueFromTextInput(id) {
      return $(id).val();
    }

    function getSelectedValueFromDropdown(inputDropdownId) {
      return $(inputDropdownId).val();
    }

    function getSelectedValueFromRadio(inputRadioId) {
      return $('input:radio[name=' + inputRadioId + ']:checked').length > 0 ? removeSpaceFromStringAndChangeToLowercase($('input:radio[name=' + inputRadioId + ']:checked').val()) : '';
    }

    function getSelectedValueFromCheckbox() {
      return $('input[id="sealing-type"]:checked').map(function () {
        return this.value;
      }).get().join(','); // Get comma separated value
    }

    function getObjectByUniqueId(uniqueId) {
      let arr = components.filter(item => item.componentUniqueID === uniqueId);
      return arr && arr.length ? arr[0] : '';
    }

    function getObjectById(id) {
      let arr = components.filter(item => item.id === Number(id));
      return arr && arr.length ? arr[0] : '';
    }

    function populateCheckboxOptions(id) {
      let obj = getObjectById(id);
      // If Shell type is 'M' - show all sealing-type checkbox option items.
      // If Shell type is 'F' - show only sealing-type chcekbox present in additionalRestriction. ie. Compound, Grommet
      let allowedOptionItems = obj.optionItems;
      if (finalShellType === 'F') {
        allowedOptionItems = obj.optionItems.filter(item => item.additionalRestriction === finalShellType);
      }
      let op = obj ? setCheckboxOptions(obj.id, allowedOptionItems) : '';
      $(`#${ obj.id }`).html(op);
    }

    function populateSelectOptionForInsertConfiguration(id) {
      if (id) {
        let obj = getObjectById(id);
        let allowedOptionItems = obj.optionItems.filter(item => item.additionalRestriction !== '' && !checkAdditionalRestrictionForCavity(item.additionalRestriction));
        let op = obj ? setDropdownOptions(obj.id, '', allowedOptionItems) : '';
        $(`#${ obj.id }`).html(op);
        $(`#${ obj.id }`).niceSelect('update');
      }
    }

    function checkAdditionalRestrictionForCavity(additionalRestriction) {
      return additionalRestriction.split('').every(i => {
        return i === finalShellSize ? false : true; // To break from 'every' loop, return FALSE
      });
    }

    function populateSelectOptions(uniqueId, id) {
      let obj = uniqueId ? getObjectByUniqueId(uniqueId) : getObjectById(id);
      let allowedOptionItems = [];
      // Mounting Style - mounting options will be used instead of allowedCombination. The filtered mounting options will take the description from the allowedCombination.
      if (uniqueId === jsonConstantComponentUniqueIdMountingStyle) {
        if (finalShellSize && finalShellType && valueConnector && finalConnectorClass) {
          let filteredMountingOptions = mountingOptions.filter(item => Number(item.shellSize) === Number(finalShellSize) &&
            item.shellType.toLowerCase() === finalShellType.toLowerCase() && item.shellCategory === valueConnector && item.connectorClass === finalConnectorClass);

          if (filteredMountingOptions && filteredMountingOptions.length) {
            allowedOptionItems = obj.optionItems.filter((el) => {
              return filteredMountingOptions.some((f) => {
                return f.mntCodeAllowed === el.value;
              });
            });
          }
        }
      } else {
        allowedOptionItems = checkAllowedCombinations(obj);
        // Contact Mounting and Release - Don't show the option if additionalRestriction: '0'
        if (uniqueId === jsonConstantComponentUniqueIdMountingRelease) {

          if (finalShellSize && finalShellType === 'M') {
            allowedOptionItems = allowedOptionItems.filter(function (item) {
              return item.additionalRestriction !== '0' && item.isRestricted === true;
              // return item.additionalRestriction !== '0';
            });
          }

          if (finalShellSize && finalShellType === 'M' || finalShellType === 'F' && valueConnector === 'sealed') {
            allowedOptionItems = allowedOptionItems.filter(function (item) {
              return item.additionalRestriction !== '0' && item.isRestricted === true;
            });
          }

          if (finalShellSize && finalShellType === 'F' && valueConnector === 'unsealed') {
            allowedOptionItems = allowedOptionItems.filter(function (item) {
              return item.additionalRestriction !== '0';
            });
          }

          if (finalShellSize && finalShellType === 'M' && valueConnector === 'unsealed') {
            allowedOptionItems = allowedOptionItems.filter(function (item) {
              return item.additionalRestriction !== '0' && item.isRestricted === true;
            });
          }

        }
      }

      let op = obj ? setDropdownOptions(obj.id, uniqueId, allowedOptionItems) : '';
      $(`#${ obj.id }`).html(op);
      $(`#${ obj.id }`).niceSelect('update');
    }

    function removeSelectOptions(id) {
      $(`#${ id } option:not(:first)`).remove();
      $(`#${ id }`).niceSelect('destroy');
      $(`#${ id }`).niceSelect();
    }

    function removeSelectOptionsForStandaredDropdown(id) {
      $(id).val('');
      $(id).niceSelect('destroy');
      $(id).niceSelect();
    }

    function removeTextOption(id) {
      $(`#${ id }`).val('');
    }

    function removeSpaceFromStringAndChangeToLowercase(str) {
      return str.split(' ').join('').toLowerCase();
    }

    function removeErrorSpan(objId) {
      $(`#${ objId }`).next().next('span').remove();
    }

    function removeErrorSpanFromDisabledTextInput(objId) {
      $(`#${ objId }`).next('span').remove();
    }

    function checkAllowedCombinations(obj, isTextField) {
      return obj.optionItems.filter(item => {
        let filteredItem = isTextField ?
          item.allowedCombinations.length > 0 && item.allowedCombinations[0] !== null && !loopAllowedCombination(item.allowedCombinations)
          :
          item.allowedCombinations.length < 1 || item.allowedCombinations[0] === null || !loopAllowedCombination(item.allowedCombinations);

        return filteredItem ? item : '';
      });
    }

    function loopAllowedCombination(allowedCombinations) {

      if (allowedCombinations && allowedCombinations.length) {
        return allowedCombinations.every(inItem => {
          let innerAndFlag = true;
          // In option items the checkbox sealing type values must be in a single object with comma separeated values.
          let mergedCommonComponentId = mergeCommonComponentId(inItem);
          mergedCommonComponentId.forEach(innerItem => {
            let eachInnerAndCondition = checkValuesOfAllowedCombination(innerItem);
            if (!eachInnerAndCondition) {
              innerAndFlag = false;
            }
          });
          return innerAndFlag ? false : true;
        });
      }
    }

    function checkValuesOfAllowedCombination(item) {
      let obj = getObjectById(item.componentId);
      let result = '';
      if (obj) {
        if (obj.formElementType === jsonConstantFieldText) {
          result = getSelectedValueFromTextInput(`#${ obj.id }`);
        }
        if (obj.formElementType === jsonConstantFieldRadio) {
          result = getSelectedValueFromRadio(`${ obj.id }`);
        }
        if (obj.formElementType === jsonConstantFieldDropdown) {
          result = getSelectedValueFromDropdown(`#${ obj.id }`);
        }
        if (obj.formElementType === jsonConstantFieldCheckbox) {
          result = getSelectedValueFromCheckbox(`#${ obj.id }`);
        }
      }

      let itemValue = obj.formElementType === jsonConstantFieldRadio || obj.formElementType === jsonConstantFieldDropdown ? removeSpaceFromStringAndChangeToLowercase(item.value) : item.value;

      if (obj.formElementType === jsonConstantFieldCheckbox) {
        // Selected checkbox & allowedCombinationCheckbox value is sorted alphabetically and converted to single string and compared.
        let selectedChcekboxValues = result ? result.split(',').sort().join('') : result;
        let allowedOptionItemCheckboxValues = itemValue.split(',').filter((item, pos, self) => self.indexOf(item) === pos).sort().join('');
        return allowedOptionItemCheckboxValues === selectedChcekboxValues ? true : false;
      } else {
        return itemValue === result ? true : false;
      }
    }

    function mergeCommonComponentId(originalArray) {
      let result = [];
      originalArray.forEach(elem => {
        let foundIndex = result.findIndex(x => x.componentId === elem.componentId);
        if (foundIndex === -1) {
          result.push(elem);
        } else {
          let temp = '';
          temp = result[foundIndex].value;
          result[foundIndex].value = '';
          result[foundIndex].value = temp + ',' + elem.value;
        }
      });
      return result;
    }

    function resetSelectedFields(resetFieldIdArray) {
      if (resetFieldIdArray && resetFieldIdArray.length) {
        resetFieldIdArray.map(item => {
          let obj = getObjectById(item);
          if (obj.formElementType === jsonConstantFieldDropdown) {
            removeSelectOptions(obj.id);
          }
          if (obj.formElementType === jsonConstantFieldText) {
            removeTextOption(obj.id);
          }
        });
      }
    }


    /*
    *
    * JSON 2
    *
    */


    // Connector radio
    $('input[type=radio][name=contacts-flag]').on('change', function () {
      packagingConnectorRadio = this.value;
      if (packagingConnectorRadio === 'with-contacts') {
        validFlag2 = false;
        if (finalShellSize && finalShellType && finalContactMountingAndRelease && valueConnector && finalInsertArrangementPowerSourceCodeCavityABDE && finalInsertArrangementPowerSourceCodeCavityCF) {
          callPackagingApi();
          // Reset source code
          $('#packaging-source-code').val('');
        }
        $('#show-on-contact').removeClass('u-hide');
      } else {
        $('#show-on-contact').addClass('u-hide');
        $('#signal-kind', '#signal-tail', '#power-kind', '#power-tail').parent().addClass('form-item__hide');
        // Reset all the packaging fields - signal, signal-kind, signal-tail, power, power-kind, power-tail, coax, quadrax, source code,
        $('#packaging-source-code').val('');
        removeSelectOptionsForStandaredDropdown('#edit-packaging-signal');
        removeSelectOptionsForStandaredDropdown('#edit-packaging-power');
        removeSelectOptions('signal-kind');
        removeSelectOptions('signal-tail');
        removeSelectOptions('power-kind');
        removeSelectOptions('power-tail');
        removeSelectOptions('edit-packaging-coax');
        removeSelectOptions('edit-packaging-quadrax');
      }

      if (packagingConnectorRadio === 'without-contacts') {
        validFlag2 = true;
        $('#packaging-source-code').val('');
        $('#packaging-source-code').next('span').remove();
        checkTF = false;
      }


    });

    function callPackagingApi() {
      $.ajax({
        type: 'GET',
        url: `${ aricContainer.attr('data-resource') }.signalSourceCode$${ finalInsertArrangementPowerSourceCodeCavityABDE }.powerSourceCode$${ finalInsertArrangementPowerSourceCodeCavityCF }.shellSize$${ finalShellSize }.mountingReleaseCode$${ finalContactMountingAndRelease }.shellType$${ finalShellType }.connectorType$${ valueConnector }.json`,
        success: function (response) {
          if (response) {
            let searchResult = '';
            if (typeof response === 'string') {
              searchResult = JSON.parse(response);
            } else {
              searchResult = response;
            }
            checkPackagingApi(searchResult);
          }
        },
        error: function (err) {
          console.log('ERROR', err);
        }
      });
    }

    function checkPackagingApi(response) {
      if (response && response !== {} && !($.isEmptyObject(response))) {
        $('#show-on-contact').removeClass('u-hide');
        showPackageUi(response);
      }
    }

    function showPackageUi(response) {
      let packagingComponents = response ? response : [];
      let packagingSignalWithWithout = '';
      let packagingPowerWithWithout = '';
      let packagingSignalKind = '';
      let packagingPowerKind = '';
      let packagingSignalTail = '';
      let packagingPowerTail = '';
      let packagingCoax = '';
      let packagingQuadrax = '';

      // SIGNAL
      if (packagingComponents.signal22) {
        $('#edit-packaging-signal').parent().removeClass('form-item__hide');
      } else {
        $('edit-packaging-signal').parent().addClass('form-item__hide');
      }

      // POWER
      if (packagingComponents.power16) {
        $('#edit-packaging-power').parent().removeClass('form-item__hide');
      } else {
        $('#edit-packaging-power').parent().addClass('form-item__hide');
      }

      // COAX
      if (packagingComponents.coax5) {
        $('#edit-packaging-coax').parent().removeClass('form-item__hide');
        setDropdownOptionsPackaging('#edit-packaging-coax', packagingComponents.coax5PackageOptions);
        // Populate coax dropdown
      } else {
        // Show default coax
        $('#edit-packaging-coax').parent().removeClass('form-item__hide');
        $('#edit-packaging-coax option:not(:first)').remove();
        let op = '<option value="N"> delivered without #5 coax without #1 coax </option>';
        $('#edit-packaging-coax').append(op);
        $('#edit-packaging-coax').niceSelect('update');
      }

      // QUADRAX
      if (packagingComponents.quadrax) {
        $('#edit-packaging-quadrax').parent().removeClass('form-item__hide');
        setDropdownOptionsPackaging('#edit-packaging-quadrax', packagingComponents.quadraxPackagingOptions);
        // Populate quadrax dropdown
      } else {
        $('#edit-packaging-quadrax').parent().addClass('form-item__hide');
      }

      // ON CHANGE SIGNAL
      $('#edit-packaging-signal').on('change', function () {
        packagingSignalWithWithout = this.value;
        removeErrorSpan('edit-packaging-signal');
        if (this.value) {
          // KIND
          $('#signal-kind').parent().removeClass('form-item__hide');
          if (packagingComponents && packagingComponents.signal22KindOptions && packagingComponents.signal22KindOptions.length) {
            let getFilteredCombination = packagingComponents.signal22KindOptions.filter(item => checkShellRestriction(item) && checkMountingReleaseRestriction(item));
            setDropdownOptionsPackaging('#signal-kind', getFilteredCombination);
          }
          // Populate kind dropdown
        } else {
          $('#signal-kind').parent().addClass('form-item__hide');
          $('#signal-tail').parent().addClass('form-item__hide');
          removeSelectOptions('signal-kind');
          removeSelectOptions('signal-tail');
          packagingSignalKind = '';
          packagingSignalTail = '';
          // Remove kind / tail dropdown
        }
      });

      $('#signal-kind').on('change', function () {
        packagingSignalKind = this.value;
        removeErrorSpan('signal-kind');

        if (packagingSignalKind && (packagingSignalKind === 'signalpctailcontacts')) {
          // TAIL
          $('#signal-tail').parent().removeClass('form-item__hide');
          if (packagingComponents && packagingComponents.signal22PackageOptions && packagingComponents.signal22PackageOptions.length) {
            let getFilteredCombination = packagingComponents.signal22PackageOptions.filter(item => checkShellRestriction(item) && checkMountingReleaseRestriction(item));
            setDropdownOptionsPackaging('#signal-tail', getFilteredCombination);
          }
          // Populate tail dropdown
        } else {
          $('#signal-tail').parent().addClass('form-item__hide');
          removeSelectOptions('signal-tail');
          // Remove tail dropdown
          setPackagingSourceCode(); // Source code
        }
      });

      $('#signal-tail').on('change', function () {
        packagingSignalTail = this.value;
        removeErrorSpan('signal-tail');
        setPackagingSourceCode(); // Source code
      });

      // ON CHANGE POWER
      $('#edit-packaging-power').on('change', function () {
        packagingPowerWithWithout = this.value;
        removeErrorSpan('edit-packaging-power');
        if (packagingPowerWithWithout) {
          // KIND
          $('#power-kind').parent().removeClass('form-item__hide');
          if (packagingComponents && packagingComponents.power16KindOptions && packagingComponents.power16KindOptions.length) {
            let getFilteredCombination = packagingComponents.power16KindOptions.filter(item => checkShellRestriction(item) && checkMountingReleaseRestriction(item));
            setDropdownOptionsPackaging('#power-kind', getFilteredCombination);
          }
          // Populate kind dropdown
        } else {
          $('#power-kind').parent().addClass('form-item__hide');
          $('#power-tail').parent().addClass('form-item__hide');
          removeSelectOptions('power-kind');
          removeSelectOptions('power-tail');
          packagingPowerKind = '';
          packagingPowerTail = '';
          // Remove kind / tail dropdown
        }
      });


      $('#power-kind').on('change', function () {
        packagingPowerKind = this.value;
        removeErrorSpan('power-kind');
        if (packagingPowerKind && packagingPowerKind === 'powerpctailcontacts') {
          // TAIL
          $('#power-tail').parent().removeClass('form-item__hide');
          if (packagingComponents && packagingComponents.power16PackageOptions && packagingComponents.power16PackageOptions.length) {
            let getFilteredCombination = packagingComponents.power16PackageOptions.filter(item => checkShellRestriction(item) && checkMountingReleaseRestriction(item));
            setDropdownOptionsPackaging('#power-tail', getFilteredCombination);
          }
          // Populate tail dropdown
        } else {
          $('#power-tail').parent().addClass('form-item__hide');
          removeSelectOptions('power-tail');
          // Remove tail dropdown
          setPackagingSourceCode(); // Source code
        }
      });

      $('#power-tail').on('change', function () {
        packagingPowerTail = this.value;
        removeErrorSpan('power-tail');
        setPackagingSourceCode(); // Source code
      });

      // ON CHANGE COAX
      $('#edit-packaging-coax').on('change', function () {
        packagingCoax = this.value;
        removeErrorSpan('edit-packaging-coax');
        removeErrorSpan('packaging-source-code');
        if (packagingCoax) {
          setPackagingSourceCode();
        }
      });

      // ON CHANGE QUADRAX
      $('#edit-packaging-quadrax').on('change', function () {
        packagingQuadrax = this.value;
        removeErrorSpan('edit-packaging-quadrax');
        if (packagingQuadrax) {
          setPackagingSourceCode();
        }
      });

      /**
       *
       * Helper
       *
       */

      function setDropdownOptionsPackaging(id, array) {
        $(id + ' option:not(:first)').remove();
        let op = array.map(item => `<option value=${ item.value ? removeSpaceFromStringAndChangeToLowercase(item.value) : '' }>${ id === '#edit-packaging-coax' ? item.description : item.value }</option>`).join('\n');
        $(id).append(op);
        $(`${ id }`).niceSelect('update');
      }

      function checkShellRestriction(optionItem) {
        return optionItem.shellSizeRestriction !== 0 ? optionItem.shellSizeRestriction === Number(finalShellSize) : true;
      }

      function checkMountingReleaseRestriction(optionItem) {
        return optionItem.additionalRestriction !== null ? checkIfStringContains(optionItem.additionalRestriction, finalContactMountingAndRelease) : true;
      }

      function checkIfStringContains(string, contains) {
        return string.toLowerCase().includes(contains.toLowerCase());
      }


      function setPackagingSourceCode() {
        let secondLetter = (packagingCoax || packagingQuadrax);
        let firstLetter = '';

        // Crimp contact only
        if (packagingSignalKind === 'signalcrimpcontacts' || packagingPowerKind === 'powercrimpcontacts') {
          // Signal without
          if (!packagingSignalWithWithout || packagingSignalWithWithout === 'without') {
            // Power without
            if (!packagingPowerWithWithout || packagingPowerWithWithout === 'without') {
              firstLetter = 'L';
            }
            // Power with
            if (packagingPowerWithWithout === 'with') {
              firstLetter = 'N';
            }
          }

          // Signal with
          if (packagingSignalWithWithout === 'with') {
            // Power without
            if (!packagingPowerWithWithout || packagingPowerWithWithout === 'without') {
              firstLetter = 'B';
            }
            // Power with
            if (packagingPowerWithWithout === 'with') {
              firstLetter = 'A';
            }
          }
        }

        // Crimp + Tail contact
        if (packagingSignalKind === 'signalpctailcontacts' || packagingPowerKind === 'powercrimpcontacts') {

          // Tail length
          if (checkIfStringContains(packagingSignalTail, '3.81') || checkIfStringContains(packagingSignalTail, '0.125')) {
            // Tail plating
            if (checkIfStringContains(packagingSignalTail, 'tin')) {
              firstLetter = 'I';
            }
          }
          // Tail length
          if (checkIfStringContains(packagingSignalTail, '6.35') || checkIfStringContains(packagingSignalTail, '0.25')) {
            // Tail plating
            if (checkIfStringContains(packagingSignalTail, 'tin')) {
              firstLetter = '3';
            }
            if (checkIfStringContains(packagingSignalTail, 'gold')) {
              firstLetter = 'E';
            }
          }
          // Tail length
          if (checkIfStringContains(packagingSignalTail, '9.52') || checkIfStringContains(packagingSignalTail, '0.375')) {
            // Tail plating
            if (checkIfStringContains(packagingSignalTail, 'gold')) {
              firstLetter = 'P';
            }
          }
        }

        // Tail contact only
        if (packagingSignalKind === 'signalpctailcontacts' || packagingPowerKind === 'powerpctailcontacts') {
          // Signal with
          if (packagingSignalWithWithout === 'with') {
            // Power without
            if (!packagingPowerWithWithout || packagingPowerWithWithout === 'without') {
              // Tail length
              if ((checkIfStringContains(packagingSignalTail, '3.81') && (packagingPowerWithWithout ? checkIfStringContains(packagingPowerTail, '3.81') : true))
                ||
                (checkIfStringContains(packagingSignalTail, '0.125') && (packagingPowerWithWithout ? checkIfStringContains(packagingPowerTail, '0.125') : true))
              ) {
                // Tail plating
                if (checkIfStringContains(packagingSignalTail, 'gold') && (packagingPowerWithWithout ? checkIfStringContains(packagingPowerTail, 'gold') : true)) {
                  firstLetter = 'G';
                }
              }

              // Tail length
              if ((checkIfStringContains(packagingSignalTail, '9.52') && (packagingPowerWithWithout ? checkIfStringContains(packagingPowerTail, '9.52') : true))
                ||
                (checkIfStringContains(packagingSignalTail, '0.375') && (packagingPowerWithWithout ? checkIfStringContains(packagingPowerTail, '0.375') : true))) {
                // Tail plating
                if (checkIfStringContains(packagingSignalTail, 'gold') && (packagingPowerWithWithout ? checkIfStringContains(packagingPowerTail, 'gold') : true)) {
                  firstLetter = 'R';
                }
              }

              // Tail length
              if ((checkIfStringContains(packagingSignalTail, '12.7') && (packagingPowerWithWithout ? checkIfStringContains(packagingPowerTail, '12.7') : true))
                ||
                (checkIfStringContains(packagingSignalTail, '0.5') && (packagingPowerWithWithout ? checkIfStringContains(packagingPowerTail, '0.5') : true))) {
                // Tail plating
                if (checkIfStringContains(packagingSignalTail, 'tin') && (packagingPowerWithWithout ? checkIfStringContains(packagingPowerTail, 'tin') : true)) {
                  firstLetter = '2';
                }

                // Tail plating
                if (checkIfStringContains(packagingSignalTail, 'gold') && (packagingPowerWithWithout ? checkIfStringContains(packagingPowerTail, 'gold') : true)) {
                  firstLetter = 'X';
                }
              }
            }
          }

          // Signal with
          if (packagingSignalWithWithout === 'with') {
            // Power without
            if (packagingPowerWithWithout === 'with') {

              // Tail length
              if ((checkIfStringContains(packagingSignalTail, '6.35') && checkIfStringContains(packagingPowerTail, '6.35'))
                ||
                (checkIfStringContains(packagingSignalTail, '0.25') && checkIfStringContains(packagingPowerTail, '0.25'))) {
                // Tail plating
                if (checkIfStringContains(packagingSignalTail, 'tin') && checkIfStringContains(packagingPowerTail, 'tin')) {
                  firstLetter = 'H';
                }

                // Tail plating
                if (checkIfStringContains(packagingSignalTail, 'gold') && checkIfStringContains(packagingPowerTail, 'gold')) {
                  firstLetter = 'Y';
                }
              }

              // Tail length
              if ((checkIfStringContains(packagingSignalTail, '12.7') && checkIfStringContains(packagingPowerTail, '12.7'))
                ||
                (checkIfStringContains(packagingSignalTail, '0.5') && checkIfStringContains(packagingPowerTail, '0.5'))) {
                // Tail plating
                if (checkIfStringContains(packagingSignalTail, 'tin') && checkIfStringContains(packagingPowerTail, 'tin')) {
                  firstLetter = '4';
                }
              }
            }
          }

          if (!packagingSignalWithWithout || packagingSignalWithWithout === 'without') {
            // Power without
            if (packagingPowerWithWithout === 'with') {
              // Tail length
              if (((packagingSignalWithWithout ? checkIfStringContains(packagingSignalTail, '6.35') : true) && checkIfStringContains(packagingPowerTail, '6.35'))
                ||
                ((packagingSignalWithWithout ? checkIfStringContains(packagingSignalTail, '0.25') : true) && checkIfStringContains(packagingPowerTail, '0.25'))) {
                // Tail plating
                if ((packagingSignalWithWithout ? checkIfStringContains(packagingSignalTail, 'tin') : true) && checkIfStringContains(packagingPowerTail, 'tin')) {
                  firstLetter = '5';
                }
              }
            }
          }
        }

         // Check first and second number

        function checkFirstSecondNumber() {
          $.ajax({
            type: 'GET',
            url: aricContainer.attr('data-resource') + '.shellSize$' + finalShellSize + '.mountingRelease$' + finalContactMountingAndRelease + '.mountingReleaseCode$' + finalContactMountingAndRelease + '.pkgFirstLetter$' + firstLetter.toUpperCase() + '.pkgSecondLetter$' + secondLetter.toUpperCase() + '.json',
            success: function success(response) {
              if (response) {
                let checkResult = '';
                if (typeof response === 'string') {
                  checkResult = JSON.parse(response);
                } else {
                  checkResult = response;
                }
                checkFirstSecond(checkResult);
              }
            },
            error: function error(err) {
              console.log('ERROR', err);
            }
          });
        }


        function checkFirstSecond(response) {
          if (response && response !== {} && !$.isEmptyObject(response)) {
            checkTF = response.validPackage;
            if (checkTF === true) {
              $('#packaging-source-code').next('span').remove();
              finalPackageingCode = firstLetter.toUpperCase() + secondLetter.toUpperCase();
              $('#packaging-source-code').val(finalPackageingCode);
              removeErrorSpan('packaging-source-code');

            }
            else {
              $('#packaging-source-code').html(' ');
              $('#packaging-source-code').val('');
              $('#packaging-source-code').after('<span class=\'error-msg\'>  ' + aricContainer.attr('data-CodificationMessage') + ' </span>');
            }
          }
        }

    // End Check first and second number

        if (packagingConnectorRadio === 'with-contacts') {
          if (firstLetter && secondLetter) {
            checkFirstSecondNumber();
          }
        }
      }
    }

    /**
     *
     * Validation
     *
     */

    function validation() {
      let validFlag = true;

      // Section -> Shell, Polarization, Insert configuration
      components.forEach(comp => {
        if (comp.componentGroup) {
          let fieldValue = '';

          if (comp.formElementType === jsonConstantFieldCheckbox) {
            fieldValue = true;
          } else if (comp.formElementType === jsonConstantFieldRadio) {
            fieldValue = $(`input:radio[name=${ comp.id }]`).is(':checked');
          } else {
            if ($(`#${ comp.id }`).attr('class') === 'cavity-field cavity-shell-size-3 wide' && Number(finalShellSize) === Number(3)) {
              fieldValue = $(`#${ comp.id }`).val();
            } else if ($(`#${ comp.id }`).attr('class') === 'cavity-field cavity-shell-size-3 wide' && Number(finalShellSize) !== Number(3)) {
              fieldValue = true;
            } else {
              fieldValue = $(`#${ comp.id }`).val();
            }
          }

          if (!fieldValue) {
            validFlag = false;
            removeErrorSpan(comp.id);
            if (comp.formElementType !== jsonConstantFieldCheckbox) {
              $(`#${ comp.id }`).next().after(`<span class='error-msg'> ${ comp.componentType.name } ${ aricContainer.attr('data-MandatoryFieldMessage') } </span>`);

            }
          }
        }
      });

      // Section -> Packaging
      $('div:not(.form-item__hide) > select').each(function () {  // Get all select element within packaging div which does not have class 'form-item__hide'
        let idName = this.id;
        let fieldValue = '';

        if (idName.length > 3) {
          fieldValue = $(`#${ idName }`).val();

          if (!fieldValue) {
            validFlag = false;
            removeErrorSpan(idName);
            $(`#${ idName }`).next().after(`<span class='error-msg'>  ${ aricContainer.attr('data-MandatoryFieldMessage') } </span>`);
          }
        }
      });

      return validFlag;
    }

    /**
     *
     * Submit
     *
     */

    $(submitButton).on('submit', function (e) {
      e.preventDefault();
      let validFlag = validation();

      if (finalInsertArrangementPowerSourceCodeCavityABDE !== '') {
        if (validFlag2 || (validFlag && checkTF) ) {
          $('#failed-result').addClass('u-hide');
          $('#valid-result').removeClass('u-hide');

          if (checkTF === false) {
            finalPackageingCode = '';
          }

          let calculatedResult = finalBasicSeries + finalConnectorClass + finalShellSize + finalShellType + finalContactMountingAndRelease + finalMountingStyle + finalInsertArrangementPowerSourceCodeCavityCF + finalContactType + finalInsertArrangementPowerSourceCodeCavityABDE + finalPolarizationCode + finalPackageingCode;
          $('#calc-result')[0].innerHTML = calculatedResult;
          $('.scroll-here')[0].scrollIntoView({
            behavior: 'smooth', inline: 'center', block: 'center'
          });

        }
      } else {
        $('#valid-result').addClass('u-hide');
        $('#failed-result').removeClass('u-hide');
      }

    });
  }
});