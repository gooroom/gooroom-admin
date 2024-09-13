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

package kr.gooroom.gpms.metrics.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.metrics.service.MemResourceVO;
import kr.gooroom.gpms.metrics.service.ResourceMetricsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository("memResourceDAO")
public class MemResourceDAO extends ResourceMetricsDAO {

    private static final Logger logger = LoggerFactory.getLogger(MemResourceDAO.class);

    /**
     * response Mem Resource Metric List.
     *
     * @return MemResource List selected client group object
     * @throws SQLException
     */
    public List<ResourceMetricsVO> selectResourceList() throws SQLException {
        List<ResourceMetricsVO> re = null;

        try {
            re = sqlSessionMeta.selectList("selectMemResourceList");
        } catch (Exception ex) {
            re = null;
            logger.error("error in selectMemResourceList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
        }
        return re;
    }

    @Override
    public Class<? extends ResourceMetricsVO> getVOClass() {
        return MemResourceVO.class;
    }
}
