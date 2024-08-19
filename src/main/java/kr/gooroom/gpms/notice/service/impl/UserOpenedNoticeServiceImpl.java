package kr.gooroom.gpms.notice.service.impl;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.notice.service.UserOpenedNoticeService;
import kr.gooroom.gpms.notice.service.UserOpenedNoticeVO;

@Service("userOpenedNoticeService")
public class UserOpenedNoticeServiceImpl implements UserOpenedNoticeService {

	private static final Logger logger = LoggerFactory.getLogger(UserOpenedNoticeServiceImpl.class);

	@Resource(name = "userOpenedNoticeDAO")
	private UserOpenedNoticeDAO userOpenedNoticeDAO;

	@Override
	public StatusVO createUserOpenedNotice(String userId, String noticePublishId) {

		StatusVO statusVO = new StatusVO();

		try {
			UserOpenedNoticeVO newUserOpenedNoticeVO = new UserOpenedNoticeVO();
			newUserOpenedNoticeVO.setUserId(userId);
			newUserOpenedNoticeVO.setNoticePublishId(noticePublishId);
			// create user opened notice
			long resultCnt = userOpenedNoticeDAO.createUserOpenedNotice(newUserOpenedNoticeVO);
			if (resultCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("userOpenedNotice.result.insert"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("userOpenedNotice.result.noinsert"));
			}
		} catch (Exception ex) {
			logger.error("error in createUserOpenedNotice : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}
		return statusVO;
	}

}