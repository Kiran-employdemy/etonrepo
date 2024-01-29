package etc.groovyconsole.scripts.eaton

def buildQuery(parent) {
    def queryManager = session.workspace.queryManager;
    def statement = 'select * from [nt:unstructured] as node where ISDESCENDANTNODE(node, "' + parent + '") AND node.[sling:resourceType] = "commerce/components/product" AND node.[cq:tags] like "eaton:product-taxonomy/%"';
    queryManager.createQuery(statement, 'JCR-SQL2');
}

final def query = buildQuery('/var/commerce/products/eaton');
final def result = query.execute()
def DRY_RUN = true;
def totalPimsUpdated = 0
def totalTagsAdded = 0

result.nodes.each {
    node ->

        if(node.hasProperty('cq:tags')){
            Property tags = node.getProperty('cq:tags');
            int total = 0;
            for (Value tag : tags.getValues()) {
                if(tag.toString().contains('eaton:product-taxonomy/')) {
                    total++;
                }
            }

            if (total > 0) {
                String[] tagsList = new String[total];
                int idx = 0;

                for (Value tag : tags.getValues()) {
                    if(tag.toString().contains('eaton:product-taxonomy/')) {
                        tagsList[idx++] = tag.toString();
                        totalTagsAdded++;
                    }
                }

                node.setProperty('cq:primaryProductTaxonomy', tagsList);
                totalPimsUpdated++;
            }
        }
        if (!DRY_RUN) {
            session.save();
        }
}

if (DRY_RUN) {
    println "This is dry run"
    println "Total PIM nodes that will be updated: " + totalPimsUpdated
    println "Total tags that will be added to pim nodes: " + totalTagsAdded
} else {
    println "Total PIM nodes updated: " + totalPimsUpdated
    println "Total tags added to pim nodes: " + totalTagsAdded
}
