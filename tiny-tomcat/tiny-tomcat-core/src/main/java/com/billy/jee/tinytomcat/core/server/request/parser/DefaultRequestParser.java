package com.billy.jee.tinytomcat.core.server.request.parser;

import com.billy.jee.tinytomcat.core.server.request.RawRequest;
import com.billy.jee.tinytomcat.core.server.request.RequestHeader;
import com.billy.jee.tinytomcat.core.server.request.RequestLine;

import java.util.ArrayList;
import java.util.List;

/**
 * the default RequestParser
 *
 */
public class DefaultRequestParser extends AbstractRequestParser {

    @Override
    public RawRequest parse(String reqString) {
        RequestLine requestLine = parseRequestLine(reqString);
        LOG.info(requestLine);
        List<RequestHeader> headerList = parseRequestHeader(reqString);
       // parseBody(reqStr);
        RawRequest rawRequest = new RawRequest();
        rawRequest.setRequestLine(requestLine);
        rawRequest.setRequestHeaders(headerList);

        return rawRequest;
    }

    private void parseBody(String reqStr) {
        int start = reqStr.indexOf("\r\n\r\n") + 4;
        String bodyString = reqStr.substring(start);

    }

    private List<RequestHeader> parseRequestHeader(String reqStr) {
        int start = reqStr.indexOf("\r\n") + 2;
        int end = reqStr.indexOf("\r\n\r\n");
        String headerString = reqStr.substring(start, end);
//        LOG.info(headerString);
        List<RequestHeader> headerList = new ArrayList<>();
        String[] headerStrArr = headerString.split("\r\n");
        for (String headerStr : headerStrArr) {
            int headerStartIndex = headerStr.indexOf(": ");
            String headerKey = headerStr.substring(0, headerStartIndex);
            String headerValue = headerStr.substring(headerStartIndex + 2);
            LOG.info("header -> " + headerKey + ": " + headerValue);
            headerList.add(new RequestHeader(headerKey, headerValue));
        }

        return headerList;
    }

    private RequestLine parseRequestLine(String reqStr) {
        String line1 = reqStr.substring(0, reqStr.indexOf("\r\n"));
        LOG.info(line1);
        
        String[] lineArr = line1.split(" ");
        for (String seg : lineArr) {
            LOG.info(seg);
        }
        if (lineArr.length != 3) {
            throw new RuntimeException("invalid http requestLine");
        }
        RequestLine requestLine = new RequestLine();
        requestLine.setMethod(lineArr[0]);
        requestLine.setRequestURI(lineArr[1]);
        requestLine.setProtocol(lineArr[2]);

        return requestLine;
    }

}
