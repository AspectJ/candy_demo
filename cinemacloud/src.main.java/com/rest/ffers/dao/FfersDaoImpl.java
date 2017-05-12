package com.rest.ffers.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.cp.bean.FfersBean;
import com.cp.filter.BaseDao;

@Controller("ffersDao")
public class FfersDaoImpl extends BaseDao{
	
	/**
	 * 新增回盘批次
	 * @param paramsMap
	 */
	public Map<String, Object> insertFfersBatch(Map<String, Object> paramsMap) {
		getSqlSession().insert("ffers.insertFfersBatch", paramsMap);
		return paramsMap;
	}
	

	/**
	 * 删除回盘批次
	 * @param paramsMap
	 */
	public void deleteFfersBatch(Map<String, Object> paramsMap) {
		getSqlSession().delete("ffers.deleteFfersBatch", paramsMap);
	}
	
	/**
	 * 更新回盘批次信息
	 * @param paramsMap
	 */
	public void updateFfersBatch(Map<String, Object> paramsMap){
		getSqlSession().update("ffers.updateFfersBatch", paramsMap);
	}
	

	/**
	 * 查询回盘批次总数
	 * @param paramsMap
	 * @return
	 */
	public int queryFfersBatchCount(Map<String, Object> paramsMap) {
		return getSqlSession().selectOne("ffers.queryFfersBatchCount",paramsMap);
	}
	
	/**
	 * 查询回盘批次列表
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> queryFfersBatchList(Map<String, Object> paramsMap){
		return getSqlSession().selectList("ffers.queryFfersBatchList", paramsMap);
	}
	
	/**
	 * 批量插入回盘通知
	 * @param insertList
	 * @return
	 */
	public List<FfersBean> insertBatch(List<FfersBean> insertList){
		getSqlSession().insert("ffers.insertFfersList", insertList);
		return insertList;
	}

	/**
	 * 查询回盘通知列表
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> queryFfersList(Map<String, Object> paramsMap) {
		return getSqlSession().selectList("ffers.queryFfersList", paramsMap);
	}

	/**
	 * 查询回盘通知总数
	 * @param paramsMap
	 * @return
	 */
	public int queryFfersListCount(Map<String, Object> paramsMap) {
		return getSqlSession().selectOne("ffers.queryFfersListCount",paramsMap);
	}

	/**
	 * 新增回盘通知
	 * @param paramsMap
	 * @return
	 */
	public Map<String, Object> insertFfers(Map<String, Object> paramsMap) {
		getSqlSession().insert("ffers.insertFfers", paramsMap);
		return paramsMap;
	}
	
	/**
	 * 删除回盘通知
	 * @param paramsMap
	 */
	public void deleteFfers(Map<String, Object> paramsMap) {
		getSqlSession().delete("ffers.deleteFfers", paramsMap);
	}
	
	/**
	 * 更新回盘通知
	 * @param paramsMap
	 */
	public void updateFfers(Map<String, Object> paramsMap) {
		getSqlSession().update("ffers.updateFfers", paramsMap);
		
	}


	/**
	 * 批量查询回盘通知
	 * @param ids
	 * @return
	 */
	public List<Map<String, Object>> queryFfersListByIds(List<String> ids) {
		return getSqlSession().selectList("ffers.queryFfersListByIds",ids);
	}

	/**
	 * 批量删除回盘通知
	 * @param ids
	 */
	
	public void removeFfersList(List<String> ids){
		getSqlSession().delete("ffers.deleteFfersList",ids );
	}


	/**
	 * 批量更新回盘通知状态
	 * @param parmasList
	 */
	public void updateFfersByIds(List<Map<String, Object>> parmasList) {
		getSqlSession().update("ffers.updateFfersByIds", parmasList);
		
	}

	public List<Map<String,Object>> getUserid(List<Integer> ffersids){
		return getSqlSession().selectList("ffers.getUserid",ffersids);
	}


	
	
}
