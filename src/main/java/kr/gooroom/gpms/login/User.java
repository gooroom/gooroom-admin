package kr.gooroom.gpms.login;

import java.util.Collection;
import java.util.List;

import kr.gooroom.gpms.user.service.UserVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import kr.gooroom.gpms.user.service.AdminUserVO;

@SuppressWarnings("serial")
public class User implements UserDetails {

	private AdminUserVO adminUserVO;
	private UserVO userVO;
	private List<Role> authorities;

	public User(AdminUserVO adminUserVO) {
		this.adminUserVO = adminUserVO;
	}

	public User(UserVO userVO) {
		this.userVO = userVO;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return (Collection<? extends GrantedAuthority>) this.authorities;
	}

	public void setAuthorities(List<Role> authorities) {
		this.authorities = authorities;
	}

	@Override
	public String getPassword() {
		if (adminUserVO != null) {
			return adminUserVO.getAdminPw();
		}
		if (userVO != null) {
			return userVO.getUserPasswd();
		}
		return null;
	}

	@Override
	public String getUsername() {
		if (adminUserVO != null) {
			return adminUserVO.getAdminId();
		}
		if (userVO != null) {
			return userVO.getUserId();
		}
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public List<String> getConnectIpList() {
		if (adminUserVO != null) {
			return adminUserVO.getConnIps();
		}
		return null;
	}

	public String getSessionId() {
		if (adminUserVO != null) {
			return adminUserVO.getSessionId();
		}
		return null;
	}

	public String getConnectIp() {
		if (adminUserVO != null) {
			return adminUserVO.getConnectIp();
		}
		return null;
	}

	public boolean isUserAccount() {
		return (userVO != null);
	}

}
