package org.zframework.web.controller.admin;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.Unit;
import org.zframework.web.service.admin.LogService;
import org.zframework.web.service.admin.UnitService;


/**
 * 机构管理模块
 * @author YangKun
 * @time 2012-12-11 下午3:00:32
 * */
@Controller
@RequestMapping("/admin/unit")
public class UnitController extends BaseController<Unit> {
	@Autowired
	private UnitService unitService;
	@Autowired
	private LogService logService;

	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		logService.recordInfo("查询机构","成功",getCurrentUser().getLoginName() , getRequest().getRemoteAddr());
		return "admin/unit/index";
	}
	@RequestMapping(value="/unitList",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> unitList(String name,String value){
		return unitService.getUnitList(name, value,this.getCurrentUser());
	}
	
	/**
	 * 转向增加页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/add",method={RequestMethod.GET})
	public String toAdd(Model model){
		return "admin/unit/add";
	}
	/**
	 * 获取机构列表信息
	 * @param id 传入的机构Id
	 * */
	@RequestMapping(value="/unitTree")
	@ResponseBody
	public JSONArray getunitTree(@RequestParam(value="id",required=false) Integer parentId,@RequestParam(value="typeId",required=false) Integer typeId){
		
		return unitService.getUnitTree(parentId, typeId,this.getCurrentUser());
	}
	
	/**
	 * 新建机构时如果选择没有上级目录，则进入此方法获取，新建的部门类型
	 * @param id 传入的机构Id
	 * */
	@RequestMapping(value="/getunitTypeTree")
	@ResponseBody
	public JSONArray getunitTypeTree(){
		return unitService.getUnitTypeTree();
	}
	
	/**
	 * 增加机构
	 * @param request 用于记录日志
	 * @param res 资源对象
	 * @param result 
	 * @return
	 */
	@RequestMapping(value="/doAdd",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONObject addSave(HttpServletRequest request,@Valid @ModelAttribute("unit")Unit unit,BindingResult result){
		JSONObject jResult = new JSONObject();
		if(result.hasErrors()){//在服务器端再验证一遍
			jResult.element("isSaved", false);
			jResult.element("error", "请按要求填写表单");
			logService.recordInfo("新增机构","失败", getCurrentUser().getLoginName(), request.getRemoteAddr());
		}else{
			return unitService.executeUnitAdd(request, unit, jResult,this.getCurrentUser());
		}
		return jResult;
	}

	/**
	 * 转向编辑页面
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/edit/{id}",method={RequestMethod.GET})
	public String toEdit(Model model,@PathVariable Integer id){
		return unitService.toUpdate(model, id);
		
	}

	/***
	 * 确认编辑机构
	 * @param request
	 * @param unit 
	 * @param result
	 * @return
	 * */
	@RequestMapping(value="/doEdit",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEdit(HttpServletRequest request,@Valid @ModelAttribute("unit")Unit unit,BindingResult result){
		return unitService.executeEdit(request, unit, result,this.getCurrentUser());
		 
	}
	
	/***
	 * 删除机构
	 * @param request
	 * @param unit 
	 * @param result
	 * @return
	 * */
	@RequestMapping(value="/doDelete",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doDelete(HttpServletRequest request,@RequestParam Integer[] ids){
		JSONObject jResult = new JSONObject();
		if(!isAllowAccess()){
			jResult.element("NeedVerifyPassword", true);
		}else{
			return unitService.executeDelete(request, ids, jResult,this.getCurrentUser());
		}
		return jResult;
		
	}
	
	/**
	 * 资源管理调用的方法
	 * */
	@RequestMapping(value="/unitListForUser",method={RequestMethod.POST})
	@ResponseBody
	public JSONArray unitListForUser(@RequestParam(required=false)Integer id){
		return unitService.toUnitListForUser(id);
	}
	
	/**
	 * 根据父级机构获取子集机构
	 * @param parentId
	 * @return
	 */
	public JSONArray unitListForUserByPId(Integer parentId){
		return unitService.toForUserByPId(parentId);
	}

}
