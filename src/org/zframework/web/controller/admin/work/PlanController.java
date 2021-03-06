package org.zframework.web.controller.admin.work;

import java.util.Date;
import java.util.Map;
import net.sf.json.JSONObject;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.util.DateUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.web.support.ControllerCommon;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.pagination.PageBean;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.work.Plan;
import org.zframework.web.service.admin.work.PlanService;

@Controller
@RequestMapping("/admin/work/plan")
public class PlanController extends BaseController<Plan>{
	@Autowired
	private PlanService planService;
	@RequestMapping
	public String index(){
		return "admin/work/plan/index";
	}
	@RequestMapping(value="/list",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> list(PageBean pageBean,String name,String value){
		planService.executeCheckState();
		pageBean.addOrder(Order.desc("startTime"));
		if("filterComplete".equals(name)){
			name = null;
			if("true".equals(value))
				pageBean.addCriterion(Restrictions.not(Restrictions.eq("state", 3)));
			value = null;
		}
		return super.list(pageBean, name, value, planService);
	}
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String add(Model model){
		model.addAttribute("cDate", DateUtil.format(new Date(), "yyyy-MM-dd"));
		model.addAttribute("cUserName", getCurrentUser().getRealName());
		return "admin/work/plan/add";
	}
	@RequestMapping(value="/doAdd",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject doAdd(Plan plan){
		//?????????????????????
		plan.setCreateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		plan.setCreateUser(getCurrentUser());
		//????????????????????????????????????????????????state???1????????????2
		if(DateUtil.parseDate(plan.getStartTime()).getTime()>new Date().getTime()){
			plan.setState(1);
		}else{
			plan.setState(2);
		}
		//?????????????????????????????????????????????
		if(DateUtil.parseDate(plan.getEndTime()).getTime()<new Date().getTime()){
			return WebResult.error("????????????????????????????????????!");
		}
		planService.save(plan);
		return WebResult.success();
	}
	@RequestMapping(value="/edit/{id}",method=RequestMethod.GET)
	public String edit(Model model,@PathVariable Integer id){
		Plan plan = planService.getById(id);
		if(ObjectUtil.isNotNull(plan)){
			//???????????????????????????????????????
			if(plan.getState() == 3 || plan.getState() == 4){
				ControllerCommon.CustomError.setError("????????????????????????????????????????????????");
				ControllerCommon.CustomError.setScript("new Windows(\"win_work_plan_edit\").close();");
				return ControllerCommon.CustomError.getViewName();
			}
			model.addAttribute("plan", plan);
			return "admin/work/plan/edit";
		}else{
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		}
	}
	@RequestMapping(value="/doEdit",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject doEdit(Plan plan){
		Plan eqPlan = planService.getById(plan.getId());
		if(ObjectUtil.isNotNull(eqPlan)){
			//????????????????????????????????????????????????state???1????????????2
			if(DateUtil.parseDate(plan.getStartTime()).getTime()>new Date().getTime()){
				eqPlan.setState(1);
			}else{
				eqPlan.setState(2);
			}
			//?????????????????????????????????????????????
			if(DateUtil.parseDate(plan.getEndTime()).getTime()<new Date().getTime()){
				return WebResult.error("????????????????????????????????????!");
			}
			eqPlan.setTitle(plan.getTitle());
			eqPlan.setStartTime(plan.getStartTime());
			eqPlan.setEndTime(plan.getEndTime());
			eqPlan.setExecutor(plan.getExecutor());
			eqPlan.setDescript(plan.getDescript());
			planService.update(eqPlan);
			return WebResult.success();
		}else{
			return WebResult.error("?????????????????????!");
		}
		
	}
	@RequestMapping(value="/doDelete",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject doDelete(Integer[] ids){
		planService.delete(ids);
		return WebResult.success();
	}
	
	@RequestMapping(value="/doComplete",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject doComplete(Integer[] ids){
		if(ObjectUtil.isNull(ids) || ids.length == 0){
			return WebResult.error("????????????????????????????????????!");
		}
		planService.updateState(ids,3);
		return WebResult.success();
	}
	
	@RequestMapping(value="/doClose",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject doClose(Integer[] ids){
		if(ObjectUtil.isNull(ids) || ids.length == 0){
			return WebResult.error("???????????????????????????!");
		}
		planService.updateState(ids,4);
		return WebResult.success();
	}
	@RequestMapping(value="/doNormal",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject doNormal(Integer[] ids){
		if(ObjectUtil.isNull(ids) || ids.length == 0){
			return WebResult.error("????????????????????????????????????!");
		}
		planService.updateState(ids,2);
		return WebResult.success();
	}
	
}
