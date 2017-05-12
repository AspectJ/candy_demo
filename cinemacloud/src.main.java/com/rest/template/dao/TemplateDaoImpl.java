package com.rest.template.dao;

import com.cp.filter.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("templateDao")
public class TemplateDaoImpl extends BaseDao{

    public Map<String, Object> saveTemplate(Map<String, Object> paramsmap){
        getSqlSession().insert("template.saveTemplate", paramsmap);
        return paramsmap;
    }


    public List<Map<String,Object>> getTemplate(Map<String, Object> paramsMap) {
       return getSqlSession().selectList("template.getTemplate",paramsMap);
    }

    public int getTemplateTotal(Map<String, Object> paramsMap) {
        return getSqlSession().selectOne("template.getTemplateTotal",paramsMap);
    }

    public void removeTemplate(Integer templateid){
        getSqlSession().delete("template.deleteTemplate",templateid);
    }

    public Map<String,Object> getTemplateOne(Integer templateid){
        return getSqlSession().selectOne("template.getTemplateOne",templateid);
    }
}
