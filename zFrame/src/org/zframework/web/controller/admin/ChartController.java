package org.zframework.web.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.zframework.app.fschart.FSCharts_3DColumn;
import org.zframework.core.web.WebContextHelper;
import org.zframework.web.service.admin.ChartService;

@Controller
@RequestMapping("/admin/chart")
public class ChartController {
	@Autowired
	ChartService chartService;
	
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model){
		FSCharts_3DColumn col = new FSCharts_3DColumn();
		col.setFormatNumberScale(0);
		String str = col.senior_genderCode(chartService.getOperaChart(), "IP访问量", "IP地址", "访问次数", 500, 800, WebContextHelper.getSession());
		model.addAttribute("str", str);
		return "admin/chart/index";
	}
}
