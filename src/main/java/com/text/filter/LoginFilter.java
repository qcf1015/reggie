package com.text.filter;


import com.alibaba.fastjson.JSON;
import com.text.common.BaseContext;
import com.text.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//  过滤器
@Slf4j
@WebFilter(filterName = "LoginFilter",urlPatterns = "/*")
public class LoginFilter implements Filter {

//    路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        获取本次uri
        String requestURI = request.getRequestURI();
        log.info("拦截请求：{}"+request.getRequestURI());
//        定义不需要拦截的路径
        String[] urls = new String[]{
                "common/**",
                "/employee/login",
                "/employee/logOut",
                "/backend/**",
                "front/**",
                "user/login",
                "user/sendMsg"
        };
//        判断是否需要处理
        boolean check = check(urls, requestURI);
//        如果不需要，直接放行
        if (check){
            log.info("本次请求不需要处理{}"+requestURI);
            filterChain.doFilter(request,response);
            return;
        }
//        判断登陆状态，如果已登录，直接放行
        if (request.getSession().getAttribute("employee") !=null){
            log.info("用户已登录，用户id{}",request.getSession().getAttribute("employee"));
            Long emId = (Long) request.getSession().getAttribute("employee");
//            保存id
            BaseContext.setThreadLocal(emId);
            filterChain.doFilter(request,response);
            return;
        }
        //        判断移动端登陆状态，如果已登录，直接放行
        if (request.getSession().getAttribute("user") !=null){
            log.info("用户已登录，用户id{}",request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
//            保存id
            BaseContext.setThreadLocal(userId);
            filterChain.doFilter(request,response);
            return;
        }
//        如果未登录，则返回未登录结果，通过输出流向客户端响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 判断是否需要放行方法
     * @param urls
     * @param requestURI
     * @return
     */

    public boolean check(String[] urls,String requestURI){
        for (String url : urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
