package kr.gooroom.gpms.ptgr.service;

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

import java.util.HashMap;

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
    ResultVO readPortableView() throws Exception;

    /**
     * 휴대형구름 등록 정보 조회
     *
     * @Param options
     * @return ResultVO
     * @throws Exception
     */
    ResultVO readPortableViewById(HashMap<String, Object> options) throws Exception;

    /**
     * 휴대형구름 등록 정보 조회
     *
     * @Param options
     * @return ResultVO
     * @throws Exception
     */
    ResultVO readPortableDataById(HashMap<String, Object> options) throws Exception;

    /**
     * 휴대형구름 일괄 등록, 미승인 정보 조회
     *
     * @Param options
     * @return ResultVO
     * @throws Exception
     */
    ResultVO readPortableDataByAdminIdAndApprove(HashMap<String, Object> options) throws Exception;

    /**
     * 휴대형구름 발급 상태 업데이트
     *
     * @param portableVO
     * @return StatusVO
     * @throws Exception
     */
    StatusVO  updatePortableData(PortableVO portableVO) throws Exception;

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
     * @param ids
     * @return StatusVO
     * @throws Exception
     */
    StatusVO  deletePortableData(HashMap<String, Object> ids) throws Exception;

    /**
     * 휴대형구름 신청 정보 전체 삭제
     *
     * @return StatusVO
     * @throws Exception
     */
    StatusVO  deleteAllPortableData() throws Exception;

    int readNextPortableDataIndex() throws Exception;

    int readPortableDataCount() throws Exception;
}
