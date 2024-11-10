package Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Article {
    private int articleID;
    private String articleTitle;
    private String content;
    private String category;
    private String source;
    private Date publicationDate;

    // Constructor without articleID for API response articles
    public Article(String articleTitle, String content, String category, String source, String publicationDateStr, String s) {
        this.articleTitle = articleTitle;
        this.content = content;
        this.category = category;
        this.source = source;
        this.publicationDate = parseDate(publicationDateStr);
    }

    // Utility to parse date
    public static Date parseDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            // Handle the exception or log it
            System.out.println("Date parsing failed for: " + dateString);
            return null; // Return null if the date is not valid
        }
    }


    // Getters and setters
    public String getArticleTitle() { return articleTitle; }
    public void setArticleTitle(String articleTitle) { this.articleTitle = articleTitle; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Date getPublicationDate() { return publicationDate; }
    public void setPublicationDate(String publicationDateStr) { this.publicationDate = parseDate(publicationDateStr); }

    @Override
    public String toString() {
        return "Article{" +
                "articleID=" + articleID +
                ", title='" + articleTitle + '\'' +
                ", category='" + category + '\'' +
                ", publicationDate=" + publicationDate +
                '}';
    }
}
