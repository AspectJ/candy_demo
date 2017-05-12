package com.ydp.server;

import com.cp.bean.ResMessage;

import java.io.Serializable;

/**
 * 返回数据内容
 * @author stone
 * @create 2015-12-2 下午5:39:43
 */
public class DataMessage implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * 返回是否成功
	 */
	private boolean success;
	
	/**
	 * 返回异常类型
	 */
	private int errorCode;
	
	/**
	 * 返回异常内容信息
	 */
	private String errorMessage;
	
	/**
	 * 返回数据内容
	 */
	private Object data;
	
	/**
	 * 数据总记录条数 
	 */
	private int total;

	public boolean isSuccess()
	{
		return success;
	}

	public void setSuccess(boolean success)
	{
		this.success = success;
	}

	public int getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(int errorCode)
	{
		this.errorCode = errorCode;
		this.errorMessage = ResMessage.getMessage(errorCode);
	}
	
	public void setErrorCode(int errorCode, Object message)
	{
		this.errorCode = errorCode;
		this.errorMessage = ResMessage.getMessage(errorCode) + "--" + message;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public Object getData()
	{
		return data;
	}

	public void setData(Object data)
	{
		this.success = true;
		this.data = data;
	}

	public int getTotal()
	{
		return total;
	}

	public void setTotal(int total)
	{
		this.total = total;
	}
}
