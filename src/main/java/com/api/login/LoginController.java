package com.api.login;

import java.io.IOException;
import java.util.Enumeration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.api.login.LoginAPI;
import com.github.scribejava.core.model.OAuth2AccessToken;

/**
 * Handles requests for the application home page.
 */
@Controller
public class LoginController{
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Resource(name="naverLogin")
	private LoginAPI naverLogin;
	
	/**
	 * 로그인 페이지로 이동 요청 바인딩
	 * @return
	 */
	@RequestMapping("/login")
    public String naverLogin(HttpServletRequest req, Model model) {
		
		HttpSession session = req.getSession();
		
		String naverAuthUrl = naverLogin.getAuthorizationUrl(session);
		
		model.addAttribute("url", naverAuthUrl);
		
        return "loginList";
    }
 
	/**
	 * code, state는 callback을 호출 시, 외부(네이버)에서 제공받음
	 * @param code
	 * @param state
	 * @param req
	 * @param model
	 * @return
	 * @throws IOException
	 */
    @RequestMapping("/api/authen/login/naver/callback")
    public String callback(@RequestParam String code, @RequestParam String state, HttpServletRequest req, Model model) throws IOException {
    	
    	HttpSession session = req.getSession();
    	
    	OAuth2AccessToken oauthToken = naverLogin.getAccessToken(session, code, state);
    	
    	String token = oauthToken.getAccessToken();
    	
    	naverLogin.getUserProfile(oauthToken);
    	
    	//TODO : cookie에 token 담아야함
    	
        return "home";
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
