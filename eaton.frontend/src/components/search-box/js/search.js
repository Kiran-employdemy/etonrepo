//-----------------------------------
// Component : Search
//-----------------------------------
'use strict';

let App = window.App || {};

App.search = (function (autosize) {

	// Variable Declarations
  const componentClass = '.eaton-search';
  const $componentElement = $(componentClass);
  const $searchInputEl = $componentElement.find('.eaton-search--default__form-input');
	// Check AEM Author Mode
  const isAEMAuthorMode = App.global.utils.isAEMAuthorMode();

	/**
	 * Init
	 */
  const init = () => {
		// If not in AEM Author Mode & component exists on page - initialize scripts
    if (!isAEMAuthorMode) {
      addEventListeners();

			// Intercept Carriage Return on TextArea and submit form.
      $searchInputEl.keydown(function (e) {
        const evt = e || window.event; // compliant with ie6
        const keyCode = evt.keyCode || evt.which;
        let inputVal = e.target.value; // Targets the active input
        const $activeSearchComponent = $(e.currentTarget).closest(componentClass);

				// Remove new lines on the 'active' text area if no character exists
        e.target.value = inputVal.replace(/^\s*(\n)\s*$/, '');

				// Detect Carriage return
        if (keyCode === 13) {
          if (e.target.value.length >= 1) {
						// Allow submit only if the textarea has atleast one alphanumeric character on carriage return
            if ('location-search-box' === $(this).attr('id')) {
              let resourcePath = $('.mapContainer').attr('data-resource-path') + '.json';
              App.Bullseye.getBullsEyeResponse(resourcePath);
            } else {
              $activeSearchComponent.find('form').submit();
            }
          }
          return false;
        }
      });


      $searchInputEl.focus(function (e) {
        $('.eaton-search__career .eaton-search--default__results').removeClass('active');
        if (e.target.value.length >= 3 && (e.target.id.indexOf('primary-menu-search') !== -1)) {
        // Request Career Search Results- AJAX if career search field
          getJibeAPICall(e, e.target.value);
        }
      });

      $(document).click(function(e) {
        if ($(e.target).closest('.eaton-search__career').length !== 1) {
          $('.eaton-search__career .eaton-search--default__results').removeClass('active');}
      });
    }
  };

 /**
   * Create Template - Markup for each predictive search result item
   */
  let linkTemplate = function linkTemplate(data, term) {
    let regX = new RegExp('(' + term + ')', 'ig');
    let linkTitleText = data.title;
    let secureIcon = data.secure ? '<i class="icon icon-secure-lock" aria-hidden="true"></i>' : '';

     // Search the title for the matched term and wrap it in required markup
    linkTitleText = linkTitleText.replace(regX, '<strong>$1</strong>');

    return '\n      <li class="eaton-search--default__result-item b-body-copy">\n        <a href="' + data.link + '" target="_self" >' + secureIcon + linkTitleText + ' </a>\n      </li>';
  };

  /**
   * Create Template - Markup for each predictive career search result item
   */
  const linkTemplateCareer = function linkTemplateCareer(data, term, category) {
    const regX = new RegExp('(' + term + ')', 'ig');
    let linkReference = '';
    let linkTitleText = data;
    if (category === 'keyword') {
      linkReference = 'https://jobs.eaton.com/jobs?keywords=' + linkTitleText + '&page=1';
    } else {
      linkReference = 'https://jobs.eaton.com/jobs?location=' + linkTitleText + '&page=1';
    }

    // Search the title for the matched term and wrap it in required markup
    linkTitleText = linkTitleText.replace(regX, '<strong>$1</strong>');

    return '\n      <li class="eaton-search--default__result-item b-body-copy">\n        <a href="' + linkReference + '" target="_blank" > ' + linkTitleText + ' </a>\n      </li>';
  };

	/**
	 * Handle Input Behaviors
	 */
  const handleInputBehavior = (event) => {

		// Check if the #of characters in the inputBox exceeds characterLimit - 3
    const $activeSearchComponent = $(event.currentTarget).closest(componentClass);
    const inputVal = event.target.value;


    if (event.target.value.length >= 3) {
			// Request Search Results - AJAX
      getSearchResults(event, inputVal);
      // Request Career Search Results- AJAX if career search field
      if (event.target.id.indexOf('primary-menu-search') !== -1) {
        getJibeAPICall(event, inputVal);
      }
    } else {
			// Empty the contents of the result-list
      $activeSearchComponent.find('.eaton-search--default__results').removeClass('active');
      $activeSearchComponent.find('.eaton-search--default__result-list').html('');
    }
  };



	/**
	 * Load Predictive Career Search Results - AJAX
	 */

  function getJibeAPICall(event, term) {
    // Get the closest search component to avoid conflicts when multiple search elements on page
    const $activeSearchComponent = $(event.currentTarget).closest('.eaton-search__career-field');
    let resultList = '';
    let careerUrl = '';
    let listData = '';
    let searchCategory = '';
    careerUrl = 'https://api.jibe.com/v1/jobs/google/typeahead/' + term;
    searchCategory = 'keyword';

    {$.ajax({
      type: 'GET',
      url: careerUrl,
      dataType: 'json',
      data: '{}',
      headers: {
        app_key: 'eaton',
        app_id: '7077e7b9',
        'Accept-Language': 'en-us',
        Accept: 'application/json; charset=UTF-8'
      },
      success: function (data) {
        if (event.target.id.indexOf('location') !== -1) {
          listData = data.locations;
        }
        else {
          listData = data;
        }
        $.each(listData, function (index, item) {
          resultList += linkTemplateCareer(item, term , searchCategory);
        });
        if (resultList.length > 0) {
          // Replace the contents of the list with the AJAX results
          $activeSearchComponent.find('.eaton-search--default__result-list').html(resultList);
          $activeSearchComponent.find('.eaton-search--default__results').addClass('active');
        }
      },
      error: function(e) {
        console.info('Error');
      }
    });
    }
  }

	/**
	 * Load Predictive Search Results - AJAX
	 */
  const getSearchResults = (event, term) => {


		// Get the closest search component to avoid conflicts when multiple search elements on page
    const $activeSearchComponent = $(event.currentTarget).closest(componentClass);
    const searchResultsURL = $activeSearchComponent.attr('data-predictive-search');
    /* eslint-disable no-unused-vars*/
    const requestOptions = {
      searchTerm: term,
      format: 'json'
    };

    let resultList = '';
    let ajaxReq = '';

		// If URL path is configured
    if (!searchResultsURL) {
      return;
    }

		// Requests Static JSON file. To be replaced by service URL in final implementation
		// ajaxReq = $.getJSON(searchResultsURL, requestOptions);
        // var termCoded = term.replace("&", "(({{}}))");
    let encodedTerm;

    let regEx = new RegExp('/', 'g');
    encodedTerm = term.replace(regEx,'{}');

    regEx = new RegExp('=', 'g');
    encodedTerm = encodedTerm.replace(regEx,'[]');

    regEx = new RegExp('\\.', 'g');
    encodedTerm = encodedTerm.replace(regEx,'::');

    regEx = new RegExp('\\$', 'g');
    encodedTerm = encodedTerm.replace(regEx,'<>');

    regEx = new RegExp('&', 'g');
    encodedTerm = encodedTerm.replace(regEx,'%%');

    regEx = new RegExp('<', 'g');
    encodedTerm = encodedTerm.replace(regEx,'');

    // alert(encodedTerm);

    encodedTerm = encodeURIComponent(encodedTerm);

		// alert(term);
		// alert(encodedTerm);

    ajaxReq = $.ajax({
      type: 'GET',
      url: '/eaton/content/search/predective-search.json',
      data: 'url=' + searchResultsURL + '&searchTerm=' + encodedTerm,
      dataType: 'json',
      success: function success(data) {

        $.each(data.results, function (index, item) {
          resultList += linkTemplate(item, term);
        });
        if (resultList.length > 0) {
				// Replace the contents of the list with the AJAX results
          $activeSearchComponent.find('.eaton-search--default__result-list').html(resultList);
          // uncomment below code to add the type-ahead functionality
          // $activeSearchComponent.find('.eaton-search--default__results').addClass('active');
        }
      }
    });

		/* ajaxReq
		// Callback for Successful Request
		.done(function (data) {

		  // Loop over all result items
		  $.each(data.results, function (index, item) {
		    resultList += linkTemplate(item, term);
		  });

		  // Replace the contents of the list with the AJAX results
		  $activeSearchComponent.find('.eaton-search--default__result-list').html(resultList);
		  $activeSearchComponent.find('.eaton-search--default__results').addClass('active');
		})

		// Callback for Failed Request
		.fail(function (data) {
		  console.error('error', data);
		});*/

  };

  const populateSearchBox = () => {
    /* eslint-disable no-undef */
    if (dataLayer.pageType === 'site search') {
      $('#site-search-box').val() === '' && $('#site-search-box').val(dataLayer.searchQuery);
    }
    /* eslint-enable no-undef */
  };

	/**
	 * Bind All Event Listeners
	 */
  const addEventListeners = () => {
    $searchInputEl.on('keyup', handleInputBehavior);
    $(document).ready(populateSearchBox);
  };

	/**
	 * If containing DOM element is found, Initialize and Expose public methods
	 */
  if ($componentElement.length > 0) {
    autosize($('.search-box textarea'));
    autosize($('.eaton-search textarea'));
    autosize($('.eaton-header textarea'));
    init();
  }
}(window.autosize));
$(document).ready(function () {
  if (window.location.href.indexOf('login') !== -1) {
    $('.search__utility').hide();
  }
  if (window.location.href.indexOf('eatoncummins') !== -1) {
    $('.header-search .eaton-search--default').show();
    $('.header-primary-nav').css('position', 'relative');
    $('.header-primary-nav__links').show();
  }
  if (window.location.href.indexOf('ecjv') !== -1) {
    $('.header-search .eaton-search--default').show();
    $('.header-primary-nav').css('position', 'relative');
    $('.header-primary-nav__links').show();
  }
  if (window.location.href.indexOf('eaton-cummins') !== -1) {
    $('.header-search .eaton-search--default').show();
    $('.header-primary-nav').css('position', 'relative');
    $('.header-primary-nav__links').show();
  }

});

/* eslint-enable no-unused-vars*/

$(document).ready(function () {
  if ( $('#site-search-box').val() === '##IGNORE##') {
    $('#site-search-box').val('').empty();
  }
});