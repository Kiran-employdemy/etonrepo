package com.eaton.platform.core.replication;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationResult;
import com.day.cq.replication.ReplicationTransaction;
import com.day.cq.replication.TransportContext;
import org.apache.http.auth.AuthenticationException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VarnishSixTransportHandlerTest {

    @Spy
    @InjectMocks
    private VarnishSixTransportHandler transportHandler;

    @Mock
    private FlushServiceConfiguration flushServiceConfiguration;

    @Mock
    private TransportContext transportContext;

    @Mock
    private ReplicationTransaction replicationTransaction;

    @Mock
    private ReplicationAction replicationAction;

    @Test
    void testDoNothingWhenContentIsSecure() throws ReplicationException, AuthenticationException, JSONException {
        when(replicationTransaction.getAction()).thenReturn(replicationAction);
        when(replicationAction.getPath()).thenReturn("/content/eaton/secure/test");
        when(flushServiceConfiguration.skipContentFlushing("/content/eaton/secure/test")).thenReturn(true);
        ReplicationResult deliver = transportHandler.deliver(transportContext, replicationTransaction);

        assertEquals(ReplicationResult.OK, deliver, "Should return OK status and do nothing");
        verify(transportHandler, never()).doActivate(transportContext, replicationTransaction);
    }

    @Test
    void testDoActivateWhenContentIsNotSecure() throws ReplicationException, IOException, AuthenticationException, JSONException {
        when(replicationTransaction.getAction()).thenReturn(replicationAction);
        when(replicationAction.getType()).thenReturn(ReplicationActionType.ACTIVATE);
        when(replicationAction.getPath()).thenReturn("/content/eaton/notSecure");
        when(flushServiceConfiguration.skipContentFlushing("/content/eaton/notSecure")).thenReturn(false);

        doReturn(ReplicationResult.OK).when(transportHandler).doActivate(transportContext, replicationTransaction);

        ReplicationResult deliver = transportHandler.deliver(transportContext, replicationTransaction);

        assertEquals(ReplicationResult.OK, deliver, "Should return OK status and call activate");
        verify(transportHandler, times(1)).doActivate(transportContext, replicationTransaction);
    }
}