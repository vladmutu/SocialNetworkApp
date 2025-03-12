package org.example.socialnetwork.Repository;

import org.example.socialnetwork.Domain.User;
import org.example.socialnetwork.Validators.Validator;

import java.sql.*;
import java.util.*;

public class UserDBRepository implements Repository<Long, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;

    public UserDBRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<User> findOne(Long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                byte[] profilePicture = resultSet.getBytes("profilepicture");
                User user = new User(firstName, lastName, email, password, profilePicture);
                user.setId(id);

                //check if the user has friends and add them
                query = "SELECT * FROM Friendships WHERE user1 = ? OR user2 = ?";
                try(PreparedStatement statement2 = connection.prepareStatement(query)) {
                    statement2.setLong(1, id);
                    statement2.setLong(2, id);
                    ResultSet resultSet2 = statement2.executeQuery();
                    while(resultSet2.next()) {
                        Long friendId = resultSet2.getLong("id");
                        query = "SELECT * FROM Users WHERE id = ?";
                        try(PreparedStatement statement3 = connection.prepareStatement(query)) {
                            statement3.setLong(1, friendId);
                            ResultSet resultSet3 = statement3.executeQuery();
                            if(resultSet3.next()) {
                                String friendFirstName = resultSet3.getString("firstname");
                                String friendLastName = resultSet3.getString("lastname");
                                String friendPassword = resultSet3.getString("password");
                                String friendEmail = resultSet3.getString("email");
                                byte[] friendProfilePicture = resultSet3.getBytes("profilepicture");
                                User friend = new User(friendFirstName, friendLastName, friendEmail, friendPassword, friendProfilePicture);
                                friend.setId(friendId);
                                user.addFriend(friend);
                            }
                        }
                    }
                }
                return Optional.of(user);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                byte[] profilePicture = resultSet.getBytes("profilepicture");
                User user = new User(firstName, lastName, email, password, profilePicture);
                user.setId(id);

                //check if the user has friends and add them
                String query = "SELECT * FROM Friendships WHERE user1 = ? OR user2 = ?";
                try(PreparedStatement statement2 = connection.prepareStatement(query)) {
                    statement2.setLong(1, id);
                    statement2.setLong(2, id);
                    ResultSet resultSet2 = statement2.executeQuery();
                    while(resultSet2.next()) {
                        Long friendId = resultSet2.getLong("id");
                        query = "SELECT * FROM Users WHERE id = ?";
                        try(PreparedStatement statement3 = connection.prepareStatement(query)) {
                            statement3.setLong(1, friendId);
                            ResultSet resultSet3 = statement3.executeQuery();
                            if(resultSet3.next()) {
                                String friendFirstName = resultSet3.getString("firstname");
                                String friendLastName = resultSet3.getString("lastname");
                                String friendPassword = resultSet3.getString("password");
                                String friendEmail = resultSet3.getString("email");
                                byte[] friendProfilePicture = resultSet3.getBytes("profilepicture");
                                User friend = new User(friendFirstName, friendLastName, friendEmail, friendPassword, friendProfilePicture);
                                friend.setId(friendId);
                                user.addFriend(friend);
                            }
                        }
                    }
                }
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Optional<User> save(User entity) {
        validator.validate(entity);
        String query = "INSERT INTO users (id, firstname, lastname, email, password, profilepicture) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.getId());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getLastName());
            statement.setString(4, entity.getEmail());
            statement.setString( 5, entity.getPassword());
            statement.setBytes(6, entity.getProfilePicture());
            int result = statement.executeUpdate();
            if (result > 0) return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<User> delete(Long id) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
             statement.setLong(1, id);
             int result = statement.executeUpdate();
             if (result > 0) return Optional.of(new User("Deleted", "User", "deleted", "deleted", new byte[0]));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User entity) {
        validator.validate(entity);
        String query = "UPDATE users SET firstname = ?, lastname = ?, email = ?, password = ?, profilepicture = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getPassword());
            statement.setBytes(5, entity.getProfilePicture());
            statement.setLong(5, entity.getId());

            int result = statement.executeUpdate();
            if (result > 0) return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity);
    }

}
