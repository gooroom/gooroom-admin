package kr.gooroom.gpms.login.exception;

import org.springframework.security.core.AuthenticationException;

public class OtpTrialExceedException extends AuthenticationException {
    // ~ Constructors
    // ===================================================================================================

    /**
     *
     */
    private static final long serialVersionUID = 383424243875700456L;

    /**
     * Constructs a <code>BadAccessIpException</code> with the specified message.
     *
     * @param msg the detail message
     */
    public OtpTrialExceedException(String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>BadAccessIpException</code> with the specified message and
     * root cause.
     *
     * @param msg the detail message
     * @param t   root cause
     */
    public OtpTrialExceedException(String msg, Throwable t) {
        super(msg, t);
    }
}
