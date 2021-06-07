package org.zframework.web.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zframework.app.common.ApplicationCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.model.dao.BaseHibernateDao;
import org.zframework.model.pagination.PageBean;
@Service
public class BaseService<M>{
	@Autowired
	protected BaseHibernateDao baseDao;
	private final Class<M> entityClass; 
	/**
	 * 获取系统配置信息
	 * @param key
	 * @return
	 */
	public String getApplicationCommon(String key){
		return ApplicationCommon.DATADICT.get(key);
	}
	/**
	 * 切换数据源
	 * @param dataSourceName
	 */
	public void setDataSource(String dataSourceName){
		baseDao.setDataSource(dataSourceName);
	}
	/**
	 * 恢复为默认数据源
	 * 在同一个方法中，如果存在以下情况，则需要调用该方法恢复为默认数据源
	 * 1. 切换完数据库之后需要操作默认数据库时
	 */
	public void restoreDefaultDataSource(){
		baseDao.restoreDefaultDataSource();
	}
	/**
	 * 获取<M>的Class对象
	 */
	@SuppressWarnings("unchecked")
	public BaseService(){
		Class<?> c = this.getClass();
		Type t = c.getGenericSuperclass();
		if(t instanceof ParameterizedType){
			Type[] p = ((ParameterizedType)t).getActualTypeArguments();
			this.entityClass = (Class<M>)p[0];
		}else{
			this.entityClass = null;
		}
	}
	/**
	 * 根据Id获取<M>
	 * @param id
	 * @return
	 */
	public M getById(Serializable id){
		return baseDao.get(entityClass, id);
	}
	/**
	 * 根据多个ID获取多个<M>
	 * @param ids
	 * @return
	 */
	public List<M> getByIds(Serializable[] ids){
		return baseDao.list(entityClass, Restrictions.in("id", ids));
	}
	/**
	 * 根据Name获取<M>
	 * @param name
	 * @return
	 */
	public M getByName(String name){
		return baseDao.getBy(entityClass, Restrictions.eq("name", name));
	}
	/**
	 * 根据属性名获取<M>
	 * @param propName
	 * @param propValue
	 * @return
	 */
	public M getByProperties(String propName,String propValue){
		return baseDao.getBy(entityClass, Restrictions.eq(propName, propValue.toString()));
	}
	/**
	 * 根据多个的查询条件查询单个对象
	 * @param criterions
	 * @return
	 */
	public M get(Criterion...criterions){
		return baseDao.getBy(entityClass, criterions);
	}
	/**
	 * 获取单个字段的值
	 * @param field
	 * @return
	 */
	public Object getField(String field){
		Object value = null;
		return value;
	}
	/**
	 * 分页获取<M>
	 * @param pageBean
	 * @return
	 */
	public List<M> listByPage(PageBean pageBean){
		if(ObjectUtil.isNotNull(pageBean.getCriterions()) && ObjectUtil.isNotEmpty(pageBean.getCriterions()))
			pageBean.setTotalCount(baseDao.count(entityClass,pageBean.getCriterions().toArray(new Criterion[]{})));
		else
			pageBean.setTotalCount(baseDao.count(entityClass));
		return baseDao.list(entityClass, pageBean);
	}
	/**
	 * 获取全部数据
	 * @return
	 */
	public List<M> list(){
		return baseDao.list(entityClass);
	}
	/**
	 * 根据查询条件获取集合
	 * @param criterions
	 * @return
	 */
	public List<M> list(Criterion...criterions){
		return baseDao.list(entityClass, criterions);
	}
	/**
	 * 按照排序规则获取集合
	 * @param 排序条件
	 * @return
	 */
	public List<M> list(Order...orders){
		return baseDao.list(entityClass,orders);
	}
	/**
	 * 获取集合
	 * @param criterion
	 * @param order
	 * @return
	 */
	public List<M> list(Criterion criterion,Order order){
		return baseDao.list(entityClass, criterion, order);
	}
	/**
	 * 
	 * @param criterions
	 * @param orders
	 * @return
	 */
	public List<M> list(Criterion[] criterions,Order...orders){
		return baseDao.list(entityClass, criterions, orders);
	}
	/**
	 * 根据HQL查询
	 * @param hql
	 * @return
	 */
	public List<M> list(String hql){
		return baseDao.list(entityClass,hql);
	}
	/**
	 *保存<M>
	 * @param m
	 */
	public void save(M m){
		baseDao.save(m);
	}
	/**
	 * 更新<M>
	 * @param m
	 */
	public void update(M m){
		baseDao.update(m);
	}
	/**
	 * 更新<M>
	 * @param m
	 */
	public void merge(M m){
		baseDao.update(m);
	}
	/**
	 * 根据ID删除<M>
	 * @param id
	 */
	public void delete(Serializable id){
		baseDao.delete(entityClass, id);
	}
	/**
	 * 根据多个id删除
	 * @param ids
	 */
	public void delete(Serializable[] ids){
		StringBuffer strIds = new StringBuffer();
		for(Serializable id : ids)
			strIds.append(","+id);
		String hql = "delete "+entityClass.getSimpleName()+" where id in("+strIds.substring(1)+")";
		baseDao.execteBulk(hql,null);
	}
	/**
	 * 删除<M>
	 * @param m
	 */
	public void delete(M m){
		baseDao.delete(m);
	}
	/**
	 * 获取所有数量
	 */
	public int count(){
		return baseDao.count(entityClass);
	}
	/**
	 * 获取指定条件的集合数量
	 * @param criterions
	 */
	public int count(Criterion...criterions){
		return baseDao.count(entityClass, criterions);
	}
}
