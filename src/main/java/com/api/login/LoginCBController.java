package com.api.login;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.api.login.serviceBuild.LoginAPI;
import com.api.login.serviceBuild.LoginAPI.UserMethod;
import com.api.model.UserVo;

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
	
	private static final Logger logger = LoggerFactory.getLogger(LoginCBController.class);
	
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
    	
		naverLogin.setUserMethod(new UserMethod() {
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
		});
		
		naverLogin.login(req, code, state);
		
        return "redirect:/";
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
    	
    	kakaoLogin.setUserMethod(new UserMethod() {
			@Override
			public UserVo getUserVo(JSONObject profile) {
				
				UserVo userVo = new UserVo();
				JSONObject properties = null;
				
				if(profile == null || !profile.containsKey("properties")) {
					logger.error("통신 실패!");
					
					return null;
				}
				
				properties = (JSONObject) profile.get("properties");
				
				
				String id =profile.get("id").toString();	//long 형태로 반환된걸 String으로 변환
				String name = (String) properties.get("nickname");
				String nickName = (String) properties.get("nickname");
				String email = (String) profile.get("kaccount_email");
				
				
				userVo.setId(id)
					.setName(name)
					.setNickName(nickName)
					.setEmail(email)
					.setServiceName("kakao");
				
				return userVo;
			}
		});
		
    	kakaoLogin.login(req, code, state);
		
        return "redirect:/";
    }
}
