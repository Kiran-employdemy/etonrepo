package etc.groovyconsole.scripts.eaton

import groovy.transform.Field

/*
    Run the script for each "region" during a down-time period in that region.
    Example:  North America, Central and South America, Western Europe, Eastern and Southern Europe, Mid-East and Africa, Asia, Pacific
    NOTE: When the script is handling a country, the script will run for the language folder it finds in the country in the language-masters folder as well.
          It keeps record of having treated already the language folder in language-masters as it can happen that a country has en-us also.
          This prevents re-processing of this folder in that case.
          If the path in data is not existing in the country in countries list, but exists in the language masters equivalent, it will treat the path in language masters.
    It is possible to limit the aria of change even further by being more granular in path selection you run this script.
    Example: path:"/catalog/alarms-and-signaling-devices" instead of /catalog/

    When selecting verbose you will get a detail of what has been moved from to where and in dry run you will even see what node structure will be there after the move
    If you don't want that detail, you can set that to false and you will get numbers of processed nodes instead.

    Example of data:
    {
        "verbose":true,
        "dryRun":true,
        "path":"/catalog/",
        "nodeSourceType":"eaton/components/product/product-family-card",
        "nodeDestinationPath":"/jcr:content/root/responsivegrid",
        "beforeNodeWithName":"product_tabs",
        "countries":    ["ca", "mx", "us"]}  //North America
                        ["ar", "br", "cl", "co", "cr", "pe"]}  //Central and South America
                        ["at", "be", "ch", "de", "dk", "ee", "es", "fi", "fr", "gb", "ie", "it", "nl", "no", "pt", "se"]}  //Western Europe
                        ["bg", "cz", "gr", "hr", "hu", "lt", "lv", "pl", "ro", "rs", "ru", "sk", "ua"]}  //Eastern and Southern Europe
                        ["ae", "il", "ke", "ma", "ng", "tr", "za"]}  //Mid-East and Africa
                        ["cn", "in", "jp", "kr", "my", "sg", "th", "tw", "vn"]}  //Asia
                        ["au", "id", "nz", "ph"]}  //Pacific
*/

@Field def verbose=data.verbose
@Field def dryRun = data.dryRun
@Field def path = data.path
@Field def nodeDestinationPath = data.nodeDestinationPath
@Field def nodeSourceType = data.nodeSourceType
@Field def languageMasterTreated = [:]
@Field def queryManager = session.workspace.queryManager
@Field def countryCount = 0
@Field def languageMastersCount = 0
@Field def beforeNodeWithName = data.beforeNodeWithName
@Field def failedMoves = []
def countries = data.countries

try {
    countries.each { country ->
        def rootPath = "/content/eaton/" + country
        if (!session.nodeExists(rootPath)) {
            println 'Path does not exist in JCR!  Path: ' + rootPath + '.  Continuing to next country.'
        } else {
            def rootNode = session.getNode(rootPath)
            def childNodes = rootNode.getNodes()
            if (childNodes.size == 1) {
                handleLanguage(rootNode)
                handleLanguageMaster(rootNode)
            }
            childNodes.each { child ->
                if (isPage(child)) {
                    handleLanguage(child)
                    handleLanguageMaster(child)
                }
            }
        }
        println '*************************************************'
        println dryRun?'The move would have been executed on:' : 'The move of the node has been executed on:'
        println 'Country pages ' + countryCount
        println 'Language master pages ' + languageMastersCount
        println '*************************************************'
        println "Failed moves because we didn't find the node with name ${beforeNodeWithName} instead were:"
        failedMoves.each{failedMove ->
            println(failedMove)
        }
        println '*************************************************'
    }
} catch (Exception e) {
    println "Exception :: " + e.getMessage()
}

def handleLanguage(node) {
    def rootPath = node.path + path
    if (!session.nodeExists(rootPath)) {
        println 'Path does not exist in JCR! Path: ' + rootPath + '.'
        return
    }
    println 'Node with name ' + rootPath + ' to handle.'
    performMoveOnRoot(rootPath)
}

def performMoveOnRoot(rootPath) {
    def rootNodeToHandle = session.getNode(rootPath)
    def childNodes = rootNodeToHandle.getNodes()
    if (childNodes.size == 1) {
        performMoveOnChild(rootNodeToHandle)
    }
    childNodes.each { child ->
        if (isPage(child)) {
            performMoveOnChild(child)
        }
    }
}

def performMoveOnChild(child) {
    sqlQuery = 'select * from [nt:unstructured] as node where ISDESCENDANTNODE(node, "' + child.path + '") AND node.[sling:resourceType]="' + nodeSourceType + '"'
    queryManager.createQuery(sqlQuery, 'JCR-SQL2').execute().nodes.each { node ->
        def origin = node.path
        def parentDestination = node.path.substring(0, node.path.indexOf("/jcr:content"))
        def destination = parentDestination + nodeDestinationPath + '/' + node.name
        if (session.nodeExists(destination)) {
            return
        }
        if(verbose) {
            println dryRun ? 'Would be processed : ' + origin + ' ---> ' + destination : 'Moving ' + origin + ' to ' + destination
        }
        session.move(origin, destination)
        def destinationNode = session.getNode(parentDestination + nodeDestinationPath)
        if(!session.nodeExists(parentDestination + nodeDestinationPath + '/' + beforeNodeWithName)) {
            failedMoves.add(origin)
            return
        }
        destinationNode.orderBefore(node.name, beforeNodeWithName)
        if (dryRun && verbose) {
            def newNodeStructure
            destinationNode.getNodes().each { part ->
                newNodeStructure += part.name + ', '
            }
            println 'After move and re-ordering the new node structure would be:' + newNodeStructure.substring(0, newNodeStructure.lastIndexOf(','))
        }
        if (!dryRun) {
            session.save()
        }
        if (child.path.contains('language-masters')) {
            languageMastersCount++
        } else {
            countryCount++
        }
    }
}

def handleLanguageMaster(node) {
    def rootPath = "/content/eaton/language-masters/" + node.name + path
    if (!session.nodeExists(rootPath)) {
        println 'Path in language masters does not exist in JCR!  Path: ' + rootPath + ' Strange, no ;-)?.'
        return
    }
    if (languageMasterTreated.containsKey(rootPath)) {
        if (verbose) {
            println rootPath + ' has already been treated, skipping...'
        }
        return
    }
    println 'Node in language masters ' + rootPath + ' to handle.'
    performMoveOnRoot(rootPath)
}

static def isPage(node) {
    return node.primaryNodeType.isNodeType("cq:Page")
}