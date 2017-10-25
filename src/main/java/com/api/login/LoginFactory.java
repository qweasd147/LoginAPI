package com.api.login;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		session.setAttribute(LoginAPI.LOGIN_SESSION_NAME, state);		
	}
	
	/**
	 * 세션에 담긴 값을 넣는다.
	 * @param session
	 * @return
	 */
	private String getSession(HttpSession session){
		return (String) session.getAttribute(LoginAPI.LOGIN_SESSION_NAME);
	}

	@Override
	public String getAuthorizationUrl(HttpSession session) {
		// 세션 유효성 검증을 위하여 난수를 생성
		String state = generateRandomString();
		//생성한 난수 값을 session에 저장
		setSession(session,state);
		
		//Scribe에서 제공하는 인증 URL 생성 기능을 이용하여 네아로 인증 URL 생성
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
	

	@Override
	public String requestAPI(String commandKey) {
		return null;
	}
	
	@Override
	public String getUserProfile(OAuth2AccessToken oauthToken) throws IOException {
		OAuth20Service oauthService = getServiceBuilder(true).build(innerAPI);
		
		String requestKey = serviceName+LoginAPI.USER_PROFILE;
		
		boolean requestURL = properties.contains(requestKey);
		
		if(!requestURL){
			logger.error("url이 존재하지 않음. properties key : "+requestKey);
			return null;
		}
		
		OAuthRequest request = new OAuthRequest(Verb.GET, properties.getProperty(requestKey), oauthService);
		
		oauthService.signRequest(oauthToken, request);
		Response response = request.send();
		
		return response.getBody();
	}
	
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
