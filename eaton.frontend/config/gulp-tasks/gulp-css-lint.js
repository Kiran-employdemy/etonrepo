'use strict';
/*
* Gulp Task: Lint scss Files
*/

const path = require('path');
const gulpStylelint = require('gulp-stylelint');
const processIfModified = require('gulp-process-only-modified-files');

const lintCSS = function(gulp, CONFIG) {
  return function() {

    // Lint SASS
    //--------------
    gulp.src([
      path.resolve(CONFIG.paths.srcRoot) + '/**/*.scss'

      // Ignore Vendor Libs
      // '!' + path.join(CONFIG.paths.srcRoot, '/global/css/vendors/**/*.scss'),
      // '!' + path.join(CONFIG.paths.srcRoot, '/global/css/vendors/**/*.css')
    ])
      .pipe(processIfModified({ cacheFilename: 'style-lint' }))
    .pipe(gulpStylelint({
      failAfterError: false,
      reporters: [
        { formatter: 'string', console: true }
      ]
    }));
  };
};

module.exports = lintCSS;
