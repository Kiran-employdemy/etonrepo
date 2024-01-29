module.exports = {
  filledAssetBulkDownloadDrawer : () => {
    return `<div id="assets-bulkdownload-email-div">
      <div class="bulk-download-fixed-drawer" style="">
        <div class="fixed-drawer container">
          <div class="fixed-drawer__header">
            <div class="fixed-drawer__header-row bg-color__green">
              <span class="fixed-drawer__header-row__title min__close">
                <i class="icon fixed-drawer__down icon-chevron-down"></i>
                Download document (<span class="fixed-drawer__count">3</span>) of
                20
              </span>
            </div>
            <div class="fixed-drawer__header-clear"><a class="clear-selection">Clear selection</a></div>
          </div>
          <div class="main__box u-flex-wrap viewmore" id="selected-files-drawer-box" data-view-limit="3" data-viewless="View less" data-download-file-count-limit="20" data-viewmore="View more"><div class="fixed-drawer__box" id="selected-file-8a08fd33" data-selected-file-id="8a08fd33"><p class="fixed-drawer__font" id="file-title-0">Anyplace switch RF9575 user guide</p><i class="icon close fixed-drawer__close icon-close" data-file-id="8a08fd33"></i></div><div class="fixed-drawer__box" id="selected-file-e3286a2c" data-selected-file-id="e3286a2c"><p class="fixed-drawer__font" id="file-title-1">Installing and testing an AF/GF receptacle instruction sheet</p><i class="icon close fixed-drawer__close icon-close" data-file-id="e3286a2c"></i></div><div class="fixed-drawer__box" id="selected-file-1c4b7478" data-selected-file-id="1c4b7478"><p class="fixed-drawer__font" id="file-title-2">AF/GF receptacles and breakers brochure</p><i class="icon close fixed-drawer__close icon-close" data-file-id="1c4b7478"></i></div></div>
          <div class="fixed-drawer__footer text-right">
            <a data-analytics-event="request quote" class="call-fd-model fixed-drawer__footer__buttons-div standard-reverse-style" title="Send email" target="_blank" id="bulkemail">
              <img src="/content/dam/eaton/resources/icons/email.png" class="new-email-icon" alt="email"> Send email
            </a>
            <a data-analytics-event="request quote" class="call-fd-model fixed-drawer__footer__buttons-div standard-style" title="Download selection" target="_blank" id="idbulkdownload">
              <i class="icon icon-download"></i>
              <div class="load-spinner" hidden></div>
              Download selection
            </a>
          </div>
        </div>

        <div class="modal fd-model fade-in" id="bulkemail-model" role="dialog">
          <div class="modal-dialog modal-center">
            <!-- Modal content-->
            <div class="modal-content">
              <div class="modal-header">
                <button type="button" class="close fd-close-popup" data-dismiss="modal">×</button>
              </div>
              <div class="modal-body">
                <h4 class="modal-heading">Send email</h4>
                <input type="text" id="bulkDownload-email-to" class="auto-search faceted-navigation__list__search__input show" placeholder="Enter email address" data-valid-error="Please enter valid email address" data-email-error="Email not sent, please try again." data-email-send="Email sent successfully">
                  <div class="email-valid-msg-div"></div>
                <a data-analytics-event="request quote" class="fixed-drawer__footer__buttons-div standard-reverse-style" title="Send email" target="_blank" id="bulkDownload-email-send-btn">
                  <img src="/content/dam/eaton/resources/icons/email.png" class="new-email-icon" alt="email"> Send
                </a>
              </div>
              <div class="modal-footer">
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>`;
  },

};
