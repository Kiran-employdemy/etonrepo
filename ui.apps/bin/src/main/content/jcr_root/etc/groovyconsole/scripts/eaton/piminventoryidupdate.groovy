import groovy.transform.Field
@Field final INVENTORY_ID =  "inventoryID"
@Field final OSGI_COFIG =  "sling:OsgiConfig"
@Field final PDH_RECORD_PATH =  "pdhRecordPath"
@Field final COMMERCE_RESOURCE_TYPE =  "commerce/components/product"

def data =[]
getNode('/var/commerce/products/eaton').recurse {
    pimNode ->
        def pimNodeResource =  getResource(pimNode.path)
        if (pimNodeResource.isResourceType(COMMERCE_RESOURCE_TYPE)) {
            def pimValumap =  pimNodeResource.valueMap
            def pdhRecordPath = pimValumap.get(PDH_RECORD_PATH, "")
            def pdhResource =  getResource(pdhRecordPath);
            if (null != pdhResource) {
                def inventoryID =  pdhResource.parent.name;
                if (pdhResource.parent.isResourceType(OSGI_COFIG)) {
                    def pimResourceNode = pimNodeResource.adaptTo(Node.class)
                    data.add([pimResourceNode.path, true])
                    pimResourceNode.setProperty(INVENTORY_ID, inventoryID)
                    save()
                }
            }
        }
}
table {
    columns("Pim Path", "Updates")
    rows(data)
}

