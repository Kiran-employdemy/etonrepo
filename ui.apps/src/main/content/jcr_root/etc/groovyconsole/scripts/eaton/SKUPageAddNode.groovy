import groovy.transform.Field

@Field final PROPERTY_CQ_TEMPLATE = "cq:template"
@Field final PROPERTY_CQ_REPLICATION_ACTION = "cq:lastReplicationAction"

@Field final SKU_TEMPLATE_NAME = "/conf/eaton/settings/wcm/templates/sku-page"
@Field final BASE_PATH_TO_TRAVERSE = "/content/eaton"

@Field final ADD_COMPONENT_NODE_PATH_TO_APPEND_TO_CONTENT_RESOURCE_PATH = "/root/responsivegrid/product_detail_tabs/content-tab-1"
@Field final ADD_COMPONENT_NODE_NAME = "product_support"

@Field final ACTIVATE_REPLICATION_ACTION = "Activate"
@Field final boolean ACTIVATE_PAGE = false

@Field final boolean DRY_RUN = false

@Field final NODE_ACTION_CREATED = "Created"
@Field final ACTION_SKIP = "Skip"
@Field final ACTION_INVESTIGATION_REQUIRED = "Investigation Required"

@Field ACTION_ACTIVATED = "Activated"
@Field final NODE_PROPERTIES = ['sling:resourceType':'eaton/components/product/product-support']

def totalPagesUpdated = 0
def totalPagesActivated = 0
def pageReplicationAction

def data = []

getPage(BASE_PATH_TO_TRAVERSE).recurse { page ->
    def content = page.node

    if(content?.get(PROPERTY_CQ_TEMPLATE).equals(SKU_TEMPLATE_NAME)) {
        def nodeSearchPath = content.path + ADD_COMPONENT_NODE_PATH_TO_APPEND_TO_CONTENT_RESOURCE_PATH
        def nodeResource = resourceResolver.getResource(nodeSearchPath)
        if(nodeResource) {
            def childNodeResource = nodeResource.getChild(ADD_COMPONENT_NODE_NAME)
            if(childNodeResource) {
                data.add([page.getPath(), childNodeResource.getPath(), ACTION_SKIP, ACTION_SKIP])
            } else {
                Node newlyCreatedNode = createNode(nodeResource)
                totalPagesUpdated++
                def lastRepAction = page.getProperties().get(PROPERTY_CQ_REPLICATION_ACTION, "")
                if (ACTIVATE_PAGE && ACTIVATE_REPLICATION_ACTION.equals(lastRepAction)) {
                    activate(page.path)
                    pageReplicationAction = ACTION_ACTIVATED
                    totalPagesActivated++
                } else {
                    pageReplicationAction = ACTION_SKIP
                }
                data.add([page.path, newlyCreatedNode.path, NODE_ACTION_CREATED, pageReplicationAction])

            }
        } else {
            data.add([page.path, nodeSearchPath, ACTION_INVESTIGATION_REQUIRED, ACTION_INVESTIGATION_REQUIRED])
        }

    }

}

if(!DRY_RUN) {
    save()
}
println("DryRun: $DRY_RUN. Total Pages updated: $totalPagesUpdated. Total pages activated: $totalPagesActivated.")
table {
    columns("Page Path", "Node Path", "Node Action", "Replication Action")
    rows(data)
}

Node createNode(Resource nodeResource) {
    def node = nodeResource.adaptTo(Node)
    def newlyCreatedNode = node.addNode(ADD_COMPONENT_NODE_NAME)
    NODE_PROPERTIES.each{ k,v ->
        newlyCreatedNode.setProperty(k, v)
    }
    return node
}