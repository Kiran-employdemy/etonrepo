# M-40 Product Tabs: Intra Tab Link

used by
- Module product tabs


#### data model example
```"use strict";
use(function () {
    var data = {};

  data.intraTab = {
    title: 'Continue to Models',
    href: '#continue-to-models',
    target: '_self'
  };

    return data;
});
```

#### sly use example
```<sly data-sly-use.tpl="../../shared/templates/module-intra-tab-link.html">
  <sly data-sly-call="${ tpl.data @ data=viewData.intraTab }"></sly>
</sly>
```
