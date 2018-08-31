package com.billy.jee.tinytomcat.core.server.request;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author home.ubuntu
 * @since 2018-04-08 20:09
 */
public class RawResponse {

    private StringWriter stringWriter = new StringWriter();
    private PrintWriter writer = new PrintWriter(stringWriter);

    public PrintWriter getWriter() {
        return writer;
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

    public String getResponseString() {
        return stringWriter.toString();
    }
}
