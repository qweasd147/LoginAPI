package com.api.login.service.support;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.api.login.service.build.LoginAPI;
import com.api.login.service.build.LoginFactory;
import com.api.model.UserVo;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.Verb;

public class NaverAPI extends LoginFactory{
	
	private static final Logger logger = LoggerFactory.getLogger(NaverAPI.class);
	
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
	public UserVo getUserVo(JSONObject profile) {
		
		String result = (String) profile.get("message");
		
		if(!"success".equals(result)){
			
			logger.error("통신 실패!");
			
			return null;
		};
		
		UserVo userVo = new UserVo();
		
		JSONObject respJSON = (JSONObject) profile.get("response");
		
		String id = (String) respJSON.get("id");
		String name = (String) respJSON.get("name");
		String nickName = (String) respJSON.get("nickname");
		String email = (String) respJSON.get("email");
		
		userVo.setId(id)
				.setName(name)
				.setNickName(nickName)
				.setEmail(email)
				.setServiceName("naver");
		
		return userVo;
	}

	@Override
	public String logoutProcess() throws IOException {
		
		String requestKey = getPropertiesKey(LoginAPI.LOGOUT_KEY);
		
		Map<String, String> params = new HashMap<String, String>();
		
		params.put(OAuthConstants.CLIENT_ID, getClientId());
		params.put(OAuthConstants.CLIENT_SECRET, getClientSecret());
		
		return requestAPI(Verb.GET,requestKey , params);
	}
}
