package com.rest.bill.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cp.bean.BillBean;
import com.cp.filter.BaseDao;


@Service("billDao")
public class BillDaoImpl extends BaseDao{


	/**
	 * 插入账单批次
	 * @param paramsMap
	 * @return
	 */
	public Map<String, Object> saveBillconf(Map<String, Object> paramsMap) {
		int num=getSqlSession().insert("bill.saveBillconf", paramsMap);
		if(num==1){
			return paramsMap;
		}
		return null;
	}

	/**
	 * 更新账单批次
	 * @param paramsMap
	 * @return
	 */
	public Map<String, Object> updateBillconf(Map<String, Object> paramsMap) {
		int num=getSqlSession().update("bill.updateBillconf", paramsMap);
		if(num==1){
			return paramsMap;
		}
		return null;
	}

	/**
	 * 删除账单批次
	 * @param paramsMap
	 * @return
	 */
	public Integer removeBillconf(Map<String, Object> paramsMap) {
		int num=getSqlSession().delete("bill.removeBillconf", paramsMap);
		if(num>0){
			return num;
		}
		return null;
	}

	/**
	 * 查询账单批次总数
	 * @param paramsMap
	 * @return
	 */
	public int getBillconfTotal(Map<String,Object> paramsMap){
		int num=getSqlSession().selectOne("bill.getBillconfListTotal", paramsMap);
		return num;
	}
	
	/**
	 * 查询账单批次
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> getBillconfList(Map<String, Object> paramsMap) {
		List<Map<String,Object>> resultlist=getSqlSession().selectList("bill.getBillconfList", paramsMap);
		return resultlist;
	}

	/**
	 * 新增账单
	 * @param paramsMap
	 * @return
	 */
	public Map<String, Object> saveBill(Map<String, Object> paramsMap) {
		int num=getSqlSession().insert("bill.saveBill",paramsMap);
		if(num==1){
			return paramsMap;
		}
		return null;
	}

	/**
	 * 更新账单
	 * @param paramsMap
	 * @return
	 */
	public Map<String, Object> updateBill(Map<String, Object> paramsMap) {
		int num=getSqlSession().update("bill.updateBill", paramsMap);
		if(num==1){
			return paramsMap;
		}
		return null;
	}

	/**
	 * 删除账单
	 * @param paramsMap
	 */
	public Integer removeBill(Map<String, Object> paramsMap) {
		int num=getSqlSession().delete("bill.removeBill", paramsMap);
		if(num==1){
			return num;
		}
		return null;
	}
	
	/**
	 * 查询账单列表总数
	 * @param paramsMap
	 * @return
	 */
	public int getBillTotal(Map<String, Object> paramsMap) {
		int num=getSqlSession().selectOne("bill.getBillListTotal", paramsMap);
		return num;
	}

	/**
	 * 查询账单列表
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> getBillList(Map<String, Object> paramsMap) {
		 List<Map<String, Object>> resultList=getSqlSession().selectList("bill.getBillList", paramsMap);
		return resultList;
	}
	
	/**
	 * 批量插入账单
	 * @param billList
	 * @return
	 */
	public List<BillBean> saveBillList(List<BillBean> billList){
		getSqlSession().insert("bill.insertBillList", billList);
		return billList;
	}
	
	public void removeBillList(List<String> idList){
		getSqlSession().delete("bill.deleteBillList", idList);
	}
	
	public List<Map<String, Object>> queryBillListByIds(List<String> ids){
		return getSqlSession().selectList("bill.queryBillListByIds", ids);
	}

	public void updateBillsByIds(List<Map<String, Object>> paramsList) {
		getSqlSession().update("bill.updateBillsByIds", paramsList);
		
	}
	
	 public List<Map<String,Object>> getUserId(Map<String, Object> map) {
			return	getSqlSession().selectList("bill.getUserid",map);
	    }

	

	
}
