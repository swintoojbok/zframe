package org.zframework.web.controller.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.app.common.ApplicationCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.web.entity.User;
import org.zframework.web.entity.type.IPRoleType;
import org.zframework.web.service.admin.LogService;
import org.zframework.web.service.admin.LoginService;

@Controller
@RequestMapping(value="/admin/login")
public class LoginController {
	@Autowired
	private LoginService loginService;
	@Autowired
	private LogService logService;
	
	/**
	 * 转向登陆页面
	 * @return
	 */
	@RequestMapping(method={RequestMethod.GET})
	public String login(){
		return "admin/login";
	}
	@RequestMapping(value="/doLogin",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doLogin(String username,String password,HttpSession session,HttpServletRequest request){
		JSONObject loginResult = new JSONObject();
		if(AllowIp(request.getRemoteAddr())){//判断IP是否允许登陆
			User user = loginService.login(username, password);
			if(ObjectUtil.isNull(user)){
				loginResult.element("msg", "用户不存在！");
				loginResult.element("errorType", "USERNAME");
				loginResult.element("result", false);
			}else if(user.getEnabled()!=0){
				logService.recordInfo("登录","账户被锁定！", username, request.getRemoteAddr());
				loginResult.element("msg", "账户被锁定！");
				loginResult.element("errorType", "USERNAME");
				loginResult.element("result", false);
			}else if(!password.equals(user.getPassWord())){
				String msg = "密码不正确！";
				if(ObjectUtil.isNotNull(session.getAttribute(ApplicationCommon.LOGIN_DENIED_NUMBER))){
					int loginDeniedNumber = Integer.parseInt(session.getAttribute(ApplicationCommon.LOGIN_DENIED_NUMBER).toString());
					if(loginDeniedNumber == 1){
						msg = "密码不正确，还有2次机会!";
						session.setAttribute(ApplicationCommon.LOGIN_DENIED_NUMBER, 2);
					}else if(loginDeniedNumber == 2){
						msg = "密码不正确，还有最后1次机会!";
						session.setAttribute(ApplicationCommon.LOGIN_DENIED_NUMBER, 3);
					}else if(loginDeniedNumber >= 3){
						msg = "密码不正确，连续错误4次，账户被锁定!";
						session.removeAttribute(ApplicationCommon.LOGIN_DENIED_NUMBER);
						loginService.lockUser(user);
					}else{
						session.setAttribute(ApplicationCommon.LOGIN_DENIED_NUMBER, loginDeniedNumber+1);
					}
				}else{
					session.setAttribute(ApplicationCommon.LOGIN_DENIED_NUMBER, 1);
				}
				loginResult.element("msg", msg);
				loginResult.element("errorType", "PASSWORD");
				loginResult.element("result", false);
			}else{
				loginService.updateLastInfo(user, request.getRemoteAddr());
				logService.recordInfo("登陆系统","成功", username, request.getRemoteAddr());
				session.setAttribute(ApplicationCommon.SESSION_ADMIN, user);
				session.removeAttribute(ApplicationCommon.LOGIN_DENIED_NUMBER);//移除登录出错标识
				//判断这个账户之前有没有登陆，如果有登陆之前的账户下线
				HttpSession beforeSession = ApplicationCommon.LOGIN_SESSIONS.get(user.getLoginName());
				if(ObjectUtil.isNotNull(beforeSession)){
					if(!beforeSession.equals(session)){//如果不是同一个session
						logService.recordInfo("退出系统", "成功(异地登陆，被迫下线!)", username, request.getRemoteAddr());
						try{
							beforeSession.removeAttribute(ApplicationCommon.SESSION_ADMIN);//之前的session失效
						}catch(Exception e){
							
						}
						ApplicationCommon.LOGIN_SESSIONS.remove(user.getLoginName());
					}
				} 
				ApplicationCommon.LOGIN_SESSIONS.put(user.getLoginName(), session);
				loginResult.element("result", true);
			}
		}else{
			loginResult.element("msg", "IP地址禁止访问系统，请联系管理员！");
			loginResult.element("errorType", "Other");
			loginResult.element("result", false);
		}
		return loginResult;
	}
	/**
	 * 判断登陆的IP是否被允许
	 * 如果IP安全规则关闭返回true
	 * @param ip
	 * @return
	 */
	private boolean AllowIp(String ip){
		if("0:0:0:0:0:0:0:1".equals(ip)){
			ip = "127.0.0.1";
		}
		//如果ip安全规则关闭怎返回true
		if(ApplicationCommon.IPROLE){
			//判断验证方式
			if(ApplicationCommon.IPROLETYPE == IPRoleType.Allow){//如果是只允许列表中的IP登陆
				if(ApplicationCommon.IP_LIST.size()==0)
					return true;
				else
					return ApplicationCommon.IP_LIST.contains(ip);
			}else if(ApplicationCommon.IPROLETYPE == IPRoleType.Deny){//如果是不允许列表中的IP登陆
				if(ApplicationCommon.IP_LIST.size()==0)
					return true;
				else
					return !ApplicationCommon.IP_LIST.contains(ip);
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	/**
	 * 退出系统
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/loginOut",method={RequestMethod.GET})
	public String loginOut(HttpSession session,HttpServletRequest request){
		User user = (User) session.getAttribute(ApplicationCommon.SESSION_ADMIN);
		if(user!=null){
			String username = user.getLoginName();
			session.removeAttribute(ApplicationCommon.SESSION_ADMIN);
			//记录退出日志
			logService.recordInfo("退出系统","成功(用户选择退出)", username, request.getRemoteAddr());
		}
		return "redirect:/admin/login";
	}
}
