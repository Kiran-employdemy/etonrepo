<%--
  ADOBE CONFIDENTIAL

  Copyright 2016 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page import="static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT,
                  static com.day.cq.dam.api.DamConstants.RENDITIONS_FOLDER,
                  static com.day.cq.dam.api.DamConstants.ORIGINAL_FILE,
                  static com.day.cq.dam.api.DamConstants.DAM_ASSET_STATE,
                  static com.day.cq.dam.api.DamConstants.DAM_ASSET_STATE_UNPROCESSED,
                  com.day.cq.workflow.status.WorkflowStatus,
                  com.day.cq.dam.api.ui.editor.metadata.MetadataEditorHelper,
                  com.day.cq.dam.entitlement.api.EntitlementConstants,
                  com.day.cq.dam.commons.util.S73DConstants,
                  com.day.cq.dam.commons.util.S73DHelper,
                  com.day.cq.i18n.I18n,
			      org.apache.sling.event.jobs.JobManager,
				  org.apache.sling.event.jobs.Job,
				  org.apache.sling.event.jobs.Job.JobState,
                  com.adobe.granite.xss.XSSAPI,
                  com.adobe.granite.ui.components.AttrBuilder,
                  org.apache.sling.api.resource.ValueMap,
                  org.apache.sling.featureflags.Features,
                  com.day.cq.dam.commons.util.DamUtil"%>
