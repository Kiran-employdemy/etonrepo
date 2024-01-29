'use strict';
/*
* Gulp Task: Cleans folders before running new builds
*/

const del = require('del');
// const path = require('path');


const cleanAEM = function(gulp, CONFIG) {
  return function() {

    // Clean Build Folders
    //--------------
    del([

      /**
      * NOTE: This task is not being used.
      * TODO: This Taks needs to be reviewd since AEM Creates aditional files(css.txt, js.txt, content.xml)
      * files in order to load Clientlibs, and those files SHOULD NOT be deleted
      */

      // Delete Files, Not folders
      // path.join(CONFIG.paths.destAEM.clientlibStatic, '/global/**/*'),
      // path.join(CONFIG.paths.destAEM.clientlibStatic, '/components/**/*')
    ],

      // Aditional Options
      {

        // NOTE: Cannot delete files/folders outside the current working directory. 'Force' is required.
        force: true
      }

    );
  };
};


module.exports = cleanAEM;
