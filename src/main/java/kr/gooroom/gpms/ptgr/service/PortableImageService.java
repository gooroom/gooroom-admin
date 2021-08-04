package kr.gooroom.gpms.ptgr.service;

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

public interface PortableImageService {

    /**
     * 휴대형구름 이미지 발급 정보 신규 등록
     *
     * @param portableImageVO
     * @return StatusVO
     * @throws Exception
     */
    StatusVO createImageData(PortableImageVO portableImageVO) throws Exception;

    /**
     * 휴대형구름 이미지 정보
     *
     * @return ResultVO
     * @throws Exception
     */
    ResultVO readImageData() throws Exception;

    /**
     * 휴대형구름 이미지 정보
     *
     * @Param  userId
     * @return ResultVO
     * @throws Exception
     */
    ResultVO readImageDataByUserId(String userId) throws Exception;

    /**
     * 휴대형구름 이미지 정보
     *
     * @Param  adminId
     * @return ResultVO
     * @throws Exception
     */
    ResultVO readImageDataByAdminId(String adminId) throws Exception;

    /**
     * 휴대형구름 이미지 발급 정보 삭제
     *
     * @param imageId
     * @return StatusVO
     * @throws Exception
     */
    StatusVO deleteImageDataByImageId(String imageId) throws Exception;

    /**
     * 휴대형구름 이미지 발급 정보 삭제
     *
     * @return StatusVO
     * @throws Exception
     */
    StatusVO deleteAllImageData() throws Exception;
}
