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

package kr.gooroom.gpms.rule.util;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.CtrlMstService;

/**
 * Handles requests for the client configuration management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */
@Component
public class RuleSelector {

	private static final Logger logger = LoggerFactory.getLogger(RuleSelector.class);

	@Resource(name = "ctrlMstService")
	private CtrlMstService ctrlMstService;
	
	

	public ResultVO getClientConfByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_CLIENT_SETUP_CONF_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				// select default rule
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_CLIENT_SETUP_CONF_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}
			//
		} catch (Exception ex) {
			logger.error("error in getClientConfByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}
		return resultVO;
	}
	

	public ResultVO getHostInfoByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_HOSTS_SETUP_CONF_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				// select default rule
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_HOSTS_SETUP_CONF_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}
			//
		} catch (Exception ex) {
			logger.error("error in getHostInfoByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}
		return resultVO;
	}

	public ResultVO getUpdateServerInfoByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_UPDATE_SERVER_CONF_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				// select default rule
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_UPDATE_SERVER_CONF_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}
			//
		} catch (Exception ex) {
			logger.error("error in getUpdateServerInfoByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}
		return resultVO;
	}

	public ResultVO getBrowserRuleByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				// select default rule
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}
			//
		} catch (Exception ex) {
			logger.error("error in getBrowserRuleByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}
		return resultVO;
	}
	
	
	public ResultVO getMediaRuleByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				// default media rule.
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}
		} catch (Exception ex) {
			logger.error("error in getMediaRuleByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}
	
	
	public ResultVO getSecurityRuleByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_ITEM_GRSECU_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				// default media rule.
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_ITEM_GRSECU_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}
		} catch (Exception ex) {
			logger.error("error in getSecurityRuleByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}
	
	
	public ResultVO getSoftwareFilterByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_ITEM_SWFILTER_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_ITEM_SWFILTER_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}

		} catch (Exception ex) {
			logger.error("error in getSoftwareFilterByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	public ResultVO getCtrlCenterItemByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}

		} catch (Exception ex) {
			logger.error("error in getCtrlCenterItemByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	public ResultVO getPolicyKitByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_ITEM_POLICYKIT_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_ITEM_POLICYKIT_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}

		} catch (Exception ex) {
			logger.error("error in getPolicyKitByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

}
