package com.rest.content.information.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cp.bean.ArchiveInfoCustom;
import com.cp.bean.Information;
import com.cp.filter.BaseDao;

@Repository("informationDao")
public class InformationDaoImpl extends BaseDao {

	/**
	 * 添加information
	 * @param paramsMap
	 */
	public void addInformation(Map<String, Object> paramsMap) {
		this.getSqlSession().insert("information.addInformation", paramsMap);
	}
	
	/**
	 * 添加归档信息
	 * @param paramsMap
	 */
	public void addArchive(Map<String, Object> paramsMap) {
		this.getSqlSession().insert("information.addArchive", paramsMap);
	}
	
	/**
	 * 验证information重复性（根据title）
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> checkRepeat(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("information.checkRepeat", paramsMap);
	}
	
	/**
	 * 更改information
	 * @param paramsMap
	 */
	public void updateInformation(Map<String, Object> paramsMap) {
		this.getSqlSession().update("information.updateInformation", paramsMap);
	}

	/**
	 * 更改归档信息
	 * @param paramsMap
	 */
	public void updateArchive(Map<String, Object> paramsMap) {
		this.getSqlSession().update("information.updateArchive", paramsMap);
	}

	/**
	 * 删除information
	 * @param info_id
	 */
	public void deleteInformation(String info_id) {
		this.getSqlSession().delete("information.deleteInformation", info_id);
	}

	/**
	 * 删除information的同时删除其归档属性
	 * @param info_id
	 */
	public void deleteArchive(String info_id) {
		this.getSqlSession().delete("information.deleteArchive", info_id);
	}

	/**
	 * 更改information可用状态(0禁用， 1可用)
	 * @param paramsMap
	 */
	public void auditInformationStatus(Map<String, Object> paramsMap) {
		this.getSqlSession().update("information.auditInformationStatus", paramsMap);
	}

	/**
	 * 查找当前information的归档信息
	 * @param info_id
	 * @return
	 */
	public List<ArchiveInfoCustom> findInformationArchive(String info_id) {
		return this.getSqlSession().selectList("information.findInformationArchive", info_id);
	}
	
	/**
	 * 根据id查询info
	 * @param info_id
	 * @return
	 */
	public Map<String, Object> getInfoByInfoId(String info_id) {
		return this.getSqlSession().selectOne("information.getInfoByInfoId", info_id);
	}

	/**
	 * 根据归档查看用户能够查看某一栏目的所有info
	 * @param theaterid
	 * @return
	 */
	public List<Information> getInfoListByArchiveAndProId(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("information.getInfoListByArchiveAndProId", paramsMap);
	}
	
	/**
	 * 查询count
	 * @param paramsMap
	 * @return
	 */
	public Integer getInfoListByArchiveAndProIdCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("information.getInfoListByArchiveAndProIdCount", paramsMap);
	}

	/**
	 * 查看某一栏目所有的information
	 * @return
	 */
	public List<Information> getAllInfoByProId(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("information.getAllInfoByProId", paramsMap);
	}
	
	/**
	 * 查询count
	 * @param paramsMap
	 * @return
	 */
	public Integer getAllInfoByProIdCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("information.getAllInfoByProIdCount", paramsMap);
	}

	/**
	 * 如果登陆用户是影院用户(roletype==2) ,则根据归档查看所有可看的info列表(查询所有栏目的info)
	 * @param theaterid
	 * @return
	 */
	public List<Information> findAvaiableInfoList(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("information.findAvaiableInfoList", paramsMap);
	}
	
	/**
	 * 查询count
	 * @param i
	 * @return
	 */
	public Integer findAvaiableInfoListCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("information.findAvaiableInfoListCount", paramsMap);
	}

	/**
	 * 如果登陆用户是系统用户或者院线用户，则可以查看所有栏目的所有info
	 * @return
	 */
	public List<Information> getAllInfoList(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("information.getAllInfoList", paramsMap);
	}
	
	/**
	 * 查询count
	 * @return
	 */
	public Integer getAllInfoListCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("information.getAllInfoListCount", paramsMap);
	}

	/**
	 * 查找影院用户能够看到的最新的文章列表(自该用户上次登录之后)
	 * @param paramsMap
	 * @return
	 */
	public List<Information> findLatestAvaiableInfoList(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("information.findLatestAvaiableInfoList", paramsMap);
	}

	/**
	 * 查找count
	 * @param paramsMap
	 * @return
	 */
	public Integer findLatestAvaiableInfoListCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("information.findLatestAvaiableInfoListCount", paramsMap);
	}

	/**
	 * 查找系统/院线用户能够看到的最新的文章列表（自上次登录之后的info列表(不是自己添加的)）
	 * @param paramsMap
	 * @return
	 */
	public List<Information> getLatestAllInfoList(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("information.getLatestAllInfoList", paramsMap);
	}

	/**
	 * 查找count
	 * @param paramsMap
	 * @return
	 */
	public Integer getLatestAllInfoListCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("information.getLatestAllInfoListCount", paramsMap);
	}


}
