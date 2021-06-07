package org.zframework.app.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.zframework.web.entity.type.IPRoleType;

/**
 * 系统全局变量配置类
 * @author ZENGCHAO
 *
 */
public class ApplicationCommon {
	/**
	 * 项目BasePath
	 */
	public static String BASEPATH = "";
	/**
	 * 后台用户session
	 */
	public final static String SESSION_ADMIN = "ADMINSESSION";
	/**
	 * 前台用户session
	 */
	public final static String SESSION_USER = "USERSESSION";
	/**
	 * 系统超级管理员用户名
	 * 不可删除该用户
	 */
	public final static String SYSTEM_ADMIN = "superadmin";
	/**
	 * 加密Key
	 * 没有此Key，无法从密文获取明文密码
	 */
	public final static String DEC_KEY = "zc@zframework";
	
	/**
	 * 登陆验证次数
	 * 存储在Session中的名称
	 */
	public final static String LOGIN_DENIED_NUMBER = "LoginDeniedNumber";
	/**
	 * 是否验证通过
	 * 用于执行关键性操作且需要验证用户密码
	 * 存储在Session中的名称
	 */
	public final static String ALLOW_ACCESS = "AllowAccess";
	/**
	 * 系统公用型数据
	 */
	public static Map<String,String> DATADICT = new HashMap<String, String>();
	/**
	 * 系统登陆的用户名集合
	 */
	public static Set<String> LOGIN_USERS = new HashSet<String>();
	/**
	 * 记录已经登陆的用户
	 */
	public static Map<String,HttpSession> LOGIN_SESSIONS = new HashMap<String, HttpSession>();
	/**
	 * IP列表
	 */
	public static List<String> IP_LIST = new ArrayList<String>();
	/**
	 * IP规则开关
	 */
	public static boolean IPROLE = false;
	/**
	 * ip安全侧率
	 * Allow： 只允许列表中的IP登陆
	 * Deny: 不允许列表中的IP登陆
	 */
	public static IPRoleType IPROLETYPE = IPRoleType.Deny;
}