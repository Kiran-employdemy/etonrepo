/* eslint-disable no-undef */
/* eslint-disable no-global-assign */
// noinspection JSConstantReassignment
//-----------------------------------
// Product Grid Comparison Widget Component
//-----------------------------------

const requirePresent = () => typeof require !== 'undefined';

if (requirePresent()) {
  const globalConstants = require('../../../global/js/etn-new-global-constants');
  const constants = require('./comparison-widget-constants');
  literals = globalConstants.literals;
  $ = require(literals.JQUERY);
  eventListeners = globalConstants.eventListeners;
  comparisonWidgetQuerySelectors = constants.comparisonWidgetQuerySelectors;
  comparisonWidgetClasses = constants.comparisonWidgetClasses;
  comparisonMustacheElements = constants.comparisonMustacheElements;
}

class Comparison {

  constructor() {
  // eslint-disable-next-line no-undef
    this.counter = 0;
    this.compareResult = '';
    this.iduncheck = [];

    this.comparisonWidget = document.querySelector(comparisonWidgetQuerySelectors.comparisonWidget);
    this.comparisonWidgetItemLabel = document.querySelectorAll(comparisonWidgetQuerySelectors.comparisonWidgetItemLabel);
    this.clearSelection = document.querySelector(comparisonWidgetQuerySelectors.clearSelection);
    this.comparisonWidgetItemCountDiv = document.querySelector(comparisonWidgetQuerySelectors.comparisonWidgetItemCountDiv);
    this.compareButton = document.querySelector(comparisonWidgetQuerySelectors.compareButton);
    this.productGridResults = document.querySelector(comparisonWidgetQuerySelectors.productGridResults);
    this.productGridResultsContainer = document.querySelector(comparisonWidgetQuerySelectors.productGridResultsContainer);
    this.comparisonResults = document.querySelector(comparisonWidgetQuerySelectors.comparisonResults);
    this.comparisonResultsTableHead = document.querySelector(comparisonWidgetQuerySelectors.comparisonResultsTableHead);
    this.comparisonResultsTableBody = document.querySelector(comparisonWidgetQuerySelectors.comparisonResultsTableBody);
    this.errorModal = document.querySelector(comparisonWidgetQuerySelectors.errorModal);
    this.minItemsModal = document.querySelector(comparisonWidgetQuerySelectors.minItemsModal);
    this.maxItemsModal = document.querySelector(comparisonWidgetQuerySelectors.maxItemsModal);
    this.loader = document.querySelector(comparisonWidgetQuerySelectors.loader);
    this.goBackButton = document.querySelector(comparisonWidgetQuerySelectors.goBackButton);
    this.compareDTTool = document.querySelector(comparisonWidgetQuerySelectors.compareDTTool);

    this.mustacheHeading = document.getElementById(comparisonMustacheElements.mustacheTemplate.heading);
    this.mustacheBody = document.getElementById(comparisonMustacheElements.mustacheTemplate.body);

    this.initializeFromSessionStorage();
    this.convertrowtocolumn();
    this.addEventListeners();
  }

// Mustache
  renderComparisonTableHeading(headerData) {
    return window.Mustache.render(this.mustacheHeading.innerHTML, headerData);
  }
  renderComparisonTableBody(bodyData) {
    return window.Mustache.render(this.mustacheBody.innerHTML, bodyData);
  }

  addEventListeners() {
    this.minimizeMaximizeComparisonWidget();
    this.checkboxAddRemove();
    this.comparisonWidgetRemoveButton();
    this.clearAllItems();
    this.displayComparisonResultsScreen();
    this.goBack();
    this.setSessionStorageVar();
    this.comparisonResultsScreenRemoveButton();
    this.highlightDifferencesAndHideBlankFields();
  }

// event listeners
  minimizeMaximizeComparisonWidget() {
    document.querySelector('.comparision .min__close').addEventListener(eventListeners.CLICK, (event) => {
      document.querySelector('.comparision .main__box').classList.toggle(comparisonWidgetClasses.hide);
      document.querySelector('.comp__down').classList.toggle('icon-chevron-up');
      document.querySelector('.comp__down').classList.toggle('icon-chevron-down');
    });
  }

  checkboxAddRemove() {
    let self = this;

    // Add and remove items via the checkboxes on the main page (listener for each checkbox)
    let compareCheckbox = document.querySelectorAll('input[type=checkbox].product-card-sku__comp');
    for (let i = 0; i < compareCheckbox.length; i++) {
      compareCheckbox[i].addEventListener('click', function (event) {

        let checkid = $(this).attr('id');
        let checkidElement = document.getElementById(checkid);
        let checklen = self.iduncheck.length;
        // if a box gets unchecked
        if (!checkidElement.checked) {
          self.removeItemFromCompare(checkid);
        }
        // else if an item is being selected to add
        else if (checkidElement.checked && checklen <= 3) {
          self.addItemToCompare(checkid);
          self.comparisonWidget.classList.remove(comparisonWidgetClasses.hide);
          self.comparisonWidget.style.width = self.calculateWidthWithoutPadding();
          document.querySelector('.main__comp-box').classList.remove(comparisonWidgetClasses.hide);
        }
        // if more than 4 items are being selected, show error modal to user
        else if (checklen >= 3) {
          checkidElement.checked = false;
          $(self.maxItemsModal).modal('show');
        }


        // Enable or disable Compare button & update the count in the html
        self.disabledButton(self.iduncheck.length);
        self.comparisonWidgetItemCountDiv.innerHTML = self.iduncheck.length;
      });
    }
  }

