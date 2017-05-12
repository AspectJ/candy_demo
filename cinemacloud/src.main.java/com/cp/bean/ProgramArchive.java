package com.cp.bean;

/**
 * 栏目归档属性实体类(javabean)
 * @author john
 *
 */
public class ProgramArchive {
	private Integer id;
	private Integer program_id;
	private Integer archiveid;
	private Integer value;
	private String  content;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProgram_id() {
		return program_id;
	}
	public void setProgram_id(Integer program_id) {
		this.program_id = program_id;
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
		return "ProgramArchive [id=" + id + ", program_id=" + program_id + ", archiveid=" + archiveid + ", value="
				+ value + ", content=" + content + "]";
	}
	
}
