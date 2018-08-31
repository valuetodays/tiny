package com.billy.jee.tinytomcat.core.server.processor;

import com.billy.jee.tinytomcat.core.Servlets;
import com.billy.jee.tinytomcat.core.Webapp;
import com.billy.jee.tinytomcat.core.server.SocketServer;
import com.billy.jee.tinytomcat.core.server.request.RawRequest;
import com.billy.jee.tinytomcat.core.server.request.RawResponse;
import com.billy.jee.tinytomcat.core.server.request.RequestFacade;
import com.billy.jee.tinytomcat.core.server.request.ResponseFacade;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.List;

/**
 * servlet processor
 *
 * @author liulei@bshf360.com
 * @since 2018-04-20 16:26
 */
public class DefaultServletProcessor implements ServletProcessor {
    private static final Logger LOG = Logger.getLogger(ServletProcessor.class);
    private final SocketServer socketServer;

    public DefaultServletProcessor(SocketServer socketServer) {
        this.socketServer = socketServer;
    }

    @Override
    public void process(RawRequest rawRequest, RawResponse rawResponse) {
            LOG.debug("requestFacade/responseFacade is to prepare");
            RequestFacade requestFacade  = new RequestFacade(rawRequest);
            ResponseFacade responseFacade  = new ResponseFacade(rawResponse);
            LOG.debug("requestFacade/responseFacade is prepared");

            HttpServlet servlet = determineServlet(requestFacade, responseFacade);
//        HttpServlet servlet = new MainServlet();
            LOG.debug("Servlet is retrieved");
            try {
                servlet.service(requestFacade, responseFacade);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private HttpServlet determineServlet(RequestFacade requestFacade, ResponseFacade responseFacade) {
        String requestURI = requestFacade.getRequestURI();
        LOG.debug("uri: " + requestURI);
        StringBuffer requestURL = requestFacade.getRequestURL();
        LOG.debug("url: " + requestURL.toString());
        String contextPath = requestFacade.getContextPath();

        List<Webapp> webapps = socketServer.getWebapps();
        for (Webapp webapp : webapps) {
            List<Servlets> servletMap = webapp.getServlets();
            for (Servlets map : servletMap) {
                HttpServlet servlet = map.getServletMap().get(contextPath);
                if (servlet != null) {
                    return servlet;
                }
            }
        }

        throw new RuntimeException("un-parsed url:" + requestURL.toString());
    }

}
