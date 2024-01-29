package etc.groovyconsole.scripts.eaton

import groovy.transform.Field
import com.day.cq.commons.jcr.JcrConstants

/* If this is set to true, no actual content changes will be made, but the changes
   that would have been made will be reported */
@Field final DRY_RUN = true

@Field final CONTENT_PATH = "/content/eaton"
@Field final META_ROBOT_TAGS = "meta-robot-tags"
@Field final NOINDEX_NOFOLLOW = "noindex,nofollow"
@Field final PROPERTY_ROBOT = "robot"
@Field final PROPERTY__VALUE_ROBOT = "true"


getPage(CONTENT_PATH).recurse { page ->
    def content = page.node
    if (content!=null && content.get(META_ROBOT_TAGS) == NOINDEX_NOFOLLOW) 
    {
        content.setProperty(PROPERTY_ROBOT,PROPERTY__VALUE_ROBOT);
        if (! DRY_RUN) {
						save()
				  }
		println ">>"+page.path;
    }
}
