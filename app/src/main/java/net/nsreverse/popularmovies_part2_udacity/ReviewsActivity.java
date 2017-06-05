package net.nsreverse.popularmovies_part2_udacity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.nsreverse.popularmovies_part2_udacity.data.background.ReviewsAsyncTask;
import net.nsreverse.popularmovies_part2_udacity.model.Review;

/**
 * This Activity class displays the reviews of a selected Movie.
 *
 * @author Robert
 * Created on 6/4/2017.
 */
public class ReviewsActivity extends AppCompatActivity
                             implements ReviewsAsyncTask.Delegate {

    public static final String KEY_ID = "ID";
    public static final String KEY_THUMB = "THUMB";

    private TextView mErrorTextView;
    private ImageView mErrorImageView;

    private ProgressBar mProgressBar;

    private ReviewsAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private ImageView mThumbImageView;

    /**
     * This method is the main entry point for this Activity.
     *
     * @param savedInstanceState A Bundle representing persisted state information from a
     *                           configuration change.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.activity_reviews));

        mErrorTextView = (TextView)findViewById(R.id.text_view_error);
        mErrorImageView = (ImageView)findViewById(R.id.image_view_sad);

        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar_loading);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view_reviews);
        mAdapter = new ReviewsAdapter();

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

            new ReviewsAsyncTask(this).execute(id);
        }
        else {
            showError(true);
        }
    }

    /**
     * This method is a callback for when the JSON containing reviews information is parsed. The
     * parsed information is then passed to the adapter for this Activity's RecyclerView.
     *
     * @param data An array of Review objects representing the parsed JSON.
     */
    @Override
    public void taskFinishedWithReviews(Review[] data) {
        mProgressBar.setVisibility(View.INVISIBLE);
        showError(false);

        mAdapter.setReviewData(data);
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
}
