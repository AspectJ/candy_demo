package com.rest.user.role.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cp.bean.Role;
import com.cp.filter.BaseDao;

@Repository("roleDao")
public class RoleDaoImpl extends BaseDao{
	
	/**
	 * 添加角色信息
	 * @param paramsMap
	 */
	public void addRole(Map<String, Object> paramsMap) {
		this.getSqlSession().insert("role.addRole", paramsMap);
	}

	/**
	 * 更改角色状态
	 * @param paramsMap
	 */
	public void auditRoleStatus(Map<String, Object> paramsMap) {
		this.getSqlSession().update("role.auditRoleStatus", paramsMap);
	}

	/**
	 * 根据角色类型和院线(影院)查询拥有角色信息
	 * @param parseInt
	 */
	public List<Map<String, Object>> findRolenameBySystem(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("role.findRolenameBySystem", paramsMap);
	}

	/**
	 * 查询角色信息列表(可根据角色名模糊查询)
	 * @param paramsMap
	 * @return
	 */
	public List<Role> findRoleListByCriteria(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("role.findRoleListByCriteria", paramsMap);
	}
	
	/**
	 * 查询角色信息总数
	 * @param paramsMap
	 * @return
	 */
	public Integer findRoleListByCriteriaCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("role.findRoleListByCriteriaCount", paramsMap);
	}
	
	
	/**
	 * 根据roleid查询角色
	 * @param paramsMap
	 * @return
	 */
	public Map<String, Object> findRoleInfoById(int roleid) {
		return this.getSqlSession().selectOne("role.findRoleInfoById", roleid);
	}
	
	

	/**
	 * 为角色添加权限
	 * @param paramsMap
	 */
	public void addPermission(Map<String, Object> paramsMap) {
		this.getSqlSession().insert("role.addPermission", paramsMap);
	}

	/**
	 * 更改角色信息(角色名)
	 * @param paramsMap
	 */
	public void updateRoleInfo(Map<String, Object> paramsMap) {
		this.getSqlSession().update("role.updateRoleInfo", paramsMap);
	}

	/**
	 * 删除角色信息
	 * @param roleid
	 */
	public void deleteRole(String roleid) {
		this.getSqlSession().delete("role.deleteRole", roleid);
	}

	/**
	 * 根据roleid查询该角色及其权限信息
	 * @param i
	 * @return
	 */
	public Role findRoleByRoleid(int roleid) {
		return this.getSqlSession().selectOne("role.findRoleByRoleid", roleid);
	}

	/**
	 * 重复性校验(rolename校验，不能重复)
	 * @param paramsMap
	 * @return
	 */
	public Integer checkRepeat(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("role.checkRepeat", paramsMap);
	}
	
	
	//================================ 影院角色"未审核用户" BEGIN =======================================
	/**
	 * 查询影院角色的"未审核用户"详细信息
	 */
	public Map<String, Object> getUnAuditRoleInfo() {
		return this.getSqlSession().selectOne("role.getUnAuditRoleInfo");
	}
	
	/**
	 * 如果影院角色没有"未审核用户"，则创建该角色
	 * 		rolename = "未审核用户"  	roletype = 2 	status = 1
	 */
	public void addUnAuditRole(Map<String, Object> paramsMap) {
		this.getSqlSession().insert("role.addUnAuditRole", paramsMap);
	}
	
	//================================  影院角色"未审核用户"  END =======================================
	
}
