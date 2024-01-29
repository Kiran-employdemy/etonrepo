import java.nio.file.Paths
import groovy.transform.Field
import javax.jcr.ItemExistsException
import javax.jcr.PathNotFoundException
import javax.jcr.PropertyType

/* ------------------------------------------------------------------- */
/* ---------------------- Script Configurations ---------------------- */
/* ------------------------------------------------------------------- */
@Field final DRY_RUN = true
@Field final MICROSITE_NAME = "demo-microsite"
@Field final MICROSITE_TITLE = "Demo Microsite"
@Field final FEATURES = [
  site: [
    enabled: true,
    source: "/content/reference-microsite",
  ],
  blueprint: [
    enabled: true,
    source: "/apps/msm/eaton-microsite-en-us"
  ],
  products: [
    enabled: true,
    source: "/var/commerce/products/microsites/reference-microsite"
  ],
  dam: [
    enabled: true
  ],
  tags: [
    enabled: true
  ],
  brandSelector: [
    enabled: false,
    source: "/etc/cloudservices/brandcolorselection/microsite-brand-selector"
  ],
  eloqua: [
    enabled: false,
    source: "/etc/cloudservices/eloquaconfig/default-microsite-eloqua-configuration"
  ],
  chat: [
    enabled: false,
    source: "/etc/cloudservices/chatconfig/default-microsite-chat-configuration"
  ],
  social: [
    enabled: false,
    source: "/etc/cloudservices/socialshare/default-microsite-social-share"
  ],
  endeca: [
    enabled: false,
    source: "/etc/cloudservices/endeca-configurations/default-microsite-endeca-config"
  ],
  dtm: [
    enabled: false,
    source: "/etc/cloudservices/dynamictagmanagement/default-microsite-aem-dtm-connection"
  ]
]
/* ------------------------------------------------------------------- */
/* ------------------------ Script Overview -------------------------- */
/* -------------------------------------------------------------------
Based upon the provided configurations this script will create the content listed
below. The created content will be based upon the given microsite name and title,
the enabled features, and the provided sources,

# Site
  # The language masters and the country sites that exist in the source site will be copied
# MSM Blueprints
# PIM Folders
# Cloud Configurations
  # Brand selector
  # Eloqua
  # Chat
  # Social
  # DTM
   ------------------------------------------------------------------- */
/* ------------------------- Computed Values ------------------------- */
/* ------------------------------------------------------------------- */
@Field final actions = []

@Field final SITE_FEATURE = "Site"
@Field final BLUEPRINT_FEATURE = "Blueprint"
@Field final PRODUCTS_FEATURE = "Products"
@Field final DAM_FEATURE = "Dam"
@Field final TAGS_FEATURE = "Tags"
@Field final CLOUD_CONFIG_FEATURE = "Cloud config"
@Field final COPY_EXCEPTION_MESSAGE = "Copy Exception"
@Field final UPDATE_EXCEPTION_MESSAGE = "Update Exception"
@Field final CREATE_EXCEPTION_MESSAGE = "Create Exception"
@Field final UPDATE_MESSAGE = DRY_RUN ? "Update" : "Updated"
@Field final COPY_MESSAGE = DRY_RUN ? "Copy" : "Copied"
@Field final CREATE_MESSAGE = DRY_RUN ? "Create" : "Created"
@Field final LOCALIZATIONS = [ : ]

getPage(FEATURES.site.source).listChildren().each { countryPage ->
  if (countryPage.name != "language-masters") {
    LOCALIZATIONS[countryPage.name] = []
    countryPage.each { languagePage ->
      LOCALIZATIONS[countryPage.name].add(languagePage.name)
    }
  }
}

// Warning: This collect method will not work if the value has the @Field annotation
final LANGUAGES = LOCALIZATIONS.collect([], { country, languages -> languages }).flatten().unique()

@Field final SITE_NAME_DEPTH = 2
@Field final SOURCE_SITE_NAME = FEATURES.site.source.count("/") >= SITE_NAME_DEPTH
  ? FEATURES.site.source.split("/")[SITE_NAME_DEPTH] : null

@Field final PRODUCTS_FOLDER_NAME_DEPTH = 5
@Field final SOURCE_PRODUCTS_FOLDER_NAME = FEATURES.products.source.count("/") >= PRODUCTS_FOLDER_NAME_DEPTH
  ? FEATURES.products.source.split("/")[PRODUCTS_FOLDER_NAME_DEPTH] : null
@Field final ROOT = "/"

