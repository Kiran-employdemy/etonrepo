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
        "propertyToChange":"sling:resourceType"
        "oldValue":"eaton/components/product/product-family-card",
        "newValue":"eaton/components/product/product-family-card-v2",
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
@Field def propertyToChange = data.propertyToChange
@Field def oldValue = data.oldValue
@Field def newValue = data.newValue
@Field def languageMasterTreated = [:]
@Field def queryManager = session.workspace.queryManager
@Field def countryCount = 0
@Field def languageMastersCount = 0

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
        println dryRun?"The property ${propertyToChange} would have been changed on:" : "The property ${propertyToChange} of the node has been changed on:"
        println 'Country pages ' + countryCount
        println 'Language master pages ' + languageMastersCount
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
    sqlQuery = "select * from [nt:unstructured] as node where ISDESCENDANTNODE(node, '${child.path}') AND node.[${propertyToChange}]='${oldValue}'"
    queryManager.createQuery(sqlQuery, 'JCR-SQL2').execute().nodes.each { node ->
        node.setProperty(propertyToChange, newValue)
        if(verbose) {
            def changedProperty = node.getProperty(propertyToChange)
            println dryRun ? "Value ${oldValue} of property ${propertyToChange} would be changed on: ${node.path} to ${changedProperty}" : "Changing  ${oldValue} of property ${propertyToChange} on ${node.path} to ${changedProperty}"
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