package newsbot.news;

import newsbot.network.DataFetcher; // <-- 1. Импортируем DataFetcher
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class LentaRssParser {

    private final DataFetcher fetcher;

    public LentaRssParser(DataFetcher fetcher) {
        this.fetcher = fetcher;
    }

    public List<NewsStory> parse(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
        List<NewsStory> news = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(stream);
        doc.getDocumentElement().normalize();

        NodeList items = doc.getElementsByTagName("item");

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);

            try {
                String title = getTagValue("title", item);
                String date = getTagValue("pubDate", item);
                String author = getTagValue("author", item);
                String link = getTagValue("link", item);

                if (link.isEmpty() || link.equals("Нет")) {
                    continue; // Пропускаем эту новость
                }

                String rawCategory = getTagValue("category", item);
                NewsCategory mappedCategory = mapLentaCategory(rawCategory);

                URI descriptionURI = new URI(link);
                String description = getDescription(descriptionURI);

                news.add(new NewsStory(title, date, author, link, mappedCategory, description));

            } catch (Exception e) {

                System.err.println("Не удалось спарсить 1 новость: " + e.getMessage());
            }
        }
        return news;
    }

    private String getDescription(URI descriptionURL) {

        try (InputStream stream = fetcher.fetch(descriptionURL.toString());
             BufferedReader citeBuffer = new BufferedReader(new InputStreamReader(stream))) {

            String line;
            String description = null;
            String searchStr = "\"description\": \"";

            while ((line = citeBuffer.readLine()) != null) {
                if (line.contains(searchStr)) {
                    int start = line.indexOf(searchStr) + searchStr.length();
                    int end = line.indexOf("\"", start);
                    if (end != -1) {
                        description = line.substring(start, end);
                    }
                    break;
                }
            }
            return (description == null) ? "Нет" : description;
        } catch (IOException | URISyntaxException e) {
            return "Ошибка чтения страницы lenta ru";
        }
    }


    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            String content = nodeList.item(0).getTextContent();
            return (content == null) ? "Нет" : content.trim();
        }
        return "Нет";
    }


    private NewsCategory mapLentaCategory(String lentaCategory) {
        if (lentaCategory == null) {
            return NewsCategory.OTHER;
        }
        switch (lentaCategory) {
            case "Россия", "Мир", "Бывший СССР", "Силовые структуры":
                return NewsCategory.POLITICS;
            case "Экономика":
                return NewsCategory.ECONOMY;
            case "Наука и техника", "Интернет и СМИ":
                return NewsCategory.TECHNOLOGY;
            case "Культура":
                return NewsCategory.CULTURE;
            case "Спорт":
                return NewsCategory.SPORT;
            default:
                return NewsCategory.OTHER;
        }
    }
}