  comparisonWidgetRemoveButton() {
    let self = this;
    // Remove an item from the comparison widget by clicking its "close" button
    $('.comp__close').click(function () {
      let itemToRemove = $(this).prev().text();
      self.removeItemFromCompare(itemToRemove);

      // uncheck the itemToRemove's box on main page, if it is visible
      if (typeof (document.getElementById(itemToRemove)) !== 'undefined' && document.getElementById(itemToRemove) !== null) {
        document.getElementById(itemToRemove).checked = false;
      }
      // Enable or disable Compare button & update the count in the html
      self.disabledButton(self.iduncheck.length);
      self.comparisonWidgetItemCountDiv.innerHTML = self.iduncheck.length;
      if (self.comparisonWidgetItemCountDiv.innerHTML === 0) {
        self.compareDTTool.innerHTML = '';
        self.comparisonWidget.classList.add(comparisonWidgetClasses.hide);
        $('#product1 tr, #product1 td,#product2 td, #product2 tr, #product3 tr, #product3 td,#product4 tr,#product4 td').remove();

      }
    });
  }

  clearAllItems() {
    let self = this;
    $('.clear-selection').click(function () {
      $('.comparision__box').addClass('comp__closed');
      self.comparisonWidgetItemLabel.forEach(element => {
        element.innerHTML = '';
      });
      $('.comp__close').removeClass('icon-close');
      $('.results-list__content input:checkbox').prop('checked', false);
      self.counter = 0;
      self.iduncheck = [];
      self.comparisonWidgetItemCountDiv.innerHTML = self.counter;
      self.compareDTTool.innerHTML = '';
      self.comparisonWidget.classList.add(comparisonWidgetClasses.hide);
      $('#product1 tr, #product1 td,#product2 td, #product2 tr, #product3 tr, #product3 td,#product4 tr,#product4 td').remove();
    });
  }

  displayComparisonResultsScreen() {
    // Display comparison screen, button is only active after two or more items to compare are selected
    let self = this;
    self.compareButton.addEventListener(eventListeners.CLICK, (event) => {
      // build data to send. Example:  C37,C17,C17 SA,C27
      let selectedSkus = '';
      for (let index = 0; index < self.iduncheck.length; index++) {
        selectedSkus += self.iduncheck[index];
        if (index + 1 < self.iduncheck.length) {
          selectedSkus += ',';
        }
      }
      self.compareDTTool.innerHTML = selectedSkus;
      self.loader.classList.remove(comparisonWidgetClasses.hide);
      const tabsMenuDiv = document.querySelector('.eaton-product-tabs');
      if (tabsMenuDiv) {
        tabsMenuDiv.scrollIntoView({
          behavior: 'smooth'
        });
      } else {
        $('html, body').animate({ scrollTop: 0 });
      }
      self.productCompare(selectedSkus);
      self.comparisonResults.classList.remove(comparisonWidgetClasses.hide);
      self.productGridResults.classList.add(comparisonWidgetClasses.hide);
    });
  }

  goBack() {
    // Go back to product list, from comparison screen
    let self = this;

    self.goBackButton.addEventListener(eventListeners.CLICK, (event) => {
      $('#header-fixed').empty();
      self.storeProdxSelection($('#tableheadComp #heading th p.header_align'));
      self.comparisonResults.classList.add(comparisonWidgetClasses.hide);
      self.comparisonResultsTableHead.innerHTML = '';
      self.comparisonResultsTableBody.innerHTML = '';
      self.loader.classList.add(comparisonWidgetClasses.loaderActive);
      self.loader.classList.remove(comparisonWidgetClasses.hide);
      setTimeout(() => {
        self.loader.classList.remove(comparisonWidgetClasses.loaderActive);
        self.loader.classList.add(comparisonWidgetClasses.hide);
        self.productGridResults.classList.remove(comparisonWidgetClasses.hide);
        self.comparisonWidget.classList.remove(comparisonWidgetClasses.hide);
        self.comparisonWidget.style.width = self.calculateWidthWithoutPadding();
        document.querySelector('.main__comp-box').classList.remove(comparisonWidgetClasses.hide);
      }, '2000');
    });
  }

  setSessionStorageVar() {
  // On click of Sort dropdown ("Alpha: A to Z"), set Session Storage variable "selectedProdx" to store the products
  //    that are currently in the comparison widget.  See product-grid.js for use of "selectedProdx"
    let self = this;
    let sortLinks = document.querySelectorAll('.faceted-navigation-header__sort-link');
    for (let i = 0; i < sortLinks.length; i++) {
      sortLinks[i].addEventListener('click', function (event) {
        self.storeProdxSelection($(self.comparisonWidgetItemLabel));
      });
    }
  }

