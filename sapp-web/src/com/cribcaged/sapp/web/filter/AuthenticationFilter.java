package com.cribcaged.sapp.web.filter;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = "/user/*")
public class AuthenticationFilter implements Filter{


	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
	    HttpServletResponse response = (HttpServletResponse) res;
	    HttpSession session = request.getSession(false);
	    String loginURI = request.getContextPath() + "/Login.xhtml";

	    boolean loggedIn = session != null && session.getAttribute("user") != null;
	    boolean loginRequest = request.getRequestURI().equals(loginURI);
	    boolean resourceRequest = request.getRequestURI().startsWith(request.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER);

	    if (loggedIn || loginRequest || resourceRequest) {
	        chain.doFilter(request, response);
	    } else {
	        response.sendRedirect(loginURI);
	    }
	}
	
	@Override
	public void destroy() {
		
	}
}
