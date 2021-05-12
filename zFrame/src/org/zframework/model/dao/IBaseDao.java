package org.zframework.model.dao;

import java.io.Serializable;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.zframework.model.pagination.PageBean;
import org.zframework.model.query.QueryParams;

/**
 * 对数据库的基本操作
 * 查询条件封装类:Restrictions
 * 派讯条件封装类:Order
 * @author ZENGCHAO
 *
 */
public interface IBaseDao {
	/**
	 * 获取SessionFactory
	 * @return
	 */
	public SessionFactory getSessionFactory();
	/**
	 * 获取Session
	 * 所有的操作，包括查询，增删改都要使用该对象
	 * 在提供的方法满足不了业务需求是调用此对象进行操作
	 * @return
	 */
	public Session getSession();
	/**
	 * 获取查询对象Query
	 * @return
	 */
	public Query getQuery(String hql);
	/**
	 * 获取查询对象SQLQuery
	 * @param sql
	 * @return
	 */
	public SQLQuery getSQLQuery(String sql);
	/**
	 * 获取查询对象Criteria
	 * @return
	 */
	public Criteria getCriteria(Class<?> clazz);
	/**
	 * 保存对象到数据库
	 * 无需主键值
	 * 保存成功后，对象拥有主键值
	 * @param <M>
	 * @param 要持久化的对象
	 * @return
	 */
	public <M> Serializable save(M m);
	/**
	 * 保存对象到数据库
	 * 此方法不会讲数据库生成的主键值绑定到对象中
	 * @param <M>
	 * @param 要持久化的对象
	 */
	public <M> void persist(M m);
	/**
	 * 保存或者更新对象
	 * 如果对象主键值存在，则执行修改，否则执行增加
	 * @param <M>
	 * @param 要保存或者修改的对象
	 */
	public <M> void saveOrUpdate(M m);
	/**
	 * 删除对象
	 * 无需设置对象的所有属性值，只需主键值即可
	 * 其他值如果存在，将被加入删除条件中
	 * @param <M>
	 * @param 需要删除的对象
	 */
	public <M> void delete(M m);
	/**
	 * 根据主键删除数据
	 * @param <M>
	 * @param 要删除的对象的Class
	 * @param id 主键值
	 */
	public <M> void delete(Class<M> clazz,Serializable id);
	/**
	 * 根据对象实体名称删除对象
	 * @param <M>
	 * @param 实体名称
	 * @param 要删除的对象
	 */
	public <M> void delete(String entityName,Object obj);
	/**
	 * 更新对象
	 * 对象的主键值必须不为空，否则无法更新
	 * @param <M>
	 * @param 要更新的对象
	 */
	public <M> void update(M m);
	/**
	 * 根据对象实体名称更新对象
	 * @param <M>
	 * @param entityName
	 * @param obj
	 */
	public <M> void update(String entityName,Object obj);
	public <M> void merge(M m);
	/**
	 * 批量增删改
	 * @param hql语句
	 * @param 参数列表
	 * @return
	 */
	public int execteBulk(final String hql, final Object[]  paramlist);
	/**
	 * 批量增删改
	 * @param SQL语句
	 * @param 参数列表
	 * @return
	 */
	public int execteNativeBulk(final String natvieSQL, final Object[]  paramlist);
	/**
	 * 根据主键获取对象
	 * @param <M>
	 * @param 要获取的对象的Class
	 * @param 对象的主键值
	 * @return
	 */
	public <M> M get(Class<M> clazz,Serializable id);
	/**
	 * 根据条件获取对象
	 * 如果有多条符合数据，则只返回首条数据
	 * @param <M>
	 * @param clazz
	 * @param criterions
	 * @return
	 */
	public <M> M getBy(Class<M> clazz,Criterion...criterions);
	/**
	 * 根据条件获取对象
	 * 如果有多条符合数据，则只返回首条数据
	 * @param <M>
	 * @param clazz
	 * @param criterions 查询条件
	 * @param order 排序条件
	 * @return
	 */
	public <M> M getBy(Class<M> clazz,Criterion criterion,Order order);
	/**
	 * 根据条件获取对象
	 * 如果有多条符合数据，则只返回首条数据
	 * @param clazz
	 * @param criterions 查询条件集合
	 * @param orders 排序条件集合
	 * @return
	 */
	public <M> M getBy(Class<M> clazz,Criterion[] criterions,Order[] orders);
	/**
	 * 根据HQL语句获取对象
	 * @param <M>
	 * @param 要获取的对象的Class
	 * @param hql
	 * @return
	 */
	public <M> M get(Class<M> clazz,String hql);
	/**
	 * 根据SQL语句获取对象
	 * @param <M>
	 * @param 要获取的对象的Class
	 * @param sql
	 * @return
	 */
	public <M> M getByNativeSQL(Class<M> clazz,String sql);
	
