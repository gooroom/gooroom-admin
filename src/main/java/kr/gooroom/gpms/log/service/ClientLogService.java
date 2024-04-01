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

package kr.gooroom.gpms.log.service;

import java.util.HashMap;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

/**
 * GPMS logging management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface ClientLogService {

	/**
	 * response general log list data.
	 * 
	 * @param fromDate string from date.
	 * @param toDate   string to date.
	 * @param logItem  string log type for select.
	 * @return ResultVO result object
	 * @throws Exception
	 */
	public ResultVO getGeneralLogList(String fromDate, String toDate, String logItem) throws Exception;

	/**
	 * response general log list data paged.
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	public ResultPagingVO getGeneralLogListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * get security log list data paged
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	public ResultPagingVO getSecurityLogListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * delete user client use hist
	 *
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	public StatusVO deleteUserClientUseHist(HashMap<String, Object> options) throws Exception;

}
