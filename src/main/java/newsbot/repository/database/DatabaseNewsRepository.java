package newsbot.repository.database;

import newsbot.news.NewsCategory;
import newsbot.news.NewsProvider;
import newsbot.news.NewsStory;
import newsbot.repository.NewsRepository;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class DatabaseNewsRepository implements NewsRepository {

    String insertSQL = "INSERT INTO news_cache (link, title, date, author, category, description, pictureLink) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (link) DO NOTHING;";

    String clearTableSQL = "TRUNCATE TABLE news_cache";

    private final NewsProvider provider;
    private final Duration cacheDuration;
    private Instant lastFetchTime;
    private final ReentrantLock lock = new ReentrantLock();

    public DatabaseNewsRepository(NewsProvider provider, Duration cacheDuration) {
        this.provider = provider;
        this.cacheDuration = cacheDuration;
        this.lastFetchTime = null;
    }

    public DatabaseNewsRepository(NewsProvider provider) {
        this(provider, Duration.ofMinutes(10));
    }

    private void refreshCacheIfNeeded() {
        if (lastFetchTime != null && Instant.now().isBefore(lastFetchTime.plus(cacheDuration))) {
            return;
        }

        if (lock.tryLock()) {

            try (Connection conn = PostgresConnection.getConnection();
                 PreparedStatement clearStmt = conn.prepareStatement(clearTableSQL);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

                if (lastFetchTime != null && Instant.now().isBefore(lastFetchTime.plus(cacheDuration))) {
                    return;
                }

                //чистим бд
                clearStmt.executeUpdate();

                //вставляем в бд
                List<NewsStory> cleanNews = provider.getNews();
                Instant fetchTime = Instant.now();
                for (NewsStory news : cleanNews) {
                    insertStmt.setString(1, news.link());
                    insertStmt.setString(2, news.title());
                    insertStmt.setString(3, news.date());
                    insertStmt.setString(4, news.author());
                    insertStmt.setString(5, news.category().toString());
                    insertStmt.setString(6, news.description());
                    insertStmt.setString(7, news.pictureLink());
                    insertStmt.executeUpdate();
                }

                //выход
                this.lastFetchTime = fetchTime;
                System.out.println("[DatabaseNewsRepository] Кэш успешно обновлен.");

            } catch (SQLException e) {
                System.out.println("Возникла проблема в подключении к базе данных" +
                        " или создании команд для sql при кэшировании refreshCacheIfNeeded:");
                System.out.println(e.getMessage());
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println("nullPointer в Connection conn для sql при кэшировании refreshCacheIfNeeded:");
                System.out.println(e.getMessage());
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }


    @Override
    public List<NewsStory> getNewsByCategory(Set<NewsCategory> categories, Instant since) {

        refreshCacheIfNeeded();

        List<NewsStory> result = new ArrayList<>();
        String placeholders = categories.stream()
                .map(cat -> "?")
                .collect(Collectors.joining(", "));

        String sql = "SELECT link, title, date, author, category, description, pictureLink, fetched_at" +
                " FROM news_cache " +
                "WHERE category IN (" + placeholders + ") " +
                "AND fetched_at > ?";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            //станавливаем параметры
            int index = 1;
            for (NewsCategory category : categories) {
                stmt.setString(index++, category.toString());
            }
            stmt.setTimestamp(index, Timestamp.from(since));

            // формирование ответа
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                NewsStory story = new NewsStory(
                        rs.getString("title"),
                        rs.getString("date"),
                        rs.getString("author"),
                        rs.getString("link"),
                        NewsCategory.valueOf(rs.getString("category")),
                        rs.getString("description"),
                        rs.getString("pictureLink")
                );
                result.add(story);
            }
            return result;

        } catch (SQLException e) {
            System.out.println("Возникла проблема в подключении к базе данных" +
                    " или создании команды получения новостей для sql при получении по категориям getNewsByCategory");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("nullPointer в Connection conn для sql при получении по категориям getNewsByCategory");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public List<NewsStory> getNewsByLinks(List<String> links) {

        List<NewsStory> result = new ArrayList<>();
        String placeholders = String.join(",", Collections.nCopies(links.size(), "?"));
        String sql = "SELECT link, title, date, author, category, description, pictureLink, fetched_at " +
                "FROM news_cache " +
                "WHERE link IN (" + placeholders + ")";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            //устанавливаем параметры
            for (int i = 0; i < links.size(); i++) {
                stmt.setString(i + 1, links.get(i));
            }

            // формирование ответа
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                NewsStory story = new NewsStory(
                        rs.getString("title"),
                        rs.getString("date"),
                        rs.getString("author"),
                        rs.getString("link"),
                        NewsCategory.valueOf(rs.getString("category")),
                        rs.getString("description"),
                        rs.getString("pictureLink")
                );
                result.add(story);
            }
            return result;

        }catch (SQLException e) {
            System.out.println("Возникла проблема в подключении к базе данных" +
                    " или создании команды получения новостей для sql при получении по ссылкакм getNewsByLinks");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("nullPointer в Connection conn для sql при получении по ссылкам getNewsByLinks");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}