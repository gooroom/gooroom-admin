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

package kr.gooroom.gpms.rule.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.CtrlMstService;
import kr.gooroom.gpms.config.service.RuleIdsVO;
import kr.gooroom.gpms.rule.util.RuleSelector;

/**
 * Handles requests for the client configuration management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class ClientRuleController {

	private static final Logger logger = LoggerFactory.getLogger(ClientRuleController.class);

	@Resource(name = "ctrlMstService")
	private CtrlMstService ctrlMstService;
	
	@Inject
	private RuleSelector ruleSelector;

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
	 * 
	 * @param req HttpServletRequest
	 * @return HashMap json format
	 *
	 */
	@PostMapping(value = "/readClientRuleByGroupId")
	public @ResponseBody HashMap<String, Object> readClientRuleByGroupId(HttpServletRequest req) {
		
		String groupId = req.getParameter("groupId");
		HashMap<String, Object> hm_total = new HashMap<>();
		
		try {
			if (groupId != null && groupId.trim().length() > 0) {

				// client conf
				ResultVO vo_clientconf = null;
				// host info
				ResultVO vo_hostinfo = null;
				// update server info
				ResultVO vo_updateserver = null;
				
				// browser
				ResultVO vo_browser = null;
				// media
				ResultVO vo_media = null;
				// security
				ResultVO vo_security = null;
				// softwareFilter
				ResultVO vo_swfilter = null;
				// control center item
				ResultVO vo_ctrlcenteritem = null;
				// policy kit
				ResultVO vo_policykit = null;
				
				// get rule ids by group id.
				ResultVO ruleIdsVO = ctrlMstService.getRuleIdsByGroupId(groupId);
				
				if (GPMSConstants.MSG_SUCCESS.equals(ruleIdsVO.getStatus().getResult())) {
					RuleIdsVO vo = (RuleIdsVO) ruleIdsVO.getData()[0];

					// Client Conf
					if (vo.getClientConfId() != null && vo.getClientConfId().length() > 0) {
						vo_clientconf = ruleSelector.getClientConfByRoleId(vo.getClientConfId(), GPMSConstants.RULE_GRADE_GROUP);
					} else {
						vo_clientconf = ruleSelector.getClientConfByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}

					// host info
					if (vo.getHostNameConfId() != null && vo.getHostNameConfId().length() > 0) {
						vo_hostinfo = ruleSelector.getClientConfByRoleId(vo.getHostNameConfId(), GPMSConstants.RULE_GRADE_GROUP);
					} else {
						vo_hostinfo = ruleSelector.getClientConfByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}

					// update server info
					if (vo.getUpdateServerConfId() != null && vo.getUpdateServerConfId().length() > 0) {
						vo_updateserver = ruleSelector.getClientConfByRoleId(vo.getUpdateServerConfId(), GPMSConstants.RULE_GRADE_GROUP);
					} else {
						vo_updateserver = ruleSelector.getClientConfByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}

					// Browser Rule
					if (vo.getBrowserRuleId() != null && vo.getBrowserRuleId().length() > 0) {
						vo_browser = ruleSelector.getBrowserRuleByRoleId(vo.getBrowserRuleId(), GPMSConstants.RULE_GRADE_GROUP);
					} else {
						vo_browser = ruleSelector.getBrowserRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}
					
					// Media Rule
					if (vo.getMediaRuleId() != null && vo.getMediaRuleId().length() > 0) {
						vo_media = ruleSelector.getMediaRuleByRoleId(vo.getMediaRuleId(), GPMSConstants.RULE_GRADE_GROUP);
					} else {
						vo_media = ruleSelector.getMediaRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}

					// Security Rule
					if (vo.getSecurityRuleId() != null && vo.getSecurityRuleId().length() > 0) {
						vo_security = ruleSelector.getSecurityRuleByRoleId(vo.getSecurityRuleId(), GPMSConstants.RULE_GRADE_GROUP);
					} else {
						vo_security = ruleSelector.getSecurityRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}

					// Software Filter Rule
					if (vo.getFilteredSwRuleId() != null && vo.getFilteredSwRuleId().length() > 0) {
						vo_swfilter = ruleSelector.getSoftwareFilterByRoleId(vo.getFilteredSwRuleId(), GPMSConstants.RULE_GRADE_GROUP);
					} else {
						vo_swfilter = ruleSelector.getSoftwareFilterByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}

					// Control Center Item Rule
					if (vo.getCtrlCenterItemRuleId() != null && vo.getCtrlCenterItemRuleId().length() > 0) {
						vo_ctrlcenteritem = ruleSelector.getCtrlCenterItemByRoleId(vo.getCtrlCenterItemRuleId(), GPMSConstants.RULE_GRADE_GROUP);
					} else {
						vo_ctrlcenteritem = ruleSelector.getCtrlCenterItemByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}

					// Policy Kit Rule
					if (vo.getPolicyKitRuleId() != null && vo.getPolicyKitRuleId().length() > 0) {
						vo_policykit = ruleSelector.getPolicyKitByRoleId(vo.getPolicyKitRuleId(), GPMSConstants.RULE_GRADE_GROUP);
					} else {
						vo_policykit = ruleSelector.getPolicyKitByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}

				} else {
					// Browser Rule - DEFAULT
					vo_clientconf = ruleSelector.getClientConfByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					// host info - DEFAULT
					vo_hostinfo = ruleSelector.getClientConfByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					// update server info - DEFAULT
					vo_updateserver = ruleSelector.getClientConfByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);

					// Browser Rule - DEFAULT
					vo_browser = ruleSelector.getBrowserRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					// Media Rule - DEFAULT
					vo_media = ruleSelector.getMediaRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					// Security Rule - DEFAULT
					vo_security = ruleSelector.getSecurityRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					// Software Filter Rule - DEFAULT
					vo_swfilter = ruleSelector.getSoftwareFilterByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					// Control Center Item Rule - DEFAULT
					vo_ctrlcenteritem = ruleSelector.getCtrlCenterItemByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					// Policy Kit Rule - DEFAULT
					vo_policykit = ruleSelector.getPolicyKitByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
				}
				
				hm_total.put("CLIENTCONF", vo_clientconf);
				hm_total.put("HOST", vo_hostinfo);
				hm_total.put("UPDATE", vo_updateserver);
				
				hm_total.put("BROWSER", vo_browser);
				hm_total.put("MEDIA", vo_media);
				hm_total.put("SECURITY", vo_security);
				hm_total.put("SWFILTER", vo_swfilter);
				
				hm_total.put("CTRLCENTERITEM", vo_ctrlcenteritem);
				hm_total.put("POLICYKIT", vo_policykit);
			}
		} catch (Exception ex) {
			logger.error("error in readClientRuleByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return hm_total;
	}

	
	/**
	 * 
	 * @param req HttpServletRequest
	 * @return HashMap json format
	 *
	 */
	@PostMapping(value = "/readAllClientRuleByGroupId")
	public @ResponseBody ResultVO readAllClientRuleByGroupId(HttpServletRequest req) {
		String groupId = req.getParameter("groupId");
		ResultVO resultVO = null;
		try {
			resultVO = ctrlMstService.readCtrlItemByGroupId(groupId);
		} catch (Exception ex) {
			logger.error("error in readAllClientRuleByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return resultVO;
	}

	/**
	 * 
	 * @param req HttpServletRequest
	 * @return HashMap json format
	 *
	 */
	@PostMapping(value = "/readAllClientUseRuleByDeptCd")
	public @ResponseBody ResultVO readAllClientUseRuleByDeptCd(HttpServletRequest req) {
		String deptCd = req.getParameter("deptCd");
		ResultVO resultVO = null;
		try {
			resultVO = ctrlMstService.readCtrlItemByDeptCd(deptCd);
		} catch (Exception ex) {
			logger.error("error in readAllClientUseRuleByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return resultVO;
	}

	/**
	 * 
	 * @param req HttpServletRequest
	 * @return HashMap json format
	 *
	 */
	@PostMapping(value = "/readAllClientUseRuleByUserId")
	public @ResponseBody ResultVO readAllClientUseRuleByUserId(HttpServletRequest req) {
		String userId = req.getParameter("userId");
		ResultVO resultVO = null;
		try {
			resultVO = ctrlMstService.readCtrlItemByUserId(userId);
		} catch (Exception ex) {
			logger.error("error in readAllClientUseRuleByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return resultVO;
	}

}
