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
 * Theme management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface ThemeMngService {

	/**
	 * create new theme data
	 * 
	 * @param themeVO ThemeVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createThemeData(ThemeVO themeVO) throws Exception;

	/**
	 * delete theme data.
	 * 
	 * @param themeId String theme id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteThemeData(String themeId) throws Exception;

	/**
	 * edit theme data
	 * 
	 * @param themeVO ThemeVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO editThemeData(ThemeVO themeVO) throws Exception;

	/**
	 * response theme list data for paging
	 * 
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	ResultPagingVO getThemeListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * response theme list data
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getThemeList() throws Exception;

	/**
	 * response theme information data by theme id
	 * 
	 * @param themeId String theme id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getThemeData(HashMap<String, Object> options) throws Exception;

}
