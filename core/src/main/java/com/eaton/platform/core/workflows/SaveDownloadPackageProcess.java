package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.AssetDownloadConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AssetDownloadPackageDataService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


@Component(service = WorkflowProcess.class,immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Eaton save asset download package process workflow",
                AEMConstants.SERVICE_VENDOR_EATON,
        })
public class SaveDownloadPackageProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(SaveDownloadPackageProcess.class);

    @Reference
    private AssetDownloadPackageDataService assetDownloadPackageDataService;

    @Override
    public void execute(final WorkItem item, final WorkflowSession workflowSession, final MetaDataMap args) throws WorkflowException {
        LOG.info("Call Save Download Package Process Workflow");

        final MetaDataMap wfMetaData = item.getWorkflowData().getMetaDataMap();

        final String resourcePath = wfMetaData.get(AssetDownloadConstants.PARAMETER_KEY_RESOURCE_PATH, StringUtils.EMPTY);
        final String downloadPackageIdentifier;

        try {
            downloadPackageIdentifier = assetDownloadPackageDataService.saveAssetDownloadPackageData(resourcePath, getAssetDownloadPackageDataMap(wfMetaData));
            wfMetaData.put(AssetDownloadConstants.PARAMETER_KEY_DOWNLOAD_PACKAGE_IDENTIFIER, downloadPackageIdentifier);
        } catch (PersistenceException e) {
            throw new WorkflowException("Unable to process further workflow from SaveDownloadPackageProcess::: "+e.getMessage());
        }

        LOG.info("End the Save Download Package Process Workflow");
    }

    private Map<String, Object> getAssetDownloadPackageDataMap(final MetaDataMap wfMetaDataMap) {
        final Map<String, Object> assetDownloadPackageDataMap = new HashMap<String, Object>();
        assetDownloadPackageDataMap.put(AssetDownloadConstants.PARAMETER_KEY_DOWNLOAD_FILE_NAME,
                wfMetaDataMap.get(AssetDownloadConstants.PARAMETER_KEY_DOWNLOAD_FILE_NAME, String.class));
        assetDownloadPackageDataMap.put(AssetDownloadConstants.PARAMETER_KEY_DOWNLOAD_ASSET_PATHS,
                wfMetaDataMap.get(AssetDownloadConstants.PARAMETER_KEY_DOWNLOAD_ASSET_PATHS, new String[0]));
        assetDownloadPackageDataMap.put(AssetDownloadConstants.PARAMETER_KEY_MERGE_ASSETS_TO_SINGLE_PDF,
                wfMetaDataMap.get(AssetDownloadConstants.PARAMETER_KEY_MERGE_ASSETS_TO_SINGLE_PDF, Boolean.class));
        assetDownloadPackageDataMap.put(AssetDownloadConstants.PARAMETER_KEY_MERGE_ASSETS_FILE_NAME,
                wfMetaDataMap.get(AssetDownloadConstants.PARAMETER_KEY_MERGE_ASSETS_FILE_NAME, String.class));
        return assetDownloadPackageDataMap;
    }
}