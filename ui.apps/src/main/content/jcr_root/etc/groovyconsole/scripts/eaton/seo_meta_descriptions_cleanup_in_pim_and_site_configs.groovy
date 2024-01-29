import groovy.transform.Field

/* If this is set to true, no actual content changes will be made, but the changes
   that would have been made will be reported */
@Field final boolean DRY_RUN = true

/* If this is set to true, content changes will be published */
@Field final boolean ACTIVATE_NODE = false

@Field final PRODUCT_RESOURCE_TYPE = "commerce/components/product"

@Field final PF_SEO_DESC_MODELS_TAB_PROP = "seoDescModelsTab"

@Field final PF_SEO_DESC_RESOURCES_TAB_PROP = "seoDescResourcesTab"

@Field final SITE_CONFIG_PF_SEO_META_MODELS_DESC = "pfModelsDesc"

@Field final SITE_CONFIG_PF_SEO_META_RESOURCES_DESC = "pfResourcesDesc"

@Field final SITE_CONFIG_SKU_PAGE_SEO_META_SPEC_DESC = "skuSpecificationsDesc"

@Field final SITE_CONFIG_SKU_PAGE_SEO_META_RESOURCES_DESC = "skuResourcesDesc"

@Field final SITE_CONFIG_PAGE_CONTENT_RESOURCE_TYPE = "/apps/eaton/components/structure/siteconfigpage"

@Field final BASE_PATH_TO_SITE_CONFIGS = "/etc/cloudservices/siteconfig"

@Field final RESOURCE_TYPE = "sling:resourceType"

@Field ACTION_ACTIVATED = "Activated"

@Field final ACTION_SKIP = "Skip"

@Field final ACTIVATE_REPLICATION_ACTION = "Activate"

@Field final PROPERTY_CQ_REPLICATION_ACTION = "cq:lastReplicationAction"

@Field final NODE_PROP_ACTION_MISSING = "Missing"

@Field final NODE_PROP_ACTION_REMOVED = "Removed"

@Field final VALUE_DELIMITER = "::"

@Field final NODE_TYPE_PIM = "PIM"

@Field final NODE_TYPE_SITE_CONFIG = "Site Config"

@Field final PRODUCT_BASE_PATH_ARRAY = ["/etc/commerce/products/eaton", "/etc/commerce/products/eaton_cummins"] as String[]

@Field final PIM_NODE_PROPERTIES_TO_BE_REMOVED = [PF_SEO_DESC_MODELS_TAB_PROP, PF_SEO_DESC_RESOURCES_TAB_PROP] as String[]

@Field final SITE_CONFIG_PROPERTIES_TO_BE_REMOVED = [SITE_CONFIG_PF_SEO_META_MODELS_DESC, SITE_CONFIG_PF_SEO_META_RESOURCES_DESC, SITE_CONFIG_SKU_PAGE_SEO_META_SPEC_DESC, SITE_CONFIG_SKU_PAGE_SEO_META_RESOURCES_DESC] as String[]

@Field totalNodesUpdated = 0
def totalNodesActivated = 0
def data = []
def pageReplicationAction

PRODUCT_BASE_PATH_ARRAY.each { pimBasePath ->
    getNode(pimBasePath).recurse { productPimNode ->
        if (productPimNode.hasProperty(RESOURCE_TYPE) && productPimNode.get(RESOURCE_TYPE).equals(PRODUCT_RESOURCE_TYPE)) {
            def nodeUpdateStatus = processNode(productPimNode, PIM_NODE_PROPERTIES_TO_BE_REMOVED)
            def lastRepAction = productPimNode.hasProperty(PROPERTY_CQ_REPLICATION_ACTION) ? productPimNode.get(PROPERTY_CQ_REPLICATION_ACTION) : "";
            if (ACTIVATE_NODE && ACTIVATE_REPLICATION_ACTION.equals(lastRepAction)) {
                pageReplicationAction = ACTION_ACTIVATED
                activate(productPimNode.path)
                totalNodesActivated++
            } else {
                pageReplicationAction = ACTION_SKIP
            }
            data.add([productPimNode.path, NODE_TYPE_PIM, nodeUpdateStatus, pageReplicationAction])
        }
    }
}

getPage(BASE_PATH_TO_SITE_CONFIGS)?.recurse { siteConfigPage ->
    def content = siteConfigPage.node
    if (content?.hasProperty(RESOURCE_TYPE) && content.get(RESOURCE_TYPE).equals(SITE_CONFIG_PAGE_CONTENT_RESOURCE_TYPE)) {
        def nodePropertiesValActions = processNode(content, SITE_CONFIG_PROPERTIES_TO_BE_REMOVED)
        def lastRepAction = content.hasProperty(PROPERTY_CQ_REPLICATION_ACTION) ? content.get(PROPERTY_CQ_REPLICATION_ACTION) : ""
        if (ACTIVATE_NODE && ACTIVATE_REPLICATION_ACTION.equals(lastRepAction)) {
            pageReplicationAction = ACTION_ACTIVATED
            activate(siteConfigPage.path)
            totalNodesActivated++
        } else {
            pageReplicationAction = ACTION_SKIP
        }
        data.add([siteConfigPage.path, NODE_TYPE_SITE_CONFIG, nodePropertiesValActions, pageReplicationAction])
    }
}

if (!DRY_RUN) {
    save()
    println("DryRun: $DRY_RUN. Total pages/nodes updated: $totalNodesUpdated. Total pages/nodes activated: $totalNodesActivated.")
} else {
    println "This is dry run"
    println("DryRun: $DRY_RUN. Total pages/nodes can be updated: $totalNodesUpdated. Total pages/nodes activated: $totalNodesActivated.")
}

table {
    columns("Node Path", "Node Type", "Node Property Name::Value::Action", "Replication Action")
    rows(data)
}

String processNode(Node nodeObj, String[] nodePropertiesToBeRemovedArray) {
    def nodePropertyValAction = ""
    if (nodeObj && nodePropertiesToBeRemovedArray?.length > 0) {
        nodePropertiesToBeRemovedArray.each {
            def propertyVal = " "
            def nodePropertyValActionType = NODE_PROP_ACTION_MISSING
            if (nodeObj.hasProperty(it)) {
                propertyVal = nodeObj.get(it)
                nodePropertyValActionType = NODE_PROP_ACTION_REMOVED
                nodeObj.getProperty(it).remove()
            }
            nodePropertyValAction += it + VALUE_DELIMITER + propertyVal + VALUE_DELIMITER + nodePropertyValActionType + "</br>"
        }
        nodePropertyValAction ? totalNodesUpdated++ : totalNodesUpdated
    }
    nodePropertyValAction;
}