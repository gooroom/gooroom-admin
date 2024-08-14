package kr.gooroom.gpms.user.service.impl;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import kr.gooroom.gpms.user.service.UserTokenService;
import kr.gooroom.gpms.user.service.UserTokenVO;

@Service("userTokenService")
public class UserTokenServiceImpl implements UserTokenService {

	@Resource(name = "userTokenDAO")
	private UserTokenDAO userTokenDAO;

	@Override
	public UserTokenVO selectTokenByTokenId(String clientId, String token, String statusCd) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("clientId", clientId);
		paramMap.put("token", token);
		if (statusCd != null)
			paramMap.put("statusCd", statusCd);

		return userTokenDAO.selectTokenInfo(paramMap);

	}

}
