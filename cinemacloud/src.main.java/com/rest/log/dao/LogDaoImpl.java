package com.rest.log.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cp.bean.LogInfo;
import com.cp.filter.BaseDao;

@Repository("logDao")
public class LogDaoImpl extends BaseDao {

	/**
	 * 插入日志
	 * @param logInfo
	 */
	public void addLogInfo(LogInfo logInfo) {
		this.getSqlSession().insert("log.addLogInfo", logInfo);
	}
	
	
	/**
	 * 查询最新日志或者全部日志
	 * 		如果 (isNew != null && isNew == 1) ===> 表示查询最新日志
	 * @return
	 */
	public List<Map<String, Object>> getLogList(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("log.getLogList", paramsMap);
	}
	
	
	/**
	 * 查询最新日志或者全部日志count
	 * @return
	 */
	public Integer getLogListCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("log.getLogListCount", paramsMap);
	}
	
}
