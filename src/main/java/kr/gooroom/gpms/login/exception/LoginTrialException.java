package kr.gooroom.gpms.login.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginTrialException extends AuthenticationException {
    // ~ Constructors
    // ===================================================================================================

    /**
     *
     */
    private static final long serialVersionUID = 383235568875700456L;

    /**
     * Constructs a <code>BadAccessIpException</code> with the specified message.
     *
     * @param msg the detail message
     */
    public LoginTrialException(String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>BadAccessIpException</code> with the specified message and
     * root cause.
     *
     * @param msg the detail message
     * @param t   root cause
     */
    public LoginTrialException(String msg, Throwable t) {
        super(msg, t);
    }
}
