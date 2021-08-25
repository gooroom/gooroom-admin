package kr.gooroom.gpms.ptgr.controller;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.ptgr.service.PortableCertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/portable")
public class PortableCertController {

    private static final Logger logger = LoggerFactory.getLogger(PortableCertController.class);

    @Resource(name = "portableCertService")
    private PortableCertService portableCertService;

    /**
     * 휴대형 구름 인증서 정보
     *
     * @param certId
     * @return ResultVO
     */
    @PostMapping (value="/readCert")
    @ResponseBody
    public ResultVO getCert (@RequestParam(value= "certId") String certId) {

        ResultVO resultVO = new ResultVO();
        try
        {
            if (certId == null || certId.isEmpty()) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                        MessageSourceHelper.getMessage("portable.result.errdata")));
            }
            else {
                resultVO = portableCertService.readCertDataByCertId(certId);
            }
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.MSG_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));

            e.printStackTrace();
        }

        return resultVO;
    }
}
