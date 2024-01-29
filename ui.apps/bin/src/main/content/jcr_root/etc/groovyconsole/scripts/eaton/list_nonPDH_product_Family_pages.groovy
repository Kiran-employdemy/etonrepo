package etc.groovyconsole.scripts.eaton

import groovy.transform.Field
import com.day.cq.commons.jcr.JcrConstants

/**
 * EAT-2725 - Product Grid component is now present on all non-PDH family pages
 * 
 * 1. List all the pages that has 'product-family-page' template and 'product-grid' component associated.
 * 
*/
@Field final PDH_RECORD_PATH_PROPERTY = "pdhRecordPath"
@Field final PIM_PAGE_PATH_PROPERTY = "pimPagePath"
@Field final PRODUCT_GRID_COMPONENT_NAME = "product_grid"
@Field final CONTENT_PATH = "/content/eaton"
@Field final PRODUCT_FAMILY_PAGE_TEMPLATE = "/conf/eaton/settings/wcm/templates/product-family-page"
@Field final CQ_TEMPLATE = "cq:template"
List<String> pagesWithProductFamilyTemplateAndProductGrid = new ArrayList()
List<String> pagesWithOutPdhRecordPath = new ArrayList()
List<String> pagesWithOutPimPagePath = new ArrayList()

getPage(CONTENT_PATH).recurse { page ->
    def content = page.node

    if (content && PRODUCT_FAMILY_PAGE_TEMPLATE == content.get(CQ_TEMPLATE)) 
    {
            // Check for "product_grid" node name
            Resource res = resourceResolver.getResource(page.path)
            if(null != res)
            {
               Node tempNode = res.adaptTo(javax.jcr.Node)
               if(null != tempNode)
               {
                  getNode( tempNode.getPath() ).recurse { node ->
                       Resource childResource = resourceResolver.getResource(node.getPath())
                       if(null != childResource)
                           {
                               Node childNode = childResource.adaptTo(javax.jcr.Node)
                                if(childNode.hasNode(PRODUCT_GRID_COMPONENT_NAME))
                                {
                                    pagesWithProductFamilyTemplateAndProductGrid.add(page.path)           
                                }
                           }
                       } 
               }
            }
    }
}
println "Pages with product-family-page template and product grid component :" + pagesWithProductFamilyTemplateAndProductGrid.size()

/**
 * To get the list of pages that has pimPagePath and associated resource does not have pdhRecordPath
 */

pagesWithProductFamilyTemplateAndProductGrid.each
{
    pageWithProductFamilyTemplateAndProductGrid ->
    Page contentPage = pageManager.getPage(pageWithProductFamilyTemplateAndProductGrid)
    
    if (null != contentPage.node)
        {
            if(null != contentPage.node.get(PIM_PAGE_PATH_PROPERTY))
            {
                Resource pimResource = resourceResolver.getResource(contentPage.node.get(PIM_PAGE_PATH_PROPERTY))
                if (null != pimResource)
                {  
                    Node pimTempNode = pimResource.adaptTo(javax.jcr.Node)
                    if(!pimTempNode.hasProperty(PDH_RECORD_PATH_PROPERTY))
                    {
                        pagesWithOutPdhRecordPath.add(pageWithProductFamilyTemplateAndProductGrid)  
                    }
                }
            }
            else
            {
                pagesWithOutPimPagePath.add(pageWithProductFamilyTemplateAndProductGrid)
            }
        }
}

/**
 * Display list of pages
 * 
 */
println "====Following pages are with product grid component having pim page path but without pdh record path at their pims====="
pagesWithOutPdhRecordPath.each
{
    pageWithOutPdhRecordPath ->
    println pageWithOutPdhRecordPath
}
println "======================================================================"
println "Pages without pimPagePath property::" + pagesWithOutPimPagePath.size()