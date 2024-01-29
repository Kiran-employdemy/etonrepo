//-----------------------------------
// What's New Component
//-----------------------------------

class WhatsNew {
  constructor(arg) {
    this.expiryTime = 3600; // in seconds
    this.country = document.getElementsByName('country')[0].getAttribute('content');
    this.localStorage = 'Eaton-Whats-New-' + this.country;
    this.container = document.getElementById('whatsnew__accordion');
    this.template = document.getElementById('mustache-whats-new');
    this.whatsNewData = {};
    this.groupings = [
      {
        name: 'last7days'
      },
      {
        name: 'last14days'
      },
      {
        name: 'last21days'
      },
      {
        name: 'last30days'
      }
    ];
  }

  init() {
    this.setWhatsNew();
  }

  getWhatsNew() {
    let loader = $('.loader');

    $.ajax({
      type: 'GET',
      url: $('#whatnewresourcepath').text() + '.json?=' + new Date().getTime(),
      success: (data) => {
        loader.removeClass('loader-active');
        this.whatsNewData.expires = new Date(new Date().getTime() + (this.expiryTime * 1000));
        this.whatsNewData.functions = {};
        this.whatsNewData.groups = [];

        this.groupings.forEach((group, ind) => {

          // Get just the Date from DateTime
          data[group.name].forEach((link) => {
            link.assetPubDate = link.assetPublicationDate.split(' ')[0];
          });

          // Push into array for templating
          this.whatsNewData.groups.push({
            expanded: (ind === 0) ? true : false,
            index: ind,
            name: group.name,
            show: (data[group.name].length > 0),
            label: document.getElementById(`label-${ group.name }`).dataset.label,
            links: data[group.name]
          });
        });

        this.unsetWhatsNew();
        localStorage.setItem(this.localStorage, JSON.stringify(this.whatsNewData));
        this.renderWhatsNew(this.whatsNewData);
      },
      error: (e) => {
        loader.removeClass('loader-active');
        console.log(e);
      }
    });
  }

  setWhatsNew() {
    const cachedWhatsNew = localStorage.getItem(this.localStorage);
    let expired = false;
    if (cachedWhatsNew) {
      let loader = $('.loader');
      loader.removeClass('loader-active');
      const whatsNewObj = JSON.parse(cachedWhatsNew);
      expired = (new Date(whatsNewObj.expires) < new Date());
      this.renderWhatsNew(whatsNewObj); // Render cached data
    }
    if (!cachedWhatsNew || expired) {
      this.getWhatsNew(); // Request new data
    }
  }

  unsetWhatsNew() {
    localStorage.removeItem(this.localStorage);
  }

  renderWhatsNew(obj) {
    if (this.container) {
      console.log(obj);
      const template = this.template.innerHTML;
      const output = window.Mustache.render(template, obj);
      this.container.innerHTML = output;
    }
  }
}

(function() {
  const whatsnew = document.getElementById('whatsnew__accordion');
  if (whatsnew) {
    new WhatsNew().init();
  }
}());



