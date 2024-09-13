package kr.gooroom.gpms.metrics.service;

import kr.gooroom.gpms.common.service.ResultVO;

public interface ResourceMetricsService {
    ResultVO readResourceMetrics(String resourceType) throws Exception;
}