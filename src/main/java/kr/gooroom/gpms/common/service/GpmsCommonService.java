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

package kr.gooroom.gpms.common.service;

/**
 * Interface for common service
 * <p>
 * get server information, insert administrator log, manage connection ip. etc.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface GpmsCommonService {

	/**
	 * response servers network information.
	 * <p>
	 * GPMS, GLM, GKM, GRM.
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getGpmsServersInfo() throws Exception;

	/**
	 * create(insert) administrator user action task in logging table.
	 * 
	 * @param actType  string action type
	 * @param actItem  string action item
	 * @param actData  string action date like parameter
	 * @param accessIp string network access ip information
	 * @param userId   string user id
	 * @return StatusVO result status data
	 * @throws Exception
	 */
	StatusVO createUserActLogHistory(String actType, String actItem, String actData, String accessIp, String userId)
			throws Exception;

	/**
	 * response available network ip information.
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getGpmsAvailableNetwork() throws Exception;

	/**
	 * response available network ip information by admin id.
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getGpmsAvailableNetworkByAdminId(String adminId) throws Exception;
}
