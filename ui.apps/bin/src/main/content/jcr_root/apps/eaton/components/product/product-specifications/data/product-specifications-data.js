'use strict';
use(function () {

  var data = {};
  var testdata = this.testdata;

  data.anchors = [
    {
      "anchorTitle": "Electrical Input",
      "anchorId": "electrical-input",
      "content": {
        "rows": [
          {
            "label": "Connection",
            "rowId": "connection",
            "specValue": [
              {
                "type": "image",
                "text": "NEMA 5",
                "imageSrc": "/content/dam/eaton/images/product-specifications/product-specs-thumb.png",
                "link": {
                  "url": "/content/dam/eaton/images/product-specifications/product-specs-1000x1000.png",
                  "target": "_blank"
                }
              }
            ]
          },
          {
            "label": "Input Cord",
            "rowId": "input-cord",
            "specValue": [
              {
                "type": "document",
                "text": "8 feet",
                "imageSrc": "",
                "link": {
                  "url": "",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Input Voltage range",
            "rowId": "input-voltage-range",
            "specValue": [
              {
                "type": "document",
                "text": "Full load: 100-144V, <30% load: 60-144V",
                "imageSrc": "",
                "link": {
                  "url": "",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Nominal Voltage",
            "rowId": "nominal-voltage",
            "specValue": [
              {
                "type": "document",
                "text": "120V default (100/110/120/125V)",
                "imageSrc": "",
                "link": {
                  "url": "",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Frequency",
            "rowId": "frequency",
            "specValue": [
              {
                "type": "document",
                "text": "50/60 Hz",
                "imageSrc": "",
                "link": {
                  "url": "",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Frequency Range",
            "rowId": "frequency-range",
            "specValue": [
              {
                "type": "document",
                "text": "60 Hz: 50-70 Hz, 50 Hz: 40-60 hz",
                "imageSrc": "",
                "link": {
                  "url": "",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Input Power Factor",
            "rowId": "input-power-factor",
            "specValue": [
              {
                "type": "document",
                "text": ">.99",
                "imageSrc": "",
                "link": {
                  "url": "",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Input Current Distortion",
            "rowId": "input-current-distortion",
            "specValue": [
              {
                "type": "document",
                "text": "<5%",
                "imageSrc": "",
                "link": {
                  "url": "",
                  "target": ""
                }
              }
            ]
          }
        ]
      }
    },
    {
      "anchorTitle": "Electrical Output",
      "anchorId": "electrical-output",
      "content": {
        "rows": [
          {
            "label": "Connection",
            "rowId": "connection",
            "specValue": [
              {
                "type": "document",
                "text": "Type: document lorem ipsum",
                "imageSrc": "",
                "link": {
                  "url": "",
                  "target": ""
                }
              },
              {
                "type": "image",
                "text": "Type: image",
                "imageSrc": "/content/dam/eaton/images/product-specifications/product-specs-thumb.png",
                "link": {
                  "url": "/content/dam/eaton/images/product-specifications/product-specs-1000x1000.png",
                  "target": "_blank"
                }
              },
              {
                "type": "link",
                "text": "Type: link NEMA 5-15P",
                "imageSrc": "",
                "link": {
                  "url": "http://eaton.dev/#document.pdf",
                  "target": "_blank"
                }
              },
              {
                "type": "anotherUnknownValueType",
                "text": "Type: default lorem ipsum dolor",
                "imageSrc": "",
                "link": {
                  "url": "",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Input Voltage range",
            "rowId": "input-voltage-range",
            "specValue": [
              {
                "type": "document",
                "text": "Full load: 100-144V, <30% load: 60-144V",
                "imageSrc": "",
                "link": {
                  "url": "",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Nominal Voltage",
            "rowId": "nominal-voltage",
            "specValue": [
              {
                "type": "document",
                "text": "120V default (100/110/120/125V)",
                "imageSrc": "",
                "link": {
                  "url": "",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Frequency",
            "rowId": "frequency",
            "specValue": [
              {
                "type": "document",
                "text": "50/60 Hz",
                "imageSrc": "",
                "link": {
                  "url": "",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Frequency Range",
            "rowId": "frequency-range",
            "specValue": [
              {
                "type": "document",
                "text": "60 Hz: 50-70 Hz, 50 Hz: 40-60 hz",
                "imageSrc": "",
                "link": {
                  "url": "",
                  "target": ""
                }
              }
            ]
          }
        ]
      }
    },
    {
      "anchorTitle": "Battery",
      "anchorId": "battery",
      "content": {
        "rows": [
          {
            "label": "Optional Extended Battery",
            "rowId": "optional-extended-battery",
            "specValue": [
              {
                "type": "link",
                "text": "9PXEBM48RT",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": "_blank"
                }
              }
            ]
          },
          {
            "label": "Battery Description",
            "rowId": "battery-description",
            "specValue": [
              {
                "type": "document",
                "text": "Sealed, lead-acid; maintenance free",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Maximum number of extended battery modules (EBM)",
            "rowId": "maximum-number-ebm",
            "specValue": [
              {
                "type": "document",
                "text": "Add up to 4 extended battery modules (EBM)",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Battery Management",
            "rowId": "battery-management",
            "specValue": [
              {
                "type": "link",
                "text": "ABM technology",
                "imageSrc": "",
                "link": {
                  "url": "#abm-technology",
                  "target": ""
                }
              },
              {
                "type": "document",
                "text": "3-stage charging extendsbattery service life by 50% and providesadvance warning for battery replacement",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Battery Replacement",
            "rowId": "battery-replacement",
            "specValue": [
              {
                "type": "document",
                "text": "Replaceable internal batteries and extendedbattery modules (EBMs)",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": ""
                }
              }
            ]
          }
        ]
      }
    },
    {
      "anchorTitle": "Communications",
      "anchorId": "communications",
      "content": {
        "rows": [
          {
            "label": "Optional Extended Battery",
            "rowId": "optional-extended-battery",
            "specValue": [
              {
                "type": "link",
                "text": "9PXEBM48RT",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": "_blank"
                }
              }
            ]
          },
          {
            "label": "Battery Description",
            "rowId": "battery-description",
            "specValue": [
              {
                "type": "document",
                "text": "Sealed, lead-acid; maintenance free",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Maximum number of extended battery modules (EBM)",
            "rowId": "maximum-number-ebm",
            "specValue": [
              {
                "type": "document",
                "text": "Add up to 4 extended battery modules (EBM)",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Battery Management",
            "rowId": "battery-management",
            "specValue": [
              {
                "type": "link",
                "text": "ABM technology",
                "imageSrc": "",
                "link": {
                  "url": "#abm-technology",
                  "target": ""
                }
              },
              {
                "type": "document",
                "text": "3-stage charging extendsbattery service life by 50% and providesadvance warning for battery replacement",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Battery Replacement",
            "rowId": "battery-replacement",
            "specValue": [
              {
                "type": "document",
                "text": "Replaceable internal batteries and extendedbattery modules (EBMs)",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": ""
                }
              }
            ]
          }
        ]
      }
    },
    {
      "anchorTitle": "Enviromental & Standards",
      "anchorId": "enviromental-and-standards",
      "content": {
        "rows": [
          {
            "label": "Optional Extended Battery",
            "rowId": "optional-extended-battery",
            "specValue": [
              {
                "type": "link",
                "text": "9PXEBM48RT",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": "_blank"
                }
              }
            ]
          },
          {
            "label": "Battery Description",
            "rowId": "battery-description",
            "specValue": [
              {
                "type": "document",
                "text": "Sealed, lead-acid; maintenance free",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Maximum number of extended battery modules (EBM)",
            "rowId": "maximum-number-ebm",
            "specValue": [
              {
                "type": "document",
                "text": "Add up to 4 extended battery modules (EBM)",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Battery Management",
            "rowId": "battery-management",
            "specValue": [
              {
                "type": "link",
                "text": "ABM technology",
                "imageSrc": "",
                "link": {
                  "url": "#abm-technology",
                  "target": ""
                }
              },
              {
                "type": "document",
                "text": "3-stage charging extendsbattery service life by 50% and providesadvance warning for battery replacement",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": ""
                }
              }
            ]
          },
          {
            "label": "Battery Replacement",
            "rowId": "battery-replacement",
            "specValue": [
              {
                "type": "document",
                "text": "Replaceable internal batteries and extendedbattery modules (EBMs)",
                "imageSrc": "",
                "link": {
                  "url": "#",
                  "target": ""
                }
              }
            ]
          }
        ]
      }
    }
  ];

  return data;
});
