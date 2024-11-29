package Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Article {
    private String title;
    private String source;
    private String url;
    private String content;
    private String category;
    private Date publicationDate;

    // No-argument constructor
    public Article() {
    }

    // Constructor
    public Article(String title, String source, String url, String content, String category, String publicationDateStr) {
        this.title = title;
        this.source = source;
        this.url = url;
        this.content = content;
        this.category = category;
        this.publicationDate = parseDate(publicationDateStr);

    }

    // Utility to parse date
    private Date parseDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Date parsing failed for: " + dateString);
            return null;
        }
    }

    // Validate essential fields
    public boolean isValid() {
        return title != null && !title.isEmpty() &&
                source != null && !source.isEmpty() &&
                url != null && !url.isEmpty();
    }
    // Getters and other methods
    public String getTitle() { return title; }
    public String getSource() { return source; }
    public String getUrl() { return url; }
    public Date getPublicationDate() { return publicationDate; }
    public String getContent() { return content; }
    public String getCategory() { return category; }

    // Setter for category
    public void setCategory(String category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSource(String source) {
        this.source = source;
    }
}