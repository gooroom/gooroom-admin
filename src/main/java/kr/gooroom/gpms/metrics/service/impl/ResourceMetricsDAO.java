package kr.gooroom.gpms.metrics.service.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.metrics.service.ResourceMetricsVO;

@Repository("ResourceMetricsDAO")
public abstract class ResourceMetricsDAO extends SqlSessionMetaDAO {
    public abstract List<? extends ResourceMetricsVO> selectResourceList() throws Exception;
    public abstract Class<? extends ResourceMetricsVO> getVOClass();
}