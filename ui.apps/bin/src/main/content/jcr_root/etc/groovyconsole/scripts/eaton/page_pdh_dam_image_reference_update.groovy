package etc.groovyconsole.scripts.eaton

import groovy.transform.Field

/**
 * EAT-3913 - Groovy script request
 * This groovy script will list down all pages & PDH which has /content/dam/eaton/products/industrialcontrols,drives,automation&amp;sensors/ reference
 * and replaces that with /content/dam/eaton/products/industrialcontrols-drives-automation-sensors/
 * 
 */

/*Eaton product path for commerce*/
@Field final PRODUCT_EATON_PATH = "/var/commerce/products/eaton"   

@Field final CONTENT_PATH = "/content/eaton"

/* If this is set to true, no actual content changes will be made, but the changes
   that would have been made will be reported */
@Field final DRY_RUN = true

@Field final PRIMARY_IMAGE_PROPERTY = "primaryImage"

@Field final INCORRECT_IMAGE_PATH = "/content/dam/eaton/products/industrialcontrols,drives,automation&amp;sensors/"

@Field final CORRECT_IMAGE_PATH   = "/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/"

def data = []

getNode(PRODUCT_EATON_PATH).recurse  { node ->
		Resource res = resourceResolver.getResource(node.path)
		if(null != res){
			Node product_pim_node = res.adaptTo(javax.jcr.Node)
			if(product_pim_node.hasProperty(PRIMARY_IMAGE_PROPERTY) ){
				if(product_pim_node.get(PRIMARY_IMAGE_PROPERTY).contains(INCORRECT_IMAGE_PATH))
				{
					String oldImagePath = product_pim_node.get(PRIMARY_IMAGE_PROPERTY);
				    String updatedImagePath = product_pim_node.get(PRIMARY_IMAGE_PROPERTY).replace(INCORRECT_IMAGE_PATH,CORRECT_IMAGE_PATH)
				    product_pim_node.setProperty(PRIMARY_IMAGE_PROPERTY, updatedImagePath)
					if (! DRY_RUN) {
						save()
					}
					data.add ([product_pim_node.path,oldImagePath,updatedImagePath])
				}
			}
		}
}


/*This method is used to Query the JCR and find results as per the Query.*/
def buildQuery(page,term) {
        def queryManager = session.workspace.queryManager;
        def statement = 'select * from nt:base where jcr:path like \''+page.path+'%\' and contains(*, \''+term+'\')';
        queryManager.createQuery(statement, 'sql');
    }

/*Defined Content Hierarchy */
final def page = getPage(CONTENT_PATH)

/*Template which is searched in the content hierarchy */
final def query = buildQuery(page, INCORRECT_IMAGE_PATH);
final def result = query.execute()

result.nodes.each { node ->
        node.getProperties().each{ property ->
            if(property.isMultiple() != true)
            {
               if(property.getString().contains(INCORRECT_IMAGE_PATH))
                {
				  String oldPropertyValue =  property.getString();
                  String updatedPropertyValue = property.getString().replace(INCORRECT_IMAGE_PATH,CORRECT_IMAGE_PATH)    
                  node.setProperty(property.name,updatedPropertyValue);
                  if (! DRY_RUN) {
						save()
				  }
				  data.add ([node.path,oldPropertyValue,updatedPropertyValue])
                }
            }
        }
   }

 
println 'Script Execution Complete!' 
 
 table
{
    columns("Updated Content Path","Old Property Value", "New Property Value")
    rows(data)
}




