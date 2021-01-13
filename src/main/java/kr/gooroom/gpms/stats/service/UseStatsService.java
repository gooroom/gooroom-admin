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

package kr.gooroom.gpms.stats.service;

import java.util.HashMap;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;

/**
 * Amount of used gooroom service statistics data management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface UseStatsService {

	/**
	 * generate daily login action count data
	 * 
	 * @param fromDate String start date
	 * @param toDate   String end date
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getLoginDailyCount(String fromDate, String toDate) throws Exception;

	/**
	 * generate login action count by type and specified data
	 * 
	 * @param searchType String search type
	 * @param searchDate String search date
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getLoginList(String searchType, String searchDate) throws Exception;

	/**
	 * generate login action count by type and specified data and paged
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result data bean
	 * @throws Exception
	 */
	ResultPagingVO getLoginListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * generate daily client create and revoke action count data
	 * 
	 * @param fromDate String start date
	 * @param toDate   String end date
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getClientMngCount(String fromDate, String toDate) throws Exception;

	/**
	 * generate client list data paged
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result data bean
	 * @throws Exception
	 */
	ResultPagingVO getClientMngListPaged(HashMap<String, Object> options) throws Exception;

}
