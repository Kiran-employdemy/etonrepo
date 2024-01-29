'use strict';

const gulp = require('gulp');
const jest = require('jest-cli');

// Environment Constants
const CONFIG = require('./config/eaton-config');
const process = require('process');


// TASK: Clean Folders
//--------------
gulp.task('clean',
  require('./config/gulp-tasks/gulp-clean')(gulp, CONFIG)
);


// TASKS: LINT Source Code
//--------------

// SASS-Lint
gulp.task('lint:css',
  require('./config/gulp-tasks/gulp-css-lint')(gulp, CONFIG)
);

// ESLint
gulp.task('lint:js',
  require('./config/gulp-tasks/gulp-js-lint')(gulp, CONFIG)
);


// TASK: Compile SASS to CSS
//--------------
gulp.task('css:build',
  require('./config/gulp-tasks/gulp-css')(gulp, CONFIG)
);


// TASKS: Jest
//--------------
gulp.task('js:test', async function(callback) {
  let options = process.argv.includes('--onlyChanged') ? {json: false, onlyChanged: true} : {json: false};
  const testResults = await jest.runCLI(options,['__tests__']);
  const { results } = testResults;
  const isTestFailed = !results.success;
  if (isTestFailed) {
    callback(new Error('You have some failed test cases, kindly fix'));
  }
});

// TASKS: Javascript
//--------------
gulp.task('js:build', ['js:test'],
  require('./config/gulp-tasks/gulp-js')(gulp, CONFIG)
);


// TASK: Build Glyphicon Font
//--------------
gulp.task('iconfont',
  require('./config/gulp-tasks/gulp-glyphicon-font')(gulp, CONFIG)
);

// TASK: Build Image Sprite - Flags
//--------------
gulp.task('sprite',
  require('./config/gulp-tasks/gulp-sprites')(gulp, CONFIG)
);

// Shortcuts for Common Tasks
//--------------
gulp.task('lint', ['lint:css', 'lint:js']);
gulp.task('css', ['css:build', 'lint:css']);
gulp.task('js', ['js:build', 'lint:js']);




// Build For Local Development
//--------------
gulp.task('dev', [
  // 'clean',
  'css:build',
  'js:build',
  'lint:css',
  'lint:js'
]);



// Build for Prod Servers
// TODO: TBD: For production builds Add minifcation, hashes, cleanup folders etc
//--------------
gulp.task('prod', [
  // 'clean',
  'css:build',
  'js:build',
  'lint:css',
  'lint:js'
]);



// Default Development Task
//--------------
gulp.task('default', ['dev'], () => {
  // Watch for File Changes
  require('./config/gulp-tasks/gulp-watch')(gulp, CONFIG);
});
