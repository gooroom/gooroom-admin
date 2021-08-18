package kr.gooroom.gpms.ptgr.service;

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

public interface PortableCertService {

    /**
     * 휴대형구름 인증서 신규 등록
     *
     * @param portableCertVO
     * @return StatusVO
     * @throws Exception
     */
    StatusVO createCertData(PortableCertVO portableCertVO) throws Exception;

    /**
     * 휴대형구름 인증서 정보
     *
     * @Param  certId
     * @return ResultVO
     * @throws Exception
     */
    ResultVO readCertDataByCertId(String certId) throws Exception;

    /**
     * 휴대형구름 인증서 업데이트
     *
     * @param portableCertVO
     * @return StatusVO
     * @throws Exception
     */
    StatusVO updateCertData(PortableCertVO portableCertVO) throws Exception;

    /**
     * 휴대형구름 인증서 삭제
     *
     * @param certId
     * @return StatusVO
     * @throws Exception
     */
    StatusVO deleteCertDataByCertId(String certId) throws Exception;

    /**
     * 휴대형구름 인증서 Index
     *
     * @return int
     * @throws Exception
     */
    int readNextCertDataIndex() throws Exception;

    /**
     * 휴대형구름 인증서 Count
     *
     * @return int
     * @throws Exception
     */
    int readCertDataCount() throws Exception;
}
