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

package kr.gooroom.sample.schedule.service.impl;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;

/**
 * data access object class for site information management process.
 * 
 * @author HNC
 */

@Repository("migSampleDAO")
public class MigSampleDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(MigSampleDAO.class);

	public int getLimitValue() throws SQLException {
		return (int) sqlSessionMeta.selectOne("getLimitValue");
	}
	public long updatePeriod(String value) throws SQLException {
		return (long) sqlSessionMeta.update("updatePeriod", value);
	}
	public long updateLimit(String value) throws SQLException {
		return (long) sqlSessionMeta.update("updateLimit", value);
	}

	
	public long deleteDeptTempData() throws SQLException {
		return (long) sqlSessionMeta.delete("deleteDeptTempData");
	}
	
	public long copyViewToDeptTemp() throws SQLException {
		return (long) sqlSessionMeta.update("copyViewToDeptTemp");
	}

	public long getCountForDeptDelete() throws SQLException {
		return (long) sqlSessionMeta.selectOne("getCountForDeptDelete");
	}

	public long getCountForDeptUpdate() throws SQLException {
		return (long) sqlSessionMeta.selectOne("getCountForDeptUpdate");
	}

	public long getCountForDeptInsert() throws SQLException {
		return (long) sqlSessionMeta.selectOne("getCountForDeptInsert");
	}

	public long getCountForDept() throws SQLException {
		return (long) sqlSessionMeta.selectOne("getCountForDept");
	}
	
	public long deleteForMigDeptData() throws SQLException {
		return (long) sqlSessionMeta.delete("deleteForMigDeptData");
	}

	public long updateForMigDeptData() throws SQLException {
		return (long) sqlSessionMeta.update("updateForMigDeptData");
	}

	public long insertForMigDeptData() throws SQLException {
		return (long) sqlSessionMeta.insert("insertForMigDeptData");
	}
	


	public long deleteUserTempData() throws SQLException {
		return (long) sqlSessionMeta.delete("deleteUserTempData");
	}
	
	public long copyViewToUserTemp() throws SQLException {
		return (long) sqlSessionMeta.update("copyViewToUserTemp");
	}

	public long getCountForUserDelete() throws SQLException {
		return (long) sqlSessionMeta.selectOne("getCountForUserDelete");
	}

	public long getCountForUserUpdate() throws SQLException {
		return (long) sqlSessionMeta.selectOne("getCountForUserUpdate");
	}

	public long getCountForUserInsert() throws SQLException {
		return (long) sqlSessionMeta.selectOne("getCountForUserInsert");
	}

	public long getCountForUser() throws SQLException {
		return (long) sqlSessionMeta.selectOne("getCountForUser");
	}
	
	public long deleteForMigUserData() throws SQLException {
		return (long) sqlSessionMeta.delete("deleteForMigUserData");
	}

	public long updateForMigUserData() throws SQLException {
		return (long) sqlSessionMeta.update("updateForMigUserData");
	}

	public long insertForMigUserData() throws SQLException {
		return (long) sqlSessionMeta.insert("insertForMigUserData");
	}

}