// I tried importing these values but those imports specifically do not work in the groovy console.
@Field final PROPERTIES = [
  cqMaster: "cq:master",
  sitePath: "sitePath",
  jcrTitle: "jcr:title",
  cqCloudserviceconfigs: "cq:cloudserviceconfigs"
]

@Field final PROPERTY_VALUES = [
  cqTag: "cq:Tag",
  slingFolder: "sling:Folder",
  ntUnstructured: "nt:unstructured"
]

@Field final SEGMENTS = [
  apps: "apps",
  content: "content",
  languageMasters: "language-masters",
  var: "var",
  etc: "etc",
  blueprints: "blueprints", // The Eaton, ECJV, and Microsite blueprints have not been moved from /etc/blueprints to /apps/msm.
  commerce: "commerce",
  products: "products",
  dam: "dam",
  tags: "tags",
  microsites: "microsites",
  cloudservices: "cloudservices",
  brandcolorselection: "brandcolorselection",
  dynamictagmanagement: "dynamictagmanagement",
  eloquaconfig: "eloquaconfig",
  chatconfig: "chatconfig",
  socialshare: "socialshare",
  endecaconfig: "endeca-configurations",
  jcrContent: "jcr:content",
  cqLiveSyncConfig: "cq:LiveSyncConfig"
]

@Field final PATHS = [
  site: join(ROOT, SEGMENTS.content, MICROSITE_NAME),
  languageMasters: join(ROOT, SEGMENTS.content, MICROSITE_NAME, SEGMENTS.languageMasters),
  blueprints: join(ROOT, SEGMENTS.etc, SEGMENTS.blueprints),
  pimParentFolder: join(ROOT, SEGMENTS.var, SEGMENTS.commerce, SEGMENTS.products, SEGMENTS.microsites, MICROSITE_NAME),
  damFolder: join(ROOT, SEGMENTS.content, SEGMENTS.dam, MICROSITE_NAME),
  tag: join(ROOT, SEGMENTS.etc, SEGMENTS.tags, MICROSITE_NAME),
  brandcolorselection: join(ROOT, SEGMENTS.etc, SEGMENTS.cloudservices, SEGMENTS.brandcolorselection),
  dynamictagmanagement: join(ROOT, SEGMENTS.etc, SEGMENTS.cloudservices, SEGMENTS.dynamictagmanagement),
  eloquaconfig: join(ROOT, SEGMENTS.etc, SEGMENTS.cloudservices, SEGMENTS.eloquaconfig),
  chatconfig: join(ROOT, SEGMENTS.etc, SEGMENTS.cloudservices, SEGMENTS.chatconfig),
  endecaconfig: join(ROOT, SEGMENTS.etc, SEGMENTS.cloudservices, SEGMENTS.endecaconfig),
  socialshare: join(ROOT, SEGMENTS.etc, SEGMENTS.cloudservices, SEGMENTS.socialshare)
]

@Field final CLOUD_CONFIGS = [
  brandcolorselection: join(PATHS.brandcolorselection, MICROSITE_NAME + "-brand-selector"),
  dynamictagmanagement: join(PATHS.dynamictagmanagement, MICROSITE_NAME + "-dtm-config"),
  eloquaconfig: join(PATHS.eloquaconfig, MICROSITE_NAME + "-eloqua-config"),
  chatconfig: join(PATHS.chatconfig, MICROSITE_NAME + "-chat-config"),
  endecaconfig: join(PATHS.endecaconfig, MICROSITE_NAME + "-endeca-config"),
  socialshare: join(PATHS.socialshare, MICROSITE_NAME + "-social-share-config")
]

@Field final CLOUD_CONFIG_TITLES = [
  brandcolorselection: "Brand Selector",
  dynamictagmanagement: "AEM DTM Connection",
  eloquaconfig: "Eloqua Configuration",
  chatconfig: "Chat Configuration",
  endecaconfig: "Endeca Configuration",
  socialshare: "Social Share"
]

PATHS.blueprints = LANGUAGES.collect { language ->
  return [
    source: FEATURES.blueprint.source,
    target: join(PATHS.blueprints, MICROSITE_NAME + "-" + language),
    sitePath: join(PATHS.languageMasters, language)
  ]
}
/* ------------------------------------------------------------------- */
/* ------------------------ Utility Methods -------------------------- */
/* ------------------------------------------------------------------- */
def join(...pathSegments) {
  Paths.get(*pathSegments).toString()
}

