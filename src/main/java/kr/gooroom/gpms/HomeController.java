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

package kr.gooroom.gpms;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles requests for the application home page.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */
@Controller
public class HomeController {

	/**
	 * Simply selects the home view to render by returning its name.
	 * 
	 * @author HNC
	 * @version 1.0
	 * @param model Spring framework UI Model Object
	 * @return String
	 */
	@GetMapping(value = "/")
	public String home(Model model) {
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
				return "main/home";
			} else if (grantedAuthority.getAuthority().equals("ROLE_SUPER")) {
				return "main/super";
			} else if (grantedAuthority.getAuthority().equals("ROLE_PART")) {
				for (GrantedAuthority subAuthority : authorities) {
					model.addAttribute(subAuthority.getAuthority(), "1");
				}
				return "main/part";
			} else if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
				for (GrantedAuthority subAuthority : authorities) {
					model.addAttribute(subAuthority.getAuthority(), "1");
				}
				return "main/user";
			}
		}
		return "redirect:/login";
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 * 
	 * @author HNC
	 * @version 1.0
	 * @param model Spring framework UI Model Object
	 * @return String
	 */
	@GetMapping(value = "/super")
	public String superPage(Model model) {

		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			model.addAttribute(grantedAuthority.getAuthority(), "1");
		}

		return "main/super";
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 * 
	 * @author HNC
	 * @version 1.0
	 * @param model Spring framework UI Model Object
	 * @return String
	 */
	@GetMapping(value = "/home")
	public String homePage(Model model) {

		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			model.addAttribute(grantedAuthority.getAuthority(), "1");
		}

		return "main/home";
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 * 
	 * @author HNC
	 * @version 1.0
	 * @param model Spring framework UI Model Object
	 * @return String
	 */
	@GetMapping(value = "/part")
	public String partPage(Model model) {
		
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			model.addAttribute(grantedAuthority.getAuthority(), "1");
		}
		
		return "main/part";
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 *
	 * @author HNC
	 * @version 1.0
	 * @param model Spring framework UI Model Object
	 * @return String
	 */
	@GetMapping(value = "/user")
	public String userPage(Model model) {

		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			model.addAttribute(grantedAuthority.getAuthority(), "1");
		}

		return "main/user";
	}

	/**
	 * error view to render by returning its name with error code.
	 * 
	 * @author HNC
	 * @version 1.0
	 * @param model Spring framework UI Model Object
	 * @param code  String for error code
	 * @return String
	 */
	@PostMapping(value = "/error")
	public String errorPost(Model model, @RequestParam(value = "code", required = false) String code) {
		if (code == null) {
			model.addAttribute("errorCode", "Error");
		} else {
			model.addAttribute("errorCode", code);
		}
		return "error/error";
	}

	/**
	 * error view to render by returning its name with error code.
	 * 
	 * @author HNC
	 * @version 1.0
	 * @param model Spring framework UI Model Object
	 * @param code  String for error code
	 * @return String
	 */
	@GetMapping(value = "/error")
	public String errorGet(Model model, @RequestParam(value = "code", required = false) String code) {
		if (code == null) {
			model.addAttribute("errorCode", "Error");
		} else {
			model.addAttribute("errorCode", code);
		}
		return "error/error";
	}

}
