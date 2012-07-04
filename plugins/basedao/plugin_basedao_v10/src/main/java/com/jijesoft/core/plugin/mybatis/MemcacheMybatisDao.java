package com.jijesoft.core.plugin.mybatis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jijesoft.core.plugin.entity.IdEntity;
import com.jijesoft.core.plugin.mapper.JsonMapper;
import com.jijesoft.core.plugin.memcached.ISpyMemcachedClient;

/**
 * mybatis memcached support
 * 
 * @author eric.zhang
 * 
 * @param <T>
 */
@Component
public abstract class MemcacheMybatisDao<T extends IdEntity> extends
		BaseMybatisDao<T> {

	@Autowired
	private ISpyMemcachedClient spyMemcachedClient;

	public void updateCached(T t, String cacheKey, int cacheExpiredTime,
			JsonMapper jsonMapper) {

		update(t);

		spyMemcachedClient.set(cacheKey + t.getId(), cacheExpiredTime,
				jsonMapper.toJson(t));
	}

	public void deleteCached(long id, String cacheKey) {

		delete(id);

		spyMemcachedClient.delete(cacheKey + id);
	}

	public T selectByIdCached(long id, String cacheKey, int cacheExpiredTime,
			JsonMapper jsonMapper) {

		String jsonString = spyMemcachedClient.get(cacheKey + id);

		T t = null;
		if (StringUtils.isNotEmpty(jsonString)) {
			t = jsonMapper.fromJson(jsonString, entityClass);
		} else {
			t = selectById(id);
			if (t != null)
				spyMemcachedClient.set(cacheKey + id, cacheExpiredTime,
						jsonMapper.toJson(t));
		}

		return t;
	}

	@SuppressWarnings("serial")
	public List<T> batchSelectByIdCached(List<Long> ids, String cacheKey,
			int cacheExpiredTime, JsonMapper jsonMapper) {

		if (ids == null || ids.size() == 0)
			return null;

		final int ids_size = ids.size();
		List<T> result = new ArrayList<T>(ids_size) {
			{
				for (int i = 0; i < ids_size; i++) {
					add(null);
				}
			}
		};
		List<Long> no_cache_ids = new ArrayList<Long>();

		for (int i = 0; i < ids_size; i++) {
			long id = ids.get(i);

			String jsonString = spyMemcachedClient.get(cacheKey + id);

			if (StringUtils.isEmpty(jsonString)) {
				no_cache_ids.add(id);
			} else {
				result.set(i, jsonMapper.fromJson(jsonString, entityClass));
			}
		}

		if (no_cache_ids.size() > 0) {
			List<T> no_cache_prjs = batchSelectById(no_cache_ids);

			if (no_cache_prjs != null) {
				for (T t : no_cache_prjs) {
					spyMemcachedClient.set(cacheKey + t.getId(),
							cacheExpiredTime, jsonMapper.toJson(t));
					result.set(ids.indexOf(t.getId()), t);
				}
			}

		}

		return result;
	}

}
