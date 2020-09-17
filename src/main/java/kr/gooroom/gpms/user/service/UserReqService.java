package kr.gooroom.gpms.user.service;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;

import java.util.HashMap;

public interface UserReqService {

     /**
     * 사용자 USB 등록/삭제 요청 리스트
     *
     * @param options HashMap<String, Object> option data
     * @return ResultPagingVO result object
     * @throws Exception
     */
    ResultPagingVO getUserReqListPaged(HashMap<String, Object> options) throws Exception;

    }
