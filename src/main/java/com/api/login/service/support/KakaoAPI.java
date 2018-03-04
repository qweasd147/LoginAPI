package com.api.login.service.support;


import java.io.IOException;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.api.login.service.build.LoginAPI;
import com.api.login.service.build.LoginFactory;
import com.api.model.UserVo;
import com.github.scribejava.core.model.Verb;

public class KakaoAPI extends LoginFactory{
	
	private static final Logger logger = LoggerFactory.getLogger(KakaoAPI.class);
	
	//TODO : host값을 어디서 초기화 해야 하는지 고민중
	private String host = "http://localhost";
	
	private String serviceName;
	private String clientId;
	private String clientSecret;
	private String redirectURL;
	private String accessTokenEndPoint;
	private String authorizationBaseURL;
	
	@Override
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	@Override
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = host + redirectURL;
	}

	@Override
	public void setAccesstokenEndpoint(String accessTokenEndPoint) {
		this.accessTokenEndPoint = accessTokenEndPoint;
		
	}

	@Override
	public void setAuthorizationBaseURL(String authorizationBaseURL) {
		this.authorizationBaseURL = authorizationBaseURL;
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}


	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public String getClientSecret() {
		return clientSecret;
	}

	@Override
	public String getRedirectURL() {
		return redirectURL;
	}

	@Override
	public String getAccesstokenEndpoint() {
		return accessTokenEndPoint;
	}

	@Override
	public String getAuthorizationBaseURL() {
		return authorizationBaseURL;
	}

	@Override
	public UserVo getUserVo(JSONObject userProfile) {
		UserVo userVo = new UserVo();
		JSONObject properties = null;
		
		if(userProfile == null || !userProfile.containsKey("properties")) {
			logger.error("통신 실패!");
			
			return null;
		}
		
		try {
			properties = (JSONObject) userProfile.get("properties");
			
			
			String id =userProfile.get("id").toString();	//long 형태로 반환된걸 String으로 변환
			String name = (String) properties.get("nickname");
			String nickName = (String) properties.get("nickname");
			String email = (String) userProfile.get("kaccount_email");
			
			
			userVo.setId(id)
				.setName(name)
				.setNickName(nickName)
				.setEmail(email)
				.setServiceName("kakao");
		}catch (Exception e) {
			//parsing exception
			throw e;
		}
		
		return userVo;
	}

	@Override
	public String logoutProcess() throws IOException {
		
		String requestKey = getPropertiesKey(LoginAPI.LOGOUT_KEY);
		
		String result = requestAPI(Verb.GET,requestKey , null);;
		
		return result;
	}
}
