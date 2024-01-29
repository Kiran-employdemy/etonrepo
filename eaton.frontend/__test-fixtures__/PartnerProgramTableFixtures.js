module.exports = {
  partnerProgramBlankTable: () => {
    return `
      <div class="accordion-component aem-GridColumn aem-GridColumn--default--12">
   <div class="accordion container">
      <div class="accordion__option">
      </div>
      <div class="panel-group" id="accordion--994264444" role="tablist" aria-multiselectable="true" data-default-closed="false">
         <div class="panel" id="panel--994264444-0">
            <div class="panel__header" role="tab" id="heading--994264444-0">
               <div class="panel__header__title" role="button" data-toggle="collapse" href="#collapse--994264444-0" aria-expanded="true" aria-controls="collapse--994264444-0">
                  <h2>Partner program type and tier level</h2>
                  <i class="panel__header__icon"></i>
               </div>
            </div>
            <div id="collapse--994264444-0" class="panel-collapse first-panel collapse in" role="tabpanel" aria-labelledby="heading--994264444-0" aria-expanded="true" style="">
               <div class="panel__body">
                  <div class="aem-Grid aem-Grid--12 aem-Grid--default--12 ">
                     <div class="partner-program-table aem-GridColumn aem-GridColumn--default--12">
                        <div class="table-flex">
                           <div class="container">
                              <div class="eaton-custom-table">
                                 <table class="table partner-program-table table-responsive table-bordered" data-resource="/content/eaton/us/en-us/secure/profile/jcr:content/root/responsivegrid/securelinkscontainer/secure-par/accordion_component_1614835261/par0/partner_program_tabl">
                                    <thead>
                                       <tr>
                                          <th>Partner program type</th>
                                          <th>Tier level</th>
                                       </tr>
                                    </thead>
                                    <tbody>
                                    </tbody>
                                 </table>
                              </div>
                           </div>
                        </div>
                     </div>
                  </div>
                  <span class="panel__body__fix">spacer</span>
               </div>
            </div>
         </div>
      </div>
   </div>
</div>
   `;
  },

  partnerProgramFilledTableRows: () => {
    return `
        <tr><td>Electrical Wholesaler</td><td>Registered</td></tr><tr><td>Electrical Distributor</td><td>Authorized</td></tr><tr><td>Electrical Customer</td><td></td></tr>
    `;
  },

  partnerProgramTestData: () => {

    let response = {
      data: [
        'Electrical Wholesaler|Registered',
        'Electrical Distributor|Authorized',
        'Electrical Customer'
      ]
    };
    return response;
  },

  additionalAccordions: () => {
    return `
            <div class="accordion-component aem-GridColumn aem-GridColumn--default--12">
         <div class="accordion container">
            <div class="accordion__option">
            </div>
            <div class="panel-group" id="accordion-918678258" role="tablist" aria-multiselectable="true" data-default-closed="false">
               <div class="panel" id="panel-918678258-0">
                  <div class="panel__header" role="tab" id="heading-918678258-0">
                     <div class="panel__header__title" role="button" data-toggle="collapse" href="#collapse-918678258-0" aria-expanded="true" aria-controls="collapse-918678258-0">
                        <h2>Test 1</h2>
                        <i class="panel__header__icon"></i>
                     </div>
                  </div>
                  <div id="collapse-918678258-0" class="panel-collapse first-panel collapse in" role="tabpanel" aria-labelledby="heading-918678258-0" aria-expanded="true" style="">
                     <div class="panel__body">
                        <div class="aem-Grid aem-Grid--12 aem-Grid--default--12 ">
                           <div class="table parbase aem-GridColumn aem-GridColumn--default--12">
                           </div>
                        </div>
                        <span class="panel__body__fix">spacer</span>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </div>
      <div class="accordion-component aem-GridColumn aem-GridColumn--default--12">
         <div class="accordion container">
            <div class="accordion__option">
            </div>
            <div class="panel-group" id="accordion-918678259" role="tablist" aria-multiselectable="true" data-default-closed="false">
               <div class="panel" id="panel-918678259-0">
                  <div class="panel__header" role="tab" id="heading-918678259-0">
                     <div class="panel__header__title" role="button" data-toggle="collapse" href="#collapse-918678259-0" aria-expanded="true" aria-controls="collapse-918678259-0">
                        <h2>Test 2</h2>
                        <i class="panel__header__icon"></i>
                     </div>
                  </div>
                  <div id="collapse-918678259-0" class="panel-collapse first-panel collapse in" role="tabpanel" aria-labelledby="heading-918678259-0" aria-expanded="true" style="">
                     <div class="panel__body">
                        <div class="aem-Grid aem-Grid--12 aem-Grid--default--12 ">
                           <div class="table parbase aem-GridColumn aem-GridColumn--default--12">
                           </div>
                        </div>
                        <span class="panel__body__fix">spacer</span>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </div>
    `
  },

  wcmEditMode: () => {
    return `
    <div class="wcm-edit-mode partner-program-table"></div>
    `;
  },

  partnerProgramLookupAjaxRequest : () => {

    return {
      type: 'GET',
      url: '/content/eaton/us/en-us/secure/profile/jcr:content/root/responsivegrid/securelinkscontainer/secure-par/accordion_component_1614835261/par0/partner_program_tabl.nocache.json',
      cache: false,
      headers: { 'Content-Type': 'application/json' },
      success: expect.any(Function),
      error: expect.any(Function),
    };
  },
};
