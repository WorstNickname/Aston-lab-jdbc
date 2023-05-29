package ru.aston.dao;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.aston.model.Role;

import java.sql.Connection;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class RoleDao {

    private static RoleDao instance;

    private static final String SELECT_ALL_SQL = """
            SELECT id, name
            FROM role
            """;

    private static final String SELECT_BY_NAME_SQL = SELECT_ALL_SQL + " WHERE name = ?";

    private static final String SAVE_SQL = """
            INSERT INTO role (name)
            VALUES (?);
            """;

    private static final String UPDATE_SQL = """
            UPDATE role
            SET name = ?
            WHERE id = ?
            """;

    public static RoleDao getInstance() {
        if (instance == null) {
            instance = new RoleDao();
        }
        return instance;
    }

    @SneakyThrows
    public Optional<Role> findByRoleName(Connection connection, String roleName) {

        var statement = connection.prepareStatement(SELECT_BY_NAME_SQL);
        statement.setString(1, roleName);

        var resultSet = statement.executeQuery();

        Role role = null;
        if (resultSet.next()) {
            var id = resultSet.getInt("id");
            var name = resultSet.getString("name");
            role = Role.builder()
                    .id(id)
                    .name(name)
                    .build();
        }
        return Optional.ofNullable(role);
    }

    @SneakyThrows
    public Role save(Connection connection, Role role) {
        var statement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS);
        statement.setString(1, role.getName());

        statement.executeUpdate();

        var generatedId = statement.getGeneratedKeys();
        if (generatedId.next()) {
            role.setId(generatedId.getInt("id"));
        }
        return role;
    }

    @SneakyThrows
    public void update(Connection connection, Role updatedRole) {
        var statement = connection.prepareStatement(UPDATE_SQL);
        statement.setString(1, updatedRole.getName());
        statement.setInt(2, updatedRole.getId());
        statement.executeUpdate();
    }

}
