package org.zframework.web.controller.desktop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin/desktop")
public class HomeController {
	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		return "desktop/index";
	}
}
