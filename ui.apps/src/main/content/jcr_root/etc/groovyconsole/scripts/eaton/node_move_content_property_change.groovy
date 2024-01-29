import groovy.transform.Field

/**
 * This script is responsible for moving any node from source to destination
 * Need to make sure DESTINATION_PATH and SOURCE_PATH do exists or you need to create them before executing this script
 * You can use the flags CONTENT_PIM_PAGE_PATH_CHANGE_REQUIRED and PDH_RECORD_PATH_CHANGE_REQUIRED to
 * make the required property change in the given path
 * This is developed as a part of EAT-1241
 */
/* If this is set to true, no actual content changes will be made, but the changes
   that would have been made will be reported */
@Field final DRY_RUN = true

/*
if this is set true, node/content mentioned at the source_path will be moved to destination_path
 */
@Field final NODE_OR_CONTENT_MOVE_REQUIRED = true

/*
if this is set true, it will change the pim page path in the content path mentioned at EATON_CONTENT_PATH to /var
After operation is done, please set it as false again
 */
@Field final CONTENT_PIM_PAGE_PATH_CHANGE_REQUIRED = false

/*
if this is set true, it will change the pdhRecordPath in the PRODUCT Path mentioned at PRODUCT_EATON_PATH to /var
After operation is done, please set it as false again
 */
@Field final PDH_RECORD_PATH_CHANGE_REQUIRED = false

/*
* Mention the source path from where nodes need to be moved. Make sure that this path exists
*/
@Field final SOURCE_PATH = "/etc/commerce/products/eaton"    // "/etc/commerce/pdh"

/*
* Mention the destination path  where nodes needs to be moved. Make sure that this path exists
*/
@Field final DESTINATION_PATH = "/var/commerce/products/eaton" //"/var/commerce/pdh"

/*
* Mention the content path where some property needs to be changed
*/
@Field final EATON_CONTENT_PATH = "/content/eaton"   //"/content/eaton"

/*Eaton new product path for commerce*/
@Field final PRODUCT_EATON_PATH = "/var/commerce/products/eaton"   //"/content/eaton"

/*Constants*/
@Field final  PIM_PAGE_PATH_CONSTANT= "pimPagePath"
@Field final PDH_RECORD_PATH_CONSTANT= "pdhRecordPath"

/* Below variables are for only reporting purpose*/
@Field final nodesMoved = [ ];
@Field final nodesAlreadyExistingAndNotDeleted = [ ];
@Field final pimPathChangedAtNodes = [ ];
@Field final pdhRecordPathChangedAtNodes = [ ];

/*
This method is responsible to move the source path to destination path
 */
if(NODE_OR_CONTENT_MOVE_REQUIRED){
    moveNodeStructure(SOURCE_PATH,DESTINATION_PATH)
}
//moving the node from one location to other
def moveNodeStructure(sourcePath,destinationPath){
    getNode(sourcePath).recurse  { node ->
        def currentnode = node.path.substring(sourcePath.length(),node.path.length())
        def currentnodeoriginal = node.path

        if(null != currentnode && currentnode.length() > 1){
            def tempDestinationPath = destinationPath.concat(currentnode)

            Resource res = resourceResolver.getResource(tempDestinationPath)
            if(null != res){
                Node tempNode = res.adaptTo(javax.jcr.Node);

                if(!tempNode.getParent().hasNode(currentnode.substring(1,currentnode.length()))){
                    nodesAlreadyExistingAndNotDeleted.add([currentnodeoriginal,destinationPath.concat(currentnode)])
                }
            } else {
                if(!DRY_RUN) {
                    move currentnodeoriginal to destinationPath.concat(currentnode)
                }
                nodesMoved.add([currentnodeoriginal,destinationPath.concat(currentnode)])
            }
        }
    }
}

if(CONTENT_PIM_PAGE_PATH_CHANGE_REQUIRED) {
    pimPagePathChange(EATON_CONTENT_PATH)
}
/**
 * This method is to update the pimPagePath property in all content path passed
 * @param aemContentPath
 * @return
 */
def pimPagePathChange(aemContentPath){
    getNode(aemContentPath).recurse  { node ->
        Resource res = resourceResolver.getResource(node.path.concat("/jcr:content"))
        if(null != res){
            Node tempNode = res.adaptTo(javax.jcr.Node)
            if(tempNode.hasProperty(PIM_PAGE_PATH_CONSTANT)){
                pimPathChangedAtNodes.add([tempNode.path])
                if(!DRY_RUN){
                    tempNode.setProperty(PIM_PAGE_PATH_CONSTANT,tempNode.get(PIM_PAGE_PATH_CONSTANT).replace("/etc/","/var/"))
                }
            }

        }
    }
    if(!DRY_RUN) {
        save()
    }
}

if(PDH_RECORD_PATH_CHANGE_REQUIRED) {
    pdhRecordPathChange(PRODUCT_EATON_PATH)
}

/**
 * This method is to update the pdhRecordPath property in all products product paths
 * @param aemPath
 * @return
 */
def pdhRecordPathChange(aemPath) {
    getNode(aemPath).recurse  { node ->
        Resource res = resourceResolver.getResource(node.path)
        if(null != res){
            Node tempNode = res.adaptTo(javax.jcr.Node)
            if(tempNode.hasProperty(PDH_RECORD_PATH_CONSTANT)){
                pdhRecordPathChangedAtNodes.add([tempNode.path])
                if(!DRY_RUN){
                    tempNode.setProperty(PDH_RECORD_PATH_CONSTANT,tempNode.get(PDH_RECORD_PATH_CONSTANT).replace("/etc/","/var/"))
                }
            }

        }
    }
    if(!DRY_RUN) {
        save()
    }
}



if(nodesMoved.size() > 0){
    println "\n Following Nodes have been or will be moved(if dry_run is false) and count is : "+nodesMoved.size()
    println nodesMoved
}

if(pimPathChangedAtNodes.size() > 0){
    println "\n Following Nodes pim path have been or will be changed(if dry_run is false) and count is : "+pimPathChangedAtNodes.size()
    println pimPathChangedAtNodes
}

if(pdhRecordPathChangedAtNodes.size() > 0){
    println "\n Following Nodes pdhRecordPath have been or will be updated(if dry_run is false) and count is : "+pdhRecordPathChangedAtNodes.size()
    println pdhRecordPathChangedAtNodes

}
