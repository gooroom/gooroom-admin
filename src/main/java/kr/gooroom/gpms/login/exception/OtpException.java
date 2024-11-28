package kr.gooroom.gpms.login.exception;

import org.springframework.security.core.AuthenticationException;

public class OtpException extends AuthenticationException {
    // ~ Constructors
    // ===================================================================================================

    /**
     *
     */
    private static final long serialVersionUID = -383235568875700456L;

    /**
     * Constructs a <code>DuplicateAccessIpException</code> with the specified
     * message.
     *
     * @param msg the detail message
     */
    public OtpException(String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>DuplicateAccessIpException</code> with the specified
     * message and root cause.
     *
     * @param msg the detail message
     * @param t   root cause
     */
    public OtpException(String msg, Throwable t) {
        super(msg, t);
    }
}