  comparisonResultsScreenRemoveButton() {
    let self = this;

  // On click of "close" button on a product in the comparison table, remove it from the comparison results table,
  //    the comparison widget, this.iduncheck, and #compare__DTtool.
    $('#heading, #header-fixed > thead').on('click', '.popup_close ', function () {
      if ($('#tableheadComp .popup_close').length === 2) {
        $(self.minItemsModal).modal('show');
      } else {
        let xSku = $(this).parent().siblings('.product-sku-url').children('.header_align').text();

        self.removeItemFromCompare(xSku);
        // uncheck the itemToRemove's box on main page, if it is visible
        if (typeof (document.getElementById(xSku)) !== 'undefined' && document.getElementById(xSku) !== null) {
          document.getElementById(xSku).checked = false;
        }
        // Enable or disable Compare button & update the count in the html
        self.disabledButton(self.iduncheck.length);
        self.comparisonWidgetItemCountDiv.innerHTML = self.iduncheck.length;

        let selectedSkus = $('#tableheadComp .header_align').map(function () {
          return $(this).text();
        }).get().join(',') + ',';
        selectedSkus = selectedSkus.replace(xSku + ',', '');  // example: remove "C27," from "C17,C17 SA,C27,"
        selectedSkus = selectedSkus.replace(/,\s*$/, '');  // remove final comma after element removal
        self.compareDTTool.innerHTML = selectedSkus;
        $('#header-fixed').empty();
        self.loader.classList.add(comparisonWidgetClasses.loaderActive);
        self.loader.classList.remove(comparisonWidgetClasses.hide);
        self.comparisonResultsTableHead.innerHTML = '';
        self.comparisonResultsTableBody.innerHTML = '';
        self.productCompare(selectedSkus);
      }
    });
  }

  highlightDifferencesAndHideBlankFields() {
  // Functionality for the three checkboxes in the comparison results table
    // For filter checkbox 1:  When checked by the user, highlight in blue the rows that have different aspects, between products.
    $('#tableheadComp #heading').on('click', '.comparision_table-checkbox', function () {
      if ($('#tableheadComp > thead .comparision_table-checkbox').is(':checked')) {
        $('.all-different').addClass('highlight');
        $('#header-fixed > thead .comparision_table-checkbox').prop('checked', true);
      } else {
        $('.all-different').removeClass('highlight');
        $('#header-fixed > thead .comparision_table-checkbox').prop('checked', false);
      }
    });

    // For filter checkbox 2:  When checked by the user, only show the rows that have different aspects, between products.
    $('#tableheadComp #heading').on('click', '.comparision_table-checkbox-diff', function () {
      if ($('#tableheadComp > thead .comparision_table-checkbox-diff').is(':checked')) {
        $('#header-fixed > thead .comparision_table-checkbox-diff').prop('checked', true);
        $('.comparision_table-row').addClass('hide');
        $('.comparision_table-row.all-different').removeClass('hide');
      } else {
        $('.comparision_table-row').removeClass('hide');
        if ($('#tableheadComp > thead .comparision_table-checkbox-incomprbl').is(':checked')) {
          $('tbody td').filter(function () {
            return $(this).text() === '-';
          }).parent().addClass('hide');
        }
        $('#header-fixed > thead .comparision_table-checkbox-diff').prop('checked', false);
      }
    });

    // For filter checkbox 3:
    $('#tableheadComp #heading').on('click', '.comparision_table-checkbox-incomprbl', function () {
      if ($('#tableheadComp > thead .comparision_table-checkbox-incomprbl').is(':checked')) {
        document.querySelectorAll(comparisonWidgetClasses.hideForBlank).display = 'none';
      } else {
        document.querySelectorAll(comparisonWidgetClasses.hideForBlank).display = 'block';
      }
    });
  }


// helper functions
  initializeFromSessionStorage() {
    if (sessionStorage.getItem('selectedProdx') !== '' && sessionStorage.getItem('selectedProdx') !== null) {
      let storageItms = sessionStorage.getItem('selectedProdx');
      let storageItmsArr = storageItms.split(',');
      storageItmsArr = storageItmsArr.filter(String);
      for (let n = 0; n < storageItmsArr.length; n++) {
        let checkidElement = document.getElementById(storageItmsArr[n]);

        // if it's visible check it
        if (typeof (checkidElement) !== 'undefined' && checkidElement !== null) {
          checkidElement.checked = true;
        }
        this.addItemToCompare(storageItmsArr[n]);
        this.comparisonWidget.classList.remove(comparisonWidgetClasses.hide);
        this.comparisonWidget.style.width = this.calculateWidthWithoutPadding();
        document.querySelector('.main__comp-box').classList.remove(comparisonWidgetClasses.hide);

      }
      // Enable or disable Compare button & update the count in the html
      this.disabledButton(this.iduncheck.length);
      this.comparisonWidgetItemCountDiv.innerHTML = this.iduncheck.length;
      sessionStorage.removeItem('selectedProdx');
    }
  }

  calculateWidthWithoutPadding() {
    let containerWidth = parseFloat(getComputedStyle(this.productGridResultsContainer).width);
    let containerTotalPadding = parseFloat(getComputedStyle(this.productGridResultsContainer).paddingLeft) + parseFloat(getComputedStyle(this.productGridResultsContainer).paddingRight);
    let finalWidth = containerWidth - containerTotalPadding;
    return finalWidth + 'px';
  }

