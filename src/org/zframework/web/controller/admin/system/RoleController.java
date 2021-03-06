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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.zframework.core.util.JSONUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.propertyEditor.RoleTypeEditer;
import org.zframework.core.web.support.ControllerCommon;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.pagination.PageBean;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.Button;
import org.zframework.web.entity.system.Resource;
import org.zframework.web.entity.system.Role;
import org.zframework.web.entity.system.RoleResourceButton;
import org.zframework.web.entity.system.type.RoleType;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.ResourceService;
import org.zframework.web.service.admin.system.RoleResBtnService;
import org.zframework.web.service.admin.system.RoleService;

@Controller
@RequestMapping("/admin/role")
public class RoleController extends BaseController<Role>{
	@Autowired
	private RoleService roleService;
	@Autowired
	private ResourceService resService;
	@Autowired
	private RoleResBtnService rrbService;
	@Autowired
	private LogService logService;
	/**
	 * ??????????????????
	 * @param model
	 * @return
	 */
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model){
		logService.recordInfo("????????????","??????",getCurrentUser().getLoginName() , getRequestAddr());
		return "admin/system/role/index";
	}
	/**
	 * ??????????????????
	 * @param pageBean
	 * @return
	 */
	@RequestMapping(value="/roleList",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> roleList(PageBean pageBean,String name,String value){
		return list(pageBean, name, value, roleService);
	}
	/**
	 * ??????????????????
	 * ?????????????????????????????????????????????????????????
	 * @param pageBean
	 * @return
	 */
	@RequestMapping(value="/roleListForUser",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> roleListForUser(PageBean pageBean,String name,String value){
		Map<String,Object> roleMap = new HashMap<String, Object>();
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value))
					pageBean.addCriterion(Restrictions.eq(name, Integer.parseInt(value)));
			}else{
				pageBean.addCriterion(Restrictions.like(name, "%"+value+"%"));
			}
		}
		//?????????????????????????????????
		pageBean.addCriterion(Restrictions.eq("enabled", 0));
		//??????????????????????????????????????????????????????????????????
		if(!isSystemadmin()){
			pageBean.addCriterion(Restrictions.not(Restrictions.eq("type", RoleType.SYSTEM)));
		}
		List<Role> roleList = roleService.list(pageBean.getCriterions().toArray(new Criterion[]{}));
		roleMap.put("rows", roleList);
		roleMap.put("total", roleList.size());
		return roleMap;
	}
	@RequestMapping(value="/add",method={RequestMethod.GET})
	public String toAdd(){
		return "admin/system/role/add";
	}
	@RequestMapping(value="/doAdd",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAdd(HttpServletRequest request,@Valid @ModelAttribute("role")Role role,BindingResult result){
		if(result.hasErrors()){
			//????????????
			logService.recordInfo("????????????","??????(??????????????????!)",getCurrentUser().getLoginName() , request.getRemoteAddr());
			return WebResult.error("????????????????????????!");
		}else{
			Role eqRole = roleService.getByName(role.getName());
			if(ObjectUtil.isNull(eqRole)){
				roleService.save(role);
				//????????????
				logService.recordInfo("????????????","??????",getCurrentUser().getLoginName() , request.getRemoteAddr());
				return WebResult.success();
			}else{
				//????????????
				logService.recordInfo("????????????","??????(?????????????????????????????????!)",getCurrentUser().getLoginName() , request.getRemoteAddr());
				return WebResult.error("?????????????????????!");
			}
		}
	}
	@RequestMapping(value="/doDelete",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doDelete(HttpServletRequest request,@RequestParam Integer[] ids){
		JSONObject jResult = new JSONObject();
		if(!isAllowAccess()){
			jResult.element("NeedVerifyPassword", true);
		}else{
			//????????????
			List<Role> roleList = roleService.getByIds(ids);
			for(Role role : roleList){
				if(ObjectUtil.isNull(role)){
					jResult = WebResult.error("??????????????????????????????");
					logService.recordInfo("????????????","??????????????????????????????????????????",getCurrentUser().getLoginName() , request.getRemoteAddr());
					break;
				}else if(role.getType() == RoleType.SYSTEM){
					jResult = WebResult.error("????????????????????????");
					logService.recordInfo("????????????","????????????????????????????????????",getCurrentUser().getLoginName() , request.getRemoteAddr());
					break;
				}else{
					roleService.delete(role);
					jResult = WebResult.success();
					logService.recordInfo("????????????","???????????????ID:"+role.getId()+",????????????:"+role.getName()+")",getCurrentUser().getLoginName() , request.getRemoteAddr());
				}
			}
		}
		return jResult;
	}
	/**
	 * ????????????????????????
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/edit/{id}",method={RequestMethod.GET})
	public String toEdit(Model model,@PathVariable Integer id){
		Role role = roleService.getById(id);
		if(!role.getName().equals(getApplicationCommon("SystemRole")) && !role.getName().equals(getApplicationCommon("OrdinaryRole")))
			model.addAttribute("isEditEnabled", true);
		else
			model.addAttribute("isEditEnabled", false);
		model.addAttribute("role", role);
		return "admin/system/role/edit";
	}
	@RequestMapping(value="/doEdit",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEdit(HttpServletRequest request,@Valid @ModelAttribute("role")Role role,BindingResult result){
		return roleService.executeEdit(request,getCurrentUser(),role, result);
	}
	/**
	 * ????????????????????????
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value="/assignResource/{roleId}",method={RequestMethod.GET})
	public String assignResource(Model model,@PathVariable Integer roleId){
		Role role = roleService.getById_NoLazyResourceAndButtons(roleId);
		if(ObjectUtil.isNotNull(role) && !role.getName().equals(getApplicationCommon("SystemRole"))){
			//???????????????????????????
			List<Resource> roleResources = role.getResources();
			//????????????????????????????????????
			List<RoleResourceButton> roleResBtns = rrbService.list(Restrictions.eq("roleId", roleId));
			JSONArray resArray = new JSONArray();
			for(Resource res : roleResources){
				JSONObject jRoleRes = new JSONObject();
				List<Button> btnList = getResourceButtons(roleResBtns, res);
				JSONArray btnIds = new JSONArray();
				for(Button btn : btnList){
					btnIds.add(btn.getId());
				}
				jRoleRes.element("resId", res.getId());
				jRoleRes.element("buttonIds",btnIds);
				resArray.add(jRoleRes);
			}
			model.addAttribute("roleResBtns",resArray.toString());
			model.addAttribute("role",role);
			return "admin/system/role/assignResource";
		}else{
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		}
	}
	/**
	 * ??????????????????
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value="/roleResource/{roleId}",method={RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> roleResource(@PathVariable("roleId") Integer roleId){
		Map<String,Object> resMap = new HashMap<String, Object>();
		List<Resource> roleResources = new ArrayList<Resource>();
		List<JSONObject> jRoleResources = new ArrayList<JSONObject>();
		//???????????????????????????
		roleResources = resService.list_noLazyButtons(new Criterion[]{Restrictions.eq("enabled", 0)},Order.asc("location"),Order.asc("parentId"));
		//????????????????????????????????????
		List<RoleResourceButton> roleResBtns = rrbService.list(Restrictions.eq("roleId", roleId));
		for(Resource res : roleResources){
			JSONObject jRes = JSONUtil.toJsonObject(res,"roles");
			jRes.element("iconCls", res.getIcon());
			jRes.element("_parentId", res.getParentId()==0?"":res.getParentId());
			//???????????????????????????
			jRes.element("buttons", getResourceButtons(roleResBtns, res));
			jRoleResources.add(jRes);
		}
		resMap.put("total", jRoleResources.size());
		resMap.put("rows", jRoleResources);
		return resMap;
	}
	public List<Button> getResourceButtons(List<RoleResourceButton> roleResBtns,Resource res){
		List<Button> btnList = new ArrayList<Button>();
		List<Integer> roleBtnIds = new ArrayList<Integer>(); 
		for(RoleResourceButton rrb : roleResBtns){
			if(rrb.getResourceId() == res.getId()){//????????????
				roleBtnIds.add(rrb.getButtonId());
			}
		}
		if(ObjectUtil.isNotEmpty(roleBtnIds)){
			for(Button btn : res.getButtons()){
				for(Integer btnid : roleBtnIds){
					if(btn.getId() == btnid){
						btnList.add(btn);
						break;
					}
				}
			}
		}
		//????????????????????????
		//ReflectUtil.removeLazyProperty(btnList);
		return btnList;
	}
	/**
	 * ??????????????????
	 * @param request
	 * @param ??????Id
	 * @param res
	 * @return
	 */
	@RequestMapping(value="/doAssignResource",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAssignResource(HttpServletRequest request,Integer roleid,String res){
		return roleService.executeAssignResource(request,getCurrentUser(),roleid, res);
	}
	/**
	 * ???????????????????????????
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/isSystemRole/{id}",method={RequestMethod.POST})
	@ResponseBody
	public boolean isSystemRole(@PathVariable Integer id){
		Role role = roleService.getById(id);
		if(ObjectUtil.isNotNull(role) && role.getName().equals(getApplicationCommon("SystemRole")))
			return true;
		else
			return false;
	}
	/**
	 * ??????????????????????????????
	 * @param request
	 * @param roleid
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value="/doAssignUserForDelete",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAssignUserForDelete(HttpServletRequest request,Integer roleid,Integer[] userIds){
		if(ObjectUtil.isEmpty(userIds)){
			return WebResult.error ("??????????????????!");
		}else{
			return roleService.executeAssignUser(request,getCurrentUser(),roleid,userIds,1);
		}
	}
	/**
	 * ???????????????????????????
	 * @param request
	 * @param roleid
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value="/doAssignUserForAddCopy",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAssignUserForAddCopy(HttpServletRequest request,Integer roleid,Integer[] userIds){
		if(ObjectUtil.isEmpty(userIds)){
			return WebResult.error("??????????????????!");
		}else{
			return roleService.executeAssignUser(request,getCurrentUser(),roleid,userIds,2);
		}
	}
	@RequestMapping(value="/doAssignUserForAddMove",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAssignUserForAddMove(HttpServletRequest request,Integer roleid,Integer[] userIds){
		if(ObjectUtil.isEmpty(userIds)){
			return WebResult.error("??????????????????!");
		}else{
			return roleService.executeAssignUser(request,getCurrentUser(),roleid,userIds,3);
		}
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
			jResult = roleService.executeLockOrUnLockUser(ids, type);
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
	@InitBinder
	public void initBinder(WebDataBinder binder,WebRequest request){
		binder.registerCustomEditor(RoleType.class, new RoleTypeEditer());
	}
}
