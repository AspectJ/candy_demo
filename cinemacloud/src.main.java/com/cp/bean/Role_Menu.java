package com.cp.bean;

/**
 * 角色权限关联POJO
 * 		对应表rel_role_permission
 * @author john
 *
 */
public class Role_Menu {
	private Integer pid;
	private Integer roleid;
	private Integer menuid;

	//扩展属性 权限详细信息
	private Menu menu;

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public Integer getRoleid() {
		return roleid;
	}

	public void setRoleid(Integer roleid) {
		this.roleid = roleid;
	}

	public Integer getMenuid() {
		return menuid;
	}

	public void setMenuid(Integer menuid) {
		this.menuid = menuid;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	@Override
	public String toString() {
		return "Role_Menu [pid=" + pid + ", roleid=" + roleid + ", menuid=" + menuid + ", menu=" + menu + "]";
	}
	
	
}
