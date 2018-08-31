package com.billy.jee.tinymybatis;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author liulei
 * @date 2017-03-30 17:24
 */
public class DBPool {
    private static DBPool dbPool = new DBPool();
    private ComboPooledDataSource dataSource;

    private DBPool() {
        try {
            dataSource = new ComboPooledDataSource();
            dataSource.setUser("root");
            dataSource.setPassword("root");
            dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3308/tinymabatis?useUnicode=true" +
                    "&characterEncoding=utf-8");
            dataSource.setDriverClass("com.mysql.jdbc.Driver");
            dataSource.setInitialPoolSize(2);
            dataSource.setMinPoolSize(1);
            dataSource.setMaxPoolSize(10);
            dataSource.setMaxStatements(50);
            dataSource.setMaxIdleTime(60);
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    public final static DBPool getInstance() {
        return dbPool;
    }

    public final Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("无法从数据源获取连接 ", e);
        }
    }

    public static void main(String[] args) throws SQLException {
        Connection con = null;
        try {
            con = DBPool.getInstance().getConnection();
        } catch (Exception e) {
        } finally {
            if (con != null)
                con.close();
        }
    }
}
