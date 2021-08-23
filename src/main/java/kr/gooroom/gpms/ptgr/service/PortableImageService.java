package kr.gooroom.gpms.ptgr.service;

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

import java.util.HashMap;

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
    ResultVO readImageDataById(HashMap<String, Object> options) throws Exception;

    /**
     * 휴대형구름 이미지 정보
     *
     * @Param  adminId
     * @return ResultVO
     * @throws Exception
     */
    ResultVO readImageDataByAdminId(String adminId) throws Exception;

    /**
     * 휴대형구름 이미지 정보 업데이트
     *
     * @Param imageVO
     * @return StatusVO
     * @throws Exception
     */
    StatusVO updateImageData (PortableImageVO imageVO) throws Exception;

    /**
     * 휴대형구름 이미지 정보 업데이트
     *
     * @Param imageVO
     * @return StatusVO
     * @throws Exception
     */
    StatusVO updateImageStatus (HashMap<String, Object> options) throws Exception;

    /**
     * 휴대형구름 이미지 발급 정보 변경 - 삭제
     *
     * @param id
     * @return StatusVO
     * @throws Exception
     */
    StatusVO removeImageDataByImageId(int id) throws Exception;

    /**
     * 휴대형구름 이미지 발급 정보 변경 - 삭제
     *
     * @param ids
     * @return StatusVO
     * @throws Exception
     */
    StatusVO removeImageDataByImageIds(HashMap<String, Object> ids) throws Exception;

    /**
     * 휴대형구름 이미지 발급 정보 변경 - 삭제
     *
     * @return StatusVO
     * @throws Exception
     */
    StatusVO removeAllImageData() throws Exception;

    /**
     * 휴대형구름 이미지 발급 정보 삭제
     *
     * @return StatusVO
     * @throws Exception
     */
    StatusVO deleteImageDataByImageId(int id);

    /**
     * 휴대형구름 이미지 Index
     *
     * @return int
     * @throws Exception
     */
    int readNextImageDataIndex() throws Exception;

    /**
     * 휴대형구름 이미지 Count
     *
     * @return int
     * @throws Exception
     */
    int readImageDataCount() throws Exception;
}
