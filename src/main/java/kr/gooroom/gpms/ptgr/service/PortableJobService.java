package kr.gooroom.gpms.ptgr.service;

import kr.gooroom.gpms.common.service.StatusVO;

public interface PortableJobService {

    /**
     * 휴대형구름 젠킨스 Job 등록
     *
     * @param portableJobVO
     * @return StatusVO
     * @throws Exception
     */
    StatusVO createJobData(PortableJobVO portableJobVO) throws Exception;

    /**
     * 휴대형구름 젠킨스 Job 삭제
     *
     * @return StatusVO
     * @throws Exception
     */
    StatusVO deleteAllJobData() throws Exception;
}
