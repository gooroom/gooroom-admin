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

package kr.gooroom.gpms.client.service;

import java.util.HashMap;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

/**
 * Client management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface ClientService {

	/**
	 * modify client information
	 * 
	 * @param clientVO ClientVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO updateClientInfo(ClientVO clientVO) throws Exception;

	/**
	 * get client list data for all page (common api)
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	ResultPagingVO getClientList(HashMap<String, Object> options) throws Exception;

	/**
	 * generate client list data in group
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	ResultPagingVO selectClientListForGroups(HashMap<String, Object> options) throws Exception;

	/**
	 * generate client list data for insert client group
	 * 
	 * @param groupId string target group id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getClientListForAddingGroup(String groupId) throws Exception;

	/**
	 * generate client information data
	 * 
	 * @param clientId string target client id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO selectClientInfo(String clientId) throws Exception;

	/**
	 * check duplicate client id
	 * 
	 * @param clientId string target client id
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	StatusVO isExistClientId(String clientId) throws Exception;

	/**
	 * revoke client certificate for delete client
	 * 
	 * @param clientId string target client id
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	StatusVO revokeClientCertificate(String clientId) throws Exception;

	/**
	 * generate client group data list
	 * 
	 * @param groupId string target group id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getClientListInGroup(String groupId) throws Exception;

	/**
	 * generate online client data list
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getClientListInOnline() throws Exception;

	/**
	 * generate online client data
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getClientInOnline(String gubun) throws Exception;

	/**
	 * generate online client data list include client group configuration
	 * 
	 * @param confId string rule configuration id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getOnlineClientIdsInClientUseConfId(String confId, String confTp) throws Exception;

	/**
	 * generate online client data list include user id
	 * 
	 * @param userIds string array user id list
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getOnlineClientIdsInUserIds(String[] userIds) throws Exception;

	/**
	 * generate online client data list include client configuration id
	 * 
	 * @param confId string configuration id
	 * @param confTp string configuration type
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getOnlineClientIdsInClientConf(String confId, String confTp) throws Exception;

	/**
	 * generate online client data list include client id
	 *
	 * @param clientId string client id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getOnlineClientIdByClientId(String clientId) throws Exception;

	/**
	 * generate client list that attacked protector rule
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	ResultPagingVO getViolatedClientList(HashMap<String, Object> options) throws Exception;

	/**
	 * generate client count
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getViolatedClientCount() throws Exception;

	/**
	 * generate client list data
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getClientAllList() throws Exception;

	/**
	 * generate client count data by status
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getClientStatusSummary() throws Exception;

	/**
	 * generate user login count data by status
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getLoginStatusSummary() throws Exception;

	/**
	 * generate package count by client and need update
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getUpdatePackageSummary() throws Exception;

	/**
	 * generate client list data that need update package.
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getUpdatePackageClientList() throws Exception;

	/**
	 * generate online client data list by noticePublishId
	 * 
	 * @param noticePublishId
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getClientListForNoticeInstantNotice(String noticePublishId) throws Exception;

}