  addItemToCompare(checkid) {
    // if not already in array of "selected" items, add
    if (this.iduncheck.includes(checkid) === false) {
      this.iduncheck.push(checkid);
    }
    this.populateWithCurrentArrayOfItems(this.iduncheck);
  }

  removeItemFromCompare(skuToRemove) {
    // remove from current array of items
    let index = this.iduncheck.indexOf(skuToRemove);
    this.iduncheck.splice(index, 1);

    // empty all and repopulate w current array of items
    this.comparisonWidgetItemLabel.forEach(element => {
      element.innerHTML = '';
      element.parentNode.classList.add('comp__closed');
      element.nextElementSibling.classList.remove('icon-close');
    });
    this.populateWithCurrentArrayOfItems(this.iduncheck);
  }

  populateWithCurrentArrayOfItems(iduncheck) {
    for (let index = 0; index < this.iduncheck.length; index++) {
      let positionElement = document.getElementById(index + 1);
      if (positionElement.textContent === '') {
        positionElement.innerHTML = this.iduncheck[index];
        positionElement.parentNode.classList.remove('comp__closed');
        positionElement.nextElementSibling.classList.add('icon-close');
      }
    }
  }

  disabledButton(itemCount) {
    // if zero or one item in comparison widget, disable Compare button. Else, enable it.
    if (itemCount <= 1) {
      this.compareButton.classList.add(comparisonWidgetClasses.compareBasketButtonDisabled);
    } else {
      this.compareButton.classList.remove(comparisonWidgetClasses.compareBasketButtonDisabled);
    }
  }

  productCompare(selectedSkus) {
    let self = this;
    $.ajax({
      type: 'POST',
      url: '/eaton/productcompare',
      data: { value1: selectedSkus },
      success: function (msg) {
        self.compareResult = msg;
        if (self.compareResult.length === 0) {
          self.showErrorModal();
        } else {
          self.dataDisplayNew();
          self.compareTableScroll();
          self.multiLine();
          $('.comparision_table-pdh').attr('colspan', $('#tableheadComp #heading tr').children().length);
          self.loader.classList.remove(comparisonWidgetClasses.loaderActive);
          self.loader.classList.add(comparisonWidgetClasses.hide);
        }
      }
    });
  }

  showErrorModal() {
    this.loader.classList.remove(comparisonWidgetClasses.loaderActive);
    this.loader.classList.add(comparisonWidgetClasses.hide);
    $(this.errorModal).modal('show');
    this.comparisonResults.classList.add(comparisonWidgetClasses.hide);
    this.productGridResults.classList.remove(comparisonWidgetClasses.hide);
    this.clearSelection.click();
  }

  constructCompareResultItems() {
    let compareResultItems = [];
    this.compareResult.forEach((item, index) => {
      compareResultItems.push({col: index + 1, url: item['ProductSku Url']['ProductSku Url'] + '.' + item[this.getTRKey(item, 'Catalog Number')] + '.html', image: item.Image.Image, catalogNumber: item[this.getTRKey(item, 'Catalog Number')] });
    });
    return compareResultItems;
  }

  constructCompareResultBodyItems() {
    let compareResultItems = [];
    this.compareResult.forEach((item, index) => {
      compareResultItems.push({productName: item[this.getTRKey(item, 'Product Name')], catalogNumber: item[this.getTRKey(item, 'Catalog Number')], uniqueCategories: []});
    });
    return compareResultItems;
  }

