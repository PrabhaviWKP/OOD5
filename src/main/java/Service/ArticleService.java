package Service;

import Model.Article;
import com.google.gson.Gson;
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

    // URL of the API endpoint
    private final String API_URL = "https://newsapi.org/v2/top-headlines?country=us&apiKey=0ada196c1f4f45d78729839c05e7d31d";

    // Method to fetch articles from an external API
    public List<Article> fetchArticlesFromAPI() {
        List<Article> articles = new ArrayList<>();

        try {
            // Connect to the API
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read response from API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse the JSON response
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray articlesArray = jsonResponse.getAsJsonArray("articles");

            // Use Gson to convert each article in the array to an Article object
            Gson gson = new Gson();
            for (int i = 0; i < articlesArray.size(); i++) {
                JsonObject articleJson = articlesArray.get(i).getAsJsonObject();

                // Check each field for null before accessing it
                String title = articleJson.has("title") && !articleJson.get("title").isJsonNull()
                        ? articleJson.get("title").getAsString()
                        : "No title available";

                String description = articleJson.has("description") && !articleJson.get("description").isJsonNull()
                        ? articleJson.get("description").getAsString()
                        : "No description available";

                String urlToImage = articleJson.has("urlToImage") && !articleJson.get("urlToImage").isJsonNull()
                        ? articleJson.get("urlToImage").getAsString()
                        : null;

                String publishedAt = articleJson.has("publishedAt") && !articleJson.get("publishedAt").isJsonNull()
                        ? articleJson.get("publishedAt").getAsString()
                        : null;

                String content = articleJson.has("content") && !articleJson.get("content").isJsonNull()
                        ? articleJson.get("content").getAsString()
                        : "No content available";

                // Filter or limit articles based on relevance or specific categories
                String category = determineCategory(title, description, content);

                // Create an Article object and add it to the list
                Article article = new Article(title, description, urlToImage, publishedAt, content, category);
                articles.add(article);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return articles;
    }

    // Method to determine the category of an article based on its title, description, and content
    private String determineCategory(String title, String description, String content) {
        // Implement your logic here to determine the category based on keywords or a predefined list of categories
        // For example, you can use a simple keyword search or a machine learning model to classify the article
        // In this example, we'll just return a default category of "general"
        return "general";
    }
}
