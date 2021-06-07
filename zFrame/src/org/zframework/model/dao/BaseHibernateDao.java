package org.zframework.model.dao;

import org.aspectj.lang.JoinPoint;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.zframework.core.ApplicationContextHelper;
import org.zframework.database.MultiDataSource;
/**
 * 数据库基础操作类的父类
 * 实现IBaseDao接口
 * @author ZENGCHAO
 *
 */
public abstract class BaseHibernateDao implements IBaseDao{
	public abstract Session getSession();
	public abstract Query getQuery(String hql);
	public abstract SQLQuery getSQLQuery(String sql);
	public abstract Criteria getCriteria(Class<?> clazz);
	public abstract SessionFactory getSessionFactory();
	/**
	 * 每次调用方法之后
	 * @param joinPoint
	 */
	public abstract void invokeAfter(JoinPoint joinPoint);
	/**
	 * 切换为指定数据源
	 * @param dataSourceName
	 */
	public void setDataSource(String dataSourceName){
		MultiDataSource dataSource = ApplicationContextHelper.getInstance().getBean("multiDataSource");
		dataSource.setDataSource(dataSource.getDataSource(dataSourceName));
	}
	/**
	 * 恢复为默认数据源
	 */
	public void restoreDefaultDataSource(){
		MultiDataSource dataSource = ApplicationContextHelper.getInstance().getBean("default");
		dataSource.restoreDefaultDataSource();
	}
}
