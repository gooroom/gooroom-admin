package kr.gooroom.gpms.dept.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.dept.service.DeptVO;

@Repository("deptDAO")
public class DeptDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(DeptDAO.class);

	/**
	 * Root 조직 정보
	 * 
	 * @param
	 * @return DeptVO
	 */
	public DeptVO selectRootChildrenDeptInfo() {
		DeptVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectRootChildrenDeptInfo");
		} catch (Exception ex) {
			re = null;
		}
		return re;
	}

	/**
	 * 하위 조직 리스트 (one depth)
	 * 
	 * @param
	 * @return List<DeptVO>
	 */
	public List<DeptVO> selectChildrenDeptList(HashMap<String, Object> map) {
		List<DeptVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectChildrenDeptList", map);
		} catch (Exception ex) {
			re = null;
		}
		return re;
	}

	/**
	 * 하위 조직 리스트 (one depth) by admin
	 * 
	 * @param
	 * @return List<DeptVO>
	 */
	public List<DeptVO> selectChildrenDeptListByAdmin(HashMap<String, Object> map) {
		List<DeptVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectChildrenDeptListByAdmin", map);
		} catch (Exception ex) {
			re = null;
		}
		return re;
	}

	/**
	 * 하위 조직 리스트 (all depth)
	 * 
	 * @param
	 * @return List<DeptVO>
	 */
	public List<DeptVO> selectAllChildrenDeptList(String deptCd) {

		List<DeptVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectAllChildrenDeptList", deptCd);
		} catch (Exception ex) {
			re = null;
		}

		return re;
	}

	/**
	 * 여러 조직의 하위 조직 리스트 (all depth)
	 * 
	 * @param
	 * @return List<DeptVO>
	 * @throws Exception
	 */
	public List<DeptVO> selectAllChildrenDeptListByParents(String[] deptCds) throws SQLException {

		List<DeptVO> re = null;
		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("deptCds", deptCds);
			re = sqlSessionMeta.selectList("selectAllChildrenDeptListByParents", map);
		} catch (Exception ex) {
			re = null;
		}

		return re;
	}
	
	/**
	 * 조직 정보 수정
	 * 
	 * @param vo DeptVD
	 * @return long
	 */
	public long updateDeptData(DeptVO vo) {
		return sqlSessionMeta.update("updateDeptData", vo);
	}

	/**
	 * 하위 조직 만료일 정보 수정
	 * 
	 * @param vo DeptVO
	 * @return long
	 */
	public long updateChildrenDeptExpireData(DeptVO vo) {
		return sqlSessionMeta.update("updateChildrenDeptExpireData", vo);
	}

	/**
	 * 조직 아이디 존재 여부 확인
	 * 
	 * @param deptCd String
	 * @return boolean
	 */
	public boolean isExistDeptCd(String deptCd) {
		boolean re = true;
		try {
			re = sqlSessionMeta.selectOne("isExistDeptCd", deptCd);
		} catch (Exception ex) {
			re = true;
		}
		return re;
	}
	
	/**
	 * 같은 조직 하위에 조직이 중복 되었는지 확인 
	 * 
	 * @param parentDeptCd string parent dept cd
	 * @param deptName string dept name
	 * @return boolean Boolean value
	 */
	public boolean isExistDeptNameByParentCd(String parentDeptCd, String deptName) {
		boolean re = true;
		try {
			if (parentDeptCd != null && parentDeptCd.length() > 0) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("parentDeptCd", parentDeptCd);
				map.put("deptName", deptName);
				re = sqlSessionMeta.selectOne("isExistDeptNameByParentCd", map);
			}
		} catch (Exception ex) {
			re = true;
			logger.error("error in isExistDeptNameByParentCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}
	
	/**
	 * 같은 조직 하위에 조직이 중복 되었는지 확인 
	 * 
	 * @param deptCd string
	 * @param deptName string dept name
	 * @return boolean Boolean value
	 */
	public boolean isExistDeptNameByDeptCd(String deptCd, String deptName) {
		boolean re = true;
		try {
			if (deptCd != null && deptCd.length() > 0) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("deptCd", deptCd);
				map.put("deptName", deptName);
				re = sqlSessionMeta.selectOne("isExistDeptNameByDeptCd", map);
			}
		} catch (Exception ex) {
			re = true;
			logger.error("error in isExistDeptNameByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}
	

	/**
	 * 조직정보 신규 등록 - 파일로 등록
	 * 
	 * @param vo DeptVO
	 * @return long
	 */
	public long createDeptRawData(DeptVO vo) {
		return sqlSessionMeta.insert("insertDeptRawData", vo);
	}

	/**
	 * 조직정보 신규 등록
	 * 
	 * @param vo DeptVO
	 * @return long
	 */
	public long createDeptMaster(DeptVO vo) {
		return sqlSessionMeta.insert("insertDeptMaster", vo);
	}

	/**
	 * 하위 포함하여 조직을 미허용으로 수정
	 * 
	 * @param vo DeptVO
	 * @return long
	 */
	public long updateDeptUnusedWithChildren(DeptVO vo) {
		return sqlSessionMeta.update("updateDeptUnusedWithChildren", vo);
	}

	/**
	 * 하위 포함하여 조직 정보 삭제
	 * 
	 * @param vo DeptVO
	 * @return long
	 */
	public long deleteDeptWithChildren(DeptVO vo) {
		return sqlSessionMeta.update("deleteDeptWithChildren", vo);
	}

	/**
	 * assign configuration data to dept
	 * 
	 * @param deptCd    string target dept cd
	 * @param regUserId string administrator user id
	 * @param configId  string configuration id
	 * @param configTp  string configuration type
	 * @return long query result count
	 */
	public long insertOrUpdateConfigWithDept(String deptCd, String regUserId, String configId, String configTp) {

		HashMap<String, String> map = new HashMap<>();
		map.put("deptCd", deptCd);
		map.put("regUserId", regUserId);
		map.put("configId", configId);
		map.put("configTp", configTp);

		return sqlSessionMeta.update("insertOrUpdateConfigWithDept", map);
	}
	
	/**
	 * assign admin's dept relation to new dept
	 * 
	 * @param deptCd    string target dept cd
	 * @param regUserId string administrator user id
	 * @return long query result count
	 */
	public long insertDeptInAdminRelation(String deptCd, String regUserId) {

		HashMap<String, String> map = new HashMap<>();
		map.put("deptCd", deptCd);
		map.put("regUserId", regUserId);

		return sqlSessionMeta.update("insertDeptInAdminRelation", map);
	}

	/**
	 * delete config data for dept
	 * 
	 * @param deptCd   string target dept cd
	 * @param configTp string configuration type
	 * @return long query result count
	 */
	public long deleteConfigWithDept(String deptCd, String configTp) {

		HashMap<String, String> map = new HashMap<>();
		map.put("deptCd", deptCd);
		map.put("configTp", configTp);
		return sqlSessionMeta.update("deleteConfigWithDept", map);
	}

	/**
	 * response dept information data.
	 * 
	 * @param deptCd string
	 * @return ClientGroupVO selected client group object
	 */
	public DeptVO selectDeptData(String deptCd) {

		HashMap<String, String> map = new HashMap<>();
		map.put("deptCd", deptCd);

		DeptVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectDeptData", map);
		} catch (Exception ex) {
			re = null;
			logger.error("error in selectDeptData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}
	
	/**
	 * response dept information data list.
	 * 
	 * @param deptCds string target dept cd array
	 * @return ClientGroupVO selected client group object
	 */
	public List<DeptVO> selectDeptNodeList(String[] deptCds) {

		List<DeptVO> re = null;
		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("deptCds", deptCds);
			
			re = sqlSessionMeta.selectList("selectDeptNodeList", map);
		} catch (Exception ex) {
			re = null;
			logger.error("error in selectDeptNodeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response dept list for paging.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return DeptVO List selected dept object
	 */
	public List<DeptVO> selectDeptListPaged(HashMap<String, Object> options) {

		List<DeptVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectDeptListPaged", options);

		} catch (Exception ex) {
			re = null;
			logger.error("error in selectDeptListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response filtered count for dept list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectDeptListFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectDeptListFilteredCount", options);
	}

	/**
	 * response total count for dept list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectDeptListTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectDeptListTotalCount", options);
	}
	
	/**
	 * response result delete dept table all
	 * 
	 * @return boolean
	 */
	public boolean deleteDeptAll() {
		boolean reFlag = false; 
		
		long existRowCount = sqlSessionMeta.selectOne("selectDeptALLCount");
		if(existRowCount > 0) {
			long deleteRowCount = sqlSessionMeta.delete("deleteDeptALL");
			return existRowCount == deleteRowCount;			
		} else {
			reFlag = true;
		}
		
		return reFlag;
	}
	
	/**
	 * insert dept history
	 * 
	 * @param chgTp string
	 * @param deptCd string
	 * @param regUserId string
	 * @return long query result count
	 * @throws SQLException
	 */
	public long createDeptHist(String chgTp, String deptCd, String regUserId) throws SQLException {
		
		HashMap<String, String> map = new HashMap<>();
		map.put("chgTp", chgTp);
		map.put("regUserId", regUserId);
		map.put("deptCd", deptCd);
		
		return sqlSessionMeta.insert("insertDeptHist", map);
	}
	
	/**
	 * delete dept data.
	 * 
	 * @param deptCd string target dept cd
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteDeptData(String deptCd) throws SQLException {
		return sqlSessionMeta.delete("deleteDeptData", deptCd);
	}
	
	/**
	 * delete dept mapping data with rule.
	 * 
	 * @param deptCd string target dept cd
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteDeptFromRule(String deptCd) throws SQLException {
		return sqlSessionMeta.delete("deleteDeptFromRule", deptCd);
	}

	/**
	 * delete dept mapping data with admin.
	 * 
	 * @param deptCd string target dept cd
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteDeptForAdmin(String deptCd) throws SQLException {
		return sqlSessionMeta.delete("deleteDeptForAdmin", deptCd);
	}

	/**
	 * delete dept mapping data with notify.
	 * 
	 * @param deptCd string target dept cd
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteDeptForNoti(String deptCd) throws SQLException {
		return sqlSessionMeta.delete("deleteDeptForNoti", deptCd);
	}
	
	/**
	 * insert users in dept
	 * 
	 * @param deptCd    string target dept cd
	 * @param userIds string array target user id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long updateDeptToUser(String deptCd, String[] userIds) throws SQLException {

		HashMap<String, Object> map = new HashMap<>();
		map.put("deptCd", deptCd);
		map.put("userIds", userIds);
		map.put("modUserId", LoginInfoHelper.getUserId());

		return sqlSessionMeta.update("updateDeptToUser", map);
	}
}
