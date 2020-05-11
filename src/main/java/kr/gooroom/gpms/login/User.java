package kr.gooroom.gpms.login;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import kr.gooroom.gpms.user.service.AdminUserVO;

@SuppressWarnings("serial")
public class User implements UserDetails {

	private AdminUserVO adminUserVO;
	private List<Role> authorities;

	public User(AdminUserVO adminUserVO) {
		this.adminUserVO = adminUserVO;
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
		return null;
	}

	@Override
	public String getUsername() {
		if (adminUserVO != null) {
			return adminUserVO.getAdminId();
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

}
