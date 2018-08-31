package billy.tinyant;

import java.lang.reflect.InvocationTargetException;

/**
 * individued from org.apache.tools.ant.IntrospectionHelper
 * @author Administrator
 *
 */
public interface NestedCreator {
    Object create(Object parent)
        throws InvocationTargetException, IllegalAccessException, InstantiationException;
}