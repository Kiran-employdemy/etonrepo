package com.eaton.platform.integration.sendgrid.factory;

import com.eaton.platform.integration.sendgrid.AbstractSendGridService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.List;
import java.util.Optional;

@Component(name = "Send Grid Factory Service",
        service = SendGridServiceFactory.class,
        immediate = true)
public class SendGridServiceFactory {
    @Reference(service = AbstractSendGridService.class,
            policy = ReferencePolicy.DYNAMIC,
            cardinality = ReferenceCardinality.MULTIPLE)
    private volatile List<AbstractSendGridService> sendGridServiceList;

    public Optional<AbstractSendGridService> getSendGridServiceById(String id){
        if(null == sendGridServiceList){
            return Optional.empty();
        }
        return sendGridServiceList.stream().filter(service -> service.getId().equals(id)).findFirst();
    }
}
