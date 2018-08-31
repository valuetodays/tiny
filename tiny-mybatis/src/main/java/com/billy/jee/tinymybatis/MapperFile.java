package com.billy.jee.tinymybatis;

import java.util.List;

/**
 * by vt.zd
 * on 2017-03-20 11:33
 */
public class MapperFile {
    private Class<?> mapperInterface;
    private List<SQLCommand> sqlCommands;
    private List<SQLSegment> sqlSegment;


    public Class<?> getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperInterface(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public List<SQLCommand> getSqlCommands() {
        return sqlCommands;
    }

    public void setSqlCommands(List<SQLCommand> sqlCommands) {
        this.sqlCommands = sqlCommands;
    }

    public List<SQLSegment> getSqlSegment() {
        return sqlSegment;
    }

    public void setSqlSegment(List<SQLSegment> sqlSegment) {
        this.sqlSegment = sqlSegment;
    }
}
