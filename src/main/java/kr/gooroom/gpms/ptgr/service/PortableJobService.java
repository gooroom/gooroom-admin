package kr.gooroom.gpms.ptgr.service;

import kr.gooroom.gpms.common.service.ResultVO;
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
    StatusVO deleteJobDataByImageId(int imageId) throws Exception;

    /**
     * 휴대형구름 젠킨스 Job 전체 삭제
     *
     * @return StatusVO
     * @throws Exception
     */
    StatusVO deleteAllJobData() throws Exception;

    /**
     * 휴대형구름 젠킨스 Job 검색
     *
     * @param imageId
     * @return StatusVO
     * @throws Exception
     */
    ResultVO readJobDataByImageId(int imageId) throws Exception;

    /**
     * 휴대형구름 젠킨스 Job 업데이트
     *
     * @param portableJobVO
     * @return StatusVO
     * @throws Exception
     */
    StatusVO updateJobData(PortableJobVO portableJobVO) throws Exception;
}
