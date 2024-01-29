'use strict';
/*
* Gulp Task: Transform SASS to CSS
*/

const path = require('path');
const sass = require('gulp-sass');
const sassGlobbing = require('node-sass-globbing');
const autoprefixer = require('gulp-autoprefixer');
const processIfModified = require('gulp-process-only-modified-files');
// const plumber = require('gulp-plumber');

// File Header/Banner
//--------------
const header = require('gulp-header');
const fileBanner = require('../utils/file-banner-message');
const msgPackageName = 'gulp-sass';
let bannerMessage = fileBanner.getBannerMessage('TPL_ASTERISK', msgPackageName);

const sassToCSS = function(gulp, CONFIG) {
  return function() {

    // SASS to CSS
    //--------------
    gulp.src([
      path.resolve(CONFIG.paths.srcRoot) + '/**/*.scss'
    ])
      // .pipe(plumber())
      .pipe(processIfModified({ cacheFilename: 'styles' }))
      .pipe(sass({
        includePaths: [
          'node_modules'
        ],
        outputStyle: 'compact',
        importer: sassGlobbing
      }))
      .on('error', sass.logError)

      // Config file: ./eaton.frontend/browserslist
      .pipe(autoprefixer({ remove: false }))
      .pipe(header(bannerMessage))
      .pipe(gulp.dest( CONFIG.paths.destAEM.clientlibStatic ));

  };
};


module.exports = sassToCSS;
