package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.PIMResourceSlingModel;
import com.eaton.platform.core.services.AdminService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Iterator;


@Component(service = WorkflowProcess.class,immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Product Family Page PIM Path Update process workflow",
                AEMConstants.SERVICE_VENDOR_EATON,
        })
public class ProductFamilyPagePIMPathUpdate implements WorkflowProcess {
    private static final Logger LOG = LoggerFactory.getLogger(ProductFamilyPagePIMPathUpdate.class);

    @Reference
    private transient AdminService adminService;

    @Override
    public void execute(final WorkItem item, final WorkflowSession workflowSession, final MetaDataMap args) throws WorkflowException {
        LOG.info("Enter Product Family Page PIM Path Update workflow");
        try(ResourceResolver adminResourceResolver  = adminService.getWriteService()){
            final WorkflowData workflowData = item.getWorkflowData();
            if (null != workflowData) {
                String payloadPath = workflowData.getPayload().toString();
                final Resource productResource = adminResourceResolver.getResource(payloadPath);
                if (null != productResource) {
                    String langCode = productResource.getParent().getParent().getParent().getName();
                    payloadPath = productResource.getParent().getParent().getPath();
                    if (langCode.contains(CommonConstants.UNDERSCORE)) {
                        final String[] splitLangCode = langCode.split(CommonConstants.UNDERSCORE);
                        if (splitLangCode.length == 2) {
                             langCode = splitLangCode[0] + CommonConstants.HYPHEN + splitLangCode[1];
                        }
                        final PIMResourceSlingModel pimResourceSlingModel = adminResourceResolver.getResource(payloadPath).adaptTo(PIMResourceSlingModel.class);
                        if (null != pimResourceSlingModel) {
                            String productFamilyPage = pimResourceSlingModel.getProductFamilyPage();
                            final Node payloadPathNode = adminResourceResolver.getResource(payloadPath).adaptTo(Node.class);
                            if (null != productFamilyPage && productFamilyPage.contains(CommonConstants.EN_US)) {
                                productFamilyPage = productFamilyPage.replace(CommonConstants.EN_US, langCode);
                                if (payloadPathNode.hasProperty(CommonConstants.PRODUCT_FAMILY_PAGE)) {
                                    payloadPathNode.setProperty(CommonConstants.PRODUCT_FAMILY_PAGE, productFamilyPage);
                                    payloadPathNode.getSession().save();
                                }
                            }
                            if (null != productFamilyPage && productFamilyPage.contains(CommonConstants.US_STRING)) {
                                productFamilyPage = productFamilyPage.replace(CommonConstants.US_STRING, splitLangCode[1]);
                                if (payloadPathNode.hasProperty(CommonConstants.PRODUCT_FAMILY_PAGE)) {
                                    payloadPathNode.setProperty(CommonConstants.PRODUCT_FAMILY_PAGE, productFamilyPage);
                                    payloadPathNode.getSession().save();
                                }
                            }
                        }
                    }
                }
            }
        }catch (RepositoryException e){
            LOG.error("RepositoryException in ProductFamilyPagePIMPathUpdate "+e.getMessage());
        }catch (Exception e){
            LOG.error("Exception in ProductFamilyPagePIMPathUpdate "+e.getMessage());
        }
    }
}
