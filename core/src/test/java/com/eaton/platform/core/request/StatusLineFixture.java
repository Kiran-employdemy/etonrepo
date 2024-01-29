package com.eaton.platform.core.request;

import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicStatusLine;

public class StatusLineFixture {
    private static final ProtocolVersion version = new ProtocolVersion("1.0",1,1);
    private static final String defaultResonPhrase ="Fixture For Unit Test";

    public static StatusLine getOKStatusLine(){
        return new BasicStatusLine(version, HttpStatus.SC_OK,defaultResonPhrase);
    }

    public static StatusLine getNotFoundStatusLine(){
        return new BasicStatusLine(version, HttpStatus.SC_NOT_FOUND,defaultResonPhrase);
    }

    public static StatusLine getInterServerErrorStatusLine() {
        return new BasicStatusLine(version, HttpStatus.SC_INTERNAL_SERVER_ERROR, defaultResonPhrase);
    }

}
