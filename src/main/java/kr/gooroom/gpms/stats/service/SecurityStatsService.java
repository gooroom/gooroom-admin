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
 * gooroom security statistics data management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface SecurityStatsService {

	/**
	 * generate daily attacked count data
	 * 
	 * @param fromDate String from date
	 * @param toDate   String to date
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getViolatedCount(String fromDate, String toDate, String countType) throws Exception;

	/**
	 * generate protector attacked data list by specified date.
	 * 
	 * @param searchType String search type
	 * @param searchDate String search date.
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getViolatedList(String searchType, String searchDate) throws Exception;

	/**
	 * generate protector attacked data list by specified date and paged
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result data bean
	 * @throws Exception
	 */
	ResultPagingVO getViolatedListPaged(HashMap<String, Object> options) throws Exception;

}
