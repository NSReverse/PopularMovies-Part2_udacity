package net.nsreverse.popularmovies_part2_udacity.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import net.nsreverse.popularmovies_part2_udacity.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * This class handles common network operations. Derived from Udacity's NetworkUtils - Project
 * Sunshine
 *
 * @author Robert
 * Created on 6/3/2017.
 */
public class NetworkUtils {
    public enum Sort {
        POPULAR,
        TOP_RATED,
        FAVORITES
    }

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String API_REVIEWS_BASE_URL = "http://api.themoviedb.org/3/movie/{id}/reviews";
    private static final String API_TRAILERS_BASE_URL = "http://api.themoviedb.org/3/movie/{id}/videos";
    private static final String API_THUMBNAIL_342_BASE_URL = "http://image.tmdb.org/t/p/w342";
    private static final String API_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String PARAM_API_KEY = "api_key";

    private static final String[] PATHS = { "popular", "top_rated" };

    /**
     * This method gets JSON based on the current sort set in MainActivity.
     *
     * @param currentSort A Sort enum passed in to define which API to use.
     * @param context A Context to retrieve a String defined in strings.xml
     * @return A String representing the downloaded JSON movie list.
     * @throws IOException Exception raised if download fails.
     */
    public static String getJsonFromURLWithSort(Sort currentSort, Context context)
            throws IOException {
        Uri uri = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath((currentSort == Sort.POPULAR)? PATHS[0] : PATHS[1])
                .appendQueryParameter(PARAM_API_KEY, context.getString(R.string.api_key))
                .build();

        Log.d(TAG, "URI: " + uri.toString());

        URL url = new URL(uri.toString());
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();

        try {
            InputStream inputStream = conn.getInputStream();

            Scanner scn = new Scanner(inputStream);
            scn.useDelimiter("\\A");

            if (scn.hasNext()) {
                return scn.next();
            }
            else {
                return null;
            }
        }
        finally {
            conn.disconnect();
        }
    }

    /**
     * This method gets the list of trailers using a selected Movie object's id.
     *
     * @param id An int representing the id of a selected Movie.
     * @param context A Context to get a String from strings.xml.
     * @return A String representing the downloaded JSON.
     * @throws IOException Exception raised if download fails.
     */
    public static String getTrailersJsonFromURLWithId(int id, Context context) throws IOException {
        Uri uri = Uri.parse(API_TRAILERS_BASE_URL.replace("{id}", "" + id)).buildUpon()
                .appendQueryParameter("api_key", context.getString(R.string.api_key))
                .build();

        URL url = new URL(uri.toString());
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();

        try {
            InputStream inputStream = conn.getInputStream();

            Scanner scn = new Scanner(inputStream);
            scn.useDelimiter("\\A");

            if (scn.hasNext()) {
                return scn.next();
            }
            else {
                return null;
            }
        }
        finally {
            conn.disconnect();
        }
    }

    /**
     * This method gets the list of reviews using a selected Movie object's id.
     *
     * @param id An int representing the id of a selected Movie.
     * @param context A Context to get a String from strings.xml.
     * @return A String representing the downloaded JSON.
     * @throws IOException Exception raised if download fails.
     */
    public static String getReviewsJsonFromURLWithId(int id, Context context) throws IOException {
        Uri uri = Uri.parse(API_REVIEWS_BASE_URL.replace("{id}", "" + id)).buildUpon()
                .appendQueryParameter("api_key", context.getString(R.string.api_key))
                .build();

        URL url = new URL(uri.toString());
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();

        try {
            InputStream inputStream = conn.getInputStream();

            Scanner scn = new Scanner(inputStream);
            scn.useDelimiter("\\A");

            if (scn.hasNext()) {
                return scn.next();
            }
            else {
                return null;
            }
        }
        finally {
            conn.disconnect();
        }
    }

    /**
     * This method gets a Bitmap from the internet using a defined path.
     *
     * @param thumbnailPath A String representing the location of the image.
     * @return A Bitmap downloaded from the provided location.
     * @throws IOException Exception raised if download fails.
     */
    public static Bitmap downloadThumbnailFromPath(String thumbnailPath) throws IOException {
        Uri uri = Uri.parse(API_THUMBNAIL_342_BASE_URL).buildUpon()
                .appendPath(thumbnailPath)
                .build();

        URL url = new URL(uri.toString());

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();

        try {
            InputStream inputStream = conn.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        }
        finally {
            conn.disconnect();
        }
    }
}
