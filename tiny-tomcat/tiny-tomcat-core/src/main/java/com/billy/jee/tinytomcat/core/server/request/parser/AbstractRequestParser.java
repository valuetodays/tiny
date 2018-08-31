package com.billy.jee.tinytomcat.core.server.request.parser;

import org.apache.log4j.Logger;

/**
 * the abstract RequestParser for the sake of using `LOG`, or anything else...
 *
 * @author liulei@bshf360.com
 * @since 2018-05-10 11:02
 */
public abstract class AbstractRequestParser implements RequestParser {
    protected final Logger LOG = Logger.getLogger(getClass());
}
