package newsbot.repository.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnection {

    public static Connection getConnection() throws SQLException {
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASSWORD");

        if (dbUrl == null) dbUrl = "jdbc:postgresql://localhost:5432/newsbot_db";
        if (dbUser == null) dbUser = "postgres";
        if (dbPass == null) dbPass = "12345";


        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }
}