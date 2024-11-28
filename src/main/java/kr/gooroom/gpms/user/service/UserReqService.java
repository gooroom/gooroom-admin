package kr.gooroom.gpms.user.service;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

import java.util.HashMap;

public interface UserReqService {

    /**
     * 사용자 USB 등록/삭제 요청 리스트
     *
     * @param userId String
     * @return ResultVO result object
     * @throws Exception
     */
    ResultVO getUserReqList(String userId) throws Exception;

    /**
     * 사용자 USB 등록/삭제 요청 데이터
     *
     * @param reqSeq String
     * @return ResultVO result object
     * @throws Exception
     */
    ResultVO getUserReqData(String reqSeq) throws Exception;

     /**
     * 사용자 USB 등록/삭제 요청 리스트
     *
     * @param options HashMap<String, Object> option data
     * @return ResultPagingVO result object
     * @throws Exception
     */
    ResultPagingVO getUserReqListPaged(HashMap<String, Object> options) throws Exception;

    /**
     * 사용자 요청관리 로그
     * <p>
     * logging history data.
     *
     * @param options HashMap<String, Object> option data
     * @return ResultPagingVO result data bean
     * @throws Exception
     */
    ResultPagingVO getUserReqActListPaged(HashMap<String, Object> options) throws Exception;

    /**
     * 사용자 USB 등록/삭제 요청 승인
     *
     * @param reqSeqs string[] target request seq array
     * @return StatusVO result status object
     * @throws Exception
     */
    StatusVO approvalUserReq(String[] reqSeqs) throws Exception;

    /**
     * 사용자 USB 등록/삭제 요청 반려
     *
     * @param reqSeqs string[] target request seq array
     * @return StatusVO result status object
     * @throws Exception
     */
    StatusVO denyUserReq(String[] reqSeqs) throws Exception;

    /**
     * 사용자 USB 권한 회수
     *
     * @param reqSeq string target request seq
     * @return StatusVO result status object
     * @throws Exception
     */
    StatusVO revokeUsbPermissionFromAdmin(String reqSeq) throws Exception ;


    }
