package org.zframework.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.criterion.Restrictions;
import org.zframework.app.common.ApplicationCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.ReflectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.WebContextHelper;
import org.zframework.model.pagination.PageBean;
import org.zframework.web.entity.Role;
import org.zframework.web.entity.User;
import org.zframework.web.service.BaseService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 控制类的父类
 * 一般用于放置一些controller类里面用到的逻辑方法
 * @author ZENGCHAO
 *
 * @param <M>
 */
public class BaseController<M> {
	private User currentUser = null;
	
	/**
	 * 当前用户是否是超级管理员
	 * @return
	 */
	public boolean isSuperadmin(){
		boolean isSuperadmin = false;
		this.currentUser = WebContextHelper.getSession()==null?null:(User)WebContextHelper.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
		if(ObjectUtil.isNotNull(currentUser)){
			if(currentUser.getLoginName().equals(ApplicationCommon.SYSTEM_ADMIN)){
				isSuperadmin = true;
			}else{
				isSuperadmin = false;
			}
		}
		return isSuperadmin;
	}
	/**
	 * 当前用户是否是系统管理员
	 * 权限低于超级管理员
	 * @return
	 */
	public boolean isSystemadmin(){
		boolean isSystemadmin = false;
		this.currentUser = WebContextHelper.getSession()==null?null:(User)WebContextHelper.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
		if(ObjectUtil.isNotNull(currentUser)){
			if(isSuperadmin()){//如果是超级管理员，也就拥有了系统管理员的角色
				isSystemadmin = true;
			}else{
				//判断角色列表中是否拥有系统管理员角色
				for(Role role : this.currentUser.getRoles()){
					if(role.getName().equals(getApplicationCommon("SystemRole"))){
						isSystemadmin = true;
						break;
					}
				}
			}
		}
		return isSystemadmin;
	}
	/**
	 * 获取当前登录用户
	 * 不存在返回Null
	 * @return
	 */
	public User getCurrentUser(){
		return WebContextHelper.getSession()==null?null:(User)WebContextHelper.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
	}
	/**
	 * 获取请求对象
	 * @return
	 */
	public HttpServletRequest getRequest(){
		return WebContextHelper.getSession()==null?null:WebContextHelper.getRequest();
	}
	/**
	 * 判断是否通过密码验证
	 * 调用一次之后验证就将失效
	 * @return
	 */
	public boolean isAllowAccess(){
		Object obj = WebContextHelper.getSession().getAttribute(ApplicationCommon.ALLOW_ACCESS);
		if(ObjectUtil.isNotNull(obj)){//判断是否是第一次验证，第一次验证时session中应该是没有AllowAccess的值
			Boolean allowAccess = (Boolean) obj;
			//使上次验证失效
			WebContextHelper.getSession().setAttribute(ApplicationCommon.ALLOW_ACCESS, false);
			return allowAccess;
		}else{
			return false;
		}
	}
	/**
	 * 判断集合中是否确定的属性及属性值
	 * @param array
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean hasPropertyValueInArray(Object array,Object propertyName,Object propertyValue){
		if(array instanceof JSONArray){
			JSONArray jArray = (JSONArray) array;
			int len = jArray.size();
			for(int i=0;i<len;i++){
				JSONObject jObj = jArray.getJSONObject(i);
				if(propertyValue.equals(jObj.get(propertyName))){
					return true;
				}
			}
		}else if(array instanceof List<?>){
			List<JSONObject> jList = (List<JSONObject>) array;
			for(JSONObject jObj : jList){
				if(propertyValue.equals(jObj.get(propertyName))){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * 获取系统配置信息
	 * @param key
	 * @return
	 */
	public String getApplicationCommon(String key){
		return ApplicationCommon.DATADICT.get(key);
	}
	/**
	 * 分页获取集合
	 * @param pageBean 分页Bean 包含查询条件，排序条件
	 * @param name 搜索用 字段名称
	 * @param value 搜索用 字段值
	 * @param BaseServiceDao<M> 查询用Service实现类
	 * @return
	 */
	public <T> Map<String,Object> list(PageBean pageBean,String name,String value,BaseService<T> bsd){
		Map<String,Object> btnMap = new HashMap<String, Object>();
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value))
					pageBean.addCriterion(Restrictions.eq(name, Integer.parseInt(value)));
			}else{
				pageBean.addCriterion(Restrictions.like(name, "%"+value+"%"));
			}
		}
		List<T> safeIpList = bsd.listByPage(pageBean);
		ReflectUtil.removeLazyProperty(safeIpList);
		btnMap.put("rows", safeIpList);
		btnMap.put("total", pageBean.getTotalCount());
		return btnMap;
	}
	/**
	 * 分页获取集合
	 * @param pageBean 分页Bean 包含查询条件，排序条件
	 * @param name 搜索用 字段名称数组
	 * @param value 搜索用 字段值数组
	 * @param BaseServiceDao<M> 查询用Service实现类
	 * @return
	 */
	public <T> Map<String,Object> list(PageBean pageBean,String[] name,String[] value,BaseService<T> bsd){
		Map<String,Object> btnMap = new HashMap<String, Object>();
		if(ObjectUtil.isNotEmpty(name) && ObjectUtil.isNotEmpty(value)){
			for(int i=0;i<value.length;i++){
				String n = name[i];
				String v = value[i];
				if("id".equals(n)){
					if(RegexUtil.isInteger(v))
						pageBean.addCriterion(Restrictions.eq(n, Integer.parseInt(v)));
				}else{
					pageBean.addCriterion(Restrictions.like(n, "%"+v+"%"));
				}
			}
		}
		List<T> safeIpList = bsd.listByPage(pageBean);
		ReflectUtil.removeLazyProperty(safeIpList);
		btnMap.put("rows", safeIpList);
		btnMap.put("total", pageBean.getTotalCount());
		return btnMap;
	}
}
