package com.billy.jee.tinytomcat.core.server.webapp;

import com.billy.jee.tinytomcat.core.prop.SystemProp;

/**
 * @author liulei@bshf360.com
 * @since 2018-05-10 11:31
 */
public class FileSystemWebappLoader implements WebappLoader{
    @Override
    public String load(String location) {
        String webxmlPath = location + SystemProp.getFileSeparator() +
                "WEB-INF" + SystemProp.getFileSeparator() +
                "web.xml";
        return webxmlPath;
    }
}
