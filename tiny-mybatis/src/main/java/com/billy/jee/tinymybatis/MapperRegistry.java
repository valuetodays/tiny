package com.billy.jee.tinymybatis;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MapperRegistry {
    private static Logger LOG = Logger.getLogger(MapperRegistry.class);
    private final Map<Class<?>, MethodProxyHandler<?>> knownMappers = new ConcurrentHashMap<>();
    private Configuration configuration;

    public MapperRegistry(Configuration configuration) {
        this.configuration = configuration;
    }


    public <T> T getMapper(Class<T> type) {
        return (T) knownMappers.get(type).getProxy();
    }

    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {  // avoid to put data more than 1 time
                return;
            }

            boolean loadCompleted = false;
            try {
                MapperFileRegistry mapperFileRegistry = configuration.getMapperFileRegistry();
                Map<Class<?>, MapperFile> mapperFiles = mapperFileRegistry.getMapperFiles();
                MapperFile mapperFile = mapperFiles.get(type);

                knownMappers.put(type, new MethodProxyHandler<T>(type, mapperFile, configuration));
                LOG.debug("type " + type.getName() + " was added as mapper . ");
                loadCompleted = true;
            } finally {
                if (!loadCompleted) {
                    knownMappers.remove(type);
                }
            }
        }
    }


}