def copyNode(sourceNodePath, destinationNodePath, feature) {
  try {
    if (! DRY_RUN) {
      // WARNING: The workspace copy() method performs the copy automatically without needing the save() call.
      // This means we cannot call the copy method unless we actually want to save the changes.
      session.workspace.copy(sourceNodePath, destinationNodePath)
    }
    actions.add([feature, destinationNodePath, COPY_MESSAGE, sourceNodePath])
  } catch (ItemExistsException e) {
    actions.add([feature, destinationNodePath, COPY_EXCEPTION_MESSAGE, "Item already exists"])
  } catch (PathNotFoundException e) {
    actions.add([feature, destinationNodePath, COPY_EXCEPTION_MESSAGE, "Intermediary nodes do not exist for either source or target path. Source: " + sourceNodePath])
  }
}

def ifNodeExists(path, feature, existsCallback) {
  if (! DRY_RUN) {
    try {
      existsCallback(getNode(path))
    } catch (PathNotFoundException e) {
      actions.add([feature, path, UPDATE_EXCEPTION_MESSAGE, "Intermediary nodes do not exist"])
    }
  }
}

def updateLiveSync(node) {
  def master = node.getProperty(PROPERTIES.cqMaster).getString()
  if (SOURCE_SITE_NAME) {
    def newMaster = master.replace(SOURCE_SITE_NAME, MICROSITE_NAME)
    node.setProperty(PROPERTIES.cqMaster, newMaster)
    actions.add([SITE_FEATURE, node.path, UPDATE_MESSAGE, PROPERTIES.cqMaster + " = " + newMaster])
  } else {
    actions.add([SITE_FEATURE, node.path, UPDATE_EXCEPTION_MESSAGE, SITE_FEATURE + " feature turned on but an invalid source site was provided"])
  }
}

def updateCloudService(node) {
  def cqCloudserviceconfigs = node.getProperty(PROPERTIES.cqCloudserviceconfigs)
  if (cqCloudserviceconfigs.isMultiple()) {
    def oldConfigs = cqCloudserviceconfigs.values
    def newConfigs = []
    def updateMade = false

    oldConfigs.each { oldConfig ->
      if (FEATURES.brandSelector.enabled && oldConfig.string == FEATURES.brandSelector.source) {
        newConfigs << CLOUD_CONFIGS.brandcolorselection
        updateMade = true
      } else if (FEATURES.dtm.enabled && oldConfig.string == FEATURES.dtm.source) {
        newConfigs << CLOUD_CONFIGS.dynamictagmanagement
        updateMade = true
      } else if (FEATURES.eloqua.enabled && oldConfig.string == FEATURES.eloqua.source) {
        newConfigs << CLOUD_CONFIGS.eloquaconfig
        updateMade = true
      } else if (FEATURES.chat.enabled && oldConfig.string == FEATURES.chat.source) {
        newConfigs << CLOUD_CONFIGS.chatconfig
        updateMade = true
      } else if (FEATURES.social.enabled && oldConfig.string == FEATURES.social.source) {
        newConfigs << CLOUD_CONFIGS.socialshare
        updateMade = true
      } else if (oldConfig.string != "[]") {
        // The reason for this comaprison to "[]" is so that we don't turn an empty multivalued
        // property into a multivalued property with an empty array inside.
        newConfigs << oldConfig.string
        updateMade = true
      }
    }

    if (updateMade) {
      node.setProperty(PROPERTIES.cqCloudserviceconfigs, newConfigs as String[])
      actions.add([CLOUD_CONFIG_FEATURE, node.path, UPDATE_MESSAGE, PROPERTIES.cqCloudserviceconfigs + " = " + newConfigs])
    }
  }
}

def updateSite(siteRootPath) {
  def jcrContentPath = join(siteRootPath, SEGMENTS.jcrContent)
  actions.add([SITE_FEATURE, jcrContentPath, UPDATE_MESSAGE, PROPERTIES.jcrTitle + " = " + MICROSITE_TITLE])

  if (DRY_RUN) {
    actions.add([SITE_FEATURE, "Recursively find " + SEGMENTS.cqLiveSyncConfig + " nodes from " + siteRootPath, UPDATE_MESSAGE, "The script will update " + PROPERTIES.cqMaster + " based upon source node."])
    actions.add([CLOUD_CONFIG_FEATURE, "Recursively find " + SEGMENTS.jcrContent + " nodes from " + siteRootPath, UPDATE_MESSAGE, "The script will update " + PROPERTIES.cqCloudserviceconfigs + " based upon source node."])
  }

  ifNodeExists(siteRootPath, SITE_FEATURE, { siteRootNode ->
    ifNodeExists(jcrContentPath, SITE_FEATURE, { jcrContentNode ->
      jcrContentNode.setProperty(PROPERTIES.jcrTitle, MICROSITE_TITLE)
    })

    siteRootNode.recurse { node ->
      if (node.name == SEGMENTS.cqLiveSyncConfig) {
        updateLiveSync(node);
      } else if (node.name == SEGMENTS.jcrContent) {
        if (node.hasProperty(PROPERTIES.cqCloudserviceconfigs)) {
          updateCloudService(node)
        }

        updatePaths(node, SITE_FEATURE)
      }
    }
  })
}

