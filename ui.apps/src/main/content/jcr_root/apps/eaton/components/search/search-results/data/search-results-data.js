'use strict';
use(function () {

  var data = {};
  var testdata = this.testdata;

  // Default Data
  //--------------
  // if ((typeof(testdata) == "undefined")) {


  // Results Tabs
  //--------------
  data.tabs = {
    "items": [
      {
        "label": "Product",
        "id": "product",
        "isHiddenTab": false,
        "isActiveTab": false,
        "isADisabledTab": false,
        "resultsCount": 36,
        "url": "http://eaton.dev/results.tab.product.html",
        "target": "_self"
      },
      {
        "label": "News & insights",
        "id": "news-insights",
        "isHiddenTab": false,
        "isActiveTab": false,
        "isADisabledTab": false,
        "resultsCount": 97,
        "url": "http://eaton.dev/results.tab.news-insights.html",
        "target": "_self"
      },
      {
        "label": "Resources",
        "id": "resources",
        "isHiddenTab": false,
        "isActiveTab": false,
        "isADisabledTab": true,
        "resultsCount": 0,
        "url": "http://eaton.dev/results.tab.resources.html",
        "target": "_self"
      },
      {
        "label": "This Tab Should be hidden",
        "id": "resources",
        "isHiddenTab": true,
        "isActiveTab": false,
        "isADisabledTab": false,
        "resultsCount": 15,
        "url": "http://eaton.dev/results.tab.hidden.html",
        "target": "_self"
      }
    ]
  };


    // Facets Config
    //--------------
    data.facets = {
      "config": {
        "facetGroupsMinVisible": 4,
        "facetValuesMinVisible": 4
        // "facetGroupExpanded": 2
      },
      "facetLabel": "Narrow Results",
      "viewMoreLabel": "View More",
      "filtersLabel": "Filters",
      "closeFiltersLabel": "Close Filters",
      "applyLabel": "Done",
      "facetItems": [
        {
          "facetGroupName": "Content Type",
          "facetGroupId": "content-type",
          "facetType": "radios",
          "facetValues": [
            {
              "facetId": "product",
              "facetLabel": "Products",
              "facetURL": "t10-search-results.html?facetvalue=product",
              "facetIsChecked": true,
              "target": "_self"
            },
            {
              "facetId": "news",
              "facetLabel": "News & Insights",
              "facetURL": "t10-search-results.html?facetvalue=news",
              "facetIsChecked": false,
              "target": "_self"
            },
            {
              "facetId": "resources",
              "facetLabel": "Resources",
              "facetURL": "t10-search-results.html?facetvalue=resources",
              "facetIsChecked": false,
              "target": "_self"
            }
          ]
        },
        {
          "facetGroupName": "Voltage",
          "facetGroupId": "voltage",
          "facetType": "checkboxes",
          "facetValues": [
            {
              "facetId": "700va",
              "facetLabel": "700 VA",
              "facetURL": "t10-search-results.html?facetvalue=700va",
              "facetIsChecked": true
            },
            {
              "facetId": "1000va",
              "facetLabel": "1000 VA",
              "facetURL": "t10-search-results.html?facetvalue=1000va",
              "facetIsChecked": true
            },
            {
              "facetId": "1500va",
              "facetLabel": "1500 VA",
              "facetURL": "t10-search-results.html?facetvalue=1500va",
              "facetIsChecked": false
            },
            {
              "facetId": "2000va",
              "facetLabel": "2000 VA",
              "facetURL": "t10-search-results.html?facetvalue=2000va",
              "facetIsChecked": true
            },
            {
              "facetId": "3000va",
              "facetLabel": "3000 VA",
              "facetURL": "t10-search-results.html?facetvalue=3000va",
              "facetIsChecked": true
            },
            {
              "facetId": "5000va",
              "facetLabel": "3500 VA",
              "facetURL": "t10-search-results.html?facetvalue=3500va",
              "facetIsChecked": true
            }
          ]
        },
        {
          "facetGroupName": "Facet 01",
          "facetGroupId": "name1",
          "facetType": "radios",
          "facetValues": [
            {
              "facetId": "value1",
              "facetLabel": "Value 1",
              "facetURL": "t10-search-results.html?facetvalue=value1",
              "facetIsChecked": false
            },
            {
              "facetId": "value2",
              "facetLabel": "Value 2",
              "facetURL": "t10-search-results.html?facetvalue=value2",
              "facetIsChecked": false
            },
            {
              "facetId": "value3",
              "facetLabel": "Value 3",
              "facetURL": "t10-search-results.html?facetvalue=value3",
              "facetIsChecked": false
            },
            {
              "facetId": "value4",
              "facetLabel": "Value 4",
              "facetURL": "t10-search-results.html?facetvalue=value4",
              "facetIsChecked": false
            },
            {
              "facetId": "value5",
              "facetLabel": "Value 5",
              "facetURL": "t10-search-results.html?facetvalue=value5",
              "facetIsChecked": false
            },
            {
              "facetId": "value6",
              "facetLabel": "Value 6",
              "facetURL": "t10-search-results.html?facetvalue=value6",
              "facetIsChecked": false
            },
            {
              "facetId": "value7",
              "facetLabel": "Value 7",
              "facetURL": "t10-search-results.html?facetvalue=value7",
              "facetIsChecked": false
            },
            {
              "facetId": "value8",
              "facetLabel": "Value 8",
              "facetURL": "t10-search-results.html?facetvalue=value8",
              "facetIsChecked": false
            }
          ]
        },
        {
          "facetGroupName": "Facet 02",
          "facetGroupId": "name02",
          "facetType": "checkboxes",
          "facetValues": [
            {
              "facetId": "f2value1",
              "facetLabel": "Value 1",
              "facetURL": "t10-search-results.html?facetvalue=f2value1",
              "facetIsChecked": false
            },
            {
              "facetId": "f2value2",
              "facetLabel": "Value 2",
              "facetURL": "t10-search-results.html?facetvalue=f2value2",
              "facetIsChecked": false
            },
            {
              "facetId": "f2value3",
              "facetLabel": "Value 3",
              "facetURL": "t10-search-results.html?facetvalue=f2value3",
              "facetIsChecked": false
            },
            {
              "facetId": "f2value4",
              "facetLabel": "Value 4",
              "facetURL": "t10-search-results.html?facetvalue=f2value4",
              "facetIsChecked": false
            },
            {
              "facetId": "f2value5",
              "facetLabel": "Value 5",
              "facetURL": "t10-search-results.html?facetvalue=f2value5",
              "facetIsChecked": false
            },
            {
              "facetId": "f2value6",
              "facetLabel": "Value 6",
              "facetURL": "t10-search-results.html?facetvalue=f2value6",
              "facetIsChecked": false
            }
          ]
        },
        {
          "facetGroupName": "Facet 03",
          "facetGroupId": "name3",
          "facetType": "radios",
          "facetValues": [
            {
              "facetId": "f3value1",
              "facetLabel": "Value 1",
              "facetURL": "t10-search-results.html?facetvalue=f3value1",
              "facetIsChecked": false
            },
            {
              "facetId": "f3value2",
              "facetLabel": "Value 2",
              "facetURL": "t10-search-results.html?facetvalue=f3value2",
              "facetIsChecked": false
            },
            {
              "facetId": "f3value3",
              "facetLabel": "Value 3",
              "facetURL": "t10-search-results.html?facetvalue=f3value3",
              "facetIsChecked": false
            },
            {
              "facetId": "f3value4",
              "facetLabel": "Value 4",
              "facetURL": "t10-search-results.html?facetvalue=f3value4",
              "facetIsChecked": false
            },
            {
              "facetId": "f3value5",
              "facetLabel": "Value 5",
              "facetURL": "t10-search-results.html?facetvalue=f3value5",
              "facetIsChecked": false
            }
          ]
        },
        {
          "facetGroupName": "Facet 04",
          "facetGroupId": "name04",
          "facetType": "checkboxes",
          "facetValues": [
            {
              "facetId": "f4value1",
              "facetLabel": "Value 1",
              "facetURL": "t10-search-results.html?facetvalue=f4value1",
              "facetIsChecked": false
            },
            {
              "facetId": "f4value2",
              "facetLabel": "Value 2",
              "facetURL": "t10-search-results.html?facetvalue=f4value2",
              "facetIsChecked": false
            },
            {
              "facetId": "f4value3",
              "facetLabel": "Value 3",
              "facetURL": "t10-search-results.html?facetvalue=f4value3",
              "facetIsChecked": false
            },
            {
              "facetId": "f4value4",
              "facetLabel": "Value 4",
              "facetURL": "t10-search-results.html?facetvalue=f4value4",
              "facetIsChecked": false
            }
          ]
        }
      ]
    };


    data.search = {
      "ajaxRequestUrl": "/content/eaton-static/us/en/qa-templates/test-data/search-results--more-data.json",
      "ajaxRequestNextPage": 2,
      "resultsCount": 200,
      "currentPage": 1,
      "resultsLabel": 'Results',
      "loadMoreLabel": 'Load More',
      "resultsOptions": {
        "showLoadMore": true,
        "disclaimerEnabled": true
      },
      "searchTerm": "bckup servre",
      "autoCorrect": {
        "enabled": true,
        "searchTerm": "bckup servre",
        "correctedTerm": "backup server",
        "url": "#backup-server",
        "target": '_self'
      },
      "activeFilters": {
        "removeFilterLabel": "Remove Filter",
        "clearAllFilters": {
          "label": "Clear filters",
          "url": "t10-search-results.html?clear-all-filters=true",
          "target": "_self"
        },
        "values": [
          {
            "label": "Products",
            "url": "t10-search-results.html?remove-filter=products",
            "target": "_self"
          },
          {
            "label": "700 VA",
            "url": "t10-search-results.html?remove-filter=700-va",
            "target": "_self"
          },
          {
            "label": "1000 VA",
            "url": "t10-search-results.html?remove-filter=1000-va",
            "target": "_self"
          },
          {
            "label": "2000 VA",
            "url": "t10-search-results.html?remove-filter=2000-va",
            "target": "_self"
          },
          {
            "label": "3000 VA",
            "url": "t10-search-results.html?remove-filter=3000-va",
            "target": "_self"
          },
          {
            "label": "3500 VA",
            "url": "t10-search-results.html?remove-filter=3500-va",
            "target": "_self"
          }
        ]
      },
      "sortBy": {
        "sortByLabel": "Sort By",
        "items": [
          {
            "label": "Relevance",
            "url": 't10-search-results.html?sortby=relevance',
            "target": '_self'
          },
          {
            "label": "Newest",
            "url": 't10-search-results.html?sortby=newest',
            "target": '_self'
          },
          {
            "label": "A to Z",
            "url": 't10-search-results.html?sortby=a-z',
            "target": '_self'
          },
          {
            "label": "Z to A",
            "url": 't10-search-results.html?sortby=z-a',
            "target": '_self'
          }
        ]
      }
    };


    //-----------------------------------
    // Results List for: Page T10-search-results
    //-----------------------------------
    data.search.searchResults = {
      "resultsList": [
        {
          "contentType": "family",
          "contentItem": {
            "name": "Network, Server & Storage UPS",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam.sed do eiusmod tempor incididunt ut labore et dolore magna …",
            "image": {
              "mobile": "/content/dam/eaton/images/products/product-1-thumbnail.png",
              "tablet": "/content/dam/eaton/images/products/product-1-thumbnail.png",
              "desktop": "/content/dam/eaton/images/products/product-1-thumbnail.png",
              "altText": "Network, Server & Storage UPS"
            },
            "link": {
              "text": "www.cooperindustries.com/.../product_updates.html",
              "url": "http://www.cooperindustries.com/",
              "target": "_blank"
            },
            "secondaryLinks": [
              { "text": "Overview", "url": "http://eaton.dev/product.overview.html", "target": "_blank" },
              { "text": "Models", "url": "http://eaton.dev/product.models.html", "target": "_blank" },
              { "text": "Resources", "url": "http://eaton.dev/product.resources.html", "target": "_blank" }
            ]
          }
        },
        {
          "contentType": "article",
          "contentItem": {
            "articleType": "external",
            "name": "Eaton 9 Series Single-Phase UPS Visio Stencils",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam.sed do eiusmod tempor incididunt ut labore et dolore magna…",
            "link": {
              "text": "www.cooperindustries.com/.../product_updates.html",
              "url": "http://www.cooperindustries.com/",
              "target": "_blank"
            }
          }
        },
        {
          "contentType": "article",
          "contentItem": {
            "articleType": "internal",
            "name": "Eaton 9-Speed Synchronized Transmissions Single “H” Shift Control Configuration article",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam.sed do eiusmod tempor incididunt ut labore et dolore magna…",
            "date": "November 27, 2015",
            "link": {
              "text": "www.cooperindustries.com/.../product_updates.html",
              "url": "http://www.cooperindustries.com/",
              "target": "_blank"
            }
          }
        },
        {
          "contentType": "sku",
          "contentItem": {
            "name": "9E6Ki-Eaton 9E",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam.sed do eiusmod tempor incididunt ut labore et dolore magna …",
            "image": {
              "mobile": "/content/dam/eaton/images/products/product-2-thumbnail.png",
              "tablet": "/content/dam/eaton/images/products/product-2-thumbnail.png",
              "desktop": "/content/dam/eaton/images/products/product-2-thumbnail.png",
              "altText": "9E6Ki-Eaton 9E"
            },
            "link": {
              "text": "www.cooperindustries.com/.../product_updates.html",
              "url": "http://www.cooperindustries.com/",
              "target": "_blank"
            },
            "secondaryLinks": [
              { "text": "Overview", "url": "http://eaton.dev/product.overview.html", "target": "_blank" },
              { "text": "Models", "url": "http://eaton.dev/product.models.html", "target": "_blank" },
              { "text": "Resources", "url": "http://eaton.dev/product.resources.html", "target": "_blank" }
            ]
          }
        },
        {
          "contentType": "resource",
          "contentItem": {
            "name": "Eaton 9 Series Single-Phase UPS Visio Stencils",
            "documentType": "PDF",
            "documentSize": "4MB",
            "documentName": "Specs-2017.pdf",
            "link": {
              "text": "www.eaton.co/.../specs.pdf",
              "url": "https://eaton.dev/resources/specs.pdf?download=true",
              "target": "_blank"
            }
          }
        },
        {
          "contentType": "family",
          "contentItem": {
            "name": "Network, Server & Storage UPS",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam.sed do eiusmod tempor incididunt ut labore et dolore magna …",
            "image": {
              "mobile": "/content/dam/eaton/images/products/product-3-thumbnail.png",
              "tablet": "/content/dam/eaton/images/products/product-3-thumbnail.png",
              "desktop": "/content/dam/eaton/images/products/product-3-thumbnail.png",
              "altText": "Network, Server & Storage UPS"
            },
            "link": {
              "text": "www.cooperindustries.com/.../product_updates.html",
              "url": "http://www.cooperindustries.com/",
              "target": "_blank"
            },
            "secondaryLinks": [
              { "text": "Overview", "url": "http://eaton.dev/product.overview.html", "target": "_blank" },
              { "text": "Models", "url": "http://eaton.dev/product.models.html", "target": "_blank" },
              { "text": "Resources", "url": "http://eaton.dev/product.resources.html", "target": "_blank" }
            ]
          }
        },
        {
          "contentType": "article",
          "contentItem": {
            "articleType": "external",
            "name": "Eaton 9 Series Single-Phase UPS Visio Stencils",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam.sed do eiusmod tempor incididunt ut labore et dolore magna…",
            "date": "November 27, 2015",
            "link": {
              "text": "www.cooperindustries.com/.../product_updates.html",
              "url": "http://www.cooperindustries.com/",
              "target": "_blank"
            }
          }
        },
        {
          "contentType": "article",
          "contentItem": {
            "articleType": "internal",
            "name": "Eaton 9-Speed Synchronized Transmissions Single “H” Shift Control Configuration article",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam.sed do eiusmod tempor incididunt ut labore et dolore magna…",
            "date": "November 27, 2015",
            "link": {
              "text": "www.cooperindustries.com/.../product_updates.html",
              "url": "http://www.cooperindustries.com/",
              "target": "_blank"
            }
          }
        },
        {
          "contentType": "sku",
          "contentItem": {
            "name": "9E6Ki-Eaton 9E",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam.sed do eiusmod tempor incididunt ut labore et dolore magna …",
            "image": {
              "mobile": "/content/dam/eaton/images/products/product-7-thumbnail.png",
              "tablet": "/content/dam/eaton/images/products/product-7-thumbnail.png",
              "desktop": "/content/dam/eaton/images/products/product-7-thumbnail.png",
              "altText": "9E6Ki-Eaton 9E"
            },
            "link": {
              "text": "www.cooperindustries.com/.../product_updates.html",
              "url": "http://www.cooperindustries.com/",
              "target": "_blank"
            },
            "secondaryLinks": [
              { "text": "Overview", "url": "http://eaton.dev/product.overview.html", "target": "_blank" },
              { "text": "Models", "url": "http://eaton.dev/product.models.html", "target": "_blank" },
              { "text": "Resources", "url": "http://eaton.dev/product.resources.html", "target": "_blank" }
            ]
          }
        },
        {
          "contentType": "article",
          "contentItem": {
            "articleType": "internal",
            "name": "Eaton 9-Speed Synchronized Transmissions Single “H” Shift Control Configuration article",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam.sed do eiusmod tempor incididunt ut labore et dolore magna…",
            "date": "November 27, 2015",
            "link": {
              "text": "www.cooperindustries.com/.../product_updates.html",
              "url": "http://www.cooperindustries.com/",
              "target": "_blank"
            }
          }
        },
        {
          "contentType": "resource",
          "contentItem": {
            "name": "Eaton 9 Series Single-Phase UPS Visio Stencils",
            "documentType": "PDF",
            "documentSize": "4MB",
            "documentName": "Specs-2017.pdf",
            "link": {
              "text": "www.eaton.co/.../specs.pdf",
              "url": "https://eaton.dev/resources/specs.pdf?download=true",
              "target": "_blank"
            }
          }
        },
        {
          "contentType": "family",
          "contentItem": {
            "name": "Network, Server & Storage UPS",
            "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam.sed do eiusmod tempor incididunt ut labore et dolore magna …",
            "image": {
              "mobile": "/content/dam/eaton/images/products/product-5-thumbnail.png",
              "tablet": "/content/dam/eaton/images/products/product-5-thumbnail.png",
              "desktop": "/content/dam/eaton/images/products/product-5-thumbnail.png",
              "altText": "Network, Server & Storage UPS"
            },
            "link": {
              "text": "www.cooperindustries.com/.../product_updates.html",
              "url": "http://www.cooperindustries.com/",
              "target": "_blank"
            },
            "secondaryLinks": [
              { "text": "Overview", "url": "http://eaton.dev/product.overview.html", "target": "_blank" },
              { "text": "Models", "url": "http://eaton.dev/product.models.html", "target": "_blank" },
              { "text": "Resources", "url": "http://eaton.dev/product.resources.html", "target": "_blank" }
            ]
          }
        }
      ]
    };

  // }

  // Override Data for View: Simple
  if ((typeof(testdata) != "undefined") && testdata == "search-results-simple") {
    data.search.autoCorrect = false;
    data.search.activeFilters = [];
    data.search.resultsCount = 56;
  }

  return data;

});
