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

package kr.gooroom.gpms.site.service;

import java.util.HashMap;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

/**
 * site information management service interface
 * 
 * @author HNC
 */

public interface SiteMngService {

	/**
	 * generate site information list data
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readSiteMngList() throws Exception;

	/**
	 * generate site information list data for paging
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultPagingVO getSiteMngListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * create new site information data
	 * 
	 * @param vo SiteMngVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createSiteMng(SiteMngVO vo) throws Exception;

	/**
	 * response site information data
	 *
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO selectSiteMngData() throws Exception;

	/**
	 * modify site information data
	 * 
	 * @param vo SiteMngVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO updateSiteMngData(SiteMngVO siteMngVO) throws Exception;

	/**
	 * modify site's status data
	 * 
	 * @param vo SiteMngVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO updateSiteStatusData(SiteMngVO siteMngVO) throws Exception;

	/**
	 * delete site information data
	 * 
	 * @param vo SiteMngVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteSiteMngData(SiteMngVO siteMngVO) throws Exception;

}
