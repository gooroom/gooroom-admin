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

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

/**
 * Icon management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface IconMngService {

	/**
	 * create new icon data
	 * 
	 * @param iconVO IconVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createIconData(IconVO iconVO) throws Exception;

	/**
	 * delete icon data.
	 * 
	 * @param iconId String icon id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteIconData(String iconId) throws Exception;

	/**
	 * edit icon data
	 * 
	 * @param iconVO IconVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO editIconData(IconVO iconVO) throws Exception;

	/**
	 * response icon list data by group id
	 * 
	 * @param grpId string group id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getIconList(String grpId) throws Exception;

	/**
	 * response icon list data that not include any group.
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getNoGroupIconList() throws Exception;

	/**
	 * response icon information data by icon id
	 * 
	 * @param iconId String icon id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getIconData(String iconId) throws Exception;

	/**
	 * create new icon group data
	 * 
	 * @param iconGroupVO IconGroupVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createIconGroupData(IconGroupVO iconGroupVO) throws Exception;

	/**
	 * delete icon group data
	 * 
	 * @param iconGroupId String icon group id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteIconGroupData(String iconGroupId) throws Exception;

	/**
	 * modify icon group data.
	 * 
	 * @param iconGroupVO IconGroupVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO editIconGroupData(IconGroupVO iconGroupVO) throws Exception;

	/**
	 * response icon group list data
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getIconGroupList() throws Exception;

	/**
	 * response icon group information data
	 * 
	 * @param iconGroupId string icon group id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getIconGroupData(String iconGroupId) throws Exception;

	/**
	 * insert icon data into icon group
	 * 
	 * @param grpId     string icon group id
	 * @param icon_list string array that icon id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createIconsInGroup(String grpId, String[] icon_list) throws Exception;

	/**
	 * remove icon from into icon group
	 * 
	 * @param grpId  string icon group id
	 * @param iconId string icon id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteIconInGroup(String grpId, String iconId) throws Exception;

}
