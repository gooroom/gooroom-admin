package kr.gooroom.gpms.ptgr.controller;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.gkm.utils.FileUtils;
import kr.gooroom.gpms.ptgr.service.PortableCertService;
import kr.gooroom.gpms.ptgr.service.PortableCertVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        if (GPMSConstants.USE_PORTABLE.equalsIgnoreCase("false")) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam")));
            return resultVO;
        }

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

    /**
     * 휴대형 구름 인증서 정보
     *
     * @param certId
     * @return ResultVO
     */
    @PostMapping (value="/removeCert")
    @ResponseBody
    public ResultVO removeCert (@RequestParam(value= "certId") String certId,
                                @RequestParam(value= "dataDelete") String dataDelete) {

        ResultVO resultVO = new ResultVO();
        try
        {
            if (certId == null || certId.isEmpty()) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                        MessageSourceHelper.getMessage("portable.result.errdata")));
            }
            else {
                resultVO = portableCertService.readCertDataByCertId(certId);
                if (resultVO.getData().length == 0) {
                    resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT,
                            MessageSourceHelper.getMessage("portable.result.nodatacert")));
                }
                PortableCertVO certVO = (PortableCertVO)resultVO.getData()[0];

                Path path = Paths.get(certVO.getCertPath());
                FileUtils.delete(new File(path.getParent().toString()));

                if (dataDelete != null && dataDelete.equalsIgnoreCase("true"))
                    portableCertService.deleteCertDataByCertId(Integer.parseInt(certId));
            }
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.MSG_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));

            e.printStackTrace();
        }

        return resultVO;
    }
}
