package com.billy.jee.tinytomcat.core.server.processor;

import com.billy.jee.tinytomcat.core.server.request.RawRequest;
import com.billy.jee.tinytomcat.core.server.request.RawResponse;

/**
 * @author liulei@bshf360.com
 * @since 2018-04-20 16:25
 */
public interface ServletProcessor {
    void process(RawRequest request, RawResponse response);
}
