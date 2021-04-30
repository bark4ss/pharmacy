package com.example.pharmacy.filter;

import com.example.pharmacy.entity.UserRole;
import com.example.pharmacy.entity.UserWithoutPassword;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/jsp/admin/*")
public class AdminAuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        if (isUserAuthorized(httpServletRequest)){
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            sendAtLoginPage(httpServletRequest, httpServletResponse);
        }
    }

    private void sendAtLoginPage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        String loginPageURI = PageManager.getPageURI(PageMappingConstant.LOGIN_PAGE_KEY);
        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + loginPageURI);
    }

    private boolean isUserAuthorized(HttpServletRequest httpServletRequest) {
        boolean result = false;
        UserWithoutPassword user = (UserWithoutPassword) httpServletRequest.getSession().getAttribute("user");
        if (user != null && user.getRole() == UserRole.ADMIN){
            result = true;
        }
        return result;
    }
}
