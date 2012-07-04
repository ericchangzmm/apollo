package com.jijesoft.core.plugin.security;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * 支持HMAC-SHA1消息签名 及 AES对称加密的工具类.
 * 
 */
public class Cryptos {

	private static final String AES = "AES";
	private static final String HMACSHA1 = "HmacSHA1";

	private static final int DEFAULT_HMACSHA1_KEYSIZE = 160; // RFC2401
	private static final int DEFAULT_AES_KEYSIZE = 128;

	private Cryptos() {
	}

	// -- HMAC-SHA1 funciton --//

	/**
	 * 使用HMAC-SHA1进行消息签名, 返回字节数组,长度为20字节.
	 * 
	 * @param input
	 *            原始输入字符数组
	 * @param key
	 *            HMAC-SHA1密钥
	 */
	public static byte[] hmacSha1(byte[] input, byte[] key) {
		try {
			SecretKey secretKey = new SecretKeySpec(key, HMACSHA1);
			Mac mac = Mac.getInstance(HMACSHA1);
			mac.init(secretKey);
			return mac.doFinal(input);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 校验HMAC-SHA1签名是否正确.
	 * 
	 * @param expected
	 *            已存在的签名
	 * @param input
	 *            原始输入字符串
	 * @param key
	 *            密钥
	 */
	public static boolean isMacValid(byte[] expected, byte[] input, byte[] key) {
		byte[] actual = hmacSha1(input, key);
		return Arrays.equals(expected, actual);
	}

	/**
	 * 生成HMAC-SHA1密钥,返回字节数组,长度为160位(20字节). HMAC-SHA1算法对密钥无特殊要求,
	 * RFC2401建议最少长度为160位(20字节).
	 */
	public static byte[] generateHmacSha1Key() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(HMACSHA1);
			keyGenerator.init(DEFAULT_HMACSHA1_KEYSIZE);
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey.getEncoded();
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	// -- AES funciton --//

	/**
	 * 使用AES加密原始字符串.
	 * 
	 * @param input
	 *            原始输入字符数组
	 * @param key
	 *            符合AES要求的密钥
	 */
	public static byte[] aesEncrypt(byte[] input, byte[] key) {
		return aes(input, key, Cipher.ENCRYPT_MODE);
	}

	/**
	 * 使用AES解密字符串, 返回原始字符串.
	 * 
	 * @param input
	 *            原始输入字符数组
	 * @param key
	 *            符合AES要求的密钥
	 */
	public static String aesDecrypt2String(byte[] input, byte[] key) {
		byte[] decryptResult = aes(input, key, Cipher.DECRYPT_MODE);
		return new String(decryptResult);
	}
	
	public static byte[] aesDecrypt(byte[] input, byte[] key) {
		return aes(input, key, Cipher.DECRYPT_MODE);
	}

	/**
	 * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
	 * 
	 * @param input
	 *            原始字节数组
	 * @param key
	 *            符合AES要求的密钥
	 * @param mode
	 *            Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
	 */
	private static byte[] aes(byte[] input, byte[] key, int mode) {
		try {
			SecretKey secretKey = new SecretKeySpec(key, AES);
			Cipher cipher = Cipher.getInstance(AES);
			cipher.init(mode, secretKey);
			return cipher.doFinal(input);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 生成AES密钥,返回字节数组, 默认长度为128位(16字节).
	 */
	public static byte[] generateAesKey() {
		return generateAesKey(DEFAULT_AES_KEYSIZE);
	}

	/**
	 * 生成AES密钥,可选长度为128,192,256位.
	 */
	public static byte[] generateAesKey(int keysize) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
			keyGenerator.init(keysize);
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey.getEncoded();
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws IOException {

		// mac
		String input = "foo message";
		// byte[] key = "a foo key".getBytes();
		byte[] key = Cryptos.generateHmacSha1Key();

		byte[] macResult = Cryptos.hmacSha1(input.getBytes(), key);
		System.out.println("hmac-sha1 key in hex      :"
				+ Hex.encodeHexString(key));
		System.out.println("hmac-sha1 in hex result   :"
				+ Hex.encodeHexString(macResult));
		System.out.println("hmac-sha1 valid is        :"
				+ Cryptos.isMacValid(macResult, input.getBytes(), key));

		// aes
		byte[] aes_key = Cryptos.generateAesKey();

		byte[] encryptResult = Cryptos.aesEncrypt(input.getBytes(), aes_key);
		String descryptResult = Cryptos.aesDecrypt2String(encryptResult, aes_key);

		System.out.println("aes key in hex            :"
				+ Hex.encodeHexString(aes_key));
		System.out.println("aes encrypt in hex result :"
				+ Hex.encodeHexString(encryptResult));
		System.out.println("aes dncrypt in hex result :" + descryptResult);

		// 生成xxx.key
		FileOutputStream fos = new FileOutputStream("d:/cookie-sign.key");
		fos.write(key);
		fos.flush();

		// 生成xxx.key
		fos = new FileOutputStream("d:/cookie-aes.key");
		fos.write(aes_key);
		fos.flush();
		fos.close();

		File file = new File("d:/cookie-sign.key");
		byte[] mac_file_key = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(mac_file_key);
		System.out.println("mac_file_key in hex            :"
				+ Hex.encodeHexString(mac_file_key));
		System.out.println("mac_file_key == mac_key :" + (Hex.encodeHexString(key).equals(Hex.encodeHexString(mac_file_key))));
		
		file = new File("d:/cookie-aes.key");
		byte[] aes_file_key = new byte[(int) file.length()];
		fis = new FileInputStream(file);
		fis.read(aes_file_key);
		System.out.println("aes_file_key == aes_key :" + (Hex.encodeHexString(aes_key).equals(Hex.encodeHexString(aes_file_key))));
		
		fis.close();
	}
}