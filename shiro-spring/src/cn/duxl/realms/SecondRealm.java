package cn.duxl.realms;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.crypto.hash.SimpleHashRequest;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;
import org.junit.Test;
import org.springframework.stereotype.Service;

import net.sf.ehcache.search.expression.Criteria;

@Service
public class SecondRealm extends AuthenticatingRealm {

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		
		String username = upToken.getUsername();
		
		System.out.println("ScecondRealm");
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
			credentials = "073d4c3ae812935f23cb3f2a71943f49e082a718";
		}else if("admin".equals(username)) {
			credentials = "ce2f6417c7e1d32c1d81a797ee0b499f87c5de06";
		}
		//3). 盐值. 
		ByteSource credentialsSalt = ByteSource.Util.bytes(username);
		//4). realmName: 当前 realm 对象的 name. 调用父类的 getName() 方法即可
		String realmName = this.getName();
		
		//不加盐
//		AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(principal, credentials, realmName);
		//加盐
		AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo("SecondRealm", credentials, credentialsSalt, realmName);
		
		return authenticationInfo;
	}
	
	
	@Test
	public void fun1() {
		String algorithmName = "SHA1";
		
		Object source = "123456";
		
		Object salt = ByteSource.Util.bytes("admin");
		
		int hashIterations = 1024;
		
		//不加盐
//		Object result = new SimpleHash(algorithmName, source);
		
		//加盐且循环一定次数
		Object result = new SimpleHash(algorithmName, source, salt, hashIterations);
		
		System.out.println(result.toString());
	}


	
}
