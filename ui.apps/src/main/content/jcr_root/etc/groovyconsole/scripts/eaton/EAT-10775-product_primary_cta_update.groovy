package etc.groovyconsole.scripts.eaton

import groovy.transform.Field
import org.apache.commons.lang3.StringUtils

/*Eaton product path for commerce*/
@Field final PRODUCT_EATON_PATH = "/var/commerce/products/eaton"
@Field final LANGUAGES = [
        "fr_fr": new Item(country: "FR", mapping: ["http://electricalsector.eaton.com/2017ESEMEAFRProjectcarefr.html": "/content/eaton/fr/fr-fr/support/customer-support/contact-sales-electrical"]),
        "de_de": new Item(country: "DE", mapping: ["https://content.eaton.com/de_contact_form": "/content/eaton/de/de-de/support/customer-support/contact-sales-electrical", "http://electricalsector.eaton.com/de-de_EatonCare": "/content/eaton/de/de-de/support/customer-support/contact-sales-electrical"]),
        "en_gb": new Item(country: "GB", mapping: ["https://content.eaton.com/uk-project-care-en-gb-v2": "/content/eaton/gb/en-gb/support/customer-support/contact-sales-electrical", "http://electricalsector.eaton.com/2018ESEMEAUKProjectCareForm": "/content/eaton/gb/en-gb/support/customer-support/contact-sales-electrical"])
]
@Field final CONTENT_PATH = "/content/eaton"

/* If this is set to true, no actual content changes will be made, but the changes
   that would have been made will be reported */
@Field final DRY_RUN = true

@Field final PRIMARY_CTA_URL = "primaryCTAURL"
@Field final COUNTRY = "country"
@Field final ENABLE_SOURCE_TRACKING = "enableSourceTracking"
@Field final ENABLE_SOURCE_TRACKING_PRIMARY_CTA = "enableSourceTrackingPrimaryCTA"
@Field final REDIRECT_TARGET = "cq:redirectTarget"
@Field replicator
@Field DATA = []
@Field totalUpdateNodes = 0
@Field totalActivatedProducts = 0
@Field csvReport = ""

class Item {
    def country
    def mapping
}

try {
    replicator = getService("com.day.cq.replication.Replicator")
} catch (Exception e) {
    println "ERROR: " + e.getMessage()
}

forEachApplicablePageNode { node, language, mappingItem ->
    updatePrimaryCTAProperty(node, language, mappingItem)
}

def forEachApplicablePageNode(callback) {
    LANGUAGES.each { language, mappingItem ->
        getNode(PRODUCT_EATON_PATH + "/" + language).recurse { node ->

            if (node.hasProperty(PRIMARY_CTA_URL)) {
                callback(node, language, mappingItem)
            }

        }
    }
}

