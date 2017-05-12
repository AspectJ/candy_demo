package com.rest.archive.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cp.filter.BaseDao;

@Repository("archiveDao")
public class ArchiveDaoImpl extends BaseDao {

	/**
	 * 查询全部归档列表
	 * @return
	 */
	public List<Map<String, Object>> findAllArchive() {
		return this.getSqlSession().selectList("archive.findAllArchive");
	}

	
	
}
