package kr.gooroom.gpms.log.service.impl;

import java.util.Arrays;
import java.util.Map;

import jakarta.annotation.Resource;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.gooroom.gpms.common.service.GpmsCommonService;
import kr.gooroom.gpms.log.filter.CustomHttpServletResponseWrapper;
import kr.gooroom.gpms.log.service.LogMappingService;

import kr.gooroom.gpms.common.utils.LogUtil;

@Service("logMappingService")
public class LogMappingServiceImpl implements LogMappingService {
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final String[] excludePath = { "", "index.bundle.js", "css", "super", "part", "logout" };
	private static final String[] postFilteredPath = { "login", "otp/login" };

	@Resource(name = "gpmsCommonService")
	private GpmsCommonService gpmsCommonService;

	public void apiHistoryLogger(ContentCachingRequestWrapper req, CustomHttpServletResponseWrapper res) {
		String reqURL = LogUtil.getURLString(req);

		if (Arrays.stream(excludePath).anyMatch(url -> url.equals(reqURL))) {
			return;
		}
		if (Arrays.stream(postFilteredPath).anyMatch(url -> url.equals(reqURL)) && !req.getMethod().equals("POST")) {
			return;
		}

		String actType = LogUtil.getActType(reqURL);
		String actTarget = LogUtil.getTarget(reqURL);
		String connectedIP = req.getRemoteAddr();

		String adminId = getAdminID(req, reqURL);

		boolean isSuccess = getIsSuccess(res, reqURL);
		String actData = LogUtil.getActData(req, res);

		gpmsCommonService.createUserActLogHistory(actType, reqURL, actData, connectedIP, adminId, actTarget, isSuccess);
	}

	private String getAdminID(ContentCachingRequestWrapper req, String reqURL) {
		if (reqURL.equals("login")) {
			try {
				String reqBody = new String(req.getContentAsByteArray(), req.getCharacterEncoding());
				JsonNode reqNode = null;
				if (!reqBody.isBlank()) {
					if (reqBody.startsWith("{") && reqBody.endsWith("}")) {
						reqNode = objectMapper.readTree(reqBody);
					} else {
						Map<String, Object> bodyMap = LogUtil.parseQueryString(reqBody);
						reqNode = objectMapper.valueToTree(bodyMap);
					}
				}
				return reqNode.get("userId").asText("anonymousUser");
			} catch (Exception e) {
				return "anonymousUser";
			}
		}
		try {
			SecurityContext ctx = SecurityContextHolder.getContext();
			return ctx.getAuthentication().getName();
		} catch (Exception e) {
			return "anonymousUser";
		}
	}

	private boolean getIsSuccess(CustomHttpServletResponseWrapper res, String reqURL) {
		if (reqURL.equals("login")) {
			try {
				SecurityContext ctx = SecurityContextHolder.getContext();
				if (ctx.getAuthentication() != null) {
					return true;
				}
				return false;
			} catch (Exception e) {
				return false;
			}
		}
		try {
			JsonNode resNode = objectMapper.readTree(res.getContentAsByteArray());
			return resNode.get("status").get("result").asText("").equalsIgnoreCase("success") ? true : false;

		} catch (Exception e) {
			return res.getStatusCode() < 400 ? true : false;
		}
	}
}
