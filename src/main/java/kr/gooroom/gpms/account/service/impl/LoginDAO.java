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

package kr.gooroom.gpms.account.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.account.service.AccountVO;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;

/**
 * data access object class for login process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("loginDAO")
public class LoginDAO extends SqlSessionMetaDAO {

	/**
	 * response login information for user
	 * <p>
	 * - username, status..
	 * 
	 * @param userId string for user id
	 * @return AccountVO
	 *
	 */
	public AccountVO selectLoginInfo(String userId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);

		return (AccountVO) sqlSessionMeta.selectOne("adminDAO.selectAdminLoginInfo", param);
	}

}
