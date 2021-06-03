package org.zframework.web.controller.weixin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.util.ObjectUtil;
import org.zframework.web.controller.BaseController;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.support.WeiXin;

@Controller
@RequestMapping("/weixin")
public class WeiXinController extends BaseController<Object>{
	@Autowired
	private LogService logService;
	private final static String TOKEN = "NETHSOFT";
	@RequestMapping("/sign")
	@ResponseBody
	public boolean sign(String signature,String timestamp,String nonce,String echostr){
		if(ObjectUtil.isNull(signature) || ObjectUtil.isNull(timestamp) || ObjectUtil.isNull(nonce) || ObjectUtil.isNull(echostr)){
			logService.recordError("微信", "验证错误:signature:"+signature+",timestamp:"+timestamp+",nonce:"+nonce+",echostr:"+echostr, "", getRequestAddr());
			return false;
		}
		logService.recordError("微信", "验证成功:signature:"+signature+",timestamp:"+timestamp+",nonce:"+nonce+",echostr:"+echostr, "", getRequestAddr());
		return WeiXin.access(TOKEN, signature, timestamp, nonce);
	}
}
