package kr.gooroom.gpms.login;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;

public class Role implements GrantedAuthority {

	@Serial
	private static final long serialVersionUID = -4586688222110526356L;
	private String name;
	
	public Role() {
	}
	
	public Role(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getAuthority() {

		return this.name;
	}

}
