package Service;

import Database.DatabaseHandler;
import Model.Article;
import Model.SystemUser;
import Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecommendationService {

    private DatabaseHandler dbHandler = new DatabaseHandler();

    public MatrixFactorizationModel trainModel(SystemUser user) {
        if (user instanceof User) {
            List<Article> likedArticles = dbHandler.getLikedArticles(user.getUserID());
            List<Article> viewedArticles = dbHandler.getViewedArticles(user.getUserID());
            List<Article> allArticles = getNonSkippedArticles(user.getUserID());

            int numUsers = 1; // Since we are training for a single user
            int numItems = allArticles.size();
            int numFactors = 10; // Number of latent factors
            double learningRate = 0.01;
            double regularization = 0.01;
            int numIterations = 50;

            int[][] ratings = new int[numUsers][numItems];

            // Create a map from article ID to index
            Map<Integer, Integer> articleIdToIndex = new HashMap<>();
            for (int i = 0; i < allArticles.size(); i++) {
                articleIdToIndex.put(allArticles.get(i).getId(), i);
            }

            // Populate the rating matrix
            for (Article article : likedArticles) {
                Integer index = articleIdToIndex.get(article.getId());
                if (index != null) {
                    ratings[0][index] = 5; // Liked articles get a high rating
                }
            }
            for (Article article : viewedArticles) {
                Integer index = articleIdToIndex.get(article.getId());
                if (index != null) {
                    ratings[0][index] = 3; // Viewed articles get a lower rating
                }
            }

            // Train the ALS model
            ALS als = new ALS(numUsers, numItems, numFactors, learningRate, regularization, numIterations);
            als.train(ratings);

            return als.getModel();
        } else {
            // Admin does not need recommendations
            return null;
        }
    }

    public List<Article> getRecommendations(SystemUser user, MatrixFactorizationModel model) {
        if (user instanceof User) {
            List<Article> allArticles = getNonSkippedArticles(user.getUserID());
            List<Article> recommendations = new ArrayList<>();

            // Create a map from article ID to index
            Map<Integer, Integer> articleIdToIndex = new HashMap<>();
            for (int i = 0; i < allArticles.size(); i++) {
                articleIdToIndex.put(allArticles.get(i).getId(), i);
            }

            // Predict ratings for all articles
            Map<Article, Double> predictedRatings = new HashMap<>();
            for (Article article : allArticles) {
                Integer index = articleIdToIndex.get(article.getId());
                if (index != null) {
                    double predictedRating = model.predict(0, index);
                    predictedRatings.put(article, predictedRating);
                }
            }

            // Sort articles by predicted rating and get the top 10
            recommendations = predictedRatings.entrySet().stream()
                    .sorted(Map.Entry.<Article, Double>comparingByValue().reversed())
                    .limit(10) // Limit to top 10 recommendations
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // Fallback: If no recommendations, get popular articles
            if (recommendations.isEmpty()) {
                recommendations = getPopularArticles();
            }

            return recommendations;
        } else {
            // Admin does not need recommendations
            return new ArrayList<>();
        }
    }

    private List<Article> getNonSkippedArticles(int userId) {
        List<Article> allArticles = dbHandler.getAllArticles();
        List<Integer> skippedArticleIds = dbHandler.getSkippedArticles(userId).stream()
                .map(Article::getId)
                .collect(Collectors.toList());

        return allArticles.stream()
                .filter(article -> !skippedArticleIds.contains(article.getId()))
                .collect(Collectors.toList());
    }

    private List<Article> getPopularArticles() {
        // Fetch the most popular articles (e.g., based on the number of views or likes)
        return dbHandler.getPopularArticles();
    }
}
