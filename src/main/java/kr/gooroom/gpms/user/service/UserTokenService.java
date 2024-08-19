package kr.gooroom.gpms.user.service;

public interface UserTokenService {

	/**
	 * 토큰 발급 정보 확인
	 * 
	 * @param clientId
	 * @param token
	 * @param statusCd (optional) : loginToken, otpToken 구분
	 * @return
	 * @throws Exception
	 */
	UserTokenVO selectTokenByTokenId(String clientId, String token, String statusCd) throws Exception;
}
