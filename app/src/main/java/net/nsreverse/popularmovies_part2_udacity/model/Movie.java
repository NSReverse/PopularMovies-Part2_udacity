package net.nsreverse.popularmovies_part2_udacity.model;

import android.graphics.Bitmap;

/**
 * Basic model for holding a single piece of a data source of movies.
 *
 * @author Robert
 * Created on 5/26/2017.
 */
public class Movie {
    private String title;
    private Bitmap thumbnail;
    private String thumbnailAddress;
    private Bitmap backdrop;
    private String backdropAddress;
    private String releaseDate;
    private String voteAverage;
    private String plotSynopsis;

    private int rowId;
    private int tmdbId;
    private boolean isFavorite;

    public Movie() {
        title = "";
        thumbnailAddress = "";
        releaseDate = "";
        voteAverage = "";
        plotSynopsis = "";
        rowId = -1;
        tmdbId = -1;
        isFavorite = false;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setThumbnailAddress(String thumbnailAddress) {
        this.thumbnailAddress = thumbnailAddress;
    }

    public void setBackdrop(Bitmap backdrop) {
        this.backdrop = backdrop;
    }

    public void setBackdropAddress(String backdropAddress) {
        this.backdropAddress = backdropAddress;
    }

    public void setTmdbId(int id) {
        tmdbId = id;
    }

    public void setRowId(int id) {
        rowId = id;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getThumbnailAddress() {
        return thumbnailAddress;
    }

    public Bitmap getBackdrop() {
        return backdrop;
    }

    public String getBackdropAddress() {
        return backdropAddress;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public int getRowId() {
        return rowId;
    }

    public boolean getFavorite() {
        return isFavorite;
    }

    @Override
    public String toString() {
        return "Title: " + getTitle() + "\n" +
                "Release Date: " + getReleaseDate() + "\n" +
                "Thumb Address: " + getThumbnailAddress() + "\n";
    }
}