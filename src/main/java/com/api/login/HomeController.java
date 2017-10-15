package com.api.login;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.api.login.naver.NaverLoginBO;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth2AccessToken;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController{
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private NaverLoginBO naverLoginBO;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	/**
	 * 로그인 페이지로 이동 요청 바인딩
	 * @return
	 */
	@RequestMapping("/login")
    public String naverLogin(HttpServletRequest req, Model model) {
		
		HttpSession session = req.getSession();
		
		String naverAuthUrl = naverLoginBO.getAuthorizationUrl(session);
		
		model.addAttribute("url", naverAuthUrl);
		
        return "naverLogin";
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
    	
    	OAuth2AccessToken oauthToken = naverLoginBO.getAccessToken(session, code, state);
    	
    	String token = oauthToken.getAccessToken();
    	
    	//TODO : cookie에 token 담아야함
    	
        return "home";
    }
}
