import groovy.transform.Field

/* -------------------------------------------------------------------------- */
/* -------------------------- SCRIPT OVERVIEW ------------------------------- */
/* -------------------------------------------------------------------------- */

/* This script will migrate a page property from one name to another name.
   A dry run option and various activation options are provided and are explained
   in further detail in the configuration section below. */

/* -------------------------------------------------------------------------- */
/* --------------------------- CONFIGURATIONS ------------------------------- */
/* -------------------------------------------------------------------------- */

/* If this is set to true no changes will actually be performed and no content
   will be activated. All the content updates and activations that would have
   been performed will be reported. */
@Field final DRY_RUN = true

/* If this is set to true and DRY_RUN is set to true then changes made to nodes
   that are already activated and that have not been changed since the last
   activation will be activated. */
@Field final ACTIVATE = true

/* If this is set to true, DRY_RUN is set to true and ACTIVATE is set to true
   then all the changes that the script performs to already activated nodes will
   be activated without checking the last modified time. */
@Field final FORCE_ACTIVATE = false

/* If this is set to true and DRY_RUN is set to false then the configured
   OLD_PROP will be deleted after being mobed to NEW_PROP . */
@Field final DELETE_OLD_PROPERTY = true

/* The property name that you want to move away from. */
@Field final OLD_PROP = "redirectTarget"

/* The property name that you want to move to. */
@Field final NEW_PROP = "cq:redirectTarget"

/* The list of root paths to recursively search under */
@Field final ROOT_PATHS = [ "/content/eaton", "/content/eaton-cummins" ]

/* -------------------------------------------------------------------------- */
/* ---------------------------- Constants ----------------------------------- */
/* -------------------------------------------------------------------------- */
@Field final PROPS = [
  NAME: [
    JCR_PRIMARY_TYPE: "jcr:primaryType",
    JCR_LAST_MODIFIED: "jcr:lastModified",
    CQ_LAST_REPLICATED: "cq:lastReplicated"
  ],
  VALUE: [
    CQ_PAGE: "cq:Page",
  ]
]

@Field final NODES = [
  JCR_CONTENT: "jcr:content",
  LANGUAGE_MASTER: "language-master"
]

@Field final MESSAGES = [
  ACTIVATED: "Activated",
  WILL_ACTIVATE: "Will activate",
  COULD_NOT_BE_ACTIVATED: "Could not be activated",
  WAS_NOT_ACTIVATED: "Was not activated",
  ALREADY_EXISTS: "New prop already exists"
]

@Field final DATA = []

/* -------------------------------------------------------------------------- */
/* -------------------------------------------------------------------------- */
/* -------------------------------------------------------------------------- */

def isValidPage(node) {
  return node.hasProperty(PROPS.NAME.JCR_PRIMARY_TYPE) &&
         node.getProperty(PROPS.NAME.JCR_PRIMARY_TYPE).getString() == PROPS.VALUE.CQ_PAGE &&
         node.hasNode(NODES.JCR_CONTENT)
}

def needsUpdated(jcrContentNode) {
  return node.hasProperty(OLD_PROP) && ! node.hasProperty(NEW_PROP)
}

def forEachApplicablePageNode(callback) {
  ROOT_PATHS.each { rootPath ->
    getNode(rootPath).recurse { node ->
      if (isValidPage(node)) {
        def jcrContentNode = node.getNode(NODES.JCR_CONTENT)

        if (jcrContentNode.hasProperty(OLD_PROP)) {
          callback(node, jcrContentNode)
        }
      }
    }
  }
}

def hasChange(node) {
  if (node.hasProperty(PROPS.NAME.CQ_LAST_REPLICATED) && node.hasProperty(PROPS.NAME.JCR_LAST_MODIFIED)) {
    def replicated = node.getProperty(PROPS.NAME.CQ_LAST_REPLICATED).getDate()
    def modified = node.getProperty(PROPS.NAME.JCR_LAST_MODIFIED).getDate()
    return modified > replicated
  } else {
    return false
  }
}

def isLanguageMaster(node) {
  return node.path.contains(NODES.LANGUAGE_MASTER)
}

def updateJcrContentNode(pageNode, jcrContentNode) {
  def activateMessage
  def updateMessage
  if (jcrContentNode.hasProperty(NEW_PROP)) {
    updateMessage = MESSAGES.ALREADY_EXISTS
    activateMessage = MESSAGES.WAS_NOT_ACTIVATED
  } else {
    def oldProp = jcrContentNode.getProperty(OLD_PROP)
    jcrContentNode.setProperty(NEW_PROP, oldProp.getString())
    updateMessage = oldProp.getString()
    oldProp.remove()

    if ((! isLanguageMaster(jcrContentNode)) && (FORCE_ACTIVATE || (ACTIVATE && ! hasChange(jcrContentNode)))) {
      activateMessage = DRY_RUN ? MESSAGES.WILL_ACTIVATE : MESSAGES.ACTIVATED
    } else if (hasChange(jcrContentNode)) {
      activateMessage = MESSAGES.COULD_NOT_BE_ACTIVATED
    } else {
      activateMessage = MESSAGES.WAS_NOT_ACTIVATED
    }
  }
  DATA.add([jcrContentNode.path, activateMessage, updateMessage])
}

/* -------------------------------------------------------------------------- */
/* -------------------------------------------------------------------------- */
/* -------------------------------------------------------------------------- */

forEachApplicablePageNode { pageNode, jcrContentNode ->
  updateJcrContentNode(pageNode, jcrContentNode)
}

if (! DRY_RUN) {
  save()
}

DATA.each { entry ->
  if (! DRY_RUN && entry[1] == MESSAGES.ACTIVATED) {
    activate(entry[0])
  }
}

table {
  columns("Page Path", "Activation", "Content Update")
  rows(DATA)
}
