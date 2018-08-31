package com.billy.jee.tinymybatis;

import java.util.List;

/**
 * @author liulei@bshf360.com
 * @since 2017-11-07 18:37
 */
public abstract class SQLNode {
    protected MapperFile mapperFile;
    protected List<SQLNode> children;

    public SQLNode(MapperFile mapperFile) {
        this.mapperFile = mapperFile;
    }

    public List<SQLNode> getChildren() {
        return children;
    }

    public void setChildren(List<SQLNode> children) {
        this.children = children;
    }

    public MapperFile getMapperFile() {
        return mapperFile;
    }

    public void setMapperFile(MapperFile mapperFile) {
        this.mapperFile = mapperFile;
    }

    public abstract String parseSql(Configuration configuration, Object obj);
}
