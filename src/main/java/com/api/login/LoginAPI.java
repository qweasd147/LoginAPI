package com.api.login;

public interface LoginAPI{
	
	public String LOGIN_SESSION_NAME="loginSession";
	
	/**
	 * DefaultApi20 확장한 클래스를 반환한다.
	 * @return
	 */
	public Class<?> getClazzInstance();
	
	/**
	 * 인증 처리후 실행할 메소드
	 */
	public void callbackMethod();

}
