package com.mct.mall.filter;


import com.mct.mall.common.Constant;
import com.mct.mall.model.pojo.User;
import java.io.IOException;
import java.io.PrintWriter;
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
public class UserFilter implements Filter {

    public static User currentUser;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        currentUser = (User) session.getAttribute(Constant.MCT_MALL_USER);
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

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
