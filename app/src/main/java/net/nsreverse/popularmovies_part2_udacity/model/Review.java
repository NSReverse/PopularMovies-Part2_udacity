package net.nsreverse.popularmovies_part2_udacity.model;

/**
 * Basic model for holding a single piece of a data source of reviews.
 *
 * @author Robert
 * Created on 6/4/2017.
 */
public class Review {
    private String reviewer;
    private String content;

    public Review() {
        reviewer = "";
        content = "";
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReviewer() {
        return reviewer;
    }

    public String getContent() {
        return content;
    }
}
