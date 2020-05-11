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
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

/**
 * Desktop application management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface DesktopAppService {

	/**
	 * create new desktop application data
	 * 
	 * @param desktopAppVO DesktopAppVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createDesktopApp(DesktopAppVO desktopAppVO) throws Exception;

	/**
	 * create cloned desktop application data
	 * 
	 * @param appId String app id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO cloneDesktopApp(String appId) throws Exception;

	/**
	 * delete desktop application data
	 * 
	 * @param desktopAppId String desktop application id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteDesktopApp(String desktopAppId) throws Exception;

	/**
	 * modify desktop application data
	 * 
	 * @param desktopAppVO DesktopAppVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO editDesktopApp(DesktopAppVO desktopAppVO) throws Exception;

	/**
	 * get desktop application list data
	 * 
	 * @param contextAddress String use desktop application.
	 * @return ResultVO
	 * @throws Exception
	 */
	ResultVO getDesktopAppList() throws Exception;

	/**
	 * get desktop application list data for paging
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultVO
	 * @throws Exception
	 */
	ResultPagingVO getDesktopAppListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * get desktop application information data by id
	 * 
	 * @param desktopAppId String desktop application id.
	 * @return ResultVO
	 * @throws Exception
	 */
	ResultVO getDesktopAppData(String desktopAppId) throws Exception;

}