<%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0"%><%
%><%@include file="/libs/dam/gui/coral/components/admin/contentrenderer/base/base.jsp"%><%
%><%@include file="/libs/dam/gui/coral/components/admin/contentrenderer/base/assetBase.jsp"%><%
    Node resourceNode = request.getAttribute(RESOURCE_NODE) != null ? (Node) request.getAttribute(RESOURCE_NODE) : null;
    boolean isNew = isNew(resourceNode);
    boolean isRecent = isRecent(resourceNode);
    boolean isRecentCreated = isRecentCreated(resourceNode);
    //running workflow status
    WorkflowStatus wfState = resource.adaptTo(WorkflowStatus.class);
    boolean inWorkflow = false;
    String printJobStatus = "finished";
    String PROPERTY_STATUS = "status";
    String METADATA_MISSING_STATUS = "Required Metadata Missing";

    String status = null;
    String damAssetState = null;
    String resourcePath = resource.getPath();
    String encodedResourcePath = xssAPI.encodeForHTMLAttr(resourcePath);

    boolean enabledS7config = false;
    final Features featureManager = sling.getService(Features.class);
    if (featureManager.getFeature(EntitlementConstants.ASSETS_SCENE7_FEATURE_FLAG_PID)!=null &&
            featureManager.isEnabled(EntitlementConstants.ASSETS_SCENE7_FEATURE_FLAG_PID)) {
        enabledS7config = true;
    }

    boolean isVideo = false;
    boolean isImage = false;

    String type = resource.adaptTo(Asset.class).getMimeType();
    if(type==null || type.isEmpty()) {
        type = request.getAttribute(MIMETYPE) != null ? (String) request.getAttribute(MIMETYPE) : "unknown";
        if (type==null || type.isEmpty()) {
            //log.error("EMPTY TYPE RETURNED FROM request getAttribute MIMETYPE");
            // What else can we do ?
            type = "unknown";
        }
    }
    isVideo = type.contains("video");
    isImage = type.contains("image");

    boolean isDynamicMediaEntitled = false;
    if (featureManager.getFeature(EntitlementConstants.ASSETS_DYNAMICMEDIA_FEATURE_FLAG_PID)!=null &&
            featureManager.isEnabled(EntitlementConstants.ASSETS_DYNAMICMEDIA_FEATURE_FLAG_PID)) {
        isDynamicMediaEntitled = true;
    }

    try {

        inWorkflow = (wfState != null && wfState.isInRunningWorkflow(true));

        // processing status on the original rendition is required to determine if the video asset processing has failed
        Resource original = resource.getResourceResolver().getResource(resourcePath + "/" + JCR_CONTENT + "/" +
                RENDITIONS_FOLDER + "/" + ORIGINAL_FILE);
        if (original != null) {
            Resource jcrContent = resource.getResourceResolver().getResource(resourcePath + "/" + JCR_CONTENT);
            ValueMap properties = jcrContent.getValueMap();
            status = (properties.containsKey(PROPERTY_STATUS) ? properties.get(PROPERTY_STATUS, String.class) : null);
            damAssetState = (properties.containsKey(DamConstants.DAM_ASSET_STATE) ? properties.get(DamConstants.DAM_ASSET_STATE, String.class) : null);
        }

        if ((isDynamicMediaEntitled || enabledS7config) && isVideo && (wfState == null || !inWorkflow)) {
            wfState = (original == null) ? wfState : original.adaptTo(WorkflowStatus.class);
            inWorkflow = (wfState != null && wfState.isInRunningWorkflow(true));
        }

        //for print assets only
        JobManager jobMgr = sling.getService(org.apache.sling.event.jobs.JobManager.class);
        printJobStatus = getPrintAssetStatus(resource,jobMgr);

    } catch(NullPointerException e){
        // CQ-77820 [Upgrade] Workflow service user wiped out on upgrade
        log.error("error determining the workflow status for asset", e);
    }




    ResourceResolver resolver = slingRequest.getResourceResolver();
    MetadataEditorHelper meh = sling.getService(MetadataEditorHelper.class);
    boolean missingMeta = !hasValidMetadata(resource, meh);

    /*
     * Considerations for video being in workflow:
     * Scenario A: DM-enabled with DM Encode Video enabled by default - use WorkflowStatus.isInRunningWorkflow()
     * Scenario B: DM-enabled with DM Enabled Video disabled to use other processing, e.g. ffmpeg - use DamUtil.isInRunningWorkflow
     * Scenario C: DM-disabled, use other processing by default e.g. ffmpeg
     */
    boolean isAssetValid = true;

    // Bypass DamUtil.isInRunningWorkflow() for DM video encode in Scenario A, as DamUtil.isInRunningWorkflow(resource) checks the value of the assetState
    // property in jcr:content which does not accurately returns the correct state when a workflow has been
    // terminated or suspended.  We based on the assumption that status will be set to some value by the non-transient
    // workflow when it was terminated.
    if (!((isDynamicMediaEntitled || enabledS7config) && isVideo && status != null)) {
        // Apply only to non DM and non-video processing because DamUtil.isInRunningWorkflow does not reflect the
        // accurate in running workflow status when a workflow has been terminated or suspended.
        // CQ-4217280: for recently uploaded images, consider assets that do not have "processed" dam:assetState as
        // being in workflow to show processing status and to prevent showing New tag on unprocessed asset.
        // Not "processed" includes non-existent dam:assetState property or property with values unprocessed or processing
        inWorkflow = (inWorkflow || DamUtil.isInRunningWorkflow(resource) ||
                (isRecent && isImage && !DamConstants.DAM_ASSET_STATE_PROCESSED.equals(damAssetState)));
    }

    /**
     * Update is not required if Finished and Error status has been reported
     */
    boolean updateIsRequired = inWorkflow && isVideo;

    /**
     * Require deferred content reload for recent videos upload or copy and paste to provide an opportunity to
     * 1) reflect the processing status for videos that have not enter workflow or have the dam:assetState updated to
     * "processing" before initial content reload after video upload, and
     * 2) rebuild card view for pasted videos that could have copied the invalid status from the source video.
     */
    boolean reloadIsRequired = isRecentCreated && isVideo;

    // Check if encoding has failed when it
    // 1) is not in workflow
    // 2) DM is enabled and
    // 3) is a video
    // 4) does not have any renditions
    if (!inWorkflow && (isDynamicMediaEntitled || enabledS7config) && isVideo) {
        // for non-DM-encoded videos, checks if video has any renditions to indicate successful non-DM processing
        // for DMS7 runmode, check if video has dam:s7damType equals to Asset
        if (!isEncoded(resource) && !hasRendition(resource) && !(enabledS7config && isAsset(resource))) {
            isAssetValid = false;
        }
    }

    // SPECIAL TREATMENT FOR 3D ASSET (and progressed asset) - NEED TO UNIFY THIS WITH THE REGULAR CARD VIEW
    // Use resource extension to determine if it is a 3D type since we may still be processing
    //boolean is3D = request.getAttribute(IS_3D_ASSET) != null ? (boolean) request.getAttribute(IS_3D_ASSET) : false;
    boolean is3DMimeType = S73DHelper.is3DMimeType(resource);

    // also check for 3D related Operation being applied to asset
    Resource progressResource = resource.getChild("jcr:content/progress");
    ValueMap progressProps = (progressResource != null) ? progressResource.getValueMap() : null;
    String operation = (progressProps != null) ? progressProps.get("Operation", String.class) : "";
    boolean OP3DinProgress = !operation.isEmpty() && !(S73DConstants.S73D_NO_OP.equals(operation));

    String alertContent = "";
    String variant = "info";
    String alertStyle = null;
    boolean hasDiv = false;
    Integer progress = 0;
    boolean hasAlert = true;

    String CONST_STATUS_FINISHED    = "Finished";
    int idx;
    AttrBuilder attrNone3DAlert = null;
    AttrBuilder attrNewTag = null;
    String contentNewTag = xssAPI.encodeForHTML(i18n.get("New"));
    AttrBuilder attrAlert = new AttrBuilder(request, xssAPI);
    AttrBuilder attrDiv = null;
    AttrBuilder attrSpan = null;
    AttrBuilder attrStockLicensedTag = null;
    String contentAttrLicensedTag = xssAPI.encodeForHTML(i18n.get("Licensed"));
    boolean isStockAsset = request.getAttribute(IS_STOCK_ASSET) != null ? (boolean) request.getAttribute(IS_STOCK_ASSET) : false;
    boolean isStockAssetLicensed = request.getAttribute(IS_STOCK_ASSET_LICENSED) != null ? (boolean) request.getAttribute(IS_STOCK_ASSET_LICENSED) : false;

    boolean isFolder = resource.isResourceType("nt:folder") || resource.isResourceType("sling:Folder") || resource.isResourceType("sling:OrderedFolder");

    if(is3DMimeType || OP3DinProgress) {
        // ToDo: Unify 3D card with standard DAM
        if (!StringUtils.isEmpty(operation) && !S73DConstants.S73D_NO_OP.equals(operation)) {
            progress = (progressProps != null) ? progressProps.get("Progress", Integer.class) : 0;
        }

        String alertParams = (progressProps != null) ? progressProps.get("Style", String.class).substring(6).replaceAll("\"", "") : "";
        boolean isMissingDependencies = request.getAttribute(IS_MISSING_DEPENDENCIES) != null ? (boolean) request.getAttribute(IS_MISSING_DEPENDENCIES) : false;

        if(OP3DinProgress) {
            alertContent = xssAPI.encodeForHTML(i18n.getVar(operation));
            alertStyle = xssAPI.encodeForHTMLAttr(alertParams);
            hasDiv = true;
        } else if(inWorkflow && !isFolder) {
            alertContent = i18n.get("Processing 3D ...");
        } else if(isMissingDependencies) {
            alertContent = i18n.get("Unresolved Dependencies");
            variant = "error";
        } else if(missingMeta) {
            alertContent = i18n.get(METADATA_MISSING_STATUS);
            variant = "error";
        } else if(isNew) {
            hasAlert = false;
        } else {
            hasAlert = false;
        }
    } else { // fallback to non-3D treatment
        attrNone3DAlert = new AttrBuilder(request, xssAPI);
        if(updateIsRequired) { // do not monitor if "Finished" or "Error" status has been recorded
            // Include data properties required for monitored assets
            if(!(status != null && (status.equals(CONST_STATUS_FINISHED) || status.startsWith(CONST_STATUS_ERROR)))) {
                attrNone3DAlert.add("data-update-is-required", "true");
            }
            if (status != null) {
                attrNone3DAlert.add("data-status", xssAPI.encodeForHTMLAttr(status));
            }
        }
        if(reloadIsRequired) {
            attrNone3DAlert.add("data-reload-is-required", "true");
        }

        // omit banner if status is "Finished"
        // sometimes assets remain in workflow for a while after "Finished" is recorded
        // or remain in workflow due to previous failure
        if ((inWorkflow && (!isFolder) && (status == null || !status.equals(CONST_STATUS_FINISHED))) || !("finished".equals(printJobStatus))) {
            if ((status != null && status.startsWith(CONST_STATUS_ERROR)) || "failed".equals(printJobStatus)) {
                // set error banner if status starts with "Error"
                // sometimes assets remain in workflow for a while after "Error" is recorded
                // unused ??? String CONST_LABEL_ERROR = i18n.get("Encoding process failed");
                alertContent = formatErrorStatus(i18n, status);
                idx = alertContent != null ? alertContent.lastIndexOf('|') : -1;
                variant = idx != -1 ? alertContent.substring(idx + 1) : "error";
                alertContent = idx != -1 ? alertContent.substring(0, idx) : alertContent;
            } else  {
                alertContent = i18n.get("Processing ...");
            }
        } else if (missingMeta) {
            alertContent = i18n.get(METADATA_MISSING_STATUS);
            variant = "error";
        } else if(!isAssetValid) {
            // unused ??? String CONST_LABEL_ERROR = i18n.get("Encoding process failed");
            alertContent = formatErrorStatus(i18n, status);
            idx = alertContent != null ? alertContent.lastIndexOf('|') : -1;
            variant = idx != -1 ? alertContent.substring(idx + 1) : "error";
            alertContent = idx != -1 ? alertContent.substring(0, idx) : alertContent;
        } else if(isNew && (!isImage || DamConstants.DAM_ASSET_STATE_PROCESSED.equals(damAssetState))) {
            // only assign New tag for images that have been "processed"
            attrNewTag = new AttrBuilder(request, xssAPI);
            attrNewTag.add("color", "blue");
            attrNewTag.add("class", "u-coral-pullRight");
            hasAlert = false;
        } else if (isStockAsset && isStockAssetLicensed) {
            attrStockLicensedTag = new AttrBuilder(request, xssAPI);
            attrStockLicensedTag.add("color", "blue");
            attrStockLicensedTag.add("class", "u-coral-pullRight");
            hasAlert = false;
        } else {
            hasAlert = false;
        }
    }

    attrAlert.add("variant", variant);
    AttrBuilder attrAlertContent = new AttrBuilder(request, xssAPI);

    if(hasDiv) {
        attrAlert.add("style", alertStyle);
        attrAlertContent.add("class","coral-Alert-title");

        attrDiv = new AttrBuilder(request, xssAPI);
        attrDiv.add("class","text-ellipsis v3D-Banner");
        attrDiv.add("data-path", encodedResourcePath);

        attrSpan = new AttrBuilder(request, xssAPI);
        attrDiv.add("class","v3d-ProgressBanner-progress");
        attrDiv.add("style", "width: " + progress + "%");
    }

    String bannerContent = alertContent;
    if (METADATA_MISSING_STATUS.equals(bannerContent)) {
        bannerContent = METADATA_MISSING_STATUS.toUpperCase();
    }

    Object viewSettings = request.getAttribute(VIEW_SETTINGS);
    if (viewSettings != null) {
        String cardName = ((JSONObject)request.getAttribute(VIEW_SETTINGS)).getString(VIEW_SETTINGS_PN_NAME);
        if ("small".equals(cardName)) {
            if (bannerContent.length() > 8 && !bannerContent.endsWith("...")) {
                bannerContent = bannerContent.substring(0, 8) + " ...";
                attrAlert.add("title", alertContent);
            }
        }
    }
    // Eaton Rights Restricted Banners
    String contentPath = resourceNode.getPath() + "/jcr:content/metadata/";
    Resource res = resourceResolver.getResource(contentPath);
    String rightsRestricted = ResourceUtil.getValueMap(res).get("xmp:eaton-rights-restricted", "");
    boolean rightsAreRestricted = rightsRestricted.equalsIgnoreCase("yes");
    String contentRestrictedTag = xssAPI.encodeForHTML(i18n.get("Rights Restricted"));
    AttrBuilder attrRestrictedTag = null;
    if (rightsAreRestricted) {
        attrRestrictedTag = new AttrBuilder(request, xssAPI);
        attrRestrictedTag.add("color", "red");
        attrRestrictedTag.add("class", "u-coral-pullRight");
    }

