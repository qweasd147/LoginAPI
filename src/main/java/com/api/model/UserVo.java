package com.api.model;

public class UserVo {
	private String serviceName;		//default, kakao, naver 등
	private String id;
	private String name;
	private String nickName;
	private String email;
	private String accessToken;
	
	public String getServiceName() {
		return serviceName;
	}
	public UserVo setServiceName(String serviceName) {
		this.serviceName = serviceName;
		
		return this;
	}
	public String getId() {
		return id;
	}
	public UserVo setId(String id) {
		this.id = id;
		
		return this;
	}
	public String getName() {
		return name;
	}
	public UserVo setName(String name) {
		this.name = name;
		
		return this;
	}
	public String getNickName() {
		return nickName;
	}
	public UserVo setNickName(String nickName) {
		this.nickName = nickName;
		
		return this;
	}
	public String getEmail() {
		return email;
	}
	public UserVo setEmail(String email) {
		this.email = email;
		
		return this;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public UserVo setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		
		return this;
	}
	
	@Override
	public String toString() {
		return "UserVo [serviceName=" + serviceName + ", id=" + id + ", name=" + name + ", nickName=" + nickName
				+ ", email=" + email + ", accessToken=" + accessToken + "]";
	}
	
	
}
