package kr.gooroom.gpms.log.filter;

import java.io.IOException;

import jakarta.annotation.Resource;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import kr.gooroom.gpms.log.service.LogMappingService;

public class GPMSFilter extends OncePerRequestFilter {

    @Resource(name = "logMappingService")
    private LogMappingService logMappingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException,
            ServletException {

        ContentCachingRequestWrapper httpsServletRequest = new ContentCachingRequestWrapper(request);
        CustomHttpServletResponseWrapper httpServletResponse = new CustomHttpServletResponseWrapper(response);
        
        chain.doFilter(httpsServletRequest, httpServletResponse);
        logMappingService.apiHistoryLogger(httpsServletRequest, httpServletResponse);
        httpServletResponse.copyBodyToResponse();
    }
}
