package com.billy.jee.tinytomcat.core.server.request;

/**
 * @author liulei@bshf360.com
 * @since 2018-04-08 13:31
 */
public class RequestLine {
    private String method;
    private String requestURI;
    private String protocol;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method='" + method + '\'' +
                ", requestURI='" + requestURI + '\'' +
                ", protocol='" + protocol + '\'' +
                '}';
    }
}
