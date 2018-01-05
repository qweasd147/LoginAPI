package com.api.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.api.WebUtil;
import com.api.login.LoginAPI;
import com.api.login.LoginAPI.UserMethod;
import com.api.login.service.LoginService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class LoginController{
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


	@Resource(name="naverLogin")
	private LoginAPI naverLogin;
	
	@Resource(name="kakaoLogin")
	private LoginAPI kakaoLogin;
	
	//@Autowired
	LoginService loginService;
	
	
	@Autowired
	private List<? extends LoginFactory> list;
	
	/**
	 * 로그인 페이지로 이동 요청 바인딩
	 * @return
	 */
	@RequestMapping("/login")
    public String loginList(HttpServletRequest req, Model model) {
		
		HttpSession session = req.getSession();
		
		if(list != null){
			
			//1. 세션 유효성 검증을 위하여 난수를 생성
			//2. 생성한 난수 값을 session에 저장
			//목적 : 현재 프로젝트에 있는 login list를 거쳐 가야만 로그인 처리 가능
			
			String state = UUID.randomUUID().toString();
			WebUtil.setSession(LoginAPI.LOGIN_SESSION_STATE_KEY, state);
			
			for(int i=0;i<list.size();i++){
				LoginFactory loginFactoryClazz = list.get(i);
				
				logger.info("load login factory. "+loginFactoryClazz.getServiceName());
				
				String authURL = loginFactoryClazz.getAuthorizationUrl(session, state);
				
				String serviceUrlName = loginFactoryClazz.getServiceName()+"URL";
				
				model.addAttribute(serviceUrlName, authURL);
			}
		}else{
			logger.warn("not exist LoginFactory instance");
		}
		
		
        return "loginList";
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
		
        return "home";
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
		
        return "home";
    }
    
    @RequestMapping("/logOut")
    @ResponseBody
    public Map<String, String> logOut(HttpServletRequest req, Model model) throws IOException {
    	
    	UserVo userVo = (UserVo) WebUtil.getSession(LoginAPI.LOGIN_SESSION_KEY);
    	
    	Map<String, String> map = new HashMap<String, String>();
    	
    	if(userVo == null) {
    		
    		logger.debug("잘못된 접근. 로그인 상태가 아님");
    		
    		map.put("result", "로그인 된 상태가 아님");
    		
    		return map;
    	}
    	
    	LoginAPI loginAPI = null;
    	
    	loginAPI = (LoginAPI) WebUtil.getBean(req, userVo.getServiceName()+"Login");
    	
    	if(loginAPI == null) {
    		logger.debug("logout 의존주입 실패!!!");
    	}
    	
    	boolean logOutResult = loginAPI.logOut();
    	
    	if(logOutResult) {
    		map.put("result", "로그아웃 처리됨");
    	}else {
    		map.put("result", "시스템 오류");
    	}
    	
    	return map;
    }
    
    @RequestMapping("/checkSession")
    @ResponseBody
    public void checkSession(HttpServletRequest req, Model model) throws IOException {
    	
    	HttpSession session = req.getSession();
    	
    	Enumeration<String> names = session.getAttributeNames();
    	
    	System.out.println("session");
    	while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			
			System.out.println("name : "+name+", val : "+session.getAttribute(name));
		}
    }
}
