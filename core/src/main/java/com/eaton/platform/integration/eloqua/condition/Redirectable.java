package com.eaton.platform.integration.eloqua.condition;

public interface Redirectable {
    boolean shouldRedirect();
    String redirectUrl();
}
