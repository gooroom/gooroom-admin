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

package kr.gooroom.gpms.config.service;

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

/**
 * Client configuration management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface ClientConfService {

	/**
	 * create new gooroom managements server information(address) data.
	 * 
	 * @param vo MgServerConfVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createMgServerConf(MgServerConfVO vo) throws Exception;

	/**
	 * get gooroom managements server information(address) history data.
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getMgServerConfList() throws Exception;

	/**
	 * get current gooroom managements server information(address) data.
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO readCurrentMgServerConf() throws Exception;

	/**
	 * get client configuration information data by group id.
	 * 
	 * @param groupId String group id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	ResultVO getClientConfIdByGroupId(String groupId) throws Exception;

}
