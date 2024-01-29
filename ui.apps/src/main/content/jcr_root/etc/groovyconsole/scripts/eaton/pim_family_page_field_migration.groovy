package etc.groovyconsole.scripts.eaton

import groovy.transform.Field
import javax.jcr.version.VersionManager

/* ------------------------------------- */
/* ----------- CONFIGURATION ----------- */
/* ------------------------------------- */

/* If this is set to true, no actual content changes will be made, but the changes
   that would have been made will be reported */
@Field final DRY_RUN = true

/* If set to true the PIMS_TO_UPDATE value will be ignored and all pims will be
   updated. If set to false, only the pim nodes identified in the PIMS_TO_UPDATE
   value will be updated */
@Field final UPDATE_ALL_LANGUAGES = false

/* If set to true the LANGUAGES_TO_UPDATE value will be ignored and pims under all
   languages will be updated. If set to false, only the pims under the languages
   specified in the PIMS_TO_UPDATE value will be updated */
@Field final UPDATE_ALL_PIMS = false

/* The specific pim nodes to update. This is only used if UPDATE_ALL_PIMS is
   set to false. */
@Field final PIMS_TO_UPDATE = [
  "_29293870"
]

/* The specific languages to update. This is only used if UPDATE_ALL_LANGUAGES is
   set to false. */
@Field final LANGUAGES_TO_UPDATE = [
  "en_us"
]

/* ---------------------------------------------------------------- */
/* ---- Define the constants and variables that will be needed. --- */
/* ---------------------------------------------------------------- */
@Field final PIM_FIELDS = [
  FAMILY_PAGE: "productFamilyPageMulti",
  SUB_CATEGORY: "primarySubCategoryMulti",
  SINGLE: "productFamilyPage"
]

@Field final PIM_NODES = [
  MULTI: "productFamilyPageMulti",
  FIRST_MULTI: "1"
]

@Field final PAGE_FIELDS = [
  SUB_CATEGORY: "primarySubCategory"
]

@Field final VERSIONABLE_NODE_TYPE = "mix:versionable"
@Field final LANGUAGE_GLOB = "*_*"
@Field final PIM_PATH = "/etc/commerce/products/eaton"

@Field final changes = [ ];
@Field final info = [ ];
@Field final warning = [ ];
@Field final errors = [ ];

/* We need a VersionManager because some of the PIM nodes are versionable and
   therefor require being checked out and checked in. */
@Field final VersionManager vm = session.getWorkspace().getVersionManager()

/* ---------------------------------------------------------------- */
/* ----------------- Define the needed methods. ------------------- */
/* ---------------------------------------------------------------- */

/* Updated the given field on the given node with the given property if DRY_RUN
   is not enabled. It will always append a change to the list of changes for
   reporting purposes. */
def applyUpdate(node, field, value) {
  changes << message(node.getPath(), field, value)
  if (! DRY_RUN) {
    final def isVersionable = node.isNodeType(VERSIONABLE_NODE_TYPE)
    final boolean checkBackIn = false

    try {
      if (isVersionable && ! vm.isCheckedOut(node.getPath())) {
        vm.checkout(node.getPath())
        // Only check the node back in if it started checked in
        checkBackIn = true
      }

      node.setProperty(field, value)
      save()

      if (isVersionable && checkBackIn && vm.isCheckedOut(node.getPath())) {
        vm.checkin(node.getPath())
      }
    } catch (e) {
      errors << "Error while setting property '${field}' on node, '${node.getPath}' to value: '${value}' \n" + e.getMessage()
    } finally {
      if (isVersionable && checkBackIn && vm.isCheckedOut(node.getPath())) {
        vm.checkin(node.gethPath());
      }
    }
  }
}

/* Returns a change message based upon the provided nodePath, field name, and value.
   The message is different depending on the value of DRY_RUN. */
def message(nodePath, field, value) {
  if (DRY_RUN) {
    return "Would have set property, '${field}' to '${value}' on node path: '${nodePath}'"
  } else {
    return "Setting property, '${field}' to '${value}' on node path: '${nodePath}'"
  }
}

