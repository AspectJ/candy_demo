package com.rest.zone.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cp.filter.BaseDao;

@Repository("zoneDao")
public class ZoneDaoImpl extends BaseDao {

	
	//查询省份
	public List<Map<String, Object>> findProvince() {
		return this.getSqlSession().selectList("zone.findProvince");
	}

	public List<Map<String, Object>> findCity(Integer provinceID) {
		return this.getSqlSession().selectList("zone.findCity", provinceID);
	}

	public List<Map<String, Object>> findArea(int cityID) {
		return this.getSqlSession().selectList("zone.findArea", cityID);
	}
	
	
}
