package com.api.login;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;

public interface LoginAPI{
	
	/**
	 * session에 login 관련 담을 key
	 */
	public String LOGIN_SESSION_STATE_KEY="LoginState";
	public String LOGIN_SESSION_KEY="LOGIN_SESSION_INFO";
	public String USER_PROFILE="v1.user.profile";
	
	public interface UserMethod{
		public UserVo getUserVo(JSONObject profile);
	};
	
	public void setUserMethod(UserMethod method);
	
	/**
	 * 로그인 요청 URL을 반환한다.
	 * @param session
	 * @return
	 */
	public String getAuthorizationUrl(HttpSession session);
	
	/**
	 * 외부 제공지에서 accesstoken을 요청한다.
	 * @param session
	 * @param code
	 * @param state
	 * @return
	 * @throws IOException
	 */
	public OAuth2AccessToken getOAuthAccessToken(HttpSession session, String code, String state) throws IOException;
	
	/**
	 * 세션에서 accessToken을 가져온다.
	 * @return
	 */
	public String getAccessTokenFromSession();
	
	/**
	 * 서비스 제공하는 쪽에서의 유저 프로필 정보를 조회하여 UserVo로 넘겨준다.
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public UserVo getUserProfile(OAuth2AccessToken oauthToken) throws IOException, ParseException;
	
	/**
	 * 서비스에서 제공하는 API를 요청한다.
	 * @param method get, post 등
	 * @param commandKey
	 * @return
	 * @throws IOException 
	 */
	public String requestAPI(Verb method, String commandKey, Map<String, String> params) throws IOException;
	
	/**
	 * 로그인 처리를 수행한다.
	 * @param resultMap
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public boolean login(HttpServletRequest req, String code, String state) throws IOException, ParseException;
	
	/**
	 * 로그아웃 처리한다.
	 * @return
	 */
	public boolean logOut();

}
