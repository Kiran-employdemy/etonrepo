//-----------------------------------
// Conduit Size Calculator Component
//-----------------------------------
$(document).ready(function() {
  $('select').niceSelect();
  let calBtn = $('#souriau-toolkit-counduit-size-calc-main-form');
  let errorPane = $('.u-error');
  let successPane = $('.u-success');
  let errorClose = $('.u-error-close');
  let successClose = $('.u-success-close');
  let calData = $('#calculator');
  let calTitle = $('#calc-title');

  $(calTitle).hide();

      /**
       * Function to calculate total wires and average diameter of wires
       *
       */
  function souriauToolkitConduitSizeCalcMainCalculator(values) {
        // Read the values from the input fields.
    let fillRate = values.fillRate;
    let numWires = values.rows.numWires;
    let wireDiameter = values.rows.wireDiameter;
    let totalWires = 0;
    let averageDiameter = 0;
    let numericValidated = false;
    let setError = false;

    // Check if wires are empty
    if (Object.values(numWires).every(x => !x || x === null || x === '')) {
      setError = true;
      return souriauToolkitConduitSizeCalcSetError(calData.attr('data-empty-wire-text'));
    } else {
      // Check if wires are numeric and calculate the total number of wires.
      Object.entries(numWires).forEach(([key, val]) => {
        if (!isNaN(val) && !numericValidated) {
          totalWires += Number(val);
        } else {
          numericValidated = true;
          setError = true;
          return souriauToolkitConduitSizeCalcSetError(calData.attr('data-nonnumeric-text'));
        }
      });

      if (totalWires && !numericValidated) {
            // Calculate the average diameter.
        Object.entries(wireDiameter).forEach(([key, val]) => {
          if (!isNaN(val) && !numericValidated) {
            averageDiameter += (val * numWires[key]);
          } else {
            numericValidated = true;
            setError = true;
            return souriauToolkitConduitSizeCalcSetError(calData.attr('data-nonnumeric-text'));
          }
        });
        if (!numericValidated) {
          averageDiameter /= totalWires; // Get the factor element.
          let factored = souriauToolkitConduitSizeCalcGetFactored(Number(totalWires), Number(averageDiameter));
          if (factored === 'FALSE') {
            setError = true;
            return souriauToolkitConduitSizeCalcSetError(calData.attr('data-many-wire-text'));
          }
          if (factored !== null && factored !== 'FALSE') {
                // Get the conduit size based on fill rate and factor.'
            setError = true;
            return souriauToolkitConduitSizeCalcGetConduitSize(fillRate, factored);
          }
        }
      }
    }

    if (!setError) {
      souriauToolkitConduitSizeCalcSetError(calData.attr('data-no-wire-text'));
    }
  }

      /**
       * Function which returns the conduit size.
       * Return conduit size string.
       */
  function souriauToolkitConduitSizeCalcGetConduitSize(fillRate, factored) {
    let conduitSize = null;
    let getStandardFactor = souriauToolkitConduitSizeCalcGetStandardFactored(fillRate);
    let flag = true;
    Object.entries(getStandardFactor).forEach(([key, val]) => {
      if (factored <= val && flag) {
        conduitSize = souriauToolkitCounduitSizeCalcGetStandard(key);
        flag = false;
      }
    }); // If the result is null, show fill rate error message.
    if (conduitSize === null && Number(fillRate) === 98) {
      return souriauToolkitConduitSizeCalcSetError(calData.attr('data-many-wire-text'));
    } else if (conduitSize === null) {
      return souriauToolkitConduitSizeCalcSetError(fillRate + ' ' + calData.attr('data-fill-rate-text'));
    }
    return conduitSize;
  }

      /**
       * Function which returns the factor component for calculation.
       *
       */
  function souriauToolkitConduitSizeCalcGetFactored(totalWires, averageDiameter) {
    let factored = null;
    if (totalWires <= 2) {
      factored = (totalWires * 1.0) * (averageDiameter);
    } else if (totalWires <= 4) {
      factored = (2 + (0.2 * (totalWires - 1))) * (averageDiameter);
    } else if (totalWires <= 6) {
      factored = (2.7 + (0.3 * (totalWires - 5))) * (averageDiameter);
    } else if (totalWires <= 8) {
      factored = (2.7 + (0.3 * (totalWires - 6))) * (averageDiameter);
    } else if (totalWires <= 10) {
      factored = (3.8 + (0.2 * (totalWires - 9))) * (averageDiameter);
    } else if (totalWires <= 14) {
      factored = (4 + (0.15 * (totalWires - 10))) * (averageDiameter);
    } else if (totalWires <= 15) {
      factored = (4.8) * (averageDiameter);
    } else if (totalWires <= 20) {
      factored = (5 + (0.15 * (totalWires - 16))) * (averageDiameter);
    } else if (totalWires <= 24) {
      factored = (5.6 + (0.1 * (totalWires - 20))) * (averageDiameter);
    } else if (totalWires <= 28) {
      factored = (6 + (0.125 * (totalWires - 24))) * (averageDiameter);
    } else if (totalWires <= 32) {
      factored = (6.5 + (0.1 * (totalWires - 28))) * (averageDiameter);
    } else if (totalWires <= 36) {
      factored = (6.9 + (0.125 * (totalWires - 32))) * (averageDiameter);
    } else if (totalWires <= 40) {
      factored = (7.4 + (0.075 * (totalWires - 36))) * (averageDiameter);
    } else if (totalWires <= 80) {
      factored = (7.7 + (0.08 * (totalWires - 40))) * (averageDiameter);
    } else if (totalWires <= 90) {
      factored = (10.9 + (0.07 * (totalWires - 80))) * (averageDiameter);
    } else if (totalWires <= 125) {
      factored = (11.6 + (0.06 * (totalWires - 90))) * (averageDiameter);
    } else if (totalWires <= 150) {
      factored = (13.7 + (0.052 * (totalWires - 125))) * (averageDiameter);
    } else if (totalWires <= 200) {
      factored = (15 + (0.044 * (totalWires - 150))) * (averageDiameter);
    } else if (totalWires <= 250) {
      factored = (17.2 + (0.042 * (totalWires - 200))) * (averageDiameter);
    } else if (totalWires <= 300) {
      factored = (19.3 + (0.034 * (totalWires - 200))) * (averageDiameter);
    } else {
      return 'FALSE';
    }
    return factored;
  }

      /**
       * The function which defines the standard factors.
       * Returns the factor components based on fill rate
       */
  function souriauToolkitConduitSizeCalcGetStandardFactored(fillRate) {
    let factoredStandard = {
      50: [0.133, 0.199, 0.221, 0.265, 0.31, 0.354, 0.442, 0.53, 0.621, 0.707, 0.884, 1.06, 1.237, 1.416],
      60: [0.145, 0.218, 0.243, 0.29, 0.34, 0.387, 0.484, 0.581, 0.682, 0.774, 0.968, 1.161, 1.355, 1.551],
      70: [0.157, 0.235, 0.262, 0.313, 0.367, 0.418, 0.523, 0.627, 0.735, 0.836, 1.046, 1.254, 1.464, 1.676],
      80: [0.167, 0.251, 0.279, 0.335, 0.391, 0.447, 0.56, 0.671, 0.783, 0.895, 1.118, 1.341, 1.565, 1.788],
      90: [0.178, 0.267, 0.296, 0.356, 0.415, 0.474, 0.593, 0.712, 0.831, 0.949, 1.186, 1.422, 1.66, 1.9],
      98: [0.1868, 0.2807, 0.312, 0.3746, 0.4372, 0.4998, 0.625, 0.7502, 0.8754, 1.0006, 1.251, 1.501, 1.752, 2.002]
    };
    return factoredStandard[Number(fillRate)];
  }

      /**
       * The standard conduit size is defined here.
       * Returns the correct size for a factor key.
       */
  function souriauToolkitCounduitSizeCalcGetStandard(key) {
    let conduitSizeStandard = [
      '06 - 3/16',
      '09 - 9/32',
      '10 - 5/1',
      '12 - 3/8',
      '14 - 7/16',
      '16 - 1/2',
      '20 - 5/8',
      '24 - 3/4',
      '28 - 7/8',
      '32 - 1',
      '40 - 1-1/4',
      '48 - 1-1/2',
      '56 - 1-3/4',
      '64 - 2.000'
    ];
    return conduitSizeStandard[Number(key)] + ' ' + calData.attr('data-inches-text');
  }

      /**
       * Helper function for setting errors.
       *
       */
  function souriauToolkitConduitSizeCalcSetError(message) {
    $(successPane).hide();
    $(errorPane).css({ display: 'block', opacity: '0' });
    $(calTitle).show();
    $('#error-banner')[0].innerHTML = message;
    setTimeout(function () { $(errorPane).css('opacity', '1'); }, 100);
  }

      /**
       * Function to get values from table
       *
       */
  function calculateValues(values) {
    let valuesToCalculate = values && values.length ? values[0] : {};
    return souriauToolkitConduitSizeCalcMainCalculator(valuesToCalculate);
  }

  function formatTableValues(values) {
    let arr = [];
    arr.push(values);
    return arr.map(item => ({
      fillRate: item.fillRate,
      rows: {
        numWires: {
          one: item['rows[numWires][one]'],
          two: item['rows[numWires][two]'],
          three: item['rows[numWires][three]'],
          four: item['rows[numWires][four]'],
          five: item['rows[numWires][five]']
        },
        wireDiameter: {
          one: item['rows[wireDiameter][one]'],
          two: item['rows[wireDiameter][two]'],
          three: item['rows[wireDiameter][three]'],
          four: item['rows[wireDiameter][four]'],
          five: item['rows[wireDiameter][five]']
        }
      }
    }));
  }

  function getValuesFromTable() {
    let getValuesFromTable = $('#souriau-toolkit-counduit-size-calc-main-form').serializeArray().reduce((obj, item) => {
      obj[item.name] = item.value;
      return obj;
    }, {});

    let formatedValues = formatTableValues(getValuesFromTable);
    let calculatedResult = calculateValues(formatedValues);

    if (calculatedResult) {
      $(errorPane).hide();
      $(successPane).css({ display: 'block', opacity: '0' });
      $(calTitle).show();
      setTimeout(function () { $(successPane).css('opacity', '1'); }, 100);
      $('#success-banner')[0].innerHTML = calData.attr('data-calculation-text');
      $('#calc-result')[0].innerHTML = calculatedResult;
    } else {
      $('#calc-result')[0].innerHTML = calData.attr('data-error-failure-text');
    }
    return false;
  }

  $('#edit-fill-rate').change(function () {
    $('.scroll-here')[0].scrollIntoView({
      behavior: 'smooth', inline: 'center', block: 'center'
    });
    return getValuesFromTable();
  });

  $(calBtn).on('submit', function() {
    $('.scroll-here')[0].scrollIntoView({
      behavior: 'smooth', inline: 'center', block: 'center'
    });
    return getValuesFromTable();
  });

  $(errorClose).click(function(e) {
    e.preventDefault();
    if (e.target !== e.currentTarget) {
      $(e.target.parentElement.parentElement).hide();
    }
  });

  $(successClose).click(function(e) {
    e.preventDefault();
    if (e.target !== e.currentTarget) {
      $(e.target.parentElement.parentElement).hide();
    }
  });

});
