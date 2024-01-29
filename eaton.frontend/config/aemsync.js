'use strict';

// Dependecies
const path = require('path');
const gutil = require('gulp-util');
const _ = require('lodash');
const aemsync = require('aemsync');

// Environment Constants
const CONFIG = require('./eaton-config.js');


//-----------------------------------
// AEM Sync Settings
//-----------------------------------
let dirPath;
let workingDirs = [];

// NOTE: Add more AEM Working dirs to 'eaton-config.js' File

_.each(CONFIG.aemBundles, (item, index) => {
  dirPath = path.resolve(__dirname, path.join('../', item));
  workingDirs.push(dirPath);
});

// console.log('Working-dirs:', workingDirs);

let targets = [

  // AEM:Author
  'http://admin:admin@localhost:4502'

  // AEM:Publish
  // 'http://admin:admin@localhost:4503'
];


let exclude = '**/*.orig';
let pushInterval = 700;


//-----------------------------------
// Push Messages
//-----------------------------------
let onPushEnd = function(err, host) {

  if (err) {
    return gutil.log(gutil.colors.red(`Error when pushing package to ${ host }.`, err));
  }

  gutil.log(gutil.colors.green.bold(`Package pushed to ${ host }.`));
};


//-----------------------------------
// Add Watchers
//-----------------------------------

let pusher = new aemsync.Pusher(targets, pushInterval, onPushEnd);
let watcher = new aemsync.Watcher();

// Initialize queue processing.
pusher.start();

_.each(workingDirs, (directory, index) => {
  watcher.watch(directory, exclude, (localPath) => {

    // Add item to Pusher's queue when a change is detected.
    pusher.enqueue(localPath);
  });
});
