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

package kr.gooroom.gpms.mng.service;

import java.util.HashMap;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.StatusVO;

/**
 * Client configuration management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface ClientMngService {

	/**
	 * response registration key data
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	ResultPagingVO getRegKeyList(HashMap<String, Object> options) throws Exception;

	/**
	 * create new client registration key data
	 * 
	 * @param clientRegKeyVO ClientRegKeyVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createRegKeyData(ClientRegKeyVO clientRegKeyVO) throws Exception;

	/**
	 * update client registration key data
	 * 
	 * @param clientRegKeyVO ClientRegKeyVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO editRegKeyData(ClientRegKeyVO clientRegKeyVO) throws Exception;

	/**
	 * delete client registration key data
	 * 
	 * @param regKeyNo String
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteRegKeyData(String regKeyNo) throws Exception;

	/**
	 * create profile set by reference client id, start job.
	 * 
	 * @param vo ClientProfileSetVO
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createProfileSetService(ClientProfileSetVO vo) throws Exception;

	/**
	 * response profile set data list with paging.
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	ResultPagingVO getProfileSetListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * response package list data in profile set.
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	ResultPagingVO getProfilePackageListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * delete profile set data
	 * 
	 * @param profileNo String
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteProfileSetData(String profileNo) throws Exception;

	/**
	 * update profile set data
	 * 
	 * @param clientProfileSetVO ClientProfileSetVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO editProfileSetData(ClientProfileSetVO clientProfileSetVO) throws Exception;

	/**
	 * create new client software data
	 * 
	 * @param clientSoftwareVO ClientSoftwareVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createClientSoftware(ClientSoftwareVO clientSoftwareVO) throws Exception;

	/**
	 * delete client software data
	 * 
	 * @param swId String
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteClientSoftware(String swId) throws Exception;

	/**
	 * update client software data
	 * 
	 * @param  clientSoftwareVO ClientSoftwareVO
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO editClientSoftware(ClientSoftwareVO clientSoftwareVO) throws Exception;

	/**
	 * response client software data
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultPagingVO getClientSoftwareList(HashMap<String, Object> options) throws Exception;

}
