package kr.gooroom.gpms.metrics.service.impl;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.metrics.service.ResourceMetricsVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ResourceMetricsDAO")
public abstract class ResourceMetricsDAO extends SqlSessionMetaDAO {
    public abstract List<? extends ResourceMetricsVO> selectResourceList() throws Exception;
    public abstract Class<? extends ResourceMetricsVO> getVOClass();
}

