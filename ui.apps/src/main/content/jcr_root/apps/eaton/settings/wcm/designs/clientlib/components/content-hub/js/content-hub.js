/**
 *
 *
 *
 * - THIS IS AN AUTOGENERATED FILE. DO NOT EDIT THIS FILE DIRECTLY -
 * - Generated by Gulp (gulp-babel).
 *
 *
 *
 *
 */


'use strict';

$(document).ready(function () {
  if ($('#content-hub__component_id').length) {
    var totalResultsCount = $('#content-hub__component_id').data('result-list-size');
    var initialResultsLoadSize = $('#content-hub__component_id').data('initial-results-to-load');
    var defaultIconPath = $('#content-hub__component_id').data('default-icon-path');
    var numberOfResultsLoaded = 0;
    var singleFilterFlag = false;
    var multiGroupFilterIndex = 0;
    var loadMoreCount = 1;
    var resultListWithFilterSelection = [];
    if (totalResultsCount > initialResultsLoadSize) {
      resultsDisplay(0, initialResultsLoadSize, defaultIconPath);
      $('#contentHubLoadMore').removeClass('hide');
    } else if (totalResultsCount > 0 && totalResultsCount < initialResultsLoadSize) {
      resultsDisplay(0, totalResultsCount, defaultIconPath);
    } else if (totalResultsCount === initialResultsLoadSize) {
      resultsDisplay(0, initialResultsLoadSize, defaultIconPath);
    }
    if (totalResultsCount > initialResultsLoadSize) {
      $('#contentHubLoadMore').removeClass('hide');
    }
    $('#contentHubLoadMore').click(function () {
      numberOfResultsLoaded = numberOfResultsLoaded + initialResultsLoadSize <= totalResultsCount ? numberOfResultsLoaded + initialResultsLoadSize : totalResultsCount;
      if (numberOfResultsLoaded + initialResultsLoadSize >= totalResultsCount) {
        $('#contentHubLoadMore').addClass('hide');
      }
      if (multiGroupFilterIndex > 1) {
        loadMoreCount++;
        resultsDisplayWithFilter(resultListWithFilterSelection, 0, initialResultsLoadSize * loadMoreCount, defaultIconPath);
      } else if (singleFilterFlag === true) {
        loadMoreCount++;
        resultsDisplayWithFilter(resultListWithFilterSelection, 0, initialResultsLoadSize * loadMoreCount, defaultIconPath);
      } else {
        resultsDisplay(numberOfResultsLoaded, initialResultsLoadSize, defaultIconPath);
      }
    });

    $('.faceted-navigation__facet-value-label .input--small').click(function () {
      $('.content-hub-results').empty();
      var filterSelected = false;
      var cardDisplayed = [];
      var cardDisplayedIndex = 0;
      var multiGroupFilter = [];
      var multiGroupFilterFacets = [];
      var groupNames = [];
      multiGroupFilterIndex = 0;
      loadMoreCount = 1;
      var multiGroupFilterFacetsIndex = 0;
      var groupNamesIndex = 0;
      resultListWithFilterSelection = [];
      $('.input--small').each(function () {
        if ($(this).prop('checked') === true) {
          var checkBoxId = $(this).attr('id');
          if (!multiGroupFilter.includes(checkBoxId)) {
            multiGroupFilter[multiGroupFilterIndex] = checkBoxId;
            multiGroupFilterFacets[multiGroupFilterFacetsIndex] = $(this).val();
            multiGroupFilterIndex++;
            multiGroupFilterFacetsIndex++;
          }
          groupNames[groupNamesIndex] = checkBoxId + '$' + $(this).attr('data-tagId');
          groupNamesIndex++;
        }
      });

      if (multiGroupFilterIndex > 1) {
        $('.input--small').each(function () {

          if ($(this).prop('checked') === true) {
            filterSelected = true;
            var checkedFilterValue = $(this).attr('data-tagId');
            var resultList = $('#content-hub__component_id').data('result-list-json').slice(0, totalResultsCount);
            $.each(resultList, function (key, value) {
              var cqTagIds = resultList[key].cqTagsWithPath;
              if ($.inArray(checkedFilterValue, cqTagIds) !== -1) {

                var flag = flagCheck(cqTagIds, groupNames, multiGroupFilterIndex);

                if (flag === true) {
                  if (!cardDisplayed.includes(resultList[key].linkPath)) {
                    cardDisplayed[cardDisplayedIndex] = resultList[key].linkPath;
                    var item = {};
                    item.linkImageAltText = resultList[key].linkImageAltText;
                    item.eyebrowLink = resultList[key].eyebrowLink;
                    item.linkEyebrow = resultList[key].linkEyebrow;
                    item.linkPath = resultList[key].linkPath;
                    item.linkTitle = resultList[key].linkTitle;
                    item.linkImage = resultList[key].linkImage;
                    item.productImageBean = resultList[key].productImageBean;
                    item.publicationDateDisplay = resultList[key].publicationDateDisplay;
                    resultListWithFilterSelection.push(item);
                    cardDisplayedIndex++;
                  }
                }
              }
            });
          }
        });
        if (cardDisplayedIndex === 0) {
          // Fix for failed test case, hide load more button
          $('#contentHubLoadMore').addClass('hide');
        }

        resultsDisplayWithFilter(resultListWithFilterSelection, 0, initialResultsLoadSize, defaultIconPath);

        if (filterSelected === false) {

          resultsDisplay(0, initialResultsLoadSize, defaultIconPath);
          $('#contentHubLoadMore').removeClass('hide');
        }
      } else {
        $('.input--small').each(function () {
          if ($(this).prop('checked') === true) {
            filterSelected = true;
            var checkedFilterValue = $(this).attr('data-tagId');
            var resultList = $('#content-hub__component_id').data('result-list-json').slice(0, totalResultsCount);
            $.each(resultList, function (key, value) {
              var cqTagIds = resultList[key].cqTagsWithPath;
              if ($.inArray(checkedFilterValue, cqTagIds) !== -1) {
                if (!cardDisplayed.includes(resultList[key].linkPath)) {
                  cardDisplayed[cardDisplayedIndex] = resultList[key].linkPath;
                  var item = {};
                  item.linkImageAltText = resultList[key].linkImageAltText;
                  item.eyebrowLink = resultList[key].eyebrowLink;
                  item.linkEyebrow = resultList[key].linkEyebrow;
                  item.linkPath = resultList[key].linkPath;
                  item.linkTitle = resultList[key].linkTitle;
                  item.linkImage = resultList[key].linkImage;
                  item.productImageBean = resultList[key].productImageBean;
                  item.publicationDateDisplay = resultList[key].publicationDateDisplay;
                  resultListWithFilterSelection.push(item);
                  cardDisplayedIndex++;
                  singleFilterFlag = true;
                }
              }
            });
          }
        });
        if (cardDisplayedIndex === 0) {
          // Fix for failed test case, hide load more button
          $('#contentHubLoadMore').addClass('hide');
        }

        resultsDisplayWithFilter(resultListWithFilterSelection, 0, initialResultsLoadSize, defaultIconPath);

        if (filterSelected === false) {
          totalResultsCount = $('#content-hub__component_id').data('result-list-size');
          initialResultsLoadSize = $('#content-hub__component_id').data('initial-results-to-load');
          numberOfResultsLoaded = 0;
          multiGroupFilterIndex = 0;
          singleFilterFlag = false;
          resultsDisplay(0, initialResultsLoadSize, defaultIconPath);
          if (totalResultsCount > initialResultsLoadSize) {
            $('#contentHubLoadMore').removeClass('hide');
          }
        }
      }
    });

    $('#contentHub-filters').click(function (e) {
      $('body').addClass('facets-open');
      e.stopPropagation();
    });
    $('#contenthub-filterClose').click(function (e) {
      $('body').removeClass('facets-open');
      e.stopPropagation();
    });

    var searchDisply = function searchDisply(searchInput, searchArray) {
      var currentFocus = void 0;
      searchInput.addEventListener('input', function (e) {
        var divCreate = this.value;
        var searchReslt = this.value;
        var i = this.value;
        var val = this.value;
        var cardDisplayed = [];
        var cardDisplayedIndex = 0;
        closeAllLists();
        if (!val) {
          $('.content-hub-results').empty();
          $('.input--small').prop('checked', false);
          totalResultsCount = $('#content-hub__component_id').data('result-list-size');
          initialResultsLoadSize = $('#content-hub__component_id').data('initial-results-to-load');
          numberOfResultsLoaded = 0;
          multiGroupFilterIndex = 0;
          singleFilterFlag = false;
          resultsDisplay(0, initialResultsLoadSize, defaultIconPath);
          if (totalResultsCount > initialResultsLoadSize) {
            $('#contentHubLoadMore').removeClass('hide');
          }
          return false;
        }
        currentFocus = -1;
        divCreate = document.createElement('DIV');
        divCreate.setAttribute('id', this.id + 'autocomplete-list');
        divCreate.setAttribute('class', 'autocomplete-items');
        this.parentNode.appendChild(divCreate);
        for (i = 0; i < searchArray.length; i++) {
          if (searchArray[i].substr(0, val.length).toUpperCase() === val.toUpperCase()) {
            searchReslt = document.createElement('DIV');
            searchReslt.innerHTML = '<strong>' + searchArray[i].substr(0, val.length) + '</strong>';
            searchReslt.innerHTML += searchArray[i].substr(val.length);
            searchReslt.innerHTML += '<input type=\'hidden\' value=\'' + searchArray[i] + '\'>';
            searchReslt.addEventListener('click', function (e) {
              searchInput.value = this.getElementsByTagName('input')[0].value;
              /* eslint-disable  no-unused-vars*/
              var filterSelected = false;
              /* eslint-enable  no-unused-vars*/
              $('#myInputautocomplete-list').click(function () {
                var searchText = searchInput.value;
                $('.input--small').each(function () {
                  $(this).val() === searchText ? $(this).prop('checked', true) : $(this).prop('checked', false);
                });
                $('.content-hub-results').empty();
                filterSelected = false;
                resultListWithFilterSelection = [];
                $('.input--small').each(function () {
                  if ($(this).prop('checked') === true) {
                    loadMoreCount = 1;
                    filterSelected = true;
                    var checkedFilterValue = $(this).attr('data-tagId');
                    var resultList = $('#content-hub__component_id').data('result-list-json').slice(0, totalResultsCount);
                    $.each(resultList, function (key, value) {
                      var cqTagIds = resultList[key].cqTagsWithPath;
                      if ($.inArray(checkedFilterValue, cqTagIds) !== -1) {
                        if (!cardDisplayed.includes(resultList[key].linkPath)) {
                          cardDisplayed[cardDisplayedIndex] = resultList[key].linkPath;
                          var item = {};
                          item.linkImageAltText = resultList[key].linkImageAltText;
                          item.eyebrowLink = resultList[key].eyebrowLink;
                          item.linkEyebrow = resultList[key].linkEyebrow;
                          item.linkPath = resultList[key].linkPath;
                          item.linkTitle = resultList[key].linkTitle;
                          item.linkImage = resultList[key].linkImage;
                          item.productImageBean = resultList[key].productImageBean;
                          item.publicationDateDisplay = resultList[key].publicationDateDisplay;
                          resultListWithFilterSelection.push(item);
                          cardDisplayedIndex++;
                          singleFilterFlag = true;
                        }
                      }
                      if (cardDisplayedIndex < initialResultsLoadSize) // Hide Load more if card count is less than initial load size
                        {
                          $('#contentHubLoadMore').addClass('hide');
                        }
                      resultsDisplayWithFilter(resultListWithFilterSelection, 0, initialResultsLoadSize, defaultIconPath);
                      if (cardDisplayedIndex === 0) {
                        $('#contentHubLoadMore').addClass('hide');
                      }
                    });
                  }
                });
              });
              closeAllLists();
            });
            divCreate.appendChild(searchReslt);
          }
        }
      });
      searchInput.addEventListener('keydown', function (e) {
        var x = document.getElementById(this.id + 'autocomplete-list');
        if (x) {
          x = x.getElementsByTagName('div');
        }
        if (e.keyCode === 40) {
          currentFocus++;
          addActive(x);
        } else if (e.keyCode === 38) {
          currentFocus--;
          addActive(x);
        } else if (e.keyCode === 13) {
          e.preventDefault();
          if (currentFocus > -1) {
            if (x) {
              x[currentFocus].click();
            }
          }
        }
      });
      function addActive(x) {
        if (!x) {
          return false;
        }
        removeActive(x);
        if (currentFocus >= x.length) {
          currentFocus = 0;
        }
        if (currentFocus < 0) {
          currentFocus = x.length - 1;
        }
        x[currentFocus].classList.add('autocomplete-active');
      }
      function removeActive(x) {
        for (var i = 0; i < x.length; i++) {
          x[i].classList.remove('autocomplete-active');
        }
      }
      function closeAllLists(elmnt) {
        var x = document.getElementsByClassName('autocomplete-items');
        for (var i = 0; i < x.length; i++) {
          if (elmnt !== x[i] && elmnt !== searchInput) {
            x[i].parentNode.removeChild(x[i]);
          }
        }
      }
      document.addEventListener('click', function (e) {
        closeAllLists(e.target);
      });
    };
    var facetFilters = [];
    var checkboxes = document.querySelectorAll('input[type=checkbox]');
    // eslint-disable-next-line no-eq-null, eqeqeq
    if (checkboxes.length != 0) {
      for (var i = 0; i < checkboxes.length; i++) {
        facetFilters.push(checkboxes[i].value);
      }
    }
    searchDisply(document.getElementById('myInput'), facetFilters);
  }
});

