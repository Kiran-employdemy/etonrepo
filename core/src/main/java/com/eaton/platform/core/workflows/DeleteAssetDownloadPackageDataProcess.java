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
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = WorkflowProcess.class,immediate = true,
    property = {
        AEMConstants.SERVICE_DESCRIPTION + "Eaton delete asset download package data process workflow",
        AEMConstants.SERVICE_VENDOR_EATON,
})
public class DeleteAssetDownloadPackageDataProcess  implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteAssetDownloadPackageDataProcess.class);

    @Reference
    private AssetDownloadPackageDataService assetDownloadPackageDataService;

    @Override
    public void execute(final WorkItem item, final WorkflowSession workflowSession, final MetaDataMap args) throws WorkflowException, ResourceNotFoundException {

        LOG.info("Delete Asset Download PackageData Process Workflow");
        final MetaDataMap wfMetaData = item.getWorkflowData().getMetaDataMap();
        boolean deleteAssetFlag;
        try {
            deleteAssetFlag = assetDownloadPackageDataService.deleteAssetDownloadPackageData(wfMetaData.get(AssetDownloadConstants.PARAMETER_KEY_RESOURCE_PATH, StringUtils.EMPTY),
                    wfMetaData.get(AssetDownloadConstants.PARAMETER_KEY_DOWNLOAD_PACKAGE_IDENTIFIER).toString());
        } catch (PersistenceException e) {
            throw new WorkflowException("Unable to process further workflow from DeleteAssetDownloadPackageDataProcess::: "+e.getMessage());
        }
        LOG.info("Asset deletion done"+deleteAssetFlag);
    }
}