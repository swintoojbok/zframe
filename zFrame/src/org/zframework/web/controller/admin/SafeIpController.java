package org.zframework.web.controller.admin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import org.zframework.app.common.ControllerCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.model.pagination.PageBean;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.SafeIp;
import org.zframework.web.service.admin.LogService;
import org.zframework.web.service.admin.SafeIpService;

@Controller
@RequestMapping("/admin/safeip")
public class SafeIpController extends BaseController<SafeIp>{
	@Autowired
	private SafeIpService safeIpService;
	@Autowired
	private LogService logService;
	@RequestMapping(method={RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> index(PageBean pageBean,String[] name,String[] value){
		Map<String, Object> result = list(pageBean, name, value, safeIpService);
		logService.recordInfo("查询安全管理IP配置","成功",getCurrentUser().getLoginName() , getRequest().getRemoteAddr());
		return result;
	}
	@RequestMapping(value="/add",method={RequestMethod.GET})
	public String add(){
		return "admin/safe/ip/add";
	}
	@RequestMapping(value="/doAdd",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAdd(@Valid @ModelAttribute("safeIp")SafeIp safeIp,BindingResult result){
		JSONObject jResult = new JSONObject();
		if(result.hasErrors()){
			jResult.element("isSaved", false);
			jResult.element("error", "请按要求填写表单!");
			//记录日志
			logService.recordInfo("新增安全配置IP地址","失败(非法提交表单!)",getCurrentUser().getLoginName() , getRequest().getRemoteAddr());
		}else{
			jResult = safeIpService.executeAdd(getRequest(), safeIp,getCurrentUser());
		}
		return jResult;
	}
	@RequestMapping(value="/edit/{id}",method={RequestMethod.GET})
	public String edit(Model model,@PathVariable Integer id){
		SafeIp ip = safeIpService.getById(id);
		if(ObjectUtil.isNotNull(ip)){
			model.addAttribute("safeIp", ip);
			return "admin/safe/ip/edit";
		}else{
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		}
	}
	@RequestMapping(value="/doEdit",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEdit(@Valid @ModelAttribute("safeIp")SafeIp safeIp,BindingResult result){
		JSONObject jResult = new JSONObject();
		if(result.hasErrors()){
			jResult.element("isEdited", false);
			jResult.element("error", "请按要求填写表单!");
			//记录日志
			logService.recordInfo("编辑安全配置IP地址","失败(非法提交表单!)",getCurrentUser().getLoginName() , getRequest().getRemoteAddr());
		}else{
			jResult = safeIpService.executeEdit(getRequest(), safeIp,getCurrentUser());
		}
		return jResult;
	}
	/**
	 * 执行删除
	 * @param request
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="/doDelete",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doDelete(HttpServletRequest request,@RequestParam Integer[] ids){
		return safeIpService.executeDelete(request, ids, getCurrentUser());
	}
	/**
	 * 执行启用或者禁用
	 * @param request
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="/doEnableOrDisable",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEnableOrDisable(HttpServletRequest request,int type,Integer[] ids){
		return safeIpService.executeEnableOrDisable(request, type,ids, getCurrentUser());
	}
}