var resultsDisplay = function resultsDisplay(startingNumber, lotSize, defaultIconPath) {
  if ($('#content-hub__component_id').data('result-list-size') > 0) {
    var resultList = $('#content-hub__component_id').data('result-list-json').slice(startingNumber, startingNumber + lotSize);
    $.each(resultList, function (key, value) {
      var imageSource = resultList[key].linkImage ? resultList[key].linkImage : defaultIconPath;
      $('.content-hub-results').append(cardHtml(imageSource, resultList[key].productImageBean, resultList[key].linkImageAltText, resultList[key].eyebrowLink, resultList[key].linkEyebrow, resultList[key].linkPath, resultList[key].linkTitle, resultList[key].publicationDateDisplay));
    });
  }
};

var resultsDisplayWithFilter = function resultsDisplayWithFilter(resultListWithFilterSelection, startingNumber, displayCount, defaultIconPath) {
  if (resultListWithFilterSelection.length > 0) {

    $('.content-hub-results').empty();

    if (resultListWithFilterSelection.length > displayCount) {
      $('#contentHubLoadMore').removeClass('hide');
    }
    if (resultListWithFilterSelection.length <= displayCount) {
      $('#contentHubLoadMore').addClass('hide');
    }
    var resultList = resultListWithFilterSelection.slice(startingNumber, displayCount);
    $.each(resultList, function (key, value) {
      var imageSource = resultList[key].linkImage ? resultList[key].linkImage : defaultIconPath;
      $('.content-hub-results').append(cardHtml(imageSource, resultList[key].productImageBean, resultList[key].linkImageAltText, resultList[key].eyebrowLink, resultList[key].linkEyebrow, resultList[key].linkPath, resultList[key].linkTitle, resultList[key].publicationDateDisplay));
    });
  }
};

