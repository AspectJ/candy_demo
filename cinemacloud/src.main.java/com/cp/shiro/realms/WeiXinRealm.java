package com.cp.shiro.realms;

import com.rest.user.dao.UserDaoImpl;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 29632 on 2017/3/20.
 */
public class WeiXinRealm extends AuthorizingRealm {

    @Autowired
    @Qualifier("userDao")
    private UserDaoImpl userDao;

    /**
     * 用户认证
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username=(String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Set<String> roleSet=null;
        Set<String> permissionSet=new HashSet<String>();
        Map<String,Object> roleMap=userDao.getRole(username);
        if(roleMap !=null && !roleMap.isEmpty()){
            roleSet=new HashSet(roleMap.values());
            List<Map<String,Object>> permissionList=userDao.getPermission((Integer)roleMap.get("roleid"));
            for(Map<String,Object> permissionMap : permissionList){
                permissionSet.add((String)permissionMap.get("permission"));
            }
        }
        authorizationInfo.setRoles(roleSet);
        authorizationInfo.setStringPermissions(permissionSet);
        return authorizationInfo;
    }

    /**
     * 用户授权
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;

        String username = upToken.getUsername();

        Map<String, Object> userMap = userDao.login(username);
        if(userMap == null || userMap.size() == 0) {
            throw new UnknownAccountException("用户不存在");
        } else if((int) userMap.get("status") == 0 || (int) userMap.get("audit_flag") == 0) {
            throw new LockedAccountException("用户未被启用或未被审核");
        } else if((int) userMap.get("role_status") == 0) {
            throw new DisabledAccountException("用户所属角色状态不可用");
        }
        //以下信息是从数据库中获取的.
        //1). principal: 认证的实体信息. 可以是 username, 也可以是数据表对应的用户的实体类对象.
        Object principal = username;
        //2). credentials: 密码.
        Object credentials = userMap.get("password");
        //3). realmName: 当前 realm 对象的 name. 调用父类的 getName() 方法即可
        String realmName = this.getName();
        AuthenticationInfo authenticationInfo=new SimpleAuthenticationInfo(principal,credentials,realmName);

        return authenticationInfo;
    }
}
