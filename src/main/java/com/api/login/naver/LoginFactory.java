package com.api.login.naver;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.api.login.LoginAPI;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

public class LoginFactory implements LoginAPI{
	
	private String host = "http://localhost";
	
	private String serviceName;
	private String clientId;
	private String clientSecret;
	private String redirectURL;
	
	public DefaultAPI defaultAPI;
	
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

	public void setDefaultAPI(DefaultAPI defaultAPI) {
		this.defaultAPI = defaultAPI;
	}
	
	public String getServiceName(){
		return this.serviceName;
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
		OAuth20Service oauthService = new ServiceBuilder()
				.apiKey(clientId)
				.apiSecret(clientSecret)
				.callback(redirectURL)
				.state(state) //앞서 생성한 난수값을 인증 URL생성시 사용함
				.build(defaultAPI);

		return oauthService.getAuthorizationUrl();
	}

	@Override
	public OAuth2AccessToken getAccessToken(HttpSession session, String code, String state) throws IOException {
		
		//Callback으로 전달받은 세선검증용 난수값과 세션에 저장되어있는 값이 일치하는지 확인
		String sessionState = getSession(session);
		if(sessionState !=null && sessionState.equals(state)){
		
			OAuth20Service oauthService = new ServiceBuilder()
					.apiKey(clientId)
					.apiSecret(clientSecret)
					.callback(redirectURL)
					.state(state)
					.build(defaultAPI);
					
			
			// Scribe에서 제공하는 AccessToken 획득 기능으로 네아로 Access Token을 획득 
			OAuth2AccessToken accessToken = oauthService.getAccessToken(code);
			return accessToken;
		}
		return null;
	}
}
