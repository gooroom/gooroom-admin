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

package kr.gooroom.gpms.mng.controller;

import java.text.SimpleDateFormat;
import java.util.*;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import kr.gooroom.gpms.job.custom.CustomJobMaker;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.FileUploadService;
import kr.gooroom.gpms.common.service.FileVO;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.CommonUtils;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.mng.service.ThemeMngService;
import kr.gooroom.gpms.mng.service.ThemeVO;
import kr.gooroom.gpms.mng.service.WallpaperMngService;
import kr.gooroom.gpms.mng.service.WallpaperVO;

/**
 * Handles requests for the theme file management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class ThemeMngController {

	private static final Logger logger = LoggerFactory.getLogger(ThemeMngController.class);

	@Resource(name = "themeMngService")
	private ThemeMngService themeMngService;

	@Resource(name = "wallpaperMngService")
	private WallpaperMngService wallpaperMngService;

	@Resource(name = "fileUploadService")
	private FileUploadService fileUploadService;

	@Inject
	private CustomJobMaker jobMaker;


	/**
	 * initialize binder for date format
	 * <p>
	 * ex) date format : 2017-10-04
	 * 
	 * @param binder WebDataBinder
	 * @return void
	 *
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	/**
	 * create(insert) new theme information data.
	 * <p>
	 * use file uploader.
	 * 
	 * @param themeVO ThemeVO theme information data bean.
	 * @param request HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/createThemeData")
	public @ResponseBody ResultVO createThemeData(@ModelAttribute("paramVO") ThemeVO themeVO,
			HttpServletRequest request) {

		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;

		ResultVO resultVO = new ResultVO();

		try {

			StatusVO status = themeMngService.createThemeData(themeVO, multipartHttpServletRequest);
			resultVO.setStatus(status);

			if(status.getResult().equals(GPMSConstants.MSG_SUCCESS)) {
				// job maker
				HashMap<String, String> map = new HashMap<>();
				map.put("theme_id", themeVO.getThemeId());
				map.put("theme_action", "0");
				jobMaker.createJobForCustomTheme(map);
			}

		} catch (Exception ex) {
			logger.error("error in createThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * delete theme information data.
	 * 
	 * @param themeId String theme id.
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/deleteThemeData")
	public @ResponseBody ResultVO deleteThemeData(@RequestParam(value = "themeId") String themeId) {

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = themeMngService.deleteThemeData(themeId);
			resultVO.setStatus(status);

			if(status.getResult().equals(GPMSConstants.MSG_SUCCESS)) {
				// job maker
				HashMap<String, String> map = new HashMap<>();
				map.put("theme_id", themeId);
				map.put("theme_action", "2");
				jobMaker.createJobForCustomTheme(map);
			}

		} catch (Exception ex) {
			logger.error("error in deleteThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * edit theme information data.
	 * <p>
	 * use file uploader.
	 * 
	 * @param themeVO ThemeVO theme information data bean.
	 * @param request HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/updateThemeData")
	public @ResponseBody ResultVO updateThemeData(@ModelAttribute("paramVO") ThemeVO themeVO,
			HttpServletRequest request, ModelMap model) {

		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
		ResultVO resultVO = new ResultVO();

		try {

			StatusVO status = themeMngService.editThemeData(themeVO, multipartHttpServletRequest);
			resultVO.setStatus(status);

			if(status.getResult().equals(GPMSConstants.MSG_SUCCESS)) {
				// job maker
				HashMap<String, String> map = new HashMap<>();
				map.put("theme_id", themeVO.getThemeId());
				map.put("theme_action", "1");
				jobMaker.createJobForCustomTheme(map);
			}

		} catch (Exception ex) {
			logger.error("error in updateThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response theme information list data.
	 * 
	 * 
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/readThemeList")
	public @ResponseBody ResultVO readThemeList() {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = themeMngService.getThemeList();

		} catch (Exception ex) {
			logger.error("error in readThemeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response theme information list data for paging
	 * 
	 * 
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/readThemeListPaged")
	public @ResponseBody ResultVO readThemeListPaged(HttpServletRequest req) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<>();
		options.put("ICON_ADDRESS", CommonUtils.createIconUrlPath());

		// << options >>
		String searchKey = ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "");
		options.put("searchKey", searchKey);

		// << paging >>
		String paramStart = ObjectUtils.defaultIfNull(req.getParameter("start"), "0");
		String paramLength = ObjectUtils.defaultIfNull(req.getParameter("length"), "10");
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		if ("chThemeId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "TM.THEME_ID");
		} else if ("chThemeNm".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "TM.THEME_NM");
		} else if ("chModDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "TM.MOD_DT");
		} else {
			options.put("paramOrderColumn", "TM.THEME_NM");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}

		try {
			resultVO = themeMngService.getThemeListPaged(options);

			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in getThemeListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response theme information data by theme id.
	 * 
	 * @param themeId String theme id. @return ResultVO result data bean. @throws
	 */
	@PostMapping(value = "/readThemeData")
	public @ResponseBody ResultVO readThemeData(@RequestParam(value = "themeId") String themeId) {

		ResultVO resultVO = new ResultVO();
		
		HashMap<String, Object> options = new HashMap<>();
		options.put("ICON_ADDRESS", CommonUtils.createIconUrlPath());
		options.put("themeId", themeId);
		
		try {
			resultVO = themeMngService.getThemeData(options);
		} catch (Exception ex) {
			logger.error("error in readThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create(insert) new wallpaper information data.
	 * <p>
	 * use file uploader.
	 *
	 * @param wallpaperVO WallpaperVO wallpaper information data bean. @param
	 *                    request HttpServletRequest @return ResultVO result data
	 *                    bean. @throws
	 */
	@PostMapping(value = "/createWallpaperData")
	public @ResponseBody ResultVO createWallpaperData(@ModelAttribute("paramVO") WallpaperVO wallpaperVO,
			HttpServletRequest request) {

		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartHttpServletRequest.getFile("wallpaperFile");

		FileVO vo = null;
		if (multipartFile != null) {
			vo = fileUploadService.store(multipartFile);
		}

		if (vo != null) {
			wallpaperVO.setFileNo(vo.getFileNo());
		}

		ResultVO resultVO = new ResultVO();
		try {

			StatusVO status = wallpaperMngService.createWallpaperData(wallpaperVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createWallpaperData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response wallpaper information list data.
	 *
	 * @return ResultVO result data bean. @throws
	 */
	@PostMapping(value = "/readWallpaperList")
	public @ResponseBody ResultVO readWallpaperList() {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = wallpaperMngService.getWallpaperList();

		} catch (Exception ex) {
			logger.error("error in readWallpaperList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete wallpaper information data.
	 * 
	 * @param wallpaperId String wallpaper id. @return ResultVO result data
	 *                    bean. @throws
	 */
	@PostMapping(value = "/deleteWallpaperData")
	public @ResponseBody ResultVO deleteWallpaperData(
			@RequestParam(value = "wallpaperId") String wallpaperId) {

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = wallpaperMngService.deleteWallpaperData(wallpaperId);

			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in deleteWallpaperData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * edit wallpaper information data.
	 * <p>
	 * use file uploader.
	 * 
	 * @param wallpaperVO WallpaperVO wallpaper information data bean. @param
	 *                    request HttpServletRequest @return ResultVO result data
	 *                    bean. @throws
	 */
	@PostMapping(value = "/updateWallpaperData")
	public @ResponseBody ResultVO updateWallpaperData(@ModelAttribute("paramVO") WallpaperVO wallpaperVO,
			HttpServletRequest request, ModelMap model) {

		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartHttpServletRequest.getFile("wallpaperFile");

		FileVO vo = null;
		if (multipartFile != null) {
			vo = fileUploadService.store(multipartFile);
		}

		if (vo != null) {
			wallpaperVO.setFileNo(vo.getFileNo());
		}

		ResultVO resultVO = new ResultVO();

		try {

			StatusVO status = wallpaperMngService.editWallpaperData(wallpaperVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in updateWallpaperData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

}
