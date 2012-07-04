package com.jijesoft.core.plugin.stateless;




public class StatelessConfig {

	private StatelessConfig() {
	}

	/** session 有效期 单位：秒 */
	public static int SESSION_MAXAGE = 20 * 60;
	
	public static String COOKIE_NAME = "bohid";
	public static String COOKIE_DOMAIN = null;
	public static int COOKIE_MAXAGE = 24 * 60 * 60;
	public static String COOKIE_PATH = "/";
	
	/** cookie 是否使用HMAC-SHA1进行消息签名 */
	public static boolean COOKIE_SIGN = true;
	/** cookie 使用HMAC-SHA1进行消息签名 的key*/
	public static String COOKIE_SIGN_KEY = "jije cookie sign key";

	/** hex编码过的aes密钥  */
	public static String HEXED_COOKIE_AES_KEY = "20ab174c59f9180e0094b1bc52d56814";
	
	/** 单个cookie存储的最大length */
	public static final int COOKIE_MAX_SIZE = 3000;
	
	/** 当前采用的session backend */
	public static final ISessionBackend SESSION_BACKEND = new MemcacheBackend();
	
	public static final boolean isBufferingRequired = true;
}
