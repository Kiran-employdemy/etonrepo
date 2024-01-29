/*******************************************************************************
 * Copyright 2020 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
 (function() {
   'use strict';

   const NS = 'cmp';
   const IS = 'pdfviewer';
   const SDK_URL = 'https://documentservices.adobe.com/view-sdk/viewer.js';
   const SDK_READY_EVENT = 'adobe_dc_view_sdk.ready';

   const selectors = {
     self: '[data-' + NS + '-is="' + IS + '"]',
     sdkScript: 'script[src="' + SDK_URL + '"]'
   };

   function initSDK() {
     const sdkIncluded = document.querySelectorAll(selectors.sdkScript).length > 0;
     if (!window.adobe_dc_view_sdk && !sdkIncluded) {
       const dcv = document.createElement('script');
       dcv.type = 'text/javascript';
       dcv.src = SDK_URL;
       document.body.appendChild(dcv);
     }
   }

   function previewPdf(component) {
     // prevents multiple initialization
     component.removeAttribute('data-' + NS + '-is');

     // add the view sdk to the page
     initSDK();

     // manage the preview
     if (component.dataset && component.id) {
       if (window.AdobeDC && window.AdobeDC.View) {
         dcView(component);
       } else {
         document.addEventListener(SDK_READY_EVENT, function() {
           dcView(component);
         });
       }
     }
   }

   function dcView(component) {
     const adobeDCView = new window.AdobeDC.View({
       clientId: component.dataset.cmpClientId,
       divId: component.id + '-content',
       reportSuiteId: component.dataset.cmpReportSuiteId
     });
     adobeDCView.previewFile({
       content: { location: { url: component.dataset.cmpDocumentPath } },
       metaData: { fileName: component.dataset.cmpDocumentFileName }
     }, JSON.parse(component.dataset.cmpViewerConfigJson));
   }

  /**
   * Document ready handler and DOM mutation observers. Initializes Accordion components as necessary.
   *
   * @private
   */
   function onDocumentReady() {
     const elements = document.querySelectorAll(selectors.self);
     for (let i = 0; i < elements.length; i++) {
       previewPdf(elements[i]);
     }

     const MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
     const body = document.querySelector('body');
     const observer = new MutationObserver(function(mutations) {
       mutations.forEach(function(mutation) {
         // needed for IE
         const nodesArray = [].slice.call(mutation.addedNodes);
         if (nodesArray.length > 0) {
           nodesArray.forEach(function(addedNode) {
             if (addedNode.querySelectorAll) {
               const elementsArray = [].slice.call(addedNode.querySelectorAll(selectors.self));
               elementsArray.forEach(function(element) {
                 previewPdf(element);
               });
             }
           });
         }
       });
     });

     observer.observe(body, {
       subtree: true,
       childList: true,
       characterData: true
     });

   }

   if (document.readyState !== 'loading') {
     onDocumentReady();
   } else {
     document.addEventListener('DOMContentLoaded', onDocumentReady);
   }
 }());
