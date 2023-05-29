package ru.aston.dao;

import lombok.SneakyThrows;

import java.sql.Connection;

public class UserRoleDao {

    private static UserRoleDao instance;

    private static final String SAVE_SQL = """
            INSERT INTO user_role (user_id, role_id) VALUES (?, ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE user_role
            SET user_id = ?, role_id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM user_role
            WHERE user_id = ?
            """;

    public static UserRoleDao getInstance() {
        if (instance == null) {
            instance = new UserRoleDao();
        }
        return instance;
    }

    @SneakyThrows
    public void save(Connection connection, Long userId, Integer roleId) {
        var statement = connection.prepareStatement(SAVE_SQL);
        statement.setLong(1, userId);
        statement.setLong(2, roleId);
        statement.executeUpdate();
    }

    @SneakyThrows
    public void update(Connection connection, Long userId, Integer roleId) {
        var statement = connection.prepareStatement(UPDATE_SQL);
        statement.setLong(1, userId);
        statement.setInt(2, roleId);
        statement.executeUpdate();
    }

    @SneakyThrows
    public void delete(Connection connection, Long userId) {
        var statement = connection.prepareStatement(DELETE_SQL);
        statement.setLong(1, userId);
        statement.executeUpdate();
    }

}