package Service;

import Database.DatabaseHandler;
import Model.Article;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ArticleService {
    private DatabaseHandler dbHandler = new DatabaseHandler();
    private static final String API_KEY = "0ada196c1f4f45d78729839c05e7d31d";
    private final String API_URL = "https://newsapi.org/v2/top-headlines?country=us&apiKey=" + API_KEY;

    // Fetch articles from API
    public List<Article> fetchArticlesFromAPI() {
        List<Article> articles = new ArrayList<>();

        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray articlesArray = jsonResponse.getAsJsonArray("articles");

            for (int i = 0; i < articlesArray.size(); i++) {
                JsonObject articleJson = articlesArray.get(i).getAsJsonObject();
                int id = articleJson.has("id") ? articleJson.get("id").getAsInt() : -1;
                String title = articleJson.get("title").getAsString();
                String source = articleJson.getAsJsonObject("source").get("name").getAsString();
                String urlToArticle = articleJson.get("url").getAsString();
                String content = articleJson.has("content") && !articleJson.get("content").isJsonNull() ? articleJson.get("content").getAsString() : "";
                String publicationDate = articleJson.get("publishedAt").getAsString();
                Article article = new Article( id,title, source, urlToArticle, content, "General", publicationDate);

                // Validate article and add to list if valid
                if (article.isValid()) {
                    articles.add(article);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return articles;
    }

    // Fetch all articles from the database
    public List<Article> getAllArticles() {
        return dbHandler.getAllArticles();
    }
}