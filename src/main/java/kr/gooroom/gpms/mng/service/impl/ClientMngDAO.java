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

package kr.gooroom.gpms.mng.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.client.service.ClientPackageVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.mng.service.ClientProfileSetVO;
import kr.gooroom.gpms.mng.service.ClientRegKeyVO;
import kr.gooroom.gpms.mng.service.ClientSoftwareVO;

/**
 * data access object class for client registration key management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("clientMngDAO")
public class ClientMngDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(ClientMngDAO.class);

	/**
	 * get client registration key information list data
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ClientRegKeyVO List selected list data
	 * @throws SQLException
	 */
	public List<ClientRegKeyVO> selectRegKeyList(HashMap<String, Object> options) throws SQLException {

		List<ClientRegKeyVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectRegKeyList", options);
		} catch (Exception ex) {
			logger.error("error in selectRegKeyList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response total count for client registration key information list by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectRegKeyListTotalCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectRegKeyListTotalCount", options);
	}

	/**
	 * response filtered total count for client registration key information list by
	 * options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectRegKeyListFilteredCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectRegKeyListFilteredCount", options);
	}

	/**
	 * insert new client registration key information data.
	 * 
	 * @param vo ClientRegKeyVO client registration key information data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertRegKeyData(ClientRegKeyVO vo) throws SQLException {
		return (long) sqlSessionMeta.insert("insertRegKeyData", vo);
	}

	/**
	 * update client registration key information data.
	 * 
	 * @param vo ClientRegKeyVO client registration key information data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long updateRegKeyData(ClientRegKeyVO vo) throws SQLException {
		return (long) sqlSessionMeta.update("updateRegKeyData", vo);
	}

	/**
	 * delete client registration key information data.
	 * 
	 * @param regKeyNo String client registation key id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteRegKeyData(String regKeyNo) throws SQLException {
		return (long) sqlSessionMeta.insert("deleteRegKeyData", regKeyNo);
	}

	/**
	 * insert new profile set data.
	 * 
	 * @param vo ClientProfileVO client profile data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertProfileSet(ClientProfileSetVO vo) throws SQLException {

		return (long) sqlSessionMeta.insert("insertProfileSet", vo);
	}

	/**
	 * get Profile Set information list data with paging.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ClientRegKeyVO List selected list data
	 * @throws SQLException
	 */
	public List<ClientProfileSetVO> selectProfileSetListPaged(HashMap<String, Object> options) throws SQLException {

		List<ClientProfileSetVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectProfileSetListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectProfileSetListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response filtered total count for Profile Set information list by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectProfileSetListFilteredCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectProfileSetListFilteredCount", options);
	}

	/**
	 * response total count for Profile Set information list by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectProfileSetListTotalCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectProfileSetListTotalCount", options);
	}

	/**
	 * delete client profile set master
	 * 
	 * @param profileNo String profile set no.
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteProfileSetMaster(String profileNo) throws SQLException {
		return (long) sqlSessionMeta.insert("deleteProfileSetMaster", profileNo);
	}

	/**
	 * delete client profile set data.
	 * 
	 * @param profileNo String profile set no.
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteProfileSetData(String profileNo) throws SQLException {
		return (long) sqlSessionMeta.insert("deleteProfileSetData", profileNo);
	}

	/**
	 * update profile set data.
	 * 
	 * @param vo ClientRegKeyVO client registration key information data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long updateProfileSetData(ClientProfileSetVO vo) throws SQLException {

		return (long) sqlSessionMeta.update("updateProfileSetData", vo);
	}

	/**
	 * delete client software information data.
	 * 
	 * @param regKeyNo String client software id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteClientSoftwareData(String swId) throws SQLException {
		return (long) sqlSessionMeta.insert("deleteClientSoftwareData", swId);
	}

	/**
	 * insert new client software information data.
	 * 
	 * @param vo ClientRegKeyVO client registration key information data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertClientSoftwareData(ClientSoftwareVO vo) throws SQLException {
		return (long) sqlSessionMeta.insert("insertClientSoftwareData", vo);
	}

	/**
	 * get client software information list data
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ClientRegKeyVO List selected list data
	 * @throws SQLException
	 */
	public List<ClientSoftwareVO> selectClientSoftwareList(HashMap<String, Object> options) throws SQLException {

		List<ClientSoftwareVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectClientSoftwareList", options);
		} catch (Exception ex) {
			logger.error("error in selectClientSoftwareList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response filtered total count for client software information list by
	 * options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectClientSoftwareListFilteredCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectClientSoftwareListFilteredCount", options);
	}

	/**
	 * response total count for client software information list by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectClientSoftwareListTotalCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectClientSoftwareListTotalCount", options);
	}

	/**
	 * update client software information data.
	 * 
	 * @param vo ClientSoftwareVO client software information data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long updateClientSoftwareData(ClientSoftwareVO vo) throws SQLException {
		return (long) sqlSessionMeta.update("updateClientSoftwareData", vo);
	}

	/**
	 * get package list in profile set.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ClientRegKeyVO List selected list data
	 * @throws SQLException
	 */
	public List<ClientPackageVO> selectProfilePackageListPaged(HashMap<String, Object> options) throws SQLException {

		List<ClientPackageVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectProfilePackageListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectProfileSetListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response filtered total count for package list in profile set.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectProfilePackageListFilteredCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectProfilePackageListFilteredCount", options);
	}

	/**
	 * response total count for package list in profile set.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectProfilePackageListTotalCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectProfilePackageListTotalCount", options);
	}

}
