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
import java.util.Date;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.mng.service.IconGroupVO;
import kr.gooroom.gpms.mng.service.IconMngService;
import kr.gooroom.gpms.mng.service.IconVO;

/**
 * Handles requests for the icon file management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class IconMngController {

	private static final Logger logger = LoggerFactory.getLogger(IconMngController.class);

	@Resource(name = "iconMngService")
	private IconMngService iconMngService;

	@Resource(name = "fileUploadService")
	private FileUploadService fileUploadService;

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
	 * create(insert) new icon information data.
	 * <p>
	 * use file uploader.
	 * 
	 * @param iconVO  IconVO icon information data bean.
	 * @param request HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/createIconData")
	public @ResponseBody ResultVO createIconData(@ModelAttribute("paramVO") IconVO iconVO, HttpServletRequest request) {

		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
		// Iterator<String> iterator = multipartHttpServletRequest.getFileNames();
		MultipartFile multipartFile = multipartHttpServletRequest.getFile("grfile");

		FileVO vo = null;
		if (multipartFile != null) {
			vo = fileUploadService.store(multipartFile);
		}

		if (vo != null) {
			iconVO.setFileNo(vo.getFileNo());
		}

		ResultVO resultVO = new ResultVO();
		try {

			StatusVO status = iconMngService.createIconData(iconVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createIconData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * delete icon information data.
	 * 
	 * @param iconId String icon id.
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/deleteIconData")
	public @ResponseBody ResultVO deleteIconData(@RequestParam(value = "iconId", required = true) String iconId) {

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = iconMngService.deleteIconData(iconId);

			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in deleteIconData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * edit icon information data.
	 * <p>
	 * use file uploader.
	 * 
	 * @param iconVO  IconVO icon information data bean.
	 * @param request HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/updateIconData")
	public @ResponseBody ResultVO updateIconData(@ModelAttribute("paramVO") IconVO iconVO, HttpServletRequest request,
			ModelMap model) {

		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartHttpServletRequest.getFile("grfile");

		FileVO vo = null;
		if (multipartFile != null) {
			vo = fileUploadService.store(multipartFile);
		}

		if (vo != null) {
			iconVO.setFileNo(vo.getFileNo());
		}

		ResultVO resultVO = new ResultVO();

		try {

			StatusVO status = iconMngService.editIconData(iconVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in updateIconData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response icon information list data.
	 * 
	 * @param grpId String icon group id.
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/readIconList")
	public @ResponseBody ResultVO readIconList(@RequestParam(value = "grpId", required = false) String grpId) {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = iconMngService.getIconList(StringUtils.defaultString(grpId));

		} catch (Exception ex) {
			logger.error("error in readIconList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response icon information list data that not include any group.
	 * 
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/readNoGroupIconList")
	public @ResponseBody ResultVO readNoGroupIconList() {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = iconMngService.getNoGroupIconList();
		} catch (Exception ex) {
			logger.error("error in readNoGroupIconList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response icon information data by icon id.
	 * 
	 * @param iconId String icon id.
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/readIconData")
	public @ResponseBody ResultVO readIconData(@RequestParam(value = "iconId", required = true) String iconId) {

		ResultVO resultVO = new ResultVO();
		try {
			resultVO = iconMngService.getIconData(iconId);
		} catch (Exception ex) {
			logger.error("error in readIconData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response icon group information list data.
	 * 
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/readIconGroupList")
	public @ResponseBody ResultVO readIconGroupList() {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = iconMngService.getIconGroupList();
		} catch (Exception ex) {
			logger.error("error in readIconGroupList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response icon group information data by icon group id.
	 * 
	 * @param iconGrpId String icon group id.
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/readIconGroupData")
	public @ResponseBody ResultVO readIconGroupData(
			@RequestParam(value = "iconGrpId", required = true) String iconGrpId) {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = iconMngService.getIconGroupData(iconGrpId);
		} catch (Exception ex) {

			logger.error("error in readIconGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create(insert) icon group data from icon group data bean.
	 * 
	 * @param iconGroupVO IconGroupVO icon group data.
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/createIconGroupData")
	public @ResponseBody ResultVO createIconGroupData(@ModelAttribute("paramVO") IconGroupVO iconGroupVO) {

		ResultVO resultVO = new ResultVO();
		try {

			StatusVO status = iconMngService.createIconGroupData(iconGroupVO);

			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createIconGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * modify icon group data from icon group data bean.
	 * 
	 * @param iconGroupVO IconGroupVO icon group data.
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/updateIconGroupData")
	public @ResponseBody ResultVO updateIconGroupData(@ModelAttribute("paramVO") IconGroupVO iconGroupVO) {

		ResultVO resultVO = new ResultVO();

		try {

			StatusVO status = iconMngService.editIconGroupData(iconGroupVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in updateIconGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete icon group data by icon group id.
	 * 
	 * @param grpId String icon group id.
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/deleteIconGroupData")
	public @ResponseBody ResultVO deleteIconGroupData(@RequestParam(value = "grpId", required = true) String grpId) {

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = iconMngService.deleteIconGroupData(grpId);

			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in deleteIconGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * insert icon data in icon group.
	 * 
	 * @param grpId String icon group id.
	 * @param icons String icon id list string that separated comma.
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/createIconsInGroup")
	public @ResponseBody ResultVO createIconsInGroup(@RequestParam(value = "grpId", required = true) String grpId,
			@RequestParam(value = "icons", required = true) String icons) {

		ResultVO resultVO = new ResultVO();

		try {

			// create array for clients
			String[] iconIds = icons.split(",");
			StatusVO status = iconMngService.createIconsInGroup(grpId, iconIds);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createIconsInGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * remove icon data from icon group.
	 * 
	 * @param grpId  String icon group id.
	 * @param iconId String icon id.
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/deleteIconInGroup")
	public @ResponseBody ResultVO deleteIconInGroup(@RequestParam(value = "grpId", required = true) String grpId,
			@RequestParam(value = "iconId", required = true) String iconId) {

		ResultVO resultVO = new ResultVO();

		try {

			StatusVO status = iconMngService.deleteIconInGroup(grpId, iconId);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in deleteIconInGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

}
