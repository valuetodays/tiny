package com.billy.jee.tinymybatis;

import org.apache.commons.beanutils.ConvertUtils;

/**
 *
 * @author liulei
 * @date 2017-03-30 17:04
 */
public class PropertiesValue {
    final String value;
    final Class<?> cls;

    public PropertiesValue(String value, Class<?> cls) {
        this.value = value;
        this.cls = cls;
    }
    public Object getValue() {
        return ConvertUtils.convert(value, cls);
    }
}
