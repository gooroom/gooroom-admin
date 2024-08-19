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

package kr.gooroom.gpms.csp.service;

import java.util.HashMap;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

/**
 * Gooroom cloud service provider management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

//TODO

public interface CspService {

	/**
	 * check csp id for duplicate.
	 * 
	 * @param gcspId string target csp id.
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO isExistGcspId(String gcspId) throws Exception;

	/**
	 * create new gooroom csp configuration data.
	 * 
	 * @param cspVO CspVO csp cofiguration data bean.
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createGcspData(CspVO cspVO) throws Exception;

	/**
	 * modify gooroom csp information
	 * 
	 * @param cspVO CspVO csp cofiguration data bean.
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO editGcspData(CspVO cspVO) throws Exception;

	/**
	 * generate gooroom csp list data.
	 * 
	 * @param statusCd   string status value.
	 * @param search_key string search keyword.
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getGcspDataList(String statusCd, String search_key) throws Exception;

	/**
	 * generate gooroom csp list data paging
	 * 
	 * @param options HashMap.
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultPagingVO getGcspListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * generate gooroom csp data by csp id.
	 * 
	 * @param gcspId string csp id.
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getGcspData(String gcspId) throws Exception;

	/**
	 * generate certificate for csp from CSR.
	 * 
	 * @param vo CspVO csp cofiguration data bean.
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO createGCSPCertificateFromCSR(CspVO vo) throws Exception;

	/**
	 * generate certificate for csp.
	 * 
	 * @param vo CspVO csp cofiguration data bean.
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO createGCSPCertificate(CspVO vo) throws Exception;

	/**
	 * delete csp information data
	 * 
	 * @param vo CspVO csp cofiguration data bean.
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteGcspData(CspVO vo) throws Exception;
}
