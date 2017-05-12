package com.rest.user.dao;

import com.cp.filter.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("userDao")
public class UserDaoImpl extends BaseDao{
	
	/**
	 * 查询所有用户详细信息
	 * @return
	 */
	public List<Map<String, Object>> getAllUser(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("user.getAllUser", paramsMap);
	}
	
	/**
	 * 根据ID查询某一用户的详细信息
	 * @param userid
	 * @return
	 */
	public Map<String, Object> findUserById(String userid) {
		return this.getSqlSession().selectOne("user.findUserById", userid);
	}

	/**
	 * 查询当前用户的角色类型及其所属影院(院线)
	 * @param userid
	 * @return
	 */
	public Map<String, Object> findRoleAndTheaterById(Integer userid) {
		return this.getSqlSession().selectOne("user.findRoleAndTheaterById", userid);
	}
	
	/**
	 * 用户注册/添加用户
	 * @param paramsMap
	 */
	public void regist(Map<String, Object> paramsMap) {
		this.getSqlSession().insert("user.regist", paramsMap);
	}

	/**
	 * 更新用户状态
	 * @param paramsMap
	 */
	public void auditUser(Map<String, Object> paramsMap) {
		this.getSqlSession().update("user.auditUser", paramsMap);
	}
	
	/**
	 * 创建用户并审核后更改用户isCreate为true/1
	 * @param userid
	 */
	public void updateIsCreate(Integer userid) {
		this.getSqlSession().update("user.updateIsCreate", userid);
	}
	
	/**
	 * 启用/禁用用户
	 * @param paramsMap
	 */
	public void changeUserStatus(Map<String, Object> paramsMap) {
		this.getSqlSession().update("user.changeUserStatus", paramsMap);
	}


	/**
	 * 检查用户名/手机号码是否已被注册
	 * @param paramsMap
	 */
	public List<Map<String, Object>> checkRepeat(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("user.checkRepeat", paramsMap);
	}
	
	/**
	 * 更改密码
	 * @param paramsMap
	 */
	public void updatePassword(Map<String, Object> paramsMap) {
		this.getSqlSession().update("user.updatePassword", paramsMap);
	}

	/**
	 * 删除用户表中用户数据
	 * @param userid
	 */
	public void deleteUser(int userid) {
		this.getSqlSession().delete("user.deleteUser", userid);
	}

	/**
	 * 用户登录
	 * @param username
	 * @return
	 */
	public Map<String, Object> login(String username) {
		return this.getSqlSession().selectOne("user.login", username);
	}
	
	/**
	 * 获取用户角色
	 * @param username
	 * @return
	 */
	public Map<String,Object> getRole(String username){
		return getSqlSession().selectOne("user.getRole",username);
	}

	/**
	 * 用户登录时，更改用户的最后一次登录时间
	 * @param userid
	 */
	public void updateLastLoginTime(int userid) {
		this.getSqlSession().update("user.updateLastLoginTime", userid);
	}
	
	/**
	 * 更改用户最后一次查看日志的时间为当前时间
	 * @param userid
	 */
	public void updateLastSeeLogTime(int userid) {
		this.getSqlSession().update("user.updateLastSeeLogTime", userid);
	}

	/**
	 * 根据ids查询已审核用户列表，同时可以根据用户名进行模糊查询
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> getAuditedUserListByStatus(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("user.getAuditedUserListByStatus", paramsMap);
	}
	
	
	/**
	 * 查询未审核用户的列表
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> getUnAuditedUserList(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("user.getUnAuditedUserList", paramsMap);
	}
	

	/**
	 * 查找未审核用户count
	 * @param paramsMap
	 * @return
	 */
	public Integer getUnAuditedUserListCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("user.getUnAuditedUserListCount", paramsMap);
	}
	
	/**
	 * 根据ids查询已审核用户 的数量(可以根据用户名进行模糊查询)
	 * @param paramsMap
	 * @return
	 */
	public Integer getAuditedUserListByStatusCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("user.getAuditedUserListByStatusCount", paramsMap);
	}

	/**
	 * 更改用户信息(密码，邮箱)
	 * @param paramsMap
	 */
	public void updateUserInfo(Map<String, Object> paramsMap) {
		this.getSqlSession().update("user.updateUserInfo", paramsMap);
	}
	
	/**
	 * 根据角色id查询用户（查询角色关联的用户）
	 * @param roleid
	 * @return
	 */
	public List<Map<String, Object>> getUserListByRoleid(String roleid) {
		return this.getSqlSession().selectList("user.getUserListByRoleid", roleid);
	}
	
	/**
	 * 查询影院关联的用户
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> findUserByTheaterid(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("user.findUserByTheaterid", paramsMap);
	}
	
	public  List<Map<String,Object>> getPermission(Integer roleid) {
		return getSqlSession().selectList("user.getPermission", roleid);
		
	}

	public List<Map<String,Object>> getUserId(Map<String, Object> map) {
		return	getSqlSession().selectList("user.getUserid",map);
	}


	/**
	 * 查询某角色下的可能正登陆用户
	 * @param roleid
	 * @return
     */
	public List<Map<String,Object>> findMabeyLoginUserList(String roleid) {
		return this.getSqlSession().selectList("user.findMabeyLoginUserList", roleid);
	}

	/**
	 * 条件查询用户列表
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String,Object>> findUserByCriteria(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("user.findUserByCriteria", paramsMap);
	}

	/**
	 * 查询count
	 * @param paramsMap
	 * @return
	 */
	public Integer findUserByCriteriaCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("user.findUserByCriteriaCount", paramsMap);
	}
}
