package org.zframework.web.service.admin.nethsoft;

import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.web.support.WebResult;
import org.zframework.web.entity.nethsoft.Payment;
import org.zframework.web.service.BaseService;

@Service
public class AccountService extends BaseService<Payment>{
	public Map<String, Object> footerList(Map<String, Object> map) {
		Object[] param = getBaseDao().getMuitColumns("select sum(income),sum(pay) from Payment", null);
		JSONArray jArray = new JSONArray();
		if(ObjectUtil.isNotNull(param[0])){
			jArray.add(new JSONObject().element("descript", "总计").element("income", param[0]).element("pay", param[1]).element("balance", Float.parseFloat(param[0].toString())-Float.parseFloat(param[1].toString())));
		}else{
			jArray.add(new JSONObject().element("descript", "总计").element("income", 0).element("pay", 0).element("balance", 0));
		}
		map.put("footer",jArray);
		return map;
	}
	//收入
	public JSONObject executeIncome(Payment payment) {
		Object balance = getBaseDao().getSingleColumn(Float.class, "select balance from Payment order by datetime desc", null);
		if(ObjectUtil.isNull(balance)){
			payment.setBalance(payment.getIncome());
		}else{
			Object[] param = getBaseDao().getMuitColumns("select sum(income),sum(pay) from Payment", null);
			payment.setBalance(Float.parseFloat(param[0].toString())-Float.parseFloat(param[1].toString()) + payment.getIncome());
		}
		payment.setPay(0);
		payment.setTakeUser(getCurrentUser());
		this.save(payment);
		return WebResult.success();
	}
	//支出
	public JSONObject executePay(Payment payment) {
		Object balance = getBaseDao().getSingleColumn(Float.class, "select balance from Payment order by datetime desc", null);
		if(ObjectUtil.isNull(balance)){
			payment.setBalance(-payment.getPay());
		}else{
			Object[] param = getBaseDao().getMuitColumns("select sum(income),sum(pay) from Payment", null);
			payment.setBalance(Float.parseFloat(param[0].toString())-Float.parseFloat(param[1].toString()) - payment.getPay());
		}
		payment.setIncome(0);
		payment.setTakeUser(getCurrentUser());
		this.save(payment);
		return WebResult.success();
	}
}
