package org.zframework.web.service.admin.work;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.zframework.core.util.DateUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.web.entity.work.Plan;
import org.zframework.web.service.BaseService;

@Service
public class PlanService extends BaseService<Plan>{
	/**
	 * 检查状态
	 */
	public void executeCheckState() {
		String today = DateUtil.format(new Date(), "yyyy-MM-dd");
		this.executeHQL("update Plan set state=5 where state<>3 and state<>4 and endTime<'"+today+"'");
		this.executeHQL("update Plan set state=2 where state<>3 and state<>4 and endTime>='"+today+"' and startTime<='"+today+"'");
		this.executeHQL("update Plan set state=1 where state<>3 and state<>4 and startTime>'"+today+"'");
	}

	public void updateState(Integer[] ids, int state){
		this.executeHQL("update Plan set state="+state+",completeTime='"+DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")+"' where state<>"+state+" and id in ("+StringUtil.toString(ids)+")");
	}
}