var flagCheck = function flagCheck(cqTagIds, groupNames, multiGroupFilterIndex) {
  var tempFlag = false;
  var tempArray = [];
  var groupNameArray = [];
  var tempArrayIndex = 0;

  for (var count = 0; count < groupNames.length; count++) {
    var filterTag = groupNames[count].split('$');
    for (var innerCount = 0; innerCount < cqTagIds.length; innerCount++) {
      if (cqTagIds[innerCount] === filterTag[1]) {
        if (!groupNameArray.includes(filterTag[0])) {
          tempArray[tempArrayIndex] = true;
          groupNameArray[tempArrayIndex] = filterTag[0];
          tempArrayIndex++;
        }
      }
    }
  }

  if (multiGroupFilterIndex === tempArray.length) {
    tempFlag = true;
  } else {
    tempFlag = false;
  }

  return tempFlag;
};

var cardHtml = function cardHtml(imageSource, productImageBean, linkImageAltText, eyebrowLink, linkEyebrow, linkPath, linkTitle, publicationDateDisplay) {
  var htmlString = '';
  var linkCheck = 'html';
  var imageRenditionString = '<div class="rendition-bg rendition-bg--alignment desktop-center-center mobile-center-center"\n\tdata-src="' + productImageBean.mobileTransformedUrl + '"\n\tdata-mobile-rendition="' + productImageBean.mobileTransformedUrl + '"\n\tdata-tablet-rendition="' + productImageBean.tabletTransformedUrl + '"\n\tdata-desktop-rendition="' + productImageBean.desktopTransformedUrl + '" style=" background-image: url(' + productImageBean.desktopTransformedUrl + ')"></div>';
  var publicationDateDisplayHTML = '';
  if (publicationDateDisplay) {
    publicationDateDisplayHTML = '<div class="featured-article-card__date b-body-copy-small"><em>' + publicationDateDisplay + '</em></div>\n';
  }
  if (eyebrowLink.indexOf(linkCheck) > 0) {
    htmlString = '<div class="content-hub__card-list-item col-sm-12 col-md-3 col-xs-12">\n          <div class="content-hub__card-list__card">\n           <div class="content-hub__card-list__card--image">\n           ' + imageRenditionString + '\n            </div> \n            <div class="content-hub__card-list__card--content"> \n             <a href=" ' + eyebrowLink + '">\n             <p class="content-hub__card-list__card--content__eyebrow b-eyebrow-small"> ' + linkEyebrow + ' </p>\n             </a> \n             <p class="content-hub__card-list__card--content__heading b-heading-h5">\n             <a href=" ' + linkPath + '"  _target = "blank"> ' + linkTitle + ' </a>\n             </p> \n\t\t\t ' + publicationDateDisplayHTML + '\n             </div>\n             </div>\n             </div>';
  } else {
    htmlString = '<div class="content-hub__card-list-item col-sm-12 col-md-3 col-xs-12">\n                  <div class="content-hub__card-list__card"> \n                   <div class="content-hub__card-list__card--image"> \n                   ' + imageRenditionString + '\n                   </div>\n                   <div class="content-hub__card-list__card--content">\n                   <p class="content-hub__card-list__card--content__eyebrowTitle b-eyebrow-small"> ' + linkEyebrow + ' </p>\n                   <p class="content-hub__card-list__card--content__heading b-heading-h5">\n                   <a href=" ' + linkPath + '" _target = "blank"> ' + linkTitle + '</a>\n                   </p> \n\t\t\t\t   ' + publicationDateDisplayHTML + '\n                    </div>\n                    </div> \n                    </div>';
  }
  return htmlString;
};