	/**
	 * 获取单个字段的单条数据
	 * @param clazz
	 * @param columName
	 * @return
	 */
	public <M> M getSingleColumn(Class<?> clazz,Class<M> m,String columName);
	/**
	 * 获取单个字段的单条数据
	 * @param 返回值类型
	 * @param hql
	 * @return
	 */
	public <M> M getSingleColumn(Class<M> m,String hql,Object[] queryParams);
	
	/**
	 * 获取多个字段单条数据
	 * @param clazz
	 * @param columnNames
	 * @return
	 */
	public Object[] getMuitColumns(Class<?> clazz,String...columnNames);
	/**
	 * 获取多个字段单条数据
	 * @param hql
	 * @return
	 */
	public Object[] getMuitColumns(String hql,Object[] queryParams);
	/**
	 * 获取全部集合
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz);
	/**
	 * 分页获取全部集合
	 * 在分页是需要许多查询条件的情况下使用
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 分页Bean
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,PageBean pageBean);
	/**
	 * 分页获取全部集合
	 * 没有任何条件的情况下使用
	 * 之间简单的页码和分页大小
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 页码
	 * @param 分页大小
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Integer currentPage,Integer pageSize);
	/**
	 * 有查询条件的分页集合
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 页码
	 * @param 分页大小
	 * @param 查询条件数组
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Integer currentPage,Integer pageSize,Criterion... criterions);
	/**
	 * 获取所有集合，并排序
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 页码
	 * @param 分页大小
	 * @param 排序条件数组
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Integer currentPage,Integer pageSize,Order... orders);
	/**
	 * 带一个查询条件的分页获取集合，并排序(只能按照一个字段排序)
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 页码
	 * @param 分页大小
	 * @param 查询条件
	 * @param 排序条件
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Integer currentPage,Integer pageSize,Criterion criterion,Order order);
	/**
	 * 多个查询条件和排序条件的分页获取集合
	 * 查询条件和排序条件采用数组方式
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 页码
	 * @param 分页大小
	 * @param 查询条件数组
	 * @param 排序条件数组
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Integer currentPage,Integer pageSize,Criterion[] criterions,Order[] orders);
	/**
	 * 多个查询条件和排序条件的分页获取集合
	 * 查询条件和排序条件采用集合方式
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 页码
	 * @param 分页大小
	 * @param 查询条件集合
	 * @param 排序条件集合
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Integer currentPage,Integer pageSize,List<Criterion> criterions,List<Order> orders);
	/**
	 * 根据查询条件查询
	 * 只有一个查询条件
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 查询条件
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Criterion criterion);
	/**
	 * 根据查询条件查询
	 * 多个查询条件
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 查询条件数组，任意个
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Criterion... criterions);
	/**
	 * 根据查询条件查询
	 * 多个查询条件
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,List<?> param);
	/**
	 * 获取所有数据并排序
	 * 只有一个排序条件
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 排序条件
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Order order);
	/**
	 * 获取所有数据并排序
	 * 多个排序条件
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 排序条件数组，任意个
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Order...orders);
	/**
	 * 根据一个查询条件和一个排序字段获取集合
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 查询条件
	 * @param 排序条件
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Criterion criterion,Order order);
	/**
	 * 根据多个查询条件和多个排序字段获取集合
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 查询条件集合
	 * @param 排序条件集合
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Criterion criterions,Order[] orders);
	/**
	 * 根据多个查询条件和多个排序字段获取集合
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 查询条件集合
	 * @param 排序条件集合
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Criterion[] criterions,Order[] orders);
	/**
	 * 根据指定条件集合查询，并按照排序条件集合排序
	 * @param <M>
	 * @param clazz
	 * @param criterions 查询条件集合
	 * @param orders 排序条件集合
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,List<Criterion> criterions,List<Order> orders);
	/**
	 * 根据封装的条件实体来查询集合
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 查询条件封装类
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,QueryParams queryParams);
	/**
	 * 根据HQL语句获取集合
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param HQL语句
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,String hql);
	/**
	 * 根据HQL语句获取集合并分页
	 * 只需要页码和分页大小的情况下使用
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param HQL语句
	 * @param 页码
	 * @param 分页大小
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,String hql,Integer currentPage,Integer pageSize);
	/**
	 * 根据HQL获取集合并分页
	 * 需要有查询提交时使用
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param HQL语句
	 * @param 分页Bean
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,String hql,PageBean pageBean);
	/**
	 * 获取集合
	 * 仅获取对象中的某些字段
	 * 其余字段为null值
	 * @param clazz
	 * @param 需要查询字段
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,String...columnNames);
	/**
	 * 获取集合 分页
	 * 仅获取对象中的某些字段
	 * 其余字段为null值
	 * @param clazz
	 * @param 需要查询字段
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,PageBean pageBean,String...columnNames);
	/**
	 * 获取单个字段的值
	 * @param clazz
	 * @param hql
	 * @return
	 */
	public <M> List<M> listSingleColumn(Class<?> clazz,Class<M> m,String columnName);
	/**
	 * 获取单个字段的值
	 * @param clazz
	 * @param hql
	 * @return
	 */
	public <M> List<M> listSingleColumn(Class<M> m,String hql,Object[] queryParams);
	/**
	 * 获取多个字段的值
	 * @param clazz
	 * @param columnNames
	 * @return
	 */
	public List<Object[]> listMuitColumn(Class<?> clazz,String...columnNames);
	/**
	 * 获取多个字段的值
	 * @param clazz
	 * @param columnNames
	 * @return
	 */
	public List<Object[]> listMuitColumn(String hql,Object[] queryParams);
	/**
	 * 根据SQL语句获取集合
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param SQL语句
	 * @return
	 */
	public <M> List<M> listByNativeSQL(Class<M> clazz,String sql);
	/**
	 * 根据SQL语句获取集合，带参数
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param SQL语句
	 * @param 参数列表数组，任意个
	 * @return
	 */
	public <M> List<M> listByNativeSQL(Class<M> clazz,String sql,Object[] params);
	/**
	 * 根据SQL语句获取集合，并分页
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param SQL语句
	 * @param 页码
	 * @param 分页大小
	 * @return
	 */
	public <M> List<M> listByNativeSQL(Class<M> clazz,String sql,Integer currentPage,Integer pageSize);
	/**
	 * 根据SQL语句获取集合，并分页，带参数
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param SQL语句
	 * @param 参数数组
	 * @param 页码
	 * @param 分页大小
	 * @return
	 */
	public <M> List<M> listByNativeSQL(Class<M> clazz,String sql,Object[] params,Integer currentPage,Integer pageSize);
	
	
	/**
	 * 获取数据库中<M>的总数量
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @return
	 */
	public <M> int count(Class<M> clazz);
	/**
	 * 根据条件查询数量
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param 查询条件数组，任意个
	 * @return
	 */
	public <M> int count(Class<M> clazz,Criterion...criterions);
	/**
	 * 根据HQL语句查询数量
	 * @param <M>
	 * @param 要获取的集合中的实体对象
	 * @param HQL语句
	 * @return
	 */
	public <M> int count(Class<M> clazz,String hql);
	/**
	 * 根据SQL语句查询数量
	 * @param <M>
	 * @param SQL语句
	 * @return
	 */
	public <M> int countByNativeSQL(String sql);
	/**
	 * 返回List
	 * List中的每一项是一个Map key为数据库字段名 value为数据库字段值
	 * @param sql
	 * @return
	 */
	//public List<Map<?, ?>> queryForList(String sql);
}