def updatePaths(node, feature) {
  node.properties.each { property ->
    if (! property.isMultiple() && property.value && property.value.type == PropertyType.STRING) {
      if (property.string =~ /^$FEATURES.site.source/) {
        if (SOURCE_SITE_NAME) {
          def newPath = property.string.replaceFirst(SOURCE_SITE_NAME, MICROSITE_NAME)
          node.setProperty(property.name, newPath)
          actions.add([feature, node.path, UPDATE_MESSAGE, property.name + " = " + newPath])
        } else {
          actions.add([feature, node.path, UPDATE_EXCEPTION_MESSAGE, feature + " feature turned on but an invalid source site was provided and so content paths could not be updaed."])
        }
      }

      if (property.string =~ /^$FEATURES.products.source/) {
        if (SOURCE_PRODUCTS_FOLDER_NAME) {
          def newPath = property.string.replaceFirst(SOURCE_PRODUCTS_FOLDER_NAME, MICROSITE_NAME)
          node.setProperty(property.name, newPath)
          actions.add([feature, node.path, UPDATE_MESSAGE, property.name + " = " + newPath])
        } else {
          actions.add([feature, node.path, UPDATE_EXCEPTION_MESSAGE, feature + " feature turned on but an invalid source products folder was provided and so PIM paths could not be updated."])
        }
      }
    }
  }
}

def updateCloudConfig(cloudServiceRootPath, cloudServiceTitle) {
  def jcrCloudContentPath = join(cloudServiceRootPath, SEGMENTS.jcrContent)
  def newTitle = MICROSITE_TITLE + " " + cloudServiceTitle
  actions.add([CLOUD_CONFIG_FEATURE, cloudServiceRootPath, UPDATE_MESSAGE, PROPERTIES.jcrTitle + " = " + newTitle])

  ifNodeExists(jcrCloudContentPath, CLOUD_CONFIG_FEATURE, { jcrContentNode ->
    jcrContentNode.setProperty(PROPERTIES.jcrTitle, newTitle)
    updatePaths(jcrContentNode, CLOUD_CONFIG_FEATURE)
  })
}

def updateBlueprint(blueprint) {
  def jcrContentPath = join(blueprint.target, SEGMENTS.jcrContent)
  actions.add([BLUEPRINT_FEATURE, jcrContentPath, UPDATE_MESSAGE, PROPERTIES.jcrTitle + " = " + MICROSITE_TITLE])
  actions.add([BLUEPRINT_FEATURE, jcrContentPath, UPDATE_MESSAGE, PROPERTIES.sitePath + " = " + blueprint.sitePath])

  ifNodeExists(jcrContentPath, BLUEPRINT_FEATURE, { jcrContentNode ->
    jcrContentNode.setProperty(PROPERTIES.jcrTitle, MICROSITE_TITLE)
    jcrContentNode.setProperty(PROPERTIES.sitePath, blueprint.sitePath)
  })
}

def updatePimFolder(pimFolderPath) {
  actions.add([PRODUCTS_FEATURE, pimFolderPath, UPDATE_MESSAGE, PROPERTIES.jcrTitle + " = " + MICROSITE_TITLE])

  ifNodeExists(pimFolderPath, PRODUCTS_FEATURE, { pimFolderNode ->
    pimFolderNode.setProperty(PROPERTIES.jcrTitle, MICROSITE_TITLE)

    pimFolderNode.recurse { node ->
      updatePaths(node, PRODUCTS_FEATURE)
    }
  })
}

def parentPath(path) {
  join("/", path.split("/").dropRight(1))
}

def segmentName(path) {
  path.split("/").last()
}

def createDamFolder(path, feature) {
  create(path, PROPERTY_VALUES.slingFolder, feature)
  create(join(path, SEGMENTS.jcrContent), PROPERTY_VALUES.ntUnstructured, feature)
}

