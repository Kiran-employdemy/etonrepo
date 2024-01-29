'use strict';
/*
* Gulp Task: Transform Javascript Files
*/

const path = require('path');
const babel = require('gulp-babel');
const plumber = require('gulp-plumber');
const gutil = require('gulp-util');
const rename = require('gulp-rename');
const processIfModified = require('gulp-process-only-modified-files');

// File Header/Banner
//--------------
const header = require('gulp-header');
const fileBanner = require('../utils/file-banner-message');
const msgPackageName = 'gulp-babel';
let bannerMessage = fileBanner.getBannerMessage('TPL_ASTERISK', msgPackageName);


// ES6 Options
//--------------
const babelOptions = {

  // NOTE: gulp-babel -> Build Error: Couldn't find preset "es2015" relative to directory #93
  // FIX -> Use NPM package names. More Info: https://github.com/babel/gulp-babel/issues/93#issuecomment-249172581
  presets: [
    'babel-preset-es2015'
  ].map(require.resolve)

};


const buildJS = function(gulp, CONFIG, bundle) {
  return function() {
    // NOTE: unsure how to rename nodelist-foreach-polyfill/index.js in the context of a src array,
    // so it is renamed and moved into clientlibs separately
    gulp
      .src(path.resolve('./node_modules/nodelist-foreach-polyfill/index.js'))
      .pipe(rename('nodelist-foreach-polyfill.js'))
      .pipe(plumber())
      .pipe(babel(babelOptions))
      .on('error', (err) => gutil.log(gutil.colors.red.bold('[Javascript Build Error]'), err.message))
      .pipe(header(bannerMessage))
      .pipe(gulp.dest( CONFIG.paths.destAEM.clientlibStatic + '/global/js/vendors/' ));

    // Grab polyfills from node_modules and copy to the clientlibs
    gulp
      .src([
        path.resolve('./node_modules/fetch-polyfill/fetch.js')
      ])
      .pipe(plumber())
      .pipe(babel(babelOptions))
      .on('error', (err) => gutil.log(gutil.colors.red.bold('[Javascript Build Error]'), err.message))
      .pipe(header(bannerMessage))
      .pipe(gulp.dest( CONFIG.paths.destAEM.clientlibStatic + '/global/js/vendors/' ));

    // NOTE: unsure how to rename nodelist-foreach-polyfill/index.js in the context of a src array,
    // so it is renamed and moved into clientlibs separately
    gulp
      .src(path.resolve('./node_modules/promise-polyfill/dist/polyfill.min.js'))
      .pipe(rename('promise-polyfill.js'))
      .pipe(plumber())
      .pipe(babel(babelOptions))
      .on('error', (err) => gutil.log(gutil.colors.red.bold('[Javascript Build Error]'), err.message))
      .pipe(header(bannerMessage))
      .pipe(gulp.dest( CONFIG.paths.destAEM.clientlibStatic + '/global/js/vendors/' ));

    // Build JS
    //--------------
    gulp
      .src([
        path.resolve(CONFIG.paths.srcRoot) + '/**/*.js'
      ])
      .pipe(processIfModified({ cacheFilename: 'javascript' }))
      .pipe(plumber())
      .pipe(babel(babelOptions))
      .on('error', (err) => gutil.log(gutil.colors.red.bold('[Javascript Build Error]'), err.message))
      .pipe(header(bannerMessage))
      .pipe(gulp.dest( CONFIG.paths.destAEM.clientlibStatic ));

  };
};


module.exports = buildJS;
