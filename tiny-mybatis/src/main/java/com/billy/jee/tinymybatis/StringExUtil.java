package com.billy.jee.tinymybatis;

/**
 * TODO(这里用一句话描述这个类的作用)
 *
 * @author liulei
 * @date 2017-04-06 13:02
 */
public class StringExUtil {
    public static String lowerFirst(String text) {
        if (text == null) {
            return null;
        }

        if (text.length()>0) {
            String first = text.substring(0, 1).toLowerCase();
            String then = text.substring(1);
            return first + then;
        } else {
            return text;
        }
    }
}
