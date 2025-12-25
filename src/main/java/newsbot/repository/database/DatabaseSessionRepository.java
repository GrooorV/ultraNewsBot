package newsbot.repository.database;

import newsbot.engine.UserSession;
import newsbot.repository.SessionRepository;
import newsbot.shared.UserId;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseSessionRepository implements SessionRepository {

    private static final String DELIMITER = ";;";

    @Override
    public UserSession getOrCreate(UserId userId) {
        String sql = "SELECT * FROM sessions WHERE user_id = ?";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId.getValue());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRowToSession(rs);
            } else {
                return new UserSession();
            }

        } catch (SQLException e) {
            System.err.println("Ошибка загрузки сессии: " + e.getMessage());
            return new UserSession();
        }
    }

    @Override
    public void save(UserId userId, UserSession session) {
        String sql = """
                INSERT INTO sessions (user_id, is_started, last_check_time, pending_links, pending_index)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (user_id) DO UPDATE 
                SET is_started = EXCLUDED.is_started,
                    last_check_time = EXCLUDED.last_check_time,
                    pending_links = EXCLUDED.pending_links,
                    pending_index = EXCLUDED.pending_index
                """;

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId.getValue());
            stmt.setBoolean(2, session.isStarted());

            Instant time = session.getLastNewsCheckTime();
            stmt.setTimestamp(3, (time == null) ? null : Timestamp.from(time));

            String joinedLinks = String.join(DELIMITER, session.getPendingNewsLinks());
            stmt.setString(4, joinedLinks);

            stmt.setInt(5, session.getPendingNewsIndex());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка сохранения сессии: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private UserSession mapRowToSession(ResultSet rs) throws SQLException {
        UserSession session = new UserSession();

        if (rs.getBoolean("is_started")) {
            session.markStarted();
        }

        Timestamp ts = rs.getTimestamp("last_check_time");
        if (ts != null) {
            session.setLastNewsCheckTime(ts.toInstant());
        }

        String linksRaw = rs.getString("pending_links");
        if (linksRaw != null && !linksRaw.isEmpty()) {
            String[] parts = linksRaw.split(DELIMITER);
            List<String> links = Arrays.stream(parts)
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toList());

            session.setPendingNewsLinks(links);
        }

        session.setPendingNewsIndex(rs.getInt("pending_index"));

        return session;
    }
}