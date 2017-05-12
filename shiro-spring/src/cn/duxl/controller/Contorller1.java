package cn.duxl.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.duxl.service.ShiroService;

@Controller
@RequestMapping("/user")
public class Contorller1 {
	
	@Autowired
	@Qualifier("shiroService")
	private ShiroService shiroService;

	/**
	 * 注解权限
	 */
	@RequestMapping("/testShiroAnnotation")
	public String testShiroAnnotation() {
		shiroService.testMethod();
		
		return "redirect:/success.jsp";
	}
	
	@RequestMapping("/login")
	public String login(String username, String password) {
		Subject currentUser = SecurityUtils.getSubject();
		if(!currentUser.isAuthenticated()) {
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            token.setRememberMe(true);
            
            try {
            	System.out.println("------> : " + username.hashCode());
                currentUser.login(token);
                
            }catch (AuthenticationException ae) {
            	System.out.println("登陆失败: " + ae.getMessage());
            }
		}
		
		return "redirect:/success.jsp";
	}
}
