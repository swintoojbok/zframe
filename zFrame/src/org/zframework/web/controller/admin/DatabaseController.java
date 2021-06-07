package org.zframework.web.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.zframework.web.controller.BaseController;
import org.zframework.web.service.admin.LogService;

@Controller
@RequestMapping("/admin/database")
public class DatabaseController extends BaseController<Object>{
	@Autowired
	private LogService logService;
	
	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		logService.recordInfo("查询数据库管理","成功",getCurrentUser().getLoginName() , getRequest().getRemoteAddr());
		return "admin/database/index";
	}
}
