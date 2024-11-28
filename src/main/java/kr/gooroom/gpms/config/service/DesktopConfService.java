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
 * Desktop configuration management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface DesktopConfService {

	/**
	 * create new desktop configuration data.
	 * 
	 * @param desktopConfNm    String desktop configuration name.
	 * @param desktopTheme String
	 * @param appDatas String
	 * @param adminType String
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createDesktopConf(String desktopConfNm, String desktopTheme, String[] appDatas, String adminType) throws Exception;

	/**
	 * create cloned desktop configuration data (copy).
	 * 
	 * @param desktopConfId String desktop configuration id.
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO cloneDesktopConf(String desktopConfId) throws Exception;

	/**
	 * edit desktop configuration data.
	 * 
	 * @param desktopConfId    String desktop configuration id.
	 * @param desktopConfNm    String desktop configuration name.
	 * @param desktopTheme String
	 * @param appDatas String
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO updateDesktopConf(String desktopConfId, String desktopConfNm, String desktopTheme, String[] appDatas)
			throws Exception;

	/**
	 * generate desktop configuration list data
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO readDesktopConfList() throws Exception;

	/**
	 * generate desktop configuration list data with paging.
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	ResultPagingVO getDesktopConfListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * generate desktop configuration data by desktop configuration id
	 * 
	 * @param desktopConfId string target desktop configuration id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getDesktopConfData(String desktopConfId) throws Exception;

	/**
	 * delete desktop configuration data by desktop configuration id
	 * 
	 * @param desktopConfId string target desktop configuration id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteDesktopConfData(String desktopConfId) throws Exception;

	/**
	 * generate desktop configuration data by client group id
	 * 
	 * @param groupId string client group id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getDesktopConfByGroupId(String groupId) throws Exception;

	/**
	 * generate desktop configuration data by dept cd
	 * 
	 * @param deptCd string dept cd
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getDesktopConfByDeptCd(String deptCd) throws Exception;

	/**
	 * generate desktop configuration data by user id
	 * 
	 * @param userId string user id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getDesktopConfByUserId(String userId) throws Exception;
}
