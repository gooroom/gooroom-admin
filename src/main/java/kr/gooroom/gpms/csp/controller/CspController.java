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

package kr.gooroom.gpms.csp.controller;

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
import kr.gooroom.gpms.csp.service.CspService;
import kr.gooroom.gpms.csp.service.CspVO;

/**
 * Handles requests for the csp configuration management process.
 * <p>
 * CSP: cloud service provider.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class CspController {

	private static final Logger logger = LoggerFactory.getLogger(CspController.class);

	@Resource(name = "cspService")
	private CspService cspService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	/**
	 * create new csp configuration data.
	 * 
	 * @param cspVO CspVO configuration data bean
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createGcspData")
	public @ResponseBody ResultVO createGcspData(@ModelAttribute("paramVO") CspVO cspVO) {

		ResultVO resultVO = new ResultVO();
		try {
			// check duplicate csp id.
			StatusVO dupStatus = cspService.isExistGcspId(cspVO.getGcspId());
			if (GPMSConstants.MSG_SUCCESS.equalsIgnoreCase(dupStatus.getResult())) {

				StatusVO status = new StatusVO();
				// create certificate
				ResultVO certResultVO = new ResultVO();
				if ("cert1".equalsIgnoreCase(cspVO.getCertGubun())) {
					// create certificate with gcsp id use cn value.
					certResultVO = cspService.createGCSPCertificate(cspVO);
				} else if ("cert2".equalsIgnoreCase(cspVO.getCertGubun())) {
					// create certificate from csr data by sign server.
					certResultVO = cspService.createGCSPCertificateFromCSR(cspVO);
				}

				// check certResultVO status - ROLLBACK
				if (GPMSConstants.MSG_SUCCESS.equalsIgnoreCase(certResultVO.getStatus().getResult())) {

					if (certResultVO.getData() != null && certResultVO.getData().length > 0) {
						CspVO resultCspVo = (CspVO) certResultVO.getData()[0];
						cspVO.setCert(resultCspVo.getCert());
						cspVO.setPriv(resultCspVo.getPriv());
						cspVO.setExpirationYmd(resultCspVo.getExpirationYmd());
						cspVO.setSerialNo(resultCspVo.getSerialNo());

						status = cspService.createGcspData(cspVO);
						resultVO.setStatus(status);
					} else {
						// set Fail in service implement.
						resultVO.setStatus(certResultVO.getStatus());
					}
				} else {
					// set Fail in service implement.
					resultVO.setStatus(certResultVO.getStatus());
				}
			} else {
				resultVO.setStatus(dupStatus);
			}
		} catch (Exception ex) {
			logger.error("error in createGcspData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * edit gooroom csp configuration data.
	 * <p>
	 * csp : cloud service provider.
	 * 
	 * @param cspVO CspVO gooroom csp configuration data bean.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateGcspData")
	public @ResponseBody ResultVO updateGcspData(@ModelAttribute("paramVO") CspVO cspVO) {

		ResultVO resultVO = new ResultVO();

		try {

			StatusVO status = cspService.editGcspData(cspVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in updateGcspData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete gooroom csp configuration data.
	 * 
	 * @param cspVO CspVO gooroom csp configuration data bean.
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/deleteGcspData")
	public @ResponseBody ResultVO deleteGcspData(@ModelAttribute("paramVO") CspVO cspVO) {

		ResultVO resultVO = new ResultVO();
		try {

			StatusVO status = cspService.deleteGcspData(cspVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {

			logger.error("error in deleteGcspData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get gooroom csp configuration list data by status and search word.
	 * <p>
	 * csp : cloud service provider.
	 * 
	 * @param gcspStatus string status value.
	 * @param searchKey  string search keyword.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readGcspList")
	public @ResponseBody ResultVO readGcspList(@RequestParam(value = "gcsp_status", required = false) String gcspStatus,
			@RequestParam(value = "search_key", required = false) String searchKey) {
		ResultVO resultVO = new ResultVO();
		if (gcspStatus == null) {
			gcspStatus = "";
		}
		if (searchKey == null) {
			searchKey = "";
		}
		try {
			resultVO = cspService.getGcspDataList(gcspStatus, searchKey);
		} catch (Exception ex) {
			logger.error("error in readGcspList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}
		return resultVO;
	}

	/**
	 * get gooroom csp configuration list paged data by status and search word.
	 * <p>
	 * csp : cloud service provider.
	 * 
	 * @param gcspStatus string status value.
	 * @param searchKey  string search keyword.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readGcspListPaged")
	public @ResponseBody ResultPagingVO readGcspListPaged(HttpServletRequest req, HttpServletResponse res,
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
		if ("chGcspNm".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "GM.GCSP_NM");
		} else if ("chGcspId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "GM.GCSP_ID");
		} else if ("chStatus".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "GM.STATUS_CD");
		} else if ("chRegUser".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "GM.REG_USER_ID");
		} else if ("chRegDt".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "GM.REG_DT");
		} else {
			options.put("paramOrderColumn", "GM.GCSP_ID");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}

		try {
			resultVO = cspService.getGcspListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);
		} catch (Exception ex) {
			logger.error("error in readGcspListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}
		return resultVO;
	}

	/**
	 * get gooroom csp configuration data by csp id.
	 * <p>
	 * csp : cloud service provider.
	 * 
	 * @param gcspId string gooroom csp id.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readGcspData")
	public @ResponseBody ResultVO readGcspData(@RequestParam(value = "gcsp_id", required = true) String gcspId) {

		ResultVO resultVO = new ResultVO();
		try {
			resultVO = cspService.getGcspData(gcspId);
		} catch (Exception ex) {
			logger.error("error in readGcspData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * create gooroom csp certificate data.
	 * 
	 * @param cspVO CspVO configuration data bean
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createGcspCert")
	public @ResponseBody ResultVO createGcspCert(@ModelAttribute("paramVO") CspVO cspVO) {

		ResultVO resultVO = new ResultVO();

		try {

			if ("cert1".equalsIgnoreCase(cspVO.getCertGubun())) {
				// create certificate with gcsp id use cn value.
				resultVO = cspService.createGCSPCertificate(cspVO);
			} else if ("cert2".equalsIgnoreCase(cspVO.getCertGubun())) {
				// create certificate from csr data by sign server.
				resultVO = cspService.createGCSPCertificateFromCSR(cspVO);
			}

		} catch (Exception ex) {
			logger.error("error in createGcspCert : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}
}
