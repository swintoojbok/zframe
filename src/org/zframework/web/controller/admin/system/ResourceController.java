package org.zframework.web.controller.admin.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.JSONUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.ControllerCommon;
import org.zframework.core.web.support.WebResult;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.Button;
import org.zframework.web.entity.system.Resource;
import org.zframework.web.service.admin.system.ButtonService;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.ResourceService;
@Controller
@RequestMapping("/admin/res")
public class ResourceController extends BaseController<Resource>{
	@Autowired
	private ResourceService resService;
	@Autowired
	private ButtonService btnService;
	@Autowired
	private LogService logService;
	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		logService.recordInfo("????????????","??????",getCurrentUser().getLoginName() , getRequestAddr());
		return "admin/system/res/index";
	}
	@RequestMapping(value="/resList",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> resList(String name,String value){
		Map<String,Object> resMap = new HashMap<String, Object>();
		List<Criterion> criterionList = new ArrayList<Criterion>();
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value))
					criterionList.add(Restrictions.idEq(Integer.parseInt(value)));
			}else{
				criterionList.add(Restrictions.like(name, "%"+value+"%"));
			}
		}
		List<Resource> resList = resService.list_noLazyButtons(criterionList.toArray(new Criterion[]{}),Order.asc("location"),Order.asc("parentId"));
		List<JSONObject> jResList = new ArrayList<JSONObject>();
		if(resList!=null){
			for(Resource res : resList){
				JSONObject jRes = JSONUtil.toJsonObject(res,"roles");
				if(StringUtil.isEmpty(value)){//????????????????????????????????????????????????
					jRes.element("_parentId", res.getParentId()==0?"":res.getParentId());
				}else{//????????????????????????????????????????????????????????????????????????
					if(hasPropertyValueInArray(jResList, "id", res.getParentId())){
						jRes.element("_parentId", res.getParentId()==0?"":res.getParentId());
					}
				}
				jRes.element("iconCls", res.getIcon());
				jResList.add(jRes);
			}
		}
		resMap.put("rows", jResList);
		resMap.put("total", jResList.size());
		return resMap;
	}
	/**
	 * ???????????????
	 * @param ?????????
	 * @return
	 */
	@RequestMapping(value="/child/{parentId}",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> childList(@PathVariable String parentId){
		Map<String,Object> childResMap = new HashMap<String, Object>();
		return childResMap;
	}
	/**
	 * ??????????????????
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/add",method={RequestMethod.GET})
	public String toAdd(Model model){
		return "admin/system/res/add";
	}
	/**
	 * ????????????
	 * @param request ??????????????????
	 * @param res ????????????
	 * @param result 
	 * @return
	 */
	@RequestMapping(value="/doAdd",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONObject doAdd(HttpServletRequest request,@Valid @ModelAttribute("res")Resource res,BindingResult result){
		if(result.hasErrors()){
			//????????????
			logService.recordInfo("????????????","??????",getCurrentUser().getLoginName() , request.getRemoteAddr());
			return WebResult.error("????????????????????????!");
		}else{	
			return resService.executeAdd(request, res,getCurrentUser());
		}
	}
	/**
	 * ???????????????
	 * @param ?????????ID 
	 * ????????????????????????Id???????????????Id???0??????????????????????????????
	 * ?????????????????????????????? ???????????????
	 * @return
	 */
	@RequestMapping(value="/resTree")
	@ResponseBody
	public JSONArray resTree(@RequestParam(value="id",required=false) Integer parentId){
		JSONArray jResTree = new JSONArray();
		if(ObjectUtil.isNull(parentId)){
			parentId = 0;
			JSONObject jNode = new JSONObject();
			jNode.element("id", 0);
			jNode.element("text", "???????????????");
			jNode.element("iconCls", "icon-ok");
			jResTree.add(jNode);
		}
		List<Resource> list = resService.list(new Criterion[]{Restrictions.eq("parentId", parentId)},Order.asc("location"),Order.asc("parentId"));
		if(ObjectUtil.isNotNull(list)){
			for(Resource res : list){
				JSONObject jNode = new JSONObject();
				jNode.element("id", res.getId());
				jNode.element("text", res.getName());
				jNode.element("iconCls", res.getIcon());
				jNode.element("url", res.getUrl());
				if(parentId == 0)
					jNode.element("state", "closed");
				jResTree.add(jNode);
			}
		}
		return jResTree;
	}
	/**
	 * ??????????????????
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/edit/{id}",method={RequestMethod.GET})
	public String toEdit(Model model,@PathVariable Integer id){
		//??????????????????
		Resource res = resService.getById(id);
		//?????????????????????????????????
		if(ObjectUtil.isNotNull(res)){
			model.addAttribute("res", res);
			if(res.getParentId()==0){
				model.addAttribute("parentResName","???????????????");
			}else{
				Resource parentRes = resService.getById(res.getParentId());
				model.addAttribute("parentResName",parentRes.getName());
			}
			return "admin/system/res/edit";
		}else{
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		}
	}
	/**
	 * ????????????
	 * @param request
	 * @param res
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/doEdit",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEdit(HttpServletRequest request,@Valid @ModelAttribute("res")Resource res,BindingResult result){
		if(result.hasErrors()){
			logService.recordInfo("????????????","??????(????????????????????????)", getCurrentUser().getLoginName(), request.getRemoteAddr());
			return WebResult.error("????????????????????????!");
		}else{
			return resService.executeEdit(request, res,getCurrentUser());
		}
	}
	
	/**
	 * ????????????
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/doDelete",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONObject doDelete(HttpServletRequest request,@RequestParam Integer[] ids){
		if(!isAllowAccess()){
			return WebResult.NeedVerifyPassword();
		}else{
			//??????????????????
			resService.deleteRes(ids);
			//?????????????????????????????????????????????????????????
			logService.recordInfo("????????????","??????", getCurrentUser().getLoginName(), request.getRemoteAddr());
			return WebResult.success();
		}
	}
	/**
	 * ??????????????????
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/sort",method={RequestMethod.GET})
	public String toSort(Model model){
		model.addAttribute("childRes", resService.list(Restrictions.eq("parentId", 0),Order.asc("location")));
		return "admin/system/res/sort";
	}
	@RequestMapping(value="/childRes",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONArray childRes(int parentId){
		JSONArray childRes = new JSONArray();
		List<Resource> list = resService.list(new Criterion[]{Restrictions.eq("parentId", parentId)},Order.asc("location"),Order.asc("parentId"));
		if(ObjectUtil.isNotNull(list)){
			for(Resource res : list){
				JSONObject jNode = new JSONObject();
				jNode.element("id", res.getId());
				jNode.element("name", res.getName());
				jNode.element("icon", res.getIcon());
				childRes.add(jNode);
			}
		}
		return childRes;
	}
	/**
	 * ????????????
	 * @return
	 */
	@RequestMapping(value="/doSort",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doSort(HttpServletRequest request,Integer parentId,Integer[] ids){
		try {
			boolean result = resService.executeSort(parentId, ids);
			if(result){
				logService.recordInfo("????????????","??????", getCurrentUser().getLoginName(), request.getRemoteAddr());
				return WebResult.success();
			}else{
				logService.recordInfo("????????????","??????", getCurrentUser().getLoginName(), request.getRemoteAddr());
				return WebResult.error("????????????");
			}
		} catch (Exception e) {
			logService.recordInfo("????????????","??????(?????????????????????)", getCurrentUser().getLoginName(), request.getRemoteAddr());
			return WebResult.error("????????????");
		}
	}
	/**
	 * ????????????????????????
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/assignButton/{id}",method={RequestMethod.GET})
	public String assignButton(Model model,@PathVariable Integer id){
		Resource res = resService.getById_NoLazyButtons(id);
		List<Button> btns = res.getButtons();
		Integer[] ids = new Integer[btns.size()];
		for(int i=0;i<btns.size();i++){
			ids[i] = btns.get(i).getId();
		}
		model.addAttribute("btnIds",StringUtil.toString(ids));
		model.addAttribute("resId", id);
		return "admin/system/res/assignButton";
	}
	@RequestMapping(value="/doAssignButton",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAssignButton(HttpServletRequest request,Integer resId,Integer[] btnIds){
		if(ObjectUtil.isNotEmpty(btnIds)){
			List<Button> selectedBtns = btnService.getByIds(btnIds);
			Resource res = resService.getById(resId);
			res.setButtons(selectedBtns);
			resService.update(res);
			logService.recordInfo("????????????","??????", getCurrentUser().getLoginName(), request.getRemoteAddr());
			return WebResult.success();
		}else{
			Resource res = resService.getById(resId);
			res.setButtons(null);
			resService.update(res);
			logService.recordInfo("????????????","??????(????????????????????????)", getCurrentUser().getLoginName(), request.getRemoteAddr());
			return WebResult.success();
		}
	}
	/**
	 * ??????????????????????????????
	 * ???????????????????????????????????????
	 * @param  ??????ID
	 * @return
	 */
	@RequestMapping(value="/resourceButtonsForRole",method={RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> resourceButtonsForRole(@RequestParam(value="resId",required=false) Integer resId){
		Map<String,Object> result = new HashMap<String, Object>();
		List<Button> resBtns = new ArrayList<Button>(); 
		if(ObjectUtil.isNotNull(resId)){
			Resource res = resService.getById_NoLazyButtons(resId);
			if(ObjectUtil.isNotNull(res)){
				resBtns = res.getButtons();
			}
		}
		//?????????Json?????????????????????????????????
		result.put("rows", JSONUtil.toJsonObjectListNoLazy(resBtns));
		result.put("total", resBtns.size());
		return result;
	}
	/**
	 * ??????????????????
	 * @return
	 */
	@RequestMapping(value="/selIcon")
	public String selIcon(Model model,HttpServletRequest request){
		model.addAttribute("list",ApplicationCommon.ICONCLS_LIST);
		return "admin/system/res/selIcon";
	}
	/**
	 * ????????????????????????
	 * @param ids
	 * @param 0 ?????????1??????
	 * @return
	 */
	@RequestMapping(value="/lock",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject lockOrUnLockUser(Integer[] ids,int type){
		JSONObject jResult = new JSONObject();
		if(ObjectUtil.isNotEmpty(ids))
			jResult = resService.executeLockOrUnLockUser(ids, type);
		else{
			String resultTag = "isLocked";
			if(type == 1){
				resultTag = "isUnLocked";
			}
			jResult.element(resultTag, false);
			jResult.element("error", "????????????!");
		}
		return jResult;
	}
}
