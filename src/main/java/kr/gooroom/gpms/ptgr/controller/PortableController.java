package kr.gooroom.gpms.ptgr.controller;

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.ptgr.service.PortableListVO;
import kr.gooroom.gpms.ptgr.service.PortableVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/portable")
public class PortableController {

    private static final Logger logger = LoggerFactory.getLogger(PortableController.class);

    /**
     * 휴대형구름 일괄 등록
     *
     * @param ptrgListVO
     * @return ResultVO
     */
    @PostMapping(value = "/admin/infos")
    @ResponseBody
    public ResultVO registerInfos (@ModelAttribute PortableListVO ptrgListVO) {

        System.out.println("registerInfo");
        return null;
    };


    @GetMapping (value = "/admin/infos")
    public ResultVO getInfos (@RequestParam(value = "ptgrId") String ptgrId) {
        return null;
    };

    @PostMapping(value = "/admin/{ptgrId}")
    public ResultVO updateInfoForApprove(@PathVariable String ptgrId) {
        return null;
    }

    @PostMapping (value ="/admin/approve")
    public ResultVO updateAllInfoForApprove (@RequestParam(value ="adminId") String adminId) {
        return  null;
    }

    @DeleteMapping (value = "/admin/infos")
    public ResultVO deleteInfo (@RequestParam(value = "ptgrId" ) String ptgrId, @RequestParam(value = "is_all") int isAll) {
        return null;
    }

    /**
     * 휴대형구름 등록
     *
     * @param ptrgVO
     * @return ResultVO
     */
    @PostMapping(value = "/user/info")
    @ResponseBody
    public ResultVO registerInfo (@ModelAttribute PortableVO ptrgVO) {
        System.out.println("registerInfo");
        return null;
    };

    @GetMapping (value = "/user/info")
    public ResultVO getInfo (@RequestParam(value = "ptgrId") String ptgrId) {
        return null;
    };
}
