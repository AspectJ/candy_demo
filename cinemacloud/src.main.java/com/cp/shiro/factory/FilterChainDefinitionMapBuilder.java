package com.cp.shiro.factory;

import java.util.LinkedHashMap;

public class FilterChainDefinitionMapBuilder {
	
	public LinkedHashMap<String, String> buildFilterChainDefinitionMap() {
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		
		
		hashMap.put("/Login", "anon");
		hashMap.put("/weAllot","anon");
		hashMap.put("/weFfers","anon");
		hashMap.put("/weNotice","anon");
		hashMap.put("/cloud/**", "anon");
		hashMap.put("/wx/**", "anon");
		hashMap.put("/manage/**", "anon");
		hashMap.put("/rest/user/login", "anon");
		hashMap.put("/rest/template/getTemplateList","anon");
		hashMap.put("/rest/activity/getActivityDetail","anon");
		hashMap.put("/rest/activity/getuserinfo","anon");
		hashMap.put("/rest/materiel/getuserinfo", "anon");
		hashMap.put("/rest/ffers/getuserinfo", "anon");
		hashMap.put("/rest/materiel/getMaterielListFromWX", "anon");
		hashMap.put("/rest/materiel/updateMaterielFromWX","anon");
		//注册
		hashMap.put("/Regist", "anon");
		hashMap.put("/rest/theater/findTheaterListForRegist", "anon");
		hashMap.put("/rest/user/regist", "anon");
		
		hashMap.put("/rest/file/uploadFile", "anon");
		
		hashMap.put("/rest/enterprize/callback", "anon");
		
		hashMap.put("/logout", "logout");
		
		// # everything else requires authentication:
//		hashMap.put("/**", "anon");
        hashMap.put("/**", "authc");
		
		return hashMap;
	}
}
