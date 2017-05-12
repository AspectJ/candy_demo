package com.cp.bean;

import java.util.Date;

public class BillBean {
	
	private Integer billid;
	private Integer billconfid;			//账单批次id
	private Integer theaterid;			//影院id
	private Integer status;				//账单发送状态账单发送状态 0-未发送，1-发送，2-已收到，3-已核验
	private Integer operatorid;			//操作人id
	private Date createtime;			//账单创建时间
	private String modifytime;			//账单修改时间
	public Integer getBillid() {
		return billid;
	}
	public void setBillid(Integer billid) {
		this.billid = billid;
	}
	public Integer getBillconfid() {
		return billconfid;
	}
	public void setBillconfid(Integer billconfid) {
		this.billconfid = billconfid;
	}
	public Integer getTheaterid() {
		return theaterid;
	}
	public void setTheaterid(Integer theaterid) {
		this.theaterid = theaterid;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getOperatorid() {
		return operatorid;
	}
	public void setOperatorid(Integer operatorid) {
		this.operatorid = operatorid;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getModifytime() {
		return modifytime;
	}
	public void setModifytime(String modifytime) {
		this.modifytime = modifytime;
	}


}
