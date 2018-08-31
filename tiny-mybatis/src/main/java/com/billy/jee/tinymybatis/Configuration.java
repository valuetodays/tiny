package com.billy.jee.tinymybatis;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Configuration {
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);
    protected MapperFileRegistry mapperFileRegistry = new MapperFileRegistry(this);
    protected TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry(this);


    public void addMapper(Class<?> type) {
        mapperRegistry.addMapper(type);
    }


    /**
     * get mapper of specified slass
     * @param type
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<T> type) {
        return mapperRegistry.getMapper(type);
    }

    /**
     * add a mapper file (*Mapper.xml)
     * @param mapperFile
     * @throws Exception
     */
    public void addMapperFile(String mapperFile) throws Exception {
        mapperFileRegistry.addMapperFile(mapperFile);
        Set<Class<?>> mapperClasses = mapperFileRegistry.getMapperClasses();
        for (Class<?> mapperClass : mapperClasses) {
            addMapper(mapperClass);
        }
    }


    public boolean addMapperDirectory(String mapperPath) throws Exception {
        mapperFileRegistry.addMapperDirectory(mapperPath);
        Set<Class<?>> mapperClasses = mapperFileRegistry.getMapperClasses();
        for (Class<?> mapperClass : mapperClasses) {
            addMapper(mapperClass);
        }

        return true;
    }

    public MapperFileRegistry getMapperFileRegistry() {
        return mapperFileRegistry;
    }

    public boolean addEntity(Class<?> clazz) {
        return addAlias(clazz);
    }
    public boolean addAlias(Class<?> clazz) {
        return typeAliasRegistry.addAlias(clazz);
    }
    public boolean addAliasPackage(String pkg) {
        return typeAliasRegistry.addAliasPackage(pkg);
    }

    public boolean containsAlias(Class<?> clazz) {
        return typeAliasRegistry.contains(clazz);
    }
    public boolean containsAlias(String alias) {
        return typeAliasRegistry.contains(alias);
    }
    public Class<?> getAlias(String alias) {
        return typeAliasRegistry.getAlias(alias);
    }
    public Class<?> getAlias(Class<?> alias) {
        return typeAliasRegistry.getAlias(alias);
    }


}
