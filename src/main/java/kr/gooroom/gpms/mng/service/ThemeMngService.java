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

import kr.gooroom.gpms.common.service.FileVO;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
	StatusVO createThemeData(ThemeVO themeVO, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception;

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
	StatusVO editThemeData(ThemeVO themeVO, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception;

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
	 * @param options themeId and iconAddress
	 * @return
	 * @throws Exception
	 */
	ResultVO getThemeData(HashMap<String, Object> options) throws Exception;

	/**
	 * restore file physically to specified location.
	 * @param newFileObj new File Object
	 * @param newFile new FileVO
	 * @param originFile origin FileVO
	 * @return
	 */
	FileVO restoreThemeFile(MultipartFile newFileObj, FileVO newFile, FileVO originFile);

	/**
	 * save file physically to specified location.
	 *
	 * @param file MultipartFile file request body
	 * @param themeId String
	 * @return FileVO saved file data
	 *
	 */
	FileVO storeIcons(MultipartFile file, String themeId);

	/**
	 * save file physically to specified location.
	 *
	 * @param fileVO FileVO
	 * @return FileVO saved file data
	 *
	 */
	FileVO storeIcons(FileVO fileVO);

	/**
	 * save file physically to specified location.
	 *
	 * @param fileType MultipartFile file request body
	 * @param voType FileVO
	 * @return FileVO saved file data
	 *
	 */
	FileVO storeWallpaper(MultipartFile fileType, FileVO voType);

}
