package com.cp.bean;

import java.util.Date;
import java.util.List;

/**
 * 栏目实体类
 * @author john
 *
 */
public class Program {
	private Integer program_id;
	private String program_name;
	private Integer status;
	private Date createtime;
	private Date modifytime;
	
	//扩展属性（封装栏目的归档属性list）
	private List<Archive> arlist;

	public Integer getProgram_id() {
		return program_id;
	}

	public void setProgram_id(Integer program_id) {
		this.program_id = program_id;
	}

	public String getProgram_name() {
		return program_name;
	}

	public void setProgram_name(String program_name) {
		this.program_name = program_name;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getModifytime() {
		return modifytime;
	}

	public void setModifytime(Date modifytime) {
		this.modifytime = modifytime;
	}

	public List<Archive> getArlist() {
		return arlist;
	}

	public void setArlist(List<Archive> arlist) {
		this.arlist = arlist;
	}

	@Override
	public String toString() {
		return "Program [program_id=" + program_id + ", program_name=" + program_name + ", status=" + status
				+ ", createtime=" + createtime + ", modifytime=" + modifytime + ", arlist=" + arlist + "]";
	}
	
	
}
