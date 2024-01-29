$(document).ready(() => {
  if (window.location.href.indexOf('ups-download-software') > 0 || window.location.href.indexOf('ups-software-download') > 0) {

    $('.category-hero__cta').remove();
    $('.eaton-breadcrumb').remove();
    $('.category-hero__eyebrow').remove();
  }

  if (window.location.href.indexOf('getapp') > 0) {
    let client = window.navigator.userAgent.toLowerCase();
    if (client.indexOf('android') >= 0) {
      location.href = 'https://play.google.com/store/apps/details?id=com.innova.mobilepush';
    } else if (client.indexOf('iphone') >= 0) {
      location.href = 'https://apps.apple.com/cn/app/winpower-view/id1524611863?l=en';
    } else {
      location.href = 'apprelease.html';
    }
  }
});