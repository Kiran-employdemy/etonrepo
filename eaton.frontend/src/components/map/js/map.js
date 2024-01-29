(function () {
  $('.map-primary__download').hide();
  $('.loader--inline').hide();
  let App = window.App || {};
  App.Bullseye = App.Bullseye || {};
  let resourcePath = $('.mapContainer').attr('data-resource-path');
  let activeFilters = [];
  let totalResults = 0;
  let resultsLoaded = 0;
  let map;
  /* eslint-disable no-unused-vars, no-undef*/
  let marker;
  /* eslint-disable no-unused-vars, no-undef*/
  let infoWindow;
  let pageSize = $('.mapContainer').data('page-size');
  let labelCategory = $('.mapContainer').data('label-category');
  if (labelCategory) {
    labelCategory = labelCategory.toString();
  }

  let filterList = $('#bullseye-map__filters').attr('data-filter-list');
  let defaultRadius = $('#bullseye-map__filters').attr('data-default-radius');
  let defaultRadiusUnit = $('#bullseye-map__filters').attr('data-default-radius-unit');
  let filterListJSON;
  let intialLayout = $('.initial-layout');
  let primaryLayout = $('.primary-layout');
  let websiteText = $('.mapContainer').data('website-text');
  let emailText = $('.mapContainer').data('email-text');
  let directionText = $('.mapContainer').data('direction-text');
  let enableExpander = $('.mapContainer').data('enable-expander');
  let enableLocationType = $('.mapContainer').data('enable-locationtype');
  let hideWebsite = $('.mapContainer').data('hide-website');
  let hideDirection = $('.mapContainer').data('hide-direction');
  let hideEmail = $('.mapContainer').data('hide-email');
  let mappingVendor = $('.mapContainer').data('mapping-vendor');
  let mappingApiKey = $('.mapContainer').data('mapping-apikey');
  let prefilters = $('.mapContainer').data('prefilters');
  let categoryGroupsAccordionText = $('.mapContainer').data('product-services-text');
  let searchTypeOverride = $('.mapContainer').data('search-type');

  let defaultLatitude;
  let defaultLongitude;
  let primaryMapInitialized = false;
  document.addEventListener('DOMContentLoaded', function() {
    intialLayout.hide();
    primaryLayout.hide();

    if (filterList) {
      filterListJSON = JSON.parse(filterList);
    }
    if ($('.mapContainer').length > 0) {
      let filtersComponent = new App.Filters(document.getElementById('bullseye-map__filters'));
      let activeFiltersComponent = new App.ActiveFilters(document.getElementById('bullseye-map-active__filters'));

      filtersComponent.addEventListener('filterRemoved', e => {
        if (e.detail) {
          if (e.detail.value !== 'distance') {
            let distanceFlag = false;
            let distanceUnitFlag = false;
            if ($('#distance').siblings('button').hasClass('collapsed')) {
              distanceFlag = true;
            }
            if ($('#distanceUnits').siblings('button').hasClass('collapsed')) {
              distanceUnitFlag = true;
            }
            activeFilters.removeValue('name', e.detail.name);
            activeFiltersComponent.activeFilterCount = activeFilters.length;
            activeFiltersComponent.activeFilters = activeFilters;
            this.filters = new App.Filters(e.target);
            this.filters.filterList = filterListJSON;
            distanceCollapseCheck(distanceFlag, distanceUnitFlag);
            App.Bullseye.getBullsEyeResponse(resourcePath + '.json');
          }
        }
      });

      filtersComponent.addEventListener('filterSelected', e => {
        if (e.detail) {
          let selectedId = e.detail.selectedId;
          let filter = e.detail.activeFilters[selectedId];
          let filterTitle = filter.title;
          let filterId = filter.id;
          let distanceFlag = false;
          let distanceUnitFlag = false;
          if ($('#distance').siblings('button').hasClass('collapsed')) {
            distanceFlag = true;
          }
          if ($('#distanceUnits').siblings('button').hasClass('collapsed')) {
            distanceUnitFlag = true;
          }
          let activeFilterDetails = {
            name: filterId,
            title: filterTitle,
            id: filterId
          };
          let removeFilterIds = e.detail.removeFilterIds;
          Object.keys(removeFilterIds).map(function (key) {
            activeFilters.removeValue('name', removeFilterIds[key]);
          });
          if (!filter.value) {
            activeFilters.push(activeFilterDetails);
            activeFiltersComponent.activeFilterCount = activeFilters.length;
            activeFiltersComponent.activeFilters = activeFilters;
          }
          this.filters = new App.Filters(e.target);
          this.filters.filterList = filterListJSON;
          distanceCollapseCheck(distanceFlag, distanceUnitFlag);
          App.Bullseye.getBullsEyeResponse(resourcePath + '.json');
        }
      });

      filtersComponent.addEventListener('clearSelection', e => {
        if (e.detail) {
          let distanceFlag = false;
          let distanceUnitFlag = false;
          if ($('#distance').siblings('button').hasClass('collapsed')) {
            distanceFlag = true;
          }
          if ($('#distanceUnits').siblings('button').hasClass('collapsed')) {
            distanceUnitFlag = true;
          }
          let filter = e.detail.component;
          let removeFilterIds = filter.activeFilterValues.map(function (filterValue) {
            activeFilters.removeValue('name', filterValue.name);
            filtersComponent.deactiveFilter(filterValue.name);
            return filterValue.id;
          });
          let newActiveFilters = this.filters.selectedFilters;
          // remove all active filter values from the given filter and updated selectedFilter list
          Object.keys(this.filters.selectedFilters).map(function (key) {
            return removeFilterIds.indexOf(newActiveFilters[key].id) > -1 && delete newActiveFilters[key];
          });
          activeFiltersComponent.activeFilterCount = activeFilters.length;
          activeFiltersComponent.activeFilters = activeFilters;
          this.filters = new App.Filters(e.target);
          this.filters.selectedFilters = newActiveFilters;
          this.filters.filterList = filterListJSON;
          distanceCollapseCheck(distanceFlag, distanceUnitFlag);
          App.Bullseye.getBullsEyeResponse(resourcePath + '.json');
        }
      });
      activeFiltersComponent.addEventListener('filterRemoved', (e) => {
        if (e.detail) {
          let name = e.detail.name;
          activeFilters.removeValue('name', name);
          activeFiltersComponent.activeFilterCount = activeFilters.length;
          activeFiltersComponent.activeFilters = activeFilters;
          filtersComponent.deactiveFilter(name);
        }
      });

      App.Bullseye.clearAllFilters = function clearAllFilters() {
        let distanceFlag = false;
        let distanceUnitFlag = false;
        if ($('#distance').siblings('button').hasClass('collapsed')) {
          distanceFlag = true;
        }
        if ($('#distanceUnits').siblings('button').hasClass('collapsed')) {
          distanceUnitFlag = true;
        }
        activeFiltersComponent.activeFilterCount = 0;
        activeFiltersComponent.activeFilters = [];
        Object.keys(activeFilters).map(function (key) {
          filtersComponent.deactiveFilter(activeFilters[0].name);
        });
        activeFilters = [];
        activeFiltersComponent.activeFilterCount = activeFilters.length;
        activeFiltersComponent.activeFilters = activeFilters;
        filtersComponent.filterList = filterListJSON;
        distanceCollapseCheck(distanceFlag, distanceUnitFlag);
        App.Bullseye.getBullsEyeResponse(resourcePath + '.json');
      };

      $('.open-facets-map-mobile').click(function() {
        filtersComponent.open();
      });
    }

    $('.mapLoadMore').click(function() {
      if (resultsLoaded < totalResults) {
        let resultToLoad = resultsLoaded + pageSize;
        App.Bullseye.getBullsEyeResponse(resourcePath + '.json', resultToLoad);
      }
    });

    $('.whereToBuy').click(function() {
      App.Bullseye.downloadBullsEyeResult();
    });

    $('.bullseye__clear-all-filters').click(function() {
      App.Bullseye.clearAllFilters();
    });

    $('#distance').removeClass('in');
    $('#distance').siblings('button').addClass('collapsed');
    $('#distanceUnits').removeClass('in');
    $('#distanceUnits').siblings('button').addClass('collapsed');
    $('#distance').find('.faceted-navigation-header__action-link').hide();
    $('#distanceUnits').find('.faceted-navigation-header__action-link').hide();

  });

  let locateMe = $('.eaton-search--map__locator__wrapper');
  locateMe.click(function() {
    navigator.geolocation.getCurrentPosition(function(position) {
      $('#location-search-box').val('');
      getLocation(position);
      $('.map-primary__location__code-error').addClass('hide');
    }, function(error) {
      console.log('error' + error);
      $('.map-primary__location__code-error').removeClass('hide');
    }, {maximumAge: 60000, timeout: 5000, enableHighAccuracy: false});
  });

  $('.map-search').click(function() {
    let keyword = $('#location-search-box').val();
    let locationForm = $('#location-search-form')[0];
    if (!keyword && locationForm) {
      locationForm.reportValidity();
    } else {
      App.Bullseye.getBullsEyeResponse(resourcePath + '.json');
    }
  });

  $(function() {

    if (window.location.search.includes('isBuyNowMap')) {

      try {
        let buyNowMapData = JSON.parse(localStorage.getItem('buyNowMapData'));
        if (!buyNowMapData) {
          throw 'Unable to find buy now data';
        } else {
          $('.mapContainer').data('current-latitude', buyNowMapData.latitude);
          $('.mapContainer').data('current-longitude', buyNowMapData.longitude);
          $('#location-search-box').val(buyNowMapData.keywords);
          localStorage.removeItem('buyNowMapData');
          App.Bullseye.getBullsEyeResponse(resourcePath + '.json');
        }
      } catch (err) {
        console.error(err);
      }
    }

  });

  function getLocation(position) {
    if (position) {
      let latitude = position.coords.latitude;
      let longitude = position.coords.longitude;
      $('.mapContainer').data('current-latitude', latitude);
      $('.mapContainer').data('current-longitude', longitude);
      if (resourcePath && (latitude && longitude)) {
        App.Bullseye.getBullsEyeResponse(resourcePath + '.json');
      }
    }
  }

  function distanceCollapseCheck(distanceFlag,distanceUnitFlag) {
    if (distanceFlag) {
      $('#distance').removeClass('in');
      $('#distance').siblings('button').addClass('collapsed');
    }
    if (distanceUnitFlag) {
      $('#distanceUnits').removeClass('in');
      $('#distanceUnits').siblings('button').addClass('collapsed');
    }
  }

  App.Bullseye.downloadBullsEyeResult = function() {
    $('.loader--inline').show();
    let currentLatitude = $('.mapContainer').attr('data-current-latitude');
    let currentLongitude = $('.mapContainer').attr('data-current-longitude');
    let categoryIds = [];
    Object.keys(activeFilters).map(function (key) {
      categoryIds.push(activeFilters[key].id);
    });
    let categories = categoryIds.join(',');
    let Keyword = $('#location-search-box').val();
    let radius = $('#distance').find('input:radio:checked').val();
    let distanceUnit = $('#distanceUnits').find('input:radio:checked').val();
    let downloadAllURL = `${ resourcePath + '.csv' }?Keyword=${ Keyword ? Keyword : '' }&PageSize=${ pageSize ? totalResults : '' }&CategoryIDs=${ categories ? categories : '' }&distanceUnit=${ distanceUnit ? distanceUnit : '' }&Radius=${ radius ? radius : '' }&Latitude=${ currentLatitude ? currentLatitude : '' }&Longitude=${ currentLongitude ? currentLongitude : '' }`;
    $.get(downloadAllURL,function(response) {
      let blob = new Blob([response]);
      if (window.navigator.msSaveOrOpenBlob) {
        // code for IE.
        window.navigator.msSaveBlob(blob, 'wheretobuy.csv');
      } else {
        // code for chromium browsers.
        let aTag = window.document.createElement('a');
        aTag.href = window.URL.createObjectURL(blob, {type: 'text/plain'});
        aTag.download = 'wheretobuy.csv';
        document.body.appendChild(aTag);
        aTag.click();
      }
      $('.loader--inline').hide();
    });
  };

  let loader = $('.loader');
  App.Bullseye.getBullsEyeResponse = function(url, pageSize) {
    if (!primaryMapInitialized) {
      primaryMapInitialized = true;

      if (mappingVendor === 'google') {
        map = new google.maps.Map(document.getElementById('mapPrimary'), {
          center: {lat: defaultLatitude, lng: defaultLongitude},
          zoom: 4,
          mapTypeId: 'terrain'
        });
      } else if (mappingVendor === 'mapbox') {
        mapboxgl.accessToken = mappingApiKey;
        map = new mapboxgl.Map({
          container: 'mapPrimary',
          style: 'mapbox://styles/mapbox/streets-v11',
          center: [defaultLongitude, defaultLatitude],
          zoom: 4
        });
      }

      $('.initial-layout').hide();
      $('.primary-layout').show();
      applyFilters();
    }

    loader.addClass('loader-active');
    if (activeFilters.length > 0) {
      $('.bullseye__clear-all-filters').show();
    } else {
      $('.bullseye__clear-all-filters').hide();
    }
    $('#distance').find('.faceted-navigation-header__action-link').hide();
    $('#distanceUnits').find('.faceted-navigation-header__action-link').hide();
    let categoryIds = [];
    let radius = $('#distance').find('input:radio:checked').val();
    let distanceUnit = $('#distanceUnits').find('input:radio:checked').val();
    let latitude = $('.mapContainer').data('current-latitude');
    let longitude = $('.mapContainer').data('current-longitude');
    let keywords = $('#location-search-box').val();
    Object.keys(activeFilters).map(function (key) {
      categoryIds.push(activeFilters[key].id);
    });
    let size;
    if (pageSize) {
      size = pageSize;
    }

    let data = {
      Latitude: latitude,
      Longitude: longitude,
      PageSize: size,
      Keyword: keywords,
      CategoryIDs: categoryIds.join(','),
      Radius: radius,
      distanceUnit: distanceUnit,
      SearchTypeOverride: searchTypeOverride
    };
    $.ajax({
      type: 'GET',
      url: url,
      data: data,
      headers: { 'Content-Type': 'application/json' },
      success: function success(resultData) {
        App.Bullseye.constructMapDetail(resultData, data.Latitude, data.Longitude, data.radius);
      }
    });
  };

  let applyFilters = () => {
    // first apply any prefilters from Bullseye Config
    let fields;
    let filters = [];
    if (typeof (prefilters) === 'string') {
      fields = prefilters.split(',');
      for (let i = 0; i < fields.length; i++) {
        if (fields[i].includes('categoryId')) {
          filters.push(fields[i].split(':')[1].replace(/"/g,''));
        }
      }
    }
    else if (typeof (prefilters) === 'object') {
      fields = prefilters;
      filters.push(fields.categoryId);
    }
    for (let i = 0; i < filters.length; i++) {
      $("input[type=checkbox][value='" + filters[i] + "']").click();
    }

    // apply filters in the URL
    let vars = {};
    let loc;
    const productLines = document.querySelectorAll('input[type=checkbox][name=Product-lines]');
    const partnerType = document.querySelectorAll('input[type=checkbox][name=Partner-type]');
    const productGrouping = document.querySelectorAll('input[type=checkbox][name=Product-grouping]');
    const productLinesNoSpace = [];
    const partnerTypeNoSpace = [];
    const productGroupingNoSpace = [];

    for (let i = 0; i < productLines.length; i++) {
      productLinesNoSpace[i] = productLines[i].getAttribute('data-title').replace(/ /g, '').toLowerCase();
    }
    for (let i = 0; i < partnerType.length; i++) {
      partnerTypeNoSpace[i] = partnerType[i].getAttribute('data-title').replace(/ /g, '').toLowerCase();
    }
    for (let i = 0; i < productGrouping.length; i++) {
      productGroupingNoSpace[i] = productGrouping[i].getAttribute('data-title').replace(/ /g, '').toLowerCase();
    }
    let parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
      key = key.toLowerCase();
      value = decodeURI(value).toLowerCase();
      let multiVal = value.split('||');
      multiVal.forEach((val) => {
        switch (key) {
            case 'productlines':
              loc = productLinesNoSpace.indexOf(val);
              if (loc !== -1) {
                $("input[type=checkbox][data-title='" + productLines[loc].getAttribute('data-title') + "']").click();
              }
              break;
            case 'partnertype':
              loc = partnerTypeNoSpace.indexOf(val);
              if (loc !== -1) {
                $("input[type=checkbox][data-title='" + partnerType[loc].getAttribute('data-title') + "']").click();
              }
              break;
            case 'productgrouping':
              loc = productGroupingNoSpace.indexOf(val);
              if (loc !== -1) {
                $("input[type=checkbox][data-title='" + productGrouping[loc].getAttribute('data-title') + "']").click();
              }
              break;
            case 'keyword' :
              fillKeyword(val);
              break;
            case 'city' :
              fillKeyword(val);
              break;
            case 'state':
              fillKeyword(val);
              break;
            case 'province':
              fillKeyword(val);
              break;
            case 'zip':
              fillKeyword(val);
              break;
            default:
              vars[key] = val.toLowerCase();
        }
      });
    });
    return vars;
  };

  let fillKeyword = value => {
    document.getElementById('location-search-box').value = value;
  };

  let markers = [];
  let previousCardActive;
  let previousInfoActive;

  App.Bullseye.constructMapDetail = function (resultData, latitude, longitude) {
    if (resultData) {
      $('.map-primary__location__code-error').addClass('hide');
      $('.bullseye-map-active__filters .faceted-navigation__action-link--clear-filters').click(function() {
        App.Bullseye.clearAllFilters();
      });
      deleteMarkers();
      $('.card-list').empty();
      let resultList = resultData.ResultList;
      $('.mapContainer').attr('data-current-latitude', resultData.Latitude);
      $('.mapContainer').attr('data-current-longitude', resultData.Longitude);
      let kmText = $('.mapContainer').attr('data-km-text');
      let miText = $('.mapContainer').attr('data-miles-text');
      if (resultList) {
        if (resultList.length > 0) {
          $('.map-primary__download').show();
        } else {
          $('.map-primary__download').hide();
        }
        resultsLoaded = resultData.ResultList.length;
        let searchTerm = resultData.searchTerm;
        let radius = resultData.radius;
        totalResults = resultData.TotalResults;
        let distanceUnit = resultData.distanceUnit;
        distanceUnit = (distanceUnit === 'kilometers') ? kmText : miText;
        if (resultsLoaded >= totalResults) {
          $('.mapLoadMore').hide();
        } else {
          $('.mapLoadMore').show();
        }
        let coma = '';
        if (searchTerm) {
          $('#location-search-box').val(searchTerm);
          coma = ', ';
          searchTerm = '"' + searchTerm + '"';
        }
        let mapResultText = $('.map_result').data('map-results');
        mapResultText = `${ totalResults || totalResults === 0 ? totalResults : '' } ${ mapResultText ? mapResultText : '' } <span class='faceted-navigation-header__sub-copy'>${ radius } ${ distanceUnit }${ coma } ${ searchTerm ? searchTerm : '' }</span>`;
        $('.map_result').html(mapResultText);
        let count = 0;
        resultList.forEach(function (item) {
          let distributorLatitude = item.Latitude;
          let distributorLongitude = item.Longitude;
          /* eslint-disable no-unused-vars, no-undef*/
          let geocoder = new google.maps.Geocoder();
          geocoder.geocode({ address: searchTerm }, function (results, status) {
            if (status === 'OK') {
              map.setCenter(results[0].geometry.location);
            }
          });
          /* eslint-disable no-unused-vars, no-undef*/
          map.setZoom(9);
          let location = {lat: distributorLatitude, lng: distributorLongitude};
          let distributorName = item.Name;
          let address1 = item.Address1;
          let address2 = item.Address2 ? item.Address2 : '';
          let phoneNumber = item.PhoneNumber;
          let mobileNumber = item.MobileNumber;
          let emailAddress = item.EmailAddress;
          let distance = item.Distance;
          let locationTypeName = item.LocationTypeName;
          let categoryIds = item.CategoryIds;
          let city = item.City ? item.City + ',' : '';
          let state = item.State ? item.State : '';
          let postCode = item.PostCode ? item.PostCode : '';
          let contactName = item.ContactName ? item.ContactName + ',' : '';
          let contactPosition = item.ContactPosition ? item.ContactPosition : '';
          let addressCityStatePC = `${ city } ${ state } ${ postCode }`;
          let websiteUrl = item.URL;
          let directionURL = `https://www.google.com/maps/dir/${ latitude },${ longitude }/${ distributorLatitude },${ distributorLongitude }`;
          let labelIndex = item.CategoryIds.split(',').indexOf(labelCategory);
          let label = undefined;
          if (labelIndex >= 0) {
            label = item.CategoryNames.split(',')[labelIndex];
          }
          let notesTitle = 'Notes';
          let notesText = '';

          if (!item.Attributes.length) {
            notesText = '';
          } else {
            for (let attr in item.Attributes) {
              if (item.Attributes[attr].AttributeName === notesTitle) {
                notesText = item.Attributes[attr].AttributeValue.trim();
                break;
              }
            }
          }

          let contentString = getInfoCard(distributorName, address1, address2, addressCityStatePC, locationTypeName, phoneNumber, mobileNumber, contactName, contactPosition, websiteUrl, emailAddress, directionURL, 'card${count}', label, notesText, notesTitle);
          let categoryGroupsArray = item.categoryGroups;
          let categoryGroupsContentArray = [];
          if (categoryGroupsArray) {
            for (let key in categoryGroupsArray) {
              let categoryArray = categoryGroupsArray[key];
              let contentString = `<p class=map-card__accordion__group__title>${ key }</p>
                  <div class='map-card__accordion__group'>
                     ${ categoryArray.map(service => `<p class=map-card__accordion__group__copy>${ service }</p>`).join(' ') }
                  </div>
                `;
              categoryGroupsContentArray.push(contentString);
            }
          }
          let mapCard = `<div class=map-card id=card${ count }>
                            <div class=map-card__content>
                              <div class=map-card-header>
                                <div class=map-card-header__wrapper>
                                  <h3 class=map-card-header__title>${ distributorName }</h3>
                                  <p class=map-card-header__distance>${ distance } ${ distanceUnit }</p>
                                </div>
                                ${ label ? `<p class=map-card-header__vpma>${ label }</p>` : '' }
                              </div>

                              <div class=map-card-body>
                                  ${ enableLocationType ? `<p class='map-card-body__copy map-card-body__copy--class'>${ locationTypeName }</p>` : '' }
                                  <p class='map-card-body__copy map-card-body__copy--address'>
                                    ${ address1 ? address1 : '' } <br>
                                    ${ address2 ? address2 : '' } <br>

                                    ${ addressCityStatePC ? addressCityStatePC : '' } <br>
                                    ${ contactName ? contactName : '' }   ${ contactPosition ? contactPosition : '' }<br>
                                    ${ phoneNumber ? phoneNumber : '' } <br>
                                    ${ mobileNumber ? mobileNumber : '' }
                                  </p>
                                  ${ notesText ? `<p class='map-card-body__copy map-card-body__copy--notes'><br><b>${ notesTitle }:</b><br>${ notesText }</p>` : '' }
                              </div>

                            ${ enableExpander ?
                               '<div class=map-card-contact map-card-expander>' : '<div class=map-card-contact>' }
                                 ${ websiteUrl && !hideWebsite ? `<a href=${ websiteUrl } class="map-card-contact__copy map-card-contact__copy--website" target=_blank><p>${ websiteText }</p></a>` : '' }
                                 ${ emailAddress && !hideEmail ? `<a href=mailto:${ emailAddress } class="map-card-contact__copy map-card-contact__copy--email" target=_blank><p>${ emailText }</p></a>` : '' }
                                 ${ directionURL && !hideDirection ? `<a href=${ directionURL } class="map-card-contact__copy map-card-contact__copy--directions" target=_blank><p>${ directionText }</p></a>` : '' }
                              </div>
                            </div>

                           ${ enableExpander ?
                             `${ categoryGroupsArray ? `<div class='map-card__accordion' data-target='#card-accordion-panel${ count }' data-toggle='collapse'>
                               <p class='map-card__accordion__title'>${ categoryGroupsAccordionText }</p>
                               <i class="map-card__accordion__icon icon icon-chevron-down"></i>
                             </div>` : '' }
                             <div class='map-card__accordion__panel--wrapper collapse in' id='card-accordion-panel${ count }'>` :
                             ''
                           }

                           ${ enableExpander ?
                              `<div class='map-card__accordion__panel'>
                                ${ categoryGroupsContentArray.join(' ') }
                              </div>` : '' }

                            </div>
                        </div>`; // end map-card

          $('.card-list').append(mapCard);

          if (mappingVendor === 'google') {
            /* eslint-disable no-unused-vars, no-undef*/
            let infowindow = new google.maps.InfoWindow({
              content: contentString
            });

            /* eslint-disable no-unused-vars, no-undef*/
            let marker = new google.maps.Marker({
              position: location,
              /* eslint-disable no-unused-vars, no-undef*/
              map: map,
              icon: item.icon,
              title: distributorName,
              infowindow: infowindow
            });
            marker.customInfo = 'card' + count;
            markers.push(marker);
            marker.addListener('click', function () {
              markers.forEach(function(marker) {
                marker.infowindow.close(map, marker);
              });
              $('.map-card').removeClass('map-card-select');
              $('#' + this.customInfo).addClass('map-card-select');
              previousCardActive = $('#' + this.customInfo);
              infowindow.open(map, marker);
              let clonedDiv = $('#' + this.customInfo).clone();
              $('#' + this.customInfo).remove();
              clonedDiv.prependTo('.card-list').fadeIn();
            });
          } else if (mappingVendor === 'mapbox') {
            let popup = new mapboxgl.Popup().setText(contentString);
            let marker = new mapboxgl.Marker()
              .setLngLat(location)
              .setPopup(popup)
              .addTo(map);
            markers.push(marker);
          }
          count++;
        });
      }
    }
    loader.removeClass('loader-active');
  };

  function getInfoCard(distributorName, address1, address2, addressCityStatePC, locationTypeName, phoneNumber, mobileNumber, contactName, contactPosition, websiteUrl, emailAddress, directionURL, id, label, notesText, notesTitle) {
    let contentString = `
              <div class=map-card-info data-card-id=${ id }>
                <div class=map-card-info__content>
                   <div class=map-card-info-header>
                    <h1 class=map-card-info-header__title>${ distributorName }</h1>
                    ${ label ? `<p class=map-card-info-header__vpma>${ label }</p>` : '' }
                   </div>

                   <div class=map-card-body>
                       <p class='map-card-body__copy map-card-body__copy--class'>${ locationTypeName }</p>
                       <p class='map-card-body__copy map-card-body__copy--address'>
                          ${ address1 ? address1 : '' } <br>
                          ${ address2 ? address2 : '' } <br>
                          ${ addressCityStatePC ? addressCityStatePC : '' }<br>
                          ${ contactName ? contactName : '' }   ${ contactPosition ? contactPosition : '' }<br>
                       </p>
                        <p class='map-card-body__copy map-card-body__copy--phonenumber'>
                          ${ phoneNumber ? phoneNumber : '' } <br>
                          ${ mobileNumber ? mobileNumber : '' }
                        </p>
                        ${ notesText ? `<p class='map-card-body__copy map-card-body__copy--notes'><br><b>${ notesTitle }:</b><br>${ notesText }</p>` : '' }
                   </div>

                   <div class=map-card-contact>
                       ${ websiteUrl && !hideWebsite ? `<a href='${ websiteUrl }' class='map-card-contact__copy map-card-contact__copy--website' target='_blank'><p>${ websiteText }</p></a>` : '' }
                       ${ emailAddress && !hideEmail ? `<a href='mailto:${ emailAddress }' class='map-card-contact__copy map-card-contact__copy--email' target='_blank'><p>${ emailText }</p></a>` : '' }
                       ${ directionURL && !hideDirection ? `<a href='${ directionURL }' class='map-card-contact__copy map-card-contact__copy--directions' target='_blank'><p>${ directionText }</p></a>` : '' }
                   </div>

                </div>
              </div>
              `;
    return contentString;
  }

  function deleteMarkers() {
    for (let i = 0; i < markers.length; i++) {
      if (mappingVendor === 'google') {
        markers[i].setMap(null);
      } else if (mappingVendor === 'mapbox') {
        markers[i].remove();
      }
    }
    markers = [];
  }

  if (mappingVendor === 'google') {
    let places = new google.maps.places.Autocomplete(document.getElementById('location-search-box'));
    // eslint-disable-next-line no-eq-null, eqeqeq
    if (places != null) {
      google.maps.event.addListener(places, 'place_changed', function () {
        let place = places.getPlace();
        console.log(place);
        let city = '';
        let state = '';
        if (place.address_components) {
          for (let i = 0;i < place.address_components.length;i++) {

            let component = place.address_components[i];

            const componentType = component.types[0];

            switch (componentType) {


                case 'locality': {
                  city = `${ component.long_name } ${ city }`;
                  break;
                }

                case 'administrative_area_level_1':
                  state += component.long_name;
                  break;
            }

          }

          let searchItem = city + ',' + state;
          let keyword = $('#location-search-box').val(searchItem);
          let locationForm = $('#location-search-form')[0];

          if (!keyword && locationForm) {
            locationForm.reportValidity();
          } else {
            App.Bullseye.getBullsEyeResponse(resourcePath + '.json');
          }
        }
      });
    }
  }
  App.Bullseye.initMap = function() {
    let urlVars = applyFilters();
    let urlLat = urlVars.latitude;
    let urlLon = urlVars.longitude;
    let urlRad = urlVars.radius;

    if (urlRad) {
      defaultRadius = urlRad;
    }
    if (urlLat) {
      defaultLatitude = urlLat;
    } else {
      defaultLatitude = document.getElementById('bullseye-map__filters').getAttribute('data-default-latitude');
    }
    if (urlLon) {
      defaultLongitude = urlLon;
    } else {
      defaultLongitude = document.getElementById('bullseye-map__filters').getAttribute('data-default-longitude');
    }
    if (defaultLatitude && defaultLongitude) {
      defaultLatitude = parseFloat(defaultLatitude);
      defaultLongitude = parseFloat(defaultLongitude);
    }

    if (defaultRadius) {
      $('input[type=radio][value=' + defaultRadius + ']').prop('checked',true);
    }
    if (defaultRadiusUnit) {
      $('input[type=radio][value=' + defaultRadiusUnit + ']').prop('checked',true);
    }
    navigator.geolocation.getCurrentPosition(function(position) {
      $('.initial-layout').hide();
      $('.primary-layout').show();
      if (mappingVendor === 'google') {
        map = new google.maps.Map(document.getElementById('mapPrimary'), {
          center: {lat: defaultLatitude, lng: defaultLongitude},
          zoom: 4,
          mapTypeId: 'terrain'
        });
      } else if (mappingVendor === 'mapbox') {
        mapboxgl.accessToken = mappingApiKey;
        map = new mapboxgl.Map({
          container: 'mapPrimary',
          style: 'mapbox://styles/mapbox/streets-v11',
          center: [defaultLongitude, defaultLatitude],
          zoom: 4
        });
      }
      getLocation(position);
      primaryMapInitialized = true;

      $(window).resize(function() {
        if ($(window).width() >= '768') {
          $('.eaton-search--map__locator--mobile').hide();
          $('.eaton-search--map__locator').show();
        } else {
          $('.eaton-search--map__locator').hide();
          $('.eaton-search--map__locator--mobile').show();
        }
      });
    },
    function(errorCode) {
      console.warn('location error');
      $('.initial-layout').show();
      $('.primary-layout').hide();
      if (mappingVendor === 'google') {
        map = new google.maps.Map(document.getElementById('mapLanding'), {
          center: {lat: defaultLatitude, lng: defaultLongitude},
          zoom: 4,
          mapTypeId: 'terrain'
        });
      } else if (mappingVendor === 'mapbox') {
        mapboxgl.accessToken = mappingApiKey;
        map = new mapboxgl.Map({
          container: 'mapPrimary',
          style: 'mapbox://styles/mapbox/streets-v11',
          center: [defaultLongitude, defaultLatitude],
          zoom: 4
        });
      }
    },{maximumAge: 60000, timeout: 5000, enableHighAccuracy: false});
  };
  if ($('.mapContainer').length > 0) {
    Array.prototype.removeValue = function(name, value) {
      if (value) {
        let removableValue = value.toString();
        let array = $.map(this, function(v,i) {
          return v[name] === removableValue ? null : v;
        });
        this.length = 0;
        this.push.apply(this, array);
      }
    };
  }

  if (mappingVendor === 'mapbox') {
    App.Bullseye.initMap();
  }

})();
