package com.api.login;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.api.login.service.build.LoginAPI;

/**
 * login service하는 곳(REST API)이 추가 될때 마다 불가피하게 소스 수정이
 * 일어나는 controller 부분만 모아놓은 class
 * @author joo
 *
 */
@Controller
public class LoginCBController {

	@Resource(name="naverLogin")
	private LoginAPI naverLogin;
	
	@Resource(name="kakaoLogin")
	private LoginAPI kakaoLogin;
	
	@Resource(name="googleLogin")
	private LoginAPI googleLogin;
	
	private static final Logger logger = LoggerFactory.getLogger(LoginCBController.class);
	
	/**
	 * 로그인 성공 후 이동할 URL
	 */
	public String SUCCESS_LOGIN_URL="redirect:/";
	
	/**
	 * naver login 처리를 진행한다.
	 * code, state는 callback을 호출 시, 외부(네이버)에서 제공받음
	 * 각 sns마다 제공하는 데이터 형태가 다를 수 있어서, callback은 각 sns마다 따로 구현해야됨
	 * @param code
	 * @param state
	 * @param req
	 * @param model
	 * @return
	 * @throws Exception 
	 */
    @RequestMapping("/api/authen/login/naver/callback")
    public String naverCallback(@RequestParam String code, @RequestParam String state, HttpServletRequest req, Model model) throws Exception {
		naverLogin.login(req, code, state);
		
        return SUCCESS_LOGIN_URL;
    }
    
    /**
	 * naver login 처리를 진행한다.
	 * code, state는 callback을 호출 시, 외부(네이버)에서 제공받음
	 * 각 sns마다 제공하는 데이터 형태가 다를 수 있어서, callback은 각 sns마다 따로 구현해야됨
	 * @param code
	 * @param state
	 * @param req
	 * @param model
	 * @return
	 * @throws Exception 
	 */
    @RequestMapping("/api/authen/login/kakao/callback")
    public String kakaoCallback(@RequestParam String code, @RequestParam String state, HttpServletRequest req, Model model) throws Exception {
    	kakaoLogin.login(req, code, state);
		
        return SUCCESS_LOGIN_URL;
    }
    
    /**
	 * google login 처리를 진행한다.
	 * code, state는 callback을 호출 시, 외부(네이버)에서 제공받음
	 * 각 sns마다 제공하는 데이터 형태가 다를 수 있어서, callback은 각 sns마다 따로 구현해야됨
	 * @param code
	 * @param state
	 * @param req
	 * @param model
	 * @return
	 * @throws Exception 
	 */
    @RequestMapping("/api/authen/login/google/callback")
    public String googleCallback(@RequestParam String code, @RequestParam String state, HttpServletRequest req, Model model) throws Exception {
    	
		googleLogin.login(req, code, state);
		
        return SUCCESS_LOGIN_URL;
    }
}
