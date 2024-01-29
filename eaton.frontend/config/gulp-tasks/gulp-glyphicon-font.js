'use strict';
/**
* TASK: Create Glyphicon Font
*/

const async = require('async');
const consolidate = require('gulp-consolidate');
const iconfont = require('gulp-iconfont');
const path = require('path');

// File Header/Banner
//--------------
const header = require('gulp-header');
const fileBanner = require('../utils/file-banner-message');
const msgPackageName = 'gulp-iconfont';
let bannerMessage = fileBanner.getBannerMessage('TPL_SLASH', msgPackageName, true);


const buildIconfont = function(gulp, CONFIG) {
  return function() {

    let runTimestamp = Math.round(Date.now() / 1000);

    const iconStream = gulp.src([
      path.resolve('./src-assets/svg-icons/*.svg')
    ])
      .pipe(iconfont({
        centerHorizontally: true,
        fontHeight: 1001,
        fontName: 'eaton-glyphicon',
        formats: ['ttf', 'eot', 'woff'],
        normalize: true,
        prependUnicode: true,
        timestamp: runTimestamp // recommended to get consistent builds when watching files
      }));


    async.parallel([
      function handleGlyphs(cb) {
        iconStream.on('glyphs', function (glyphs, options) {
          gulp.src('./config/glyphicon-font/*.scss')
            .pipe(consolidate('lodash', {
              glyphs: glyphs,
              fontName: 'eaton-glyphicon',
              fontPath: '../css/fonts/',
              className: 'icon'
            }))
            .pipe(header(bannerMessage))
            .pipe(gulp.dest(CONFIG.paths.srcRoot + '/global/css/base/'))
            .on('finish', cb);
        });
      },

      function handleFonts(cb) {
        iconStream
          .pipe(gulp.dest(CONFIG.paths.destAEM.clientlibStatic + '/global/css/fonts'))
          .on('finish', cb);
      },


      // Create "testdata" for AEM's Styleguide Page
      function handleStyleguide(cb) {
        iconStream.on('glyphs', function (glyphs, options) {
          gulp.src('./config/glyphicon-font/*.js')
            .pipe(consolidate('lodash', {
              glyphs: glyphs,
              className: 'icon'
            }))
            .pipe(header(bannerMessage))
            .pipe(gulp.dest(path.join(CONFIG.paths.destAEM.staticComponents, '/styleguide/data/')))
            .on('finish', cb);
        });
      }

    ]);
  };
};




module.exports = buildIconfont;
