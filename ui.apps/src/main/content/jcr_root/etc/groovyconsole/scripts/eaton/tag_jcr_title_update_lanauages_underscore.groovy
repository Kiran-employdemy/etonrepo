package etc.groovyconsole.scripts.eaton

import com.day.cq.commons.jcr.JcrConstants
import org.apache.commons.lang.LocaleUtils
import java.util.Locale

def eaton_tagpath = "/content/cq:tags/eaton"
def delay = 10 //in Milliseconds.
def jcr_title_constant = JcrConstants.JCR_TITLE+"."
def final dry_run = false
def tags_updated = 0
//just for logging purpose
def data =[]

getNode(eaton_tagpath).recurse  {
    node ->
        Resource res = resourceResolver.getResource(node.path)
        if (res != null) {
            Tag tag = res.adaptTo(com.day.cq.tagging.Tag)
            Node tempNode = res.adaptTo(javax.jcr.Node)
            PropertyIterator it = tempNode.getProperties()
            while (it.hasNext()) {
                Property property = it.nextProperty()

                if (property.getName().startsWith(jcr_title_constant)) {
                    if (null != property.getName().substring(10)) {
                        def locale = property.getName().substring(10)
                        def language = locale.substring(0, 2)
                        def localeCode = locale.substring(3,5)
                        def check1 = jcr_title_constant + language
                        def check2 = jcr_title_constant + language + "-" + language
                        def check3 = jcr_title_constant + locale

                        //for checking locale such as cz_cs. cz_cs will be converted to cs_cz. EAT-2377 & EAT-2340.
                        if (localeCode != language ) {
                            def existingLocale  = language.concat("_").concat(localeCode.toUpperCase());
                            def check4 = language.concat("_").concat(localeCode);
                            def actualLocal = jcr_title_constant.concat(localeCode.concat("_").concat(language));
                            Locale actualLocale = LocaleUtils.toLocale(existingLocale)
                            if (!LocaleUtils.isAvailableLocale(actualLocale)) {
                                data.add([node.path,jcr_title_constant.concat(check4),
                                          tempNode.get(jcr_title_constant.concat(check4)),
                                          actualLocal,tempNode.get(jcr_title_constant.concat(check4)),
                                          tempNode.get(jcr_title_constant.concat(check4)),"Updated"])
                                tempNode.setProperty(actualLocal, tempNode.get(jcr_title_constant.concat(check4)))
                                tags_updated++
                                data.add([node.path,check3,tempNode.get(check3),check3,tempNode.get(check3),
                                          tempNode.get(check3),"removed"])
                                tempNode.getProperty(check3).remove()
                                tags_updated++
                            }
                        }


                        //for cases like fr-fr / de-de ...
                        if (tempNode.hasProperty(check2)) {
                            // def check4 = jcr_title_constant + language + "_" + language;

                            if(tempNode.hasProperty(check2.replace("-", "_"))){
                                data.add([node.path,check2.replace("-", "_"),tempNode.get(check2.replace("-", "_")),check2.replace("-", "_"),tempNode.get(check2.replace("-", "_")),tempNode.get(check2),"Updated"])
                            }
                            else {
                                data.add([node.path,check2.replace("-", "_"),null,check2.replace("-", "_"),null,tempNode.get(check2),"Created"])
                            }
                            tempNode.setProperty(check2.replace("-", "_"), tempNode.get(check2))
                            tags_updated++
                            data.add([node.path,check2,tempNode.get(check2),check2.replace("-", "_"),tempNode.get(check2),tempNode.get(check2.replace("-", "_")),"Removed"])
                            tempNode.getProperty(check2).remove()
                            tags_updated++
                            if (tempNode.hasProperty(check1)) {
                                data.add([node.path,check1,tempNode.get(check1),check2.replace("-", "_"),tempNode.get(check2),tempNode.get(check2.replace("-", "_")),"Removed"])
                                tempNode.getProperty(check1).remove()
                                tags_updated++
                            }
                        }

                        // for cases like fr-ca en-gb he-il
                        else if (tempNode.hasProperty(check3) && check3.contains("-")) {
                            //def check5 = jcr_title_constant + locale.replace("-", "_");
                            if(tempNode.hasProperty(check3.replace("-", "_"))){
                                data.add([node.path,check3.replace("-", "_"),tempNode.get(check3.replace("-", "_")),check3.replace("-", "_"),tempNode.get(check3.replace("-", "_")),tempNode.get(check3),"Updated"])
                            }
                            else {
                                data.add([node.path,check3.replace("-", "_"),null,check3.replace("-", "_"),null,tempNode.get(check3),"Created"])
                            }
                            tempNode.setProperty(check3.replace("-", "_"), tempNode.get(check3))
                            tags_updated++
                            data.add([node.path,check3,tempNode.get(check3),check3.replace("-", "_"),tempNode.get(check3),tempNode.get(check2.replace("-", "_")),"Removed"])
                            tempNode.getProperty(check3).remove()
                            tags_updated++
                        }

                        // for cases like fr de en he
                        else if (tempNode.hasProperty(check3) && !check3.contains("-") && !check3.contains("_")) {

                            def localeWithNoCountry = ""
                            if (locale.equals("en")) {
                                localeWithNoCountry = jcr_title_constant + "en_us"
                            } else {
                                localeWithNoCountry = jcr_title_constant + language + "_" + language
                            }
                            if(tempNode.hasProperty(localeWithNoCountry)){
                                data.add([node.path,check3,tempNode.get(check3),localeWithNoCountry,tempNode.get(localeWithNoCountry),tempNode.get(check3),"Removed"])
                                tempNode.getProperty(check3).remove()
                                tags_updated++
                            }
                            else {
                                data.add([node.path,null,null,localeWithNoCountry,null,tempNode.get(check3),"Created"])
                                tempNode.setProperty(localeWithNoCountry, tempNode.get(check3))
                                tags_updated++
                                data.add([node.path,check3,tempNode.get(check3),localeWithNoCountry,tempNode.get(localeWithNoCountry),tempNode.get(check3),"Removed"])
                                tempNode.getProperty(check3).remove()
                                tags_updated++

                            }
                        }
                    }
                }
            }
        }

}

if( dry_run ) {
    println "This is dry run"
    println "Total  Tags that will be updated:" + tags_updated
}
else {
    println "Total Updated Tags :" + tags_updated
    save()
}

table {
    columns("Tag Path", "Existing property name", "Existing property value", "NewReplaced property Name", "NewReplaced property exisitng Value", "NewReplaced Property New Value", "Action")
    rows(data)
}