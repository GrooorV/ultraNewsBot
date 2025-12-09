    package newsbot.repository.database;

    import newsbot.news.NewsCategory;
    import newsbot.repository.UserProfileRepository;
    import newsbot.shared.UserId;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.util.HashSet;
    import java.util.Set;

    public class DatabaseUserProfileRepository implements UserProfileRepository {
        @Override
        public boolean categoriesAreEmpty(UserId userId) {
            Set<NewsCategory> set = getCategories(userId);
            return set.isEmpty();
        }

        @Override
        public Set<NewsCategory> getCategories(UserId userId) {
            String sql = "SELECT category FROM users_categories WHERE user_id = ?";

            Set<NewsCategory> set = new HashSet<>();

            try (Connection conn = PostgresConnection.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setString(1, userId.getValue());

                try(ResultSet rs = statement.executeQuery()) {

                    while (rs.next()) {
                        set.add(NewsCategory.valueOf(rs.getString("category")));
                    }
                }

            } catch (SQLException e) {
                System.err.println("Error while get categories: " + e.getMessage());
            }
            return set;
        }

        @Override
        public void addCategory(UserId userId, NewsCategory newsCategory) {
            String sql = "INSERT INTO users_categories (user_id, category) values (?, ?) " +
                    "ON CONFLICT (user_id, category) DO NOTHING";

            try (Connection conn = PostgresConnection.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setString(1, userId.getValue());
                statement.setString(2, newsCategory.name());

                statement.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error while add category: " + e.getMessage());
            }
        }

        @Override
        public void removeCategory(UserId userId, NewsCategory newsCategory) {
            String sql = "DELETE FROM users_categories WHERE user_id = ? AND category = ?";

            try(Connection conn = PostgresConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setString(1, userId.getValue());
                statement.setString(2, newsCategory.name());

                statement.executeUpdate();

            } catch (SQLException e) {
                System.err.println("Error while remove category: " + e.getMessage());
            }
        }
    }