  dataDisplayNew() {
    let catList = [];
    let atrList = [];
    let noOfAttribute = 0;

    let showDiffIDVar = document.getElementById('showDiffId').textContent;
    let showHighLightIDVar = document.getElementById('showHighLightId').textContent;
    let hideIncompAttr = document.getElementById('hideIncompAttrId').textContent;

    let headerData = {
      showDiffIDVar: showDiffIDVar,
      showHighLightIDVar: showHighLightIDVar,
      hideIncompAttr: hideIncompAttr,
      compareResultItems: this.constructCompareResultItems()
    };
    this.comparisonResultsTableHead.innerHTML = this.renderComparisonTableHeading(headerData);


    let productNameRow = this.getTRKey(this.compareResult[0], 'Product Name');
    let productNameLabel = this.getTRLabel(productNameRow, 'label');
    let catalogNumberRow = this.getTRKey(this.compareResult[0], 'Catalog Number');
    let catalogNumberLabel = this.getTRLabel(catalogNumberRow, 'label');

    let bodyData = {
      productNameRowTitle: productNameLabel,
      catalogNumberRowTitle: catalogNumberLabel,
      allUniqueCategories: [],
      compareResultItems: this.constructCompareResultBodyItems(),
      catRowColspan: this.compareResult.length + 1
    };

    for (let i = 0; i < this.compareResult.length; i++) {
      $.each(this.compareResult[i], function (key, value) {
        if (key.includes('Categories_') || key.includes('categories_')) {
          catList.push(key);
          $.each(value, function (atrkey, atrvalue) {
            atrList.push(atrkey);
          });
        }
      });
    }

    // Remove Duplicate cat list.
    // eslint-disable-next-line consistent-this
    let NewUniqueCatList = catList.filter(function (element, index, self) {
      return index === self.indexOf(element);
    });

    // Bring General specifications row to top.
    let first = this.getTRKey(this.compareResult[0], 'General specifications');
    NewUniqueCatList.sort(function (x, y) { return x === first ? -1 : y === first ? 1 : 0; });

    // eslint-disable-next-line no-unused-vars, consistent-this
    let NewUniqueAtrList = atrList.filter(function (element, index, self) {
      return index === self.indexOf(element);
    });

    // Sorting of atribute list.
    for (let i = 0; i < NewUniqueCatList.length; i++) {
      let tempCatName = NewUniqueCatList[i].replace('Categories_', '');
      tempCatName = this.getTRLabel(tempCatName, 'lable');
      let tempCatId = this.getTRLabel(tempCatName, 'id');

      // Store Attribute rows starting with Unique Categories, then Subcategories.
      for (let j = 0; j < this.compareResult.length; j++) {
        bodyData.compareResultItems[j].uniqueCategories.push({tempCatId: tempCatId.toLowerCase(), tempCatName: tempCatName, subcategories: []});

        if (j === 0) {
          bodyData.allUniqueCategories.push({tempCatId: tempCatId.toLowerCase(), tempCatName: tempCatName, allSubcategories: []});
        }

        // eslint-disable-next-line no-unused-vars
        let atrAvailFlag = 0;
        for (let key of Object.keys(this.compareResult[j])) {
          if (key.includes('Categories_') && key === NewUniqueCatList[i] && (typeof this.compareResult[j][NewUniqueCatList[i]] !== 'undefined')) {
            // if category match then print attribute label.
            for (let key of Object.keys(this.compareResult[j][NewUniqueCatList[i]])) {
              let rowId = tempCatName + '-' + noOfAttribute;
              rowId = rowId.replace(/ /g,'-');
              let dataIDKey = this.getTRLabel(key, 'id');
              let dataLabelKey = this.getTRLabel(key, 'lable');
              noOfAttribute++;
              bodyData.allUniqueCategories[i].allSubcategories.push({rowId: rowId, dataIDKey: dataIDKey, dataLabelKey: dataLabelKey, productValues: []});

              // Store Subcategory values for each product
              // Check if category exists.
              let tempVar = (typeof this.compareResult[j][NewUniqueCatList[i]] !== 'undefined') ? this.compareResult[j][NewUniqueCatList[i]][key] : 'undefined';
              if (tempVar !== undefined || typeof tempVar !== 'undefined' || tempVar !== 'undefined') {
                // condition for Runtime graph & its URL.
                bodyData.compareResultItems[j].uniqueCategories[i].subcategories.push({rowId: rowId, dataIDKey: dataIDKey, dataLabelKey: dataLabelKey, dataValue: tempVar});
              } else {
                  // if nothing then undefined.
                bodyData.compareResultItems[j].uniqueCategories[i].subcategories.push({rowId: rowId, dataIDKey: dataIDKey, dataLabelKey: dataLabelKey, dataValue: '-'});
              }
            }

          }
        } // end for
        atrAvailFlag++;
      }
    }

    // Having constructed a list of all categories and all subcategories nested for the template,
    //   place the values of compareResultItems into the bodyData json for use in the Mustache template columns.
    for (let a = 0; a < bodyData.allUniqueCategories.length; a++) {
      for (let b = 0; b < bodyData.allUniqueCategories[a].allSubcategories.length; b++) {
        // if dataLabelKey of the current product equals the dataLabelKey of the current subcategory
        let currentSubcategoryDataLabelKey = bodyData.allUniqueCategories[a].allSubcategories[b].dataLabelKey;

        bodyData.compareResultItems.forEach(resultItem => {
          resultItem.uniqueCategories.forEach(resultUniqueCategory => {
            resultUniqueCategory.subcategories.forEach(resultSubcategory => {
              if (resultSubcategory.dataLabelKey === currentSubcategoryDataLabelKey) {
                // if product's subcategory datalabelkey = allSubcategories datalabelkey, push on the key and the value, for display.
                bodyData.allUniqueCategories[a].allSubcategories[b].productValues.push({dataLabelKey: currentSubcategoryDataLabelKey, dataValue: resultSubcategory.dataValue});
              }
            });
          });
        });
      }
    }

    this.comparisonResultsTableBody.innerHTML = this.renderComparisonTableBody(bodyData);

    let seen = {};
    $('.attribute-key').each(function () {
      let txt = $(this).text() + '-cat-' + $(this).parent('tr').attr('data-cat');
      if (seen[txt]) { $(this).parent('tr').remove(); }
      else { seen[txt] = true; }
    });

    let runtimeGraphTitle = [];
    let runtimeGraphURL = [];
    let tempVal = '';

    $('.attribute-key-value').each(function () {
      // set url for Runtime graph.
      if ($(this).attr('data-key') === 'runtime graph') {
        tempVal = $(this).text();
        runtimeGraphTitle.push(tempVal);
      } else {
        if ($(this).attr('data-key') === 'runtime graph url') {
          tempVal = $(this).text();
          runtimeGraphURL.push(tempVal);
        }
      }
      tempVal = '';
    });

    // Set Runtime graph URL.
    if (runtimeGraphTitle.length === runtimeGraphURL.length) {
      let rCounter = 0;
      $('.attribute-key-value[data-key="runtime graph"]').each(function () {
        if ($(this).text() === '-') {
          // Do nothing
        } else {
          $(this).html('<a target="_blank" href="' + runtimeGraphURL[rCounter] + '">' + runtimeGraphTitle[rCounter] + '</a>');
        }
        rCounter++;
      });
    }

    // Remove row from table - Runtime Graph URL, Product name & Catalog number which is under category
    $('tr.comparision_table-row[data-key="runtime graph url"]').remove();
    $('tr.attribute-row[data-key="product name"]').remove();
    $('tr.attribute-row[data-key="catalog number"]').remove();

    $('tr.category-row[data-cat="defaulttaxonomyattributelabel"]').children('td').html('Product specifications');

    $('.category-row').each(function () {
      let catDataCount = 0;
      let catName = $(this).attr('data-cat');
      $('.attribute-row').each(function () {
        // If category row and attribute mach then data exists for category.
        if (catName === $(this).attr('data-cat')) {
          catDataCount++;
        }
      });
      if (catDataCount === 0) {
        $(this).remove();
      }
    });

    // replace all undefine value with dash (undefine: no attribute for that product.)
    $('.attribute-key-value').each(function () {
      if ($(this).text() === 'undefined') {
        $(this).text('-');
      }
    });

    // Merger code.
    if (this.compareResult.length === 2) {
      $('.comparision_table-row').each(function () {
        if ($(this).find('td:eq(1)').text() === $(this).find('td:eq(2)').text() && $(this).find('td:eq(1)').attr('data-key') !== 'runtime graph') {
          $(this).find('td:eq(2)').remove();
          $(this).find('td:eq(1)').attr('colspan', 2);
        } else {
          // Add class for all diffent values.
          $(this).addClass('all-different');
        }
      });
    } else {
      if (this.compareResult.length === 3) {
        $('.comparision_table-row').each(function () {
          // if 1,2 & 3 are same
          if ($(this).find('td:eq(1)').text() === $(this).find('td:eq(2)').text() && $(this).find('td:eq(1)').text() === $(this).find('td:eq(3)').text() && $(this).find('td:eq(1)').attr('data-key') !== 'runtime graph') {
            $(this).find('td:eq(3)').remove();
            $(this).find('td:eq(2)').remove();
            $(this).find('td:eq(1)').attr('colspan', 3);
          } else {
            // if 1 and 2 are same.
            if ($(this).find('td:eq(1)').text() === $(this).find('td:eq(2)').text() && $(this).find('td:eq(1)').attr('data-key') !== 'runtime graph') {
              $(this).find('td:eq(2)').remove();
              $(this).find('td:eq(1)').attr('colspan', 2);
            } else {
              // if 2 and 3 same
              if ($(this).find('td:eq(2)').text() === $(this).find('td:eq(3)').text() && $(this).find('td:eq(2)').attr('data-key') !== 'runtime graph') {
                $(this).find('td:eq(3)').remove();
                $(this).find('td:eq(2)').attr('colspan', 2);
              } else {
                // do nothing.
                $(this).addClass('all-different');
              }
            }
          }
        });
      } else {
        if (this.compareResult.length === 4) {
          $('.comparision_table-row').each(function () {
            // if 1,2, 3 & 4 are same
            if ($(this).find('td:eq(1)').text() === $(this).find('td:eq(2)').text() && $(this).find('td:eq(1)').text() === $(this).find('td:eq(3)').text() && $(this).find('td:eq(1)').text() === $(this).find('td:eq(4)').text() && $(this).find('td:eq(1)').attr('data-key') !== 'runtime graph') {
              $(this).find('td:eq(4)').remove();
              $(this).find('td:eq(3)').remove();
              $(this).find('td:eq(2)').remove();
              $(this).find('td:eq(1)').attr('colspan', 4);
            } else {
              // if 1,2 & 3 are same
              if ($(this).find('td:eq(1)').text() === $(this).find('td:eq(2)').text() && $(this).find('td:eq(1)').text() === $(this).find('td:eq(3)').text() && $(this).find('td:eq(1)').attr('data-key') !== 'runtime graph') {
                $(this).find('td:eq(3)').remove();
                $(this).find('td:eq(2)').remove();
                $(this).find('td:eq(1)').attr('colspan', 3);
              } else {
                // if 2,3 & 4 are same
                if ($(this).find('td:eq(2)').text() === $(this).find('td:eq(3)').text() && $(this).find('td:eq(2)').text() === $(this).find('td:eq(4)').text() && $(this).find('td:eq(1)').attr('data-key') !== 'runtime graph') {
                  $(this).find('td:eq(4)').remove();
                  $(this).find('td:eq(3)').remove();
                  $(this).find('td:eq(2)').attr('colspan', 3);
                } else {
                  // if 1 and 2 are same.
                  if ($(this).find('td:eq(1)').text() === $(this).find('td:eq(2)').text() && $(this).find('td:eq(1)').attr('data-key') !== 'runtime graph') {
                    $(this).find('td:eq(2)').remove();
                    $(this).find('td:eq(1)').attr('colspan', 2);
                  } else {
                    // if 2 and 3 same
                    if ($(this).find('td:eq(2)').text() === $(this).find('td:eq(3)').text() && $(this).find('td:eq(2)').attr('data-key') !== 'runtime graph') {
                      $(this).find('td:eq(3)').remove();
                      $(this).find('td:eq(2)').attr('colspan', 2);
                    } else {
                      // if 3 and 4 same
                      if ($(this).find('td:eq(3)').text() === $(this).find('td:eq(4)').text() && $(this).find('td:eq(3)').attr('data-key') !== 'runtime graph') {
                        $(this).find('td:eq(4)').remove();
                        $(this).find('td:eq(3)').attr('colspan', 2);
                      } else {
                        $(this).addClass('all-different');
                      }
                    }
                  }
                }
              }
            }
          });
        }
      }
    }

    if (this.comparisonWidgetItemCountDiv.textContent.trim() === '2') {
      $('.heading-prd-cmp').addClass('col-sm-4');
      $('.hide--mob-td').addClass('col-sm-4');
    } else if (this.comparisonWidgetItemCountDiv.textContent.trim() === '3') {
      $('.heading-prd-cmp').addClass('col-sm-3');
      $('.hide--mob-td').addClass('col-sm-3');
    } else if (this.comparisonWidgetItemCountDiv.textContent.trim() === '4') {
      $('.heading-prd-cmp').addClass('col-sm-2');
      $('.hide--mob-td').addClass('col-sm-2');
    }

    $('.displayNone').remove();
    $('.dataTables_sizing').addClass('hide');
    $('.comparision_table-pdh').attr('colspan', document.querySelector('#tableheadComp #heading tr').childElementCount);
    $('.runtime-graph').parent('tr').addClass('skipMerge');
    $('.skipMerge').children('td').removeAttr('colspan');

    $('.displayNone').remove();
    $('.dataTables_sizing').addClass('hide');
    $('.comparision_table-pdh').attr('colspan', document.querySelector('#tableheadComp #heading tr').childElementCount);
    $('.runtime-graph').parent('tr').addClass('skipMerge');
    $('.skipMerge').children('td').removeAttr('colspan');


    /**
     * Setting filters classes.
     * 1. class: all-differnce - for all diffent data.
     * 2. class hide-for-blank - for blank data depend on no of product.
     */
    let dashCount = 0;
    let dashCountForMerge = 0;
    let compareDTToolText = this.compareDTTool.textContent; // determine number of items without using this.compareResult
    let columns = compareDTToolText.split(',');

    $('.attribute-row').each(function () {

      if ($(this).children('.attribute-key-value').length > 1) {
        $(this).addClass('all-different');
      } else {
        $(this).removeClass('all-different');
        $(this).removeClass('highlight');
      }

      // For 2 column.
      if (columns.length === 2) {
        dashCount = 0;
        $(this).children('.attribute-key-value').each(function() {
          if ($(this).text() === '-') {
            dashCount++;
          }
        });
        if (dashCount === 1) {
          $(this).addClass(comparisonWidgetClasses.hideForBlank);
        } else {
          $(this).removeClass(comparisonWidgetClasses.hideForBlank);
        }
      }

      // For 3 column.
      if (columns.length === 3) {
        dashCount = 0;
        dashCountForMerge = 0;
        $(this).children('.attribute-key-value').each(function() {
          if ($(this).text() === '-') {
            dashCount++;
          }
          if ($(this).text() === '-' && $(this).attr('colspan') === '2') {
            dashCountForMerge = 1;
          }
        });

        // For 2 dash and 1 data.
        if (dashCount === 2) {
          $(this).addClass(comparisonWidgetClasses.hideForBlank);
        } else {
          // For 1 dash and 1 data i.e. 2 merge column & 1 column.
          if (dashCount === 1 && dashCountForMerge === 1) {
            $(this).addClass(comparisonWidgetClasses.hideForBlank);
          } else {
            $(this).removeClass(comparisonWidgetClasses.hideForBlank);
          }
        }
      }

      // For 4 column.
      if (columns.length === 4) {
        dashCount = 0;
        dashCountForMerge = 0;
        $(this).children('.attribute-key-value').each(function() {
          if ($(this).text() === '-') {
            dashCount++;
          }
          if ($(this).text() === '-' && $(this).attr('colspan') === '3') {
            dashCountForMerge = 1;
          }
          if ($(this).text() === '-' && $(this).attr('colspan') === '2') {
            dashCountForMerge = 2;
          }
        });

        // For 3 dash and 1 data.
        if (dashCount === 1 && dashCountForMerge === 1) {
          $(this).addClass(comparisonWidgetClasses.hideForBlank);
        } else {
          // For 2 dash and 1 data i.e. 2 merge dash + 1 data + 1 dash.
          if (dashCount === 2 && dashCountForMerge === 2) {
            $(this).addClass(comparisonWidgetClasses.hideForBlank);
          } else {
            $(this).removeClass(comparisonWidgetClasses.hideForBlank);
          }
        }
      }

    });
  }


