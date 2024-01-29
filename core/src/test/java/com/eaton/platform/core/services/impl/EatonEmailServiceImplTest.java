package com.eaton.platform.core.services.impl;

import com.adobe.acs.commons.email.EmailService;
import com.eaton.platform.core.services.config.EatonEmailServiceConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EatonEmailServiceImplTest {
    private static final String SENDER_EMAIL_ADD = "senderEmailAddress";
    private static final String SENDER_NAME = "senderName";

    public static final String FROM_ADDRESS = "noreply-EATONcatalog@eaton.com";
    public static final String PRE_ADDRESS_NAME = "Eaton.com";
    public static final String HOMER_SIMPSONS_SPRINGIELD_US = "homer.simpsons@springield.us";
    @InjectMocks
    EatonEmailServiceImpl eatonEmailService = new EatonEmailServiceImpl();
    @Mock
    EmailService emailService;
    @Mock
    EatonEmailServiceConfig eatonEmailServiceConfig;

    @Test
    void testSendEmailAllOK() {
        when(eatonEmailServiceConfig.fromAddress()).thenReturn(FROM_ADDRESS);
        when(eatonEmailServiceConfig.senderPreName()).thenReturn(PRE_ADDRESS_NAME);
        eatonEmailService.activate(eatonEmailServiceConfig);
        eatonEmailService.sendEmail(HOMER_SIMPSONS_SPRINGIELD_US, EmailParamFixtures.forDataSheet("body"), "template");
        Map<String, String> expected = EmailParamFixtures.forDataSheet("body");
        expected.put(SENDER_EMAIL_ADD, FROM_ADDRESS);
        expected.put(SENDER_NAME, PRE_ADDRESS_NAME);
        verify(emailService).sendEmail("template", expected, HOMER_SIMPSONS_SPRINGIELD_US);
    }
}