/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.gooroom.gpms.user.service;

import java.util.HashMap;
import java.util.List;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * user management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface UserService {

	/**
	 * generate user list data
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readUserList() throws Exception;

	/**
	 * generate user list data for paging
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	ResultPagingVO getUserListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * check duplicate user id
	 * 
	 * @param userId string user id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	StatusVO isNoExistUserId(String userId) throws Exception;

	/**
	 * create new user data
	 * 
	 * @param userVO UserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createUserData(UserVO userVO) throws Exception;

	/**
	 * create new user data with rule
	 * 
	 * @param userVO UserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createUserDataWithRule(UserVO userVO) throws Exception;

	/**
	 * response user information data
	 * 
	 * @param adminId string user id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readUserData(String userId) throws Exception;

	/**
	 * modify user information data
	 * 
	 * @param userVO UserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO updateUserData(UserVO userVO) throws Exception;

	/**
	 * reset user login trial count
	 * 
	 * @param userId String user id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO updateUserLoginTrialCount(String userId) throws Exception;

	/**
	 * delete user information data
	 * 
	 * @param userId String user id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteUserData(String userId) throws Exception;

	/**
	 * register NFC data to user information data
	 * 
	 * @param userVO UserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO editUserNfcData(UserVO userVO) throws Exception;

	/**
	 * response user list data in organization specified.
	 * 
	 * @param deptCd string organization id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getUserListInDept(String deptCd) throws Exception;

	/**
	 * response user list data in organization specified for paging
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	ResultPagingVO getUserListPagedInDept(HashMap<String, Object> options) throws Exception;

	/**
	 * response user list data in organization array specified.
	 * 
	 * @param deptCds string array organization id list
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getUserListInDeptArray(String[] deptCds) throws Exception;

	/**
	 * response user list data in online user.
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getUserListInOnline() throws Exception;

	/**
	 * get user configuration information data by user id.
	 * 
	 * @param userId String user id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getUserConfIdByUserId(String userId) throws Exception;

	/**
	 * get user configuration information data by dept code from user id.
	 * 
	 * @param userId String user id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getUserConfIdByDeptCdFromUserId(String userId) throws Exception;

	/**
	 * get user configuration information data by dept code.
	 * 
	 * @param DeptCd String dept code
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getUserConfIdByDeptCd(String deptCd) throws Exception;

	/**
	 * get user configuration information data by group id.
	 * 
	 * @param groupId String group id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getUserConfIdByGroupId(String groupId) throws Exception;

	/**
	 * insert user data into organization.
	 * 
	 * @param deptCd    String organization id
	 * @param user_list String array user id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createUsersInDept(String deptCd, String[] user_list) throws Exception;

	/**
	 * 사용자 정보 일괄 등록 헤드 리스트 유효성 체크
	 * @param headList
	 * @return
	 * @throws Exception
	 */
	StatusVO isUserHeadListExist(List<String> headList) throws Exception;

	/**
	 * 사용자 정보 일괄 등록 필수값 체크
	 * @param rowData
	 * @return
	 * @throws Exception
	 */
	StatusVO isRequiredDataExist(List<String> rowData) throws Exception;

	/**
	 * 사용자 정보 일괄 등록 체크(파일자료)
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	ResultVO isCanUpdateUserDataFromFile(List<List<String>> dataList) throws Exception;

	/**
	 * insert user data all from file.
	 * @param userVOs
	 * @return
	 * @throws Exception
	 */
	StatusVO updateUserDataFromFile(List<UserVO> userVOs) throws Exception;

	/**
	 * 사용자 정보 일괄 다운로드
	 *
	 * @return XSSFWorkbook
	 * @throws Exception
	 */
	XSSFWorkbook createUserFileFromData() throws Exception;

	/**
	 * 사용자 정보 업로드 샘플 다운로드
	 */
	XSSFWorkbook createUserSampleFileFromData() throws Exception;

}
