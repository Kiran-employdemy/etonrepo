package com.eaton.platform.integration.auth.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EatonSsoToken implements Comparable<EatonSsoToken> {
	private static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
	private static final Logger LOG = LoggerFactory.getLogger(EatonSsoToken.class);
	public static final String MSG_EXCEPTION_WHILE_DECRYPTING = "Exception while decrypting token [%s]";
	static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String MSG_UNRECOGNIZED_TOKEN_ATRIBUTE = "Unrecognized token atribute [%s]";
	public static final String MSG_TOKEN_NULL_OR_EMPTY = "Token text cannot be null or empty";

	DateFormat dfUtc = new SimpleDateFormat(ISO8601_FORMAT);
	Date sessionStartTime;

	String dn;
	String uid;
	String eatonPersonType;
	String givenName;
	String sn;
	String mail;
	String domain;
	String token;
	Cipher cipher;

	private static String keyString = deobfuscate(
			new byte[] { 49, 50, 54, 121, 115, 104, 101, 105, 116, 104, 114, 106, 75, 95, 58, 100 });

	static SecretKeySpec key = new SecretKeySpec(keyString.getBytes(), "AES");
	static final String CHAR_SET = "UTF-8";
	static final TimeZone TIMEZONE_FOR_TOKEN = TimeZone.getTimeZone("UTC");
	static final String TOKEN_KEY_VALUE_SEPARATOR = ":=";
	static final String TOKEN_ATTRIBUTE_SEPARATOR = "\t";
	static final SanitizerMap TOKEN_SANITIZEMENTS = new SanitizerMap();
	static final String TOKEN_TEMPLATE = "SESSION_STARTTIME:=%s\tUSER_DN:=%s\tUSER_ID:=%s\tUSER_TYPE:=%s\tUSER_FIRSTNAME:=%s\tUSER_LASTNAME:=%s\tUSER_EMAIL:=%s\tUSER_DOMAIN:=%s";
	static final String DN_FROM_UID_TEMPLATE = "uid=%s";
	public static final String PARAM_NAME_DN = "DN";
	public static final String PARAM_NAME_UID = "UI";
	public static final String PARAM_NAME_EATONPERSONTYPE = "eatonPersonType";
	public static final String PARAM_NAME_GIVENNAME = "givenName";
	public static final String PARAM_NAME_SN = "SN";
	public static final String PARAM_NAME_MAIL = "mail";

	static {
		TOKEN_SANITIZEMENTS.put("\t", "TAS");
		TOKEN_SANITIZEMENTS.put(":=", "TKVS");
	}

	public static EatonSsoToken createTokenWithUid(String uid, String eatonPersonType, String givenName, String sn,
			String mail) throws Exception {
		if (uid == null || uid.isEmpty()) {
			throw new MissingParameterRuntimeException("UI");
		}
		return new EatonSsoToken(String.format(DN_FROM_UID_TEMPLATE, uid.trim()), eatonPersonType, givenName,
				sn, mail);
	}

	private EatonSsoToken(String dn, String eatonPersonType, String givenName, String sn, String mail)
			throws Exception {
		this(dn, eatonPersonType, givenName, sn, mail, null);
	}

	private EatonSsoToken(String dn, String eatonPersonType, String givenName, String sn, String mail, Date sessionDate)
			throws Exception {
		validateParameters(dn, eatonPersonType, givenName, sn);
		this.sessionStartTime = (sessionDate != null) ? sessionDate : new Date();
		this.dn = TOKEN_SANITIZEMENTS.sanitizeTokenText(dn);
		this.uid = TOKEN_SANITIZEMENTS.sanitizeTokenText(extractUidFromDn(this.dn));
		this.eatonPersonType = TOKEN_SANITIZEMENTS.sanitizeTokenText(eatonPersonType);
		this.givenName = TOKEN_SANITIZEMENTS.sanitizeTokenText(givenName);
		this.sn = TOKEN_SANITIZEMENTS.sanitizeTokenText(sn);
		this.mail = TOKEN_SANITIZEMENTS.sanitizeTokenText((mail == null || mail.trim().isEmpty()) ? "null" : mail);
		this.domain = TOKEN_SANITIZEMENTS.sanitizeTokenText(computeDomain());
		this.token = encryptAndEncode(getUnencryptedToken());
	}

	private String computeDomain() {
		if (this.mail != null && this.mail.indexOf('@') > -1)
			return this.mail.split("@")[1].trim();
		if (this.dn.indexOf(',') > -1)
			return this.dn.substring(this.dn.indexOf(',') + 1);
		return null;
	}

	private void validateParameters(String dn, String eatonPersonType, String givenName, String sn) {
		if (dn == null || dn.isEmpty())
			throw new MissingParameterRuntimeException(PARAM_NAME_DN);
		if (eatonPersonType == null || eatonPersonType.isEmpty())
			throw new MissingParameterRuntimeException(PARAM_NAME_EATONPERSONTYPE);
		if (givenName == null || givenName.isEmpty())
			throw new MissingParameterRuntimeException(PARAM_NAME_GIVENNAME);
		if (sn == null || sn.isEmpty())
			throw new MissingParameterRuntimeException(PARAM_NAME_SN);
	}

	static String extractUidFromDn(String dn) {
		if (dn != null && !dn.trim().isEmpty()) {
			String[] dnParts = dn.split(",");
			String[] arrayOfString1 = dnParts;
			int i = arrayOfString1.length;
			byte b = 0;
			if (b < i) {
				String dnPart = arrayOfString1[b];
				String[] keyVal = dnPart.trim().split("=");
				if (keyVal.length < 2) {
					if (LOG.isWarnEnabled()) {
						LOG.warn(String.format("Did not find properly formed dn part [%s], returning null",
								dnPart.trim()));
					}
					return null;
				}
				if (!"uid".equalsIgnoreCase(keyVal[0].trim()) && !"cn".equalsIgnoreCase(keyVal[0].trim())
						&& LOG.isDebugEnabled()) {
					LOG.debug(String.format("Found non-standard attribute [%s] (expecting \"uid\" or \"cn\")",
							keyVal[0].trim()));
				}
				return keyVal[1].trim();
			}
		}
		return null;
	}

	public String getUnencryptedToken() {
		return String.format(TOKEN_TEMPLATE, dateAsIso8601(this.sessionStartTime), this.dn, this.uid,
				this.eatonPersonType, this.givenName, this.sn, this.mail, this.domain);
	}

	public String getDn() {
		return TOKEN_SANITIZEMENTS.unsanitizeTokenText(this.dn);
	}

	public String getUid() {
		return TOKEN_SANITIZEMENTS.unsanitizeTokenText(this.uid);
	}

	public String getGivenName() {
		return TOKEN_SANITIZEMENTS.unsanitizeTokenText(this.givenName);
	}

	public String getSn() {
		return TOKEN_SANITIZEMENTS.unsanitizeTokenText(this.sn);
	}

	public String getMail() {
		return TOKEN_SANITIZEMENTS.unsanitizeTokenText(this.mail);
	}

	public String getDomain() {
		return TOKEN_SANITIZEMENTS.unsanitizeTokenText(this.domain);
	}

	public String getType() {
		return TOKEN_SANITIZEMENTS.unsanitizeTokenText(this.eatonPersonType);
	}

	public Date getSessionStartTime() {
		return this.sessionStartTime;
	}

	private String dateAsIso8601(Date date) {
		this.dfUtc.setTimeZone(TIMEZONE_FOR_TOKEN);
		return this.dfUtc.format(date);
	}

	public String getToken() {
		return this.token;
	}

	public String toString() {
		return getToken();
	}

	static String deobfuscate(byte[] bytes) {
		String result = null;
		try {
			byte[] b3 = new byte[bytes.length];
			for (int i = 1; i < bytes.length; i++)
				b3[i - 1] = bytes[i];
			b3[b3.length - 1] = bytes[0];
			result = new String(b3, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	private static String encryptAndEncode(String str) throws Exception {
		String b64Encoded = encrypt(str);
		return URLEncoder.encode(b64Encoded, CHAR_SET);
	}

	private static String encrypt(String str) throws Exception {
		AlgorithmParameterSpec paramSpec = new IvParameterSpec(keyString.getBytes());
		Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
		cipher.init(1, key, paramSpec);
		byte[] encrypted = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
		return new String(Base64.encodeBase64(encrypted), StandardCharsets.UTF_8);
	}

	public static boolean containsIllegals(String toExamine) {
		Pattern pattern = Pattern.compile("[~#@*+/{}<>\\[\\]|\"\\_^]");
		Matcher matcher = pattern.matcher(toExamine);
		return matcher.find();
	}

	public boolean equals(EatonSsoToken o) {
		if (getToken() == null || o == null) {
			LOG.trace("Either this object or compared object is null");
			return false;
		}
		if (this == o || getToken().equals(o.getToken())) {
			LOG.trace("Either this object == compared object or they have the same token String");
			return true;
		}
		if (getDn() != null && o.getDn() != null && getMail() != null && o.getMail() != null && getType() != null
				&& o.getType() != null) {
			return (getDn().equals(o.getDn()) && getMail().equals(o.getMail()) && getType().equals(o.getType()));
		}
		LOG.trace("DN, mail, or type is null for this object or compared object");
		return false;
	}

	public int compareTo(EatonSsoToken o) {
		return (o != null && getUnencryptedToken().equals(o.getUnencryptedToken())) ? 0 : 1;
	}

	public boolean equals(Object obj) {
		return (obj instanceof EatonSsoToken && compareTo((EatonSsoToken) obj) == 0);
	}

	static class SanitizerMap extends LinkedHashMap<String, String> {
		private static final long serialVersionUID = 1L;

		private static final String TEMPLATE_KEY_PREFIX = "{~";

		private static final String TEMPLATE_KEY_SUFFIX = "~}";

		private static final String TEMPLATE_KEY = "%s%s%s";

		private LinkedHashMap<String, String> unsanitizer = new LinkedHashMap<>();

		public String put(String key, String value) {
			this.unsanitizer.put(formatReplacement(value), key);
			return super.put(key, formatReplacement(value));
		}

		String formatReplacement(String value) {
			return String.format(TEMPLATE_KEY, TEMPLATE_KEY_PREFIX, value, TEMPLATE_KEY_SUFFIX).toLowerCase();
		}

		String sanitizeTokenText(String txt) {
			if (txt != null)
				return replaceTextFromMap(txt.toLowerCase().trim(), this);
			return null;
		}

		String unsanitizeTokenText(String txt) {
			if (txt != null)
				return replaceTextFromMap(txt.trim(), this.unsanitizer);
			return null;
		}

		String replaceTextFromMap(String txt, Map<String, String> replacements) {
			if (txt == null)
				return null;
			StringBuilder newText = new StringBuilder(txt);
			for (String toReplace : replacements.keySet()) {
				String replacement = replacements.get(toReplace);
				while (newText.indexOf(toReplace) > -1)
					newText.replace(newText.indexOf(toReplace), newText.indexOf(toReplace) + toReplace.length(),
							replacement);
			}
			return newText.toString();
		}
	}
}