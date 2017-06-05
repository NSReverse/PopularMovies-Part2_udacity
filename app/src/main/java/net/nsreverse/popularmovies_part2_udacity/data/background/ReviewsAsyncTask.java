package net.nsreverse.popularmovies_part2_udacity.data.background;

import android.content.Context;
import android.os.AsyncTask;

import net.nsreverse.popularmovies_part2_udacity.R;
import net.nsreverse.popularmovies_part2_udacity.data.NetworkUtils;
import net.nsreverse.popularmovies_part2_udacity.data.ParseJsonUtils;
import net.nsreverse.popularmovies_part2_udacity.model.Review;

import org.json.JSONException;

import java.io.IOException;

/**
 * This class downloads and parses JSON containing reviews about a selected movie.
 *
 * @author Robert
 * Created on 6/4/2017.
 */
public class ReviewsAsyncTask extends AsyncTask<Integer, Void, Review[]> {

    @SuppressWarnings("unused")
    private static final String TAG = ReviewsAsyncTask.class.getSimpleName();

    private Delegate mDelegate;
    private Context mContext;

    /**
     * This interface should be implemented by the calling Activity to handle completion events.
     */
    public interface Delegate {
        /**
         * This method handles the new data source to pass to the handler.
         *
         * @param data An array of Review objects to pass back to the handler.
         */
        void taskFinishedWithReviews(Review[] data);

        /**
         * This method is called whenever the task encounters an error.
         *
         * @param message A String representing the error that the task encountered.
         */
        void taskFinishedWithErrorMessage(String message);
    }

    /**
     * This is the basic constructor used to create a new ReviewsAsyncTask object.
     *
     * @param context A Context implementing the Delegate interface.
     */
    public ReviewsAsyncTask(Context context) {
        mDelegate = (Delegate)context;
        mContext = context;
    }

    /**
     * This method executes in the background to download and parse JSON data containing reviews
     * about a selected Movie.
     *
     * @param params Varargs representing the ids of a selected Movie. Only the first is used.
     * @return An array of Review objects representing the reviews of a selected Movie.
     */
    @Override
    protected Review[] doInBackground(Integer... params) {
        Review[] reviews;

        try {
            String json = NetworkUtils.getReviewsJsonFromURLWithId(params[0], mContext);
            reviews = ParseJsonUtils.parseReviewJson(mContext, json);
        }
        catch (IOException ex) {
            if (mDelegate != null) {
                mDelegate.taskFinishedWithErrorMessage(
                        mContext.getString(R.string.error_retrieve_data));
            }

            reviews = new Review[0];
        }
        catch (JSONException ex) {
            if (mDelegate != null) {
                mDelegate.taskFinishedWithErrorMessage(
                        mContext.getString(R.string.error_parse_json));
            }

            reviews = new Review[0];
        }

        return reviews;
    }

    /**
     * This method handles the completion of this task.
     *
     * @param reviews An array of Review objects to pass back to the handler.
     */
    @Override
    protected void onPostExecute(Review[] reviews) {
        if (reviews != null && reviews.length > 0) {
            if (mDelegate != null) {
                mDelegate.taskFinishedWithReviews(reviews);
            }
        }
        else {
            if (mDelegate != null) {
                mDelegate.taskFinishedWithErrorMessage(
                        mContext.getString(R.string.error_no_reviews));
            }
        }
    }
}
