package com.eaton.platform.core.models;

import javax.inject.Inject;
import javax.servlet.http.Cookie;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import static com.eaton.platform.core.constants.CommonConstants.TRUE;
import static com.eaton.platform.core.constants.CommonConstants.FALSE;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CookieStatusModel {
	public static final String STATUS_COOKIE = "eatoncookies";

	public static final boolean DEFAULT_IS_ACCEPTED = false;
	public static final boolean DEFAULT_IS_DECLINED = ! DEFAULT_IS_ACCEPTED;

	@Inject
	private SlingHttpServletRequest request;

	/**
	 * @return True if the user has explicitly accepted cookies this will return false.
	 */
	public boolean isAccepted() {
		return hasCookieWithValue(request, STATUS_COOKIE, TRUE, false);
	}

	/**
	 * @return True if the user has explicitly declined cookies this will return true.
	 */
	public boolean isDeclined() {
		return hasCookieWithValue(request, STATUS_COOKIE, FALSE, false);
	}

	/**
	 * @param request The request that may or may not have a cookie of the given cookie name.
	 * @param cookieName The name of the cookie to look for.
	 * @param cookieValue The expected value of the cookie with the given cookie name.
	 * @param defaultValue The default value to return if no cookie with the given name is found.
	 * @return True is returned if there exists a cookie with the given name that has the given value.
	 *         False is returned if it has the given cookie but it does not match the value.
	 *         The provided default value is returned if no cookie with the given cookie name was returned.
	 */
	private static boolean hasCookieWithValue(SlingHttpServletRequest request, String cookieName, String cookieValue, boolean defaultValue) {
		boolean hasValue = defaultValue;

		if (request != null) {
			Cookie cookie = request.getCookie(cookieName);

			if (cookie != null) {
				String status = cookie.getValue();

				if (status != null) {
					hasValue = status.equals(cookieValue);
				}
			}
		}

		return hasValue;
	}
}

