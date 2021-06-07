package org.zframework.web.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.app.fschart.FSCharts_2DBar;
import org.zframework.app.fschart.FSCharts_2DLine;
import org.zframework.app.fschart.FSCharts_3DColumn;
import org.zframework.app.fschart.FSCharts_3DPie;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.web.WebContextHelper;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.Resource;
import org.zframework.web.entity.User;
import org.zframework.web.service.admin.UserService;

@Controller
@RequestMapping("/admin/index")
public class IndexController extends BaseController<Object>{
	@Autowired
	private UserService userService;
	
	/**
	 * 加载用户资源
	 * @param model
	 * @param theme
	 * @return
	 */
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model,@RequestParam(required=false,value="theme")String theme){
		User user = getCurrentUser();
		if(ObjectUtil.isNotNull(user)){
			Map<Integer,List<Resource>> userResMap = new HashMap<Integer, List<Resource>>();
			List<Resource> resources = user.getResources();
			List<Resource> firstRes = new ArrayList<Resource>();
			for(Resource res : resources){
				if(res.getParentId()==0){
					firstRes.add(res);
					userResMap.put(res.getId(), getChildRes(resources, res.getId()));
				}
			}
			model.addAttribute("firstRes",firstRes);
			model.addAttribute("resMap",userResMap);
		}
		return "admin/index";
	}
	/**
	 * 获取子资源
	 * @param resources
	 * @param parentId
	 * @return
	 */
	private List<Resource> getChildRes(List<Resource> resources,Integer parentId){
		List<Resource> childRes = new ArrayList<Resource>();
		for(Resource res : resources){
			if(res.getParentId() == parentId){
				childRes.add(res);
			}
		}
		return childRes;
	}
	@RequestMapping(value="/welcome",method={RequestMethod.GET})
	public String welcome(Model model){
		Map<String,String> map = new LinkedHashMap<String, String>();
		map.put("第1项", "100");
		map.put("第2项", "200");
		map.put("第3项", "300");
		map.put("第4项", "400");
		map.put("第5项", "500");
		map.put("第6项", "600");
		map.put("第7项", "700");
		map.put("第8项", "800");
		map.put("第9项", "900");
		map.put("第10项", "1000");
		map.put("第11项", "1100");
		map.put("第12项", "1200");
		List<String> categorys = new ArrayList<String>();
		Map<String,List<String>> dataset = new LinkedHashMap<String, List<String>>();
		categorys.add("奥地利");
		categorys.add("巴西");
		categorys.add("法国");
		categorys.add("德国");
		categorys.add("美国");
		List<String> temp1 = new ArrayList<String>();
		temp1.add("25601.34");
		temp1.add("20148.82");
		temp1.add("17372.76");
		temp1.add("35407.15");
		temp1.add("38105.68");
		List<String> temp2 = new ArrayList<String>();
		temp2.add("57401.85");
		temp2.add("41941.19");
		temp2.add("45263.37");
		temp2.add("117320.16");
		temp2.add("114845.27");
		List<String> temp3 = new ArrayList<String>();
		temp3.add("45000.65");
		temp3.add("44835.76");
		temp3.add("18722.18");
		temp3.add("77557.31");
		temp3.add("92633.68");
		dataset.put("1996", temp1);
		dataset.put("1997", temp2);
		dataset.put("1998", temp3);
		
		FSCharts_3DColumn col = new FSCharts_3DColumn();
		col.setFormatNumberScale(0);
		col.addTrendLines(100, "91C728", "最低值");
		col.addTrendLines(1000, "91C728", "最高值");
		String chart1 = col.single_genderCode(map, "3D柱形图", "X轴", "Y轴", 200, 460, WebContextHelper.getSession());
		
		FSCharts_3DPie pie = new FSCharts_3DPie();
		String chart2 = pie.single_genderCode(map, "3D饼形图", 220, 460, WebContextHelper.getSession());
		
		FSCharts_2DLine  line = new FSCharts_2DLine();
		line.addTrendLines(100, "91C728", "最低值");
		line.addTrendLines(1000, "91C728", "最高值");
		String chart3 = line.single_genderCode(map, "2D折线图", "X轴", "Y轴", 220, 460, WebContextHelper.getSession());
		
		FSCharts_2DBar bar = new FSCharts_2DBar();
		String chart4 = bar.single_genderCode(map, "2D条形图", "X轴", "Y轴", 220, 460, WebContextHelper.getSession());
		
		model.addAttribute("chart1", chart1);
		model.addAttribute("chart2", chart2);
		model.addAttribute("chart3", chart3);
		model.addAttribute("chart4", chart4);
		return "admin/welcome";
	}
	@RequestMapping(value="/changeThemes",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doChangeThemes(String themeName){
		JSONObject jResult = new JSONObject();
		User user = getCurrentUser();
		user.setPageStyle(themeName);
		userService.update(user);
		jResult.element("result", "success");
		return jResult;
	}
}
