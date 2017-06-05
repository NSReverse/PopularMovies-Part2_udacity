package net.nsreverse.popularmovies_part2_udacity.data.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import net.nsreverse.popularmovies_part2_udacity.data.ParseJsonUtils;
import net.nsreverse.popularmovies_part2_udacity.model.Movie;

import org.json.JSONException;

/**
 * This class parses the result of MoviesAsyncTask. The reason that this is separate is for the
 * modularity of parsing previously downloaded JSON.
 *
 * @author Robert
 * Created on 6/3/2017.
 */
public class ParseJsonTask extends AsyncTask<String, Void, Movie[]> {

    private static final String TAG = ParseJsonTask.class.getSimpleName();

    private Delegate mDelegate;
    private Context mContext;

    /**
     * This interface should be implemented to handle the completion events of this task.
     */
    public interface Delegate {
        /**
         * This callback method is called when the task finishes parsing data.
         *
         * @param movies An array of Movie objects from the parsed JSON.
         */
        void taskFinishedWithArray(Movie[] movies);

        /**
         * This callback method is called when the task encounters an error.
         *
         * @param error A String representing the cause of this task's failure.
         */
        void taskFinishedWithErrorMessage(String error);
    }

    /**
     * This constructor is the basis for creating a new ParseJsonTask.
     *
     * @param context A Context that implements the Delegate interface.
     */
    public ParseJsonTask(Context context) {
        mDelegate = (Delegate)context;
        mContext = context;
    }

    /**
     * This method runs in the background to parse desired data.
     *
     * @param params A String passed in representing the JSON data to be parsed.
     * @return An array of Movie objects.
     */
    @Override
    protected Movie[] doInBackground(String... params) {
        Movie[] movies;

        try {
            movies = ParseJsonUtils.parseJson(mContext, params[0]);
        }
        catch (JSONException ex) {
            movies = null;
        }

        return movies;
    }

    /**
     * This method handles the parsing of the JSON into an array of Movie objects to pass to the
     * handler.
     *
     * @param movies An array of Movies representing the parsed JSON.
     */
    @Override
    protected void onPostExecute(Movie[] movies) {
        if (mDelegate != null) {
            if (movies != null) {
                mDelegate.taskFinishedWithArray(movies);
            }
            else {
                mDelegate.taskFinishedWithErrorMessage("Unable to parse JSON.");
            }
        }
        else {
            Log.e(TAG, "Delegate not set for ParseJsonTask.");
        }
    }
}
