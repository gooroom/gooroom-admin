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

package kr.gooroom.gpms.site.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.site.service.SiteMngService;
import kr.gooroom.gpms.site.service.SiteMngVO;

/**
 * Handles requests for the site info management api
 * 
 * @author HNC
 */

@Controller
public class SiteMngController {

	private static final Logger logger = LoggerFactory.getLogger(SiteMngController.class);

	@Resource(name = "siteMngService")
	private SiteMngService siteMngService;

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
	 * response site information list data
	 * 
	 * @return ResultVO result data bean
	 */
	@PostMapping(value = "/readSiteMngList")
	public @ResponseBody ResultVO readSiteMngList() {
		ResultVO resultVO = new ResultVO();
		try {
			resultVO = siteMngService.readSiteMngList();
		} catch (Exception ex) {
			logger.error("error in readSiteMngList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response site information list data for paging
	 * 
	 * @return ResultVO result data bean
	 */
	@PostMapping(value = "/readSiteMngListPaged")
	public @ResponseBody ResultPagingVO readSiteMngListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {
		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>
		options.put("searchKey", ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : ""));
		options.put("status", req.getParameter("status"));

		// << paging >>
		String paramStart = StringUtils.defaultString(req.getParameter("start"), "0");
		String paramLength = StringUtils.defaultString(req.getParameter("length"), "10");
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		if ("chSiteId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "SITE_ID");
		} else if ("chAdminNm".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "SITE_NM");
		} else if ("chRegDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "REG_DT");
		} else {
			options.put("paramOrderColumn", "SITE_NM");
		}
		options.put("paramOrderDir", paramOrderDir);

		try {
			resultVO = siteMngService.getSiteMngListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readSiteMngListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * create new site information data
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/createSiteMng")
	public @ResponseBody ResultVO createSiteMng(@ModelAttribute("paramVO") SiteMngVO paramVO) {
		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = siteMngService.createSiteMng(paramVO);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in createSiteMng : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * modify site information data
	 * 
	 * @param paramVO SiteMngVO data bean
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/updateSiteMngData")
	public @ResponseBody ResultVO updateSiteMngData(@ModelAttribute("paramVO") SiteMngVO paramVO) {
		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = siteMngService.updateSiteMngData(paramVO);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in updateSiteMngData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * modify site's status data
	 * 
	 * @param paramVO SiteMngVO data bean
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/updateSiteStatusData")
	public @ResponseBody ResultVO updateSiteStatusData(@ModelAttribute("paramVO") SiteMngVO paramVO) {
		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = siteMngService.updateSiteStatusData(paramVO);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in updateSiteStatusData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * delete site information data
	 * 
	 * @param siteId String site id
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/deleteSiteMngData")
	public @ResponseBody ResultVO deleteSiteMngData(@RequestParam(value = "siteId", required = true) String siteId) {
		ResultVO resultVO = new ResultVO();
		try {
			SiteMngVO vo = new SiteMngVO();
			vo.setSiteId(siteId);
			StatusVO status = siteMngService.deleteSiteMngData(vo);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in deleteSiteMngData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * get site information data by site id
	 * 
	 * @param siteId String site id
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/readSiteMngData")
	public @ResponseBody ResultVO readSiteMngData(@RequestParam(value = "siteId", required = true) String siteId) {
		ResultVO resultVO = new ResultVO();
		try {
			resultVO = siteMngService.selectSiteMngData();
		} catch (Exception ex) {
			logger.error("error in readSiteMngData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}
}
