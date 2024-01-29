module.exports = {
  integratedPageMarkup : () => {
    return `
    <div class="container drilldown-selection-tool">

        <div class="dropdown-list" data-resource-path="/content/eaton/us/en-us/mypage/jcr:content/root/responsivegrid/tag-drilldown-tool">

            <div class="dropdown-list__dropdown">
                <label for="dropdown-0">Select the type of support you're looking for</label>
                <select name="dropdown-0" id="dropdown-0" data-index="0" data-dropdown-tag-path="/content/cq:tags/eaton/support-taxonomy">
                    <option disabled="" selected="">Please select</option>
                    <option data-option-label="option-" data-tag-path="/content/cq:tags/eaton/support-taxonomy/business-resources" data-title="Business resources">Business resources</option>
                    <option data-option-label="option-" data-tag-path="/content/cq:tags/eaton/support-taxonomy/category" data-title="Category">Category</option>
                </select>
            </div>

            <div class="dropdown-list__dropdown">
                <label hidden="" for="dropdown-1">Select the type of product</label>
                <select hidden="" name="dropdown-1" id="dropdown-1" data-index="1" data-dropdown-tag-path="/content/cq:tags/eaton/product-taxonomy"><option disabled="" selected="">Please select</option></select>
            </div>

            <script id="drilldown-selection-tool-next-dropdown-mustache" type="text/mustache">
 {{#.}}<option data-option-label="option-{{index}}" data-tag-path="{{tagPath}}" data-title="{{title}}">{{title}}</option>{{/.}}</script>

        </div>

        <div class="results-container">
            <div class="results-title hide">
                <span class="count"></span> Results
            </div>
            <div class="results-list"></div>
            <script id="drilldown-selection-tool-page-results-mustache" type="text/mustache">
            {{#resultsItemModelList}}
                <div class="results-item col-md-3">
                    <a class="results-item__link"
                        href="{{path}}"
                        target="_blank">
                        <img src="{{imagePath}}" />
                        <p class="results-item__title">{{title}}</p>
                    </a>
                    <div class="results-item__description">
                        {{{description}}}
                    </div>
                </div>
            {{/resultsItemModelList}}
            </script>
            <div class="results-footer" data-page-size="4">
                <div class="results-footer__load-more hide">
                    <button class="b-button b-button__primary b-button__primary--light" role="button" data-load-more="">
                        Load More
                    </button>
                </div>
                <div class="alert alert-warning results-footer__message hide" role="alert">
                    <p>No available options</p>
                </div>
                <div class="alert alert-danger results-footer__error hide" role="alert">
                </div>
                <div class="results-footer__load-spinner hide"></div>
            </div>

        </div>

    </div>
    `
  },
  drilldownOneNoResultsAjaxRequest : () => {
    return {
      async: true,
      data: {'nextDropdownIndex': 1, 'selectedDropdownOptionTags': '/content/cq:tags/eaton/support-taxonomy/business-resources'},
      error: expect.any(Function),
      success: expect.any(Function),
      type: 'GET',
      url: '/content/eaton/us/en-us/mypage/jcr:content/root/responsivegrid/tag-drilldown-tool.dropdown.nocache.json'
    };
  },
  drilldownOneNextDropdownAjaxRequest : () => {  //user selects Category, request to show next dropdown
    return {
      async: true,
      data: {'nextDropdownIndex': 1, 'selectedDropdownOptionTags': '/content/cq:tags/eaton/support-taxonomy/category'},
      error: expect.any(Function),
      success: expect.any(Function),
      type: 'GET',
      url: '/content/eaton/us/en-us/mypage/jcr:content/root/responsivegrid/tag-drilldown-tool.dropdown.nocache.json'
    }
  },
  drilldownTwoPageResultsAjaxRequest : () => {
    return {
      url: '/content/eaton/us/en-us/mypage/jcr:content/root/responsivegrid/tag-drilldown-tool.results.nocache.json',
      async: true,
      type: 'GET',
      data: {'selectedDropdownOptionTags': '/content/cq:tags/eaton/support-taxonomy/category|/content/cq:tags/eaton/product-taxonomy/clutches-brakes', 'startIndex': 0, 'pageSize': '4'},
      success: expect.any(Function),
      error: expect.any(Function)
    }
  },
  servletResponseDrilldownNoResults : () => {
    return undefined;
  },
  servletResponseDrilldownOne : () => {  //user selects Category, returns next tags
    return [{"tagPath":"/content/cq:tags/eaton/product-taxonomy/clutches-brakes","title":"Clutches and brakes"},{"tagPath":"/content/cq:tags/eaton/product-taxonomy/electrical-circuit-protection","title":"Electrical circuit protection"}];
  },
  servletResponsePageResultsOne : () => {
    return {
      "total": 5,
      "resultsItemModelList": [{
        "path": "/content/eaton/us/en-us/support/terms-conditions/wiring-devices-terms-and-conditions.html",
        "title": "Wiring Devices Terms and Conditions",
        "description": "",
        "imagePath": "/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/images/terms-conditions-wiring-devices.jpg"
      },
      {
        "path": "/content/eaton/us/en-us/support/warranty-returns/wiring-devices-warranty.html",
        "title": "Wiring Devices warranty",
        "description": "",
        "imagePath": "/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/images/warranty-wiring-devices.jpg"
      },
      {
        "path": "/content/eaton/us/en-us/locate/wiring-devices.html",
        "title": "Wiring devices product locator",
        "description": "",
        "imagePath": "/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/images/where-to-buy-wiring-devices.jpg"
      },
      {
        "path": "/content/eaton/us/en-us/test/wiring-devices.html",
        "title": "Wiring devices product locator",
        "description": "",
        "imagePath": "/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/images/where-to-buy-wiring-devices.jpg"
      }]
    };
  },
  loadMoreAjaxRequest : () => {
    return {
      url: '/content/eaton/us/en-us/mypage/jcr:content/root/responsivegrid/tag-drilldown-tool.results.nocache.json',
      async: true,
      type: 'GET',
      data: {'selectedDropdownOptionTags': '/content/cq:tags/eaton/support-taxonomy/category|/content/cq:tags/eaton/product-taxonomy/clutches-brakes', 'startIndex': 4, 'pageSize': '4'},
      success: expect.any(Function),
      error: expect.any(Function)
    }
  },
  servletResponseLoadMore : () => {
    return {
      "total": 5,
      "resultsItemModelList": [{
        "path": "/content/eaton/us/en-us/test/wiring-devices.html",
        "title": "Other wiring devices are here",
        "description": "",
        "imagePath": "/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/images/where-to-buy-wiring-devices.jpg"
      }]
    }
  }
};
