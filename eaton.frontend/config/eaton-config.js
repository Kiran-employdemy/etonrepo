module.exports = {
  repository: {
    type: 'git',
    url: 'https://github.com/Razorfish-Central/sr_eaton'
  },

  paths: {
    srcRoot: './src',
    src: {
      global: './src/components',
      components: './src/global'
    },

    destAEM: {
      global: '../eaton.ui.static/src/main/content/jcr_root/etc/designs/eaton-static/clientlib-static/global',
      components: '../eaton.ui.static/src/main/content/jcr_root/etc/designs/eaton-static/clientlib-static/components',
      clientlibAll: '../ui.apps/src/main/content/jcr_root/apps/eaton/settings/wcm/designs/clientlib/clientlib-all/js',
      clientlibStatic: '../ui.apps/src/main/content/jcr_root/apps/eaton/settings/wcm/designs/clientlib',
      staticComponents: '../ui.apps/src/main/content/jcr_root/apps/eaton/components/'
    }
  },

  // AEM Project Folders for AEMSync Task
  aemBundles: [
    '../ui.apps/src/main/content/jcr_root'
  ]
};
