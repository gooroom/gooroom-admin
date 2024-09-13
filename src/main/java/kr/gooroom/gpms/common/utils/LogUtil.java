package kr.gooroom.gpms.common.utils;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;
import kr.gooroom.gpms.log.filter.CustomHttpServletResponseWrapper;

public class LogUtil {
	private static final String[] additionalCreateUrl = { };
	private static final String[] additionalDeleteUrl = { };
	private static final String[] additionalBooleanUrl = { };
	private static final String[] additionalPageUrl = { };
	private static final String[] additionalReadUrl = { "module/status", "home", "super", "part" };
	private static final String[] additionalUpdateUrl = { "module/control" };
	private static final String[] additionalApprovalUrl = { };

	// write target in order of importance
	private static final String[][] targetList = {
			{ "admin", "Admin" },
			{ "user", "User" },
			{ "notice", "Notice" },
			{ "portable", "Portable" },
			{ "module", "Module" },
			{ "clientgroup", "Client Group" },
			{ "hostname", "Client" },
			{ "mgserver", "Client" },
			{ "updateserver", "Client" },
			{ "browserrule", "Client" },
			{ "mediarule", "Client" },
			{ "securityrule", "Client" },
			{ "softwarefilter", "Client" },
			{ "ctrlcenter", "Client" },
			{ "policykit", "Client" },
			{ "regkey", "Client" },
			{ "profile", "Client" },
			{ "icon", "Client" },
			{ "theme", "Client" },
			{ "client", "Client" },
			{ "desktop", "Desktop" },
			{ "gcsp", "Cloud" },
			{ "dept", "Department" },
			{ "job", "Job" },
			{ "server", "Server" },
			{ "package", "Package" },
			{ "site", "Site" }
	};

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String getURLString(HttpServletRequest req) {
		String actItem = req.getRequestURI();

		if (actItem.startsWith("/")) {
			actItem = actItem.substring(1);
		}
		return actItem.replace("gpms/", "");
	}

	public static String getActType(String url) {
		if (url.startsWith("create") || Arrays.stream(additionalCreateUrl).anyMatch((path -> path.equals(url)))) {
			return "I";
		} else if (url.startsWith("delete")
				|| Arrays.stream(additionalDeleteUrl).anyMatch((path -> path.equals(url)))) {
			return "D";
		} else if (url.startsWith("is") || Arrays.stream(additionalBooleanUrl).anyMatch((path -> path.equals(url)))) {
			return "B";
		} else if (url.startsWith("page") || Arrays.stream(additionalPageUrl).anyMatch((path -> path.equals(url)))) {
			return "M";
		} else if (url.startsWith("read") || Arrays.stream(additionalReadUrl).anyMatch((path -> path.equals(url)))) {
			return "R";
		} else if (url.startsWith("update") || url.startsWith("edit")
				|| Arrays.stream(additionalUpdateUrl).anyMatch((path -> path.equals(url)))) {
			return "U";
		} else if (url.startsWith("approval")
				|| Arrays.stream(additionalApprovalUrl).anyMatch((path -> path.equals(url)))) {
			return "A";
		} else if (url.startsWith("cancel")) {
			return "C";
		} else if (url.startsWith("otp")) {
			return "O";
		} else if (url.startsWith("login") || url.startsWith("logout")) {
			return "L";
		}
		return "ETC";
	}

	public static String getTarget(String url) {
		String lowerCaseUrl = url.toLowerCase();
		return Arrays.stream(targetList).filter(target -> lowerCaseUrl.contains(target[0]))
				.map(target -> target[1]).findFirst().orElse("");
	}

	public static String getActData(ContentCachingRequestWrapper req, CustomHttpServletResponseWrapper res) {
		if (res.getStatusCode() >= 500) {
			return "{\n" + //
					"  \"request\" : null,\n" + //
					"  \"response\" : { \"errorMessage\":\"Unknown error has occurred.\" } \n" + //
					"}";
		}
		try {
			String reqBody = new String(req.getContentAsByteArray(), req.getCharacterEncoding());
			JsonNode reqNode = null;
			if (!reqBody.isBlank()) {
				if (reqBody.startsWith("{") && reqBody.endsWith("}")) {
					reqNode = objectMapper.readTree(reqBody);
				} else {
					Map<String, Object> bodyMap = parseQueryString(reqBody);
					reqNode = objectMapper.valueToTree(bodyMap);
				}
			}
			JsonNode resNode = objectMapper.readTree(res.getContentAsByteArray());

			ObjectNode combinedNode = objectMapper.createObjectNode();
			combinedNode.set("request", reqNode);
			combinedNode.set("response", resNode.has("data") ? resNode.get("data") : resNode);
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(combinedNode);
		} catch (Exception e) {
			return "{\n" + //
					"  \"request\" : null,\n" + //
					"  \"response\" : null\n" + //
					"}";
		}
	}

	public static Map<String, Object> parseQueryString(String queryString) {
		Map<String, Object> queryParams = new HashMap<>();
		String[] pairs = queryString.split("&");
		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			if (keyValue.length == 2) {
				String key = "";
				String value = "";
				try {
					key = URLDecoder.decode(keyValue[0], "UTF-8");
					value = URLDecoder.decode(keyValue[1], "UTF-8");
					if (key.endsWith("Pw")) {
						value = "***********";
					}
					else if(key.endsWith("userPassword")){
						value = "***********";
					}
				} catch (Exception e) {
					key = keyValue[0];
					value = keyValue[1];
					if (key.endsWith("Pw")) {
						value = "***********";
					}
					else if(key.endsWith("userPassword")){
						value = "***********";
					}
				} finally {
					try {
						JsonNode jsonValue = objectMapper.readTree(value);
						queryParams.put(key, jsonValue);
					} catch (Exception e) {
						queryParams.put(key, value);
					}
				}
			}
		}
		return queryParams;
	}
}
