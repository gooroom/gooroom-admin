package kr.gooroom.gpms.login.exception;

import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

public class BadAccessIpException extends AuthenticationException {

	@Serial
	private static final long serialVersionUID = -4548271530012295383L;

	/**
	 * Constructs a <code>BadAccessIpException</code> with the specified message.
	 *
	 * @param msg the detail message
	 */
	public BadAccessIpException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a <code>BadAccessIpException</code> with the specified message and
	 * root cause.
	 *
	 * @param msg the detail message
	 * @param t   root cause
	 */
	public BadAccessIpException(String msg, Throwable t) {
		super(msg, t);
	}
}
