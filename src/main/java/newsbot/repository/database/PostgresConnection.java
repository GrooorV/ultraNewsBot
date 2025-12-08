package newsbot.repository.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnection {

    public static Connection getConnection() throws SQLException {
        String url = System.getenv("url");
        String user = System.getenv("user");
        String password = System.getenv("password");

        return DriverManager.getConnection(url, user, password);
    }
}
