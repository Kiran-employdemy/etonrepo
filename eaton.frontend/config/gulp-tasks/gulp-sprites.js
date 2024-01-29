'use strict';
/**
* TASK: Create Image Sprite
*/

const spritesmith = require('gulp.spritesmith');
const path = require('path');
const merge = require('merge-stream');

// File Header/Banner
//--------------
const header = require('gulp-header');
const fileBanner = require('../utils/file-banner-message');
const msgPackageName = 'gulp.spritesmith';
let bannerMessage = fileBanner.getBannerMessage('TPL_SLASH', msgPackageName, true);
bannerMessage += '// stylelint-disable\n\n';


const buildImageSprite = function(gulp, CONFIG) {
  return function() {

    const spriteData = gulp.src('./src-assets/flag-sprite-icons/*.png')
      .pipe(spritesmith({
        imgName: 'flags-sprite.png',
        cssName: '_eaton-flags-sprite.scss',
        imgPath: '../../clientlib-all/resources/images/flags-sprite.png',
        padding: 4
      }));

    // Pipe image stream to be generated in clientLibs folder
    const imgStream = spriteData.img
      .pipe(gulp.dest(path.join(CONFIG.paths.destAEM.clientlibStatic, 'global/css/images')));

    // Pipe CSS stream to be generated in clientLibs folder
    const cssStream = spriteData.css
      .pipe(header(bannerMessage))
      .pipe(gulp.dest(path.join(CONFIG.paths.srcRoot, '/global/css/base/')));

    // Return a merged stream to handle both `end` events
    return merge(imgStream, cssStream);

  };
};

module.exports = buildImageSprite;
