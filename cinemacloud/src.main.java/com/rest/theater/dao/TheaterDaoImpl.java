package com.rest.theater.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cp.filter.BaseDao;

@Repository("theaterDao")
public class TheaterDaoImpl extends BaseDao {

	/**
	 * 添加影院信息
	 * @param paramsMap
	 */
	public void addTheater(Map<String, Object> paramsMap) {
		this.getSqlSession().insert("theater.addTheater", paramsMap);
	}
	
	/**
	 * 添加影院的归档属性
	 * @param paramsMap
	 */
	public void addArchive(Map<String, Object> paramsMap) {
		this.getSqlSession().insert("theater.addArchive", paramsMap);
	}

	/**
	 * 更新影院信息
	 * @param paramsMap
	 */
	public void updateTheater(Map<String, Object> paramsMap) {
		this.getSqlSession().update("theater.updateTheater", paramsMap);
	}

	/**
	 * 获取所有影院详细信息(可根据影院名进行模糊查询)
	 */
	public List<Map<String, Object>> findAllTheater(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("theater.findAllTheater", paramsMap);
	}
	
	/**
	 * 获取所有影院总数(可根据影院名进行模糊查询)
	 * @param paramsMap
	 * @return
	 */
	public Integer findAllTheaterCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("theater.findAllTheaterCount", paramsMap);
	}
	
	/**
	 * 查询能够查看的院线的简易信息(theaterid， theatername)
	 */
	public List<Map<String, Object>> findTheaterSimpleInfoList(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("theater.findTheaterSimpleInfoList", paramsMap);
	}

	/**
	 * 删除影院的归档属性
	 * @param paramsMap
	 */
	public void deleteArchive(Map<String, Object> paramsMap) {
		this.getSqlSession().delete("theater.deleteArchive", paramsMap);
	}

	/**
	 * 删除影院信息
	 * @param paramsMap
	 */
	public void deleteTheater(Map<String, Object> paramsMap) {
		this.getSqlSession().delete("theater.deleteTheater", paramsMap);
	}

	/**
	 * 根据影院编码查询影院详细信息
	 * @param theaternum
	 * @return
	 */
	public Map<String, Object> findTheaterByNum(String theaternum) {
		return this.getSqlSession().selectOne("theater.findTheaterByNum", theaternum);
	}
	
	/**
	 * 根据影院ID查询影院详细信息
	 * @param theaterid
	 * @return
	 */
	public Map<String, Object> findTheaterById(Integer theaterid) {
		return this.getSqlSession().selectOne("theater.findTheaterById", theaterid);
	}

	/**
	 * 查询院线列表
	 * @return
	 */
	public List<Map<String, Object>> findTheaterChainList(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("theater.findTheaterChainList", paramsMap);
	}
	
	/**
	 * 根据id查询院线详细信息
	 * @return
	 */
	public Map<String, Object> findTheaterChainById(Integer theaterchainid) {
		return this.getSqlSession().selectOne("theater.findTheaterChainById", theaterchainid);
	}

	/**
	 * 验证影院的重复性(影院名称和影院编码唯一)
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> checkTheaterRepeat(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("theater.checkTheaterRepeat", paramsMap);
	}

	/**
	 * 根据影院id查询该影院归档属性信息
	 * @param theaterid
	 * @return
	 */
	public List<Map<String, Object>> findArchiveListByTheaterid(Integer theaterid) {
		return this.getSqlSession().selectList("theater.findArchiveListByTheaterid", theaterid);
	}

	/**
	 * 查询该影院下的角色是否为空，若为空则可以删除该影院
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> findRoleByTheaterid(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("theater.findRoleByTheaterid", paramsMap);
	}

	/**
	 * 根据userid查询该用户所属影院信息
	 * @param userid
	 */
	public Map<String, Object> findTheaterByUserid(String userid) {
		return this.getSqlSession().selectOne("theater.findTheaterByUserid", userid);
	}

	/**
	 *  查找所有影院，用于注册
	 */
	public List<Map<String, Object>> findTheaterListForRegist(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("theater.findTheaterListForRegist", paramsMap);
	}

	/**
	 * 查询当前用户能够有权限查看的影院ID，名称及其城市类别
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> findTheaterByCityLevel(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("theater.findTheaterByCityLevel", paramsMap);
	}

	public Map<String,Object> findTheaterByName(String theatername) {
		return getSqlSession().selectOne("theater.findTheaterByName",theatername);
	}
}
