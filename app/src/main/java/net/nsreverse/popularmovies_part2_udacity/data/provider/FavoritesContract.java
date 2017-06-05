package net.nsreverse.popularmovies_part2_udacity.data.provider;

import android.net.Uri;
import android.provider.BaseColumns;


/**
 * This class defines the keys, the path, and the content Uri of a provider.
 *
 * @author Robert
 * Created on 6/3/2017.
 */
public class FavoritesContract {
    public static final String AUTHORITY = "net.nsreverse.popularmovies_part2_udacity";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";

    /**
     * This class contains the keys to the columns in the provider's underlying database.
     */
    public static final class FavoritesEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_SYNOPSIS = "movieSynopsis";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";
        public static final String COLUMN_MOVIE_VOTE_AVG = "movieRating";
        public static final String COLUMN_MOVIE_THUMB_NAME = "thumbFilename";
        public static final String COLUMN_MOVIE_BACKDROP_NAME = "backdropFilename";
    }
}
