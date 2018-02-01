package com.api;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * TODO 쫌 억지로 쓰는부분이 많은거 같음... 나중에 수정 할 예정
 * @author joo
 *
 */
public class WebUtil {
	
	public static void setSession(String key, Object obj) {
		
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		setSessionAttribute(req, key, obj);
	}
	
	public static void setSessionAttribute(HttpServletRequest req,  String key, Object obj) {
		
		HttpSession session = getSession(req);
		
		if ( session.getAttribute(key) != null ){
			removeSessionAttribute(req, key);	// 기존값을 제거해 준다.
        }

		session.setAttribute(key, obj);
	}
	
	public static void removeSessionAttribute(String sessionKey) {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		removeSessionAttribute(req, sessionKey);
	}
	
	public static void removeSessionAttribute(HttpServletRequest req, String sessionKey) {
		HttpSession session = getSession(req);
		
		if ( session.getAttribute(sessionKey) != null ){
			session.removeAttribute(sessionKey);	// 기존값을 제거해 준다.
        }
	}

	public static Object getSessionAttribute(String key) {
		
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		return getSessionAttribute(req, key);
	}
	
	public static Object getSessionAttribute(HttpServletRequest req, String key) {
		
		HttpSession session = req.getSession();
		
		return session.getAttribute(key);
	}
	
	public static Object getBean(String beanName) {
		
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		return getBean(req, beanName);
	}
	
	public static Object getBean(HttpServletRequest req, String beanName) {
		
		HttpSession session = req.getSession();
		
		ServletContext context = session.getServletContext();
    	
    	WebApplicationContext wContext = WebApplicationContextUtils.getWebApplicationContext(context);
    	
		return wContext.getBean(beanName);
	}
	
	public static HttpSession getSession() {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		return getSession(req);
	}
	
	public static HttpSession getSession(HttpServletRequest request) {
		return request.getSession();
	}
}
