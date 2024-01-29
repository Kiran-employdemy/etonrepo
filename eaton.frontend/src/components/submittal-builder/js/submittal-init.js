document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.submittal-builder__intro')
    .forEach(element => new window.App.SubmittalIntro(element));

  document.querySelectorAll('.submittal-builder__download')
    .forEach(element => new window.App.SubmittalDownload(element));

  document.querySelectorAll('.submittal-builder__filters')
    .forEach(element => new window.App.Filters(element));

  document.querySelectorAll('.submittal-builder__results')
    .forEach(element => new window.App.SubmittalResults(element));

  document.querySelectorAll('.submittal-builder')
    .forEach(element => new window.App.SubmittalBuilder(element));
});
