package com.billy.jee.tinytomcat.core.prop;

import java.util.Properties;

/**
 * @author liulei@bshf360.com
 * @since 2018-04-20 11:20
 */
public final class SystemProp {
    private SystemProp() {}

    /**
     * get file.separator
     * @return file.separator
     */
    public static String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    public static void main(String[] args) {
        Properties properties = System.getProperties();
        properties.list(System.out);
    }
}
