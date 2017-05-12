package com.rest.materiel.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.cp.bean.SuppliesBean;
import com.cp.filter.BaseDao;

@Controller("materielDao")
public class MaterielDaoImpl extends BaseDao{
	
	/**
	 * 新增物料批次
	 * @param paramsMap
	 */
	public Map<String, Object> insertMaterielBatch(Map<String, Object> paramsMap) {
		getSqlSession().insert("materiel.insertMaterielBatch", paramsMap);
		return paramsMap;
	}
	
	/**
	 * 更新物料批次信息
	 * @param paramsMap
	 */
	public Map<String, Object> updateMaterielBatch(Map<String, Object> paramsMap){
		getSqlSession().update("materiel.updateMaterielBatch", paramsMap);
		return paramsMap;
	}
	
	/**
	 * 删除物料批次
	 * @param paramsMap
	 */
	public void deleteMaterielBatch(Map<String, Object> paramsMap) {
		getSqlSession().delete("materiel.deleteMaterielBatch", paramsMap);
	}
	
	/**
	 * 查询物料批次总数
	 * @param paramsMap
	 * @return
	 */
	public int queryMaterielBatchCount(Map<String, Object> paramsMap) {
		return getSqlSession().selectOne("materiel.queryMaterielBatchCount",paramsMap);
	}
	
	/**
	 * 查询物料批次列表
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> queryMaterielBatchList(Map<String, Object> paramsMap){
		return getSqlSession().selectList("materiel.queryMaterielBatchList", paramsMap);
	}
	
	/**
	 * 批量插入物料分发通知
	 * @param suppliesList
	 * @return
	 */
	public List<SuppliesBean> insertBatch(List<SuppliesBean> suppliesList){
		getSqlSession().insert("materiel.insertMaterielList", suppliesList);
		return suppliesList;
	}

	/**
	 * 查询物料分发通知列表
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> queryMaterielList(Map<String, Object> paramsMap) {
		return getSqlSession().selectList("materiel.queryMaterielList", paramsMap);
	}

	/**
	 * 查询物料分发通知总数
	 * @param paramsMap
	 * @return
	 */
	public int queryMaterielListCount(Map<String, Object> paramsMap) {
		return getSqlSession().selectOne("materiel.queryMaterielListCount",paramsMap);
	}

	/**
	 * 新增物料分发通知
	 * @param paramsMap
	 * @return
	 */
	public Map<String, Object> insertMateriel(Map<String, Object> paramsMap) {
		getSqlSession().insert("materiel.insertMateriel", paramsMap);
		return paramsMap;
	}
	
	/**
	 * 更新物料分发通知
	 * @param paramsMap
	 */
	public Map<String, Object> updateMateriel(Map<String, Object> paramsMap) {
		int num=getSqlSession().update("materiel.updateMateriel", paramsMap);
		if(num==1){
			return paramsMap;
		}
		return null;
	}
	
	/**
	 * 删除物料分发通知
	 * @param paramsMap
	 */
	public void deleteMateriel(Map<String, Object> paramsMap) {
		getSqlSession().delete("materiel.deleteMateriel", paramsMap);
	}

	/**
	 * 批量查询物料分发通知
	 * @param ids
	 * @return
	 */
	public List<Map<String,Object>> queryMaterielListByIds(List<String> ids){
		return  getSqlSession().selectList("materiel.queryMaterielListByIds", ids);
	}

	/**
	 * 批量更新物料分发通知
	 * @param ids
	 */
	public void updateMaterielByIds(List<Map<String,Object>> paramsList) {
		getSqlSession().update("materiel.updateMaterielByIds", paramsList);
		
	}
	
	/**
	 * 查询物料分发通知发送状态
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String,Object>> queryMaterielStatusList(Map<String,Object> paramsMap){
		return getSqlSession().selectList("materiel.queryMaterielStatusList", paramsMap);
		
	}

	/**
	 * 批量删除物料分发通知
	 * @param idlist
	 */
	public void batchDeleteMateriel(List<String> idlist) {
		getSqlSession().delete("materiel.batchDeleteMateriel", idlist);
		
	}


	public List<Map<String,Object>> getUserid(List<Integer> idlist) {
		return getSqlSession().selectList("materiel.getUserid",idlist);
	}
}
