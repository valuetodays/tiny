/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package billy.tinyant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Helper class that collects the methods a task or nested element
 * holds to set attributes, create nested elements or hold PCDATA
 * elements.
 *
 * @author Stefan Bodewig <a href="mailto:stefan.bodewig@megabit.net">stefan.bodewig@megabit.net</a> 
 */
public class IntrospectionHelper  {

    private static final Logger LOG = LoggerFactory.getLogger(IntrospectionHelper.class);
    
    /**
     * holds the types of the attributes that could be set.
     */
    private Hashtable attributeTypes;

    /**
     * holds the attribute setter methods.
     */
    private Hashtable attributeSetters;

    /**
     * Holds the types of nested elements that could be created.
     */
    private Hashtable nestedTypes;

    /**
     * Holds methods to create nested elements.
     */
    private Hashtable nestedCreators;

    /**
     * The method to add PCDATA stuff.
     */
    private Method addText = null;

    /**
     * The Class that's been introspected.
     */
    private Class bean;

    /**
     * instances we've already created
     */
    private static Hashtable helpers = new Hashtable();

    private IntrospectionHelper(final Class bean) {
        attributeTypes = new Hashtable();
        attributeSetters = new Hashtable();
        nestedTypes = new Hashtable();
        nestedCreators = new Hashtable();
        this.bean = bean;

        Method[] methods = bean.getMethods();
        for (int i=0; i<methods.length; i++) {
            final Method m = methods[i];
            final String name = m.getName();
            Class returnType = m.getReturnType();
            Class[] args = m.getParameterTypes();

            // not really user settable properties on tasks
            if (Task.class.isAssignableFrom(bean)
                && args.length == 1 &&
                (
                 (
                  "setLocation".equals(name) && Location.class.equals(args[0])
                  ) || ( 
                   "setDescription".equals(name) && String.class.equals(args[0])
                  ) || (
                   "setTaskType".equals(name) && String.class.equals(args[0])
                  )
                 )) {
                continue;
            }
            
            if ("addText".equals(name)
                && Void.TYPE.equals(returnType)
                && args.length == 1
                && String.class.equals(args[0])) {

                addText = methods[i];

            } else if (name.startsWith("set")
                       && Void.TYPE.equals(returnType)
                       && args.length == 1
                       && !args[0].isArray()) {

                String propName = getPropertyName(name, "set");
                AttributeSetter as = createAttributeSetter(m, args[0]);
                if (as != null) {
                    attributeTypes.put(propName, args[0]);
                    attributeSetters.put(propName, as);
                }

            } else if (name.startsWith("create")
                       && !returnType.isArray()
                       && !returnType.isPrimitive()
                       && args.length == 0) {

                String propName = getPropertyName(name, "create");
                nestedTypes.put(propName, returnType);
                NestedCreator nestedCreator = new NestedCreator() {

                    public Object create(Object parent) 
                        throws InvocationTargetException, 
                        IllegalAccessException {

                        return m.invoke(parent, new Object[] {});
                    }

                };
                nestedCreators.put(propName, nestedCreator);
                
            } else if (name.startsWith("add")
                       && Void.TYPE.equals(returnType)
                       && args.length == 1
                       && !args[0].isArray()
                       && !args[0].isPrimitive()) {
                 
                try {
                    final Constructor c = 
                        args[0].getConstructor(new Class[] {});
                    String propName = getPropertyName(name, "add");
                    nestedTypes.put(propName, args[0]);
                    nestedCreators.put(propName, new NestedCreator() {

                            public Object create(Object parent) 
                                throws InvocationTargetException, IllegalAccessException, InstantiationException {
                                
                                Object o = c.newInstance(new Object[] {});
                                m.invoke(parent, new Object[] {o});
                                return o;
                            }

                        });
                } catch (NoSuchMethodException nse) {
                }
                    
            }
        }
    }
    
    /**
     * Factory method for helper objects.
     */
    public synchronized static IntrospectionHelper getHelper(Class c) {
        IntrospectionHelper ih = (IntrospectionHelper) helpers.get(c);
        if (ih == null) {
            ih = new IntrospectionHelper(c);
            helpers.put(c, ih);
        }
        return ih;
    }

