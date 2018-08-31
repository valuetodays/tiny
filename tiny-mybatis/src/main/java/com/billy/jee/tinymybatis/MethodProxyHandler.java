package com.billy.jee.tinymybatis;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * by vt.zd
 * on 2017-03-20 10:59
 */
public class MethodProxyHandler<T> implements InvocationHandler {
    private static final Logger LOG = Logger.getLogger(MethodProxyHandler.class);
    private Class<T> mapperInterface;
    private MapperFile mapperFile;
    private T proxy;
    private Configuration configuration;

    public MethodProxyHandler(Class<T> type, MapperFile mapperFile, Configuration configuration) {
        mapperInterface = type;
        this.mapperFile = mapperFile;
        proxy = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{mapperInterface}, this);
        this.configuration = configuration;

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//            before();
            Object result = execute(method, args);
//            end();
            return result;
    }

    /**
     * 使用指定参数执行指定方法
     *
     * 注意： TODO method.getDeclaringClass();与mapperInterface不一致时
     *
     * @param method 待执行的方法
     * @param args 参数
     * @return
     */
    private Object execute(Method method, Object[] args) throws SQLException, IllegalAccessException, InvocationTargetException, InstantiationException {
//        String namespaceName = method.getDeclaringClass().getName() + "." + method.getName();
//        System.out.println(namespaceName);

        Parameter[] parameters = method.getParameters();
        if (parameters != null || parameters.length == 0 || parameters.length == 1) {
            if (parameters.length == 0) {
               return execute0(method.getName(), null);
            } else if (parameters.length == 1) {
                Map<String, Object> paramMap = new HashMap<>();
                Parameter parameter = parameters[0];
                Class<?> parameterType = parameter.getType();
                if (isAlias(parameterType)) {
                    return executeByEntity(method.getName(), args[0], parameterType);
                } else {
                    final Object o = parameterType.cast(args[0]);
                    Param annotation = parameter.getAnnotation(Param.class);
                    String v = annotation.value();
                    paramMap.put(v, o);
                    return execute0(method.getName(), paramMap);
                }
            }

        } else {
            return "execute...parameter should be 0 or 1.";
        }

        return "execute...default";
    }

    private Object execute0(String name, Object o) throws InvocationTargetException, SQLException, InstantiationException, IllegalAccessException {
        return executeByEntity(name, o, null);
    }


    private boolean isAlias(Class<?> parameterType) {
        return configuration.containsAlias(parameterType);
    }

    private Object executeByEntity(String methodName, Object entity, Class<?> parameterType)
            throws SQLException, IllegalAccessException, InstantiationException, InvocationTargetException {
        SQLCommand sqlCommandExecute = null;
        List<SQLCommand> sqlCommands = mapperFile.getSqlCommands();
        for (final SQLCommand sqlCommand : sqlCommands) {
            if (methodName.equals(sqlCommand.getId())) {
               sqlCommandExecute = sqlCommand;
               break;
            }
        }

        String finalSql = sqlCommandExecute.getFinalSql(entity);
        LOG.debug("sql: " + finalSql);
        List<PropertiesValue> preparedStatementParameters = sqlCommandExecute.getPreparedStatementParameter();

        Connection connection = DBPool.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(finalSql);
        for (int i = 0; i < preparedStatementParameters.size(); i++) {
            preparedStatement.setObject((i+1), preparedStatementParameters.get(i).getValue());
        }
        List<Object> listResult = new ArrayList<>();
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        Class<?> resultTypeClass = sqlCommandExecute.getResultTypeClass();
        if (resultTypeClass == null) {
            throw new InstantiationException("resultTypeClass is null in mapper of " + mapperInterface.getName() + "" +
                    "." + methodName);
        }
        while (resultSet.next()) {
            Object o = resultTypeClass.newInstance();
            for (int i=1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object object = resultSet.getObject(i);
//                System.out.println("\t" + columnName +  " ==> " + object);
                BeanUtils.setProperty(o, columnName, object);
            }
            listResult.add(o);
        }
        // TODO close ResultSet?

        return listResult;
    }



    public T getProxy() {
        return (T) proxy;
    }

}
