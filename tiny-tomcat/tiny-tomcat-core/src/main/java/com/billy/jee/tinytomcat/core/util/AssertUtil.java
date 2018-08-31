package com.billy.jee.tinytomcat.core.util;

import java.util.Objects;

/**
 * @author liulei@bshf360.com
 * @since 2018-04-20 12:57
 */
public class AssertUtil {
    private AssertUtil() {}

    public static void checkNull(Object o) {
        if (o == null) {
            throw new NullPointerException("parameter is null");
        }
    }

    public static void checkEqual(Object o1, Object o2) {
        if (!Objects.equals(o1, o2)) {
            throw new RuntimeException("not equal");
        }

    }
}
