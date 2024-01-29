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


//-----------------------------------
// Re-Usable HTML Templates used by Javascript Files
//-----------------------------------
'use strict';

var App = App || {};
App.global = App.global || {};

App.global.templates = function () {

  var getSecureIcon = function getSecureIcon(isSecure) {
    return isSecure ? '<i class="icon icon-secure-lock" aria-hidden="true"></i> ' : '';
  };

  /**
  * Based on AEM Template: /eaton.ui.static/src/main/content/jcr_root/apps/eaton/static-components/shared/templates/rendition-image.html
  * @param { Object } image - Image URL's for all breakpoints
  * Aditional Info: Jira EATON-81
  */
  var renditionImage = function renditionImage(image) {
    return '\n      <div class="rendition">\n        <img\n          class="rendition__image img-responsive"\n          data-src="' + image.mobile + '"\n          data-mobile-rendition="' + image.mobile + '"\n          data-tablet-rendition="' + image.tablet + '"\n          data-desktop-rendition="' + image.desktop + '"\n        />\n      </div>\n    ';
  };

  /**
  * Based on AEM Template: /eaton.ui.static/src/main/content/jcr_root/apps/eaton/static-components/shared/templates/rendition-background.html
  * @param { Object } image - Image URL's for all breakpoints
  * Aditional Info: Jira EATON-81
  */
  var renditionBackground = function renditionBackground(image) {
    return '\n      <div class="rendition-bg"\n        style="background-image: url(\'' + image.mobile + '\');"\n        data-src="' + image.mobile + '"\n        data-mobile-rendition="' + image.mobile + '"\n        data-tablet-rendition="' + image.tablet + '"\n        data-desktop-rendition="' + image.desktop + '"\n      >\n      </div>\n    ';
  };

  // M-36: SKU Card Template
  //--------------
  var productCompatibilityledView = function productCompatibilityledView(data, i18n) {
    return '\n      <div class="product-card-sku">\n\n        <div class="product-card-sku__image-wrapper b-body-copy-small">\n\n            ' + renditionImage(data.contentItem.image) + '\n          </a>\n        </div>\n\n        <div class="product-card-sku__header">\n\n          <div class="product-card-sku__title-wrapper">\n            <div class="product-card-sku__price__product-header b-body-copy">\n                <p class="product-card-sku__price-list__model-number" >' + data.contentItem.lampModelNumberlabel + ': ' + data.contentItem.lampModelNumber + '</p>\n                <p class="product-card-sku__price-list__lamp-manufacturer" >' + data.contentItem.lampManufacturerlabel + ': ' + data.contentItem.lampManufacturer + '</p>\n            </div>\n          </div>\n\n\n\n        </div>\n\n        <div class="product-card-sku__specs-content">\n           <ul class="product-card-sku__specs-column">\n\n            ' + data.contentItem.productAttributes.map(function (attribute) {
      return '\n              <li class="product-card-sku__specs-column-list">' + attribute.productAttributeLabel + ': ' + attribute.productAttributeValue + '\n              </li>';
    }).join('') + '\n           </ul>\n\n        </div>\n\n      </div>';
  };
  // M-36: SKU Card Template
  //--------------
  var productCompatibilityDimmerView = function productCompatibilityDimmerView(data, i18n) {
    return '\n      <div class="product-card-sku">\n\n        <div class="product-card-sku__image-wrapper b-body-copy-small">\n          <a href="' + data.contentItem.link.url + '"\n            class="product-card-sku__image-link"\n            data-analytics-event="model-result"\n            target="' + data.contentItem.link.target + '"\n          >\n            ' + renditionImage(data.contentItem.image) + '\n          </a>\n        </div>\n\n        <div class="product-card-sku__header">\n\n          <div class="product-card-sku__title-wrapper">\n            <h3 class="product-card-sku__name">\n              <a href="' + data.contentItem.link.url + '"\n                target="' + data.contentItem.link.target + '"\n                class="product-card-sku__url-link"\n              >\n                <span class="name-label">' + data.contentItem.name + '</span>\n                <i class="icon icon-chevron-right" aria-hidden="true"></i>\n              </a>\n            </h3>\n\n          </div>\n\n          <ul class="product-card-sku__links-list">\n            <li class="product-card-sku__link-item">\n              <a href="' + data.contentItem.productLinks.specificationsURL + '"\n                class="product-card-sku__link-item-link"\n                target="_self"\n                aria-label="' + i18n.goTo + ' ' + i18n.productSpecificationTitle + '"\n              >\n                <span class="link-label">' + i18n.productSpecificationTitle + '</span>\n                <i class="icon icon-chevron-right u-visible-mobile" aria-hidden="true"></i>\n              </a>\n            </li>\n\n            <li class="product-card-sku__link-item">\n              <a href="' + data.contentItem.productLinks.resourcesURL + '"\n                class="product-card-sku__link-item-link"\n                target="_self"\n                aria-label="' + i18n.goTo + ' ' + i18n.productResourcesTitle + '"\n              >\n                <span class="link-label">' + i18n.productResourcesTitle + '</span>\n                <i class="icon icon-chevron-right u-visible-mobile" aria-hidden="true"></i>\n              </a>\n            </li>\n          </ul>\n\n        </div>\n\n        <div class="product-card-sku__content">\n\n          <div class="product-card-sku__description">' + data.contentItem.description + '</div>\n        </div>\n\n      </div>';
  };

  // M-36: SKU Card Template
  //--------------
  var productGridSKU = function productGridSKU(data, i18n) {
    var modelCode = data.contentItem.modelCode && data.contentItem.modelCode !== data.contentItem.name ? '<span> | ' + i18n.ModelCode + ': ' + data.contentItem.modelCode + '</span>' : '';

    return '\n      <div class="product-card-sku">\n\n        <div class="product-card-sku__image-wrapper b-body-copy-small">\n          <a href="' + data.contentItem.link.url + '"\n            class="product-card-sku__image-link"\n            data-analytics-event="model-result"\n            target="' + data.contentItem.link.target + '"\n          >\n            ' + renditionImage(data.contentItem.image) + '\n          </a>\n        </div>\n\n        <div class="product-card-sku__header">\n\n          <div class="product-card-sku__title-wrapper">\n            <h3 class="product-card-sku__name">\n              <a href="' + data.contentItem.link.url + '"\n                target="' + data.contentItem.link.target + '"\n                class="product-card-sku__url-link"\n              >\n                <span class="name-label">' + data.contentItem.name + '</span>\n                ' + modelCode + '\n                <i class="icon icon-chevron-right" aria-hidden="true"></i>\n              </a>\n            </h3>\n            <div class="product-card-sku__price b-body-copy">' + data.contentItem.productPrice + '</div>\n          </div>\n\n          <ul class="product-card-sku__links-list">\n            <li class="product-card-sku__link-item">\n              <a href="' + data.contentItem.productLinks.specificationsURL + '"\n                class="product-card-sku__link-item-link"\n                target="_self"\n                aria-label="' + i18n.goTo + ' ' + i18n.productSpecificationTitle + '"\n              >\n                <span class="link-label">' + i18n.productSpecificationTitle + '</span>\n                <i class="icon icon-chevron-right u-visible-mobile" aria-hidden="true"></i>\n              </a>\n            </li>\n\n            <li class="product-card-sku__link-item">\n              <a href="' + data.contentItem.productLinks.resourcesURL + '"\n                class="product-card-sku__link-item-link"\n                target="_self"\n                aria-label="' + i18n.goTo + ' ' + i18n.productResourcesTitle + '"\n              >\n                <span class="link-label">' + i18n.productResourcesTitle + '</span>\n                <i class="icon icon-chevron-right u-visible-mobile" aria-hidden="true"></i>\n              </a>\n            </li>\n\n            <input type="checkbox" class="product-card-sku__comp" id="' + data.contentItem.name + '" autocomplete="off" data-cols="1">\n            <span class="link-label-comparision" > Compare </span>\n\n          </ul>\n\n        </div>\n\n        <div class="product-card-sku__content">\n          <div class="product-card-sku__attrs-list">\n\n            ' + data.contentItem.productAttributes.map(function (attribute) {
      return '\n                <div class="product-card-sku__attrs-list-item">\n                  <div class="product-card-sku__attr-label b-eyebrow-small text-uppercase">' + attribute.productAttributeLabel + '</div>\n                  <div class="product-card-sku__attr-value b-body-copy">' + attribute.productAttributeValue + '</div>\n                </div>';
    }).join('') + '\n\n          </div>\n          <div class="product-card-sku__description">' + data.contentItem.description + '</div>\n        </div>\n\n      </div>';
  };

  // M-37: Subcategory Card Template
  //--------------
  var productGridSubcategory = function productGridSubcategory(data, i18n) {
    var secureIcon = data.contentItem.secure === true ? '<i class="icon icon-secure-lock" aria-hidden="true"></i> ' : '';
    var productDescription = data.contentItem.productGridDescription !== undefined && data.contentItem.productGridDescription !== null ? data.contentItem.productGridDescription : '';
    if (typeof data.contentItem.subcategory !== 'undefined' && data.contentItem.subcategory !== null) {
      return '\n      <div class="product-card-subcategory">\n\n        <a href="' + data.contentItem.link.url + '"\n          target="' + data.contentItem.link.target + '"\n          data-analytics-event="model-result"\n          class="product-card-subcategory__link">\n          <span class="sr-only">' + i18n.goTo + ' ' + data.contentItem.name + '</span>\n        </a>\n\n        <div class="product-card-subcategory__image-wrapper">\n          ' + renditionImage(data.contentItem.image) + '\n        </div>\n\n        <div class="product-card-subcategory__content-wrapper">\n          <div class="product-card-subcategory__subcategory b-eyebrow-small">' + data.contentItem.subcategory + '</div>\n          <h2 class="product-card-subcategory__name" data-is-secure="' + data.contentItem.secure + '">\n            ' + secureIcon + '  ' + data.contentItem.name + '\n          </h2>\n          ' + productDescription + '\n        </div>\n\n      </div>';
    } else {
      return '\n          <div class="product-card-subcategory">\n\n            <a href="' + data.contentItem.link.url + '"\n              target="' + data.contentItem.link.target + '"\n              data-analytics-event="model-result"\n              class="product-card-subcategory__link">\n              <span class="sr-only">' + i18n.goTo + ' ' + data.contentItem.name + '</span>\n            </a>\n\n            <div class="product-card-subcategory__image-wrapper">\n              ' + renditionImage(data.contentItem.image) + '\n            </div>\n\n            <div class="product-card-subcategory__content-wrapper">\n              <h2 class="product-card-subcategory__name" data-is-secure="' + data.contentItem.secure + '">\n\t\t\t\t' + secureIcon + '  ' + data.contentItem.name + '\n\t\t\t  </h2>\n\t\t\t  ' + productDescription + '\n            </div>\n\n          </div>';
    }
  };

  // Search Results: Product Family Template
  //--------------
  var searchResultsProductFamily = function searchResultsProductFamily(data, i18n) {
    var productStatus = data.contentItem.status === 'Discontinued' ? '<div class="results-list-submodule__badge-discontinued">' + data.contentItem.status + '</div>' : '';
    return '\n      <div class="results-list-submodule results-list-submodule--type-' + data.contentType + '">\n        <div class="results-list-submodule__image-wrapper b-body-copy-small">\n          <a href="' + data.contentItem.link.url + '"\n            class="results-list-submodule__image-link"\n            target=""\n          >\n            ' + renditionImage(data.contentItem.image) + '\n            </a>\n        </div>\n        <div class="results-list-submodule__content-wrapper">\n\t\t' + productStatus + '\n\n          <h4 class="results-list-submodule__name b-heading-h5">\n            <a href="' + data.contentItem.link.url + '"\n              target=""\n              data-analytics-event="search-result"\n              class="results-list-submodule__name-link"\n            >' + data.contentItem.name + '</a>\n          </h4>\n\n          <div class="results-list-submodule__description b-body-copy-small">' + data.contentItem.description + '</div>\n\n          <div class="results-list-submodule__url b-body-copy-small">\n            <a href="' + data.contentItem.link.url + '"\n              target=""\n              class="results-list-submodule__url-link"\n              aria-label="' + i18n.goTo + ' ' + data.contentItem.link.text + '"\n            >' + getSecureIcon(data.contentItem.secure) + ' ' + data.contentItem.link.completeUrl + '</a>\n          </div>\n\n          <ul class="results-list-submodule__link-list b-body-copy-small u-list-inline">\n\n            ' + data.contentItem.secondaryLinks.map(function (link) {
      return '\n                <li class="results-list-submodule__link-item">\n                  <a class="results-list-submodule__link-item-link"\n                    href="' + link.url + '"\n                    target=""\n                    aria-label="' + i18n.goTo + ' ' + link.text + '"\n                  >' + link.text + '</a>\n                </li>';
    }).join('') + '\n          </ul>\n        </div>\n      </div>';
  };

  // Search Results: Article Template
  //--------------
  var searchResultsArticle = function searchResultsArticle(data, i18n) {

    var articleDateTPL = data.contentItem.date ? '<div class="results-list-submodule__date b-body-copy-small">' + data.contentItem.date + '</div>' : '';

    var articleExternalIconTPL = data.contentItem.link.linkType === 'external' ? '<i class="icon icon-link-external" aria-hidden="true"></i>' : '';

    return '\n      <div class="results-list-submodule results-list-submodule--type-' + data.contentType + '">\n\n        <div class="results-list-submodule__content-wrapper">\n\n          <h4 class="results-list-submodule__name b-heading-h5">\n            <a href="' + data.contentItem.link.url + '"\n              class="results-list-submodule__name-link"\n              data-analytics-event="search-result"\n              target="' + data.contentItem.link.target + '"\n            >\n              <span class="name-label">' + data.contentItem.name + '</span>\n              ' + articleExternalIconTPL + '\n            </a>\n          </h4>\n\n          ' + articleDateTPL + '\n\n          <div class="results-list-submodule__description b-body-copy-small">' + data.contentItem.description + '</div>\n\n          <div class="results-list-submodule__url b-body-copy-small">\n            <a href="' + data.contentItem.link.url + '"\n              target="' + data.contentItem.link.target + '"\n              class="results-list-submodule__url-link"\n              aria-label="' + i18n.goTo + ' ' + data.contentItem.link.text + '"\n            >' + getSecureIcon(data.contentItem.secure) + ' ' + data.contentItem.link.completeUrl + '</a>\n          </div>\n\n        </div>\n      </div>\n    ';
  };

  // Search Results: Resource Template
  //--------------
  var searchResultsResource = function searchResultsResource(data, i18n) {
    /* eslint-disable no-unused-vars*/
    var fileDetails = data.contentItem.documentType !== '' ? data.contentItem.documentSize !== '' ? '(' + data.contentItem.documentType + ' ' + data.contentItem.documentSize + ')' : '(' + data.contentItem.documentType + ')' : data.contentItem.documentSize !== '' ? '(' + data.contentItem.documentSize + ')' : '';
    var sha256 = data.contentItem.eatonSHA !== '' ? '<div class="b-body-copy-small download-links__SHA"> ' + i18n.sha256 + ' : ' + data.contentItem.eatonSHA + ' </div>' : '';
    var eccn = data.contentItem.eatonECCN !== '' ? '<div class="b-body-copy-small download-links__SHA"> ' + i18n.eccnWarning + ' ' + data.contentItem.eatonECCN + '</div>' : '';

    return '\n      <div class="results-list-submodule results-list-submodule--type-' + data.contentType + '">\n\n        <div class="results-list-submodule__icon-wrapper">\n          <a href="' + data.contentItem.link.url + '"\n            target="' + data.contentItem.link.target + '"\n            class="results-list-submodule__url-link"\n            data-analytics-event="search-result"\n            aria-label="' + i18n.download + ' ' + data.contentItem.documentName + '"\n          >\n            <i class="icon icon-download" aria-hidden="true"></i>\n            <span class="sr-only">' + data.contentItem.link.text + '</span>\n          </a>\n        </div>\n\n        <div class="results-list-submodule__content-wrapper">\n\n          <h4 class="results-list-submodule__name b-heading-h5">\n            <a href="' + data.contentItem.link.url + '"\n              target="' + data.contentItem.link.target + '"\n              class="results-list-submodule__name-link"\n            >' + data.contentItem.name + '</a>\n          </h4>\n\n          <div class="results-list-submodule__document b-body-copy">' + fileDetails + '</div>\n\n          ' + sha256 + '\n          ' + eccn + '\n\n          <div class="results-list-submodule__url b-body-copy-small">\n            <a href="' + data.contentItem.link.url + '"\n              target="' + data.contentItem.link.target + '"\n              class="results-list-submodule__link"\n              aria-label="' + i18n.download + ' ' + data.contentItem.documentName + '"\n            >' + getSecureIcon(data.contentItem.secure) + ' ' + data.contentItem.link.completeUrl + '</a>\n          </div>\n\n        </div>\n\n\n      </div>\n    ';
  };

  return {
    renditionImage: renditionImage,
    renditionBackground: renditionBackground,
    productGridSKU: productGridSKU,
    productGridSubcategory: productGridSubcategory,
    searchResultsProductFamily: searchResultsProductFamily,
    searchResultsArticle: searchResultsArticle,
    searchResultsResource: searchResultsResource,
    productCompatibilityDimmerView: productCompatibilityDimmerView,
    productCompatibilityledView: productCompatibilityledView

  };
}();