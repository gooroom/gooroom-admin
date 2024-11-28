package kr.gooroom.gpms.login.exception;

import org.springframework.security.core.AuthenticationException;

public class OtpTrialException extends AuthenticationException {
    // ~ Constructors
    // ===================================================================================================

    /**
     *
     */
    private static final long serialVersionUID = 423235568875700456L;

    /**
     * Constructs a <code>BadAccessIpException</code> with the specified message.
     *
     * @param msg the detail message
     */
    public OtpTrialException(String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>BadAccessIpException</code> with the specified message and
     * root cause.
     *
     * @param msg the detail message
     * @param t   root cause
     */
    public OtpTrialException(String msg, Throwable t) {
        super(msg, t);
    }
}
