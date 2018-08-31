package com.billy.jee.tinymybatis;

import com.billy.jee.tinymybatis.parser.PropertyParser;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * by vt.zd
 * on 2017-03-20 13:23
 */
public class AdvancedSQLCommand extends SQLCommand {
    final Configuration configuration;
    final List<SQLNode> sqlNodes;
    private String resultType;
    private Class<?> resultTypeClass;
    private String finalSql;
    private List<PropertiesValue> preparedStatementParameter;
    private MapperFile mapperFile;

    /**
     *
     */
    public AdvancedSQLCommand(MapperFile mapperFile, Configuration configuration, String id, List<SQLNode> sqlNodes, String resultType) {
        super.setId(id);
        this.mapperFile = mapperFile;
        this.configuration = configuration;
        this.sqlNodes = sqlNodes;
        this.resultType = resultType;
        if (resultType != null) {
            resultTypeClass = configuration.getAlias(resultType);
        }
    }

    private void paddingFinalSql(Map<String, Object> paramMap) {
        String sql = getSqlWithParam(paramMap);
        String sqlTmp = new String(sql);
        while (sqlTmp.contains("${")) {
            sqlTmp = PropertyParser.parse$(sqlTmp, paramMap);
        }
        this.finalSql = sqlTmp;
    }

    // ====================================
    // 两部分可以合并处理 instanceof Map
    // 处理 <if>, etc
    private String getSqlWithParam(Map<String, Object> paramMap) {
        return null;
    }
    // 处理 <if>, etc
    private String getSqlWithEntity(Object entity) {
        StringBuilder sb = new StringBuilder();
        for (SQLNode sqlNode : sqlNodes) {
            String sqlTemp = sqlNode.parseSql(configuration, entity);
            sb.append(sqlTemp);
        }
        return sb.toString();
    }
    // ====================================


    public String getFinalSql(Map<String, Object> paramMap) {
        paddingFinalSql(paramMap);
        return finalSql;
    }

    @Override
    public String getFinalSql(Object entity) {
        paddingFinalSqlEntity(entity);
        return finalSql;
    }

    public List<PropertiesValue> getPreparedStatementParameter() {
        return preparedStatementParameter;
    }

    public String getResultType() {
        return resultType;
    }
    public Class<?> getResultTypeClass() {
        return resultTypeClass;
    }

    private void paddingFinalSqlEntity(Object entity) {
        List<PropertiesValue> paramList = new ArrayList<>();
        String sqlTmp = new String(getSqlWithEntity(entity));
        while (sqlTmp.contains("#{")) {
            int st = sqlTmp.indexOf("#{");
            int en = sqlTmp.indexOf("}", st);
            String propertyKey = sqlTmp.substring(st + "#{".length(), en);
            try {

                String propertyValue = BeanUtils.getProperty(entity, propertyKey);
                Class<?> propertyType = PropertyUtils.getPropertyType(entity, propertyKey);
//                System.out.println(propertyKey + "==" + propertyValue);
                PropertiesValue propertiesValue = new PropertiesValue(propertyValue, propertyType);
                paramList.add(propertiesValue);
                sqlTmp = sqlTmp.substring(0, st) + "?" + sqlTmp.substring(en+1);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        preparedStatementParameter = Collections.unmodifiableList(paramList);
        this.finalSql = sqlTmp;
    }

}
