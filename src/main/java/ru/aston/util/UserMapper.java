package ru.aston.util;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.aston.model.Role;
import ru.aston.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class UserMapper {

    private static UserMapper instance;

    public static UserMapper getInstance() {
        if (instance == null) {
            instance = new UserMapper();
        }
        return instance;
    }

    @SneakyThrows(SQLException.class)
    public User map(ResultSet resultSet) {
        return User.builder()
                .id(resultSet.getLong(1))
                .email(resultSet.getString(2))
                .password(resultSet.getString(3))
                .role(Role.builder()
                        .id(resultSet.getInt(4))
                        .name(resultSet.getString(5))
                        .build())
                .build();
    }

}
