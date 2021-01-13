package kr.gooroom.gpms.login.exception;

import org.springframework.security.core.AuthenticationException;

public class DuplicateAccessIpException extends AuthenticationException {
	// ~ Constructors
	// ===================================================================================================

	/**
	 * 
	 */
	private static final long serialVersionUID = 4414132120245767084L;

	/**
	 * Constructs a <code>DuplicateAccessIpException</code> with the specified
	 * message.
	 *
	 * @param msg the detail message
	 */
	public DuplicateAccessIpException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a <code>DuplicateAccessIpException</code> with the specified
	 * message and root cause.
	 *
	 * @param msg the detail message
	 * @param t   root cause
	 */
	public DuplicateAccessIpException(String msg, Throwable t) {
		super(msg, t);
	}
}
