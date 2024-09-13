package kr.gooroom.gpms.totp.Service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.totp.Service.OtpAuthService;
import kr.gooroom.gpms.user.service.impl.AdminUserDAO;

@Service("OtpAuthService")
public class OtpAuthServiceImpl implements OtpAuthService {
    private static final Logger logger = LoggerFactory.getLogger(OtpAuthServiceImpl.class);

    @Resource(name = "adminUserDAO")
    private AdminUserDAO adminUserDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public StatusVO enableOtpAuth() throws Exception {
//        adminUserDao.update
        return new StatusVO();
    }

    @Override
    public StatusVO diableOtpAuth() throws Exception {
        return null;
    }
}
