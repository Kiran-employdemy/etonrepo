'use strict';
use(function () {

  var data = {};


  //-----------------------------------
  // Color Palette
  //-----------------------------------
  data.colors = [
    {
      "name": "Eaton Blue",
      "colorCode": "#005EB8",
      "sassVariable": "$color__primary-blue"
    },
    {
      "name": "Gray 1",
      "colorCode": "#333F48",
      "sassVariable": "$color__primary-gray1"
    },
    {
      "name": "Gray 2",
      "colorCode": "#5B6770",
      "sassVariable": "$color__primary-gray2"
    },
    {
      "name": "Gray 3",
      "colorCode": "#98A4AE",
      "sassVariable": "$color__primary-gray3"
    },
    {
      "name": "Gray 4",
      "colorCode": "#CCD2D7",
      "sassVariable": "$color__secondary-gray4"
    },
    {
      "name": "Gray 5",
      "colorCode": "#EEF0F2",
      "sassVariable": "$color__secondary-gray5"
    },
    {
      "name": "Gray 6",
      "colorCode": "#F8F8F8",
      "sassVariable": "$color__secondary-gray6"
    },
    {
      "name": "Dark Blue",
      "colorCode": "#003865",
      "sassVariable": "$color__accent-darkblue"
    },
    {
      "name": "Aqua",
      "colorCode": "#00A298",
      "sassVariable": "$color__accent-aqua"
    },
    {
      "name": "Orange",
      "colorCode": "#ED8B00",
      "sassVariable": "$color__accent-orange"
    },
    {
      "name": "Black",
      "colorCode": "#000",
      "sassVariable": "$color__black"
    },
    {
      "name": "White",
      "colorCode": "#FFF",
      "sassVariable": "$color__white"
    },
    {
      "name": "Green",
      "colorCode": "#4C8C2B",
      "sassVariable": "$color__green"
    },
    {
      "name": "Red",
      "colorCode": "#9D2235",
      "sassVariable": "$color__red"
    }
  ];


  //-----------------------------------
  // Font Ramp
  //-----------------------------------
  data.typography = {};


  // Font Familes
  //--------------
  data.typography.fonts = [
    {
      name: "Arial",
      fontFamily: "Arial",
      cssStyle: "font-family: Arial; font-weight: normal;"
    },
    {
      name: "ArialBold",
      fontFamily: "Arial",
      cssStyle: "font-family: Arial; font-weight: bold;"
    },
    {
      name: "Eaton",
      fontFamily: "Eaton",
      cssStyle: "font-family: Eaton; font-weight: normal;"
    },
    {
      name: "EatonMedium",
      fontFamily: "EatonMedium",
      cssStyle: "font-family: EatonMedium; font-weight: normal;"
    },
    {
      name: "EatonBold",
      fontFamily: "EatonBold",
      cssStyle: "font-family: EatonBold; font-weight: normal;"
     }
  ];


  // Text Elements
  //--------------
  data.typography.styles = {
    headings: [
      {
        name: "H1 Hero",
        id: "h1-hero",
        className: "b-heading-h1-hero",
        mobile: {
          fontFamily: "'EatonMedium', sans-serif",
          fontSize: "3.5rem",
          fontWeight: "normal",
          lineHeight: "4rem"
        },
        desktop: {
          fontFamily: "'EatonMedium', sans-serif",
          fontSize: "5.4rem",
          fontWeight: "normal",
          lineHeight: "6.3rem"
        }
      },
      {
        name: "H1",
        id: "h1",
        className: "b-heading-h1",
        mobile: {
          fontFamily: "'EatonMedium', sans-serif",
          fontSize: "3.3rem",
          fontWeight: "normal",
          lineHeight: "3.8rem"
        },
        desktop: {
          fontFamily: "'EatonMedium', sans-serif",
          fontSize: "5rem",
          fontWeight: "normal",
          lineHeight: "6rem"
        }
      },
      {
        name: "H2",
        id: "h2",
        className: "b-heading-h2",
        mobile: {
          fontFamily: "'EatonMedium', sans-serif",
          fontSize: "2.8rem",
          fontWeight: "normal",
          lineHeight: "3.2rem"
        },
        desktop: {
          fontFamily: "'EatonMedium', sans-serif",
          fontSize: "4.0rem",
          fontWeight: "normal",
          lineHeight: "5rem"
        }
      },
      {
        name: "H3",
        id: "h3",
        className: "b-heading-h3",
        mobile: {
          fontFamily: "'EatonMedium', sans-serif",
          fontSize: "2.8rem",
          fontWeight: "normal",
          lineHeight: "3.2rem"
        },
        desktop: {
          fontFamily: "'EatonMedium', sans-serif",
          fontSize: "4.0rem",
          fontWeight: "normal",
          lineHeight: "5rem"
        }
      },
      {
        name: "H4",
        id: "h4",
        className: "b-heading-h4",
        mobile: {
          fontFamily: "Arial, sans-serif",
          fontSize: "2.2rem",
          fontWeight: "bold",
          lineHeight: "2.9rem"
        },
        desktop: {
          fontFamily: "Arial, sans-serif",
          fontSize: "2.1rem",
          fontWeight: "bold",
          lineHeight: "1.25"
        }
      },
      {
        name: "H5",
        id: "h5",
        className: "b-heading-h5",
        mobile: {
          fontFamily: "Arial, sans-serif",
          fontSize: "2.0rem",
          fontWeight: "bold",
          lineHeight: "3.0rem"
        },
        desktop: {
          fontFamily: "Arial, sans-serif",
          fontSize: "2.6rem",
          fontWeight: "bold",
          lineHeight: "4.0rem"
        }
      }
    ],

    bodyStyles: [
      {
        name: "Body Copy",
        id: "body-copy",
        className: "b-body-copy",
        mobile: {
          fontFamily: "Arial, sans-serif",
          fontSize: "1.8rem",
          fontWeight: "normal",
          lineHeight: "2.8rem"
        },
        desktop: {
          fontFamily: "Arial, sans-serif",
          fontSize: "2.4rem",
          fontWeight: "normal",
          lineHeight: "3.8rem"
        }
      },
      {
        name: "Body Copy Small",
        id: "body-copy-small",
        className: "b-body-copy-small",
        mobile: {
          fontFamily: "Arial, sans-serif",
          fontSize: "1.3rem",
          fontWeight: "normal",
          lineHeight: "1.7rem"
        },
        desktop: {
          fontFamily: "Arial, sans-serif",
          fontSize: "1.8rem",
          fontWeight: "normal",
          lineHeight: "2.4rem"
        }
      },
      {
        name: "Hero Description",
        id: "hero-description",
        className: "b-hero-description",
        mobile: {
          fontFamily: "Arial, sans-serif",
          fontSize: "1.4rem",
          fontWeight: "normal",
          lineHeight: "2.2rem"
        },
        desktop: {
          fontFamily: "Arial, sans-serif",
          fontSize: "2rem",
          fontWeight: "normal",
          lineHeight: "3.2rem"
        }
      },
      {
        name: "Eyebrow Large",
        id: "eyebrow-large",
        className: "b-eyebrow-large",
        mobile: {
          fontFamily: "Arial, sans-serif",
          fontSize: "1.7rem",
          fontWeight: "bold",
          lineHeight: "2rem"
        },
        desktop: {
          fontFamily: "Arial, sans-serif",
          fontSize: "2.4rem",
          fontWeight: "bold",
          lineHeight: "2.8rem"
        }
      },
      {
        name: "Eyebrow Small",
        id: "eyebrow-small",
        className: "b-eyebrow-small",
        mobile: {
          fontFamily: "Arial, sans-serif",
          fontSize: "1.4rem",
          fontWeight: "bold",
          lineHeight: "1.7rem"
        },
        desktop: {
          fontFamily: "Arial, sans-serif",
          fontSize: "1.8rem",
          fontWeight: "bold",
          lineHeight: "2rem"
        }
      }
    ]
  };



  //-----------------------------------
  // CTA's / Buttons
  //-----------------------------------
  data.buttons = [
    {
      name: "Primary Light",
      theme: "light",
      cssClasses: "b-button b-button__primary b-button__primary--light"
    },
    {
      name: "Primary Dark",
      theme: "dark",
      cssClasses: "b-button b-button__primary b-button__primary--dark"
    },
    {
      name: "Secondary Light",
      theme: "",
      cssClasses: "b-button b-button__secondary b-button__secondary--light"
    },
    {
      name: "Secondary Dark",
      theme: "dark",
      cssClasses: "b-button b-button__secondary b-button__secondary--dark"
    },
    {
      name: "Tertiary Light",
      theme: "",
      cssClasses: "b-button b-button__tertiary b-button__tertiary--light"
    },
    {
      name: "Tertiary Dark",
      theme: "dark",
      cssClasses: "b-button b-button__tertiary b-button__tertiary--dark"
    }
  ]


  return data;

});