def updatePrimaryCTAProperty(node, language, mappingItem) {
    def ctaProp = node.getProperty(PRIMARY_CTA_URL)
    def country = getCountry(node, language)
    if (mappingItem.country != country) {
        return
    }
    def productNode = getProductNode(node)
    def contentRedirectPage = ""


    if (ctaProp.isMultiple()) {
        println "WARN: Invalid $PRIMARY_CTA_URL property for the node $node.path"
        println "WARN: Property value: $ctaProp.values\n"
        return
    }
    def primaryCtaProperty = node.getProperty(PRIMARY_CTA_URL).string
    def sourceUrl = primaryCtaProperty

    if (StringUtils.isBlank(primaryCtaProperty)) {
        println "WARN: Primary CTA is blank: $node.path\n"
        return

    }

    //if the primaryCtaProperty start with /content try to check redirect target of the page
    if (StringUtils.startsWith(primaryCtaProperty, "/content/")
            && session.nodeExists(primaryCtaProperty)) {
        def content = session.getNode(primaryCtaProperty).getNode("jcr:content")
        if (content != null && content.hasProperty(REDIRECT_TARGET)) {
            sourceUrl = content.getProperty(REDIRECT_TARGET).string
            contentRedirectPage = primaryCtaProperty
        }
    }
    def targetUrl = null
    mappingItem.mapping.each { key, value ->
        if (StringUtils.startsWith(sourceUrl, key)) {
            targetUrl = value;
        }
    }

    if (targetUrl != null) {

        def sourceTrackingInitial = ""
        def sourceTrackingFinal = ""
        def isSuffixDisabled = ""

        if (productNode != null) {
            if (productNode.hasProperty("isSuffixDisabled")) {
                isSuffixDisabled = productNode.getProperty("isSuffixDisabled").string
                productNode.getProperty("isSuffixDisabled").remove()
            }

            if (node.path == productNode.path) {
                sourceTrackingInitial = getAndSetSourceTracking(node, ENABLE_SOURCE_TRACKING)
                sourceTrackingFinal = node.getProperty(ENABLE_SOURCE_TRACKING).string
            } else {
                sourceTrackingInitial = getAndSetSourceTracking(node, ENABLE_SOURCE_TRACKING_PRIMARY_CTA)
                sourceTrackingFinal = node.getProperty(ENABLE_SOURCE_TRACKING_PRIMARY_CTA).string
            }
        }
        node.setProperty(PRIMARY_CTA_URL, targetUrl)

        def activated = false
        activated = saveAndActivate(productNode, activated)
        totalUpdateNodes++

        DATA.add([node.path, country, activated, sourceTrackingInitial, sourceTrackingFinal, contentRedirectPage, sourceUrl, targetUrl])

        csvReport += node.path + ","
        csvReport += country + ","
        csvReport += String.valueOf(activated) + ","
        csvReport += isSuffixDisabled + ","
        csvReport += sourceTrackingInitial + ","
        csvReport += sourceTrackingFinal + ","
        csvReport += contentRedirectPage + ","
        csvReport += sourceUrl + ","
        csvReport += targetUrl
        csvReport += "\n"
    }
}

private String getAndSetSourceTracking(node, String propName) {
    def sourceTrackingInitial = ""
    if (node.hasProperty(propName)) {
        sourceTrackingInitial = node.getProperty(propName).string
    }
    node.setProperty(propName, "true")
    return sourceTrackingInitial
}

private boolean saveAndActivate(productNode, boolean activated) {
    if (!DRY_RUN) {
        session.save()
        if (productNode != null) {
            def rs = replicator.getReplicationStatus(session, productNode.path)
            if (rs != null && rs.isActivated()) {
                replicator.replicate(session, ReplicationActionType.ACTIVATE, productNode.path)
                activated = true
                totalActivatedProducts++
            }
        }
    }
    activated
}

private getProductNode(node) {
    if (node.hasProperty("cq:commerceType")) {
        return node
    } else {
        def productNode = node.getParent().getParent()
        if (productNode.hasProperty("cq:commerceType")) {
            return productNode
        } else {
            println "WARN: cq:commerceType property was not found: $node.path\n"
        }
    }
}

private getCountry(node, language) {
    def country
    if (node.hasProperty("cq:commerceType")) {
        country = "primary($language)"
    } else {
        if (node.hasProperty(COUNTRY)) {
            def countryProperty = node.getProperty(COUNTRY)
            if (countryProperty.isMultiple()) {
                println "WARN: Invalid $COUNTRY property for the node $node.path"
                println "WARN: Property value: $countryProperty.values\n"
            } else {
                country = countryProperty.string
                if (StringUtils.isBlank(country)) {
                    println "WARN: Country property is blank for: $node.path"
                }
            }
        }
    }
    return country
}


println "Total updated nodes: $totalUpdateNodes"
println "Total activated products: $totalActivatedProducts"
println 'Script Execution Complete!'
println "CSV Report:\n" + csvReport
table
        {
            columns("Node path", "Country", "Activated", "Source Tracking initial", "Source Tracking current", "Content redirect page", "Source CTA URL", "Target CTA URL")
            rows(DATA)
        }
