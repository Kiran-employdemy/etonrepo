package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.impl.EatonNotifyServiceFactoryConfig;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ParticipantStepChooser.class, immediate = true, property = {
        AEMConstants.SERVICE_VENDOR_EATON,
        AEMConstants.SERVICE_DESCRIPTION + "Eaton Implementation of dynamic participant chooser.",
        "chooser.label=Eaton Dynamic Participant Choose"
})
public class EatonParticipantStepImpl implements ParticipantStepChooser
{

    private static final Logger LOGGER = LoggerFactory.getLogger(EatonParticipantStepImpl.class);

    public String getParticipant(final WorkItem workItem,final WorkflowSession wfSession, final MetaDataMap metaDataMap)
            throws WorkflowException
    {
        String participant = StringUtils.EMPTY;
        try {
            final MetaDataMap metaDataMapDynamic = workItem.getWorkflowData().getMetaDataMap();

            LOGGER.info("################ Inside the EatonParticipantStepImpl GetParticipant ##########################");

            if (metaDataMapDynamic.containsKey(EatonNotifyServiceFactoryConfig.ROLLOUT_NOTIFICATION_GROUP)) {
                participant = metaDataMapDynamic.get(EatonNotifyServiceFactoryConfig.ROLLOUT_NOTIFICATION_GROUP).toString();
            }
            LOGGER.info("####### Participant : " + participant + " ##############");
            return participant;
        }catch (Exception e){
            throw new WorkflowException("Unable to process further workflow from Exception Process::: " + e.getMessage());
        }
    }
}
