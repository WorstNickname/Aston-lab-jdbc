package ru.aston.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"id", "email"})
public class User {

    private long id;
    private String email;
    private String password;
    private Role role;

}
