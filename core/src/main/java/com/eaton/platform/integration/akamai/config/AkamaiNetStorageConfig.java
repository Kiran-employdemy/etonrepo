package com.eaton.platform.integration.akamai.config;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Akamai Authorization Configuration",description = "Provide Authorization information to Akamai Net-Storage")
public @interface AkamaiNetStorageConfig {

    @AttributeDefinition(name = "Enable Short-Live URL", description = "Check to enable a short-live download url. This is achieve by appending a unique token value at end of the download url")
    boolean isShortLiveUrl() default false;

    @AttributeDefinition(name = "ID", description = "An unique identification that define this configuration")
    String id() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Net Storage Domain", description = "Domain value for Akamai Net Storage")
    String domain() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Net Storage Account", description = "Account value for this Net Storage")
    String account() default StringUtils.EMPTY;

    @AttributeDefinition(name = "HTTP API Access key", description = "HTTP API Access key")
    String accessKey() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Net Storage action version", description = "Version for this Net Storage")
    String version() default "1";

    @AttributeDefinition(name = "Auth Sign Version", description = "Auth Sign Version")
    int signVersion() default 5;

    @AttributeDefinition(name = "CP Code", description = "Identification of storage group")
    String cpCode() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Software Delivery Download domain / Akamai Download Delivery Domain", description = "Software Delivery Download domain / Akamai Download Delivery Domain")
    String downloadDomain() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Storage Group", description = "Group identifier for this storage")
    String storageGroup() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Short-Live Token TTL", description = "Define duration of the short-live token",type = AttributeType.LONG)
    long shortLiveTokenTTL() default 300L;

    @AttributeDefinition(name = "Short-Live Token Encryption Key",description = "Used to encrypted short-live url ACL, and EXP")
    String shortLiveTokenEncryptionKey() default StringUtils.EMPTY;

}
