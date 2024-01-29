module.exports = {
  advancedSearchFromDashboardListAuthenticated: () => {
    return advancedSearchFromDashboard('list', true, true);
  },
  advancedSearchFromDashboardGridAuthenticated: () => {
    return advancedSearchFromDashboard('grid', true, true);
  },
  advancedSearchFromDashboardListNotAuthenticated: () => {
    return advancedSearchFromDashboard('list', false, true);
  },
  advancedSearchFromDashboardGridNotAuthenticated: () => {
    return advancedSearchFromDashboard('grid', false, true);
  },
  advancedSearchWithoutGridListViewDefaultGrid: () => {
    return advancedSearchFromDashboard('grid', false, false);
  },
  advancedSearchWithoutGridListViewDefaultList: () => {
    return advancedSearchFromDashboard('list', false, false);
  },
  noExtraOptionSelectedGrid: (authenticated) => {
    return advancedSearchNoExtraOptionSelected('grid', authenticated);
  },
  noExtraOptionSelectedList: (authenticated) => {
    return advancedSearchNoExtraOptionSelected('list', authenticated);
  }
};

const advancedSearchNoExtraOptionSelected = (gridlistView, authenticated) => {
  return `
    <div class="eaton-advanced-search" id="advance__search-result-doc" data-authenticated="${ authenticated }"
         data-resource="/content/eaton/us/en-us/secure/dashboard/jcr:content/root/responsivegrid/advanced_search"
         data-date-label="Date" data-size-label="Sizes" data-language-label="Language(s)"
         data-bulkdownload-tooltip-text="Select to enable bulk downloads" data-default-sort-option="relevance"
         data-attribute="3" data-attribute-view-type="${ gridlistView }" data-download-text="Download">
        <div class="eaton-title eaton-title--default eaton-title--inset doc-title"></div>
        <div>
            <div class="eaton-search eaton-search--default">
                <div class="container">
                    <div class="row">
                        <div id="site-search" class="col-xs-12 eaton-search__form eaton-search--default__form"
                             autocomplete="off" target="">
                            <div class="form-group">
                                <label for="site-search-box" class="sr-only"></label>
                                <textarea rows="1" name="search-term" id="adv-site-search-box"
                                          class="form-control eaton-search--default__form-input eaton-search-textarea"
                                          placeholder="Search here" required="required"
                                          style="overflow: hidden; overflow-wrap: break-word; height: 51px;"></textarea>
                                <div class="eaton-search--default__form-mobile-label u-visible-mobile hidden-xs hidden-sm">
                                    <span> Search here</span>
                                </div>
                                <button type="submit"
                                        class="button--reset eaton-search__submit eaton-search--default__form-submit">
                                    <span class="sr-only"></span> <i class="icon icon-search adv-icon-search"
                                                                     aria-hidden="true"></i>
                                </button>
                            </div>
                            <div class="eaton-search--default__results">
                                <ul class="eaton-search--default__result-list">
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="eaton-search--advanced-results" id="advance__search">
            <div class="loader loader-active adv-search__loader">
                <div class="loader__loader-circle"></div>
            </div>
            <div class="container">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="faceted-navigation-header">
                            <div class="faceted-navigation-header__header-top faceted-navigation-header__mobile-view"
                                 data-sortable="true">
                                <div class="faceted-navigation-header__results-count">
                                    <span class="result-count" id="final-result"></span>
                                    Results
                                </div>
                                <a rel="nofollow" target="_self" role="button" data-redirect-link=""
                                   class="filterNone faceted-navigation-header__action-link faceted-navigation-header__action-link--clear-filters faceted-navigation-header__action-link--clear-filters--desktop">
                                    Clear Filters
                                </a>
                                <div class="faceted-navigation-header__title doc-sort-title-mobile-view doc-sort">
                                    ProductGrid.sortBy
                                </div>
                                <div class="faceted-navigation-header__sort-options">
                                    <div class="dropdown">
                                        <button id="dSortFacets" class="" type="button" data-toggle="dropdown"
                                                aria-haspopup="true" aria-expanded="false">
                                            <div class="faceted-navigation-header__default">
                                                <span class="faceted-navigation-header__default--option"
                                                      data-default-sorting-title="Newest to Oldest"
                                                      data-default-fallback="ProductGrid.sortBy"><span
                                                        class="faceted-navigation-header__sort-link"> Newest to Oldest</span></span>
                                                <i class="icon icon-chevron-down" aria-hidden="true"></i>
                                            </div>
                                        </button>
                                        <ul class="dropdown-menu " aria-labelledby="dSortFacets">
                                            <li>
                                                <a class="faceted-navigation-header__sort-link select-sorting"
                                                   rel="nofollow" data-value="pub_date_desc"
                                                   aria-label="Go To Newest to Oldest" id="Newest to Oldest">
                                                    Newest to Oldest
                                                </a>
                                            </li>
                                            <li>
                                                <a class="faceted-navigation-header__sort-link select-sorting  faceted-navigation-header__sort-options--selected"
                                                   rel="nofollow" data-value="relevance" aria-label="Go To Relevance"
                                                   id="Relevance">
                                                    Relevance
                                                </a>
                                            </li>
                                            <li>
                                                <a class="faceted-navigation-header__sort-link select-sorting"
                                                   rel="nofollow" data-value="asc" aria-label="Go To A-Z" id="A-Z">
                                                    A-Z
                                                </a>
                                            </li>
                                            <li>
                                                <a class="faceted-navigation-header__sort-link select-sorting"
                                                   rel="nofollow" data-value="desc" aria-label="Go To Z-A" id="Z-A">
                                                    Z-A
                                                </a>
                                            </li>
                                        </ul>
                                    </div>
                                    <select class="native-select">
                                        <option value="pub_date_desc">Newest to Oldest</option>
                                        <option value="relevance">Relevance</option>
                                        <option value="asc">A-Z</option>
                                        <option value="desc">Z-A</option>
                                    </select>
                                </div>
                            </div>
                            <div class="faceted-navigation-header__container">
                                <button id="secure-filter" data-toggle-modal-facet=""
                                        class="open-facets-mobile b-button b-button__primary b-button__primary--light"
                                        role="button">Filters
                                </button>
                            </div>
                            <div id="maxlimit-exceed" class=" submittal-builder__modal overlay hidden"
                                 data-title="Download">
                                <div class=" submittal-builder__modal__inner-content">
                                    <h3 class="submittal-builder__modal__header">Download</h3>
                                    <button aria-label="Close" class="button--reset submittal-builder__modal__close">
                                        <span class="sr-only">Close Download</span>
                                        <i id="maxlimit-close" class="icon icon-close" aria-hidden="true"></i>
                                    </button>
                                    <div class="eaton-form submittal-builder__download__form ">
                                        <p class="submittal-builder__download__preferred-option">You have exceeded the
                                            download limit</p>
                                    </div>
                                </div>
                            </div>
                            <div class="submittal-builder__floating-button__container">
                                <button data-analytics-name="document-search-floating-buttons-download" disabled=""
                                        class="button--reset submittal-builder__floating-button submittal-builder__download-button floating-document-download"
                                        style="display: none;">
                                    <i class="icon icon-download submittal-builder__floating-button__icon"
                                       aria-hidden="true"></i>
                                </button>
                                <button data-analytics-name="document-search-view-package" disabled=""
                                        class="button--reset submittal-builder__floating-button submittal-builder__preview-button floating-document-count"
                                        style="display: none;">
                                    <span class="submittal-builder__preview-button__document-count ">0</span>
                                    <i class="icon icon-folder submittal-builder__floating-button__icon"
                                       aria-hidden="true"></i>
                                </button>
                                <button class="back-to-top button--reset" data-scroll-to="body">
                                    <span class="sr-only">Back to top of the page</span>
                                    <i class="icon icon-chevron-up" aria-hidden="true"></i>
                                </button>
                            </div>
                            <div id="track-exceed" class="submittal-builder__modal overlay hide" data-title="Download">
                                <div class=" submittal-builder__modal__inner-content">
                                    <h3 class="submittal-builder__modal__header">Download tracking capabilities</h3>
                                    <div class="eaton-form submittal-builder__download__form ">
                                        <p class="submittal-builder__download__preferred-option">Tracking capabilities</p>
                                    </div>
                                    <div class="eaton-form track-download">
                                        <div class="cta-default ">
                                            <a class="b-button b-button__primary b-button__primary--primary-branding-color-reversed track-download okay-track"
                                               role="button">Okay</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="faceted-navigation-header__active-filter" id="selectedFilter"></div>
                <div class="row">
                    <div class="col-xs-12 col-md-3">
                        <div id="search-results__filters" data-hide-from-active="[&quot;contentType&quot;]"
                             data-title="Filters.Title" data-mobile-dialog-title="Filters.Title"
                             data-close-text="Filters.Close" data-clear-all-filters-text="Filters.ClearAllFilters"
                             data-clear-filters-text="Filters.ClearFilters"
                             data-clear-selection-text="Filters.ClearSelection" data-view-more-text="Filters.ViewMore"
                             data-view-less-text="Filters.ViewLess" data-results-plural-text="Search.Results"
                             data-results-singular-text="Search.Result"
                             data-facet-search-label="GlobalFacetSearch.SearchLabel"
                             data-facet-search-placeholder="GlobalFacetSearch.Placeholder"
                             data-no-suggestions-text="GlobalFacetSearch.NoSugestions" data-in-text="GlobalFacetSearch.In"
                             data-of-text="Global.Of" data-facet-group-search-label="GroupFacetSearch.Label"
                             data-facet-group-search-placeholder="GroupFacetSearch.Placeholder"
                             data-facet-group-search-no-suggestions-text="GroupFacetSearch.NoSuggestionsText">
                            <div class="eaton-form faceted-navigation faceted-navigation__filters">
                                <h3 class="faceted-navigation__title">Filters.Title</h3>
                                <div class="mobile-header">
                                    <span>Filters.Title</span>
                                    <button data-toggle-modal-facet="" aria-label="Close"
                                            class="close-facets-mobile button--reset">
                                        <span class="sr-only">Close Filters</span>
                                        <i class="icon icon-close icon-close-filter" aria-hidden="true"></i>
                                    </button>
                                </div>
                                <hr class="faceted-navigation__hr eaton-advanced-search-mreset">
                                <div class="cross-reference__active-filters bullseye-map-active__filters faceted-navigation__active-filters__container faceted-navigation__active-filters__container--mobile  eaton-advanced-search-mreset"
                                     data-active-filter-values="%5B%5D" data-result-count="undefined"
                                     data-results-text="Result" data-of-text="of"
                                     data-clear-all-filters-text="Clear all filters" data-clear-filters-text="Clear filters"
                                     data-clear-selection-text="Clear selection" data-ref="0.9407220479717402">
                                    <div class="hidden">
                                        <div class="hidden"></div>
                                        <a href="#" rel="nofollow"
                                           class="faceted-navigation__action-link faceted-navigation__action-link--clear-filters faceted-navigation__action-link--clear-filters--mobile hidden">
                                            Clear Filters
                                        </a>
                                    </div>
                                </div>
                                <div class="faceted-navigation__filter-container "
                                     data-clear-selection-text="Clear selection" data-view-more-text="View more"
                                     data-view-less-text="View less"
                                     data-facet-group-search-label="Search within these results"
                                     data-facet-group-search-placeholder="Enter a value"
                                     data-facet-group-search-no-suggestions-text="No results based on search terms"
                                     data-ref="0.6021941790490317">
                                    <div class="faceted-navigation__facet-group  ">
                                        <div>
                                            <div class="faceted-navigation__list__search__container hidden">
                                                <label class="faceted-navigation__list__search__label">GlobalFacetSearch.SearchLabel</label>
                                                <input type="text" class="faceted-navigation__list__search__input "
                                                       placeholder="GlobalFacetSearch.Placeholder">
                                                <div class="faceted-navigation__list__search__suggestions faceted-navigation__list__search__no-suggestions__container hidden">
                                                    <div class="faceted-navigation__list__search__suggestion faceted-navigation__list__search__suggestion__no-suggestions__message">
                                                        GroupFacetSearch.NoSuggestionsText
                                                        <span class="faceted-navigation__list__search__suggestion__suggested-term"></span>
                                                    </div>
                                                </div>
                                            </div>
                                            <a href="#" rel="nofollow" role="button"
                                               class="faceted-navigation-header__action-link faceted-navigation-header__action-link--clear-filters faceted-navigation-header__action-link--clear-filters--sidebar hidden">
                                                Filters.ClearSelection
                                            </a>
                                            <div class="advanced-search__filter" id="filter-list">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <script id="mustache-advanced-search-filter" type="text/mustache">
                        {{#facetGroupList}}
                            <div class="faceted-navigation__filter-container">
                                <div class="faceted-navigation__facet-group {{gridFacet}}">
                                    <button class="faceted-navigation__header button--reset" data-toggle="collapse" data-target="#{{facetGroupId}}" aria-expanded="true" aria-controls="{{facetGroupId}}">
                                        {{facetGroupLabel}}
                                            <i class="icon icon-sign-minus" aria-hidden="true"></i>
                                            <i class="icon icon-sign-plus faceted-navigation__icon-sign-plus" aria-hidden="true"></i>
                                    </button>
                                    <div id="{{facetGroupId}}" class="collapse in">
                                        <label class="faceted-navigation__list__search__label {{facetSearchEnabled}}">{{searchLabel}}</label>
                                        <input type="text" data-searchbox-for="{{facetGroupId}}" class="auto-search faceted-navigation__list__search__input {{facetSearchEnabled}}" placeholder="{{searchPlaceHolder}}">
                                        <div class="auto-suggest-div">
                                            <div class="ul-auto-suggest" id="custom-{{facetGroupId}}">
                                                {{#facetValueList}}
                                                <div class="li-div hide" data-filter-title="{{facetValueLabel}}">
                                                    <div class="classCustomList faceted-navigation__list-item ">
                                                            <div class="faceted-navigation__facet-value">
                                                            <label class="global-filter-search__suggestion">
                                                                <a rel="nofollow" role="link" class="has-url" id={{facetValueId}}>
                                                                <input data-analytics-event="submittal-builder-facet" data-analytics-name="checkbox : {{facetValueId}}" data-analytics-state="off" data-single-facet-enabled="true" type="{{singleFacetEnabled}}" class="input filter-selected input--small hide" value="{{facetValueId}}" name="{{facetGroupId}}" data-title="{{facetValueLabel}}" data-id="{{facetValueId}}" id="facet-{{facetValueId}}" {{facetSelected}}>
                                                                <span class="inner"><bdi>{{facetValueLabel}}</bdi></span>
                                                                </a>
                                                            </label>
                                                            </div>
                                                        </div>
                                                </div>
                                            {{/facetValueList}}
                                            </div>
                                        </div>
                                        <ul class="faceted-navigation__list faceted-navigation__list--unfiltered" id="facet-{{facetGroupId}}">
                                        {{#facetValueList}}
                                            <li class="faceted-navigation__list-item ">
                                                    <div class="faceted-navigation__facet-value">
                                                      <label class="faceted-navigation__facet-value-label">
                                                        <a rel="nofollow" role="link" class="has-url actual-filter-item {{facetSelected}}" id={{facetValueId}}>
                                                          <input data-analytics-event="submittal-builder-facet" data-analytics-name="checkbox : {{facetValueId}}" data-analytics-state="off" data-single-facet-enabled="true" type="{{singleFacetEnabled}}" class="input filter-selected input--small" value="{{facetValueId}}" name="{{facetGroupId}}" data-title="{{facetValueLabel}}" data-id="{{facetValueId}}" id="facet-{{facetValueId}}" {{facetSelected}}>
                                                          <span class="inner"><bdi>{{facetValueLabel}}</bdi></span>
                                                        </a>
                                                      </label>
                                                    </div>
                                            </li>
                                        {{/facetValueList}}
                                        <div class="button-view-more-less">
                                            <button class="button--reset faceted-navigation__view-more-values">View more <span class="faceted-navigation__view-more-values__count"><bdi>{{}}</bdi></span></button>
                                             <button class="button--reset faceted-navigation__view-less-values">View less</button>
                                        </div>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                            {{/facetGroupList}}
                            <hr class="faceted-navigation__hr">
                        </script>
                    </div>
                    <div class="col-xs-12 col-md-9">
                        <div class="advanced-search__results u-clearfix"></div>
                        <div class="advanced-search__result--list table-responsive" id="result-list"></div>
                        <div class="adv-results-container">
                            <div class="results-list__bottom">
                                <div class="results-list__actions text-center">
                                    <button class="b-button b-button__primary b-button__primary--light load__more"
                                            role="button" data-load-more="">Load More
                                    </button>
                                </div>
                            </div>
                            <script id="mustache-advanced-search-result" type="text/mustache">
                                {{#siteSearchResults}}
                                    <div class="advanced-search__result">
                                     {{#downloadEnabled}}
                                        <input type="checkbox" title="{{i18n.tooltiptext}}" class="advanced-search__select advanced-search_track" name="toDownload" data-size="{{fileSize}}" id="{{id}}" data-url="{{url}}"  track-download="{{trackDownload}}" asset-type="{{fileType}}" />
                                     {{/downloadEnabled}}
                                        <div class="advanced-search__image">
                                            <img src="{{image}}" />
                                        </div>
                                        <div class="advanced-search__title">{{#secure}}<i class="icon icon-secure-lock" aria-hidden="true"></i>{{/secure}}<a target="_blank" data-track-download="{{trackDownload}}" href="{{url}}" filename="{{fileName}}">{{title}}</a></div>

                                    </div>
                                {{/siteSearchResults}}
                            </script>
                            <script id="mustache-list-result-partial" type="text/mustache">
                                {{#siteSearchResults}}
                                    <tr class="list-view">
                                        <td class="advanced-search__result--list--checkbox">
                                         {{#downloadEnabled}}
                                             <input type="checkbox" class="advanced-search_track" name="toDownload" data-size="{{fileSize}}" id="{{id}}" data-url="{{url}}" track-download="{{trackDownload}}" asset-type="{{fileType}}"></td>
                                         {{/downloadEnabled}}
                                        <td class="advanced-search__result--list--name">
                                         {{#secure}}
                                            <i class="icon icon-secure-lock" aria-hidden="true"></i>
                                         {{/secure}}
                                            <span class="doc-name"><a target="_blank" data-track-download="{{trackDownload}}" href="{{url}}" filename="{{fileName}}">{{title}}</a></span>
                                        </td>
                                        <td>{{publishDate}}</td>
                                        <td>{{fileTypeAndSize}}</td>
                                        <td>{{Language}}</td>
                                    </tr>
                                {{/siteSearchResults}}
                            </script>
                            <script id="mustache-advanced-search-list-result" type="text/mustache">
                                <div class="table-responsive">
                                    <table class="table advance__search-list-view">
                                        <thead>
                                            <tr>
                                              <th></th>
                                              <th></th>
                                              <th>{{i18n.date}}</th>
                                              <th>{{i18n.size}}</th>
                                              <th>{{i18n.language}}</th>
                                            </tr>
                                      </thead>
                                      <tbody>
                                        {{> listItems}}
                                      </tbody>
                                    </table>
                                </div>
                            </script>
                        </div>
                    </div>
                    <div class="search-results__no-result hide">
                        <div class="eaton-title eaton-title--default eaton-title--inset">
                            <div class="no-results-container">
                                <h1 class="eaton-title__headline">0 Search Results</h1>
                            </div>
                        </div>
                        <div class="eaton-text-default">
                            <div class="no-results-container">
                                <div class="eaton-text-default__content rich-text-container"><p class="no-results-after-text-search">Your search for "<span id="noresult"></span>" found no results.<br><br>
                                </p></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
  `;
};

