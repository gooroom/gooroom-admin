package kr.gooroom.gpms.log.filter;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class CustomHttpServletResponseWrapper extends ContentCachingResponseWrapper {
    private int statusCode;

    public CustomHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void setStatus(int sc) {
        this.statusCode = sc;
        super.setStatus(sc);
    }

    @Override
    public void sendError(int sc) throws IOException {
        this.statusCode = sc;
        super.sendError(sc);
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}