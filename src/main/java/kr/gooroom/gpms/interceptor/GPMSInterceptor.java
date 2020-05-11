/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.gooroom.gpms.interceptor;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import kr.gooroom.gpms.common.service.GpmsCommonService;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;

/**
 * Intercepter class for MVC framework.
 * <p>
 * create global object.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class GPMSInterceptor extends HandlerInterceptorAdapter {

	// private static final Logger logger =
	// LoggerFactory.getLogger(GPMSInterceptor.class);

	@Resource(name = "gpmsCommonService")
	private GpmsCommonService gpmsCommonService;

	/**
	 * pre handle method.
	 * 
	 * @param req     HttpServletRequest
	 * @param res     HttpServletResponse
	 * @param handler Object
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {

		String actItem = req.getRequestURI();

		if (actItem.indexOf("/") > -1) {
			actItem = actItem.substring(actItem.lastIndexOf("/"));
		}

		String actType = "ETC";
		if (actItem.startsWith("/create")) {
			actType = "I";
		} else if (actItem.startsWith("/delete")) {
			actType = "D";
		} else if (actItem.startsWith("/is")) {
			actType = "B";
		} else if (actItem.startsWith("/page")) {
			actType = "M";
		} else if (actItem.startsWith("/read")) {
			actType = "R";
		} else if (actItem.startsWith("/update")) {
			actType = "U";
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> paramMap = (Map<String, Object>) req.getParameterMap();
		String actData = "";
		if (paramMap.size() > 0) {
			StringBuffer sb = new StringBuffer();
			Set<String> keys = paramMap.keySet();
			for (String key : keys) {
				sb.append("{[").append(key).append("][").append(req.getParameter(key)).append("]}\n");
			}
			actData = sb.toString();
		}

		if (LoginInfoHelper.isAuthenticated()) {
			gpmsCommonService.createUserActLogHistory(actType, actItem, actData, req.getRemoteAddr(),
					LoginInfoHelper.getUserId());
		}

		return true;
	}

	/**
	 * post handle method.
	 * 
	 * @param req     HttpServletRequest
	 * @param res     HttpServletResponse
	 * @param handler Object
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView modelAndView)
			throws Exception {

		// for version
		if (modelAndView != null) {

			// get locale for message
			modelAndView.addObject("gpmsLanguage", req.getLocale().getLanguage());

			modelAndView.addObject("gpmsVersion", String.valueOf(Calendar.getInstance().getTimeInMillis()));
			// modelAndView.addObject("gpmsVersion", "1");
			if (req != null && req.getParameter("b") != null) {
				String[] menus = req.getParameter("b").split(":");
				modelAndView.addObject("GRMENUS", menus);
			}
		}
	}
}
