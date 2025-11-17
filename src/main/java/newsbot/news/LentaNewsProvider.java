package newsbot.news;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URI;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
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
                String rawCategory = getTagValue("category", item);
                NewsCategory mappedCategory = mapLentaCategory(rawCategory);
                // Получаем описание
                URI descriptionURI = new URI(link);
                String description = getDescription(descriptionURI);

                // Формируем список новостей
                news.add(new NewsStory(title, date, author, link, mappedCategory, description));
            }

            return news;

        } catch (IOException | URISyntaxException | ParserConfigurationException | SAXException e) {
            // Возвращает пустой список
            System.err.println("Не удалось загрузить новости Lenta: " + e.getMessage());
            return news;
        }
    }

    private NewsCategory mapLentaCategory(String lentaCategory) {
        if (lentaCategory == null) {
            return NewsCategory.OTHER;
        }

        // Используем switch для удобного маппинга
        switch (lentaCategory) {
            // POLITICS
            case "Россия":
            case "Мир":
            case "Бывший СССР":
            case "Силовые структуры":
                return NewsCategory.POLITICS;

            // ECONOMY
            case "Экономика":
                return NewsCategory.ECONOMY;

            // TECHNOLOGY
            case "Наука и техника":
            case "Интернет и СМИ":
                return NewsCategory.TECHNOLOGY;

            // CULTURE
            case "Культура":
                return NewsCategory.CULTURE;

            // SPORT
            case "Спорт":
                return NewsCategory.SPORT;

            // OTHER (Все остальное)
            case "Ценности":
            case "Путешествия":
            case "Из жизни":
            case "Среда обитания":
            case "Забота о себе":
            case "Победа":
            default:
                return NewsCategory.OTHER;
        }
    }

    private static String getDescription(URI descriptionURL) {
        try (BufferedReader citeBuffer =
                     new BufferedReader(new InputStreamReader(descriptionURL.toURL().openStream()))) {
            String line;
            // Значение по умолчанию
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
        } catch (IOException e) {
            return "Ошибка чтения страницы";
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
