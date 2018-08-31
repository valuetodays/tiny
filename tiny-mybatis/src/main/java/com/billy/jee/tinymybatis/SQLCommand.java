package com.billy.jee.tinymybatis;

import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;

import java.util.ArrayList;
import java.util.List;

/**
 * by vt.zd
 * on 2017-03-20 13:23
 */
public abstract class SQLCommand {
    private String id;

    public SQLCommand() {}

    public static SQLCommand of(MapperFile mapperFile, Configuration configuration, String id, List<?> contentList, String
            resultType) {
        int size = contentList.size();
        if (size == 1) {
            Object content = contentList.get(0);
            String textOfTextNode = getTextOfTextNode(content);
            return new TextSQLCommand(mapperFile, configuration, id, textOfTextNode, resultType);
        } else {
            List<SQLNode> sqlNodes = new ArrayList<>();
            dealWithNodes(mapperFile, sqlNodes, contentList);
            return new AdvancedSQLCommand(mapperFile, configuration, id, sqlNodes, resultType);
        }

        //return new SQLCommand(configuration, id, "", resultType);
    }

    private static void dealWithNodes(MapperFile mapperFile, List<SQLNode> nodeList, List<?> contentList) {
        for (int i = 0; i < contentList.size(); i++) {
            Object content = contentList.get(i);
            if (content instanceof org.dom4j.tree.DefaultText) {
                String textOfTextNode = getTextOfTextNode(content);
                nodeList.add(new PlaintextSQLNode(mapperFile, textOfTextNode));
            } else if (content instanceof org.dom4j.tree.DefaultElement) {
                DefaultElement elementContent = (DefaultElement) content;
                SQLNode sqlNode = dealWithNodes0(mapperFile, elementContent, elementContent.content());
                nodeList.add(sqlNode);
            }
        }
    }

    private static SQLNode dealWithNodes0(MapperFile mapperFile, DefaultElement elementContent, List<?> content) {
        // 得到指令名称了！
        String name = elementContent.getName();
        if ("if".equals(name)) {
            SQLNode sqlNodeIf = new IfSQLNode(mapperFile, elementContent.attributeValue("test"));
            List<SQLNode> sqlNodes = new ArrayList<>();
            dealWithNodes(mapperFile, sqlNodes, content);
            sqlNodeIf.setChildren(sqlNodes);
            return sqlNodeIf;
        } else if ("include".equals(name)) {
            String id = elementContent.attributeValue("refid");
            SQLNode sqlNodeInclude = new IncludeSQLNode(mapperFile, id);
            List<SQLNode> sqlNodes = new ArrayList<>();
            dealWithNodes(mapperFile, sqlNodes, content);
            sqlNodeInclude.setChildren(sqlNodes);
            return sqlNodeInclude;
        }
        return null;
    }

    private static String getTextOfTextNode(Object content) {
        if (content instanceof org.dom4j.tree.DefaultText) {
            return ((DefaultText) content).getText();
        } else {
            throw new RuntimeException("ex: node is incorrect.");
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract String getFinalSql(Object entity);

    public abstract List<PropertiesValue> getPreparedStatementParameter();

    public abstract Class<?> getResultTypeClass();

    private static class IfSQLNode extends SQLNode {
        private String test;

        public IfSQLNode(MapperFile mapperFile, String test) {
            super(mapperFile);
            this.test = test;
        }

        public String getTest() {
            return test;
        }

        @Override
        public String parseSql(Configuration configuration, Object obj) {
            if (configuration.containsAlias(obj.getClass())) {
                String s = "";
                Object o = null;
                try {
                    o = Ognl.getValue(test, obj, Boolean.class);
                } catch (OgnlException e) {
                    e.printStackTrace();
                    throw new RuntimeException("ex: error when parsing expression in <if> in mapper file XXXX.");
                }
                if (Boolean.TRUE.equals(Boolean.valueOf(o.toString()))) {
                    List<SQLNode> children = getChildren();
                    if (!CollectionUtils.isEmpty(children)) {
                        for (SQLNode sqlNode : children) {
                            s += sqlNode.parseSql(configuration, obj);
                        }
                    }
                }
                return s;
            } else {
                throw new RuntimeException("先不处理内置的类");
            }
        }
    }

    private static class PlaintextSQLNode extends SQLNode {
        private String plainText;

        public PlaintextSQLNode(MapperFile mapperFile, String textOfTextNode) {
            super(mapperFile);
            this.plainText = textOfTextNode;
        }

        @Override
        public String parseSql(Configuration configuration, Object obj) {
            return plainText;
        }
    }

    private static class IncludeSQLNode extends SQLNode {
        private String id;
        public IncludeSQLNode(MapperFile mapperFile, String id) {
            super(mapperFile);
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public String parseSql(Configuration configuration, Object obj) {
            String s = "";
            List<SQLSegment> sqlSegmentList = mapperFile.getSqlSegment();
            if (CollectionUtils.isNotEmpty(sqlSegmentList)) {
                for (SQLSegment sqlSegment : sqlSegmentList) {
                    if (id.equals(sqlSegment.getId())) {
                        SQLCommand content = sqlSegment.getContent();
                        s += content.getFinalSql(obj);
                    }
                }
            }
            return s;
        }
    }
}
