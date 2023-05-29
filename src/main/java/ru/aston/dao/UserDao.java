package ru.aston.dao;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.aston.model.Role;
import ru.aston.model.User;
import ru.aston.util.UserMapper;
import ru.aston.util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class UserDao implements Dao<User, Long> {

    private static UserDao instance;
    private static RoleDao roleDao;
    private static UserRoleDao userRoleDao;
    private static UserMapper userMapper;

    private static final String SELECT_ALL_SQL = """
            SELECT u.id, u.email, u.password, r.id, r.name
            FROM users AS u
            JOIN user_role AS ur ON u.id = ur.user_id
            JOIN role AS r ON ur.role_id = r.id
            """;

    private static final String SELECT_BY_ID_SQL = SELECT_ALL_SQL + " WHERE u.id = ?";

    private static final String SAVE_SQL = """
            INSERT INTO users (email, password)
            VALUES (?, ?);
            """;

    private static final String UPDATE_SQL = """
            UPDATE users
            SET email = ?, password = ?
            WHERE id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM users
            WHERE id = ?
            """;

    public static UserDao getInstance() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    @SneakyThrows(SQLException.class)
    @Override
    public List<User> findAll() {
        List<User> result = new ArrayList<>();
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(SELECT_ALL_SQL)) {
            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                var user = userMapper.map(resultSet);
                result.add(user);
            }
        }
        return result;
    }

    @SneakyThrows(SQLException.class)
    @Override
    public Optional<User> findById(Long id) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setLong(1, id);

            var resultSet = statement.executeQuery();

            User user = null;
            if (resultSet.next()) {
                user = userMapper.map(resultSet);
            }
            return Optional.ofNullable(user);
        }
    }

    @SneakyThrows(SQLException.class)
    @Override
    public User save(User user) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPassword());

            statement.executeUpdate();

            int roleId = roleDao.findByRoleName(connection, user.getRole().getName())
                    .map(Role::getId)
                    .orElseGet(() -> roleDao.save(connection, user.getRole()).getId());

            var generatedId = statement.getGeneratedKeys();
            if (generatedId.next()) {
                var id = generatedId.getLong("id");
                user.setId(id);
            }

            userRoleDao.save(connection, user.getId(), roleId);

            connection.commit();
        }
        return user;
    }

    @SneakyThrows(SQLException.class)
    @Override
    public void update(User updatedUser) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            connection.setAutoCommit(false);

            statement.setString(1, updatedUser.getEmail());
            statement.setString(2, updatedUser.getPassword());
            statement.setLong(3, updatedUser.getId());

            statement.executeUpdate();

            checkRoleDuplicate(updatedUser, connection);

            connection.commit();
        }
    }

    private void checkRoleDuplicate(User updatedUser, Connection connection) {
        var mayBeRole = roleDao.findByRoleName(connection, updatedUser.getRole().getName());
        if (mayBeRole.isPresent()) {
            userRoleDao.update(connection, updatedUser.getId(), mayBeRole.get().getId());
        } else {
            userRoleDao.delete(connection, updatedUser.getId());
            var savedRole = roleDao.save(connection, updatedUser.getRole());
            userRoleDao.save(connection, updatedUser.getId(), savedRole.getId());
        }
    }

    @SneakyThrows(SQLException.class)
    @Override
    public boolean delete(Long id) {
        int result;
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(DELETE_SQL)) {
            connection.setAutoCommit(false);

            statement.setLong(1, id);
            result = statement.executeUpdate();

            connection.commit();
        }
        return result > 0;
    }

    private static void init() {
        instance = new UserDao();
        roleDao = RoleDao.getInstance();
        userRoleDao = UserRoleDao.getInstance();
        userMapper = UserMapper.getInstance();
    }

}
