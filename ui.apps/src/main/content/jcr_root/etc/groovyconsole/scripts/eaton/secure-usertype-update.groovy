package etc.groovyconsole.scripts.eaton

import com.day.cq.commons.jcr.JcrConstants

def property_path
def delay = 0 //in Milliseconds.
def final dry_run = true
def isupdatepage= true
def tags_updated = 0

if(isupdatepage){
    property_path="/content/eaton/us/en-us/secure"
}else{
    property_path="/content/dam/eaton"
}

getNode(property_path).recurse  { 
  node ->
    Resource res = resourceResolver.getResource(node.path)
    if (res != null) {
      Node tempNode = res.adaptTo(javax.jcr.Node)
      PropertyIterator it = tempNode.getProperties()
      while (it.hasNext()) {
        Property property = it.nextProperty()
        if (property.getName()=="userType") {
          println "\nAsset Path: " + tempNode.getPath() 
          if(property.isMultiple()){ 
            Value[] jcrValues = new Value[property.getValues().length];
            ValueFactory valueFactory = tempNode.getSession().getValueFactory();
            int i=0;
            for (Value value : property.getValues()) {
               String usertype=value.toString()
               println "old tag value: " + usertype
               String accounttype=usertype.replace("usertype","accounttype")
               if(i<property.getValues().length){
               jcrValues[i] = valueFactory.createValue(accounttype);
               println "updated tag value: " + jcrValues[i]
               }
               i++
             }
            node.setProperty("userType", jcrValues,PropertyType.STRING);
            node.setProperty("accountType", jcrValues,PropertyType.STRING);
            tempNode.getProperty("userType").remove()
         }
       }
     }
   }
}

if(!dry_run){
    session.save()
    println("Total Updated templates count:")
}else{
    println("This is dry run.")
}
