package com.cp.bean;

import java.util.Date;

/**
 * 日志实体类(POJO)
 * @author john
 *
 */
public class LogInfo {
	private Integer ID;
	private Integer USERID;
	private String METHOD_NAME;
	
	private String METHOD_DESCRIPTION;
	private String REQUEST_IP;
	private String REQUEST_URI;
	
	private Date CREATE_TIME;

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public Integer getUSERID() {
		return USERID;
	}

	public void setUSERID(Integer uSERID) {
		USERID = uSERID;
	}

	public String getMETHOD_NAME() {
		return METHOD_NAME;
	}

	public void setMETHOD_NAME(String mETHOD_NAME) {
		METHOD_NAME = mETHOD_NAME;
	}

	public String getMETHOD_DESCRIPTION() {
		return METHOD_DESCRIPTION;
	}

	public void setMETHOD_DESCRIPTION(String mETHOD_DESCRIPTION) {
		METHOD_DESCRIPTION = mETHOD_DESCRIPTION;
	}

	public String getREQUEST_IP() {
		return REQUEST_IP;
	}

	public void setREQUEST_IP(String rEQUEST_IP) {
		REQUEST_IP = rEQUEST_IP;
	}

	public String getREQUEST_URI() {
		return REQUEST_URI;
	}

	public void setREQUEST_URI(String rEQUEST_URI) {
		REQUEST_URI = rEQUEST_URI;
	}

	public Date getCREATE_TIME() {
		return CREATE_TIME;
	}

	public void setCREATE_TIME(Date cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}

	@Override
	public String toString() {
		return "LogInfo [ID=" + ID + ", USERID=" + USERID + ", METHOD_NAME=" + METHOD_NAME + ", METHOD_DESCRIPTION="
				+ METHOD_DESCRIPTION + ", REQUEST_IP=" + REQUEST_IP + ", REQUEST_URI=" + REQUEST_URI + ", CREATE_TIME="
				+ CREATE_TIME + "]";
	}
	
	
}
