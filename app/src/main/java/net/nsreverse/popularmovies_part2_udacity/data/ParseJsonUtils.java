package net.nsreverse.popularmovies_part2_udacity.data;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import net.nsreverse.popularmovies_part2_udacity.R;
import net.nsreverse.popularmovies_part2_udacity.model.Movie;
import net.nsreverse.popularmovies_part2_udacity.model.Review;
import net.nsreverse.popularmovies_part2_udacity.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * This class parses the JSON found in MainActivity, ReviewsActivity, and TrailersActivity.
 *
 * @author Robert
 * Created on 6/3/2017.
 */
public class ParseJsonUtils {
    private static final String TAG = ParseJsonUtils.class.getSimpleName();

    /**
     * This method parses the JSON for the main Movie list in MainActivity.
     *
     * @param context This Context is used to retrieve a broken image resource if parsing fails.
     * @param json A String representing the raw JSON data.
     * @return An array of Movie objects representing the parsed JSON data.
     * @throws JSONException Exception raised if parsing fails.
     */
    public static Movie[] parseJson(Context context, String json) throws JSONException {
        JSONObject baseObject = new JSONObject(json);
        JSONArray resultArray = baseObject.getJSONArray("results");

        Movie[] movies = new Movie[resultArray.length()];

        for (int i = 0; i < movies.length; i++) {
            JSONObject currentResult = resultArray.getJSONObject(i);

            Movie currentMovie = new Movie();
            currentMovie.setTitle(currentResult.getString("title"));
            try {
                currentMovie.setThumbnail(NetworkUtils.downloadThumbnailFromPath(
                        currentResult.getString("poster_path").replace("/", "")
                ));
            }
            catch (IOException ex) {
                // Catching here makes it to where it doesn't stop the entire load if one image fails.
                currentMovie.setThumbnail(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_image_black_24dp));
                Log.d(TAG, "Unable to retrieve image resource: " + ex.getMessage());
            }
            currentMovie.setPlotSynopsis(currentResult.getString("overview"));
            currentMovie.setReleaseDate(currentResult.getString("release_date"));
            currentMovie.setVoteAverage(currentResult.getString("vote_average"));
            currentMovie.setThumbnailAddress(
                    currentResult.getString("poster_path").replace("/", "")
            );
            currentMovie.setBackdropAddress(currentResult.getString("backdrop_path"));
            try {
                currentMovie.setBackdrop(NetworkUtils.downloadThumbnailFromPath(
                        currentMovie.getBackdropAddress().replace("/", "")
                ));
            }
            catch (IOException ex) {
                currentMovie.setBackdrop(null);
                Log.d(TAG, "Unable to retrieve image resource: " +
                        currentMovie.getBackdropAddress());
            }
            currentMovie.setTmdbId(currentResult.getInt("id"));

            movies[i] = currentMovie;
        }

        return movies;
    }

    /**
     * This method parses the JSON for the Review list in ReviewsActivity.
     *
     * @param context Unused.
     * @param json A String representing the raw JSON data.
     * @return An array of Review objects representing the parsed JSON data.
     * @throws JSONException Exception raised if parsing fails.
     */
    public static Review[] parseReviewJson(Context context, String json) throws JSONException {
        JSONObject baseObject = new JSONObject(json);
        JSONArray resultArray = baseObject.getJSONArray("results");

        Review[] reviews = new Review[resultArray.length()];

        for (int i = 0; i < reviews.length; i++) {
            JSONObject currentResult = resultArray.getJSONObject(i);

            Review currentReview = new Review();
            currentReview.setReviewer(currentResult.getString("author"));
            currentReview.setContent(currentResult.getString("content"));

            reviews[i] = currentReview;
        }

        return reviews;
    }

    /**
     * This method parses the JSON for the Trailer list in TrailersActivity.
     *
     * @param context Unused
     * @param json A String representing the raw JSON data.
     * @return An array of Trailer objects representing the parsed JSON data.
     * @throws JSONException Exception raised if parsing fails.
     */
    public static Trailer[] parseTrailerJson(Context context, String json) throws JSONException {
        JSONObject baseObject = new JSONObject(json);
        JSONArray resultArray = baseObject.getJSONArray("results");

        Trailer[] trailers = new Trailer[resultArray.length()];

        for (int i = 0; i < trailers.length; i++) {
            JSONObject currentResult = resultArray.getJSONObject(i);

            Trailer currentTrailer = new Trailer();
            currentTrailer.setName(currentResult.getString("name"));
            currentTrailer.setType(currentResult.getString("type"));
            currentTrailer.setVideoId(currentResult.getString("key"));

            if (currentResult.getString("site").equalsIgnoreCase("youtube")) {
                currentTrailer.setIsYoutube(true);
            }
            else {
                currentTrailer.setIsYoutube(false);
            }

            trailers[i] = currentTrailer;
        }

        return trailers;
    }
}
