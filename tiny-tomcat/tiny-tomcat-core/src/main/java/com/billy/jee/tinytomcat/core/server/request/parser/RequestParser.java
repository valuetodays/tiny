package com.billy.jee.tinytomcat.core.server.request.parser;

import com.billy.jee.tinytomcat.core.server.request.RawRequest;

/**
 *
 * @author liulei@bshf360.com
 * @since 2018-05-10 10:41
 */
public interface RequestParser {
    /**
     * parse the request string into a {@link RawRequest} object
     * @param requestString request string
     * @return the parsed RawRequest
     */
    RawRequest parse(String requestString);
}
