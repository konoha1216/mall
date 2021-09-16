package com.mct.mall.filter;


import com.mct.mall.common.Constant;
import com.mct.mall.model.pojo.User;
import com.mct.mall.service.UserService;
import java.io.IOException;
import java.io.PrintWriter;
import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

/**
 * @description: 管理员校验
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/22 6:42 下午
 * @version:v1.0
 */
public class AdminFilter implements Filter {
    @Resource
    UserService userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute(Constant.MCT_MALL_USER);
        if (currentUser == null ){
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("{\n"
                + "    \"status\": 10007,\n"
                + "    \"msg\": \"NEED_LOGIN\",\n"
                + "    \"data\": null\n"
                + "}");
            out.flush();
            out.close();
            return;
        }
        // 管理员才能加
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("{\n"
                + "    \"status\": 10009,\n"
                + "    \"msg\": \"NOT_ADMIN\",\n"
                + "    \"data\": null\n"
                + "}");
            out.flush();
            out.close();
        }
    }

    @Override
    public void destroy() {

    }
}
