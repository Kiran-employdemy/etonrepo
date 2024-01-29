import groovy.transform.Field
import com.adobe.cq.commerce.api.Product
import javax.jcr.version.VersionException

/* ----------------------------------------------------------------------------- */
/* --------------------------- Script Configurations --------------------------- */
/* ----------------------------------------------------------------------------- */

// This list of specified root pages will be recursively searched for content that needs updated.
@Field final eaton_page_base_array = ["/content/eaton", "/content/eaton-cummins"]

// Specifying this as true will prevent the script from actually making any changes
@Field final dry_run = true

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

/* ----------------------------------------------------------------------------- */
/* ----------------------------------------------------------------------------- */
/* ----------------------------------------------------------------------------- */

@Field final FEATURE_BLOCK_CONTENT = "eaton/components/content/feature-block"
@Field final FEATURE_BLOCK_COLOR = "featureBlockColor"
@Field final THEME_BLUE = "theme-blue"

@Field final TOPIC_LINK_CONTENT = "eaton/components/content/topic-link"
@Field final TOPIC_LINK_COLOR = "topicLinkColor"
@Field final SPECIFIC_COLOR = "#005EB8"

@Field final GRID_2_COLUMN_CONTRO_CONTENT = "eaton/components/layout/grid-2-column-control"
@Field final COLOR = "color"
@Field final BLUE = "blue"

@Field final GRID_3_COLUMN_CONTRO_CONTENT = "eaton/components/layout/grid-3-column-control"

@Field final CTA_BTN_CONTENT = "eaton/components/general/ctabutton"


@Field final PRIMARY_BRANDING_COLOR = "primary-branding-color"

@Field final SLING_RESOURCETYPE = "sling:resourceType"
@Field final CQ_LAST_REPLICATION_ACTION = "cq:lastReplicationAction"
@Field final CQ_LAST_REPLICATED = "cq:lastReplicated"
@Field final JCR_LAST_MODIFIED = "jcr:lastModified"
@Field final CQ_ACTIVATE_ACTION = "Activate"
@Field final LANGUAGE_MASTERS = "language-masters"

def data =[]

eaton_page_base_array.each { pageBasePath ->
    getNode(pageBasePath).recurse { content ->
        if (content.hasProperty(SLING_RESOURCETYPE)) {
            try {
                def changeMade = false
                if (content != null && content.getProperty(SLING_RESOURCETYPE).getString() == FEATURE_BLOCK_CONTENT
                        && content.hasProperty(FEATURE_BLOCK_COLOR) && content.getProperty(FEATURE_BLOCK_COLOR).getString() == THEME_BLUE) {
                    def featureBlockColor = content.getProperty(FEATURE_BLOCK_COLOR).getString();
                    data.add([content.path, "Update", "feature_block", featureBlockColor, PRIMARY_BRANDING_COLOR])
                    content.setProperty(FEATURE_BLOCK_COLOR,PRIMARY_BRANDING_COLOR)
                    changeMade = true
                }

                if (content != null && content.getProperty(SLING_RESOURCETYPE).getString() == TOPIC_LINK_CONTENT
                        && content.hasProperty(TOPIC_LINK_COLOR) && content.getProperty(TOPIC_LINK_COLOR).getString() == SPECIFIC_COLOR) {
                    def topicLinkColor = content.getProperty(TOPIC_LINK_COLOR).getString();
                    data.add([content.path, "Update", "topic_link", topicLinkColor, PRIMARY_BRANDING_COLOR])
                    content.setProperty(TOPIC_LINK_COLOR,PRIMARY_BRANDING_COLOR)
                    changeMade = true
                }

                if (content != null && content.getProperty(SLING_RESOURCETYPE).getString() == GRID_2_COLUMN_CONTRO_CONTENT
                        && content.hasProperty(COLOR) && content.getProperty(COLOR).getString() == BLUE) {
                    def gridTwoColumnControColor = content.getProperty(COLOR).getString();
                    data.add([content.path, "Update", "grid_2_column_contro", gridTwoColumnControColor, PRIMARY_BRANDING_COLOR])
                    content.setProperty(COLOR,PRIMARY_BRANDING_COLOR)
                    changeMade = true
                }

                if (content != null && content.getProperty(SLING_RESOURCETYPE).getString() == GRID_3_COLUMN_CONTRO_CONTENT
                        && content.hasProperty(COLOR) && content.getProperty(COLOR).getString() == BLUE) {
                    def gridThreeColumnControColor = content.getProperty(COLOR).getString();
                    data.add([content.path, "Update", "grid_3_column_contro", gridThreeColumnControColor, PRIMARY_BRANDING_COLOR])
                    content.setProperty(COLOR,PRIMARY_BRANDING_COLOR)
                    changeMade = true
                }

                if (content != null  && content.getProperty(SLING_RESOURCETYPE).getString() == CTA_BTN_CONTENT
                        && content.hasProperty(COLOR) && content.getProperty(COLOR).getString() == "light" ) {
                    def ctaButtonColor = content.getProperty(COLOR).getString();
                    data.add([content.path, "Update", "cta_button", ctaButtonColor, PRIMARY_BRANDING_COLOR])
                    content.setProperty(COLOR,PRIMARY_BRANDING_COLOR)
                    changeMade = true
                }

                if (changeMade && activate_content && canPerformActivation(content) && !content.path.contains(LANGUAGE_MASTERS)) {
                    data.add([content.path, "Activation", "", "", ""])
                    if (! dry_run) {
                        activate(content.path);
                    }
                } else if (changeMade && isActivated(content)) {
                    // If the node needs activated but wasn't activated then report to the user.
                    data.add([content.path, "Unactivated Update", "", "", ""])
                } else if (changeMade && activate_content && canPerformActivation(content) && content.path.contains(LANGUAGE_MASTERS)) {
                    // If the node contains language-masters.
                    data.add([content.path, "(Language master page) unactivated Update", "", "", ""])
                }
            } catch (VersionException ex) {
                println(ex)
            }
        }
    }
}

/**
 * @returns true if either the content is safe to activate or the force_activate configuration is turned on.
 */
def canPerformActivation(node) {
    return safeToActivate(node) || force_activate
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
 * @returns Whether or not the node needs activated based upon whether or not the last activation
 *          action was "Activate"
 */
def isActivated(node) {
    def action = node.hasProperty(CQ_LAST_REPLICATION_ACTION) ? node.getProperty(CQ_LAST_REPLICATION_ACTION).string : ""
    return action == CQ_ACTIVATE_ACTION
}

/**
 * @returns Whether or not it is safe to publish this node determined by checking
 *          if it is already activated and by checking to see if it has unpublished changes.
 */
def safeToActivate(node) {
    return isActivated(node) && ! nodeHasUnpublishedChanges(node)
}

if (!dry_run) {
    save()
    println("Updated values.")
} else {
    println("This is a dry run.")
}

println("")
println("1. Info: If a row in the column says 'Unactivated Update' this is because the node was changed but not activated.")
println("This happens when the node already had unactivated changes and the force_activate configuration is false.")
println("2. If a row in the column says '(Language master page) unactivated Update' this is because the node has contain language-masters.")

table {
    columns("Node Path", "Action", "Componet Name", "Old value", "New Value")
    rows(data)
}
