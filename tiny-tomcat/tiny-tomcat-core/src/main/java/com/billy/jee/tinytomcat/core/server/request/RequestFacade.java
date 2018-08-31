package com.billy.jee.tinytomcat.core.server.request;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

/**
 * @author home.ubuntu
 * @since 2018-04-08 20:07
 */
public class RequestFacade implements HttpServletRequest {
    private RawRequest rawRequest;
    private Map<String, String> headers;
    private Map<String, String[]> parameterMaps;
    private String queryString;

    public RequestFacade(RawRequest rawRequest) {
        this.rawRequest = rawRequest;
        parseHeaders();
        parseParameter();
    }


    private void parseParameter() {
        parameterMaps = new HashMap<>();
        String requestURI = rawRequest.getRequestLine().getRequestURI();
        int pathIndex = requestURI.indexOf("?");
        if (pathIndex > -1) {
            this.queryString = requestURI.substring(pathIndex);
        }

    }

    private void parseHeaders() {
        headers = new HashMap<>();
        List<RequestHeader> requestHeaders = rawRequest.getRequestHeaders();
        for (RequestHeader requestHeader : requestHeaders) {
            headers.put(requestHeader.getKey(), requestHeader.getValue());
        }
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String s) {
        return 0;
    }

    @Override
    public String getHeader(String s) {
        return headers.get(s);
    }

    @Override
    public Enumeration getHeaders(String s) {
        return Collections.enumeration(Arrays.asList(headers.get(s)));
    }

    @Override
    public Enumeration getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    @Override
    public int getIntHeader(String s) {
        return 0;
    }

    @Override
    public String getMethod() {
        return rawRequest.getRequestLine().getMethod();
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        String requestURI = rawRequest.getRequestLine().getRequestURI();
        int firstSlash = requestURI.indexOf("/");
        int secondSlash = requestURI.indexOf("/", firstSlash+1);
        if (-1 == secondSlash) {
            return requestURI.substring(firstSlash);
        }
        return requestURI.substring(firstSlash, secondSlash);
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return rawRequest.getRequestLine().getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer url = new StringBuffer();
        String scheme = getScheme();
        int port = getServerPort();
        if (port < 0)
            port = 80; // Work around java.net.URL bug

        url.append(scheme);
        url.append("://");
        url.append(getServerName());
        if ((scheme.equals("http") && (port != 80))
                || (scheme.equals("https") && (port != 443))) {
            url.append(':');
            url.append(port);
        }
        url.append(getRequestURI());

        return (url);
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean b) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String s) {
        return null;
    }

    @Override
    public Enumeration getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String s) {
        return new String[0];
    }

    @Override
    public Map getParameterMap() {
        return null;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public String getScheme() {
        String protocol = rawRequest.getRequestLine().getProtocol();
        Map<String, String> protocolMap = new HashMap<>();
        protocolMap.put("HTTP/1.1", "http");
        return protocolMap.getOrDefault(protocol, "http");
    }

    @Override
    public String getServerName() {
        // TODO
        return "localhost";
    }

    @Override
    public int getServerPort() {
        // TODO
        return 27016;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    public String getRealPath(String s) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }
}
