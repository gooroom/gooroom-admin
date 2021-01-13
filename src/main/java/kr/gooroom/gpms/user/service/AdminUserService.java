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

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

/**
 * administrator user management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface AdminUserService {

	/**
	 * generate administrator user list data
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readAdminUserList() throws Exception;

	/**
	 * generate administrator user list data for paging
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultPagingVO getAdminUserListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * get administrator user action logging data paging.
	 * <p>
	 * logging history data.
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultPagingVO getAdminRecordListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * check duplicate user id
	 * 
	 * @param adminId string user id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	StatusVO isExistAdminUserId(String adminId) throws Exception;

	/**
	 * create new administrator user data
	 * 
	 * @param vo AdminUserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createAdminUser(AdminUserVO vo) throws Exception;

	/**
	 * response administrator user information data
	 * 
	 * @param adminId string user id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO selectAdminUserData(String adminId) throws Exception;

	/**
	 * modify administrator user information data
	 * 
	 * @param vo AdminUserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO updateAdminUserData(AdminUserVO adminUserVO) throws Exception;

	/**
	 * modify current administrator user information data (pollingCycle)
	 * 
	 * @param vo AdminUserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO setCurrentAdminUserData(AdminUserVO adminUserVO) throws Exception;

	/**
	 * delete administrator user information data
	 * 
	 * @param vo AdminUserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteAdminUserData(AdminUserVO adminUserVO) throws Exception;

	/**
	 * get administrator user information data by user id and password.
	 * 
	 * @param adminId string user id
	 * @param adminPw string user password
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getAdminUserAuthAndInfo(String adminId, String adminPw) throws Exception;

	/**
	 * get administrator user action logging data.
	 * <p>
	 * logging history data.
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result data bean
	 * @throws Exception
	 */
	public ResultPagingVO getAdminActListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * clear administrator login trial info
	 * 
	 * @param 
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO updateLoginTrialData() throws Exception;

}
