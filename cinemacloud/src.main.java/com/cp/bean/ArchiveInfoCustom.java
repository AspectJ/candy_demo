package com.cp.bean;

import java.util.List;

/**
 * archive与information一对多映射javabean
 * @author john
 *
 */
public class ArchiveInfoCustom {
	
	private Integer archiveid;
	private String archivename;
	private Integer type;
	private String content;
	
	//扩展属性(用于mybatis一对多映射) ----> archive 和 InformationArchive 一对多
	private List<InformationArchive> infoArlist;
	
	
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
	public List<InformationArchive> getInfoArlist() {
		return infoArlist;
	}
	public void setInfoArlist(List<InformationArchive> infoArlist) {
		this.infoArlist = infoArlist;
	}
	
	@Override
	public String toString() {
		return "ArchiveInfoCustom [archiveid=" + archiveid + ", archivename=" + archivename + ", type=" + type
				+ ", content=" + content + ", infoArlist=" + infoArlist + "]";
	}
	
}