const advancedSearchFromDashboard = (viewType, authenticated, gridListView) => {
  return `
    <div class="bde-activated">
      <div id="assets-bulkdownload-email-div">
         <div class="bulk-download-fixed-drawer" style="display: none;">
            <div class="fixed-drawer container">
              <div class="fixed-drawer__header">
                <div class="fixed-drawer__header-row bg-color__green">
                  <span class="fixed-drawer__header-row__title min__close">
                    <i class="icon fixed-drawer__down icon-chevron-down"></i>
                    bdeDownloadDocument (<span class="fixed-drawer__count">0</span>) bdeof
                    20
                  </span>
                </div>
                <div class="fixed-drawer__header-clear"><a class="clear-selection">bdeClearSelection</a></div>
              </div>
              <div class="main__box u-flex-wrap viewmore" id="selected-files-drawer-box" data-view-limit="5" data-viewless="viewLessText" data-download-file-count-limit="20" data-viewmore="viewMoreText">
              </div>
              <div class="fixed-drawer__footer text-right">
                <a data-analytics-event="request quote" class="call-fd-model fixed-drawer__footer__buttons-div standard-reverse-style" title="bdeSendEmail" target="_blank" id="bulkemail">
                  <img src="/content/dam/eaton/resources/icons/email.png" class="new-email-icon" alt="email"> bdeSendEmail
                </a>
                <a data-analytics-event="request quote" class="call-fd-model fixed-drawer__footer__buttons-div standard-style" title="bdeDownloadSelection" target="_blank" id="idbulkdownload">
                  <i class="icon icon-download"></i> bdeDownloadSelection
                </a>
              </div>
            </div>
            <div class="modal fd-model fade-in" id="bulkemail-model" role="dialog">
              <div class="modal-dialog modal-center">
                <!-- Modal content-->
                <div class="modal-content">
                  <div class="modal-header">
                    <button type="button" class="close fd-close-popup" data-dismiss="modal">×</button>
                  </div>
                  <div class="modal-body">
                    <h4 class="modal-heading">bdeSendEmail</h4>
                    <input type="text" id="bulkDownload-email-to" class="auto-search faceted-navigation__list__search__input show" placeholder="Enter email address" data-valid-error="bdeInvalidEmail" data-email-error="bdeEmailNotSent" data-email-send="bdeEmailSent">
                      <div class="email-valid-msg-div"></div>
                    <a data-analytics-event="request quote" class="fixed-drawer__footer__buttons-div standard-reverse-style" title="bdeSendEmail" target="_blank" id="bulkDownload-email-send-btn">
                      <img src="/content/dam/eaton/resources/icons/email.png" class="new-email-icon" alt="email"> bdeSend
                    </a>
                  </div>
                  <div class="modal-footer">
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="eaton-advanced-search" id="advance__search-result-doc" data-bulkdownload="true"
             data-authenticated="${ authenticated ? 'true' : 'false' }"
             data-resource="/content/eaton/us/en-us/secure/dashboard/jcr:content/root/responsivegrid/advanced_search"
             data-date-label="Date" data-size-label="Size" data-language-label="Language(s)"
             data-bulkdownload-tooltip-text="Select to enable bulk downloads" data-default-sort-option="relevance"
             data-attribute="3" data-attribute-view-type="${ viewType }" data-download-text="Download">
            <div class="eaton-title eaton-title--default eaton-title--inset doc-title">
                <div class="eaton-title eaton-title--default eaton-title--inset ">
                    <div class="container">
                        <h1 class="eaton-title__headline">Advanced document search</h1>
                    </div>
                </div>
            </div>
            <div>
                <div class="eaton-search eaton-search--default">
                    <div class="container">
                        <div class="row">
                            <div id="site-search" class="col-xs-12 eaton-search__form eaton-search--default__form"
                                 autocomplete="off" target="">
                                <div class="form-group">
                                    <label for="site-search-box" class="sr-only"></label>
                                    <textarea rows="1" name="search-term" id="adv-site-search-box"
                                              class="form-control eaton-search--default__form-input eaton-search-textarea"
                                              placeholder="Search and filter for public and secure documents"
                                              required="required"
                                              style="overflow: hidden; overflow-wrap: break-word; height: 51px;"></textarea>
                                    <div class="eaton-search--default__form-mobile-label u-visible-mobile hidden-xs hidden-sm">
                                        <span> Search and filter for public and secure documents</span>
                                    </div>
                                    <button type="submit"
                                            class="button--reset eaton-search__submit eaton-search--default__form-submit">
                                        <span class="sr-only"></span> <i class="icon icon-search adv-icon-search"
                                                                         aria-hidden="true"></i>
                                    </button>
                                </div>
                                <div class="eaton-search--default__results">
                                    <ul class="eaton-search--default__result-list">
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="eaton-search--advanced-results" id="advance__search">
                <div class="loader adv-search__loader">
                    <div class="loader__loader-circle"></div>
                </div>
                <div class="container">
                    <div class="row">
                        <div class="col-xs-12">
                            <div class="faceted-navigation-header">
                                <div class="faceted-navigation-header__header-top faceted-navigation-header__mobile-view"
                                     data-sortable="true">
                                    <div class="faceted-navigation-header__results-count">
                                        <span class="result-count" id="final-result">3942</span>
                                        Results
                                    </div>
                                    <a rel="nofollow" target="_self" role="button" data-redirect-link=""
                                       class="filterNone faceted-navigation-header__action-link faceted-navigation-header__action-link--clear-filters faceted-navigation-header__action-link--clear-filters--desktop">
                                        Clear filters
                                    </a>
                                    ${ gridListView ? `
                                    <div class="result-view" id="btnContainer">
                                        <button class="btn ${ viewType === 'grid' ? 'active' : '' }" id="gridbtn"><img
                                                src="/etc.clientlibs/eaton/settings/wcm/designs/clientlib/clientlib-all/resources/images/grid-view-icon-blue.png">
                                            <p>Grid View</p>
                                        </button>
                                        <button class="btn ${ viewType === 'list' ? 'active' : '' }" id="listbtn"><img
                                                src="/etc.clientlibs/eaton/settings/wcm/designs/clientlib/clientlib-all/resources/images/list-view-icon-grey.png">
                                            <p>List View</p>
                                        </button>
                                    </div>
                                    ` : '' }
                                    <div class="faceted-navigation-header__title doc-sort-title-mobile-view doc-sort">Sort
                                        By
                                    </div>
                                    <div class="faceted-navigation-header__sort-options">
                                        <div class="dropdown">
                                            <button id="dSortFacets" class="" type="button" data-toggle="dropdown"
                                                    aria-haspopup="true" aria-expanded="false">
                                                <div class="faceted-navigation-header__default">
                                                <span class="faceted-navigation-header__default--option"
                                                      data-default-sorting-title="Relevance"
                                                      data-default-fallback="Sort By"><span
                                                        class="faceted-navigation-header__sort-link"> Relevance</span></span>
                                                    <i class="icon icon-chevron-down" aria-hidden="true"></i>
                                                </div>
                                            </button>
                                            <ul class="dropdown-menu " aria-labelledby="dSortFacets">
                                                <li>
                                                    <a class="faceted-navigation-header__sort-link select-sorting"
                                                       rel="nofollow" data-value="pub_date_desc"
                                                       aria-label="Go to Newest to Oldest" id="Newest to Oldest">
                                                        Newest to Oldest
                                                    </a>
                                                </li>
                                                <li>
                                                    <a class="faceted-navigation-header__sort-link select-sorting faceted-navigation-header__sort-options--selected"
                                                       rel="nofollow" data-value="relevance" aria-label="Go to Relevance"
                                                       id="Relevance">
                                                        Relevance
                                                    </a>
                                                </li>
                                                <li>
                                                    <a class="faceted-navigation-header__sort-link select-sorting"
                                                       rel="nofollow" data-value="asc" aria-label="Go to A-Z" id="A-Z">
                                                        A-Z
                                                    </a>
                                                </li>
                                                <li>
                                                    <a class="faceted-navigation-header__sort-link select-sorting"
                                                       rel="nofollow" data-value="desc" aria-label="Go to Z-A" id="Z-A">
                                                        Z-A
                                                    </a>
                                                </li>
                                            </ul>
                                        </div>
                                        <select class="native-select">
                                            <option value="pub_date_desc">Newest to Oldest</option>
                                            <option value="relevance">Relevance</option>
                                            <option value="asc">A-Z</option>
                                            <option value="desc">Z-A</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="faceted-navigation-header__container">
                                    <button id="secure-filter" data-toggle-modal-facet=""
                                            class="open-facets-mobile b-button b-button__primary b-button__primary--light"
                                            role="button">Filters
                                    </button>
                                </div>
                                <div id="maxlimit-exceed" class=" submittal-builder__modal overlay hidden"
                                     data-title="Download">
                                    <div class=" submittal-builder__modal__inner-content">
                                        <h3 class="submittal-builder__modal__header">Download</h3>
                                        <button aria-label="Close" class="button--reset submittal-builder__modal__close">
                                            <span class="sr-only">Close Download</span>
                                            <i id="maxlimit-close" class="icon icon-close" aria-hidden="true"></i>
                                        </button>
                                        <div class="eaton-form submittal-builder__download__form ">
                                            <p class="submittal-builder__download__preferred-option">You have exceeded the
                                                download limit</p>
                                        </div>
                                    </div>
                                </div>
                                <div class="submittal-builder__floating-button__container">
                                    <button data-analytics-name="document-search-floating-buttons-download" disabled="true"
                                            class="button--reset submittal-builder__floating-button submittal-builder__download-button floating-document-download"
                                            style="display: none;">
                                        <i class="icon icon-download submittal-builder__floating-button__icon"
                                           aria-hidden="true"></i>
                                    </button>
                                    <button data-analytics-name="document-search-view-package" disabled="true"
                                            class="button--reset submittal-builder__floating-button submittal-builder__preview-button floating-document-count"
                                            style="display: none;">
                                        <span class="submittal-builder__preview-button__document-count ">(0)</span>
                                        <i class="icon icon-folder submittal-builder__floating-button__icon"
                                           aria-hidden="true"></i>
                                    </button>
                                    <button class="back-to-top button--reset" data-scroll-to="body">
                                        <span class="sr-only">Back to top of the page</span>
                                        <i class="icon icon-chevron-up" aria-hidden="true"></i>
                                    </button>
                                </div>
                                <div id="track-exceed" class="submittal-builder__modal overlay hide" data-title="Download">
                                    <div class=" submittal-builder__modal__inner-content">
                                        <h3 class="submittal-builder__modal__header">Download tracking capabilities</h3>
                                        <div class="eaton-form submittal-builder__download__form ">
                                            <p class="submittal-builder__download__preferred-option">Tracking
                                                capabilities</p>
                                        </div>
                                        <div class="eaton-form track-download">
                                            <div class="cta-default ">
                                                <a class="b-button b-button__primary b-button__primary--primary-branding-color-reversed track-download okay-track"
                                                   role="button">Okay</a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="bulk-download adv-bulk-download" id="bulkDownload" data-cache-duration="300"
                                 data-zip-size-limit="104857600" data-file-name-prefix="Eaton_Download_">
                                <a rel="nofollow" role="button" id="bulkDownload-link" class="disabledbutton"> <i
                                        class="icon icon-download" aria-hidden="true"></i>Bulk Download
                                    <span id="selecteddownload">(0)</span></a>
                            </div>
                        </div>
                    </div>
                    <div class="faceted-navigation-header__active-filter" id="selectedFilter"></div>
                    <div class="row">
                        <div class="col-xs-12 col-md-3">
                            <div id="search-results__filters" data-hide-from-active="[&quot;contentType&quot;]"
                                 data-title="Filters" data-mobile-dialog-title="Filters" data-close-text="Close"
                                 data-clear-all-filters-text="Clear all filters" data-clear-filters-text="Clear filters"
                                 data-clear-selection-text="Clear selection" data-view-more-text="View more"
                                 data-view-less-text="View less" data-results-plural-text="Results"
                                 data-results-singular-text="Result" data-facet-search-label="Narrow results"
                                 data-facet-search-placeholder="Search values" data-no-suggestions-text="No suggestions"
                                 data-in-text="in" data-of-text="of"
                                 data-facet-group-search-label="Search within these results"
                                 data-facet-group-search-placeholder="Enter a value"
                                 data-facet-group-search-no-suggestions-text="No results based on search terms">
                                <div class="eaton-form faceted-navigation faceted-navigation__filters">
                                    <h3 class="faceted-navigation__title">Filters</h3>
                                    <div class="mobile-header">
                                        <span>Filters</span>
                                        <button data-toggle-modal-facet="" aria-label="Close"
                                                class="close-facets-mobile button--reset">
                                            <span class="sr-only">Close Filters</span>
                                            <i class="icon icon-close icon-close-filter" aria-hidden="true"></i>
                                        </button>
                                    </div>
                                    <hr class="faceted-navigation__hr eaton-advanced-search-mreset">
                                    <div class="cross-reference__active-filters bullseye-map-active__filters faceted-navigation__active-filters__container faceted-navigation__active-filters__container--mobile  eaton-advanced-search-mreset"
                                         data-active-filter-values="%5B%5D" data-result-count="undefined"
                                         data-results-text="Result" data-of-text="of"
                                         data-clear-all-filters-text="Clear all filters"
                                         data-clear-filters-text="Clear filters"
                                         data-clear-selection-text="Clear selection" data-ref="0.9407220479717402">
                                        <div class="hidden">
                                            <div class="hidden"></div>
                                            <a href="#" rel="nofollow"
                                               class="faceted-navigation__action-link faceted-navigation__action-link--clear-filters faceted-navigation__action-link--clear-filters--mobile hidden">
                                                Clear filters
                                            </a>
                                        </div>
                                    </div>
                                    <div class="adv-date-filter-section close-accordion-mobile">
                                        <button class="faceted-navigation__header button--reset" data-toggle="collapse"
                                                data-target="#date-type" aria-expanded="true" aria-controls="content-type">
                                            Date Range
                                            <i class="icon icon-sign-minus" aria-hidden="true"></i>
                                            <i class="icon icon-sign-plus faceted-navigation__icon-sign-plus"
                                               aria-hidden="true"></i>
                                        </button>
                                        <div class="collapse in faceted-navigation__list__date-picker" id="date-type">
                                            <div class="faceted-navigation__facet-value-label">
                                                <a class="has-url">
                                                    <div class="faceted-navigation__list__field">
                                                        <label class="faceted-navigation__list__date col-sm-3">From:</label>
                                                        <input type="text"
                                                               class="faceted-navigation__list__date-input col-sm-8 start-date hasDatepicker"
                                                               placeholder="DD-MM-YYYY" required="" id="dp1685126670055">
                                                        <span class="input-group-addon calender_icon"><i
                                                                class="glyphicon glyphicon-calendar icon_cal"></i></span>
                                                        <div class="date-error" id="date-error">Enter a Valid Date</div>
                                                    </div>
                                                    <div class="faceted-navigation__list__field">
                                                        <label class="faceted-navigation__list__date col-sm-3">To:</label>
                                                        <input placeholder="DD-MM-YYYY" type="text"
                                                               class="faceted-navigation__list__date-input col-sm-8 end-date hasDatepicker"
                                                               id="dp1685126670056">
                                                        <span class="input-group-addon calender_icon"><i
                                                                class="glyphicon glyphicon-calendar icon_cal"></i></span>
                                                    </div>
                                                </a>
                                            </div>
                                            <a rel="nofollow" role="button"
                                               class="faceted-navigation-header__action-link faceted-navigation-header__action-link--apply apply-date"><u>Apply</u></a>
                                            <a rel="nofollow" role="button"
                                               class="faceted-navigation-header__action-link faceted-navigation-header__action-link--apply reset-date"><u>Reset</u></a>
                                        </div>
                                        <hr class="faceted-navigation__hr eaton-advanced-search-mreset">
                                    </div>
                                    <div class="faceted-navigation__filter-container close-accordion-mobile"
                                         data-clear-selection-text="Clear selection" data-view-more-text="View more"
                                         data-view-less-text="View less"
                                         data-facet-group-search-label="Search within these results"
                                         data-facet-group-search-placeholder="Enter a value"
                                         data-facet-group-search-no-suggestions-text="No results based on search terms"
                                         data-ref="0.6021941790490317">
                                        <div class="faceted-navigation__facet-group  ">
                                            <div>
                                                <div class="faceted-navigation__list__search__container hidden">
                                                    <label class="faceted-navigation__list__search__label">Narrow
                                                        results</label>
                                                    <input type="text" class="faceted-navigation__list__search__input "
                                                           placeholder="Search values">
                                                    <div class="faceted-navigation__list__search__suggestions faceted-navigation__list__search__no-suggestions__container hidden">
                                                        <div class="faceted-navigation__list__search__suggestion faceted-navigation__list__search__suggestion__no-suggestions__message">
                                                            No results based on search terms
                                                            <span class="faceted-navigation__list__search__suggestion__suggested-term"></span>
                                                        </div>
                                                    </div>
                                                </div>
                                                <a href="#" rel="nofollow" role="button"
                                                   class="faceted-navigation-header__action-link faceted-navigation-header__action-link--clear-filters faceted-navigation-header__action-link--clear-filters--sidebar hidden">
                                                    Clear selection
                                                </a>
                                                <div class="advanced-search__filter" id="filter-list"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <script id="mustache-advanced-search-filter" type="text/mustache">
                    {{#facetGroupList}}
                        <div class="faceted-navigation__filter-container">
                            <div class="faceted-navigation__facet-group {{gridFacet}}">
                                <button class="faceted-navigation__header button--reset" data-toggle="collapse" data-target="#{{facetGroupId}}" aria-expanded="true" aria-controls="{{facetGroupId}}">
                                    {{facetGroupLabel}}
                                        <i class="icon icon-sign-minus" aria-hidden="true"></i>
                                        <i class="icon icon-sign-plus faceted-navigation__icon-sign-plus" aria-hidden="true"></i>
                                </button>
                                <div id="{{facetGroupId}}" class="collapse in">
                                    <label class="faceted-navigation__list__search__label {{facetSearchEnabled}}">{{searchLabel}}</label>
                                    <input type="text" data-searchbox-for="{{facetGroupId}}" class="auto-search faceted-navigation__list__search__input {{facetSearchEnabled}}" placeholder="{{searchPlaceHolder}}">
                                    <div class="auto-suggest-div">
                                        <div class="ul-auto-suggest" id="custom-{{facetGroupId}}">
                                            {{#facetValueList}}
                                            <div class="li-div hide" data-filter-title="{{facetValueLabel}}">
                                                <div class="classCustomList faceted-navigation__list-item ">
                                                        <div class="faceted-navigation__facet-value">
                                                        <label class="global-filter-search__suggestion">
                                                            <a rel="nofollow" role="link" class="has-url" id={{facetValueId}}>
                                                            <input data-analytics-event="submittal-builder-facet" data-analytics-name="checkbox : {{facetValueId}}" data-analytics-state="off" data-single-facet-enabled="true" type="{{singleFacetEnabled}}" class="input filter-selected input--small hide" value="{{facetValueId}}" name="{{facetGroupId}}" data-title="{{facetValueLabel}}" data-id="{{facetValueId}}" id="facet-{{facetValueId}}" {{facetSelected}}>
                                                            <span class="inner"><bdi>{{facetValueLabel}}</bdi></span>
                                                            </a>
                                                        </label>
                                                        </div>
                                                    </div>
                                            </div>
                                        {{/facetValueList}}
                                        </div>
                                    </div>
                                    <ul class="faceted-navigation__list faceted-navigation__list--unfiltered" id="facet-{{facetGroupId}}">
                                    {{#facetValueList}}
                                        <li class="faceted-navigation__list-item ">
                                                <div class="faceted-navigation__facet-value">
                                                  <label class="faceted-navigation__facet-value-label">
                                                    <a rel="nofollow" role="link" class="has-url actual-filter-item" id={{facetValueId}}>
                                                      <input data-analytics-event="submittal-builder-facet" data-analytics-name="checkbox : {{facetValueId}}" data-analytics-state="off" data-single-facet-enabled="true" type="{{singleFacetEnabled}}" class="input filter-selected input--small" value="{{facetValueId}}" name="{{facetGroupId}}" data-title="{{facetValueLabel}}" data-id="{{facetValueId}}" id="facet-{{facetValueId}}" {{facetSelected}}>
                                                      <span class="inner"><bdi>{{facetValueLabel}}</bdi></span>
                                                    </a>
                                                  </label>
                                                </div>
                                        </li>
                                    {{/facetValueList}}
                                    <div class="button-view-more-less">
                                        <button class="button--reset faceted-navigation__view-more-values">View more <span class="faceted-navigation__view-more-values__count"><bdi>{{}}</bdi></span></button>
                                         <button class="button--reset faceted-navigation__view-less-values">View less</button>
                                    </div>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        {{/facetGroupList}}
                    <hr class="faceted-navigation__hr">

                    </script>
                </div>
                <div class="col-xs-12 col-md-9">
                    <div class="advanced-search__results u-clearfix"></div>
                    <div class="adv-results-container">
                        <div class="results-list__bottom">
                            <div class="results-list__actions text-center">
                                <button class="b-button b-button__primary b-button__primary--light load__more"
                                        role="button" data-load-more="">Load more
                                </button>
                            </div>
                        </div>
                        <script id="mustache-advanced-search-result" type="text/mustache">
                        {{#siteSearchResults}}
                            <div class="advanced-search__result">
                             {{#downloadEnabled}}
                                <input type="checkbox" title="{{i18n.tooltiptext}}" class="advanced-search__select advanced-search_track" name="toDownload" data-size="{{fileSize}}" id="{{id}}" data-url="{{url}}"  track-download="{{trackDownload}}" asset-type="{{fileType}}" />
                             {{/downloadEnabled}}
                                <div class="advanced-search__image">
                                    <img src="{{image}}" />
                                </div>
                                <div class="advanced-search__title">{{#secure}}<i class="icon icon-secure-lock" aria-hidden="true"></i>{{/secure}}<a target="_blank" data-track-download="{{trackDownload}}" href="{{url}}" filename="{{fileName}}">{{title}}</a></div>

                            </div>
                        {{/siteSearchResults}}

                        </script>
                        <script id="mustache-list-result-partial" type="text/mustache">
                        {{#siteSearchResults}}
                            <tr class="list-view">
                                <td class="advanced-search__result--list--checkbox">
                                 {{#downloadEnabled}}
                                     <input type="checkbox" class="advanced-search_track" name="toDownload" data-size="{{fileSize}}" id="{{id}}" data-url="{{url}}" track-download="{{trackDownload}}" asset-type="{{fileType}}"></td>
                                 {{/downloadEnabled}}
                                <td class="advanced-search__result--list--name">
                                 {{#secure}}
                                    <i class="icon icon-secure-lock" aria-hidden="true"></i>
                                 {{/secure}}
                                    <span class="doc-name"><a target="_blank" data-track-download="{{trackDownload}}" href="{{url}}" filename="{{fileName}}">{{title}}</a></span>
                                </td>
                                <td>{{publishDate}}</td>
                                <td>{{fileTypeAndSize}}</td>
                                <td>{{Language}}</td>
                            </tr>
                        {{/siteSearchResults}}

                        </script>
                        <script id="mustache-advanced-search-list-result" type="text/mustache">
                        <div class="table-responsive">
                            <table class="table advance__search-list-view">
                                <thead>
                                    <tr>
                                      <th></th>
                                      <th></th>
                                      <th>{{i18n.date}}</th>
                                      <th>{{i18n.size}}</th>
                                      <th>{{i18n.language}}</th>
                                    </tr>
                              </thead>
                              <tbody>
                                {{> listItems}}
                              </tbody>
                            </table>
                        </div>
                        </script>
                    </div>
                </div>
                <div class="search-results__no-result hide">
                    <div class="eaton-title eaton-title--default eaton-title--inset">
                        <div class="no-results-container">
                            <h1 class="eaton-title__headline">Try another search</h1>
                        </div>
                    </div>
                    <div class="eaton-text-default">
                        <div class="no-results-container">
                            <div class="eaton-text-default__content rich-text-container"><p class="no-results-after-text-search">Your search "<span
                                    id="noresult"></span>" did not match any documents or products.<br>
                                <br>
                            </p></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    `;
};
