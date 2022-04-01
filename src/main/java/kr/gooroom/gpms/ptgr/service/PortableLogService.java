package kr.gooroom.gpms.ptgr.service;

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

import java.util.HashMap;

public interface PortableLogService {

    /**
     * 휴대형구름 로그 등록
     *
     * @param portableLogVO
     * @return StatusVO
     * @throws Exception
     */
    StatusVO createLogData(PortableLogVO portableLogVO) throws Exception;

    /**
     * 휴대형구름 로그 정보
     *
     * @return ResultVO
     * @throws Exception
     */
    ResultVO readLogData() throws Exception;

    /**
     * 휴대형구름 로그 정보
     *
     * @Param options
     * @return ResultVO
     * @throws Exception
     */
    ResultVO readLogDataByOptions(HashMap<String, Object> options) throws Exception;

    /**
     * 휴대형구름 로그 삭제
     *
     * @return StatusVO
     * @throws Exception
     */
    StatusVO deleteAllLogData() throws Exception;

    /**
     *
     * @return int
     * @throws Exception
     */
    int readNextLogDataIndex() throws Exception;

    /**
     *
     * @return int
     * @throws Exception
     */
    int readLogDataCount() throws Exception;
}
