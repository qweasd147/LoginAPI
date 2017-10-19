package com.api.login.naver;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class DefaultAPI extends DefaultApi20{
 	
	private String accessTokenEndPoint; 
	
	private String authorizationBaseUrl;
	
	public DefaultAPI(String accessTokenEndPoint, String authorizationBaseUrl){
		this.accessTokenEndPoint = accessTokenEndPoint;
		this.authorizationBaseUrl = authorizationBaseUrl;
	}
	
	
    /**
     * 액세스 토큰 요청 할 URL을 반환합니다.
     */
	@Override
	public String getAccessTokenEndpoint() {
		//return "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code";
		
		return this.accessTokenEndPoint;
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		//return "https://nid.naver.com/oauth2.0/authorize";
		
		return this.authorizationBaseUrl;
	}
	
	public DefaultAPI getInstance(){
		return this;
	}
}
