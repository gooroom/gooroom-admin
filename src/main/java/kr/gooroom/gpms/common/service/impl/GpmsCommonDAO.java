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

package kr.gooroom.gpms.common.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.FileVO;
import kr.gooroom.gpms.common.service.ServerAddrInfoVO;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;

/**
 * data access object class for GPMS common services.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("gpmsCommonDAO")
public class GpmsCommonDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(GpmsCommonDAO.class);

	/**
	 * response client list by options.
	 * <p>
	 * server ip, server url.
	 *
	 * @return ServerAddrInfoVO server address information data bean
	 */
	public ServerAddrInfoVO getGpmsServersInfo() {

		ServerAddrInfoVO re;
		try {
			re = sqlSessionMeta.selectOne("selectServerAddrInfo");
		} catch (Exception ex) {
			re = null;
			logger.error("error in getGpmsServersInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * insert action log by administrator user.
	 * 
	 * @param actType  string action type
	 * @param actItem  string action item
	 * @param actData  string action date like parameter
	 * @param accessIp string network access ip information
	 * @param userId   string user id
	 * @return long query result count
	 */
	public long createUserActLogHistory(String actType, String actItem, String actData, String accessIp, String userId) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("actType", actType);
		map.put("actItem", actItem);
		map.put("actData", actData);
		map.put("accessIp", accessIp);
		map.put("userId", userId);

		return sqlSessionMeta.insert("insertUserActLogHistory", map);
	}

	/**
	 * response servers network information.
	 * <p>
	 * GPMS, GLM, GKM, GRM.
	 * 
	 * @return String list of query result
	 */
	public List<String> selectGpmsAvailableNetwork() {
		List<String> re = null;
		try {
			re = sqlSessionMeta.selectList("selectGpmsAvailableNetwork");
		} catch (Exception ex) {
			logger.error("error in selectGpmsAvailableNetwork : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

	/**
	 * response available ip for GPMS by admin id.
	 * 
	 * @return String list of query result
	 */
	public List<String> selectGpmsAvailableNetworkByAdminId(String userId) {
		List<String> re = null;
		try {
			re = sqlSessionMeta.selectList("selectGpmsAvailableNetworkByAdminId", userId);
		} catch (Exception ex) {
			logger.error("error in selectGpmsAvailableNetworkByAdminId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

	/**
	 * insert file information data.
	 * 
	 * @param vo FileVO File information object
	 * @return long query result count
	 * @throws SQLException
	 */
	public long insertFileInfo(FileVO vo) throws SQLException {

		return sqlSessionMeta.insert("insertFileInfo", vo);

	}

	/**
	 * select file information data.
	 *
	 * @param fileNo String File No information object
	 * @return long query result count
	 * @throws SQLException
	 */
	public FileVO selectFileInfo(String fileNo) throws SQLException {

		return sqlSessionMeta.selectOne("selectFileInfo", fileNo);

	}

}
