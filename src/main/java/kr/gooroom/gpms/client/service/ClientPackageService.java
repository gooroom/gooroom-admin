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

/**
 * Client package information service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface ClientPackageService {

	/**
	 * generate total package list data
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO bean for package data result
	 * @throws Exception
	 */
	ResultPagingVO readTotalPackageListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * generate package list data in client
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO bean for package data result
	 * @throws Exception
	 */
	ResultPagingVO readPackageListPagedInClient(HashMap<String, Object> options) throws Exception;

	/**
	 * generate total package list data
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO bean for package data result
	 * @throws Exception
	 */
	ResultPagingVO readTotalPackageList(HashMap<String, Object> options) throws Exception;

}
