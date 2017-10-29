package com.api.login;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.api.WebUtil;
import com.api.login.LoginAPI;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

public class LoginFactory implements LoginAPI{
	
	private static final Logger logger = LoggerFactory.getLogger(LoginFactory.class);
	
	//TODO : 어디서 host값을 받아올지 고민중
	private String host = "http://localhost";
	
	private String serviceName;
	private String clientId;
	private String clientSecret;
	private String redirectURL;
	
	private static final JSONParser JSON_PARSER = new JSONParser();
	
	private LoginAPI.UserMethod userMethod;
	
	
	//request 주소를 담은 프로퍼티
	@Resource(name="requestURL")
	private Properties properties;
	
	public InnerAPI innerAPI = new InnerAPI();
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = host+redirectURL;
	}

	public String getServiceName(){
		return this.serviceName;
	}
	
	public void setAccesstokenEndpoint(String accessTokenEndPoint){
		innerAPI.accessTokenEndPoint = accessTokenEndPoint;
	}
	
	public void setAuthorizationBaseURL(String authorizationBaseUrl){
		innerAPI.authorizationBaseUrl = authorizationBaseUrl;
	}
	
	@Override
	public void setUserMethod(UserMethod method) {
		this.userMethod = method;
	}
	
	/**
	 * 세션 유효성 검증을 위한 난수 생성기 
	 * @return
	 */
	private static String generateRandomString() {
		return UUID.randomUUID().toString();
	}

	/**
	 * session에서 login관련 상태값을 넣는다.
	 * @param session
	 * @param state
	 */
	private void setSession(HttpSession session,String state){
		session.setAttribute(LoginAPI.LOGIN_SESSION_STATE_KEY, state);		
	}
	
	/**
	 * 세션에 담긴 값을 넣는다.
	 * @param session
	 * @return
	 */
	private String getSession(HttpSession session){
		return (String) session.getAttribute(LoginAPI.LOGIN_SESSION_STATE_KEY);
	}

	@Override
	public String getAuthorizationUrl(HttpSession session) {
		// 세션 유효성 검증을 위하여 난수를 생성
		String state = generateRandomString();
		//생성한 난수 값을 session에 저장
		setSession(session,state);
		
		//Scribe에서 제공하는 인증 URL 생성
		OAuth20Service oauthService = getServiceBuilder(true).state(state).build(innerAPI);
		
		return oauthService.getAuthorizationUrl();
	}

	@Override
	public OAuth2AccessToken getAccessToken(HttpSession session, String code, String state) throws IOException {
		
		//Callback으로 전달받은 세선검증용 난수값과 세션에 저장되어있는 값이 일치하는지 확인
		String sessionState = getSession(session);
		if(sessionState !=null && sessionState.equals(state)){
		
			OAuth20Service oauthService = getServiceBuilder(true).build(innerAPI);
			// Scribe에서 제공하는 AccessToken 획득 기능으로 네아로 Access Token을 획득 
			OAuth2AccessToken accessToken = oauthService.getAccessToken(code);
			return accessToken;
		}
		return null;
	}
	
	//TODO : token으로 제공되는 API요청 메소드. 구현예정
	@Override
	public String requestAPI(String commandKey) {
		return null;
	}
	
	@Override
	public UserVo getUserProfile(OAuth2AccessToken oauthToken) throws IOException, ParseException {
		OAuth20Service oauthService = getServiceBuilder(true).build(innerAPI);
		
		String requestKey = serviceName+LoginAPI.USER_PROFILE;
		
		boolean requestURL = properties.containsKey(requestKey);
		
		if(!requestURL){
			logger.error("url이 존재하지 않음. properties key : "+requestKey);
			return null;
		}
		
		OAuthRequest request = new OAuthRequest(Verb.GET, properties.getProperty(requestKey), oauthService);
		
		oauthService.signRequest(oauthToken, request);
		Response response = request.send();
		
		String strResult = response.getBody();
		
		JSONObject userProfile = (JSONObject)JSON_PARSER.parse(strResult);
		
		if(userMethod == null) {
			logger.error("UserMethod가 정의되어 있지 않음");
			
			return null;
		}
		
		UserVo userVo = userMethod.getUserVo(userProfile);
		
		return userVo;
	}
	
	@Override
	public boolean login(HttpServletRequest req, String code, String state) throws IOException, ParseException {
		
		HttpSession session = req.getSession();
		
		OAuth2AccessToken oauthToken = getAccessToken(session, code, state);
    	
    	String token = oauthToken.getAccessToken();
    	
    	UserVo userVo = getUserProfile(oauthToken);
    	
    	
		if(userVo == null) {
			logger.error("로그인 잘못됨!");
			
			return false;
		}
		
		userVo.setAccessToken(token);
		
		/*
		 * TODO : userVo로 DB에 등록된 사용자를 조회. DB 구축되면 구현
    	Object userData = loginService.getUser(userVo);
    	*/
    	WebUtil.setSession(req, LoginAPI.LOGIN_SESSION_KEY, userVo);
    	
    	logger.info("User Vo :"+userVo);
    	
		return true;
	}
	
	@Override
	public boolean logOut() {
		
		//TODO : 할꺼 예정
		//로그아웃 API는 별도로 존재하지 않다고 함. 이유는 이용자 보호 정책이라 적혀 있음.
		//일단 내 어플리케이션에 등록된 세션 정보를 비우고, 차후 access token을 반납 하는 것으로 대체 예정
		
		WebUtil.removeSessionAttribute(LoginAPI.LOGIN_SESSION_KEY);
		
		
		return true;
	}
	
	/**
	 * 레퍼런스에는 DefaultApi20를 service에 주입을 해야한다고 나와있음.
	 * 다른곳에 만들어도 쓸때도 없어서 그냥 inner class로 선언함
	 * @author KIM
	 *
	 */
	private class InnerAPI extends DefaultApi20{
		
		public String accessTokenEndPoint; 
		
		public String authorizationBaseUrl;
		
		
		@Override
		public String getAccessTokenEndpoint() {
			return accessTokenEndPoint;
		}

		@Override
		protected String getAuthorizationBaseUrl() {
			return authorizationBaseUrl;
		}
	}
	
	private ServiceBuilder getServiceBuilder(boolean addCallback){
		
		ServiceBuilder sb = new ServiceBuilder()
				.apiKey(clientId)
				.apiSecret(clientSecret);
		
		if(addCallback){
			sb.callback(redirectURL);
		}
		
		return sb;
	}
}
