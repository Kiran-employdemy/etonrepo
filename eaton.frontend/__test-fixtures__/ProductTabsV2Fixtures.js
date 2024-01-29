module.exports = {
  productTabs: (version) => {
    return `
<div class="product-tabs${ version } aem-GridColumn aem-GridColumn--default--12">
    <div class="eaton-product-tabs">
        <div class="">
            <input id="tab-0" type="radio" name="product-tab" checked="">
            <input id="tab-1" type="radio" name="product-tab">
            <input id="tab-2" type="radio" name="product-tab">
            <div class="container container-tabs sticky-tabs">
                <div class="row eaton-product-tabs__buttons">
                    <div class="col-md-5 eaton-product-tabs__buttons__center">
                        <div class="row">
                            <!-- graphicToggle without <a> -->
                            <label for="tab-1" class="tab-label tab-active">
                                <span>Overview</span>
                            </label>
                            <label for="tab-2" class="tab-label ">
                                <span>Models</span>
                            </label>
                            <label for="tab-3" class="tab-label ">
                                <span>Resources</span>
                            </label>
                            <label for="tab-4" class="tab-label ">
                                <span>Support</span>
                            </label>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12 eaton-product-tabs__tab-panel" data-tab-name="tab-1">
                    <div class="aem-Grid aem-Grid--12 aem-Grid--default--12 ">
                        <div class="product-highlights aem-GridColumn aem-GridColumn--default--12">
                            <div class="product-highlights__component">
                                <div class="container">
                                    <div class="row">
                                        <div class="product-highlights__wrapper">
                                            <div class="col-xs-12 col-md-12 product-highlights__item">
                                                <div class="product-highlights__content">
                                                    <div class="product-highlights__title b-eyebrow-small">Power
                                                        rating
                                                    </div>
                                                    <div class="product-highlights__text">500-1500 VA</div>
                                                </div>
                                            </div>
                                            <div class="col-xs-12 col-md-12 product-highlights__item">
                                                <div class="product-highlights__content">
                                                    <div class="product-highlights__title b-eyebrow-small">Voltage</div>
                                                    <div class="product-highlights__text">120V, 208/230V</div>
                                                </div>
                                            </div>
                                            <div class="col-xs-12 col-md-12 product-highlights__item">
                                                <div class="product-highlights__content">
                                                    <div class="product-highlights__title b-eyebrow-small">Form factor
                                                    </div>
                                                    <div class="product-highlights__text">Tower</div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-12 eaton-product-tabs__tab-panel" data-tab-name="tab-2">
                    <span class="print-tab-heading specification-print-heading">Specifications</span>
                    <div class="aem-Grid aem-Grid--12 aem-Grid--default--12 ">
                        <div class="product-grid aem-GridColumn aem-GridColumn--default--12">
                            <div class="product-grid-results product-grid-results--sku">
                                <div class="container">
                                    <div class="row">
                                        <div class="col-xs-12">
                                            <div class="faceted-navigation-header"
                                                 data-redirect-link="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.html"
                                                 data-active-facet-count="1">
                                                <div class="faceted-navigation-header__header-top faceted-navigation-header__mobile-view"
                                                     data-sortable="true">
                                                    <div class="faceted-navigation-header__results-count">1 Results
                                                    </div>
                                                    <a href="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.html"
                                                       rel="nofollow" target="_self" role="button"
                                                       data-redirect-link="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.html"
                                                       class="faceted-navigation-header__action-link faceted-navigation-header__action-link--clear-filters faceted-navigation-header__action-link--clear-filters--desktop">Clear
                                                        filters
                                                    </a>
                                                    <div class="faceted-navigation-header__title doc-sort-title-mobile-view "
                                                         style="display: none;">Sort By
                                                    </div>
                                                    <div class="faceted-navigation-header__sort-options">
                                                        <div class="dropdown">
                                                            <button id="dSortFacets" class="" type="button"
                                                                    data-toggle="dropdown" aria-haspopup="true"
                                                                    aria-expanded="false">
                                                                <div class="faceted-navigation-header__default">
                                                                    <span class="faceted-navigation-header__default--option"
                                                                          data-default-fallback="Sort By"><span
                                                                            class="faceted-navigation-header__sort-link">Sort By</span></span>
                                                                    <i class="icon icon-chevron-down"
                                                                       aria-hidden="true"></i>
                                                                </div>
                                                            </button>
                                                            <ul class="dropdown-menu "
                                                                aria-labelledby="dSortFacets">
                                                                <li>
                                                                    <a class="faceted-navigation-header__sort-link"
                                                                       rel="nofollow"
                                                                       href="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.facets$3052647501.sort$VA_Rating*desc.html"
                                                                       target="" aria-label=" : High to Low">
                                                                        : High to Low
                                                                    </a>
                                                                </li>

                                                                <li>
                                                                    <a class="faceted-navigation-header__sort-link"
                                                                       rel="nofollow"
                                                                       href="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.facets$3052647501.sort$VA_Rating*asc.html"
                                                                       target="" aria-label=" : Low to High">
                                                                        : Low to High
                                                                    </a>
                                                                </li>

                                                                <li>
                                                                    <a class="faceted-navigation-header__sort-link"
                                                                       rel="nofollow"
                                                                       href="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.facets$3052647501.sort$asc.html"
                                                                       target="" aria-label=" Alpha: A to Z">
                                                                        Alpha: A to Z
                                                                    </a>
                                                                </li>

                                                                <li>
                                                                    <a class="faceted-navigation-header__sort-link"
                                                                       rel="nofollow"
                                                                       href="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.facets$3052647501.sort$desc.html"
                                                                       target="" aria-label=" Alpha: Z to A">
                                                                        Alpha: Z to A
                                                                    </a>
                                                                </li>
                                                            </ul>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="faceted-navigation__active-filters faceted-navigation-filters"
                                                     data-active-filter-values="%5B%7B%22%6E%61%6D%65%22%3A%22%35%30%30%20%56%41%22%2C%22%69%64%22%3A%22%33%30%35%32%36%34%37%35%30%31%22%2C%22%74%69%74%6C%65%22%3A%22%35%30%30%20%56%41%22%2C%22%76%61%6C%75%65%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%68%74%6D%6C%22%7D%5D"
                                                     data-clear-filters-text="Clear filters"
                                                     data-ref="0.7168192144268608">
                                                    <div class="">
                                                        <div class="">
                                                            <div class="faceted-navigation-header__active-filter"
                                                                 data-name="500%20VA"
                                                                 data-value="%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.html"
                                                                 data-title="500%20VA"
                                                                 data-ref="0.7675591265413411">
                                                                <button class="faceted-navigation-header__filter-link"
                                                                        aria-label="Remove 500 VA filter"
                                                                        data-filter-name="500 VA"
                                                                        data-filter-value="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.html">
                                                                    <span class="faceted-navigation-header__filter-label"><bdi>500 VA</bdi></span>
                                                                    <i class="icon icon-close"
                                                                       aria-hidden="true"></i>
                                                                </button>
                                                            </div>
                                                        </div>
                                                        <a href="#" rel="nofollow"
                                                           class="faceted-navigation__action-link faceted-navigation__action-link--clear-filters faceted-navigation__action-link--clear-filters--mobile ">
                                                            Clear filters
                                                        </a>
                                                    </div>
                                                </div>
                                                <div class="faceted-navigation-header__container">
                                                    <a rel="nofollow" target="_self" data-toggle-modal-facet=""
                                                       class="open-facets-mobile b-button b-button__primary b-button__primary--light"
                                                       role="button">
                                                        Filters (1)‎
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-xs-12 col-md-3">
                                            <div id="product-grid__filters"
                                                 data-filter-list="%5B%7B%22%73%68%6F%77%41%73%47%72%69%64%22%3A%66%61%6C%73%65%2C%22%76%61%6C%75%65%73%22%3A%5B%7B%22%6E%61%6D%65%22%3A%22%33%30%35%32%36%34%37%35%30%31%22%2C%22%63%6F%75%6E%74%22%3A%30%2C%22%61%63%74%69%76%65%22%3A%74%72%75%65%2C%22%69%64%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%68%74%6D%6C%22%2C%22%74%69%74%6C%65%22%3A%22%35%30%30%20%56%41%22%2C%22%76%61%6C%75%65%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%68%74%6D%6C%22%2C%22%75%72%6C%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%68%74%6D%6C%22%7D%2C%7B%22%6E%61%6D%65%22%3A%22%34%31%35%37%36%37%39%33%34%37%22%2C%22%63%6F%75%6E%74%22%3A%32%2C%22%61%63%74%69%76%65%22%3A%66%61%6C%73%65%2C%22%69%64%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%66%61%63%65%74%73%24%33%30%35%32%36%34%37%35%30%31%24%34%31%35%37%36%37%39%33%34%37%2E%68%74%6D%6C%22%2C%22%74%69%74%6C%65%22%3A%22%37%35%30%20%56%41%22%2C%22%76%61%6C%75%65%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%66%61%63%65%74%73%24%33%30%35%32%36%34%37%35%30%31%24%34%31%35%37%36%37%39%33%34%37%2E%68%74%6D%6C%22%2C%22%75%72%6C%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%66%61%63%65%74%73%24%33%30%35%32%36%34%37%35%30%31%24%34%31%35%37%36%37%39%33%34%37%2E%68%74%6D%6C%22%7D%2C%7B%22%6E%61%6D%65%22%3A%22%34%30%39%38%35%30%32%33%37%34%22%2C%22%63%6F%75%6E%74%22%3A%31%2C%22%61%63%74%69%76%65%22%3A%66%61%6C%73%65%2C%22%69%64%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%66%61%63%65%74%73%24%33%30%35%32%36%34%37%35%30%31%24%34%30%39%38%35%30%32%33%37%34%2E%68%74%6D%6C%22%2C%22%74%69%74%6C%65%22%3A%22%31%30%30%30%20%56%41%22%2C%22%76%61%6C%75%65%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%66%61%63%65%74%73%24%33%30%35%32%36%34%37%35%30%31%24%34%30%39%38%35%30%32%33%37%34%2E%68%74%6D%6C%22%2C%22%75%72%6C%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%66%61%63%65%74%73%24%33%30%35%32%36%34%37%35%30%31%24%34%30%39%38%35%30%32%33%37%34%2E%68%74%6D%6C%22%7D%2C%7B%22%6E%61%6D%65%22%3A%22%33%30%32%39%36%31%36%35%37%22%2C%22%63%6F%75%6E%74%22%3A%31%2C%22%61%63%74%69%76%65%22%3A%66%61%6C%73%65%2C%22%69%64%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%66%61%63%65%74%73%24%33%30%35%32%36%34%37%35%30%31%24%33%30%32%39%36%31%36%35%37%2E%68%74%6D%6C%22%2C%22%74%69%74%6C%65%22%3A%22%31%34%34%30%20%56%41%22%2C%22%76%61%6C%75%65%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%66%61%63%65%74%73%24%33%30%35%32%36%34%37%35%30%31%24%33%30%32%39%36%31%36%35%37%2E%68%74%6D%6C%22%2C%22%75%72%6C%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%66%61%63%65%74%73%24%33%30%35%32%36%34%37%35%30%31%24%33%30%32%39%36%31%36%35%37%2E%68%74%6D%6C%22%7D%2C%7B%22%6E%61%6D%65%22%3A%22%34%31%35%31%39%30%36%36%33%36%22%2C%22%63%6F%75%6E%74%22%3A%31%2C%22%61%63%74%69%76%65%22%3A%66%61%6C%73%65%2C%22%69%64%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%66%61%63%65%74%73%24%33%30%35%32%36%34%37%35%30%31%24%34%31%35%31%39%30%36%36%33%36%2E%68%74%6D%6C%22%2C%22%74%69%74%6C%65%22%3A%22%31%35%30%30%20%56%41%22%2C%22%76%61%6C%75%65%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%66%61%63%65%74%73%24%33%30%35%32%36%34%37%35%30%31%24%34%31%35%31%39%30%36%36%33%36%2E%68%74%6D%6C%22%2C%22%75%72%6C%22%3A%22%5C%2F%75%73%5C%2F%65%6E%2D%75%73%5C%2F%63%61%74%61%6C%6F%67%5C%2F%62%61%63%6B%75%70%2D%70%6F%77%65%72%2D%75%70%73%2D%73%75%72%67%65%2D%69%74%2D%70%6F%77%65%72%2D%64%69%73%74%72%69%62%75%74%69%6F%6E%5C%2F%65%61%74%6F%6E%2D%35%73%63%2D%75%70%73%2E%66%61%63%65%74%73%24%33%30%35%32%36%34%37%35%30%31%24%34%31%35%31%39%30%36%36%33%36%2E%68%74%6D%6C%22%7D%5D%2C%22%6E%61%6D%65%22%3A%22%56%41%5F%52%61%74%69%6E%67%22%2C%22%74%69%74%6C%65%22%3A%6E%75%6C%6C%2C%22%73%69%6E%67%6C%65%46%61%63%65%74%45%6E%61%62%6C%65%64%22%3A%66%61%6C%73%65%2C%22%66%61%63%65%74%53%65%61%72%63%68%45%6E%61%62%6C%65%64%22%3A%66%61%6C%73%65%7D%5D"
                                                 data-property-list="%5B%7B%22%73%68%6F%77%41%73%47%72%69%64%22%3A%66%61%6C%73%65%2C%22%6E%61%6D%65%22%3A%22%56%41%5F%52%61%74%69%6E%67%22%2C%22%73%69%6E%67%6C%65%46%61%63%65%74%45%6E%61%62%6C%65%64%22%3A%66%61%6C%73%65%2C%22%66%61%63%65%74%53%65%61%72%63%68%45%6E%61%62%6C%65%64%22%3A%66%61%6C%73%65%7D%5D"
                                                 data-hide-from-active="[&quot;Product_Type&quot;]"
                                                 data-facet-value-count="15" data-title="Filters"
                                                 data-mobile-dialog-title="Filters" data-close-text="Close"
                                                 data-clear-all-filters-text="Clear all filters"
                                                 data-clear-filters-text="Clear filters"
                                                 data-clear-selection-text="Clear selection"
                                                 data-view-more-text="View more" data-view-less-text="View less"
                                                 data-results-plural-text="Results"
                                                 data-results-singular-text="Result"
                                                 data-facet-search-label="Narrow results"
                                                 data-facet-search-placeholder="Search values"
                                                 data-no-suggestions-text="No suggestions" data-in-text="in"
                                                 data-of-text="of"
                                                 data-facet-group-search-label="Search within these results"
                                                 data-facet-group-search-placeholder="Enter a value"
                                                 data-facet-group-search-no-suggestions-text="No results based on search terms"
                                                 data-ref="0.7482591627384056">
                                                <div class="eaton-form faceted-navigation faceted-navigation__filters">
                                                    <h3 class="faceted-navigation__title">Filters</h3>
                                                    <div class="global-filter-search__container"
                                                         data-global-facet-search-label="Narrow results"
                                                         data-global-facet-search-placeholder="Search values"
                                                         data-global-facet-search-in-text="in"
                                                         data-global-facet-search-no-suggestions-text="No suggestions"
                                                         data-global-facet-search-filter-components=""
                                                         data-ref="0.4560182136163098">
                                                        <label class="global-filter-search__label">Narrow
                                                            results</label>
                                                        <input type="text" class="global-filter-search__input"
                                                               placeholder="Search values">
                                                        <ul class="global-filter-search__suggestions"></ul>
                                                        <div class="global-filter-search__suggestions no-suggestions-container hidden">
                                                            <div class="global-filter-search__suggestion global-filter-search__suggestion__no-suggestions__message">
                                                                No suggestions
                                                                <span class="global-filter-search__suggestion__suggested-term">
                                                                </span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="mobile-header">
                                                        <span>Filters</span>
                                                        <button data-toggle-modal-facet="" aria-label="Close"
                                                                class="close-facets-mobile button--reset">
                                                            <span class="sr-only">Close Filters</span>
                                                            <i class="icon icon-close" aria-hidden="true"></i>
                                                        </button>
                                                    </div>
                                                    <hr class="faceted-navigation__hr">
                                                    <div class="cross-reference__active-filters bullseye-map-active__filters faceted-navigation__active-filters__container faceted-navigation__active-filters__container--mobile "
                                                         data-active-filter-values="%5B%7B%22name%22%3A%223052647501%22%2C%22value%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.html%22%2C%22title%22%3A%22500%20VA%22%2C%22id%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.html%22%7D%5D"
                                                         data-result-count="undefined" data-results-text="Result"
                                                         data-of-text="of"
                                                         data-clear-all-filters-text="Clear all filters"
                                                         data-clear-filters-text="Clear filters"
                                                         data-clear-selection-text="Clear selection"
                                                         data-ref="0.9581137011502838">
                                                        <div class="">
                                                            <div class="">
                                                                <div class="faceted-navigation-header__active-filter"
                                                                     data-name="3052647501"
                                                                     data-value="%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.html"
                                                                     data-title="500%20VA"
                                                                     data-ref="0.16022104107696777">
                                                                    <button class="faceted-navigation-header__filter-link"
                                                                            aria-label="Remove 500 VA filter"
                                                                            data-filter-name="3052647501"
                                                                            data-filter-value="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.html">
                                                                        <span class="faceted-navigation-header__filter-label"><bdi>500 VA</bdi></span>
                                                                        <i class="icon icon-close"
                                                                           aria-hidden="true"></i>
                                                                    </button>
                                                                </div>
                                                            </div>
                                                            <a href="#" rel="nofollow"
                                                               class="faceted-navigation__action-link faceted-navigation__action-link--clear-filters faceted-navigation__action-link--clear-filters--mobile "
                                                               onclick="if(selectedSku.length > 0) { sessionStorage.setItem('selectedProdx', selectedSku) }">
                                                                Clear filters
                                                            </a>
                                                        </div>
                                                    </div>
                                                    <div class="faceted-navigation__filter-container"
                                                         data-values="%7B%22showAsGrid%22%3Afalse%2C%22values%22%3A%5B%7B%22name%22%3A%223052647501%22%2C%22count%22%3A0%2C%22active%22%3Atrue%2C%22id%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.html%22%2C%22title%22%3A%22500%20VA%22%2C%22value%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.html%22%2C%22url%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.html%22%7D%2C%7B%22name%22%3A%224157679347%22%2C%22count%22%3A2%2C%22active%22%3Afalse%2C%22id%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.facets%243052647501%244157679347.html%22%2C%22title%22%3A%22750%20VA%22%2C%22value%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.facets%243052647501%244157679347.html%22%2C%22url%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.facets%243052647501%244157679347.html%22%7D%2C%7B%22name%22%3A%224098502374%22%2C%22count%22%3A1%2C%22active%22%3Afalse%2C%22id%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.facets%243052647501%244098502374.html%22%2C%22title%22%3A%221000%20VA%22%2C%22value%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.facets%243052647501%244098502374.html%22%2C%22url%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.facets%243052647501%244098502374.html%22%7D%2C%7B%22name%22%3A%22302961657%22%2C%22count%22%3A1%2C%22active%22%3Afalse%2C%22id%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.facets%243052647501%24302961657.html%22%2C%22title%22%3A%221440%20VA%22%2C%22value%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.facets%243052647501%24302961657.html%22%2C%22url%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.facets%243052647501%24302961657.html%22%7D%2C%7B%22name%22%3A%224151906636%22%2C%22count%22%3A1%2C%22active%22%3Afalse%2C%22id%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.facets%243052647501%244151906636.html%22%2C%22title%22%3A%221500%20VA%22%2C%22value%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.facets%243052647501%244151906636.html%22%2C%22url%22%3A%22%2Fus%2Fen-us%2Fcatalog%2Fbackup-power-ups-surge-it-power-distribution%2Featon-5sc-ups.facets%243052647501%244151906636.html%22%7D%5D%2C%22name%22%3A%22VA_Rating%22%2C%22title%22%3Anull%2C%22singleFacetEnabled%22%3Afalse%2C%22facetSearchEnabled%22%3Afalse%2C%22secure%22%3Afalse%7D"
                                                         data-clear-selection-text="Clear selection"
                                                         data-view-more-text="View more" data-facet-value-count="15"
                                                         data-view-less-text="View less"
                                                         data-facet-group-search-label="Search within these results"
                                                         data-facet-group-search-placeholder="Enter a value"
                                                         data-facet-group-search-no-suggestions-text="No results based on search terms"
                                                         data-ref="0.7651086711216271">
                                                        <div class="faceted-navigation__facet-group  ">
                                                            <button class="faceted-navigation__header button--reset"
                                                                    data-toggle="collapse" data-target="#VA_Rating"
                                                                    aria-expanded="true" aria-controls="VA_Rating">
                                                                null
                                                                <i class="icon icon-sign-minus"
                                                                   aria-hidden="true"></i>
                                                                <i class="icon icon-sign-plus faceted-navigation__icon-sign-plus"
                                                                   aria-hidden="true"></i>
                                                            </button>
                                                            <div id="VA_Rating" class="collapse in">
                                                                <div class="faceted-navigation__list__search__container hidden">
                                                                    <label class="faceted-navigation__list__search__label">Search
                                                                        within these results</label>
                                                                    <input type="text"
                                                                           class="faceted-navigation__list__search__input"
                                                                           placeholder="Enter a value">
                                                                    <div class="faceted-navigation__list__search__suggestions faceted-navigation__list__search__no-suggestions__container hidden">
                                                                        <div class="faceted-navigation__list__search__suggestion faceted-navigation__list__search__suggestion__no-suggestions__message">
                                                                            No results based on search terms
                                                                            <span class="faceted-navigation__list__search__suggestion__suggested-term"></span>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <a href="#" rel="nofollow" role="button"
                                                                   class="faceted-navigation-header__action-link faceted-navigation-header__action-link--clear-filters faceted-navigation-header__action-link--clear-filters--sidebar "
                                                                   onclick="if(selectedSku.length > 0) { sessionStorage.setItem('selectedProdx', selectedSku) }">
                                                                    Clear selection
                                                                </a>
                                                                <fieldset>
                                                                    <ul class="faceted-navigation__list faceted-navigation__list--unfiltered ">
                                                                        <li class="faceted-navigation__list-item  ">
                                                                            <div class="faceted-navigation__facet-value">
                                                                                <label class="faceted-navigation__facet-value-label">
                                                                                    <a href="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.html"
                                                                                       rel="nofollow" role="link"
                                                                                       class="has-url"
                                                                                       onclick="if(selectedSku.length > 0) { sessionStorage.setItem('selectedProdx', selectedSku) }">
                                                                                        <input data-analytics-event="submittal-builder-facet"
                                                                                               data-analytics-name="checkbox : 3052647501"
                                                                                               data-analytics-state="on"
                                                                                               data-single-facet-enabled="false"
                                                                                               type="checkbox"
                                                                                               class="input input--small"
                                                                                               value="3052647501"
                                                                                               name="VA_Rating"
                                                                                               data-title="500 VA"
                                                                                               data-id="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.html"
                                                                                               data-is-secure="undefined"
                                                                                               checked="">
                                                                                        <span class="inner"><bdi>500 VA</bdi></span>
                                                                                    </a>
                                                                                </label>
                                                                            </div>
                                                                        </li>
                                                                        <li class="faceted-navigation__list-item  ">
                                                                            <div class="faceted-navigation__facet-value">
                                                                                <label class="faceted-navigation__facet-value-label">
                                                                                    <a href="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.facets$3052647501$4157679347.html"
                                                                                       rel="nofollow" role="link"
                                                                                       class="has-url"
                                                                                       onclick="if(selectedSku.length > 0) { sessionStorage.setItem('selectedProdx', selectedSku) }">
                                                                                        <input data-analytics-event="submittal-builder-facet"
                                                                                               data-analytics-name="checkbox : 4157679347"
                                                                                               data-analytics-state="off"
                                                                                               data-single-facet-enabled="false"
                                                                                               type="checkbox"
                                                                                               class="input input--small"
                                                                                               value="4157679347"
                                                                                               name="VA_Rating"
                                                                                               data-title="750 VA"
                                                                                               data-id="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.facets$3052647501$4157679347.html"
                                                                                               data-is-secure="undefined">
                                                                                        <span class="inner"><bdi>750 VA</bdi></span>
                                                                                    </a>
                                                                                </label>
                                                                            </div>
                                                                        </li>
                                                                        <li class="faceted-navigation__list-item  ">
                                                                            <div class="faceted-navigation__facet-value">
                                                                                <label class="faceted-navigation__facet-value-label">
                                                                                    <a href="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.facets$3052647501$4098502374.html"
                                                                                       rel="nofollow" role="link"
                                                                                       class="has-url"
                                                                                       onclick="if(selectedSku.length > 0) { sessionStorage.setItem('selectedProdx', selectedSku) }">
                                                                                        <input data-analytics-event="submittal-builder-facet"
                                                                                               data-analytics-name="checkbox : 4098502374"
                                                                                               data-analytics-state="off"
                                                                                               data-single-facet-enabled="false"
                                                                                               type="checkbox"
                                                                                               class="input input--small"
                                                                                               value="4098502374"
                                                                                               name="VA_Rating"
                                                                                               data-title="1000 VA"
                                                                                               data-id="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.facets$3052647501$4098502374.html"
                                                                                               data-is-secure="undefined">
                                                                                        <span class="inner"><bdi>1000 VA</bdi></span>
                                                                                    </a>
                                                                                </label>
                                                                            </div>
                                                                        </li>
                                                                        <li class="faceted-navigation__list-item  ">
                                                                            <div class="faceted-navigation__facet-value">
                                                                                <label class="faceted-navigation__facet-value-label">
                                                                                    <a href="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.facets$3052647501$302961657.html"
                                                                                       rel="nofollow" role="link"
                                                                                       class="has-url"
                                                                                       onclick="if(selectedSku.length > 0) { sessionStorage.setItem('selectedProdx', selectedSku) }">
                                                                                        <input data-analytics-event="submittal-builder-facet"
                                                                                               data-analytics-name="checkbox : 302961657"
                                                                                               data-analytics-state="off"
                                                                                               data-single-facet-enabled="false"
                                                                                               type="checkbox"
                                                                                               class="input input--small"
                                                                                               value="302961657"
                                                                                               name="VA_Rating"
                                                                                               data-title="1440 VA"
                                                                                               data-id="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.facets$3052647501$302961657.html"
                                                                                               data-is-secure="undefined">
                                                                                        <span class="inner"><bdi>1440 VA</bdi></span>
                                                                                    </a>
                                                                                </label>
                                                                            </div>
                                                                        </li>
                                                                        <li class="faceted-navigation__list-item  ">
                                                                            <div class="faceted-navigation__facet-value">
                                                                                <label class="faceted-navigation__facet-value-label">
                                                                                    <a href="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.facets$3052647501$4151906636.html"
                                                                                       rel="nofollow" role="link"
                                                                                       class="has-url"
                                                                                       onclick="if(selectedSku.length > 0) { sessionStorage.setItem('selectedProdx', selectedSku) }">
                                                                                        <input data-analytics-event="submittal-builder-facet"
                                                                                               data-analytics-name="checkbox : 4151906636"
                                                                                               data-analytics-state="off"
                                                                                               data-single-facet-enabled="false"
                                                                                               type="checkbox"
                                                                                               class="input input--small"
                                                                                               value="4151906636"
                                                                                               name="VA_Rating"
                                                                                               data-title="1500 VA"
                                                                                               data-id="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.facets$3052647501$4151906636.html"
                                                                                               data-is-secure="undefined">
                                                                                        <span class="inner"><bdi>1500 VA</bdi></span>
                                                                                    </a>
                                                                                </label>
                                                                            </div>
                                                                        </li>
                                                                        <li class="faceted-navigation__view-more-less-container hidden">
                                                                            <button class="button--reset faceted-navigation__view-more-values">
                                                                                View more <span
                                                                                    class="faceted-navigation__view-more-values__count"><bdi>(-10)</bdi></span>
                                                                            </button>
                                                                            <button class="button--reset faceted-navigation__view-less-values">
                                                                                View less
                                                                            </button>
                                                                        </li>
                                                                    </ul>
                                                                </fieldset>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-xs-12 col-md-9">
                                            <div class="results-list"
                                                 data-results-url="{&quot;dataAttribute&quot;:[{&quot;productType&quot;:&quot;UPS&quot;},{&quot;skuCardAttr&quot;:&quot;||&quot;},{&quot;currentLanguage&quot;:&quot;en_US&quot;},{&quot;currentCountry&quot;:&quot;US&quot;},{&quot;pageType&quot;:&quot;product-family&quot;},{&quot;primarySubCategoryTag&quot;:&quot;eaton:product-taxonomy\\/backup-power,-ups,-surge-&amp;-it-power-distribution\\/backup-power-(ups)\\/eaton-5sc|eaton:language\\/en-us|eaton:country\\/north-america\\/us&quot;},{&quot;pageSize&quot;:60},{&quot;defaultSortOrder&quot;:&quot;asc&quot;},{&quot;defaultSortFacet&quot;:&quot;VA_Rating&quot;},{},{&quot;currentLoadMore&quot;:0},{&quot;currentSortByOption&quot;:null},{&quot;currentSortFacetOption&quot;:null},{&quot;currentResourcePath&quot;:&quot;\\/content\\/eaton\\/us\\/en-us\\/catalog\\/backup-power-ups-surge-it-power-distribution\\/eaton-5sc-ups\\/jcr:content\\/root\\/responsivegrid\\/product_tabs\\/content-tab-2\\/product_grid&quot;},{&quot;pimPrimaryImage&quot;:&quot;https:\\/\\/www.eaton.com\\/mdmfiles\\/PDM22630115\\/Eaton-5SC-UPS_FM\\/500x500_72dpi&quot;},{&quot;inventoryId&quot;:&quot;3394635&quot;},{&quot;selectors&quot;:&quot;facets$3052647501|anon&quot;},{&quot;requestUri&quot;:&quot;\\/content\\/eaton\\/us\\/en-us\\/catalog\\/backup-power-ups-surge-it-power-distribution\\/eaton-5sc-ups\\/_jcr_content\\/root\\/responsivegrid\\/product_tabs\\/content-tab-2\\/product_grid.facets$3052647501.anon.html&quot;},{&quot;currentPagePath&quot;:&quot;\\/content\\/eaton\\/us\\/en-us\\/catalog\\/backup-power-ups-surge-it-power-distribution\\/eaton-5sc-ups&quot;},{&quot;fallBackImage&quot;:&quot;\\/content\\/dam\\/eaton\\/resources\\/default-sku-image.jpg&quot;},{&quot;londDescCheck&quot;:&quot;true&quot;}]}"
                                                 data-results-next-page="0" data-results-type="product-card-sku"
                                                 data-i18n="{
      &quot;goTo&quot;: &quot;Go to&quot;,
      &quot;productSpecificationTitle&quot;: &quot;Specifications&quot;,
      &quot;productResourcesTitle&quot;: &quot;Resources&quot;
    }">
                                                <div class="results-list__content">
                                                    <div class="product-card-sku">
                                                        <div class="product-card-sku__image-wrapper b-body-copy-small">
                                                            <a href="https://eaton-sit.tcc.etn.com/us/en-us/skuPage.5SC500.html"
                                                               data-analytics-event="model-result"
                                                               class="product-card-sku__image-link" target="_self">
                                                                <div class="rendition">
                                                                    <img class="rendition__image img-responsive"
                                                                         data-src="https://www.eaton.com/mdmfiles/PDM67607756/5SC500_R/220x220_96dpi"
                                                                         data-mobile-rendition="https://www.eaton.com/mdmfiles/PDM67607756/5SC500_R/500x500_72dpi"
                                                                         data-tablet-rendition="https://www.eaton.com/mdmfiles/PDM67607756/5SC500_R/220x220_96dpi"
                                                                         data-desktop-rendition="https://www.eaton.com/mdmfiles/PDM67607756/5SC500_R/220x220_96dpi"
                                                                         src="https://www.eaton.com/mdmfiles/PDM67607756/5SC500_R/220x220_96dpi">
                                                                </div>
                                                            </a>
                                                        </div>
                                                        <div class="product-card-sku__header">
                                                            <div class="product-card-sku__title-wrapper">
                                                                <h3 class="product-card-sku__name">
                                                                    <a href="https://eaton-sit.tcc.etn.com/us/en-us/skuPage.5SC500.html"
                                                                       data-analytics-event="model-result"
                                                                       target="_self"
                                                                       class="product-card-sku__url-link"><span
                                                                            class="name-label">5SC500</span><i
                                                                            class="icon icon-chevron-right"
                                                                            aria-hidden="true"></i></a>
                                                                </h3>
                                                                <div class="product-card-sku__price b-body-copy">$290
                                                                </div>
                                                            </div>
                                                            <ul class="product-card-sku__links-list">
                                                                <li class="product-card-sku__link-item">
                                                                    <a href="https://eaton-sit.tcc.etn.com/us/en-us/skuPage.5SC500.html#Specifications"
                                                                       data-analytics-event="model-result"
                                                                       class="product-card-sku__link-item-link"
                                                                       target="_self"
                                                                       aria-label="Go to Specifications">
                                                                        <span class="link-label">Specifications</span>
                                                                        <i class="icon icon-chevron-right u-visible-mobile"
                                                                           aria-hidden="true"></i>
                                                                    </a>
                                                                </li>
                                                                <li class="product-card-sku__link-item">
                                                                    <a href="https://eaton-sit.tcc.etn.com/us/en-us/skuPage.5SC500.html#Resources"
                                                                       data-analytics-event="model-result"
                                                                       class="product-card-sku__link-item-link"
                                                                       target="_self" aria-label="Go to ">
                                                                        <span class="link-label">Resources</span>
                                                                        <i class="icon icon-chevron-right u-visible-mobile"
                                                                           aria-hidden="true"></i>
                                                                    </a>
                                                                </li>
                                                                <input type="checkbox"
                                                                       class="product-card-sku__comp hide"
                                                                       id="5SC500"
                                                                       autocomplete="off"> <span
                                                                    class="link-label-comparision hide">Compare</span>
                                                            </ul>
                                                        </div>
                                                        <div class="product-card-sku__content">
                                                            <div class="product-card-sku__description">Eaton 5SC
                                                                UPS,
                                                                500 VA, 350 W, 5-15P input, Outputs: (4) 5-15R
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="results-list__bottom">
                                                    <div class="results-list__actions text-center">
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-12 eaton-product-tabs__tab-panel" data-tab-name="tab-3">
                    <span class="print-tab-heading resource-print-heading">Download Links</span>
                    <div class="container">
                        <div class="grey__bg-area">
                            <div class="hide" id="correctAuthentication">
                                <div class="row">
                                    <div class="col-md-12">
                                        <div class="eaton-qr-authenticated serial_number_verified">
                                            <div><span
                                                    class="eaton-qr-authenticated__label">Serial Number Verified :</span><span
                                                    id="serial_verified_number"></span></div>
                                        </div>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="container">
                        <div class="grey__bg-area">
                            <div class="hide" id="correctAuthCodeMessage">
                                <div class="row">
                                    <div class="col-md-12">
                                        <div class="eaton-qr-authenticated">
                                            <div><span class="eaton-qr-authenticated__success"><span
                                                    class="eaton-qr-authenticated__label">Authenticated:</span> <span
                                                    id="serial_auth_verified"></span></span><img alt=""><br>The
                                                product
                                                is verified as being authentic; however, this does not guarantee the
                                                condition or fit for purpose of the product.
                                            </div>

                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-12 eaton-product-tabs__tab-panel" data-tab-name="tab-4">
                    <div class="aem-Grid aem-Grid--12 aem-Grid--default--12 ">
                        <div class="text aem-GridColumn aem-GridColumn--default--12">
                            <div class="eaton-text-statement">
                                <p class="eaton-text-statement__content rich-text-container">Here is some authored Support content.</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="module-intra-tab-link">
                    <div class="container">
                        <a target="_self"
                           href="/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-5sc-ups.models.html"
                           aria-label="Models"><span><bdi>Models</bdi></span><i class="icon icon-chevron-right"
                                                                                aria-hidden="true"></i></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
    `;
  }
};
