import groovy.transform.Field
import com.adobe.cq.commerce.api.Product

def data =[]

@Field final TAXONOMYATTRIBUTES = "taxonomyAttributes"
@Field final TAXONOMYATTRIBUTEGRPNAME = "taxonomyAttributeGrpName"
@Field final TAXONOMY_ATTRIBUTE = "taxonomyAttribute"
@Field final PIM_PAGE_PATH = "pimPagePath"
@Field final SLASH_TAXONOMYATTRIBUTES = "/taxonomyAttributes"
@Field final SWITCH_TAXONOMY_ATTRIBUTES_TO_PIM = "switchTaxonomyAttributesToPIM"
@Field final CQ_LAST_REPLICATION_ACTION = "cq:lastReplicationAction"
@Field final CQ_LAST_REPLICATED = "cq:lastReplicated"
@Field final JCR_LAST_MODIFIED = "jcr:lastModified"
@Field final CQ_ACTIVATE_ACTION = "Activate"
@Field final LANGUAGE_MASTERS = "language-masters"

// Specifying this as true will prevent the script from actually making any changes
def final dry_run = true
// Specifying this as true will activate all modified content that does not have
// unpublished changes. This requires that dry_run be true
// If this is true and dry_run is also true, then the report will show what content would
// have been activated but it will not actually activate that content.
@Field def final activate_content = false

// Specifying this as true will activate all modified content even if that content had
// unpublished changes. This requires that both dry_run and activate_content also be true.
// If this is true, dry_run is true, and activate_content is true, then the report will show
// what content would have been activated but it will not actually activate that content.
@Field def final force_activate = false

def eaton_page_base_array =["/content/eaton","/content/eaton_cummins"] as String[]

eaton_page_base_array.each {
    pageBasePath ->
        if(resourceResolver.getResource(pageBasePath) != null){
            getPage(pageBasePath).recurse {
                page ->
                    def content = page.node

                    if (content != null && content.hasNode(TAXONOMYATTRIBUTES) && content.hasProperty(PIM_PAGE_PATH)) {
                        def pageActivation = "";
                        if(!dry_run) {
                            // deactivation before removing taxonomy attributes node
                            deactivate(content.path + SLASH_TAXONOMYATTRIBUTES);
                            session.removeItem(content.path + SLASH_TAXONOMYATTRIBUTES);
                            save()
                        }
                        if (activate_content && canPerformActivation(content) && !content.path.contains(LANGUAGE_MASTERS)){
                            if(!dry_run) {
                                activate(content.path)
                            }
                            pageActivation = "Activation"
                        }else if (isActivated(content)) {
                            // If the node needs activated but wasn't activated then report to the user.
                            pageActivation = "Unactivated Update"
                        }else if(activate_content && canPerformActivation(content) && content.path.contains(LANGUAGE_MASTERS)){
                            // If the node contains language-masters.
                            pageActivation = "(Language master page) unactivated Update"
                        }
                        if(content.hasProperty(SWITCH_TAXONOMY_ATTRIBUTES_TO_PIM)){
                            content.getProperty(SWITCH_TAXONOMY_ATTRIBUTES_TO_PIM).remove();
                        }
                        def pimPath = content.getProperty(PIM_PAGE_PATH).getString();
                        data.add([pimPath,content.path,"Yes",getTaxmnyAttrOfPim(pimPath),pageActivation]);
                    }
            }
        }
}

def getTaxmnyAttrOfPim(pimPath){
    def taxonomyAttributesInfoOfPim = "";
    if(resourceResolver.getResource(pimPath) != null){
        getNode(pimPath).recurse {
            product_pim_node ->
                def pimBasePath = resourceResolver.getResource(product_pim_node.path)
                if (pimBasePath.isResourceType(Product.RESOURCE_TYPE_PRODUCT)) {
                    if(product_pim_node.hasNode(TAXONOMYATTRIBUTES)){
                        def pim_node = product_pim_node.getNode(TAXONOMYATTRIBUTES)
                        taxonomyAttributesInfoOfPim = getTaxonomyAttributes(pim_node)
                    }
                }
        }
        return taxonomyAttributesInfoOfPim;
    }else{
        return taxonomyAttributesInfoOfPim = "PIM does not exist"
    }
}


def getTaxonomyAttributes(taxonomyAttributesNodePath) {
    def taxonomyAttributesInfo = "";
    def taxonomyAttributes = [];
    def taxonomyAttributeGrpName = "";
    def txnmyMap = [:]
    if(null != taxonomyAttributesNodePath) {
        taxonomyAttributesNodePath.recurse {
            taxonomy_attributes ->
                if (taxonomy_attributes.hasProperty(TAXONOMYATTRIBUTEGRPNAME)) {
                    taxonomyAttributeGrpName = taxonomy_attributes.getProperty(TAXONOMYATTRIBUTEGRPNAME).getString()
                    taxonomyAttributes = [];
                }
                if (taxonomy_attributes.hasProperty(TAXONOMY_ATTRIBUTE)) {
                    taxonomyAttributes.push(taxonomyAttributesInfo + taxonomy_attributes.getProperty(TAXONOMY_ATTRIBUTE).getString())
                    taxonomyAttributes.sort()
                }
                if (taxonomyAttributeGrpName != "") {
                    txnmyMap.put(taxonomyAttributeGrpName, taxonomyAttributes)
                }

        }
    }

    txnmyMap = txnmyMap.sort()
    if(txnmyMap.size() != 0){
        for(Map.Entry entry: txnmyMap.entrySet()){
            taxonomyAttributesInfo +="\n["+entry.getKey()+"]"
            taxonomyAttributesInfo += entry.getValue().join("\n")
        }
    }
    return taxonomyAttributesInfo.trim();
}

/**
 * @returns true if either the content is safe to activate or the force_activate configuration is turned on.
 */
def canPerformActivation(node) {
    return safeToActivate(node) || force_activate
}

/**
 * @returns Whether or not the node needs activated based upon whether or not the last activation
 *          action was "Activate"
 */
def isActivated(node) {
    def action = node.hasProperty(CQ_LAST_REPLICATION_ACTION) ? node.getProperty(CQ_LAST_REPLICATION_ACTION).string : ""
    return action == CQ_ACTIVATE_ACTION
}

/**
 * @returns Whether or not the node has unpublished changes determined by comparing
 *          the cq:lastReplicated and jcr:lastModified properties.
 */
def nodeHasUnpublishedChanges(node) {
    def replicated = node.hasProperty(CQ_LAST_REPLICATED) ? node.getProperty(CQ_LAST_REPLICATED).date : new Date(Long.MIN_VALUE)
    def modified = node.hasProperty(JCR_LAST_MODIFIED) ? node.getProperty(JCR_LAST_MODIFIED).date : new Date()
    return modified > replicated
}

/**
 * @returns Whether or not it is safe to publish this node determined by checking
 *          if it is already activated and by checking to see if it has unpublished changes.
 */
def safeToActivate(node) {
    return isActivated(node) && ! nodeHasUnpublishedChanges(node)
}

if(!dry_run){
    println("Updated and activated the pages.")
    save()
}else{
    println("This is dry run.");
}


table {
    columns("PIM Path",
            "Page Path",
            "Taxonomy Attribute Node removed from page (Yes/No)",
            "Taxonomy Attribute available in PIM",
            "Page Activation")
    rows(data)
}