package newsbot.news;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.IOException;
import java.net.URI;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LentaNewsProvider implements NewsProvider {
    public List<NewsStory> getNews() {
        // Список новостей (пока пустой)
        List<NewsStory> news = new ArrayList<>();
        // Русская RSS-лента Lenta.ru
        String rssUrl = "https://lenta.ru/rss";

        // Пробуем загрузить RSS-ленту
        try (InputStream stream = new URI(rssUrl).toURL().openStream()) {

            // Создаем парсер
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Парсим XML
            Document doc = builder.parse(stream);
            doc.getDocumentElement().normalize();

            // Получаем все элементы <item>
            NodeList items = doc.getElementsByTagName("item");

            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                // Получаем заголовок
                String title = getTagValue("title", item);
                // Получаем дату
                String date = getTagValue("pubDate", item);
                // Получаем автора
                String author = getTagValue("author", item);
                // Получаем ссылку
                String link = getTagValue("link", item);
                // Получаем категорию
                String category = getTagValue("category", item);
                // Получаем описание
                URI descriptionURI = new URI(link);
                String description = getDescription(descriptionURI);

                // Формируем список новостей
                news.add(new NewsStory(title, date, author, link, category, description) );
            }

            return news;

        } catch (Exception e) {
            // Возвращает пустой список
            return news;
        }
    }

    private static String getDescription(URI descriptionURL) {
        try (BufferedReader citeBuffer =
                     new BufferedReader(new InputStreamReader(descriptionURL.toURL().openStream()))) {
            String line;
            // Значение по умолчанию
            String description = "Нет";
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
            return description;
        } catch (IOException e) {
            return "Ошибка";
        }
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "Нет";
    }
}
