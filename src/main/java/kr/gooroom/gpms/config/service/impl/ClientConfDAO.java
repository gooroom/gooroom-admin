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

package kr.gooroom.gpms.config.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.client.service.ClientGroupVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.MgServerConfVO;
import kr.gooroom.gpms.config.service.SiteConfVO;

/**
 * data access object class for client management process.
 * <p>
 * with client group management.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("clientConfDAO")
public class ClientConfDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(ClientConfDAO.class);

	/**
	 * response boolean value which exist gooroom server configuration data.
	 * 
	 * @return boolean true if exist.
	 * @throws SQLException
	 */
	public boolean isExistMgServerConf() throws SQLException {

		boolean re = true;
		try {
			re = ((Boolean) sqlSessionMeta.selectOne("isExistMgServerConf")).booleanValue();

		} catch (Exception ex) {
			logger.error("error in isExistMgServerConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = true;
		}

		return re;
	}

	/**
	 * response gooroom server configuration modify history data
	 * 
	 * @return MgServerConfVO List include edit history
	 * @throws SQLException
	 */
	public List<MgServerConfVO> getMgServerConfList() throws SQLException {

		List<MgServerConfVO> re = null;
		try {

			re = sqlSessionMeta.selectList("selectMgServerConfList");

		} catch (Exception ex) {
			logger.error("error in getMgServerConfList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * insert history for new gooroom server configuration data create.
	 * 
	 * @param vo MgServerConfVO gooroom server configuration data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createMgServerConfHist(MgServerConfVO vo) throws SQLException {

		return (long) sqlSessionMeta.insert("insertMgServerConfHistory", vo);

	}

	/**
	 * insert new gooroom server configuration data.
	 * 
	 * @param vo MgServerConfVO gooroom server configuration data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createMgServerConf(MgServerConfVO vo) throws SQLException {

		return (long) sqlSessionMeta.insert("insertMgServerConf", vo);

	}

	/**
	 * edit gooroom server configuration data.
	 * 
	 * @param vo MgServerConfVO gooroom server configuration data bean
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long editMgServerConf(MgServerConfVO vo) throws SQLException {

		return (long) sqlSessionMeta.insert("editMgServerConf", vo);

	}

	/**
	 * response gooroom server configuration data
	 * 
	 * @return MgServerConfVO gooroom server configuration data bean
	 * @throws SQLException
	 */
	public MgServerConfVO getMgServerConf() throws SQLException {

		MgServerConfVO re = null;
		try {

			re = sqlSessionMeta.selectOne("selectMgServerConf");

		} catch (Exception ex) {
			logger.error("error in getMgServerConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response polling time for gooroom server
	 * 
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectSitePollingTime(String siteId) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectSitePollingTime", siteId);
	}

	/**
	 * response login trial count
	 * 
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectSiteLoginTrialCount(String siteId) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectSiteLoginTrialCount", siteId);
	}

	/**
	 * response login locked time
	 * 
	 * @return long total minutes.
	 * @throws SQLException
	 */
	public long selectSiteLoginLockTime(String siteId) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectSiteLoginLockTime", siteId);
	}

	/**
	 * response password rule config
	 * 
	 * @return long total count number.
	 * @throws SQLException
	 */
	public String selectSitePasswordRule(String siteId) throws SQLException {
		return (String) sqlSessionMeta.selectOne("selectSitePasswordRule", siteId);
	}

	/**
	 * response whether enable duplicate login
	 * 
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectSiteLoginDuplicateEnable(String siteId) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectSiteLoginDuplicateEnable", siteId);
	}

	/**
	 * response client configuration id by group id
	 * 
	 * @param groupId string group id
	 * @return UserRoleVO user role data bean
	 * @throws SQLException
	 */
	public ClientGroupVO selectClientConfIdByGroupId(String groupId) throws SQLException {

		ClientGroupVO re = null;
		try {
			re = sqlSessionMeta.selectOne("ClientGroupDAO.selectClientConfIdByGroupId", groupId);
		} catch (Exception ex) {
			logger.error("error in selectClientConfIdByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * insert history for new site conf
	 * 
	 * @param vo MgServerConfVO gooroom server configuration data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createSiteConfHist(SiteConfVO vo) throws SQLException {
		return (long) sqlSessionMeta.insert("insertSiteConfHistory", vo);
	}

	/**
	 * update gooroom site conf
	 * 
	 * @param vo MgServerConfVO gooroom server configuration data bean
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateSiteConf(SiteConfVO vo) throws SQLException {

		return (long) sqlSessionMeta.update("updateSiteConf", vo);

	}


	/**
	 * update login trial count in user mstr
	 * 
	 * @param map HashMap
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateLoginTrialInUser(HashMap<String, Object> map) throws SQLException {

		return (long) sqlSessionMeta.update("updateLoginTrialInUser", map);

	}
}
