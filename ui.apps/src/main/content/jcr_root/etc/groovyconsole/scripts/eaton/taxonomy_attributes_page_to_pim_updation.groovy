import groovy.transform.Field
import com.adobe.cq.commerce.api.Product

def pagePimPathMap = [:]
def data =[]
def mapToUpdatePgm =[:]
def final dry_run = true
def pim_count = 0;

def eaton_page_base_array =["/content/eaton","/content/eaton-cummins"] as String[]
def eaton_product_base_array = ["/var/commerce/products/eaton","/var/commerce/products/eaton_cummins"] as String[]

@Field final SLING_RESOURCE_TYPE = "sling:resourceType"
@Field final TAXONOMYATTRIBUTES = "taxonomyAttributes"
@Field final TAXONOMY_ATTRIBUTE = "taxonomyAttribute"
@Field final TAXONOMYATTRIBUTEGRPNAME = "taxonomyAttributeGrpName"
@Field final PIM_PAGE_PATH = "pimPagePath"
@Field final SLASH_TAXONOMYATTRIBUTES = "/taxonomyAttributes"
@Field final SWITCH_TAXONOMY_ATTRIBUTES_TO_PIM = "switchTaxonomyAttributesToPIM"
@Field final UPDATED_PROGRAMMATICALLY = "Updated programmatically"
@Field final UPDATE_MANUALLY = "Update manually"
@Field final MATCHING = "Matching"
@Field final JCR_CONTENT = "/jcr:content"

eaton_page_base_array.each {
    pageBasePath ->
        if(null != pageBasePath){
            getPage(pageBasePath).recurse {
                page ->
                    def content = page.node
                    if (content != null && content.hasNode(TAXONOMYATTRIBUTES) && content.hasProperty(PIM_PAGE_PATH)) {
                        pagePimPathMap.put(content.path, content.getProperty(PIM_PAGE_PATH).getString());
                    }
            }
        }
}
eaton_product_base_array.each {
    pimBasePath ->
        if(null != pimBasePath){
            getNode(pimBasePath).recurse {
                product_pim_node ->
                    def pimPath = resourceResolver.getResource(product_pim_node.path)
                    if (pimPath.isResourceType(Product.RESOURCE_TYPE_PRODUCT)) {
                        def listOfPages = getListOfContentPages(pimPath, pagePimPathMap)
                        if (listOfPages != []) {
                            pimAndPageUpdateAction(product_pim_node, listOfPages, data, mapToUpdatePgm)
                            pim_count++
                        }
                    }
            }
        }
}

def getListOfContentPages(pimPath,pagePimPathMap){
    def listOfPages = []
    for(Map.Entry entry: pagePimPathMap.entrySet()){
        if (entry.getValue().equals(pimPath.path)) {
            listOfPages.push(entry.getKey())
        }
    }
    return listOfPages;
}

