package org.example.socialnetwork.Repository;

import org.example.socialnetwork.Domain.Friendship;
import org.example.socialnetwork.Utils.Pageable.Page;
import org.example.socialnetwork.Utils.Pageable.Pageable;
import org.example.socialnetwork.Validators.Validator;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.*;

public class FriendshipDBRepository implements FriendshipPageRepository<Long, Friendship> {
    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;

    public FriendshipDBRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Friendship> findOne(Long id) {
        String query = "SELECT * FROM friendships WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime date = resultSet.getTimestamp("friendssince").toLocalDateTime();
                boolean pending = resultSet.getBoolean("pending");

                Friendship friendship = new Friendship(id1, id2, date);
                friendship.setId(id);
                friendship.setPending(pending);
                return Optional.of(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long user_id1 = resultSet.getLong("user1");
                Long user_id2 = resultSet.getLong("user2");
                LocalDateTime date = resultSet.getTimestamp("friendssince").toLocalDateTime();
                boolean pending = resultSet.getBoolean("pending");
                Friendship prietenie = new Friendship(user_id1, user_id2, date);
                prietenie.setId(id);
                prietenie.setPending(pending);
                friendships.add(prietenie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        validator.validate(entity);
        String query = "INSERT INTO friendships (id, user1, user2, friendssince, pending) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, entity.getId());
            statement.setLong(2, entity.getId1());
            statement.setLong(3, entity.getId2());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getDate()));
            statement.setBoolean(5, entity.isPending());

            int result = statement.executeUpdate();
            if (result > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getLong(1));
                    }
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<Friendship> delete(Long id) {
        String query = "DELETE FROM friendships WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            int result = statement.executeUpdate();
            if (result > 0) return Optional.of(new Friendship(-1L, -1L, LocalDateTime.now()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        validator.validate(entity);
        String query = "UPDATE friendships SET user1 = ?, user2 = ?, friendssince = ?, pending = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.getId1());
            statement.setLong(2, entity.getId2());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            statement.setBoolean(4, entity.isPending());
            statement.setLong(5, entity.getId());

            int result = statement.executeUpdate();
            if (result > 0) return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity);
    }

    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable, Long id) {
        List<Friendship> friendships = new ArrayList<>();
        String query = "SELECT * FROM friendships WHERE (user1 = ? OR user2 = ?) AND pending = false ";
        query += "LIMIT ? OFFSET ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setInt(3, pageable.getPageSize());
            statement.setInt(4, pageable.getPageNumber() * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long Id = resultSet.getLong("id");
                Long user_id1 = resultSet.getLong("user1");
                Long user_id2 = resultSet.getLong("user2");
                LocalDateTime date = resultSet.getTimestamp("friendssince").toLocalDateTime();
                boolean pending = resultSet.getBoolean("pending");
                if(user_id1.equals(id)) {
                    Friendship prietenie = new Friendship(user_id1, user_id2, date);
                    prietenie.setId(Id);
                    prietenie.setPending(pending);
                    friendships.add(prietenie);
                }
                else {
                    Friendship prietenie = new Friendship(user_id2, user_id1, date);
                    prietenie.setId(Id);
                    prietenie.setPending(pending);
                    friendships.add(prietenie);
                }
            }

            return new Page<>(friendships, friendships.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
