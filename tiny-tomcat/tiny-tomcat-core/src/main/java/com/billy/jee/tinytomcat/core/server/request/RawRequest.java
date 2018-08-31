package com.billy.jee.tinytomcat.core.server.request;

import java.util.List;

/**
 * @author home.ubuntu
 * @since 2018-04-08 20:09
 */
public class RawRequest {
    private RequestLine requestLine;
    private List<RequestHeader> requestHeaders;


    public RequestLine getRequestLine() {
        return requestLine;
    }

    public void setRequestLine(RequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public List<RequestHeader> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(List<RequestHeader> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }
}
