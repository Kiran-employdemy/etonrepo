package etc.groovyconsole.scripts.eaton

import javax.jcr.query.QueryResult
import javax.jcr.query.Query
import groovy.transform.Field

/*
    Get all paths of nodes that contain a specific property

    Example of data:
    {
        "dryRun": true,
        "startPath": "/content/eaton",
        "nodeType": "PAGE",
        "propertyNameToFind": "sling:vanityPath"
    }

    possible nodeTypes: PAGE
*/

enum NodeType {
    PAGE("cq:PageContent"),

    final String value

    NodeType(String value) {
        this.value = value
    }
}

@Field def dryRun = data.dryRun
@Field def startPath = data.startPath
@Field def nodeType = NodeType.valueOf(data.nodeType).value
@Field def propertyNameToFind = data.propertyNameToFind
@Field def queryManager = session.workspace.queryManager

String queryString = "SELECT * FROM [${nodeType}] AS s WHERE ISDESCENDANTNODE(s,'${startPath}') AND s.[${propertyNameToFind}] IS NOT NULL"

Query query = queryManager.createQuery(queryString, Query.JCR_SQL2)
QueryResult result = query.execute()

if (dryRun) {
    print("Number of nodes found: " + result.nodes.size())
} else {
    result.nodes.each { node ->
        print(node.path + ",")
    }
}
