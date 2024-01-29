
/**
 * This script is to find old SKU pages across '/content/eaton' and updates below resourceTypes/templates.
 * cq:Template : /conf/eaton/settings/wcm/templates/sku-page --> /conf/eaton/settings/wcm/templates/sku-page1
 * sling:resourceType : eaton/components/product/product-detail-tabs --> eaton/components/product/single-sku-product-detail-tabs
 * sling:resourceType : eaton/components/product/product-detail-card --> eaton/components/product/single-sku-product-detail-card
 */
def dryRun = true;

getPage("/content/eaton").recurse { page ->
    def content = page.node

    if (content && "/conf/eaton/settings/wcm/templates/sku-page" == content.get("cq:template")) {
        println "SKU Page Found --> "+content.parent.path;
        if(!dryRun){
            content.setProperty("cq:template", "/conf/eaton/settings/wcm/templates/sku-page1");
        }
        def predicates = [
                "path": content.parent.path,
                "type": "nt:unstructured"
        ]
        def query = createQuery(predicates)
        query.hitsPerPage = 200
        def result = query.result
        result.hits.each { hit ->
            if(hit.node.hasProperty("sling:resourceType")){
                if(hit.node.getProperty("sling:resourceType").value.toString().equals("eaton/components/product/product-detail-tabs")){
                    println "Found Product Details tabs Component Node -->  " + hit.node.path;
                    if(!dryRun){
                        hit.node.setProperty("sling:resourceType","eaton/components/product/single-sku-product-detail-tabs");
                    }
                }
                if(hit.node.getProperty("sling:resourceType").value.toString().equals("eaton/components/product/product-detail-card")){
                    println "Found Product Details Card Component Node -->  " + hit.node.path;
                    if(!dryRun){
                        hit.node.setProperty("sling:resourceType","eaton/components/product/single-sku-product-detail-card")
                    }
                }
            }
        }

        println "------------------------------------------------------------------------------------------------------------------\n"
    }
}

if(!dryRun){
    session.save();
    println "Updated Successfully !"
}