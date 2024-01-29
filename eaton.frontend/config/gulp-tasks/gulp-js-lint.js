'use strict';
/*
* Gulp Task: Lint Javascript Files
*/

const path = require('path');
const gulpEslint = require('gulp-eslint');
const gulpIf = require('gulp-if');
const gutil = require('gulp-util');
const configEslint = require(path.resolve('./', '.eslintrc.js'));
const processIfModified = require('gulp-process-only-modified-files');

// Add/Merge 'autofix' option with ESLint config
const configFixFiles = Object.assign({}, configEslint, { fix: true });


/**
* Helper / Fix Files based on ESLint Rules
* http://eslint.org/docs/rules/
*/
function isFixed(file) {

  // Has ESLint fixed the file contents?
  return file.eslint !== null && file.eslint.fixed;
}

/**
* ESLint Gulp
*/
const lintJS = function(gulp, CONFIG, bundle) {
  return function() {

    // Lint JavaScript - Source Code
    //--------------
    gulp.src([
      path.resolve(CONFIG.paths.srcRoot) + '/**/*.js',

      // Ignore Vendor Libs
      '!' + CONFIG.paths.srcRoot + '/**/vendors/**/*.js'
    ])
      .pipe(gulpEslint(configFixFiles))
      .pipe(gulpEslint.format())

      // If the file was fixed By ESLint, Write the file to dest
      .pipe(gulpIf(isFixed, gulp.dest(CONFIG.paths.srcRoot)))

      // Show Count - Total Errors
      .pipe(gulpEslint.results(function( results ) {
        let count = results.warningCount + results.errorCount;
        if (count > 0) {
          gutil.log( gutil.colors.white.bgRed.bold(` ${ count } - ESLint Errors! `));
        }
      }));



    // Lint Gulp Tasks And Config Files
    //--------------
    gulp.src([
      path.resolve('./config') + '/**/*.js',

      // Ignore These folders
      '!' + path.resolve('./config/glyphicon-font/**/*.js')

    ])
      .pipe(processIfModified({ cacheFilename: 'javascript-lint' }))
      .pipe(gulpEslint(configEslint))
      .pipe(gulpEslint.format());

  };
};


module.exports = lintJS;
