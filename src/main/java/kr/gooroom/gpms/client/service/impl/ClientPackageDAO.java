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

package kr.gooroom.gpms.client.service.impl;

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

/**
 * data access object class for client package management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("clientPackageDAO")
public class ClientPackageDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(ClientPackageDAO.class);

	/**
	 * select total package list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ClientPackageVO List selected package object
	 * @throws SQLException
	 */
	public List<ClientPackageVO> selectTotalPackageListPaged(HashMap<String, Object> options) throws SQLException {

		List<ClientPackageVO> re = null;

		try {
			re = sqlSessionMeta.selectList("selectTotalPackageListPaged", options);
		} catch (Exception ex) {
			re = null;
			logger.error("error in selectTotalPackageListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * select total package count for package list by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectTotalPackageListTotalCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectTotalPackageListTotalCount", options);
	}

	/**
	 * select filtered package count for package list by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectTotalPackageListFilteredCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectTotalPackageListFilteredCount", options);
	}

	/**
	 * select package list by client
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ClientPackageVO List selected package object
	 * @throws SQLException
	 */
	public List<ClientPackageVO> selectClientPackageListPaged(HashMap<String, Object> options) throws SQLException {

		List<ClientPackageVO> re = null;

		try {
			re = sqlSessionMeta.selectList("selectClientPackageListPaged", options);
		} catch (Exception ex) {
			re = null;
			logger.error("error in selectClientPackageListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * select package count by options
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectClientPackageListTotalCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectClientPackageListTotalCount", options);
	}

	/**
	 * select filtered package count by options
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectClientPackageListFilteredCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectClientPackageListFilteredCount", options);
	}

	/**
	 * select total package list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ClientPackageVO List selected package object
	 * @throws SQLException
	 */
	public List<ClientPackageVO> selectTotalPackageList(HashMap<String, Object> options) throws SQLException {

		List<ClientPackageVO> re = null;

		try {
			re = sqlSessionMeta.selectList("selectTotalPackageList", options);
		} catch (Exception ex) {
			re = null;
			logger.error("error in selectTotalPackageList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * select total package count for package list by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectTotalPackageCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectTotalPackageCount", options);
	}

	/**
	 * select filtered package count for package list by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectTotalPackageFiltered(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectTotalPackageFiltered", options);
	}

}
