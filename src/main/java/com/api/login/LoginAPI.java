package com.api.login;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.json.simple.parser.ParseException;

import com.github.scribejava.core.model.OAuth2AccessToken;

public interface LoginAPI{
	
	/**
	 * session에 login 관련 담을 key
	 */
	public String LOGIN_SESSION_NAME="loginSession";
	public String USER_PROFILE=".v1.user.profile";
	
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
	
	/**
	 * 유저 프로필 정보를 조회한다.
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public String getUserProfile(OAuth2AccessToken oauthToken) throws IOException, ParseException;
	
	/**
	 * 서비스에서 제공하는 API를 요청한다.
	 * @param commandKey
	 * @return
	 */
	public String requestAPI(String commandKey);

}
