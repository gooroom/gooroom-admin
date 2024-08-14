package kr.gooroom.spring.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

public class CustomCookieFilter extends GenericFilterBean {

	@Override
    public void doFilter(ServletRequest request,  ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse)response;
        resp.setHeader("Set-Cookie", "SameSite=Lax");
        chain.doFilter(request, response);
    }
}
