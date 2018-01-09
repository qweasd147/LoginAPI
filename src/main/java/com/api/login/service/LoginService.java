package com.api.login.service;

import java.util.Map;

import com.api.model.UserVo;

public interface LoginService {
	/**
	 * 로그인 정보를 바탕으로 DB에 등록된 정보를 조회한다.
	 * @param userVo
	 * @return
	 */
	public Map<String, String> getUser(UserVo userVo);
}
