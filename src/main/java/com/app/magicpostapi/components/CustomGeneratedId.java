package com.app.magicpostapi.components;

import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class CustomGeneratedId implements IdentifierGenerator {
    private String prefix = ""; // Một giá trị mặc định cho prefix.
    private String tableName = "";

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        // Kiểm tra nếu có tham số "prefix" trong @Parameter thì lấy giá trị.
        if (params != null) {
            String prefixParam = params.getProperty("prefix");
            String tableParam = params.getProperty("tableName");
            if (prefixParam != null) {
                prefix = prefixParam;
            }
            if (tableName != null) {
                tableName = tableParam;
            }
        }
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Connection connection = null;
        try {
            ConnectionProvider connectionProvider = session.getFactory().getServiceRegistry().getService(ConnectionProvider.class);
            connection = connectionProvider.getConnection();

            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SELECT MAX(next_val) as maxId FROM " + tableName);
            if (rs.next()) {
                int maxId = rs.getInt("maxId");
                statement.executeUpdate("SET SQL_SAFE_UPDATES = 0");
                statement.executeUpdate("update " + tableName + " SET next_val = next_val + 1");
                return prefix + maxId;
            }
        } catch (Exception e) {
            // Xử lý ngoại lệ theo ý muốn của bạn.
            System.out.println(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    // Xử lý ngoại lệ khi đóng kết nối.
                }
            }
        }
        return null;
    }
}
