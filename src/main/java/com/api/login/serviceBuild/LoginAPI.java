package com.api.login.serviceBuild;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.api.model.UserVo;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;

public interface LoginAPI{
	
	/**
	 * session에 login 관련 담을 key
	 */
	public String LOGIN_SESSION_STATE_KEY="LoginState";
	public String LOGIN_SESSION_KEY="LOGIN_SESSION_INFO";
	public String USER_PROFILE="v1.user.profile";
	public String LOGOUT_KEY="v1.accesstokenDelete";
	
	public interface UserMethod{
		public UserVo getUserVo(JSONObject profile);
	};
	
	public void setUserMethod(UserMethod method);
	
	/**
	 * 로그인 요청 URL을 반환한다.
	 * @param session
	 * @param state
	 * @return
	 */
	public String getAuthorizationUrl(HttpSession session, String state);
	
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
	
	/**
	 * 사용자 정보가 유효한지 판별한다(세션, token 유효성 검사).
	 * @return
	 */
	public boolean accountVerify();
	
	/**
	 * 현재 인스턴스의 서비스명(naver, kakao 등)을 가져온다.
	 * @return
	 */
	public String getServiceName();

}
