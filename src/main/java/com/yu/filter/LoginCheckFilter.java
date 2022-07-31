package com.yu.filter;

import com.alibaba.fastjson2.JSON;
import com.yu.common.BaseContext;
import com.yu.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    //放行的url
    static String[] urls = new String[]{
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/user/sendMsg",//移动端发送短信
            "/user/login",//移动端登录
            "/",
            "/1"
    };

    static String[] urlHtml = new String[]{
            "/front/page/login",
            "/backend/page/login/login.html",
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        Long id = (Long) request.getSession().getAttribute("employee");
        Long userId = (Long) request.getSession().getAttribute("user");

        if (check(uri)) {
            filterChain.doFilter(request, response);
        } else {
            if (id != null) {
                BaseContext.setCurrentId(id);
                filterChain.doFilter(request, response);
                return;
            }
            if (userId != null) {
                BaseContext.setCurrentId(userId);
                filterChain.doFilter(request, response);
                return;
            }
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        }

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    /**
     * 路径匹配
     *
     * @param uri
     * @return
     */
    public boolean check(String uri) {
        for (String url : urls) {
            boolean b = PATH_MATCHER.match(url, uri);
            if (b)
                return true;
        }
        return false;
    }



}
