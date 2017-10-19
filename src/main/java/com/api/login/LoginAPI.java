package com.api.login;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import com.github.scribejava.core.model.OAuth2AccessToken;

public interface LoginAPI{
	
	/**
	 * session에 login 관련 담을 key
	 */
	public String LOGIN_SESSION_NAME="loginSession";
	
	/**
	 * 로그인 요청 URL을 반환한다.
	 * @param session
	 * @return
	 */
	public String getAuthorizationUrl(HttpSession session);
	
	/**
	 * accesstoken을 요청한다.
	 * @param session
	 * @param code
	 * @param state
	 * @return
	 * @throws IOException
	 */
	public OAuth2AccessToken getAccessToken(HttpSession session, String code, String state) throws IOException;

}
