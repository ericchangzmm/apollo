package com.jijesoft.core.plugin.mapper;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * packaging Jackson, the Mapper for [JSON String<->Java Object]
 * 
 * @author eric.zhang
 * 
 */
@SuppressWarnings("unchecked")
public class JsonMapper {

	private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);

	private ObjectMapper mapper;

	public JsonMapper() {
		this(Include.ALWAYS);
	}

	public JsonMapper(Include include) {
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(include);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	/**
	 * create the Mapper that only export not null properties to Json String
	 */
	public static JsonMapper buildNonNullMapper() {
		return new JsonMapper(Include.NON_NULL);
	}

	/**
	 * create the Mapper that only export changed properties to Json String
	 */
	public static JsonMapper buildNonDefaultMapper() {
		return new JsonMapper(Include.NON_DEFAULT);
	}

	/**
	 * create the Mapper that only export not null and not empty properties to
	 * Json String
	 */
	public static JsonMapper buildNonEmptyMapper() {
		return new JsonMapper(Include.NON_EMPTY);
	}

	/**
	 * Object can be POJO,Collection,Array.
	 * <ul>
	 * <li>if object == null return "null"</li>
	 * <li>if collection is empty return "[]"</li>
	 * </ul>
	 * 
	 */
	public String toJson(Object object) {

		try {
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			logger.warn("write to json string error:" + object, e);
			return null;
		}
	}

	/**
	 * unSerialize POJO , simple Collection like List<String>.
	 * 
	 * <ul>
	 * <li>if JSON String is Null or equals "null" return Null</li>
	 * <li>if JSON String equals "[]" return empty collection</li>
	 * </ul>
	 * 
	 * if you want to unSerialize complex Collection like List<MyBean>,please
	 * use fromJson(String,JavaType)
	 * 
	 * @see #fromJson(String, JavaType)
	 */
	public <T> T fromJson(String jsonString, Class<T> clazz) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			return mapper.readValue(jsonString, clazz);
		} catch (IOException e) {
			logger.warn("parse json string error:" + jsonString, e);
			return null;
		}
	}

	/**
	 * unSerialize complex Collection like List<MyBean>, first construct
	 * javaType by using createCollectionType and then use this function.
	 * 
	 * @see #createCollectionType(Class, Class...)
	 */
	public <T> T fromJson(String jsonString, JavaType javaType) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			return (T) mapper.readValue(jsonString, javaType);
		} catch (IOException e) {
			logger.warn("parse json string error:" + jsonString, e);
			return null;
		}
	}

	/**
	 * construct Collection Type.
	 * <ul>
	 * <li>use constructCollectionType(ArrayList.class,MyBean.class) to
	 * construct ArrayList<MyBean></li>
	 * <li>use constructCollectionType(HashMap.class,String.class, MyBean.class)
	 * to construct HashMap<String,MyBean></li>
	 * </ul>
	 * 
	 */
	public JavaType createCollectionType(Class<?> collectionClass,
			Class<?>... elementClasses) {
		return mapper.getTypeFactory().constructParametricType(collectionClass,
				elementClasses);
	}

	/**
	 * get original Mapper for more use
	 */
	public ObjectMapper getMapper() {
		return mapper;
	}
}
