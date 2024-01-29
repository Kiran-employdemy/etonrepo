'use strict';
use(function () {

  var data = {};
  var testdata = this.testdata;

  data = {
    eyebrowCta:  {
      title: 'NETWORK SERVER STORAGE UPS',
      href: '#take-me-somewhere',
      target: '_blank'
    },

    productName: '9PX UPS',

    // set to false if you requiere postbacks insted hide/show behaviors
    // setting graphicToggle to false adds regular <a>
    graphicToggle: false,

    // SKU use dark teheme
    useDarktheme: false,

    // min 1, max 3 tabs
    tabs: [
      {
        icon: 'doc-text',
        title: 'Overview',
        href: '/content/eaton/en-us/test.html',
        target: '_self',
        selected: false
      },
      {
        icon: 'list-items',
        title: 'Specifications',
        href: '/content/eaton/en-us/test.html',
        target: '_self',
        selected: true
      },
      {
        icon: 'doc-download',
        title: 'Resources',
        href: '/content/eaton/en-us/test.html',
        target: '_self',
        selected: false
      }
    ],

    dropdown: {
      label : 'How to buy',
      options : [
        {
          icon: 'headset',
          title: 'Request a quote',
          href: '#request-a-quote',
          target: '_blank'
        },
        {
          icon: 'tiles',
          title: 'Find a reseller',
          href: '#find-a-reseller',
          target: '_blank'
        },
        {
          icon: 'tiles',
          title: 'Order a catalog',
          href: '#order-a-catalog',
          target: '_blank'
        },
        {
          icon: 'phone',
          title: 'Call toll-free 1 800 365 5794',
          href: '#18003655794',
          target: '_blank'
        },
        {
          icon: 'tiles',
          title: 'Live chat',
          href: '#live-chat',
          target: '_blank'
        }
      ]
    }
  };

  if ((typeof(testdata) != "undefined") && (testdata == "tab-specifications")) {
    data.tabs[0].selected = false;
    data.tabs[1].selected = true;
    data.tabs[2].selected = false;
  }

  if ((typeof(testdata) != "undefined") && (testdata == "tab-resources")) {
    data.tabs[0].selected = false;
    data.tabs[1].selected = false;
    data.tabs[2].selected = true;
  }

  // Alternative View Data
  if (properties.get('view') == 'toggle-graphic') {
    data.graphicToggle = true;
  }


  return data;

});
