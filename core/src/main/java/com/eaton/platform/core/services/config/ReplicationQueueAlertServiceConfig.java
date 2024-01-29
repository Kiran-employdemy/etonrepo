package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Replication Queue Scheduler",
        description= "Sling scheduler configuration")
public @interface ReplicationQueueAlertServiceConfig {
    /**
     * This method will return the name of the Scheduler
     *
     * @return {@link String}
     */
    @AttributeDefinition(
            name = "Replication Queue Scheduler",
            description = "Replication Queue blocker Scheduler",
            type = AttributeType.STRING)
    public String schdulerName() default "Replication Queue Scheduler";

    /**
     * This method returns the environment for the scheduler
     *
     * @return {@link String}
     */
    @AttributeDefinition(
            name = "agents",
            description = "Please enter agent ID's",
            type = AttributeType.STRING)
    public String[] agentIDs() default {};

    /**
     * This method returns the environment for the scheduler
     *
     * @return {@link String}
     */
    @AttributeDefinition(
            name = "Email list",
            description = "Email list for sending the alert",
            type = AttributeType.STRING)
    public String emailDL() default "";

    /**
     * This method will check if the scheduler is concurrent or not
     *
     * @return {@link Boolean}
     */
    @AttributeDefinition(
            name = "Enabled",
            description = "True, if scheduler service is enabled",
            type = AttributeType.BOOLEAN)
    public boolean enabled() default false;

}
