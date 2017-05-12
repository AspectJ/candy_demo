package com.rest.content.program.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cp.bean.Archive;
import com.cp.bean.Program;
import com.cp.filter.BaseDao;


@Repository("programDao")
public class ProgramDaoImpl extends BaseDao {

	/**
	 * 添加information栏目信息
	 * @param program_name
	 */
	public void addProgram(Map<String, Object> paramsMap) {
		this.getSqlSession().insert("program.addProgram", paramsMap);
	}

	/**
	 * 添加栏目归档属性
	 * @param paramsMap
	 */
	public void addArchive(Map<String, Object> paramsMap) {
		this.getSqlSession().insert("program.addArchive", paramsMap);
	}


	/**
	 * 根据栏目查询information
	 * @param program_id
	 */
	public List<Map<String, Object>> findInfoByProgram(int program_id) {
		return this.getSqlSession().selectList("program.findInfoByProgram", program_id);
	}

	/**
	 * 删除栏目信息(前提：该栏目为空栏目)
	 * @param program_id
	 */
	public void deleteProgram(int program_id) {
		this.getSqlSession().delete("program.deleteProgram", program_id);
	}

	/**
	 *	删除空栏目的归档属性
	 * @param program_id
	 */
	public void deleteArchive(int program_id) {
		this.getSqlSession().delete("program.deleteArchive", program_id);
	}
	
	/**
	 * 更新栏目的栏目信息(名称)
	 * @param program_id
	 */
	public void updateProgram(Map<String, Object> paramsMap) {
		this.getSqlSession().update("program.updateProgram", paramsMap);
	}
	
	/**
	 * 根据栏目id查询栏目名称
	 * @param program_id
	 * @return
	 */
	public String findProgramNameById(String program_id) {
		return this.getSqlSession().selectOne("program.findProgramNameById", program_id);
	}

	/**
	 * 更改栏目的可用状态
	 * @param program_id
	 */
	public void changeProgramStatus(Map<String, Object> paramsMap) {
		this.getSqlSession().update("program.changeProgramStatus", paramsMap);
	}

	/**
	 * 查询所有栏目
	 */
	public List<Program> getAllProgram(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("program.getAllProgram", paramsMap);
	}
	
	/**
	 * 查询count
	 * @return
	 */
	public Integer getAllProgramCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("program.getAllProgramCount", paramsMap);
	}

	/**
	 * 根据影院归档查询其可看的目录(program)
	 * @param object
	 * @return
	 */
	public List<Program> getProgramListByArchive(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("program.getProgramListByArchive", paramsMap);
	}
	
	/**
	 * 查找count
	 * @param i
	 * @return
	 */
	public Integer getProgramListByArchiveCount(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectOne("program.getProgramListByArchiveCount", paramsMap);
	}

	/**
	 * 验证栏目名重复性
	 * @param paramsMap
	 */
	public List<Map<String, Object>> checkRepeat(Map<String, Object> paramsMap) {
		return this.getSqlSession().selectList("program.checkRepeat", paramsMap);
	}
	
	
	/**
	 * 根据栏目查询归档信息
	 */
	public List<Archive> findProgramArchive(int program_id) {
		return this.getSqlSession().selectList("program.findProgramArchive", program_id);
	}

	
}
