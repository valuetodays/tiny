package com.billy.jee.tinymybatis;

/**
 * @author liulei@bshf360.com
 * @since 2017-11-10 15:05
 */
public class SQLSegment {
    private String id;
    private SQLCommand content;

    public SQLSegment(String id, SQLCommand sqlCommand) {
        setId(id);
        setContent(sqlCommand);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SQLCommand getContent() {
        return content;
    }

    public void setContent(SQLCommand content) {
        this.content = content;
    }
}
