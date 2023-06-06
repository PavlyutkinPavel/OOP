package chat_storage;

import chat_enty.User;

import java.sql.*;

public class DataBaseConnector {
    private Connection connection;
    private String url;
    private String username;
    private String password;

    public DataBaseConnector(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addUser(User user) throws SQLException {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
        }

    }

    public User getUser(String username) throws SQLException {
        String query = "SELECT username, password FROM users WHERE username = ?";
        User user = null;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String dbUsername = resultSet.getString("username");
                String dbPassword = resultSet.getString("password");
                user = new User(dbUsername, dbPassword);
            }
        }

        return user;
    }

    public boolean userExists(String username, String password) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        boolean exists = false;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                exists = count > 0;
            }
        }

        return exists;
    }



    // Other methods for working with the database...

}
