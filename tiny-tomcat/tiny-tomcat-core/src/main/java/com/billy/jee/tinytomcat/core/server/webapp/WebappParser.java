package com.billy.jee.tinytomcat.core.server.webapp;

import com.billy.jee.tinytomcat.core.Webapp;

/**
 * @author liulei@bshf360.com
 * @since 2018-05-10 11:28
 */
public interface WebappParser {
    String NODE_ROOT = "web";
    String NODE_SERVLETS = "servlets";
    String NODE_SERVLET = "servlet";
    String NODE_MAIN_CLASS = "main-class";
    String NODE_URL_PATTERN = "url-pattern";

    Webapp parse(String webXmlPath);
}
