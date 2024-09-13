package kr.gooroom.gpms.totp.Service;

import kr.gooroom.gpms.common.service.StatusVO;

public interface OtpAuthService {
    StatusVO enableOtpAuth() throws Exception;

    StatusVO diableOtpAuth() throws Exception;
}
