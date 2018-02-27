package com.api.login;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.api.WebUtil;
import com.api.login.service.LoginService;
import com.api.login.service.build.HandleLoginFactory;
import com.api.login.service.build.LoginAPI;
import com.api.login.service.build.LoginFactory;
import com.api.model.UserVo;

/**
 * login, logout 처리 controller.
 * 차후 loginFactory 인스턴스(beab)가 추가 된다 하더라도, 현재 class는
 * 수정사항 없이 관리 할 예정
 * @author joo
 *
 */
@Controller
public class LoginController{
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	
	//차후 API에서 받은 사용자 정보를 바탕으로 프로젝트 내 DB 조회 등 목적으로 만들어 놓기만 한 service 
	//@Autowired
	private LoginService loginService;
	
	
	@Autowired
	private List<? extends LoginFactory> loginFactoryList;
	
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
    public String loginList(HttpServletRequest req, Model model) {
		
		//로그인 URL 값을 model에 넣는다.
		new HandleLoginFactory(loginFactoryList).setLoginURLParams(model);
		
        return "loginList";
    }
 
    @RequestMapping("/logOut")
    @ResponseBody
    public Map<String, String> logOut(HttpServletRequest req, Model model) throws IOException {
    	
    	Map<String, String> map = new HashMap<String, String>();
    	
    	UserVo userVo = (UserVo) WebUtil.getSessionAttribute(LoginAPI.LOGIN_SESSION_KEY);
    	
    	if(userVo == null) {
    		
    		logger.debug("잘못된 접근. 로그인 상태가 아님");
    		
    		map.put("result", "로그인 된 상태가 아님");
    		
    		return map;
    	}
    	
    	LoginAPI loginAPI = (LoginAPI) WebUtil.getBean(userVo.getServiceName()+"Login");
    	
    	Map<String, String> resultMap = new HandleLoginFactory(loginAPI).getLogOutResult(map);
    	
    	map.putAll(resultMap);
    	
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