  /* Original helper functions */

  convertrowtocolumn() {
    $('.comparision__table-result').each(function () {
      let $this = $(this);
      let newrows = [];
      $this.find('.comparision__table--row').each(function () {
        let i = 0;
        $(this).find('.comparision-data').each(function () {
          i++;
          if (newrows[i] === undefined) { newrows[i] = $('<tr></tr>'); }
          newrows[i].append($(this));
        });
      });
      $this.find('.comparision__table--row').remove();
      $.each(newrows, function () {
        $this.append(this);
      });
    });
  }

  /**
   * Funtion to get translated key in JSON.
   */
  getTRKey(tempArr, trKey) {
    let tempKeyArr = Object.keys(tempArr);
    for (let i = 0; i < tempKeyArr.length; i++) {
      if (tempKeyArr[i].includes(trKey)) {
        return tempKeyArr[i];
      }
    }
  }

  /**
   * Funtion to get translated label in JSON.
   */
  getTRLabel(tempStr, tempWhat) {
    let tempStrArr = [];
    let keyString = String(tempStr);

    // if unique key not exists.
    if (keyString.includes('_Pr@ductC@mpT@R_') && keyString !== 'undefined') {
      tempStrArr = keyString.split('_Pr@ductC@mpT@R_');
    } else {
      tempStrArr[0] = keyString;
      tempStrArr[1] = keyString;
    }

    // check unique key and split.
    if ((tempWhat === 'lable' || tempWhat === 'label') && keyString !== 'undefined') {
      // return label value
      keyString = tempStrArr[1];
    } else {
      if (tempWhat === 'id' && keyString !== 'undefined') {
        // return id value
        keyString = tempStrArr[0].toLowerCase();
      }
    }

    keyString = keyString.replace(/_+/g, ' ');
    return keyString;
  }

