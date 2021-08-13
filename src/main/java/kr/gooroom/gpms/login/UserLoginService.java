package kr.gooroom.gpms.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.gooroom.gpms.user.service.AdminUserVO;
import kr.gooroom.gpms.user.service.UserService;
import kr.gooroom.gpms.user.service.impl.AdminUserDAO;

@Service
@Transactional
public class UserLoginService implements UserDetailsService {

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "adminUserDAO")
	private AdminUserDAO adminUserDao;

	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {

		AdminUserVO vo = new AdminUserVO();
		try {
			HashMap<String, Object> options = new HashMap<String, Object>();
			options.put("adminId", username);			
			options.put("isAuth", "yes");

			vo = adminUserDao.selectAdminUserData(options);
			
//			if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
//				vo = (UserVO) resultVO.getData()[0];
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		vo.setLoginId("admin");
//		vo.setUserPasswd("c7c6b155bd5dc4a8ac54b1a7a54a81c11abcf3ee1760c3b35dfddb7e1a9d1b65");
		
		List<Role> roles = new ArrayList<Role>();
		if(vo != null) {
			switch (vo.getAdminTp()) {
			case "S":
				roles.add(new Role("ROLE_SUPER"));
				break;
			case "A":
				roles.add(new Role("ROLE_ADMIN"));
				break;
			case "P":
				roles.add(new Role("ROLE_PART"));
				
				// SET ROLE - sub rules
				if("1".equals(vo.getIsClientAdmin())) { roles.add(new Role("ROLE_CLIENT_ADMIN")); }
				if("1".equals(vo.getIsUserAdmin())) { roles.add(new Role("ROLE_USER_ADMIN")); }
				if("1".equals(vo.getIsNoticeAdmin())) { roles.add(new Role("ROLE_NOTICE_ADMIN")); }
				if("1".equals(vo.getIsDesktopAdmin())) { roles.add(new Role("ROLE_DESKTOP_ADMIN")); }
				if("1".equals(vo.getIsPortableAdmin())) { roles.add(new Role("ROLE_PORTABLE_ADMIN")); }

				break;
			default:
				break;
			}
		}

		User user = new User(vo);
		user.setAuthorities(roles);

		return user;
	}

}
