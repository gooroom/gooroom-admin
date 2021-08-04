package kr.gooroom.gpms.ptgr.controller;

import kr.gooroom.gpms.common.service.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/portable")
public class PortableCertController {

    private static final Logger logger = LoggerFactory.getLogger(PortableCertController.class);

    @GetMapping (value="/user/cert")
    public ResultVO getCert (@RequestParam(value = "certId" ) String certId)  {
        return null;
    }
}
