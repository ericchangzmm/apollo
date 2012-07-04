package com.jijesoft.core.plugin.utils;



import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public final class HmacUtils {

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	private HmacUtils() {
	}

	public static String calculateRFC2104HMAC(String data, String key)
			throws SignatureException {
		String result;

		if (data == null || key == null) {
			throw new IllegalArgumentException(
					"data and key must be not null. data:" + data + " key: "
							+ key == null ? "null" : "not null");
		}
		// Get an hmac_sha1 key from the raw key bytes
		byte[] keyBytes = key.getBytes();
		SecretKeySpec signingKey = new SecretKeySpec(keyBytes,
				HMAC_SHA1_ALGORITHM);

		try {
			// Get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);

			// Compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());

			// Convert raw bytes to Hex
			byte[] hexBytes = new Hex().encode(rawHmac);

			// Covert array of Hex bytes to a String
			result = new String(hexBytes, "ISO-8859-1");
		} catch (InvalidKeyException e) {
			throw new SignatureException("Failed to generate HMAC : "
					+ e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new SignatureException("Failed to generate HMAC : "
					+ e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			throw new SignatureException("Failed to generate HMAC : "
					+ e.getMessage(), e);
		}

		return result;
	}

	public static String signRFC2104HMAC(String data, String key,
			String separator) throws SignatureException {

		String hash = HmacUtils.calculateRFC2104HMAC(data, key);
		String signedData = data + separator + hash;

		return signedData;
	}

	public static String verifyRFC2104HMAC(String data, String key,
			String separator) throws SignatureException {
		int idx = data.lastIndexOf(separator);
		if (idx >= 0) {
			String unsignedValue = data.substring(0, idx);

			String hash = data.substring(idx + 1);

			if (hash.equals(calculateRFC2104HMAC(unsignedValue, key))) {
				return unsignedValue;
			}
		}
		return null;
	}
}
