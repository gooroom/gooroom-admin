package kr.gooroom.gpms.ptgr.controller;

import kr.gooroom.gpms.common.service.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/portable")
public class PortableImageController {

    private static final Logger logger = LoggerFactory.getLogger(PortableImageController.class);

    @GetMapping(value="/admin/images")
    @ResponseBody
    public ResultVO getImageList() {
        return null;
    }

    @DeleteMapping (value="/admin/images")
    public ResultVO deleteImages (@RequestParam(value = "imageIds" ) String imageIds, @RequestParam(value = "is_all") int isAll) {
        return null;
    }


}
