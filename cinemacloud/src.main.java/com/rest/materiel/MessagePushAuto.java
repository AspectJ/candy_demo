package com.rest.materiel;

import com.cp.exception.CustomException;
import com.cp.util.DateFormatUtil;
import com.redis.UserRedisImpl;
import com.rest.materiel.dao.MaterielDaoImpl;
import com.wx.WXTools;
import org.apache.commons.lang.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.*;

/**
 * 定时任务 对于发货五天没有确认收货的，推送一条确认收货链接
 * 
 * @author 29632
 *
 */
public class MessagePushAuto extends QuartzJobBean {

	@Autowired
	private MaterielDaoImpl materielDao;
	@Autowired
	private WXTools wxTools;
	@Autowired
	private UserRedisImpl userRedis;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

		String access_token = userRedis.getWXToken();

		Map<String, Object> paramsMap = new HashMap<String, Object>();
		Date nowDate = new Date();
		String s_time = DateFormatUtil.to_yyyy_MM_dd_str(DateUtils.addDays(nowDate, -6));
		String e_time = DateFormatUtil.to_yyyy_MM_dd_str(DateUtils.addDays(nowDate, -5));
		paramsMap.put("s_time", s_time);
		paramsMap.put("e_time", e_time);
		List<Map<String, Object>> resultlist = materielDao.queryMaterielList(paramsMap);
		// 获取已经关注企业号的员工
		Map<Object, Object> usermap = wxTools.getUser(access_token, 1, 1, 1);
		List<String> useridlist = new ArrayList<String>();
		for (Map<String, Object> map : resultlist) {
			String title = (String) map.get("suppliesconfname");
			String description = (String) map.get("content");
			String userid = (String) map.get("userid");
			boolean boo = usermap.containsKey(userid);
			if (!boo) {
				useridlist.add(userid);
				continue;
			}
				String redirectUrl = "www.baidu.com";
			try {
				wxTools.sendMsg(userid, access_token, title, description, redirectUrl,"ydp");
			} catch (CustomException e) {
				e.printStackTrace();
			}
		}
	}

}
