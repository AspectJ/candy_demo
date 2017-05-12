package com.cp.bean;

/**
 * 文章归档实体类
 * @author john
 *
 */
public class InformationArchive {
	private Integer id;
	private Integer info_id;
	private Integer archiveid;
	
	private Integer value;
	private String content;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getInfo_id() {
		return info_id;
	}
	public void setInfo_id(Integer info_id) {
		this.info_id = info_id;
	}
	public Integer getArchiveid() {
		return archiveid;
	}
	public void setArchiveid(Integer archiveid) {
		this.archiveid = archiveid;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "InformationArchive [id=" + id + ", info_id=" + info_id + ", archiveid=" + archiveid + ", value=" + value
				+ ", content=" + content + "]";
	}
	
}
