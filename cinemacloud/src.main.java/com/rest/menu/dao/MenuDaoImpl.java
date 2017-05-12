package com.rest.menu.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cp.bean.Menu;
import com.cp.bean.Role_Menu;
import com.cp.filter.BaseDao;

@Repository("menuDao")
public class MenuDaoImpl extends BaseDao {

	/**
	 * 根据角色ID删除该角色所有权限
	 * @param roleid
	 */
	public void deleteMenuByRoleid(String roleid) {
		this.getSqlSession().delete("menu.deleteMenuByRoleid", roleid);
	}

	/**
	 * 查询出系统所有权限
	 * @return
	 */
	public List<Menu> findAllMenu() {
		return this.getSqlSession().selectList("menu.findAllMenu");
	}

	/**
	 * 查询当前用户所属角色的权限列表
	 * @param userid
	 * @return
	 */
	public List<Menu> findAvailableMenuList(int roleid) {
		return this.getSqlSession().selectList("menu.findAvailableMenuList", roleid);
	}

	/**
	 * 根据角色id查询权限列表
	 * @param roleid
	 * @return
	 */
	public List<Role_Menu> findMenuListByRoleid(Integer roleid) {
		return this.getSqlSession().selectList("menu.findMenuListByRoleid", roleid);
	}

}
