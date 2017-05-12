package com.cp.bean;

import java.util.Date;
import java.util.List;

/**
 * 文章实体类
 * @author john
 *
 */
public class Information {
	private Integer info_id;
	private Integer program_id;
	private String title;
	private String content;
	
	private Integer use_status;
	private Integer brows_times;
	private String doc_name;
	private Integer audit_flag;
	
	private String image_name;
	private Date start_time;
	private Date end_time;
	private Date createtime;
	
	private Date modifytime;
	private String author;
	private Integer operatorid;
	
	//扩展属性(文档在七牛的上传路径)
	private String doc_url;
	
	//扩展属性（封装栏目的归档属性list）
	private List<ArchiveInfoCustom> ariclist;

	public Integer getInfo_id() {
		return info_id;
	}

	public void setInfo_id(Integer info_id) {
		this.info_id = info_id;
	}

	public Integer getProgram_id() {
		return program_id;
	}

	public void setProgram_id(Integer program_id) {
		this.program_id = program_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getUse_status() {
		return use_status;
	}

	public void setUse_status(Integer use_status) {
		this.use_status = use_status;
	}

	public Integer getBrows_times() {
		return brows_times;
	}

	public void setBrows_times(Integer brows_times) {
		this.brows_times = brows_times;
	}

	public String getDoc_name() {
		return doc_name;
	}

	public void setDoc_name(String doc_name) {
		this.doc_name = doc_name;
	}

	public Integer getAudit_flag() {
		return audit_flag;
	}

	public void setAudit_flag(Integer audit_flag) {
		this.audit_flag = audit_flag;
	}

	public String getImage_name() {
		return image_name;
	}

	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Integer getOperatorid() {
		return operatorid;
	}

	public void setOperatorid(Integer operatorid) {
		this.operatorid = operatorid;
	}

	public String getDoc_url() {
		return doc_url;
	}

	public void setDoc_url(String doc_url) {
		this.doc_url = doc_url;
	}

	public List<ArchiveInfoCustom> getAriclist() {
		return ariclist;
	}

	public void setAriclist(List<ArchiveInfoCustom> ariclist) {
		this.ariclist = ariclist;
	}

	@Override
	public String toString() {
		return "Information [info_id=" + info_id + ", program_id=" + program_id + ", title=" + title + ", content="
				+ content + ", use_status=" + use_status + ", brows_times=" + brows_times + ", doc_name=" + doc_name
				+ ", audit_flag=" + audit_flag + ", image_name=" + image_name + ", start_time=" + start_time
				+ ", end_time=" + end_time + ", createtime=" + createtime + ", modifytime=" + modifytime + ", author="
				+ author + ", operatorid=" + operatorid + ", doc_url=" + doc_url + ", ariclist=" + ariclist + "]";
	}

}
