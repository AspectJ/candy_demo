package cn.duxl.factory;

import java.util.LinkedHashMap;

public class FilterChainDefinitionMapBuilder {
	
	public LinkedHashMap<String, String> buildFilterChainDefinitionMap() {
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		
		hashMap.put("/login.jsp", "anon");
		hashMap.put("/user/login", "anon");
		hashMap.put("/logout", "logout");
		
		// # everything else requires authentication:
        hashMap.put("/**", "authc");
		
		return hashMap;
	}
}
