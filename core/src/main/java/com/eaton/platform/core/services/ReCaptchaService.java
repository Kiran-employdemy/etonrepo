package com.eaton.platform.core.services;

public interface ReCaptchaService {
	String PARAM_RECAPTCHA_CLIENT_RESPONSE = "g-recaptcha-response";
	String PARAM_RECAPTCHA_VERIFICATION_SECRET = "secret";
	String PARAM_RECAPTCHA_VERIFICATION_RESPONSE = "response";
	String PARAM_RECAPTCHA_VERIFICATION_SUCCESS = "success";

	boolean validate(final String reCaptchaResponse, final String reCaptchaSecret);
}
