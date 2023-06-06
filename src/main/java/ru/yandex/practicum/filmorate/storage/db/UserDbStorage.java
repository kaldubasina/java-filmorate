package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> getAll() {
        String sqlQuery = "select * " +
                "from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public Optional<User> getById(Integer id) {
        String sqlQuery = "select * " +
                "from users " +
                "where id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id));
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    @Override
    public User addNew(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        int userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        return getById(userId).get();
    }

    @Override
    public User update(User user) {
        String sqlQuery = "update users set " +
                "email = ?, " +
                "login = ?, " +
                "name = ?, " +
                "birthday = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return getById(user.getId())
                .orElseThrow(() ->
                        new UserNotFoundException("Пользователь с id " + user.getId() + " не найден"));
    }

    @Override
    public Set<User> getFriendsByUserId(Integer userId) {
        String sqlQuery = "select * " +
                "from users " +
                "where id in " +
                "(select friend_id " +
                "from user_friends " +
                "where user_id = ?)";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId));
    }

    @Override
    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        return getFriendsByUserId(id).stream()
                .filter(getFriendsByUserId(otherId)::contains)
                .collect(Collectors.toSet());
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        getById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("Пользователь с id " + userId + " не найден"));
        getById(friendId)
                .orElseThrow(() ->
                        new UserNotFoundException("Пользователь с id " + friendId + " не найден"));
        String sqlQuery = "insert into user_friends " +
                "(user_id, friend_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        getById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("Пользователь с id " + userId + " не найден"));
        getById(friendId)
                .orElseThrow(() ->
                        new UserNotFoundException("Пользователь с id " + friendId + " не найден"));
        String sqlQuery = "delete from user_friends " +
                "where user_id = ? and friend_id = ? " +
                "limit 1";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(getFriendsByUserId(resultSet.getInt("id")))
                .build();
    }
}