/* Updates the PIM_FIELDS.SINGLE field if able and if needed. This method can assume
   that the pim node and fieldSet node are present but is responsible for checking
   for the existence of all other needed nodes and properties */
def updatePimNodeFamilyField(pim, fieldSet) {
  if (fieldSet.hasProperty(PIM_FIELDS.FAMILY_PAGE) && ! fieldSet.getProperty(PIM_FIELDS.FAMILY_PAGE).isMultiple()) {
    final def newFamilyPagePath = fieldSet.getProperty(PIM_FIELDS.FAMILY_PAGE).getString()

    if (! newFamilyPagePath.isEmpty()) {
      applyUpdate(pim, PIM_FIELDS.SINGLE, newFamilyPagePath)
    } else {
      info << "Property '${PIM_FIELDS.FAMILY_PAGE}' is empty on '${fieldSet.getPath()}'."
    }
  } else if (! fieldSet.hasProperty(PIM_FIELDS.FAMILY_PAGE)) {
    info << "Property '${PIM_FIELDS.FAMILY_PAGE}' not found on '${fieldSet.getPath()}'."
  } else if (fieldSet.getProperty(PIM_FIELDS.FAMILY_PAGE).isMultiple()) {
    warning << "Property '${PIM_FIELDS.FAMILY_PAGE}' is multivalued on '${fieldSet.getPath()}'."
  }
}

/* Updates the PAGE_FIELDS.SUB_CATEGORY field if able and if needed. This method
   can assume that the pim node and fieldSet node are present but is is responsible
   for checking for the existence of all other needed nodes and properties */
def updateFamilyPageSubCategoryField(fieldSet) {
  if (fieldSet.hasProperty(PIM_FIELDS.FAMILY_PAGE) && ! fieldSet.getProperty(PIM_FIELDS.FAMILY_PAGE).isMultiple() &&
      fieldSet.hasProperty(PIM_FIELDS.SUB_CATEGORY) && ! fieldSet.getProperty(PIM_FIELDS.SUB_CATEGORY).isMultiple()) {
    final def familyPagePath = fieldSet.getProperty(PIM_FIELDS.FAMILY_PAGE).getString()
    final def subCategory = fieldSet.getProperty(PIM_FIELDS.SUB_CATEGORY).getString()
    final def familyPage = getPage(familyPagePath)

    if (familyPage != null && familyPage.hasContent()) {
      final def contentNode = familyPage.getContentResource().adaptTo(Node.class)

      // If the contentNode does not have the property, or the property is empty (but not multiple).
      if (! contentNode.hasProperty(PAGE_FIELDS.SUB_CATEGORY) ||
           (! contentNode.getProperty(PAGE_FIELDS.SUB_CATEGORY).isMultiple() &&
              contentNode.getProperty(PAGE_FIELDS.SUB_CATEGORY).getString().isEmpty())) {
        applyUpdate(contentNode, PAGE_FIELDS.SUB_CATEGORY, subCategory)
      } else if (contentNode.hasProperty(PAGE_FIELDS.SUB_CATEGORY) &&
                 contentNode.getProperty(PAGE_FIELDS.SUB_CATEGORY).isMultiple()) {
        warning << "Property '${PAGE_FIELDS.SUB_CATEGORY}' is multivalued on '${contentNode.getPath()}'"
      }
    } else if (familyPage == null) {
      warning << "No page found at '${familyPagePath}' for the '${PIM_FIELDS.FAMILY_PAGE}' property on '${fieldSet.getPath()}'"
    } else if (! familyPage.hasContent()) {
      warning << "Property '${PIM_FIELDS.FAMILY_PAGE}' is empty on '${fieldSet.getPath()}'."
    }
  } else if (! fieldSet.hasProperty(PIM_FIELDS.FAMILY_PAGE)) {
    info << "Property '${PIM_FIELDS.FAMILY_PAGE}' is empty on '${fieldSet.getPath()}'."
  } else if (! fieldSet.hasProperty(PIM_FIELDS.SUB_CATEGORY)) {
    info << "roperty '${PIM_FIELDS.SUB_CATEGORY}' is empty on '${fieldSet.getPath()}'."
  } else if (fieldSet.getProperty(PIM_FIELDS.FAMILY_PAGE).isMultiple()) {
    warning << "Property '${PIM_FIELDS.FAMILY_PAGE}' is multivalued on '${fieldSet.getPath()}'."
  } else if (fieldSet.getProperty(PIM_FIELDS.SUB_CATEGORY).isMultiple()) {
    warning << "'${PIM_FIELDS.SUB_CATEGORY}' is mutivalued on '${fieldSet.getPath()}'."
  }
}

