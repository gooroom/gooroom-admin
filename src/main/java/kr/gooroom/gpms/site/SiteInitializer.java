package kr.gooroom.gpms.site;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.site.service.SiteMngVO;
import kr.gooroom.gpms.site.service.impl.SiteMngDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class SiteInitializer {

    private static final Logger logger = LoggerFactory.getLogger(SiteInitializer.class);

    @Resource(name = "siteMngDAO")
    private SiteMngDAO siteMngDao;

    @PostConstruct
    public void init() {
        logger.info("========Start GPMS Site Init===========");

        try {
            SiteMngVO siteMngVO = siteMngDao.selectSiteMngData();

            siteMngVO.setModUserId("SYSTEM");
            siteMngVO.setSiteRegCd(GPMSConstants.SITE_REG_CODE);
            long reCnt = siteMngDao.updateSiteMngData(siteMngVO);
            if (reCnt > 0) {
                System.out.println("=====Success GPMS Site Init========");
                logger.debug("update site register code in SiteInitializer : {}, {}", GPMSConstants.CODE_UPDATE,
                        MessageSourceHelper.getMessage(GPMSConstants.MSG_SUCCESS));
            } else {
                logger.error("error in SiteInitializer : {}, {}, {}", GPMSConstants.CODE_UPDATE,
                        MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), "site register code update err");
            }
        } catch (Exception ex) {
            logger.error("error in SiteInitializer : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
        }

        logger.info("========End GPMS Site Init===========");
    }
}
