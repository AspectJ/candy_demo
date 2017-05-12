package cn.duxl.service;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Service;

@Service("shiroService")
public class ShiroService {

	@RequiresRoles({"admin"})
	public void testMethod() {
		Boolean bool = SecurityUtils.getSubject().hasRole("admin");
		System.out.println("-----------> : " + bool);
		System.out.println("@RequiresRoles [admin]");
	}
}
