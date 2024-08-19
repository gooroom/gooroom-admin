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

public interface ClientGroupService {

	/**
	 * generate client group list data
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO selectClientGroupList() throws Exception;

	/**
	 * generate client group list data for paging.
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	ResultPagingVO getClientGroupListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * generate client group information data
	 * 
	 * @param groupId string target group id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO readClientGroupData(String groupId) throws Exception;

	/**
	 * generate client group information data list
	 * 
	 * @param groupIds string target group id array
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO readClientGroupNodeList(String[] groupIds) throws Exception;

	/**
	 * get client group information data
	 * 
	 * @param groupId string target group id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ClientGroupVO getClientGroupData(String groupId) throws Exception;

	/**
	 * create new client group data
	 * 
	 * @param clientGroupVO ClientGroupVO group data bean
	 * @param isDefault     boolean which default group
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	StatusVO createClientGroup(ClientGroupVO clientGroupVO, boolean isDefault) throws Exception;

	/**
	 * check duplicate client group name in group id
	 * 
	 * @param parentGrpId string parent group id
	 * @param groupName string target group name
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	StatusVO isExistGroupNameByParentId(String parentGrpId, String groupName) throws Exception;

	/**
	 * check duplicate client group name in group id
	 * 
	 * @param grpId   string target group id
	 * @param groupName string target group name
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	StatusVO isExistGroupNameByGroupId(String grpId, String groupName) throws Exception;

	/**
	 * delete client group
	 * 
	 * @param groupId string target group id
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	StatusVO deleteClientGroup(String groupId) throws Exception;

	/**
	 * delete client group array
	 * 
	 * @param groupIds string[] target group id array
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	StatusVO deleteClientGroupList(String[] groupIds, String isDeleteClient) throws Exception;

	/**
	 * change(update) client group
	 * 
	 * @param clientGroupVO ClientGroupVO group data
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	StatusVO updateClientGroup(ClientGroupVO clientGroupVO) throws Exception;

	/**
	 * setting group info in client data (insert or delete client in group)
	 * 
	 * @param groupId     string target group id
	 * @param client_list string array that client id list
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	StatusVO updateGroupToClient(String groupId, String[] client_list) throws Exception;

	/**
	 * check exist default client group
	 * 
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	StatusVO isExistDefaultClientGroup() throws Exception;

	/**
	 * response client id array in group
	 * 
	 * @param groupId string client group id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getOnlineClientIdsByGroupId(String groupId) throws Exception;

	/**
	 * response client id array in group array
	 * 
	 * @param groupId string client group array
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getOnlineClientIdsByGroupList(String[] groupId) throws Exception;

	/**
	 * response client id array in group array
	 * 
	 * @param groupIds string[] client group id array
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readClientIdsByGroupIdList(String[] groupIds) throws Exception;

	/**
	 * response client group children id array
	 * 
	 * @return ResultVO
	 * @throws Exception
	 */
	ResultVO getChildrenClientGroupList(String grpId, String hasWithRoot) throws Exception;

	/**
	 * update group rule and conf
	 * 
	 * @param grpId string
	 * @param cfgId string
	 * @param confType string
	 * @return StatusVO
	 * @throws Exception
	 */
	StatusVO updateClientGroupConf(String grpId, String cfgId, String confType) throws Exception;

	/**
	 * get child group list
	 * 
	 * @param grpId string
	 * @return ResultVO
	 * @throws Exception
	 */
	ResultVO getAllChildrenGroupList(String grpId) throws Exception;

	/**
	 * 여러그룹 정책정보 일괄수정
	 * 
	 * @return StatusVO
	 * @throws Exception
	 */
	StatusVO updateRuleInfoToMultiGroup(String[] grpIds, String clientConfigId, String hostNameConfigId,
			String updateServerConfigId, String browserRuleId, String mediaRuleId, String securityRuleId,
			String filteredSoftwareRuleId, String ctrlCenterItemRuleId, String policyKitRuleId, String desktopConfId) throws Exception;

}
