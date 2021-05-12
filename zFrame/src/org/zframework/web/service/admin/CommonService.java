package org.zframework.web.service.admin;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zframework.app.common.ApplicationCommon;
import org.zframework.app.common.ControllerCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.model.pagination.PageBean;
import org.zframework.web.entity.Common;
import org.zframework.web.entity.User;
import org.zframework.web.service.BaseService;

/**
 *@author YangKun
 *@time 2012-12-20 下午1:42:23
 */
@Service
public class CommonService extends BaseService<Common> {
	@Autowired
	private LogService logService;
	/**
	 * 初始化数据字典
	 * 将数据库中的数据项加载到系统内存中
	 */
	public void InitDataDict(){
		//获取数据库中所有的数据项
		List<Common> list = list();
		ApplicationCommon.DATADICT.clear();
		if(ObjectUtil.isNotNull(list)){
			for(Common com : list){
				ApplicationCommon.DATADICT.put(com.getKey(), com.getValue());
			}
		}
	}
	/**
	 * 分页显示commmon
	 * @param pageBean
	 * */
	public List<Common> getCommonList(PageBean pageBean, String name, String value) {
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value)){
					 
					pageBean.addCriterion(Restrictions.idEq(Integer.parseInt(value)));
				}
			}else{
				
				pageBean.addCriterion(Restrictions.like(name, value+"%"));
			}
		}
		List<Common> commList=this.listByPage(pageBean);
		return commList;
	}
	/***
	 * 执行增加
	 * @param request
	 * @param comm
	 * @param result
	 * @param user
	 * */
	public JSONObject executeAdd(HttpServletRequest request, Common comm,
			JSONObject jResult,User user) {
		Common commByKey=this.getByProperties("key",comm.getKey());
		if(ObjectUtil.isNotNull(commByKey)){
			jResult.element("isSaved", false);
			jResult.element("error", "标识已经存在!");
			logService.recordInfo("新增数据字典","失败(标识名称已经存在)", user.getLoginName(), request.getRemoteAddr());
			return jResult;
		}else{
			this.save(comm);
			//更新缓存
			if(ObjectUtil.isNull(ApplicationCommon.DATADICT.get(comm.getKey()))){
				ApplicationCommon.DATADICT.put(comm.getKey(),comm.getValue());
			}
			logService.recordInfo("新增数据字典","成功", user.getLoginName(), request.getRemoteAddr());
			jResult.element("isSaved", true);
			return jResult;
		}
	}
	/**
	 * 删除操作
	 * */
	public JSONObject executeDelete(HttpServletRequest request, Integer[] ids,
			JSONObject jResult,User user) {
		List<Common> list=this.getByIds(ids);
		boolean typebol=false;
		for(int i=0;i<list.size();i++){
			Common com=list.get(i);
			if(com.getType()==1||com.getType()==2){
				typebol=true;
				break;
			}else{
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.DATADICT.get(com.getKey()))){
					ApplicationCommon.DATADICT.remove(com.getKey());
				}
			}
		}
		if(typebol==false){
			this.delete(ids);
			logService.recordInfo("删除数据库字典", "成功",user.getLoginName(), request.getRemoteAddr());
			return jResult.element("isDeleted", true);
		}else{
			
			logService.recordInfo("删除数据库字典", "失败(尝试删除系统数据或关键数据)",user.getLoginName(), request.getRemoteAddr());
			jResult.element("error", "不能删除系统数据或关键数据");
			return jResult.element("isDeleted", false);
		}
	}
	/**
	 * 根据id获取
	 * */
	public Common getCommon(Integer id) {
		Common common =this.getById(id);
		return common;
	}
	/**
	 * 确认编辑
	 * */
	public JSONObject executeEdit(HttpServletRequest request, Common comm,
			JSONObject jResult,User user) {
		Common oldcomm =this.getById(comm.getId());
		Common commbyKey =this.getByProperties("key", comm.getKey());//根据传入的Key查看是否存在
		if(ObjectUtil.isNotNull(oldcomm)){
			//判断是否修改
			if(ObjectUtil.equalProperty(comm, oldcomm, new String[]{"id","key","value","descrip","type"})){
				jResult.element("NoChanges", true);
				return jResult;
			}//判断标识是否存在
			else if(commbyKey!=null&&commbyKey.getId()!=comm.getId()&&commbyKey.getKey().equals(comm.getKey())){
				jResult.element("isEdited", false);
				jResult.element("error", "标识已经存在，请重新输入");
				logService.recordInfo("编辑数据字典","失败（重复标识）", user.getLoginName(), request.getRemoteAddr());
				return jResult;
			}else{//修改
				oldcomm.setKey(comm.getKey());
				oldcomm.setValue(comm.getValue());
				oldcomm.setDescrip(comm.getDescrip());
				oldcomm.setType(comm.getType());
				this.update(oldcomm);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.DATADICT.get(comm.getKey()))){
					ApplicationCommon.DATADICT.remove(comm.getKey());
					ApplicationCommon.DATADICT.put(comm.getKey(),comm.getValue());
				}
				logService.recordInfo("编辑数据字典","成功", user.getLoginName(), request.getRemoteAddr());
				jResult.element("isEdited", true);
				jResult.element("info","修改成功!");
				return jResult;
			}
		}else{
			
			jResult.element("isEdited", false);
			jResult.element("error",ControllerCommon.UNAUTHORIZED_ACCESS);
			logService.recordInfo("编辑数据字典","失败（尝试编辑不存在的数据字典）", user.getLoginName(), request.getRemoteAddr());
			return jResult;
		}
	}
}
