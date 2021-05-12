package org.zframework.web.controller.admin;

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
import org.zframework.app.common.ControllerCommon;
import org.zframework.core.propertyEditor.RoleTypeEditer;
import org.zframework.core.util.JSONUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.ReflectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.model.pagination.PageBean;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.Button;
import org.zframework.web.entity.Resource;
import org.zframework.web.entity.Role;
import org.zframework.web.entity.RoleResourceButton;
import org.zframework.web.entity.type.RoleType;
import org.zframework.web.service.admin.LogService;
import org.zframework.web.service.admin.ResourceService;
import org.zframework.web.service.admin.RoleResBtnService;
import org.zframework.web.service.admin.RoleService;

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
	 * 用户管理首页
	 * @param model
	 * @return
	 */
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model){
		logService.recordInfo("查询角色","成功",getCurrentUser().getLoginName() , getRequest().getRemoteAddr());
		return "admin/role/index";
	}
	/**
	 * 获取角色列表
	 * @param pageBean
	 * @return
	 */
	@RequestMapping(value="/roleList",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> roleList(PageBean pageBean,String name,String value){
		return list(pageBean, name, value, roleService);
	}
	/**
	 * 获取角色列表
	 * 提供给添加和编辑用户界面分配角色时调用
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
		//读取所有已经启用的用户
		pageBean.addCriterion(Restrictions.eq("enabled", 0));
		//如果是超级管理员则显示最高用户，否则不可分配
		if(!isSystemadmin()){
			pageBean.addCriterion(Restrictions.not(Restrictions.eq("type", RoleType.SYSTEM)));
		}
		List<Role> roleList = roleService.list(pageBean.getCriterions().toArray(new Criterion[]{}));
		roleList = ReflectUtil.removeLazyProperty(roleList);
		roleMap.put("rows", roleList);
		roleMap.put("total", roleList.size());
		return roleMap;
	}
	@RequestMapping(value="/add",method={RequestMethod.GET})
	public String toAdd(){
		return "admin/role/add";
	}
	@RequestMapping(value="/doAdd",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAdd(HttpServletRequest request,@Valid @ModelAttribute("role")Role role,BindingResult result){
		JSONObject jResult = new JSONObject();
		if(result.hasErrors()){
			jResult.element("isSaved", false);
			jResult.element("error", "请按要求填写表单!");
			//记录日志
			logService.recordInfo("新增角色","失败(非法提交表单!)",getCurrentUser().getLoginName() , request.getRemoteAddr());
		}else{
			Role eqRole = roleService.getByName(role.getName());
			if(ObjectUtil.isNull(eqRole)){
				roleService.save(role);
				jResult.element("isSaved", true);
				//记录日志
				logService.recordInfo("新增角色","成功",getCurrentUser().getLoginName() , request.getRemoteAddr());
			}else{
				jResult.element("isSaved", false);
				jResult.element("error", "该角色已经存在!");
				//记录日志
				logService.recordInfo("新增角色","失败(尝试添加已经存在的角色!)",getCurrentUser().getLoginName() , request.getRemoteAddr());
			}
		}
		return jResult;
	}
	@RequestMapping(value="/doDelete",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doDelete(HttpServletRequest request,@RequestParam Integer[] ids){
		JSONObject jResult = new JSONObject();
		if(!isAllowAccess()){
			jResult.element("NeedVerifyPassword", true);
		}else{
			//记录日志
			List<Role> roleList = roleService.getByIds(ids);
			for(Role role : roleList){
				if(ObjectUtil.isNull(role)){
					jResult.element("isDeleted", false);
					jResult.element("error", "尝试删除不存在的角色");
					logService.recordInfo("删除角色","失败（尝试删除不存在的角色）",getCurrentUser().getLoginName() , request.getRemoteAddr());
					break;
				}else if(role.getType() == RoleType.SYSTEM){
					jResult.element("isDeleted", false);
					jResult.element("error", "系统角色不可删除");
					logService.recordInfo("删除角色","失败（拒绝删除系统角色）",getCurrentUser().getLoginName() , request.getRemoteAddr());
					break;
				}else{
					roleService.delete(role);
					jResult.element("isDeleted", true);
					logService.recordInfo("删除角色","成功（角色ID:"+role.getId()+",角色名称:"+role.getName()+")",getCurrentUser().getLoginName() , request.getRemoteAddr());
				}
			}
		}
		return jResult;
	}
	/**
	 * 转到编辑角色页面
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
		return "admin/role/edit";
	}
	@RequestMapping(value="/doEdit",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEdit(HttpServletRequest request,@Valid @ModelAttribute("role")Role role,BindingResult result){
		return roleService.executeEdit(request,getCurrentUser(),role, result);
	}
	/**
	 * 转向分配资源页面
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value="/assignResource/{roleId}",method={RequestMethod.GET})
	public String assignResource(Model model,@PathVariable Integer roleId){
		Role role = roleService.getById_NoLazyResourceAndButtons(roleId);
		if(ObjectUtil.isNotNull(role) && !role.getName().equals(getApplicationCommon("SystemRole"))){
			//获取角色拥有的资源
			List<Resource> roleResources = role.getResources();
			//获取角色的资源及按钮权限
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
			return "admin/role/assignResource";
		}else{
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		}
	}
	/**
	 * 获取角色资源
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value="/roleResource/{roleId}",method={RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> roleResource(@PathVariable("roleId") Integer roleId){
		Map<String,Object> resMap = new HashMap<String, Object>();
		List<Resource> roleResources = new ArrayList<Resource>();
		List<JSONObject> jRoleResources = new ArrayList<JSONObject>();
		//获取所有可用的资源
		roleResources = resService.list_noLazyButtons(new Criterion[]{Restrictions.eq("enabled", 0)},Order.asc("location"),Order.asc("parentId"));
		//获取角色的资源及按钮权限
		List<RoleResourceButton> roleResBtns = rrbService.list(Restrictions.eq("roleId", roleId));
		for(Resource res : roleResources){
			JSONObject jRes = JSONUtil.toJsonObject(res,"roles");
			jRes.element("iconCls", res.getIcon());
			jRes.element("_parentId", res.getParentId()==0?"":res.getParentId());
			//获取角色的按钮权限
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
			if(rrb.getResourceId() == res.getId()){//筛选资源
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
		//移除延迟加载属性
		ReflectUtil.removeLazyProperty(btnList);
		return btnList;
	}
	/**
	 * 执行分配资源
	 * @param request
	 * @param 角色Id
	 * @param res
	 * @return
	 */
	@RequestMapping(value="/doAssignResource",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAssignResource(HttpServletRequest request,Integer roleid,String res){
		return roleService.executeAssignResource(request,getCurrentUser(),roleid, res);
	}
	/**
	 * 判断是否是系统角色
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
	 * 执行从角色中移除用户
	 * @param request
	 * @param roleid
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value="/doAssignUserForDelete",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAssignUserForDelete(HttpServletRequest request,Integer roleid,Integer[] userIds){
		JSONObject jResult = new JSONObject();
		if(ObjectUtil.isEmpty(userIds)){
			jResult.element("isAssign", false);
			jResult.element("error", "用户列表为空!");
		}else{
			jResult = roleService.executeAssignUser(request,getCurrentUser(),roleid,userIds,1);
		}
		return jResult;
	}
	/**
	 * 执行添加用户到角色
	 * @param request
	 * @param roleid
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value="/doAssignUserForAddCopy",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAssignUserForAddCopy(HttpServletRequest request,Integer roleid,Integer[] userIds){
		JSONObject jResult = new JSONObject();
		if(ObjectUtil.isEmpty(userIds)){
			jResult.element("isAssign", false);
			jResult.element("error", "用户列表为空!");
		}else{
			jResult = roleService.executeAssignUser(request,getCurrentUser(),roleid,userIds,2);
		}
		return jResult;
	}
	@RequestMapping(value="/doAssignUserForAddMove",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAssignUserForAddMove(HttpServletRequest request,Integer roleid,Integer[] userIds){
		JSONObject jResult = new JSONObject();
		if(ObjectUtil.isEmpty(userIds)){
			jResult.element("isAssign", false);
			jResult.element("error", "用户列表为空!");
		}else{
			jResult = roleService.executeAssignUser(request,getCurrentUser(),roleid,userIds,3);
		}
		return jResult;
	}
	@InitBinder
	public void initBinder(WebDataBinder binder,WebRequest request){
		binder.registerCustomEditor(RoleType.class, new RoleTypeEditer());
	}
}
