package com.billy.jee.tinytomcat.core.server.webapp;

import com.billy.jee.tinytomcat.core.Servlets;
import com.billy.jee.tinytomcat.core.Webapp;
import com.billy.jee.tinytomcat.core.util.AssertUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.List;

/**
 * @author liulei@bshf360.com
 * @since 2018-05-10 11:35
 */
public class DefaultWebappParser implements WebappParser {

    @Override
    public Webapp parse(String webXmlPath) {
        Webapp webapp = new Webapp();
        File webXmlFile = new File(webXmlPath);
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(webXmlFile);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        AssertUtil.checkNull(doc);
        Element root = doc.getRootElement();
        AssertUtil.checkNull(root);
        AssertUtil.checkEqual(root.getName(), NODE_ROOT);
        List servlets = root.elements(NODE_SERVLETS);
        AssertUtil.checkNull(servlets);
        AssertUtil.checkEqual(1, servlets.size());

        DefaultElement servletsEle = (DefaultElement) servlets.get(0);
        List servletListEle = servletsEle.elements(NODE_SERVLET);
        Servlets servletMap = new Servlets();
        for (Object servletObject : servletListEle) {
            DefaultElement servletEle = (DefaultElement) servletObject;
            String mainClass = servletEle.element(NODE_MAIN_CLASS).getStringValue();
            String urlPattern = servletEle.element(NODE_URL_PATTERN).getStringValue();
            if (urlPattern.charAt(0) != '/') {
                urlPattern = '/' + urlPattern;
            }
            Class<HttpServlet> httpServletClass;
            try {
                httpServletClass = (Class<HttpServlet>) Class.forName(mainClass);
                HttpServlet servlet = httpServletClass.newInstance();
                servlet.init();
                servletMap.getServletMap().put(urlPattern, servlet);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("unknown HttpServlet for " + mainClass);
            }
        }

        webapp.getServlets().add(servletMap);

        return webapp;
    }

}
