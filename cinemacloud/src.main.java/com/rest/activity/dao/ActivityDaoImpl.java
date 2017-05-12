package com.rest.activity.dao;


import com.cp.filter.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by john on 2017/3/14.
 */
@Repository("activityDao")
public class ActivityDaoImpl extends BaseDao {


    /**
     * 添加活动通知
     */
    public void addActivity(Map<String, Object> paramsMap) {
        this.getSqlSession().insert("activity.addActivity", paramsMap);
    }

    /**
     * 检验活动名称重复性
     * @param paramsMap
     * @return
     */
    public Integer checkRepeat(Map<String, Object> paramsMap) {
        return this.getSqlSession().selectOne("activity.checkRepeat", paramsMap);
    }

    /**
     * 添加活动关联的影院
     *
     * @param paramsMap
     */
    public void addActivityTheater(Map<String, Object> paramsMap) {
        this.getSqlSession().insert("activity.addActivityTheater", paramsMap);
    }

    /**
     * 删除活动
     *
     * @param activityid
     */
    public void deleteActivity(String activityid) {
        this.getSqlSession().delete("activity.deleteActivity", activityid);
    }

    /**
     * 删除影院回复信息
     *
     * @param activityid
     */
    public void deleteActivityTheater(String activityid) {
        this.getSqlSession().delete("activity.deleteActivityTheater", activityid);
    }

    /**
     * 系统管理员或者院线查询活动通知批次
     *
     */
    public List<Map<String, Object>> findActivityBatch(Map<String, Object> paramsMap) {
        return this.getSqlSession().selectList("activity.findActivityBatch", paramsMap);
    }

    /**
     * 查询count
     * @return
     */
    public Integer findActivityBatchCount(Map<String, Object> paramsMap) {
        return this.getSqlSession().selectOne("activity.findActivityBatchCount", paramsMap);
    }

    /**
     * 查询第一条批次的前十条详细数据
     * @param activityid
     * @return
     */
    public List<Map<String,Object>> getFirstBatchDetailPreTen(Integer activityid) {
        return this.getSqlSession().selectList("activity.getFirstBatchDetailPreTen", activityid);
    }


    /**
     * 查询第一条批次的关联信息的总数
     * @param activityid
     * @return
     */
    public Integer getFirstBatchDetailCount(Integer activityid) {
        return this.getSqlSession().selectOne("activity.getFirstBatchDetailCount", activityid);
    }

    /**
     * 根据活动id查询活动
     * @param paramsMap
     * @return
     */
    public Map<String,Object> findActivityById(Map<String, Object> paramsMap) {
        return this.getSqlSession().selectOne("activity.findActivityById", paramsMap);
    }



    /**
     * 根据活动id查询活动批次rel_activity_theater(关联的影院及其角色信息)
     * @param paramsMap
     * @return
     */
    public List<Map<String,Object>> findRel_Activity_theater(Map<String, Object> paramsMap) {
        return this.getSqlSession().selectList("activity.findRel_Activity_theater", paramsMap);
    }

    /**
     * 查询某一个批次关联影院信息的条数count
     * @param paramsMap
     * @return
     */
    public Integer findRel_Activity_theaterCount(Map<String, Object> paramsMap) {
        return this.getSqlSession().selectOne("activity.findRel_Activity_theaterCount", paramsMap);
    }

    /**
     * 影院用户只能查看自己影院并且自己所属角色能够有权限查看的活动通知
     * @param paramsMap
     * @return
     */
    public List<Map<String,Object>> findAvaiableActivityList(Map<String, Object> paramsMap) {
        return this.getSqlSession().selectList("activity.findAvaiableActivityList", paramsMap);
    }

    /**
     * 查找count
     * @param paramsMap
     * @return
     */
    public Integer findAvaiableActivityListCount(Map<String, Object> paramsMap) {
        return this.getSqlSession().selectOne("activity.findAvaiableActivityListCount", paramsMap);
    }

    /**
     * 影院用户对活动通知确认操作
     * @param paramsMap
     */
    public void confirmActivity(Map<String, Object> paramsMap) {
        this.getSqlSession().update("activity.confirmActivity", paramsMap);
    }

    /**
     * 根据活动通知中间表id查询活动详情
     * @param id
     * @return
     */
    public Map<String,Object> getActivityDetail(Integer id){
        return getSqlSession().selectOne("activity.getActivityDetail",id);
    }

    public List<Map<String,Object>> getActivityDetailByactivityid(Integer activityid){
        return getSqlSession().selectList("activity.getActivityDetailByactivityid",activityid);
    }


    /**
     * 影院用户登录之后查看未处理的活动通知
     * @param paramsMap
     * @return
     */
    public List<Map<String,Object>> getAvailableLatestActivity(Map<String, Object> paramsMap) {
        return this.getSqlSession().selectList("activity.getAvailableLatestActivity", paramsMap);
    }

    /**
     * 查找count
     * @param paramsMap
     * @return
     */
    public Integer getAvailableLatestActivityCount(Map<String, Object> paramsMap) {
        return this.getSqlSession().selectOne("activity.getAvailableLatestActivityCount", paramsMap);
    }

    /**
     * 查询系统/院线用户全部最新的活动通知
     * @param paramsMap
     * @return
     */
    public List<Map<String,Object>> getAllLatestActivity(Map<String, Object> paramsMap) {
        return this.getSqlSession().selectList("activity.getAllLatestActivity", paramsMap);
    }

    /**
     * 查询count
     * @param paramsMap
     * @return
     */
    public Integer getAllLatestActivityCount(Map<String, Object> paramsMap) {
        return this.getSqlSession().selectOne("activity.getAllLatestActivityCount", paramsMap);
    }

}
