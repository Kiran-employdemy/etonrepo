package etc.groovyconsole.scripts.eaton

import groovy.transform.Field

/*
    Run this script for the list of paths gotten from the output of the report generated with all references to business group tags.

    When selecting verbose you will get a detail of what has been changed.
    If you don't want that detail, you can set that to false and you will get numbers of processed nodes instead, separated in how many assets and pages.

    Example of data:
    {
        "verbose":true,
        "dryRun":true,
        "propertiesToRemove": ["businessGroup"],
        "paths": "/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/lighting-contactors-v10-t4-ca08100012e.pdf/jcr:content/metadata,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/packaged-control-solutions-br123001en.pdf/jcr:content/metadata,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/c30cn-mechanically-held-lighting-contactors/lighting-conactors-for-construction-quick-selector-guide-TD03701001E.pdf/jcr:content/metadata,/content/eaton/ca/fr-ca/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/eaton/language-masters/en-gb/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/c30cn-mechanically-held-lighting-contactors/section-16485-contactors-product-specification.doc/jcr:content/metadata,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/freedom-nema-motor-control/box-1-and-box-2-non-combination-enclosed-control-and-c600m-cover-control-kit-wiring-p52899.pdf/jcr:content/metadata,/content/eaton/cr/es-mx/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/enclosed-dimensions-v10-t14-ca08100012e.pdf/jcr:content/metadata,/content/eaton/us/en-us/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/non-combination-box-1-enclosed-control-pa123002en.pdf/jcr:content/metadata,/content/eaton/language-masters/el/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/eaton/br/en-us/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/eaton/jp/en-us/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/eaton/id/en-us/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/eaton/language-masters/fr-ca/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/eaton/cr/en-us/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/eaton/sg/en-us/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/c30cn-mechanically-held-lighting-contactors/2-and-3-wire-control-for-c30cnm-mechanically-held-30a-lighting-contractors-AP03702001E.pdf/jcr:content/metadata,/content/eaton/language-masters/es-mx/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/industrial-controls-line-card-sa083077en.pdf/jcr:content/metadata,/content/eaton/language-masters/zh-tw/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/eaton/ar/es-mx/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/eaton/tw/en-us/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/eaton/language-masters/en-us/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/c30cn-mechanically-held-lighting-contactors/c30cn-lighting-contactors-installation-instructions-PUB50765.pdf/jcr:content/metadata,/content/eaton/kr/en-us/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/enclosed-control-reference-guide-sa03311001e.pdf/jcr:content/metadata,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/legacy-page---soft-starters/low-voltage-motor-starters-and-contactors-consulting-application-guide-tb03300002e.pdf/jcr:content/metadata,/content/eaton/ca/en-gb/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/lighting-contactors-v5-t6-ca08100006e.pdf/jcr:content/metadata,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/lighting-contactors-br03702001e.pdf/jcr:content/metadata,/content/eaton/language-masters/el-gr/catalog/machinery-controls/c30cn-lighting-contactors/jcr:content,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/c30cn-mechanically-held-lighting-contactors/c30cn-lighting-contactors-power-pole-kits-instructions-PUB50767.pdf/jcr:content/metadata,/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/c30cn-mechanically-held-lighting-contactors/c30cn-lighting-contactors-coil-kits-instructions-PUB50766.pdf/jcr:content/metadata"
    }
*/

@Field def verbose = data.verbose
@Field def dryRun = data.dryRun
@Field def paths = data.paths
@Field def propertiesToRemove = data.propertiesToRemove
@Field def queryManager = session.workspace.queryManager
@Field def pagesCount = 0
@Field def assetsCount = 0
@Field def pathsSkipped = 0


try {
    paths.split(',').each { path ->
        if (!session.nodeExists(path)) {
            println 'Path does not exist in JCR!  Path: ' + path + '.  Continuing to next path.'
            augmentSkipped();
        } else {
            def node = session.getNode(path)
            performRemoveOfPropertiesOnNode(session, node)
        }
    }
    println '*************************************************'
    println dryRun ? "The properties ${propertiesToRemove} would have been removed on :" : "The properties ${propertiesToRemove} have been removed from:"
    println 'Pages ' + pagesCount
    println 'Assets ' + assetsCount
    println 'Paths skipped ' + pathsSkipped
    println 'Total ' + (pagesCount + assetsCount + pathsSkipped)
    println '*************************************************'
} catch (Exception e) {
    println "Exception :: " + e.getMessage()
}

def performRemoveOfPropertiesOnNode(session, node) {
    printLineIfVerbose(dryRun ? "The properties ${propertiesToRemove} would be removed on ${node.path}" : "Removing ${propertiesToRemove} on ${node.path}" )
    def jcrContent = session.getNode(node.path.substring(0, node.path.indexOf("/jcr:content") + "/jcr:content".length()))
    def parentNode = getResourcePath(node, session)
    printLineIfVerbose("parent node is ${jcrContent.path}")
    def lastReplicationActionProperty
    try {
        lastReplicationActionProperty = jcrContent.getProperty('cq:lastReplicationAction')
    } catch (Exception e) {
        printLineIfVerbose("No lastReplicationAction present")
    }
    def lastReplicationAction = lastReplicationActionProperty != null ? lastReplicationActionProperty.getValue() as String : ""
    printLineIfVerbose("lastReplicationAction is ${lastReplicationAction}")
    boolean activated = lastReplicationAction == 'Activate'
    boolean propertyFound = false;
    propertiesToRemove.each() { propertyName ->
        node.getProperties().each { property ->
            if (property.name == propertyName) {
                propertyFound = true
                printLineIfVerbose(dryRun ? "The property ${property.name} would be removed." : "The property ${property.name} will be removed" )
                property.remove()
                if(activated && dryRun) {
                    printLineIfVerbose("The parentNode ${parentNode.path} would have been re-activated")
                }
                if (!dryRun) {
                    session.save()
                    if (activated) {
                        activate(parentNode.path)
                        printLineIfVerbose("The parentNode ${parentNode.path} is re-activated")
                    }
                }
                augmentCounter(session, node)
            }
        }
    }
    if (!propertyFound) {
        augmentSkipped()
    }
}

private void augmentSkipped() {
    pathsSkipped++
}

private void augmentCounter(session, node) {
    if (isPage(session, node)) {
        pagesCount++
    } else if (isAsset(session, node)) {
        assetsCount++
    }
}

private static def isPage(session, node) {
    def typePage = getResourcePath(node, session).primaryNodeType.isNodeType("cq:Page")
    return typePage
}

private static def getResourcePath(node, session) {
    def path = node.path
    if (path.contains("/jcr:content")) {
        def substring = path.substring(0, path.indexOf("/jcr:content"))
        return session.getNode(path.substring(0, path.indexOf("/jcr:content")))
    }
}

private static def isAsset(session, node) {
    def typeAsset = getResourcePath(node, session).primaryNodeType.isNodeType("dam:Asset")
    return typeAsset
}

private void printLineIfVerbose(String debugStatement) {
    if (verbose) {
        println debugStatement;
    }
}