    /**
     * Sets the named attribute.
     */
    public void setAttribute(Project p, Object element, String attributeName, 
                             String value)
        throws BuildException {
        Set entrySet = attributeSetters.entrySet();
        Iterator iterator = entrySet.iterator();
        while(iterator.hasNext()) {
            Object next = iterator.next();
        }
        Object object = attributeSetters.get(attributeName);
        AttributeSetter as = (AttributeSetter) object;
        if (as == null) {
            String msg = "Class " + element.getClass() +
                " doesn't support the \"" + attributeName + "\" attribute";
            LOG.info(msg);
            throw new BuildException(msg);
        }
        try {
            LOG.info("invoke[setXXX] start");
            as.set(p, element, value);
            LOG.info("invoke[setXXX] end");
        } catch (IllegalAccessException ie) {
            // impossible as getMethods should only return public methods
            throw new BuildException(ie);
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (t instanceof BuildException) {
                throw (BuildException) t;
            }
            throw new BuildException(t);
        }
    }

    /**
     * Adds PCDATA areas.
     */
    public void addText(Object element, String text) {
        if (addText == null) {
            String msg = "Class " + element.getClass() +
                " doesn't support nested text elements";
            throw new BuildException(msg);
        }
        try {
            addText.invoke(element, new String[] {text});
        } catch (IllegalAccessException ie) {
            // impossible as getMethods should only return public methods
            throw new BuildException(ie);
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (t instanceof BuildException) {
                throw (BuildException) t;
            }
            throw new BuildException(t);
        }
    }

    /**
     * Creates a named nested element.
     */
    public Object createElement(Object element, String elementName) 
        throws BuildException {
        NestedCreator nc = (NestedCreator) nestedCreators.get(elementName);
        if (nc == null) {
            String msg = "Class " + element.getClass() +
                " doesn't support the nested \"" + elementName + "\" element";
            throw new BuildException(msg);
        }
        try {
            return nc.create(element);
        } catch (IllegalAccessException ie) {
            // impossible as getMethods should only return public methods
            throw new BuildException(ie);
        } catch (InstantiationException ine) {
            // impossible as getMethods should only return public methods
            throw new BuildException(ine);
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (t instanceof BuildException) {
                throw (BuildException) t;
            }
            throw new BuildException(t);
        }
    }

    /**
     * returns the type of a named nested element.
     */
    public Class getElementType(String elementName) 
        throws BuildException {
        Class nt = (Class) nestedTypes.get(elementName);
        if (nt == null) {
            String msg = "Class " + bean +
                " doesn't support the nested \"" + elementName + "\" element";
            throw new BuildException(msg);
        }
        return nt;
    }

    /**
     * returns the type of a named attribute.
     */
    public Class getAttributeType(String attributeName) 
        throws BuildException {
        Class at = (Class) attributeTypes.get(attributeName);
        if (at == null) {
            String msg = "Class " + bean +
                " doesn't support the \"" + attributeName + "\" attribute";
            throw new BuildException(msg);
        }
        return at;
    }

    /**
     * Does the introspected class support PCDATA?
     */
    public boolean supportsCharacters() {
        return addText != null;
    }

    /**
     * Return all attribues supported by the introspected class.
     */
    public Enumeration getAttributes() {
        return attributeSetters.keys();
    }

    /**
     * Return all nested elements supported by the introspected class.
     */
    public Enumeration getNestedElements() {
        return nestedTypes.keys();
    }

