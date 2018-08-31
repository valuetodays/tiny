package com.billy.jee.tinytomcat.core;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liulei@bshf360.com
 * @since 2018-04-19 18:29
 */
public class Servlets {
    private Map<String, HttpServlet> servletMap = new HashMap<>();

    public Map<String, HttpServlet> getServletMap() {
        return servletMap;
    }
}
