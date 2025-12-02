package newsbot.news;

import newsbot.network.DataFetcher; // <-- Импорт
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;



public class LentaNewsProvider implements NewsProvider {

    private final DataFetcher fetcher;
    private final LentaRssParser parser;
    private final String rssUrl = "https://lenta.ru/rss";

    public LentaNewsProvider(DataFetcher fetcher, LentaRssParser parser) {
        this.fetcher = fetcher;
        this.parser = parser;
    }

    @Override
    public List<NewsStory> getNews() {
        try (InputStream stream = fetcher.fetch(rssUrl)) {
            return parser.parse(stream);
        } catch (IOException | URISyntaxException | ParserConfigurationException | SAXException e) {
            System.err.println("Не удалось получить или распарсить новости Lenta: " + e.getMessage());
            return List.of();
        }
    }
}