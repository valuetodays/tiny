package billy.tinyant;

import java.lang.reflect.InvocationTargetException;

/**
 * individued from org.apache.tools.ant.IntrospectionHelper
 * @author Administrator
 *
 */
public interface AttributeSetter {
    void set(Project p, Object parent, String value)
        throws InvocationTargetException, IllegalAccessException, 
               BuildException;
}