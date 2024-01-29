/*******************************************************************************
 * ADOBE CONFIDENTIAL
 * __________________
 *
 * Copyright 2016 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 ******************************************************************************/
"use strict";

use([], function () {
    var rootResource = resource.getChild('root');
    var resourcePath = "";

    if (rootResource != null) {
        resourcePath = rootResource.getPath();
    } else {
        // if we don't have a "root" subnode just take the first one
        var children = resource.getChildren();
        if (children.length > 0) {
            resourcePath = children[0].getPath();
        }
    }
    return {
        cssClasses: "xf-web-container",
        resourcePath: resourcePath,
        rootResource: rootResource
    };
});