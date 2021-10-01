package kr.gooroom.gpms.ptgr;

public class PortableConstants {

    public static final String  LOG_ERROR = "ERROR";
    public static final String  LOG_INFO = "INFO";
    public static final String  LOG_WARN = "WARN";
    public static final String  LOG_DEBUG = "DEBUG";

    public static final String STATUS_APPROVE_REQUEST = "REQUEST";
    public static final String STATUS_APPROVE= "APPROVE";
    public static final String STATUS_APPROVE_REAPPROVE= "REAPPROVE";

    public static final int STATUS_IMAGE_REQUEST = 0; //"REQUEST";
    public static final int STATUS_IMAGE_CREATE = 1; //"CREATE";
    public static final int STATUS_IMAGE_COMPLETE = 2; //"COMPLETE";
    public static final int STATUS_IMAGE_DELETE = 3; //"DELETE";
    public static final int STATUS_IMAGE_FAILED = 4; //"FAILED";
    public static final int STATUS_IMAGE_COPIED_FAIL =  5; //"FAILED";

    public static final String STATUS_CERT_DEFAULT = "REQUEST";
    public static final String STATUS_CERT_CREATE = "CREATE";

    public static final int STATUS_BUILD_DEFAULT = 0;
    public static final int STATUS_BUILD_REQUEST = 1;
}
