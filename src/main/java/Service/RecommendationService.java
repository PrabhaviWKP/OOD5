package Service;

import Database.DatabaseHandler;
import Model.Article;
import net.librec.common.LibrecException;
import net.librec.conf.Configuration;
import net.librec.data.DataModel;
import net.librec.data.model.TextDataModel;
import net.librec.recommender.RecommenderContext;
import net.librec.recommender.cf.UserKNNRecommender;
import net.librec.recommender.item.RecommendedItem;
import net.librec.similarity.PCCSimilarity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecommendationService {
    private final DatabaseHandler dbHandler = new DatabaseHandler();

    // Method to get recommendations for a user
    public List<RecommendedItem> getRecommendations(int userId) throws LibrecException, IOException {
        // Fetch viewed and liked articles for the user
        List<Article> viewedArticles = dbHandler.getViewedArticles(userId);
        List<Article> likedArticles = dbHandler.getLikedArticles(userId);

        // Create LibRec-compatible data format (UIR: UserId, ItemId, Rating)
        File tempFile = createInteractionFile(userId, viewedArticles, likedArticles);

        // Load the data into LibRec's DataModel
        DataModel dataModel = buildDataModel(tempFile);

        // Configure and train the recommender
        Configuration config = new Configuration();
        config.set("data.model.splitter", "ratio");
        config.set("data.model.splitter.ratio", "0.8"); // 80-20 train-test split
        config.set("data.input.path", tempFile.getAbsolutePath());
        config.set("data.column.format", "UIR");
        config.set("rec.recommender.class", UserKNNRecommender.class.getName());
        config.set("rec.similarity.class", PCCSimilarity.class.getName());
        config.set("rec.neighbors.knn.number", "10");

        RecommenderContext context = new RecommenderContext(config, dataModel);
        UserKNNRecommender recommender = new UserKNNRecommender();
        recommender.setContext(context);

        // Debug statement to check similarity matrix
        if (context.getSimilarity() == null) {
            System.out.println("Similarity matrix is null");
        } else {
            System.out.println("Similarity matrix is initialized");
        }

        recommender.train(context);

        // Get recommendations
        List<RecommendedItem> recommendedItems = (List<RecommendedItem>) recommender.recommendRank();
        // Debug statement to check recommendations
        System.out.println("Recommendations fetched: " + recommendedItems.size());

        return recommendedItems;
    }

    private File createInteractionFile(int userId, List<Article> viewedArticles, List<Article> likedArticles) throws IOException {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File tempFile = new File(dataDir, "user_interactions.csv");
        System.out.println("Temporary file created: " + tempFile.getAbsolutePath()); // Debug statement
        try (FileWriter writer = new FileWriter(tempFile)) {
            for (Article article : likedArticles) {
                writer.write(userId + "," + article.getId() + ",1\n");
            }
            for (Article article : viewedArticles) {
                writer.write(userId + "," + article.getId() + ",0\n");
            }
        }
        // Verify that the file exists
        if (tempFile.exists()) {
            System.out.println("File exists: " + tempFile.getAbsolutePath());
        } else {
            System.out.println("File does not exist: " + tempFile.getAbsolutePath());
        }
        return tempFile;
    }

    private DataModel buildDataModel(File interactionFile) throws LibrecException, IOException {
        Configuration conf = new Configuration();

        // Use absolute path and replace backslashes with forward slashes
        String filePath = interactionFile.getAbsolutePath().replace("\\", "/");
        System.out.println("File path to be set: " + filePath); // Debugging

        // File existence check
        File tempFile = new File(filePath);
        if (!tempFile.exists()) {
            throw new IOException("File does not exist: " + tempFile.getAbsolutePath());
        }
        System.out.println("File confirmed: " + tempFile.getAbsolutePath()); // Debugging

        // Configure LibRec settings
        conf.set("dfs.data.dir", interactionFile.getParentFile().getAbsolutePath().replace("\\", "/"));
        conf.set("data.input.path", interactionFile.getName());
        conf.set("data.column.format", "UIR");
        conf.set("data.model.splitter", "ratio");
        conf.set("data.model.splitter.ratio", "0.8"); // 80-20 train-test split

        DataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel(); // Build the model
        return dataModel;
    }
}
