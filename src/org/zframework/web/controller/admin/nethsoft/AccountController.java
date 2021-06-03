package org.zframework.web.controller.admin.nethsoft;

import java.util.Map;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.orm.pagination.PageBean;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.nethsoft.Payment;
import org.zframework.web.service.admin.nethsoft.AccountService;

@Controller
@RequestMapping("/admin/nethsoft/accounting")
public class AccountController extends BaseController<Payment>{
	@Autowired
	private AccountService accService;
	@RequestMapping(method=RequestMethod.GET)
	public String index(){
		return "admin/nethsoft/account/index";
	}
	
	@RequestMapping(value="/list",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> list(PageBean pageBean,String name,String value){
		if(pageBean.getSort().equals("id")){
			pageBean.setSort("datetime");
		}
		Map<String,Object> map = list(pageBean, name, value, accService);
		return accService.footerList(map);
	}
	//收入
	@RequestMapping(value="/income",method=RequestMethod.GET)
	public String income(){
		return "admin/nethsoft/account/income";
	}
	@RequestMapping(value="/doIncome",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject doIncome(Payment payment){
		return accService.executeIncome(payment);
	}
	//支出
	@RequestMapping(value="/pay",method=RequestMethod.GET)
	public String pay(){
		return "admin/nethsoft/account/pay";
	}
	@RequestMapping(value="/doPay",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject doPay(Payment payment){
		return accService.executePay(payment);
	}
}
