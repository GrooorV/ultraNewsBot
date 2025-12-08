package newsbot.repository.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseInitializer {

    private PostgresConnection connection;

    public DatabaseInitializer(PostgresConnection connection) { this.connection = connection; }


    public void init() {
        createUsersCategoriesTable();
        createSessionTable();
        createNewsCacheTable();
    }

    private void createUsersCategoriesTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS user_categories (
                   UserId VARCHAR(50),
                   Category VARCHAR(50),
                   PRIMARY KEY (UserId, Category)
                );
                """;
        execute(sql);
    }


    private void createNewsCacheTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS news_cache (
                    link VARCHAR(300) PRIMARY KEY,
                    title TEXT NOT NULL,
                    pub_date VARCHAR(100),
                    author VARCHAR(200),
                    category VARCHAR(50),
                    description TEXT,
                    picture_link VARCHAR(500),
                    fetched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                """;
        execute(sql);
    }

    private void createSessionTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS sessions (
                    user_id VARCHAR(100) PRIMARY KEY,
                    is_started BOOLEAN DEFAULT FALSE,
                    last_check_time TIMESTAMP,
                    pending_links TEXT,
                    pending_index INTEGER DEFAULT 0
                );
                """;
        execute(sql);
    }

    private void execute(String sql) {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while creating Database");
        }
    }
}


