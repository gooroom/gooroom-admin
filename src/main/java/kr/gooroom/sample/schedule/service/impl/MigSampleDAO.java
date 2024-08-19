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

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * data access object class for site information management process.
 * 
 * @author HNC
 */

@Repository("migSampleDAO")
public class MigSampleDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(MigSampleDAO.class);

	public int getLimitValue() {
		return sqlSessionMeta.selectOne("getLimitValue");
	}
	public long updatePeriod(String value) {
		return sqlSessionMeta.update("updatePeriod", value);
	}
	public long updateLimit(String value) {
		return sqlSessionMeta.update("updateLimit", value);
	}

	
	public long deleteDeptTempData() {
		return sqlSessionMeta.delete("deleteDeptTempData");
	}
	
	public long copyViewToDeptTemp() {
		return sqlSessionMeta.update("copyViewToDeptTemp");
	}

	public long getCountForDeptDelete() {
		return sqlSessionMeta.selectOne("getCountForDeptDelete");
	}

	public long getCountForDeptUpdate() {
		return sqlSessionMeta.selectOne("getCountForDeptUpdate");
	}

	public long getCountForDeptInsert() {
		return sqlSessionMeta.selectOne("getCountForDeptInsert");
	}

	public long getCountForDept() {
		return sqlSessionMeta.selectOne("getCountForDept");
	}
	
	public long deleteForMigDeptData() {
		return sqlSessionMeta.delete("deleteForMigDeptData");
	}

	public long updateForMigDeptData() {
		return sqlSessionMeta.update("updateForMigDeptData");
	}

	public long insertForMigDeptData() {
		return sqlSessionMeta.insert("insertForMigDeptData");
	}
	


	public long deleteUserTempData() {
		return sqlSessionMeta.delete("deleteUserTempData");
	}
	
	public long copyViewToUserTemp() {
		return sqlSessionMeta.update("copyViewToUserTemp");
	}

	public long getCountForUserDelete() {
		return sqlSessionMeta.selectOne("getCountForUserDelete");
	}

	public long getCountForUserUpdate() {
		return sqlSessionMeta.selectOne("getCountForUserUpdate");
	}

	public long getCountForUserInsert() {
		return sqlSessionMeta.selectOne("getCountForUserInsert");
	}

	public long getCountForUser() {
		return sqlSessionMeta.selectOne("getCountForUser");
	}
	
	public long deleteForMigUserData() {
		return sqlSessionMeta.delete("deleteForMigUserData");
	}

	public long updateForMigUserData() {
		return sqlSessionMeta.update("updateForMigUserData");
	}

	public long insertForMigUserData() {
		return sqlSessionMeta.insert("insertForMigUserData");
	}

}
