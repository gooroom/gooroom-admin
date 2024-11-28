package kr.gooroom.gpms.dept.service;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;
import java.util.List;

/**
 * @Class Name : UserService.java
 * @Description : UserService
 * @Modification Information
 *
 * @author syhan
 * @since 2017-06-20
 * @version 1.0
 * @see
 * 
 * @	Copyright (C) All right reserved.
 */

public interface DeptService {

	/**
	 * 조직 트리에 하위 조직 리스트 조회
	 * 
	 * @param
	 * @return ResultVO
	 * @throws Exception
	 */
	ResultVO getChildrenDeptList(String deptCd, String hasWithRoot) throws Exception;

	/**
	 * 조직 트리에 모든 하위 조직 리스트 조회
	 * 
	 * @param
	 * @return ResultVO
	 * @throws Exception
	 */
	ResultVO getAllChildrenDeptList(String deptCd) throws Exception;

	/**
	 * 조직 정보 조회
	 * 
	 * @param
	 * @return ResultVO
	 * @throws Exception
	 */
	ResultVO getDeptData(String deptCd) throws Exception;

	/**
	 * generate dept information data list
	 * 
	 * @param deptCds string target dept cd array
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO readDeptNodeList(String[] deptCds) throws Exception;

	/**
	 * 조직 정보 수정
	 * 
	 * @param deptVO DeptVO
	 * @return StatusVO
	 * @throws Exception
	 */
	StatusVO updateDeptData(DeptVO deptVO) throws Exception;

	/**
	 * 하위 조직 만료일 정보 수정
	 * 
	 * @param deptVO DeptVO
	 * @return StatusVO
	 * @throws Exception
	 */
	StatusVO updateChildrenDeptExpireData(DeptVO deptVO) throws Exception;

	/**
	 * 여러조직 정책정보 일괄수정
	 * 
	 * @return StatusVO
	 * @throws Exception
	 */
	StatusVO updateRuleInfoToMultiDept(String[] deptCds, String browserRuleId, String mediaRuleId,
			String securityRuleId, String filteredSoftwareRuleId, String ctrlCenterItemRuleId, String policyKitRuleId, String desktopConfId) throws Exception;

	/**
	 * 조직 연동 설정 수정
	 * 
	 * @param deptCd DeptVO
	 * @param cfgId string
	 * @param confType string
	 * @return StatusVO
	 * @throws Exception
	 */
	StatusVO updateDeptConf(String deptCd, String cfgId, String confType) throws Exception;

	/**
	 * 조직 아이디 중복 검사
	 * 
	 * @param deptCd string
	 * @return StatusVO
	 * @throws Exception
	 */
	StatusVO isExistDeptCd(String deptCd) throws Exception;
	
	/**
	 * 조직 이름 중복 검사 by parent deptCd
	 * 
	 * @param parentDeptCd string
	 * @param deptName string
	 * @return StatusVO
	 * @throws Exception
	 */
	StatusVO isExistDeptNameByParentCd(String parentDeptCd, String deptName) throws Exception;

	/**
	 * 조직 이름 중복 검사 by deptCd
	 * 
	 * @param deptCd string
	 * @param deptName string
	 * @return StatusVO
	 * @throws Exception
	 */
	StatusVO isExistDeptNameByDeptCd(String deptCd, String deptName) throws Exception;

	/**
	 * 신규 조직 정보 등록
	 * 
	 * @param deptVO DeptVO
	 * @return StatusVO
	 * @throws Exception
	 */
	StatusVO createDeptData(DeptVO deptVO) throws Exception;

	/**
	 * 하위 포함하여 조직 정보 삭제
	 * 
	 * @param deptCd string
	 * @return StatusVO
	 * @throws Exception
	 */
	StatusVO deleteDeptWithChildren(String deptCd) throws Exception;

	/**
	 * delete dept array
	 * 
	 * @param deptCds string[] target dept cd array
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	StatusVO deleteDeptList(String[] deptCds, String isDeleteUser) throws Exception;
	
	/**
	 * 하위 포함하여 조직을 미허용으로 수정
	 * 
	 * @param deptCd string
	 * @return StatusVO
	 * @throws Exception
	 */
	StatusVO updateDeptUnusedWithChildren(String deptCd) throws Exception;

	/**
	 * 조직 리스트 조회 - 페이징 
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultVO
	 * @throws Exception
	 */
	ResultPagingVO getDeptListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * 조직 정보 일괄 등록 체크(파일자료)
	 *
	 * @param dataList List<List<String>> dataList
	 * @return StatusVO
	 * @throws Exception
	 */
	ResultVO isCanUpdateDeptDataFromFile(List<List<String>> dataList) throws Exception;

	/**
	 * 조직 정보 일괄 등록 (파일자료)
	 * 
	 * @param deptVOs DeptVOs
	 * @return StatusVO
	 * @throws Exception
	 */
	StatusVO updateDeptDataFromFile(List<DeptVO> deptVOs) throws Exception;

	/**
	 * 조직 정보 일괄 등록 헤드 리스트 유효성 체크
	 * @param headList
	 * @return
	 * @throws Exception
	 */
	StatusVO isDeptHeadListExist(List<String> headList) throws Exception;

	/**
	 * 조직 정보 일괄 등록 필수값 체크
	 * @param rowData
	 * @return
	 * @throws Exception
	 */
	StatusVO isRequiredDataExist(List<String> rowData) throws Exception;

	/**
	 * 조직 정보 일괄 다운로드
	 *
	 * @return XSSFWorkbook
	 * @throws Exception
	 */
	XSSFWorkbook createDeptFileFromData() throws Exception;

	/**
	 * 조직 정보 업로드 샘플 다운로드
	 * @return
	 * @throws Exception
	 */
	XSSFWorkbook createDeptSampleFileFromData() throws Exception;
}
