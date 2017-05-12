package com.cp.bean;

import java.util.List;

/**
 * 菜单实体类（POJO）
 * @author john
 *
 */
public class Menu {
	private Integer menuid;
	private String menuname;
	private Integer parentid;
	private String requesturl;
	private Integer sortno;
	private Integer menutype;
	private String permission;
	private List<Menu> subMenuList;
	
	public Integer getMenuid() {
		return menuid;
	}
	public void setMenuid(Integer menuid) {
		this.menuid = menuid;
	}
	public String getMenuname() {
		return menuname;
	}
	public void setMenuname(String menuname) {
		this.menuname = menuname;
	}
	public Integer getParentid() {
		return parentid;
	}
	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}
	public String getRequesturl() {
		return requesturl;
	}
	public void setRequesturl(String requesturl) {
		this.requesturl = requesturl;
	}
	public Integer getSortno() {
		return sortno;
	}
	public void setSortno(Integer sortno) {
		this.sortno = sortno;
	}
	public Integer getMenutype() {
		return menutype;
	}
	public void setMenutype(Integer menutype) {
		this.menutype = menutype;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public List<Menu> getSubMenuList() {
		return subMenuList;
	}
	public void setSubMenuList(List<Menu> subMenuList) {
		this.subMenuList = subMenuList;
	}
	
	@Override
	public String toString() {
		return "Menu [menuid=" + menuid + ", menuname=" + menuname + ", parentid=" + parentid + ", requesturl="
				+ requesturl + ", sortno=" + sortno + ", menutype=" + menutype + ", permission=" + permission
				+ ", subMenuList=" + subMenuList + "]";
	}
	
}