%>

<coral-card-info <%= attrNone3DAlert != null ? attrNone3DAlert.build() : "" %>>
    <!-- EQS-53 - Eaton Rights Restricted Banner -->
    <% if(attrRestrictedTag != null) { %>
    <coral-tag <%= attrRestrictedTag.build()%>> <%= contentRestrictedTag%></coral-tag>
    <% } %>
    <% if(attrNewTag != null) { %>
    <coral-tag <%= attrNewTag.build()%>> <%= contentNewTag%></coral-tag>
    <% } if(attrStockLicensedTag != null) { %>
    <coral-tag <%= attrStockLicensedTag.build()%>>
        <coral-icon icon="checkCircle" size="XS"></coral-icon>
        <span><%= contentAttrLicensedTag%></span>
    </coral-tag>
    <% }
        if(hasAlert) { %>
    <coral-alert <%= attrAlert.build()%>>
        <% if(hasDiv) { %>
        <div <%= attrDiv.build()%>>
            <span <%= attrSpan.build()%>></span>
            <strong>
                <% } %>
                <coral-alert-content <%= attrAlertContent.build()%>><%= bannerContent %></coral-alert-content>
                <% if(hasDiv) { %>
            </strong>
        </div>
        <% } %>
    </coral-alert>
    <% } %>
