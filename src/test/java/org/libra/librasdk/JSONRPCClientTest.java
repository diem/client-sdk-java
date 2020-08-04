package org.libra.librasdk;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class JSONRPCClientTest{

        @InjectMocks
        private JSONRPCClient jsonrpcClient = new JSONRPCClient(LibraNetwork.TESTNET.url);

        @Mock
        private JSONRPC2Session jsonrpc2Session;

        @Mock
        private JSONRPC2Response jsonrpc2Response;

        @Mock
        private JSONRPC2Error jsonrpc2Error;

        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);
        }

        @Test
        public void call_noSuccess() throws JSONRPC2SessionException {
            when(jsonrpc2Session.send((JSONRPC2Request) any())).thenReturn(jsonrpc2Response);
            when(jsonrpc2Response.getError()).thenReturn(jsonrpc2Error);
            when(jsonrpc2Error.getMessage()).thenReturn("");

            ArrayList<Object> params = new ArrayList<>() {};
            String response = jsonrpcClient.call(Method.get_account, params);
            assertNull(response);
        }

        @Test
        public void call_success() throws JSONRPC2SessionException {
            when(jsonrpc2Session.send((JSONRPC2Request) any())).thenReturn(jsonrpc2Response);
            when(jsonrpc2Response.indicatesSuccess()).thenReturn(true);
            when(jsonrpc2Response.getResult()).thenReturn(new Object());

            ArrayList<Object> params = new ArrayList<>() {};
            String response = jsonrpcClient.call(Method.get_transactions, params);
            assertNotNull(response);
        }
    }