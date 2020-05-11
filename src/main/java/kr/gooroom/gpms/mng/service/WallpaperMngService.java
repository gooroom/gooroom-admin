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

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

/**
 * Wallpaper management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface WallpaperMngService {

	/**
	 * create new wallpaper data
	 * 
	 * @param wallpaperVO wallpaperVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createWallpaperData(WallpaperVO wallpaperVO) throws Exception;

	/**
	 * delete wallpaper data.
	 * 
	 * @param wallpaperId String wallpaper id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO deleteWallpaperData(String wallpaperId) throws Exception;

	/**
	 * edit wallpaper data
	 * 
	 * @param wallpaperVO WallpaperVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO editWallpaperData(WallpaperVO wallpaperVO) throws Exception;

	/**
	 * response wallpaper list data by group id
	 * 
	 * @param grpId string group id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getWallpaperList() throws Exception;

	/**
	 * response wallpaper information data by wallpaper id
	 * 
	 * @param wallpaperId String wallpaper id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getWallpaperData(String wallpaperId) throws Exception;

}
