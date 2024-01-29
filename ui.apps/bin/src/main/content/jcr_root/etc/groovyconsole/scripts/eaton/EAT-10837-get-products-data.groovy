package etc.groovyconsole.scripts.eaton

import com.eaton.platform.core.bean.ProductFamilyPDHDetails
import com.eaton.platform.core.util.CommonUtil
import com.google.gson.Gson
import groovy.transform.Field
import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.lang3.StringUtils

/*Eaton product path for commerce*/
@Field final PRODUCT_EATON_PATH = "/var/commerce/products/eaton/en_us"


@Field csvReport = new StringBuilder()

@Field results = []


forEachApplicableProductNode { valueMap, resource ->
    generateReport(valueMap, resource)
}

def forEachApplicableProductNode(callback) {
    getNode(PRODUCT_EATON_PATH).recurse { node ->
        resource = resourceResolver.getResource(node.path)
        def valueMap = resource.adaptTo(ValueMap.class)
        if (valueMap.containsKey("cq:commerceType")) {
            callback(valueMap, resource)
        }

    }

}

def generateReport(valueMap, resource) {
    def pdhPath = valueMap.get("pdhRecordPath", StringUtils.EMPTY)
    def marketingDesc = ""
    def coreFeatures = ""
    ProductFamilyPDHDetails productFamilyPDHDetails
    if (StringUtils.isNoneBlank(pdhPath)) {
        def res = resourceResolver.getResource(pdhPath)
        if (res != null) {
            try {
                productFamilyPDHDetails = CommonUtil.readPDHNodeData(resourceResolver, res)
                marketingDesc = productFamilyPDHDetails.marketingDescription
                coreFeatures = productFamilyPDHDetails.coreFeatures
            } catch (Exception e) {
                println "ERROR: Error during getting PDH details for the next product " + pdhPath
                println "ERROR: " + e.getMessage()
            }

        }
    }
    if (!StringUtils.startsWith(valueMap.get("pdhRecordPath", StringUtils.EMPTY),
            "/var/commerce/pdh/product-family/en_us")) {
        "WARN:" + valueMap.get("pdhRecordPath", StringUtils.EMPTY)
    }
    csvReport.append(resource.path).append(",")
    csvReport.append(StringEscapeUtils.escapeCsv(valueMap.get("inventoryID", StringUtils.EMPTY))).append(",")
    csvReport.append(StringEscapeUtils.escapeCsv(valueMap.get("jcr:title", StringUtils.EMPTY))).append(",")
    csvReport.append(StringEscapeUtils.escapeCsv(valueMap.get("productName", StringUtils.EMPTY))).append(",")
    csvReport.append(StringEscapeUtils.escapeCsv(valueMap.get("marketingDesc", StringUtils.EMPTY))).append(",")
    csvReport.append(StringEscapeUtils.escapeCsv(valueMap.get("coreFeatures", StringUtils.EMPTY))).append(",")
    csvReport.append(StringEscapeUtils.escapeCsv(valueMap.get("pdhRecordPath", StringUtils.EMPTY))).append(",")
    csvReport.append(StringEscapeUtils.escapeCsv(marketingDesc)).append(",")
    csvReport.append(StringEscapeUtils.escapeCsv(coreFeatures)).append(",")
    csvReport.append("\n")
    results.add(new Product(
            path: resource.path,
            inventoryID: valueMap.get("inventoryID", StringUtils.EMPTY),
            extensionId: valueMap.get("jcr:title", StringUtils.EMPTY),
            productName: valueMap.get("productName", StringUtils.EMPTY),
            marketingDesc: valueMap.get("marketingDesc", StringUtils.EMPTY),
            coreFeatures: valueMap.get("coreFeatures", StringUtils.EMPTY),
            pdhRecordPath: valueMap.get("pdhRecordPath", StringUtils.EMPTY),
            pdhMarketingDesc: marketingDesc,
            pdhCoreFeatures: coreFeatures
    ))

}

class Product {
    def path;
    def inventoryID
    def extensionId
    def productName
    def marketingDesc
    def coreFeatures
    def pdhRecordPath
    def pdhMarketingDesc
    def pdhCoreFeatures
}

println 'Script Execution Complete!'
println 'Get the result from the /etc/groovyconsole/jcr:content/audit/ node'
println new Gson().toJson(results)

println "CSV Report:\n" + csvReport.toString()