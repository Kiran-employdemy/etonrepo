'use strict';
use(function () {

  var data = {
    "headline": "Media Gallery Headline (Optional)",
    "description": "Description goes here quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
    "items": [
      {
        "title": "This is a long active headline text - Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        "text": "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        "isVideo": false, // isVideo is not currenly supported. This propery is just a placeholder for future development
        "downloadEnabled": true,
        "zoomEnabled": true,
        "images": {
          "altText": "Gallery Item 1",
          "thumbnail": "/content/dam/eaton/images/story_03.png",
          "preview": "/content/dam/eaton/images/story_03.png",
          "zoom": "/TBD.ext",
          "download": "/content/dam/eaton/images/story_03.png"
        }
      },
      {
        "title": "Image 2",
        "text": "Description",
        "isVideo": false, // isVideo is not currenly supported. This propery is just a placeholder for future development
        "downloadEnabled": false,
        "zoomEnabled": true,
        "images": {
          "altText": "Gallery Item 2",
          "thumbnail": "/content/dam/eaton/images/video.png",
          "preview": "/content/dam/eaton/images/video.png",
          "zoom": "/TBD.ext",
          "download": "/content/dam/eaton/images/video.png"
        }
      },
      {
        "title": "Eaton 9px Family - Item 3",
        "text": "",
        "isVideo": false, // isVideo is not currenly supported. This propery is just a placeholder for future development
        "downloadEnabled": false,
        "zoomEnabled": false,
        "images": {
          "altText": "Gallery Item 3",
          "thumbnail": "/content/dam/eaton/images/Hero_image_Secondary_Landing.png",
          "preview": "/content/dam/eaton/images/Hero_image_Secondary_Landing.png",
          "zoom": "/TBD.ext",
          "download": "/content/dam/eaton/images/Hero_image_Secondary_Landing.png"
        }
      },
      {
        "title": "",
        "text": "This Slide has no title, only description",
        "isVideo": false, // isVideo is not currenly supported. This propery is just a placeholder for future development
        "downloadEnabled": true,
        "zoomEnabled": true,
        "images": {
          "altText": "Gallery Item 4",
          "thumbnail": "/content/dam/eaton/images/hero_05.png",
          "preview": "/content/dam/eaton/images/hero_05.png",
          "zoom": "/TBD.ext",
          "download": "/content/dam/eaton/images/hero_05.png"
        }
      },
      {
        "title": "",
        "text": "",
        "isVideo": false, // isVideo is not currenly supported. This propery is just a placeholder for future development
        "downloadEnabled": true,
        "zoomEnabled": true,
        "images": {
          "altText": "Gallery Item 5",
          "thumbnail": "/content/dam/eaton/images/story_02.png",
          "preview": "/content/dam/eaton/images/story_02.png",
          "zoom": "/TBD.ext",
          "download": "/content/dam/eaton/images/story_02.png"
        }
      },
      {
        "title": "Eaton 9px Family - Item 6",
        "text": "Eaton FlexPDU& HotSwap MBP",
        "isVideo": false, // isVideo is not currenly supported. This propery is just a placeholder for future development
        "downloadEnabled": true,
        "zoomEnabled": true,
        "images": {
          "altText": "Gallery Item 6",
          "thumbnail": "/content/dam/eaton/images/Cityscape_mob.png",
          "preview": "/content/dam/eaton/images/Cityscape_mob.png",
          "zoom": "/TBD.ext",
          "download": "/content/dam/eaton/images/Cityscape_mob.png"
        }
      }
    ]
  };

  return data;

});
