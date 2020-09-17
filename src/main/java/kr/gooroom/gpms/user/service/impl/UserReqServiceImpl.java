package kr.gooroom.gpms.user.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.user.service.UserReqService;
import kr.gooroom.gpms.user.service.UserReqVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;


@Service("userReqService")
public class UserReqServiceImpl implements UserReqService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserServiceImpl.class);

    @Resource(name = "userReqDAO")
    private UserReqDAO userReqDao;
    
     /**
     * 사용자 요청(USB 등록/삭제) 리스트
     *
     * @param options HashMap<String, Object> option data
     * @return ResultPagingVO result object
     * @throws Exception
     */
    @Override
    public ResultPagingVO getUserReqListPaged(HashMap<String, Object> options) throws Exception {

        ResultPagingVO resultVO = new ResultPagingVO();

        try {
            List<UserReqVO> re = userReqDao.selectUserReqListPaged(options);
			long totalCount = userReqDao.selectUserReqListTotalCount(options);
			long filteredCount = userReqDao.selectUserReqListFilteredCount(options);

            if (re != null && re.size() > 0) {

                UserReqVO[] row = re.stream().toArray(UserReqVO[]::new);
                resultVO.setData(row);
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.selectdata")));

                resultVO.setRecordsTotal(String.valueOf(totalCount));
                resultVO.setRecordsFiltered(String.valueOf(filteredCount));
            } else {

                Object[] o = new Object[0];
                resultVO.setData(o);
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            }
        } catch (Exception ex) {
            logger.error("error in getUserReqListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            if (resultVO != null) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                        MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            }
        }

        return resultVO;
    }

}
