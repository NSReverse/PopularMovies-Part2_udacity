package net.nsreverse.popularmovies_part2_udacity.data.background;

import android.content.Context;
import android.os.AsyncTask;

import net.nsreverse.popularmovies_part2_udacity.R;
import net.nsreverse.popularmovies_part2_udacity.data.NetworkUtils;
import net.nsreverse.popularmovies_part2_udacity.data.ParseJsonUtils;
import net.nsreverse.popularmovies_part2_udacity.model.Trailer;

import org.json.JSONException;

import java.io.IOException;

/**
 * This class downloads and parses JSON containing trailers about a selected movie.
 *
 * @author Robert
 * Created on 6/4/2017.
 */
public class TrailersAsyncTask extends AsyncTask<Integer, Void, Trailer[]> {

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
        void taskFinishedWithTrailers(Trailer[] data);

        /**
         * This method is called whenever the task encounters an error.
         *
         * @param message A String representing the error that the task encountered.
         */
        void taskFinishedWithErrorMessage(String message);
    }

    /**
     * This is the basic constructor used to create a new TrailersAsyncTask object.
     *
     * @param context A Context implementing the Delegate interface.
     */
    public TrailersAsyncTask(Context context) {
        mDelegate = (Delegate)context;
        mContext = context;
    }

    /**
     * This method executes in the background to download and parse JSON data containing trailers
     * about a selected Movie.
     *
     * @param params Varargs representing the ids of a selected Movie. Only the first is used.
     * @return An array of Trailer objects representing the trailers of a selected Movie.
     */
    @Override
    protected Trailer[] doInBackground(Integer... params) {
        Trailer[] trailers;

        try {
            String json = NetworkUtils.getTrailersJsonFromURLWithId(params[0], mContext);
            trailers = ParseJsonUtils.parseTrailerJson(mContext, json);
        }
        catch (IOException ex) {
            if (mDelegate != null) {
                mDelegate.taskFinishedWithErrorMessage(
                        mContext.getString(R.string.error_retrieve_data));
            }

            return new Trailer[0];
        }
        catch (JSONException ex) {
            if (mDelegate != null) {
                mDelegate.taskFinishedWithErrorMessage(
                        mContext.getString(R.string.error_parse_json));
            }

            return new Trailer[0];
        }

        return trailers;
    }

    /**
     * This method handles the completion of this task.
     *
     * @param trailers An array of Review objects to pass back to the handler.
     */
    @Override
    protected void onPostExecute(Trailer[] trailers) {
        if (trailers != null && trailers.length > 0) {
            if (mDelegate != null) {
                mDelegate.taskFinishedWithTrailers(trailers);
            }
        }
        else {
            if (mDelegate != null) {
                mDelegate.taskFinishedWithErrorMessage(
                        mContext.getString(R.string.error_no_trailers));
            }
        }
    }
}
