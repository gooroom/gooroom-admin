package kr.gooroom.gpms.ptgr.service;

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

public interface PortableService {

    /**
     * 휴대형구름 신규 등록
     *
     * @param portableVO
     * @return StatusVO
     * @throws Exception
     */
    StatusVO createPortableData(PortableVO portableVO) throws Exception;

    /**
     * 휴대형구름 등록 정보 조회
     *
     * @return ResultVO
     * @throws Exception
     */
    ResultVO readPortableData() throws Exception;

    /**
     * 휴대형구름 등록 정보 조회
     * @Param adminId
     * @return ResultVO
     * @throws Exception
     */
    ResultVO readPortableDataById(String adminId) throws Exception;

    /**
     * 휴대형구름 발급 상태 업데이트
     *
     * @param ptgrId
     * @return StatusVO
     * @throws Exception
     */
    StatusVO  updatePortableDataForApprove(String ptgrId) throws Exception;

    /**
     * 휴대형구름 발급 상태 전체 업데이트
     *
     * @param adminId
     * @return StatusVO
     * @throws Exception
     */
    StatusVO  updateAllPortableDataForApprove(String adminId) throws Exception;

    /**
     * 휴대형구름 신청 정보 삭제
     *
     * @param ptgrId
     * @return StatusVO
     * @throws Exception
     */
    StatusVO  deletePortableData(String ptgrId) throws Exception;

    /**
     * 휴대형구름 신청 정보 전체 삭제
     *
     * @return StatusVO
     * @throws Exception
     */
    StatusVO  deleteAllPortableData() throws Exception;
}