def createTag(path, feature) {
  create(path, PROPERTY_VALUES.cqTag, feature)
}

def create(path, type, feature) {
  try {
    def parentNode = getNode(parentPath(path));
    def newNode = parentNode.addNode(segmentName(path), type)
    actions.add([feature, newNode.path, CREATE_MESSAGE, type]);
  } catch (ItemExistsException e) {
    actions.add([feature, path, CREATE_EXCEPTION_MESSAGE, "Item already exists"]);
  } catch (PathNotFoundException e) {
    actions.add([feature, path, CREATE_EXCEPTION_MESSAGE, "Intermediary nodes do not exist"]);
  }
}

/* ------------------------------------------------------------------- */
/* -------------------- Create and Copy Content ---------------------- */
/* ------------------------------------------------------------------- */

if (FEATURES.site.enabled) {
  copyNode(FEATURES.site.source, PATHS.site, SITE_FEATURE)
  updateSite(PATHS.site)
}

if (FEATURES.blueprint.enabled) {
  PATHS.blueprints.each { blueprint ->
    copyNode(blueprint.source, blueprint.target, BLUEPRINT_FEATURE)
    updateBlueprint(blueprint)
  }
}

if (FEATURES.products.enabled) {
  copyNode(FEATURES.products.source, PATHS.pimParentFolder, PRODUCTS_FEATURE)
  updatePimFolder(PATHS.pimParentFolder)
}

if (FEATURES.dam.enabled) {
  createDamFolder(PATHS.damFolder, DAM_FEATURE)
}

if (FEATURES.tags.enabled) {
  createTag(PATHS.tag, TAGS_FEATURE)
}

if (FEATURES.brandSelector.enabled) {
  copyNode(FEATURES.brandSelector.source, CLOUD_CONFIGS.brandcolorselection, CLOUD_CONFIG_FEATURE)
  updateCloudConfig(CLOUD_CONFIGS.brandcolorselection, CLOUD_CONFIG_TITLES.brandcolorselection)
}

if (FEATURES.dtm.enabled) {
  copyNode(FEATURES.dtm.source, CLOUD_CONFIGS.dynamictagmanagement, CLOUD_CONFIG_FEATURE)
  updateCloudConfig(CLOUD_CONFIGS.dynamictagmanagement, CLOUD_CONFIG_TITLES.dynamictagmanagement)
}

if (FEATURES.eloqua.enabled) {
  copyNode(FEATURES.eloqua.source, CLOUD_CONFIGS.eloquaconfig, CLOUD_CONFIG_FEATURE)
  updateCloudConfig(CLOUD_CONFIGS.eloquaconfig, CLOUD_CONFIG_TITLES.eloquaconfig)
}

if (FEATURES.chat.enabled) {
  copyNode(FEATURES.chat.source, CLOUD_CONFIGS.chatconfig, CLOUD_CONFIG_FEATURE)
  updateCloudConfig(CLOUD_CONFIGS.chatconfig, CLOUD_CONFIG_TITLES.chatconfig)
}

if (FEATURES.social.enabled) {
  copyNode(FEATURES.social.source, CLOUD_CONFIGS.socialshare, CLOUD_CONFIG_FEATURE)
  updateCloudConfig(CLOUD_CONFIGS.socialshare, CLOUD_CONFIG_TITLES.socialshare)
}

if (FEATURES.endeca.enabled) {
  copyNode(FEATURES.endeca.source, CLOUD_CONFIGS.endecaconfig, CLOUD_CONFIG_FEATURE)
  updateCloudConfig(CLOUD_CONFIGS.endecaconfig, CLOUD_CONFIG_TITLES.endecaconfig)
}

/* ------------------------------------------------------------------- */
/* ----------------------- Report The Changes ------------------------ */
/* ------------------------------------------------------------------- */

if (DRY_RUN) {
  println "This was a dry run. All of the reported content was not actually created."
  println ""
  println "Notice: Exception reporting will not be available in dry run mode because the workspace copy method automatically saves changes."
  println ""
  println "Notice: Recursive updates cannot be reported until the real script is run as it depends on the workspace copy method to actually be called."
  println "        Because of this the dry run will report on recursive updates and the real run will report the actual updates made for each node that was recursively found."
} else {
  println "This was a real run of the script. All of the reported content was actually created."
  save()
}

table {
  columns("Feature", "Path", "Type", "Source Path / Info")
  rows(actions)
}
