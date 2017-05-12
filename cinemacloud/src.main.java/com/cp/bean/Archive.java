package com.cp.bean;

import java.util.List;

/**
 * 归档POJO
 * @author john
 *
 */
public class Archive {
	
	private Integer archiveid;
	private String archivename;
	private Integer type;
	private String content;
	//扩展属性(用于mybatis一对多映射)
	private List<ProgramArchive> parlist;
	
	public Integer getArchiveid() {
		return archiveid;
	}
	public void setArchiveid(Integer archiveid) {
		this.archiveid = archiveid;
	}
	public String getArchivename() {
		return archivename;
	}
	public void setArchivename(String archivename) {
		this.archivename = archivename;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<ProgramArchive> getParlist() {
		return parlist;
	}
	public void setParlist(List<ProgramArchive> parlist) {
		this.parlist = parlist;
	}
	
	@Override
	public String toString() {
		return "Archive [archiveid=" + archiveid + ", archivename=" + archivename + ", type=" + type + ", content="
				+ content + ", parlist=" + parlist + "]";
	}
	

}
