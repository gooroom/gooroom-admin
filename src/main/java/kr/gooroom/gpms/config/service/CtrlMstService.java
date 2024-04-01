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

package kr.gooroom.gpms.config.service;

import java.util.HashMap;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

/**
 * gooroom rule and configuration management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface CtrlMstService {

	/**
	 * response control item list data
	 * 
	 * @param mngObjTp string control item type
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readCtrlItemList(String mngObjTp) throws Exception;

	/**
	 * response control item and property list data
	 * 
	 * @param mngObjTp string control item type
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readCtrlItemAndPropList(String mngObjTp) throws Exception;

	/**
	 * response control item and property list data with Paging.
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultPagingVO readCtrlItemAndPropListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * response control item data by item id
	 * 
	 * @param objId string control item id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readCtrlItem(String objId) throws Exception;

	/**
	 * response control property list by item id
	 * 
	 * @param objId string control item id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readCtrlPropList(String objId) throws Exception;

	/**
	 * create new control item and property data
	 * 
	 * @param itemVo CtrlItemVO control item object bean
	 * @param propVo CtrlPropVO array control property object array
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createCtrlItem(CtrlItemVO itemVo, CtrlPropVO[] propVo) throws Exception;

	/**
	 * create cloned control item and property data
	 * 
	 * @param itemVo CtrlItemVO control item object bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO cloneCtrlItem(CtrlItemVO itemVo) throws Exception;

	/**
	 * create default control item and property data
	 * 
	 * @param itemVo CtrlItemVO control item object bean
	 * @param propVo CtrlPropVO array control property object array
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createCtrlDefaultItem(CtrlItemVO itemVo, CtrlPropVO[] propVo) throws Exception;

	/**
	 * modify control item and property data
	 * 
	 * @param itemVo CtrlItemVO control item object bean
	 * @param propVo CtrlPropVO array control property object array
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO updateCtrlItem(CtrlItemVO itemVo, CtrlPropVO[] propVo) throws Exception;

	/**
	 * delete control item and property data
	 * 
	 * @param objId    string item id
	 * @param ctrlType string control type
	 * @param confTp   string configuration type
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteCtrlItem(String objId, String ctrlType, String confTp) throws Exception;

	/**
	 * response client group id by configuration id
	 * 
	 * @param confId string configuration id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readClientGroupIdByConfId(String confId) throws Exception;

	/**
	 * response configuration id by client group and configuration type
	 * 
	 * @param groupId  string client group id
	 * @param confType string configuration type
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readConfIdInClientGroupId(String groupId, String confType) throws Exception;

	/**
	 * response total rule id.
	 * 
	 * @param userId   string user id
	 * @param clientId string client id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getRuleIdsByClientAndUser(String userId, String clientId) throws Exception;

	/**
	 * response rule ids by Group id.
	 * 
	 * @param groupId string group id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getRuleIdsByGroupId(String groupId) throws Exception;

	/**
	 * response control item data by group id
	 * 
	 * @param groupId string group id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readCtrlItemByGroupId(String groupId) throws Exception;

	/**
	 * response control item data by dept cd
	 * 
	 * @param deptCd string
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readCtrlItemByDeptCd(String deptCd) throws Exception;

	/**
	 * response control item data by user id
	 * 
	 * @param userId string
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readCtrlItemByUserId(String userId) throws Exception;

	/**
	 * 정책 - 적용 그룹 리스트
	 */
	ResultPagingVO readActivateGroupListPaged(HashMap<String, Object> options) throws Exception;

}
