package com.billy.jee.tinytomcat.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liulei@bshf360.com
 * @since 2018-04-19 18:29
 */
public class Webapp {
    private List<Servlets> servlets = new ArrayList<>();


    public List<Servlets> getServlets() {
        return servlets;
    }
}