    /**
     * Create a proper implementation of AttributeSetter for the given
     * attribute type.  
     */
    private AttributeSetter createAttributeSetter(final Method m,
                                                  final Class arg) {
//        StackPrintUtil.p("createAttributeSetter");
        // simplest case - setAttribute expects String
        if (String.class.equals(arg)) {
            return new AttributeSetter() {
                    public void set(Project p, Object parent, String value) 
                        throws InvocationTargetException, IllegalAccessException {
                        m.invoke(parent, new String[] {value});
                    }
                };

        // now for the primitive types, use their wrappers
        } else if (Character.class.equals(arg)
                   || Character.TYPE.equals(arg)) {
            return new AttributeSetter() {
                    public void set(Project p, Object parent, String value) 
                        throws InvocationTargetException, IllegalAccessException {
                        m.invoke(parent, new Character[] {new Character(value.charAt(0))});
                    }

                };
        } else if (Byte.TYPE.equals(arg)) {
            return new AttributeSetter() {
                    public void set(Project p, Object parent, String value) 
                        throws InvocationTargetException, IllegalAccessException {
                        m.invoke(parent, new Byte[] {new Byte(value)});
                    }

                };
        } else if (Short.TYPE.equals(arg)) {
            return new AttributeSetter() {
                    public void set(Project p, Object parent, String value) 
                        throws InvocationTargetException, IllegalAccessException {
                        m.invoke(parent, new Short[] {new Short(value)});
                    }

                };
        } else if (Integer.TYPE.equals(arg)) {
            return new AttributeSetter() {
                    public void set(Project p, Object parent, String value) 
                        throws InvocationTargetException, IllegalAccessException {
                        m.invoke(parent, new Integer[] {new Integer(value)});
                    }

                };
        } else if (Long.TYPE.equals(arg)) {
            return new AttributeSetter() {
                    public void set(Project p, Object parent, String value) 
                        throws InvocationTargetException, IllegalAccessException {
                        m.invoke(parent, new Long[] {new Long(value)});
                    }

                };
        } else if (Float.TYPE.equals(arg)) {
            return new AttributeSetter() {
                    public void set(Project p, Object parent, String value) 
                        throws InvocationTargetException, IllegalAccessException {
                        m.invoke(parent, new Float[] {new Float(value)});
                    }

                };
        } else if (Double.TYPE.equals(arg)) {
            return new AttributeSetter() {
                    public void set(Project p, Object parent, String value) 
                        throws InvocationTargetException, IllegalAccessException {
                        m.invoke(parent, new Double[] {new Double(value)});
                    }

                };

        // boolean gets an extra treatment, because we have a nice method 
        // in Project
        } else if (Boolean.class.equals(arg)
                   || Boolean.TYPE.equals(arg)) {
            return new AttributeSetter() {
                    public void set(Project p, Object parent, String value) 
                        throws InvocationTargetException, IllegalAccessException {
                        m.invoke(parent, 
                                 new Boolean[] {new Boolean(Project.toBoolean(value))});
                    }

                };

        // Class doesn't have a String constructor but a decent factory method
        } else if (Class.class.equals(arg)) {
            return new AttributeSetter() {
                    public void set(Project p, Object parent, String value) 
                        throws InvocationTargetException, IllegalAccessException, BuildException {
                        try {
                            m.invoke(parent, new Class[] {Class.forName(value)});
                        } catch (ClassNotFoundException ce) {
                            throw new BuildException(ce);
                        }
                    }
                };

        // resolve relative paths through Project
        } else if (File.class.equals(arg)) {
            return new AttributeSetter() {
                    public void set(Project p, Object parent, String value) 
                        throws InvocationTargetException, IllegalAccessException {
                        m.invoke(parent, new File[] {p.resolveFile(value)});
                    }

                };

        // EnumeratedAttributes have their own helper class
        } else if (EnumeratedAttribute.class.isAssignableFrom(arg)) {
            return new AttributeSetter() {
                    public void set(Project p, Object parent, String value) 
                        throws InvocationTargetException, IllegalAccessException, BuildException {
                        try {
                            EnumeratedAttribute ea = 
                                (EnumeratedAttribute)arg.newInstance();
                            ea.setValue(value);
                            m.invoke(parent, new EnumeratedAttribute[] {ea});
                        } catch (InstantiationException ie) {
                            throw new BuildException(ie);
                        }
                    }
                };
        

        // worst case. look for a public String constructor and use it
        } else {

            try {
                final Constructor c = 
                    arg.getConstructor(new Class[] {String.class});

                return new AttributeSetter() {
                        public void set(Project p, Object parent, 
                                        String value) 
                            throws InvocationTargetException, IllegalAccessException, BuildException {
                            try {
                                m.invoke(parent, new Object[] {c.newInstance(new String[] {value})});
                            } catch (InstantiationException ie) {
                                throw new BuildException(ie);
                            }
                        }
                    };
                
            } catch (NoSuchMethodException nme) {
            }
        }
        
        return null;
    }

    /**
     * extract the name of a property from a method name - subtracting
     * a given prefix.  
     */
    private String getPropertyName(String methodName, String prefix) {
        int start = prefix.length();
        return methodName.substring(start).toLowerCase();
    }



}
