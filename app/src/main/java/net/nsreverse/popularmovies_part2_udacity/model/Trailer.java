package net.nsreverse.popularmovies_part2_udacity.model;

/**
 * Basic model for holding a single piece of a data source of trailers.
 *
 * @author Robert
 * Created on 6/4/2017.
 */
public class Trailer {
    private String name;
    private String videoId;
    private String type;
    private boolean isYoutube;

    public Trailer() {
        name = "";
        videoId = "";
        type = "";
        isYoutube = false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIsYoutube(boolean isYoutube) {
        this.isYoutube = isYoutube;
    }

    public String getName() {
        return name;
    }

    public String getVideoId() {
        return videoId;
    }

    @SuppressWarnings("unused")
    public String getType() {
        return type;
    }

    public boolean getIsYoutube() {
        return isYoutube;
    }
}