def pimAndPageUpdateAction(product_pim_node,listOfPages,data,mapToUpdatePgm){
    def taxonomyAttributesInfoOfPim = "";
    def switchTaxonomyAttributesToPIM = "";
    if(product_pim_node.hasNode(TAXONOMYATTRIBUTES)){
        def pim_node = product_pim_node.getNode(TAXONOMYATTRIBUTES)
        taxonomyAttributesInfoOfPim = getTaxonomyAttributes(pim_node)
    }
    def listOfTaxonomyAttributes = [];
    def checkTxnmyStatus=""
    listOfPages.eachWithIndex {
        item,index ->
            def switchTaxonomyAttributesToPIMFlag = false;
            def action=""
            def contentPath = getNode(item);
            if(contentPath.hasNode(TAXONOMYATTRIBUTES) && contentPath.hasProperty(SWITCH_TAXONOMY_ATTRIBUTES_TO_PIM)){
                switchTaxonomyAttributesToPIM = contentPath.getProperty(SWITCH_TAXONOMY_ATTRIBUTES_TO_PIM).getString();
                if(switchTaxonomyAttributesToPIM.toBoolean()){
                    switchTaxonomyAttributesToPIMFlag = true
                }
            }
            if(index == 0){
                listOfTaxonomyAttributes =  getlistOfTaxonomyAttributes(listOfPages);
            }
            if(!switchTaxonomyAttributesToPIMFlag && listOfTaxonomyAttributes != []){
                def language = getContentLanguage(contentPath)
                def count = listOfTaxonomyAttributes.count(listOfTaxonomyAttributes.get(0))
                checkTxnmyStatus = getCheckTxnmyStatus(listOfPages,listOfTaxonomyAttributes,count)
                if(checkTxnmyStatus == MATCHING && product_pim_node.path.contains(language)){
                    action = UPDATED_PROGRAMMATICALLY;
                    mapToUpdatePgm.put(product_pim_node.path,listOfPages)
                }else{
                    action = UPDATE_MANUALLY;
                }
            }
            data.add([product_pim_node.path,contentPath.path,listOfTaxonomyAttributes.get(index),taxonomyAttributesInfoOfPim,switchTaxonomyAttributesToPIMFlag.toString(),action,checkTxnmyStatus,"No"])
    }

}

def getCheckTxnmyStatus(listOfPages,listOfTaxonomyAttributes,count){
    def checkTxnmyStatus=""
    if(listOfPages.size() == 1){
        checkTxnmyStatus = MATCHING
    }else if(count == listOfTaxonomyAttributes.size()){
        checkTxnmyStatus = MATCHING
    }
    return checkTxnmyStatus;
}

if(!dry_run){
    getUpdateTxnAttrToPimFromPage(mapToUpdatePgm)
    println("Total PIM has updated "+mapToUpdatePgm.size() +" out of "+pim_count)
}else{
    println("This is dry run ");
    println("Total PIM has updated "+mapToUpdatePgm.size() +" out of "+pim_count);
}

def getUpdateTxnAttrToPimFromPage(mapToUpdatePgm){
    for(Map.Entry entry: mapToUpdatePgm.entrySet()){
        def pimPath = getNode(entry.getKey());
        def listOfPages = entry.getValue()
        def countPagePath = getNode(listOfPages.get(0)+SLASH_TAXONOMYATTRIBUTES)
        if(!pimPath.hasNode(TAXONOMYATTRIBUTES)){
            copy countPagePath.path to pimPath.path+SLASH_TAXONOMYATTRIBUTES
        }else{
            session.removeItem(pimPath.path+SLASH_TAXONOMYATTRIBUTES)
            save()
            copy countPagePath.path to pimPath.path+SLASH_TAXONOMYATTRIBUTES
        }
        save()
    }
}

def getContentLanguage(contentPath){
    def language="";
    def page = getPage(contentPath.path.replace(JCR_CONTENT,""))
    if(null != page){
        language = page.getLanguage(true).toString().replace("-","_").toLowerCase();
    }
}

def getlistOfTaxonomyAttributes(listOfPages){
    def listOfTaxonomyAttributes = [];
    listOfPages.each{
        def contentPath = getNode("${it}");
        if(contentPath.hasNode(TAXONOMYATTRIBUTES)){
            def taxonomyAttributesInfoOfPage = getTaxonomyAttributes(contentPath.getNode(TAXONOMYATTRIBUTES))
            listOfTaxonomyAttributes.push(taxonomyAttributesInfoOfPage);
        }
    }
    return listOfTaxonomyAttributes;
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


table {
    columns("PIM Path",
            "Page Path",
            "Sorted Taxonomy Attributes for page",
            "Sorted Taxonomy Attributes for PIM",
            "Checkbox:: switch Taxonomy Attributes To PIM (True/false)",
            "Action:: Updated Taxonomy Attributes To PIM (Update manually/Update programmatically)",
            "Checksum:: Taxonomy Attributes of the page's (Matching/'')",
            "Taxonomy Attribute Node removed from page (Yes/No)" )
    rows(data)
}