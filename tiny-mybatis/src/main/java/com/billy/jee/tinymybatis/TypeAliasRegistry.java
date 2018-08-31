package com.billy.jee.tinymybatis;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO(这里用一句话描述这个类的作用)
 *
 * @author liulei
 * @date 2017-04-06 12:57
 */
public class TypeAliasRegistry {
    private static Logger LOG = Logger.getLogger(TypeAliasRegistry.class);
    final private Configuration configuration;
    private Map<String, Class<?>> knownAlias = new ConcurrentHashMap<String, Class<?>>();

    public TypeAliasRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }


    public boolean addAlias(Class<?> clazz) {
        if (clazz != null && !clazz.isAnonymousClass() && !clazz.isInterface() && !clazz.isMemberClass()) {
            String simpleName = clazz.getSimpleName();
            String s = StringExUtil.lowerFirst(simpleName);
            knownAlias.put(s, clazz);
            LOG.debug("alias for " + s + "/" + clazz.getName() + " was registered successfully. ");

            return true;
        }
        LOG.debug("alias for " + clazz.getName() + " is not registered, because class is an anonymous class, or " +
                " an interface, or a member class, or null.");

        return false;
    }

    public boolean addAlias(String clazzFullName) {
        try {
            Class<?> aClass = Class.forName(clazzFullName);
            if (aClass != null) {
                return addAlias(aClass);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addAliasPackage(String pkg) {
        String pkgPath = pkg.replace(".", "/");
        String path = Thread.currentThread().getContextClassLoader().getResource(pkgPath).getPath();
        System.out.println("path-->" + path);
        parseClassFile(pkg, path);

        return true;
    }

    private void parseClassFile(String pkg, String path) {
        File files = new File(path);
        File[] subFiles = files.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                if (dir.isDirectory()) {
                    return true;
                }
                if (name.endsWith(".class")) {
                    return true;
                }
                return false;
            }
        });

        for (int i = 0; i < subFiles.length; i++) {
            File subFile = subFiles[i];
            if (subFile.isDirectory()) {
                parseClassFile(pkg + "."+subFile.getName(), subFile.getPath());
            } else {
                addAlias(pkg + "." + subFile.getName().substring(0, (subFile.getName().length()-".class".length())));
            }
        }
    }


    public boolean contains(Class<?> clazz) {
        return knownAlias.containsValue(clazz);
    }

    public boolean contains(String alias) {
        return knownAlias.containsKey(alias);
    }
    public Class<?> getAlias(Class<?> clazz) {
        return knownAlias.get(clazz);
    }

    public Class<?> getAlias(String alias) {
        return knownAlias.get(alias);
    }


}
