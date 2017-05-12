package com.cp.bean;

import java.util.List;

/**
 * 角色实体类(POJO)
 * @author john
 *
 */
public class Role {
	private Integer roleid;
	private String rolename;
	private Integer roletype;
	private Integer status;
	private Integer theaterid;
	private Integer theaterchainid;
	//扩展属性 用于角色封装权限列表
	private List<Role_Menu> menuList;
	//角色所属影院
	private String theatername;
	//角色所属院线
	private String theaterchainname;
	
	public Integer getRoleid() {
		return roleid;
	}
	public void setRoleid(Integer roleid) {
		this.roleid = roleid;
	}
	public String getRolename() {
		return rolename;
	}
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	public Integer getRoletype() {
		return roletype;
	}
	public void setRoletype(Integer roletype) {
		this.roletype = roletype;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getTheaterid() {
		return theaterid;
	}
	public void setTheaterid(Integer theaterid) {
		this.theaterid = theaterid;
	}
	public Integer getTheaterchainid() {
		return theaterchainid;
	}
	public void setTheaterchainid(Integer theaterchainid) {
		this.theaterchainid = theaterchainid;
	}
	public List<Role_Menu> getMenuList() {
		return menuList;
	}
	public void setMenuList(List<Role_Menu> menuList) {
		this.menuList = menuList;
	}
	public String getTheatername() {
		return theatername;
	}
	public void setTheatername(String theatername) {
		this.theatername = theatername;
	}
	public String getTheaterchainname() {
		return theaterchainname;
	}
	public void setTheaterchainname(String theaterchainname) {
		this.theaterchainname = theaterchainname;
	}
	@Override
	public String toString() {
		return "Role [roleid=" + roleid + ", rolename=" + rolename + ", roletype=" + roletype + ", status=" + status
				+ ", theaterid=" + theaterid + ", theaterchainid=" + theaterchainid + ", menuList=" + menuList
				+ ", theatername=" + theatername + ", theaterchainname=" + theaterchainname + "]";
	}
	
	
	
}
