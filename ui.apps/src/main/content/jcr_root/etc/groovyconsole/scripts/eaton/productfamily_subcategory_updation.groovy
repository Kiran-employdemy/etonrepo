import groovy.transform.Field
import javax.jcr.version.VersionException
import com.adobe.cq.commerce.api.Product

@Field final PRODUCT_FAMILY_PAGE = "productFamilyPage"
@Field final PRODUCT_FAMILY_PAGE_MULTI = "productFamilyPageMulti"
@Field final LANGUAGE_MASTER = "language-masters"
@Field final PRIMARY_SUB_CATEGORY = "primarySubCategory"
@Field final SWITCH_SUB_CATEGORY_TO_PIM = "switchSubCategoryToPIM"
@Field final PRIMARY_SUB_CATEGORY_MULTI = "primarySubCategoryMulti"

def eaton_product_base_array = ["/var/commerce/products/eaton", "/var/commerce/products/eaton_cummins"] as String[]
def data = []
def final dry_run = true
def totalPimSubCategoryUpdated = 0
def totalProductFamilyUpdated = 0


eaton_product_base_array.each {
    pimBasePath ->
        getNode(pimBasePath).recurse {
            productPimNode ->
                def pimNodeResource = resourceResolver.getResource(productPimNode.path)
                if (null != pimNodeResource && (pimNodeResource.isResourceType(Product.RESOURCE_TYPE_PRODUCT)
                        && productPimNode.hasNode(PRODUCT_FAMILY_PAGE_MULTI))) {
                    def productFamilyPageConfig = getProductFamilyPageConfig(productPimNode)
                    def productFamilyPageMultiResource = pimNodeResource.getChild(PRODUCT_FAMILY_PAGE_MULTI)
                    if (null != productFamilyPageMultiResource) {
                        getNode(productFamilyPageMultiResource.path).getNodes().eachWithIndex {
                            productFamilyPageMultiConfigNode, index ->
                                def productFamilyUpdated = false
                                def subCategoryUpdated = false
                                if (null != productFamilyPageMultiConfigNode) {

                                    def productFamilyPageMultiValue = getProductFamilyPageMulti(productFamilyPageMultiConfigNode)
                                    def productFamilyPage = getPage(productFamilyPageMultiValue)
                                    if (!productFamilyPageMultiValue.equals("")) {
                                        if (null != productFamilyPage) {
                                            def country = productFamilyPage.getLanguage(false)
                                                    .getCountry().toLowerCase();
                                            def productFamilyPageMultiLanguaMaster = productFamilyPageMultiValue
                                                    .replace("/"+country+"/", "/"+LANGUAGE_MASTER+"/")
                                            if (index == 0 && productFamilyPageConfig.equals("")) {
                                                productPimNode.setProperty(PRODUCT_FAMILY_PAGE,
                                                        productFamilyPageMultiLanguaMaster)
                                                productFamilyUpdated = true
                                                totalProductFamilyUpdated++
                                                if (!dry_run) {
                                                    productFamilyPageConfig = productFamilyPageMultiLanguaMaster
                                                    save()
                                                }
                                            }

                                            def contentResource = productFamilyPage.getContentResource()
                                            def productFamilyPageNode = contentResource.adaptTo(Node.class)
                                            def subCategoryPageConfig = productFamilyPageNode
                                                    .hasProperty(PRIMARY_SUB_CATEGORY) ?
                                                    productFamilyPageNode.getProperty(PRIMARY_SUB_CATEGORY)
                                                            .getString() : ""

                                            def switchSubCategoryToPIM = isSwitchSubCategoryEnabled(productFamilyPageNode)
                                            if (!switchSubCategoryToPIM) {
                                                try {
                                                    def primarySubCategoryMulti =
                                                            getSubCategoryMulti(productFamilyPageMultiConfigNode)
                                                    if (subCategoryPageConfig.equals("")) {
                                                        productFamilyPageNode
                                                                .setProperty(PRIMARY_SUB_CATEGORY,
                                                                primarySubCategoryMulti)
                                                        subCategoryUpdated = true
                                                    }

                                                    if (subCategoryUpdated || productFamilyUpdated) {
                                                        data.add([pimNodeResource.path,
                                                                  productFamilyPageConfig,
                                                                  productFamilyPageMultiValue,
                                                                  primarySubCategoryMulti,
                                                                  switchSubCategoryToPIM,
                                                                  subCategoryPageConfig,
                                                                  productFamilyUpdated,
                                                                  subCategoryUpdated])
                                                    }
                                                    if (!dry_run) {
                                                        save()
                                                    }
                                                } catch (VersionException ex) {
                                                    println(ex)
                                                }
                                                totalPimSubCategoryUpdated++
                                            }
                                        }
                                    }
                                }
                        }
                    }
                    if (null != productFamilyPageMultiResource && !dry_run) {
                        session.removeItem(productFamilyPageMultiResource.path)
                    }
                }
        }
}

def getSubCategoryMulti(productFamilyPageMultiConfigNode) {
    def primarySubCategoryMulti = ""
    if (productFamilyPageMultiConfigNode.hasProperty(PRIMARY_SUB_CATEGORY_MULTI)) {
        def primarySubCategoryMultiProperty = productFamilyPageMultiConfigNode
                .getProperty(PRIMARY_SUB_CATEGORY_MULTI)
        if (null != primarySubCategoryMultiProperty) {
            primarySubCategoryMulti = primarySubCategoryMultiProperty.getString()
        }
    }
    return primarySubCategoryMulti
}

def getProductFamilyPageConfig(productPimNode) {
    def productFamilyPageValue = ""
    def hasProductFamilyPageProp = productPimNode.hasProperty(PRODUCT_FAMILY_PAGE)
    if (hasProductFamilyPageProp) {
        productFamilyPageValue = productPimNode.getProperty(PRODUCT_FAMILY_PAGE).getString()
    }

    return productFamilyPageValue;
}

def getProductFamilyPageMulti(productFamilyPageMultiConfigNode) {
    def productFamilyPageMulti = ""
    if (productFamilyPageMultiConfigNode.hasProperty(PRODUCT_FAMILY_PAGE_MULTI)) {
        def productFamilyPageMultiProperty = productFamilyPageMultiConfigNode
                .getProperty(PRODUCT_FAMILY_PAGE_MULTI)
        if (null != productFamilyPageMultiProperty) {
            productFamilyPageMulti = productFamilyPageMultiProperty.getString()
        }
    }
    return productFamilyPageMulti
}

def isSwitchSubCategoryEnabled(contentValueMap) {
    def switchSubCategoryToPIM = false
    if (contentValueMap.hasProperty(SWITCH_SUB_CATEGORY_TO_PIM)) {
        def switchSubCategoryToPIMString = contentValueMap
                .getProperty(SWITCH_SUB_CATEGORY_TO_PIM).getString();
        switchSubCategoryToPIM = switchSubCategoryToPIMString.toBoolean()
    }
    return switchSubCategoryToPIM;
}

if (dry_run) {
    println "This is dry run"
    println "Total  PIMs subcategory that will be updated:" + totalPimSubCategoryUpdated
    println "Total  PIMs product family that will be updated:" + totalProductFamilyUpdated
} else {
    println "Total Updated PIMs with Sub category :" + totalPimSubCategoryUpdated
    println "Total  Updated PIMs product family updated:" + totalProductFamilyUpdated
}

table {
    columns("PIM path",
            "Product Family Config",
            "Product Family Multi config",
            "Sub Category Multi",
            "Switch Sub Category to page property",
            "Sub Category Configured",
            "Product family update status",
            "Sub Category Update Status")
    rows(data)
}





