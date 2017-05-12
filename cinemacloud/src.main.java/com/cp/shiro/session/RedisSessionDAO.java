package com.cp.shiro.session;

import java.io.Serializable;
import java.util.Collection;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cp.shiro.cache.SerializeUtils;

public class RedisSessionDAO extends AbstractSessionDAO{

	private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);  


    private RedisManager redisManager;  
      
    /** 
     * shiro-redis的session对象前缀   
     */  
    private String keyPrefix = "shiro_redis_session:";  
      
    @Override  
    public void update(Session session) throws UnknownSessionException {  
        this.saveSession(session);  
    }  
      
    /** 
     * save session 
     * @param session 
     * @throws UnknownSessionException 
     */  
    private void saveSession(Session session) throws UnknownSessionException{ 
        if(session == null || session.getId() == null){  
            logger.error("session or session id is null");  
            return;  
        }  
         System.out.println("进入saveSession:"+session); 
       byte[] key = getByteKey(session.getId());  
       byte[] value = SerializeUtils.serialize(session);
        /* String key=(String) session.getId();
         String value=session.toString();*/
        session.setTimeout(redisManager.getExpire()*1000);        
        this.redisManager.set(key, value, redisManager.getExpire());  
    }  
  
    @Override  
    public void delete(Session session) {  
        if(session == null || session.getId() == null){  
            logger.error("session or session id is null");  
            return;  
        }  
        System.out.println("进入deleteSession:"+session); 
        redisManager.del(this.getByteKey(session.getId()));  
  
    }  
  
    //用来统计当前活动的session  
    @Override  
    public Collection<Session> getActiveSessions() {  
        /*Set<Session> sessions = new HashSet<Session>();  
          
        Set<byte[]> keys = redisManager.keys(this.keyPrefix + "*");  
        if(keys != null && keys.size()>0){  
            for(byte[] key:keys){  
                Session s = (Session)SerializeUtils.deserialize(redisManager.get(key));  
                sessions.add(s);  
            }  
        }  
          
        return sessions; */ 
    	return null;
    }  
  
    @Override  
    protected Serializable doCreate(Session session) {  
    	System.out.println("进入doCreate:"+session); 
        Serializable sessionId = this.generateSessionId(session);    
        this.assignSessionId(session, sessionId);  
        this.saveSession(session);  
        return sessionId;  
    }  
  
    @Override  
    protected Session doReadSession(Serializable sessionId) {  
        if(sessionId == null){  
            logger.error("session id is null");  
            return null;  
        }  
        System.out.println("进入doReadSession:"+sessionId);
        Session s = (Session)SerializeUtils.deserialize(redisManager.get(getByteKey(sessionId)));  
        return s;  
    }  
      
    /** 
     * 获得byte[]型的key 
     * @param key 
     * @return 
     */  
    private byte[] getByteKey(Serializable sessionId){  
        String preKey = this.keyPrefix + sessionId;  
        return preKey.getBytes();  
    }  
  
    public RedisManager getRedisManager() {  
        return redisManager;  
    }  
  
    public void setRedisManager(RedisManager redisManager) {  
        this.redisManager = redisManager;  
          
        /** 
         * 初始化redisManager 
         */  
    }  
  
    /** 
     * Returns the Redis session keys 
     * prefix. 
     * @return The prefix 
     */  
    public String getKeyPrefix() {  
        return keyPrefix;  
    }  
  
    /** 
     * Sets the Redis sessions key  
     * prefix. 
     * @param keyPrefix The prefix 
     */  
    public void setKeyPrefix(String keyPrefix) {  
        this.keyPrefix = keyPrefix;  
    }  

}
