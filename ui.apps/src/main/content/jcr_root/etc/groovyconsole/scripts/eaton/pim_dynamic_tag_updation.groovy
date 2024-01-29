import groovy.transform.Field

@Field final PRODUCT_RESOURCE_TYPE = "commerce/components/product"
@Field final CQ_TAGS =  "cq:tags"
@Field final TAGS =  "tags"
def actualPimNodes = 0
def nodesUpdated = 0
def eaton_product_base_array = ["/var/commerce/products/eaton", "/var/commerce/products/eaton_cummins"] as String[]

eaton_product_base_array.each {
  pim_base_path ->
    getNode(pim_base_path).recurse  {
      product_pim_node ->
        Resource res = resourceResolver.getResource(product_pim_node.path)
        if (null != res && PRODUCT_RESOURCE_TYPE.equals(res.resourceType)) {
          actualPimNodes ++
          def ValueMap pimResourceValueMap =  res.adaptTo(ValueMap.class)
          def pimResourceTags = (String [])pimResourceValueMap.get(TAGS)
          if (null != pimResourceTags) {
            def pimResourceNode = res.adaptTo(Node.class)
            pimResourceNode.setProperty(CQ_TAGS, pimResourceTags)
            pimResourceNode.getProperty(TAGS).remove()
            println("PIM Node modified with dynamic tag reference -> " + product_pim_node.path)
            save()
            nodesUpdated ++;
          }
        }
    }
}
println("PIM nodes updated " + nodesUpdated + " out of " + actualPimNodes + " PIM nodes.")


