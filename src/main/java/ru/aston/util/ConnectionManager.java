package ru.aston.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@UtilityClass
public class ConnectionManager {

    private static final String DB_URL = "db.url";
    private static final String DB_USERNAME = "db.username";
    private static final String DB_PASSWORD = "db.password";

    @SneakyThrows(SQLException.class)
    public static Connection open() {
        return DriverManager.getConnection(
                PropertiesUtil.getProperty(DB_URL),
                PropertiesUtil.getProperty(DB_USERNAME),
                PropertiesUtil.getProperty(DB_PASSWORD)
        );
    }

}
