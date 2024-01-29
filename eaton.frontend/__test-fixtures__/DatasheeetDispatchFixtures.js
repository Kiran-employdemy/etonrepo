module.exports = {
  initialScreenLoad: (withDescription) => {
    return `
            <div class="datasheet-dispatch aem-GridColumn aem-GridColumn--default--12">
                <div class="container dispatcher left-textarea ">
                    <div class="row dispatcher-intro stickydsp">
                        <div class="dsp-top-sect">
                            <div class="col-md-4">
                                <div class="row">
                                    <div class="col-md-12">
                                        <h1>Datasheet dispatcher</h1>
                                        <p>Create and send deep links</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-8 text-right">
                                <button id="createDeepLinks2"
                                        class="scroll-top b-button b-button__tertiary b-button__tertiary--light">Create
                                </button>
                                <button id="reset-dispatcher"
                                        class="b-button b-button__primary b-button__primary--light disp-disabled">Reset
                                </button>
                                <button id="send-mail" class="b-button b-button__tertiary b-button__tertiary--light disp-disabled">
                                    Send Email
                                </button>
                            </div>
                        </div>
                    </div>
                    <hr>
                    <div class="col-md-3">
                        <div class="articleTextArea row">
                            <label for="article" class="title">Enter catalog number(s)</label>
                            <p class="small-font">In case of multiple article numbers, please use comma (",") as separator.</p>
                            <p class="small-font">Please note that the maximum number of article numbers that can be added to one
                                request is 20.</p>
                            <div class="bg-light">
                                <textarea name="article" id="articleTA" rows="8" class="dispatcher-textarea"></textarea>
                            </div>
                        </div>
                        <br>
                        <div class="fileUploader row">
                            <label for="xlfile" class="title">Or upload a file (.xlsx)</label>
                            <p><a href="#" data-toggle="modal" data-target="#dispatcher_learn_more_modal">Click here for more
                                information.</a></p>
                            <input type="file" name="xlfile" multiple="" id="xlsxUploader" accept=".xlsx">
                        </div>
                        <div class="localeselector row">
                            <p class="title">Select datasheet language</p>
                            <div class="bg-light container">
                                <div class="col-md-12 leng_raw">
                                    <input type="checkbox" name="locales" id="English" value="en-us">
                                    <label for="locales">English</label>
                                </div>
                                <div class="col-md-12 leng_raw">
                                    <input type="checkbox" name="locales" id="" value="de-de">
                                    <label for="locales">German</label>
                                </div>
                                <div class="col-md-12 leng_raw">
                                    <input type="checkbox" name="locales" id="" value="fr-fr">
                                    <label for="locales">French</label>
                                </div>
                            </div>
                            <br>
                            <button id="createDeepLinks" class="scroll-top b-button b-button__tertiary b-button__tertiary--light">
                                Create
                            </button>
                        </div>
                    </div>
                    <div class="col-md-9">
                        <div id="target" class="flexTable datasheet-dispatcher-main-table"
                        ${ withDescription ? `
                             data-dd-no-cap="Article"
                             data-dd-lang-cap="Langue" data-dd-sku-link-cap="All Resources"
                             data-dd-pdf-link-cap="Product specifications as PDF"
                             data-dd-spec-link-cap="Product specs"
                             ` : '' }
                        >
                        </div>
                    </div>
                </div>
                <script id="mustache-datasheet-result" type="text/mustache">
                  <div class="dispatcher-table_row-parent row">
                      <div class="r-dispatcher-table_row-article dispatcher-table_row__header col-sm-3 col-md-3 dsp-table-heading ">
                          <span class="dsp-table-heading__first-span">{{i18n.articleNumber}} </span></div>
                      <div class="dispatcher-table_row col-sm-9 col-md-9">
                          <div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3 dsp-table-heading">{{i18n.language}}</span>
                              <span class="col-sm-3 col-md-3 dsp-table-heading"> {{i18n.productSpec}} </span> <span
                                      class="col-sm-3 col-md-3 dsp-table-heading"> {{i18n.productSpecAsPdf}} </span> <span
                                      class="col-sm-3 col-md-3 dsp-table-heading"> {{i18n.resources}} </span></div>
                      </div>
                  </div>
                  {{#datasheetGen}}
                  <div class="dispatcher-table_row-parent row">
                      <div class="datasheet-dispatcher-main-table__row-article col-sm-3 col-md-3"><a
                              href="{{skuLink}}" target="_blank">{{skuId}}</a></div>
                      <div class="dispatcher-table_row col-sm-9 col-md-9">
                      {{#skuDeepLinksList}}
                          <div class="dispatcher-table_row-docx row">
                              <span class="col-sm-3 col-md-3">{{locale}}</span>
                              <span class="col-sm-3 col-md-3">{{#skuLink}}<a href="{{skuLink}}#Specifications" target="_blank"><i class="icon icon-list-items" aria-hidden="true"></i></a>{{/skuLink}}{{^skuLink}}No Data{{/skuLink}}</span>
                              <span class="col-sm-3 col-md-3">{{#downloadPDF}}<a href="{{downloadPDF}}" target="_blank"><img src="/content/dam/eaton/resources/icons/datasheet/pdf.png" class="datasheet_icon" alt=""></a>{{/downloadPDF}}{{^downloadPDF}}No Data{{/downloadPDF}}</span>
                              <span class="col-sm-3 col-md-3">{{#downloadXls}}<a href="{{downloadXls}}#Resources" target="_blank"><i class="icon icon-doc-download" aria-hidden="true"></i></a>{{/downloadXls}}{{^downloadXls}}No Data{{/downloadXls}}</span>
                          </div>
                      {{/skuDeepLinksList}}
                      </div>
                  </div>
                  {{/datasheetGen}}
                </script>
                <script id="mustache-datasheet-email" type="text/mustache">
                  {{#datasheetGen}}
                    <p>
                        <b style='font-family:Arial, Helvetica, sans-serif; font-size:17px;'>{{skuId}}</b>
                        {{#skuDeepLinksList}}
                        <span style='font-family:Arial, Helvetica, sans-serif; font-size:15px;'><br>{{locale}}</span>
                        <br> Product specs : {{#skuLink}}<a href='{{{skuLink}}}#Specifications' target='_blank' style='font-family:Arial, Helvetica, sans-serif; font-size:14px;'>{{{skuLink}}}#Specifications</a>{{/skuLink}}{{^skuLink}}<span style='font-family:Arial, Helvetica, sans-serif; font-size:14px;'>No Data</span>{{/skuLink}}
                        <br> Product specifications as PDF : {{#downloadPDF}}<a href='{{{downloadPDF}}}' target='_blank' style='font-family:Arial, Helvetica, sans-serif; font-size:14px;'>{{{downloadPDF}}}</a>{{/downloadPDF}}{{^downloadPDF}}<span style='font-family:Arial, Helvetica, sans-serif; font-size:14px;'>No Data</span>{{/downloadPDF}}
                        <br> All Resources : {{#downloadXls}}<a href='{{{downloadXls}}}#Resources' target='_blank' style='font-family:Arial, Helvetica, sans-serif; font-size:14px;'>{{{downloadXls}}}#Resources</a>{{/downloadXls}}{{^downloadXls}}<span style='font-family:Arial, Helvetica, sans-serif; font-size:14px;'>No Data</span>{{/downloadXls}}
                        {{/skuDeepLinksList}}
                    </p>
                  {{/datasheetGen}}
                </script>
                <div class="modal fade" id="dispatcher_modal" role="dialog">
                    <div class="modal-dialog datasheet-dispatch__models-popup-innerdiv">
                        <!-- Modal content-->
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close close-popup" data-dismiss="modal">×</button>
                            </div>
                            <div class="modal-body">
                                <h4 class="modal-heading ">Error </h4>
                                <p class="modal-part ">The task can't be completed.</p>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="b-button b-button__tertiary b-button__tertiary--light"
                                        data-dismiss="modal">Ok
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal fade" id="dispatcher_learn_more_modal" role="dialog">
                    <div class="modal-dialog modal-lg datasheet-dispatch__models-popup-innerdiv">
                        <!-- Modal content-->
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close close-popup" data-dismiss="modal">×</button>
                            </div>
                            <div class="modal-body ">
                                <h4 class="modal-heading">Data format<span> Description and example</span></h4>
                                <ol>
                                    <li>Supported file types
                                        <p>
                                            Only Excel97-2016 files (.xlsx) are supported.</p>
                                    </li>
                                    <li>Maximum file size and products
                                        <p>The maximum allowed file size is 2MB.</p>
                                        <p>Furthermore, only the first 20 article numbers contained in the file are processed.</p>
                                    </li>
                                    <li>Data format
                                        <ul>
                                            <li>Only the first sheet of the Excel file is processed</li>
                                            <li>The article numbers have to appear in the first column of the sheet</li>
                                            <li>The cells containing the article numbers should be formatted as text to preserve
                                                leading zeroes and to prevent Excel from interpreting and formatting the cell value
                                            </li>
                                            <li>The first row is excepected to contain column headers and is therefore ignored</li>
                                        </ul>
                                    </li>
                                </ol>
                                <p class=""> Download the <a href="/content/dam/eaton/resources/datasheet-dispatch/example.xlsx"
                                                             target="_blank" rel="noopener noreferrer"> example file
                                </a><i>-&gt;</i>"</p>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="b-button b-button__tertiary b-button__tertiary--light"
                                        data-dismiss="modal">Ok
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal fade small_poup" id="locale_error" role="dialog">
                    <div class="modal-dialog datasheet-dispatch__models-popup-innerdiv">
                        <!-- Modal content-->
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close close-popup" data-dismiss="modal">×</button>
                            </div>
                            <div class="modal-body">
                                <p class="">Please select minimum one language.</p>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="b-button b-button__tertiary b-button__tertiary--light"
                                        data-dismiss="modal">Ok
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal fade small_poup" id="create_error" role="dialog">
                    <div class="modal-dialog  datasheet-dispatch__models-popup-innerdiv">
                        <!-- Modal content-->
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close close-popup" data-dismiss="modal">×</button>
                            </div>
                            <div class="modal-body">
                                <p class="">Please enter catalog number.</p>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="b-button b-button__tertiary b-button__tertiary--light"
                                        data-dismiss="modal">Ok
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal fade small_poup" id="space_error" role="dialog">
                    <div class="modal-dialog datasheet-dispatch__models-popup-innerdiv">
                        <!-- Modal content-->
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close close-popup" data-dismiss="modal">×</button>
                            </div>
                            <div class="modal-body">
                                <p class="">Please make sure you are using only commas as separators and you are not using line
                                    breaks or spaces.</p>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="b-button b-button__tertiary b-button__tertiary--light"
                                        data-dismiss="modal">Ok
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal fade small_poup" id="above_sku_error" role="dialog">
                    <div class="modal-dialog datasheet-dispatch__models-popup-innerdiv">
                        <!-- Modal content-->
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close close-popup" data-dismiss="modal">×</button>
                            </div>
                            <div class="modal-body">
                                <p class="">You can not request more than 20 datasheets at the same time.</p>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="b-button b-button__tertiary b-button__tertiary--light"
                                        data-dismiss="modal">Ok
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal fade small_poup" id="dispatcher_Email_modal" role="dialog">
                    <div class="modal-dialog datasheet-dispatch__models-popup-innerdiv">
                        <!-- Modal content-->
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close close-popup" data-dismiss="modal">×</button>
                            </div>
                            <div class="modal-body">
                                <div class="mail-box">
                                    <!--<sly data-sly-use.chatConfig="com.eaton.platform.core.models.ChatConfigModel"></sly>
                                    <sly data-sly-test="false">
                                      <sly data-sly-resource="content"></sly>
                                    </sly>-->
                                    <div class="mail-box">
                                        <p>Enter your email address</p>
                                        <input type="text" id="txtEmail" placeholder="Email">
                                        <p class="email-error"></p>
                                        <p class="email-success"></p>
                                        <button type="button" class="b-button b-button__tertiary b-button__tertiary--light"
                                                data-dismiss="modal">Cancel
                                        </button>
                                        <button id="MailButton" class="b-button b-button__tertiary b-button__tertiary--light">Send
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
  },
  expectedTableInnerHtmlSingleLocaleSingleSku: () => {
    return '<div class="dispatcher-table_row-parent row"><div class="r-dispatcher-table_row-article dispatcher-table_row__header col-sm-3 col-md-3 dsp-table-heading "><span class="dsp-table-heading__first-span">Article </span></div><div class="dispatcher-table_row col-sm-9 col-md-9"><div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3 dsp-table-heading">Langue</span><span class="col-sm-3 col-md-3 dsp-table-heading"> Product specs </span> <span class="col-sm-3 col-md-3 dsp-table-heading"> Product specifications as PDF </span> <span class="col-sm-3 col-md-3 dsp-table-heading"> All Resources </span></div></div></div><div class="dispatcher-table_row-parent row"><div class="datasheet-dispatcher-main-table__row-article col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.BR120.html" target="_blank">BR120</a></div><div class="dispatcher-table_row col-sm-9 col-md-9"><div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3">English</span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.BR120.html#Specifications" target="_blank"><i class="icon icon-list-items" aria-hidden="true"></i></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.BR120.pdf" target="_blank"><img src="/content/dam/eaton/resources/icons/datasheet/pdf.png" class="datasheet_icon" alt=""></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.BR120.html#Resources" target="_blank"><i class="icon icon-doc-download" aria-hidden="true"></i></a></span></div></div></div>';
  },
  expectedTableInnerHtmlMultipleLocaleSingleSku: () => {
    return '<div class="dispatcher-table_row-parent row"><div class="r-dispatcher-table_row-article dispatcher-table_row__header col-sm-3 col-md-3 dsp-table-heading "><span class="dsp-table-heading__first-span">Article </span></div><div class="dispatcher-table_row col-sm-9 col-md-9"><div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3 dsp-table-heading">Langue</span><span class="col-sm-3 col-md-3 dsp-table-heading"> Product specs </span> <span class="col-sm-3 col-md-3 dsp-table-heading"> Product specifications as PDF </span> <span class="col-sm-3 col-md-3 dsp-table-heading"> All Resources </span></div></div></div><div class="dispatcher-table_row-parent row"><div class="datasheet-dispatcher-main-table__row-article col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.BR120.html" target="_blank">BR120</a></div><div class="dispatcher-table_row col-sm-9 col-md-9"><div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3">English</span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.BR120.html#Specifications" target="_blank"><i class="icon icon-list-items" aria-hidden="true"></i></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.BR120.pdf" target="_blank"><img src="/content/dam/eaton/resources/icons/datasheet/pdf.png" class="datasheet_icon" alt=""></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.BR120.html#Resources" target="_blank"><i class="icon icon-doc-download" aria-hidden="true"></i></a></span></div><div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3">German</span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/de/de-de/skuPage.BR120.html#Specifications" target="_blank"><i class="icon icon-list-items" aria-hidden="true"></i></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/de/de-de/skuPage.BR120.pdf" target="_blank"><img src="/content/dam/eaton/resources/icons/datasheet/pdf.png" class="datasheet_icon" alt=""></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/de/de-de/skuPage.BR120.html#Resources" target="_blank"><i class="icon icon-doc-download" aria-hidden="true"></i></a></span></div><div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3">French</span><span class="col-sm-3 col-md-3">No Data</span><span class="col-sm-3 col-md-3">No Data</span><span class="col-sm-3 col-md-3">No Data</span></div></div></div>';
  },
  expectedTableInnerHtmlMultipleLocaleMultipleSku: () => {
    return '<div class="dispatcher-table_row-parent row"><div class="r-dispatcher-table_row-article dispatcher-table_row__header col-sm-3 col-md-3 dsp-table-heading "><span class="dsp-table-heading__first-span">Article </span></div><div class="dispatcher-table_row col-sm-9 col-md-9"><div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3 dsp-table-heading">Langue</span><span class="col-sm-3 col-md-3 dsp-table-heading"> Product specs </span> <span class="col-sm-3 col-md-3 dsp-table-heading"> Product specifications as PDF </span> <span class="col-sm-3 col-md-3 dsp-table-heading"> All Resources </span></div></div></div><div class="dispatcher-table_row-parent row"><div class="datasheet-dispatcher-main-table__row-article col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.BR120.html" target="_blank">BR120</a></div><div class="dispatcher-table_row col-sm-9 col-md-9"><div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3">English</span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.BR120.html#Specifications" target="_blank"><i class="icon icon-list-items" aria-hidden="true"></i></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.BR120.pdf" target="_blank"><img src="/content/dam/eaton/resources/icons/datasheet/pdf.png" class="datasheet_icon" alt=""></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.BR120.html#Resources" target="_blank"><i class="icon icon-doc-download" aria-hidden="true"></i></a></span></div><div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3">German</span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/de/de-de/skuPage.BR120.html#Specifications" target="_blank"><i class="icon icon-list-items" aria-hidden="true"></i></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/de/de-de/skuPage.BR120.pdf" target="_blank"><img src="/content/dam/eaton/resources/icons/datasheet/pdf.png" class="datasheet_icon" alt=""></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/de/de-de/skuPage.BR120.html#Resources" target="_blank"><i class="icon icon-doc-download" aria-hidden="true"></i></a></span></div><div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3">French</span><span class="col-sm-3 col-md-3">No Data</span><span class="col-sm-3 col-md-3">No Data</span><span class="col-sm-3 col-md-3">No Data</span></div></div></div><div class="dispatcher-table_row-parent row"><div class="datasheet-dispatcher-main-table__row-article col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.CH120.html" target="_blank">CH120</a></div><div class="dispatcher-table_row col-sm-9 col-md-9"><div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3">English</span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.CH120.html#Specifications" target="_blank"><i class="icon icon-list-items" aria-hidden="true"></i></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.CH120.pdf" target="_blank"><img src="/content/dam/eaton/resources/icons/datasheet/pdf.png" class="datasheet_icon" alt=""></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/us/en-us/skuPage.CH120.html#Resources" target="_blank"><i class="icon icon-doc-download" aria-hidden="true"></i></a></span></div><div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3">German</span><span class="col-sm-3 col-md-3">No Data</span><span class="col-sm-3 col-md-3">No Data</span><span class="col-sm-3 col-md-3">No Data</span></div><div class="dispatcher-table_row-docx row"><span class="col-sm-3 col-md-3">French</span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/fr/fr-fr/skuPage.CH120.html#Specifications" target="_blank"><i class="icon icon-list-items" aria-hidden="true"></i></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/fr/fr-fr/skuPage.CH120.pdf" target="_blank"><img src="/content/dam/eaton/resources/icons/datasheet/pdf.png" class="datasheet_icon" alt=""></a></span><span class="col-sm-3 col-md-3"><a href="https://www-local.eaton.com/fr/fr-fr/skuPage.CH120.html#Resources" target="_blank"><i class="icon icon-doc-download" aria-hidden="true"></i></a></span></div></div></div>';
  }
};