/* Given a PIM node, make the needed changes to both the product family property
   on the pim node and the sub category property on the corresponding family page. */
def updatePimNode(pim) {
  if (pim.hasNode(PIM_NODES.MULTI)) {
    final def multiNode = pim.getNode(PIM_NODES.MULTI)

    // If the pim node does not have the property, or the property is empty (but not multiple).
    if (! pim.hasProperty(PIM_FIELDS.SINGLE) || (! pim.getProperty(PIM_FIELDS.SINGLE).isMultiple() &&
          pim.getProperty(PIM_FIELDS.SINGLE).getString().isEmpty())) {
      if (multiNode.hasNode(PIM_NODES.FIRST_MULTI)) {
        updatePimNodeFamilyField(pim, multiNode.getNode(PIM_NODES.FIRST_MULTI))
      } else {
        info << "Node '${PIM_NODES.FIRST_MULTI}' not found under '${multiNode.getPath()}'."
      }
    } else if (pim.hasProperty(PIM_FIELDS.SINGLE) && pim.getProperty(PIM_FIELDS.SINGLE).isMultiple()) {
      warning << "Property '${PIM_FIELDS.SINGLE}' is multivalued on '${pim.getPath()}'."
    }

    multiNode.getNodes().each({ fieldSet ->
      updateFamilyPageSubCategoryField(fieldSet)
    })
  } else {
    info << "Node '${PIM_NODES.MULTI}' not found under '${pim.getPath()}'."
  }
}

/* Given a PIM language node, update all of the PIM nodes under it that match
   the configuration. */
def updateLanguage(language) {
  if (UPDATE_ALL_LANGUAGES || LANGUAGES_TO_UPDATE.contains(language.getName())) {
    language.getNodes().each({ pim ->
      if (UPDATE_ALL_PIMS || PIMS_TO_UPDATE.contains(pim.getName())) {
        updatePimNode(pim)
      }
    })
  }
}

/* Given a set of PIM language nodes, perform the migration for each language and
   then perform a final save of the changes to ensure all changes have been saved. */
def migrateFamilyPageField(pimLanguageNodes) {
  pimLanguageNodes.each({ language ->
    updateLanguage(language)
  })
}

/* ---------------------------------------------------------------- */
/* --------------- Actually perform the migration. ---------------- */
/* ---------------------------------------------------------------- */
migrateFamilyPageField(getNode(PIM_PATH).getNodes(LANGUAGE_GLOB))

/* ---------------------------------------------------------------- */
/* -------- Report the changes, warnings and info messages. ------- */
/* ---------------------------------------------------------------- */
if (changes.size() > 0) {
  println(changes.size() + " changes:")
  changes.each({ change ->
    println(change)
  })
} else {
  println("No Changes")
}

println("")
println("------------------------------------------------------")
println("")
if (errors.size() > 0) {
  println(errors.size() + " errors:")
  println("")
  errors.each({ change ->
    println(change)
    println("")
  })
} else {
  println("No errors")
}

println("")
println("------------------------------------------------------")
println("")
if (warning.size() > 0) {
  println(warning.size() + " warnings:")
  warning.each({ warningMessage ->
    println(warningMessage)
  })
} else {
  println("No warnings")
}

println("")
println("------------------------------------------------------")
println("")
if (info.size() > 0) {
  println(info.size() + " info messages:")
  info.each({ infoMessage ->
    println(infoMessage)
  })
} else {
  println("No info messages")
}
