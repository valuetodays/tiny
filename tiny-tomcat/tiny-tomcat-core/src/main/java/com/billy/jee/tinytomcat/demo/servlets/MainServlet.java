package com.billy.jee.tinytomcat.demo.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author liulei@bshf360.com
 * @since 2018-04-09 15:59
 */
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

	@Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String info = "reqClassName: " + req.getClass().getName() + "@" + System.currentTimeMillis();
        System.out.println(info);
        resp.getWriter().write(info);
        System.out.println("done");
    }
}
