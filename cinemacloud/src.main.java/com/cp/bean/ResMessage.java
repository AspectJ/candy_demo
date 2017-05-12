package com.cp.bean;

/**
 * 返回参数对应表
 * @author stone
 * 2015-6-18下午5:13:10
 */
public enum ResMessage {
	Success(1000, "Success"),
	
	
	Username_is_regist(1099,"用户名已注册"), 
	Userlogin_Name_Fail(1101,"用户名不存在"), 
	User_Login_fail(1113, "用户登录失败"),
	Role_Auth_Fail(1114,"为角色授权失败"),
	Userlogin_Pass_Fail(1102,"密码错误"),
	Userlogin_Status_Fail(1103,"用户未被启用或未被审核"),
	User_Add_Fail(1104,"新增用户信息失败"),
	User_Updata_Fail(1105,"修改用户信息失败"),
	User_Delete_Fail(1106,"删除用户信息失败"),
	User_Select_Fail(1107,"查询用户信息失败"),
	User_Select_NotExist(1108,"查询用户信息不存在"),
	User_Login_TimeOut(1109,"用户登录超时"),
	Userlogin_RoleStatus_Fail(1110,"角色已被禁用"),
	User_audit__fail(1111,"审核用户失败"), //更改用户状态
	Mobile_Is_Regist(1112, "手机号码已被注册"),
	
	Add_Info_Fail(1201,"新增信息失败"),
	Update_Info_Fail(1202,"修改信息失败"),
	Delete_Info_Fail(1203,"删除信息失败"),
	Select_Info_Fail(1204,"查询信息失败"),
	Select_Info_NotExist(1205,"查询信息不存在"),
	Assign_RoleMenu_Fail(1206,"分配权限失败"),
	Commit_Repeat_Fail(1207,"重复提交"),
	Commit_Operator_Fail(1208,"不能进行该操作"),
	Theater_NameORNum_Exist(1209, "影院名称或影院编码已存在"),
	
	
	Lack_Privilege(9997, "您没有此项权限"),
	Lack_Param(9998, "缺少参数"),
	Server_Abnormal(9999, "服务器处理异常"),
	
	Program_Not_null(13001, "该栏目下不为空，不能删除"),
	Role_of_Theater_NOTNULL(13002, "该影院下拥有角色不为空，尚不能删除"),
	User_of_Theater_NOTNULL(13003, "该影院下关联用户不为空，尚不能删除"),
	User_of_Role_NOTNULL(13004, "该角色下用户不为空，尚不能删除"),
	Program_Name_Exists(13005, "栏目名已存在"),
	Information_Name_Exists(13006, "文章名已存在"),
	Role_Status_Fail(13007, "用户所属角色状态不可用"),
	Role_Name_Exists(13008, "该角色名已存在"),
	Not_Change_OwnRole_Menu(13009, "不能更改自己所属角色的权限"),
	Delete_QNFile_Fail(13010, "删除七牛文件失败"),
	File_Not_Exists(13011, "文件不存在"),
	Activity_Title_Exists(13012, "活动名已存在"),
	OldPass_Error(13013, "原密码错误"),
	Two_Pass_Same(13013, "新密码不能和原密码相同"),

	
	;

	public int code;
	public String message;

	private ResMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public static String getMessage(int code) {
		for (ResMessage rm : values()) {
			if (rm.code == code) {
				return rm.message;
			}
		}
		return "";
	}
}