</coral-card-info>

<%!
    String CONST_STATUS_ERROR = "Error";

    /**
     * Extract detailed information from status
     * @param status Status expected in format of Error[optionalDetailedInfo] where [...] is optional
     * @return return detailed information of the error, null otherwise
     */
    private String getErrorDetailInfo(String status) {
        if (status != null && status.startsWith(CONST_STATUS_ERROR)) {
            int startIdx        = status.indexOf("[");
            int endIdx          = status.indexOf("]");
            // extract status detailed information
            return ((startIdx != -1 && endIdx != -1) ? status.substring(startIdx + 1, endIdx) : "");
        }

        return null;
    }

    /**
     * Determine the error message by analyzing optional detailed information.
     * @param i18n API for localization
     * @param status Status expected in format of Error[optionalDetailedInfo] where [...] is optional
     * @return Error message to display and optional alert variant, separated by |
     */
    private String formatErrorStatus(I18n i18n, String status) {
        String CONST_LABEL_ERROR = i18n.get("Process failed");
        String CONST_LABEL_ERROR_NO_ENCODE =  i18n.get("No encodings processed");

        String CONST_STATUS_ERROR_DETAIL_NO_ENCODING = "No Encoding";

        String text = CONST_LABEL_ERROR;
        // provide different error message for "No Encoding" from
        // either no DMCS setup or no video profile associated to the parent folder.
        String detailedInfo = getErrorDetailInfo(status);
        if (detailedInfo != null && detailedInfo.equals(CONST_STATUS_ERROR_DETAIL_NO_ENCODING)) {
            text = CONST_LABEL_ERROR_NO_ENCODE + "|warning";
        }

        return text;
    }

    private boolean hasValidMetadata (Resource res, MetadataEditorHelper meh) {
        List<Resource> invalidItems = meh.getInvalidFormItems(res);
        if (null == invalidItems || invalidItems.size() == 0) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * Check if asset is print asset and if yes than, get print job status
     * status - finished,failed,pending
     */
    private String getPrintAssetStatus(Resource resource,JobManager jobMgr) {
        String printJobStatus = "finished";
        Node resNode = resource.adaptTo(Node.class);
        boolean isEditablePrintAsset = isEditablePrintAsset(resNode);
        boolean isMergedTemplate = isMergedPrintAsset(resNode);
        if (isEditablePrintAsset || isMergedTemplate) {
            // print asset , check running print job status
            String exportJobId = resource.getValueMap().get("jcr:content/exportJobId",String.class);
            if (exportJobId!=null){
                Job offloadingJob = jobMgr.getJobById((String) exportJobId);
                if (offloadingJob != null) {
                    JobState jobState = offloadingJob.getJobState();
                    if (jobState == JobState.QUEUED || jobState == jobState.ACTIVE) {
                        printJobStatus = "pending";
                    } else if (jobState != JobState.SUCCEEDED) {
                        printJobStatus = "failed";
                    }
                }
            }
        }
        return printJobStatus;
    }

%>
