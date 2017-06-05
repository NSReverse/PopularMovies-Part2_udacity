package net.nsreverse.popularmovies_part2_udacity.data.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import net.nsreverse.popularmovies_part2_udacity.R;
import net.nsreverse.popularmovies_part2_udacity.data.NetworkUtils;

/**
 * This class handles the downloading of Movie JSON data from themoviedb.org
 *
 * @author Robert
 */
public class MoviesAsyncTask extends AsyncTask<NetworkUtils.Sort, Void, String> {

    private Delegate mDelegate;
    private Context mContext;

    /**
     * This interface should be implemented to handle the completion events of this task.
     */
    public interface Delegate {

        /**
         * This callback method is called when the task finishes downloading data.
         *
         * @param json A String representing the downloaded JSON data.
         */
        void taskFinishedWithJson(String json);

        /**
         * This callback method is called when the task encounters an error.
         *
         * @param error A String representing the cause of this task's failure.
         */
        void taskFinishedWithErrorMessage(String error);
    }

    /**
     * This constructor is the basis for creating a new MoviesAsyncTask.
     *
     * @param context A Context that implements the Delegate interface.
     */
    public MoviesAsyncTask(Context context) {
        mDelegate = (Delegate)context;
        mContext = context;
    }

    /**
     * This method runs in the background to retrieve desired data.
     *
     * @param params A Sort enum passed in to determine which api link to use. Only the first
     *               of the varargs is used.
     * @return An String representing the JSON that was downloaded.
     */
    @Override
    protected String doInBackground(NetworkUtils.Sort... params) {
        String json;

        try {
            json = NetworkUtils.getJsonFromURLWithSort(params[0], mContext);
        }
        catch (Exception ex) {
            // General Exception as the basic handling is identical.
            Log.d(MoviesAsyncTask.class.getSimpleName(),
                    mContext.getString(R.string.error_load_data_detail) + ex.getMessage());

            json = null;
        }

        return json;
    }

    /**
     * This method transfers the newly downloaded String data to it's handler.
     *
     * @param data A String representing the downloaded JSON data.
     */
    @Override
    protected void onPostExecute(String data) {
        if (data != null) {
            mDelegate.taskFinishedWithJson(data);
        }
        else {
            mDelegate.taskFinishedWithErrorMessage("JSON is null.");
        }
    }
}