  multiLine() {
    $('td.comparision-data.comparision_table-data').each(function () {
      let cellTxt = $(this).text();
      if (cellTxt.includes('|')) {
        let splitxt = $(this).text().split('|');
        let txt2 = '';
        $.each(splitxt, function (key, value) {
          txt2 += '<p>' + value + '</p>';
        });
        $(this).empty();
        $(this).append(txt2);
      }
    });
  }

  compareTableScroll() {
    let $header = $('#tableheadComp > thead').clone();
    let $fixedHeader = $('#header-fixed').append($header);
    let bottm = $('#tableheadComp > tbody tr:last-child').offset().top - $('.header-primary-nav').outerHeight() - $('.header-utility-nav').outerHeight() - $('#tableheadComp > tbody tr:last-child').outerHeight();

    $('#header-fixed > thead .comparision_table-checkbox-diff').change(function () {
      $('#tableheadComp thead .comparision_table-checkbox-diff').click();

      if ($('#header-fixed .comparision_table-checkbox-diff').is(':checked')) {
        $('#tableheadComp > thead .comparision_table-checkbox-diff').prop('checked', true);
      } else {
        $('#tableheadComp > thead .comparision_table-checkbox-diff').prop('checked', false);
      }

    });

    $('#header-fixed > thead .comparision_table-checkbox').change(function () {
      $('#tableheadComp thead .comparision_table-checkbox').click();

      if ($('#header-fixed .comparision_table-checkbox').is(':checked')) {
        $('#tableheadComp > thead .comparision_table-checkbox').prop('checked', true);
      } else {
        $('#tableheadComp > thead .comparision_table-checkbox').prop('checked', false);
      }

    });

    $('#header-fixed > thead .comparision_table-checkbox-incomprbl').change(function () {
      $('#tableheadComp thead .comparision_table-checkbox-incomprbl').click();

      if ($('#header-fixed .comparision_table-checkbox-incomprbl').is(':checked')) {
        $('#tableheadComp > thead .comparision_table-checkbox-incomprbl').prop('checked', true);
      } else {
        $('#tableheadComp > thead .comparision_table-checkbox-incomprbl').prop('checked', false);
      }

    });

    $('#header-fixed > thead').on('click', '.popup_close ', function () {
      let myIndex = $(this).parent().parent().prevAll().length;
      $('#tableheadComp thead tr').children().eq(myIndex).children('p').children('.popup_close').click();
    });

    function tableScroll() {
      let top = $('.header-primary-nav').outerHeight() + $('.header-utility-nav').height();
      $fixedHeader.css('top', top / 10 + 'rem');
      $fixedHeader.css('width', $('#tableheadComp').width());
      let topPos = $('#tableheadComp').offset().top - $('.header-primary-nav').outerHeight() + $('.header-utility-nav').outerHeight();
      let pos = $(document).scrollTop();
      let tcol = $('#tableheadComp thead tr').children().length;
      $('#heading th').css('width', 100 / tcol + '%');

      if (pos > topPos && pos < bottm) {
        $fixedHeader.show();
      } else {
        $fixedHeader.hide();
      }
    }

    $(window).scroll(function () {
      tableScroll();
    });

    $(document).ready(function () {
      setTimeout(function () {
        tableScroll();
        $(window).trigger('resize');
      }, 300);
    });
  }

  storeProdxSelection(el) {
    let prodxarr = [];
    el.each(function () {
      if ($(this).text()) {
        prodxarr.push($(this).text());
      }
    });
    sessionStorage.setItem('selectedProdx', prodxarr);
  }

}

document.addEventListener('DOMContentLoaded', function() {
  // eslint-disable-next-line no-unused-vars
  const comparisonDiv = document.querySelector('.comparision');
  if (comparisonDiv) {
    new Comparison();
  }
});

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  // eslint-disable-next-line no-global-assign
  $ = require('jquery');
  module.exports = Comparison;
}
