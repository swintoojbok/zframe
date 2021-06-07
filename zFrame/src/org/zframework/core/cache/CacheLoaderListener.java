package org.zframework.core.cache;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.zframework.app.common.ApplicationCommon;
import org.zframework.web.controller.admin.ServerController;
import org.zframework.web.service.admin.CommonService;
import org.zframework.web.service.admin.SafeConfigService;
import org.zframework.web.service.admin.SafeIpService;

/**
 * 加载数据库的数据到内存中
 * @author ZENGCHAO
 *
 */
public class CacheLoaderListener implements ApplicationListener<ApplicationEvent>{
	@Autowired
	private CommonService commonService;
	@Autowired
	private SafeIpService safeIpService;
	@Autowired
	private SafeConfigService scService;
	public void onApplicationEvent(ApplicationEvent event) {
		//判断是否是web容器加载完成后
		if(ApplicationCommon.DATADICT.size()==0){
			//初始化数据字典
			commonService.InitDataDict();
			//初始化IP地址
			safeIpService.initIp();
			//初始化IP规则
			scService.initSafeConfig();
			//记录启动时间
			ServerController.startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
	}
}
