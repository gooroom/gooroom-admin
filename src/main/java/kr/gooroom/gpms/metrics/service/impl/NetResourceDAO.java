package kr.gooroom.gpms.metrics.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.metrics.service.NetResourceVO;
import kr.gooroom.gpms.metrics.service.ResourceMetricsVO;

@Repository("netResourceDAO")
public class NetResourceDAO extends ResourceMetricsDAO {
    private static final Logger logger = LoggerFactory.getLogger(NetResourceDAO.class);

    /**
     * response Cpu Resource Metrics List.
     *
     * @return ResourceMetricsVO List selected client group object
     * @throws SQLException
     */
    @Override
    public List<ResourceMetricsVO> selectResourceList() throws Exception {
        List<ResourceMetricsVO> re = null;

        try {
            re = sqlSessionMeta.selectList("selectNetResourceList");
        } catch (Exception ex) {
            re = null;
            logger.error("error in selectNetResourceList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
        }
        return re;
    }

    @Override
    public Class<? extends ResourceMetricsVO> getVOClass() {
        return NetResourceVO.class;
    }
}
