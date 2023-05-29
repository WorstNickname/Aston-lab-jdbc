package ru.aston.dao;

import org.junit.jupiter.api.Test;
import ru.aston.model.Role;
import ru.aston.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserDaoTest {

    private final UserDao userDao = UserDao.getInstance();

    @Test
    void saveSuccessful() {
        var user = User.builder()
                .email("testemail@test.com")
                .password("123")
                .role(Role.builder().name("USER").build())
                .build();

        userDao.save(user);
        var savedUser = userDao.findById(user.getId());

        assertThat(user.getId()).isGreaterThan(0);
        assertThat(user).isEqualTo(savedUser.get());
    }

    @Test
    void findById() {
        var mayBeUser = userDao.findById(1L);
        assertThat(mayBeUser).isPresent();
    }

    @Test
    void findAll() {
        var all = userDao.findAll();
        System.out.println(all);
    }

    @Test
    void deleteIfUserExists() {
        var res = userDao.delete(1L);
        assertThat(res).isTrue();
    }

    @Test
    void update() {
        var updatedUser = User.builder()
                .id(6L)
                .email("testemail@test.com")
                .password("updated")
                .role(Role.builder().name("AUDIT").build())
                .build();
        userDao.update(updatedUser);
    }

}