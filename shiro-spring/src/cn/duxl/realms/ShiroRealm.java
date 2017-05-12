package cn.duxl.realms;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.junit.Test;
import org.springframework.stereotype.Service;

@Service
public class ShiroRealm extends AuthorizingRealm {
	
	/**
	 * 认证
	 */
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		
		String username = upToken.getUsername();
		
		System.out.println("11111111111111111111111111111111111111");
		System.out.println("username ----------->" + username.hashCode());
		
		if("unknown".equals(username)) {
			throw new UnknownAccountException("用户不存在");
		}
		if("hello".equals(username)) {
			throw new LockedAccountException("用户被锁定");
		}
		
		//以下信息是从数据库中获取的.
		//1). principal: 认证的实体信息. 可以是 username, 也可以是数据表对应的用户的实体类对象. 
		Object principal = username;
		//2). credentials: 密码. 
		Object credentials = null;
		if("user".equals(username)) {
			credentials = "098d2c478e9c11555ce2823231e02ec1";
		}else if("admin".equals(username)) {
			credentials = "038bdaf98f2037b31f1e75b5b4c9b26e";
		}
		//3). 盐值. 
		ByteSource credentialsSalt = ByteSource.Util.bytes(username);
		//4). realmName: 当前 realm 对象的 name. 调用父类的 getName() 方法即可
		String realmName = this.getName();
		
		//不加盐
//		AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(principal, credentials, realmName);
		//加盐
		AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName);
		
		return authenticationInfo;
	}
	
	/**
	 * MD5或者SHA1加盐加密
	 */
	@Test
	public void fun1() {
		String algorithmName = "MD5";
		
		Object source = "123456";
		
		Object salt = ByteSource.Util.bytes("user");
		
		int hashIterations = 1024;
		
		//不加盐
//		Object result = new SimpleHash(algorithmName, source);
		
		//加盐且循环一定次数
		Object result = new SimpleHash(algorithmName, source, salt, hashIterations);
		
		System.out.println(result.toString());
	}


	/**
	 * 授权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		//1. 从 PrincipalCollection 中来获取登录用户的信息
		System.out.println(principals.getRealmNames());
		
		//2. 利用登录的用户的信息来用户当前用户的角色或权限(可能需要查询数据库)
		Object principal = principals.getPrimaryPrincipal();
		Set<String> roles = new HashSet<String>();
		roles.add("user");
		if("admin".equals(principal)) {
			roles.add("admin");
		}
		
		//3. 创建 SimpleAuthorizationInfo, 并设置其 reles 属性.
		AuthorizationInfo info = new SimpleAuthorizationInfo(roles);
		
		//4. 返回 SimpleAuthorizationInfo 对象. 
		return info;
	}
	
}
