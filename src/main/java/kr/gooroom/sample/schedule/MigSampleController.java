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

package kr.gooroom.sample.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.sample.schedule.service.MigSampleService;

/**
 * 
 * 
 * @author HNC
 */

@Controller
@RequestMapping("/samples")
public class MigSampleController {

	private static final Logger logger = LoggerFactory.getLogger(MigSampleController.class);

	@Resource(name = "migSampleService")
	private MigSampleService migSampleService;

	/**
	 * initialize binder for date format
	 * <p>
	 * ex) date format : 2017-10-04
	 * 
	 * @param binder WebDataBinder
	 * @return void
	 * 
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@GetMapping(value = "/data_sync")
	public ModelAndView syncPage() {
		ModelAndView mv = new ModelAndView("syncPage");
		return mv;
	}

	/**
	 * 
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 * 
	 */
	@GetMapping(value = "/createMigSampleProcess")
	public @ResponseBody String createMigSampleProcess(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String isForce = req.getParameter("isforce");
		
		try {
			StatusVO status = migSampleService.createMigProcess(isForce);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in createMigSampleProcess : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO.getStatus().getMessage();
	}
	
	@GetMapping(value = "/saveValue")
	public @ResponseBody String saveValue(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();
		
		String inType = req.getParameter("intype");
		String inValue = req.getParameter("invalue");
		
		try {
			StatusVO status = migSampleService.changeValue(inType, inValue);
			
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in saveValue : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO.getStatus().getMessage();
	}


}
