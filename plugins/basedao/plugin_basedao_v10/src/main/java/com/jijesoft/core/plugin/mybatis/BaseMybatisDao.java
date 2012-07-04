package com.jijesoft.core.plugin.mybatis;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.jijesoft.core.plugin.entity.IdEntity;

@SuppressWarnings("unchecked")
public abstract class BaseMybatisDao<T extends IdEntity> extends
		SqlSessionDaoSupport {

	protected Class<T> entityClass;

	public BaseMybatisDao() {
		entityClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	protected abstract String getNamespace();

	protected static final String PAGE_OFFSET = "pageOffset";
	protected static final String PAGE_SIZE = "pageSize";
	protected static final String ORDER_STRING = "orderString";

	/**
	 * <insert id="insert"...
	 */
	public Long insert(T t) {
		getSqlSession().insert(getStatement("insert"), t);
		return t.getId();
	}

	/**
	 * <update id="update"...
	 */
	public void update(T t) {
		getSqlSession().update(getStatement("update"), t);
	}

	/**
	 * <delete id="delete"...
	 */
	public void delete(long id) {
		getSqlSession().delete(getStatement("delete"), id);
	}

	/**
	 * <select id="selectById"...
	 */
	public T selectById(long id) {
		return getSqlSession().selectOne(getStatement("selectById"), id);
	}

	/**
	 * <select id="batchSelectById"...
	 */
	public List<T> batchSelectById(List<Long> ids) {
		if (ids == null || ids.size() == 0)
			return null;
		return getSqlSession().selectList(getStatement("batchSelectById"), ids);
	}

	/**
	 * <select id="count"...
	 */
	public long count(Map<String, Object> parameter) {
		return (Long) getSqlSession().selectOne(getStatement("count"),
				parameter);
	}

	/**
	 * <select id="selectPage"...
	 */
	public Page<T> selectPage(final Pageable pageable,
			Map<String, Object> parameter) {

		Long count = count(parameter);
		List<T> list = getSqlSession().selectList(getStatement("selectPage"),
				applyPagination(parameter, pageable));

		return new PageImpl<T>(list, pageable, count);
	}

	/**
	 * <select id="select"...
	 */
	public Iterable<T> select(Map<String, Object> parameter) {

		return getSqlSession().selectList(getStatement("select"), parameter);
	}

	/**
	 * <select id="select"...
	 */
	public Iterable<T> select(Map<String, Object> parameter, Sort sort) {

		return getSqlSession().selectList(getStatement("select"),
				applySorting(parameter, sort));
	}

	protected Map<String, Object> applySorting(Map<String, Object> parameter,
			Sort sort) {

		if (sort == null)
			return parameter;

		StringBuilder sb = new StringBuilder();
		for (Order order : sort) {
			sb.append("," + order.getProperty() + " " + order.getDirection());
		}
		parameter.put(ORDER_STRING, sb.substring(1));

		return parameter;
	}

	protected Map<String, Object> applyPagination(
			Map<String, Object> parameter, Pageable pageable) {

		if (pageable == null)
			return parameter;

		parameter.put(PAGE_OFFSET, pageable.getOffset());
		parameter.put(PAGE_SIZE, pageable.getPageSize());

		return applySorting(parameter, pageable.getSort());
	}

	protected String getStatement(String id) {
		return getNamespace() + "." + id;
	}
}
