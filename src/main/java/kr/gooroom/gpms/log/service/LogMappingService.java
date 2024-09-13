package kr.gooroom.gpms.log.service;


import org.springframework.web.util.ContentCachingRequestWrapper;
import kr.gooroom.gpms.log.filter.CustomHttpServletResponseWrapper;

public interface LogMappingService {
    public void apiHistoryLogger(ContentCachingRequestWrapper req, CustomHttpServletResponseWrapper res);
}
