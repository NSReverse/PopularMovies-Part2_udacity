package net.nsreverse.popularmovies_part2_udacity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.nsreverse.popularmovies_part2_udacity.data.background.TrailersAsyncTask;
import net.nsreverse.popularmovies_part2_udacity.model.Trailer;

/**
 * This Activity class displays the trailers of a selected Movie.
 *
 * @author Robert
 * Created on 6/4/2017.
 */
public class TrailersActivity extends AppCompatActivity
                              implements TrailersAsyncTask.Delegate,
                                         TrailersAdapter.TrailerOnClickHandler {

    public static final String KEY_ID = "ID";
    public static final String KEY_THUMB = "THUMBNAIL";

    private TextView mErrorTextView;
    private ImageView mErrorImageView;

    private ProgressBar mProgressBar;

    private TrailersAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @SuppressWarnings("FieldCanBeLocal") private ImageView mThumbImageView;

    /**
     * This method is the main entry point for this Activity.
     *
     * @param savedInstanceState A Bundle representing the saved state when a configuration
     *                           change occurs.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailers);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.activity_trailers));

        mErrorTextView = (TextView)findViewById(R.id.text_view_error);
        mErrorImageView = (ImageView)findViewById(R.id.image_view_sad);

        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar_loading);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view_trailers);
        mAdapter = new TrailersAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        if (getIntent() != null) {
            int id = getIntent().getIntExtra(KEY_ID, -1);
            mProgressBar.setVisibility(View.VISIBLE);

            // https://stackoverflow.com/questions/2795833/check-orientation-on-android-phone
            if (getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE) {
                byte[] thumbnailData = getIntent().getByteArrayExtra(KEY_THUMB);
                Bitmap thumbnail = BitmapFactory.decodeByteArray(
                        thumbnailData, 0, thumbnailData.length);

                mThumbImageView = (ImageView)findViewById(R.id.image_view_thumbnail);
                mThumbImageView.setImageBitmap(thumbnail);
            }

            new TrailersAsyncTask(this).execute(id);
        }
        else {
            showError(true);
        }
    }

    /**
     * This method displays a set of Views shown when an error is encountered.
     *
     * @param show A boolean representing if views for error should be shown.
     */
    private void showError(boolean show) {
        mRecyclerView.setVisibility((show)? View.INVISIBLE : View.VISIBLE);
        mErrorTextView.setVisibility((show)? View.VISIBLE : View.INVISIBLE);
        mErrorImageView.setVisibility((show)? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * This method handles when an item in the ActionBar is selected.
     *
     * @param item A MenuItem object representing the selected item in the ActionBar.
     * @return A boolean representing if the click was handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is a callback for when the parser finishes parsing the JSON containing the
     * data source of trailer information.
     *
     * @param data An array of Trailer objects representing the parsed JSON.
     */
    @Override
    public void taskFinishedWithTrailers(Trailer[] data) {
        mProgressBar.setVisibility(View.INVISIBLE);
        showError(false);

        mAdapter.setTrailerData(data);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * This method is called whenever the parser encounters an error.
     *
     * @param message A String representing the issue the parser encountered.
     */
    @Override
    public void taskFinishedWithErrorMessage(String message) {
        showError(true);
        mProgressBar.setVisibility(View.INVISIBLE);

        System.out.println("ERROR: " + message);
    }

    /**
     * This method handles whenever a Trailer is selected. If YouTube is installed on the device,
     * the video will open in the YouTube app, otherwise it is opened in the web browser.
     *
     * @param selectedTrailer A Trailer object representing the selected Trailer.
     * @param position An int representing the index of the selected row in the RecyclerView.
     */
    @Override
    public void onTrailerSelected(Trailer selectedTrailer, int position) {
        // https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
        // Opens YouTube on device, web browser otherwise. Web browser on emulator due to
        // YouTube app absent.
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.youtube.com/watch?v=" +
                selectedTrailer.getVideoId())));
    }
}
