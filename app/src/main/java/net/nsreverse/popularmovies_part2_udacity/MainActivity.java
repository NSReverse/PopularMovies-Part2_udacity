package net.nsreverse.popularmovies_part2_udacity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.nsreverse.popularmovies_part2_udacity.data.NetworkUtils.Sort;
import net.nsreverse.popularmovies_part2_udacity.data.RuntimeCache;
import net.nsreverse.popularmovies_part2_udacity.data.background.MoviesAsyncTask;
import net.nsreverse.popularmovies_part2_udacity.data.background.ParseJsonTask;
import net.nsreverse.popularmovies_part2_udacity.data.provider.FavoritesContract;
import net.nsreverse.popularmovies_part2_udacity.model.Movie;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class is the main entry point for the application.
 *
 * @author Robert
 * Created on 6/2/2017.
 */
public class MainActivity extends AppCompatActivity
                          implements MoviesAsyncTask.Delegate,
                                     ParseJsonTask.Delegate,
                                     MovieAdapter.MovieAdapterOnClickHandler,
                                     LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Normal components
    private ProgressBar mProgressBar;
    private TextView mErrorTextView;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    // Tablet components
    private ImageView mThumbImageView;
    private TextView mTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mRatingTextView;
    private TextView mSynopsisTextView;
    private LinearLayout mLayoutDetailButtons;
    private FloatingActionButton mFab;

    // Other runtime Activity members
    private Toast mToast;
    private Sort mCurrentSort;

    private String mPopularJson;
    private String mTopRatedJson;

    private int mSelectedIndex;
    private boolean mIsTabletFormFactor = false;

    private static final int TASK_LOADER_ID = 0;

    private AsyncTask currentTask;

    private Cursor currentCursor;

    private Snackbar mSnackbar;

    // Constants for configuration changes
    private static final String INSTANCE_STATE_POPULAR_JSON_KEY = "popular_json";
    private static final String INSTANCE_STATE_TOP_RATED_JSON_KEY = "top_rated_json";
    private static final String INSTANCE_STATE_SORT_CRITERIA_KEY = "sort";
    private static final String INSTANCE_STATE_PREVIOUS_INDEX = "selected_index";

    private static final int SORT_CRITERIA_POPULAR_ID = 100;
    private static final int SORT_CRITERIA_TOP_RATED_ID = 101;
    private static final int SORT_CRITERIA_FAVORITES_ID = 102;

    private boolean isFavorite;

    /**
     * This method is the main entry point for the Activity.
     *
     * @param savedInstanceState A Bundle representing data persisted through configuration
     *                           change.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner mSortSpinner = (Spinner)findViewById(R.id.spinner_sort);

        String[] spinnerItems = {
                getString(R.string.spinner_popular),
                getString(R.string.spinner_top_rated),
                getString(R.string.spinner_favorites)
        };
        mSortSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, spinnerItems));
        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mCurrentSort = Sort.POPULAR;

                    if (RuntimeCache.popularMovies != null) {
                        taskFinishedWithArray(RuntimeCache.popularMovies);
                    }
                    else {
                        refreshDataSource();
                    }
                }
                else if (position == 1) {
                    mCurrentSort = Sort.TOP_RATED;

                    if (RuntimeCache.topRatedMovies != null) {
                        taskFinishedWithArray(RuntimeCache.topRatedMovies);
                    }
                    else {
                        refreshDataSource();
                    }
                }
                else {
                    mCurrentSort = Sort.FAVORITES;
                    refreshDataSource();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ignore
            }
        });

        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar_loading);
        mErrorTextView = (TextView)findViewById(R.id.text_view_error);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view_movies);

        mThumbImageView = (ImageView)findViewById(R.id.image_view_thumbnail);

        if (mThumbImageView != null) {
            // Tablet detected.
            mIsTabletFormFactor = true;

            mTitleTextView = (TextView)findViewById(R.id.text_view_title);
            mReleaseDateTextView = (TextView)findViewById(R.id.text_view_release_date);
            mRatingTextView = (TextView)findViewById(R.id.text_view_rating);
            mSynopsisTextView = (TextView)findViewById(R.id.text_view_synopsis);
            mLayoutDetailButtons = (LinearLayout)findViewById(R.id.layout_detail_buttons);
            mFab = (FloatingActionButton)findViewById(R.id.fab);
        }

        System.out.println("Point 1");
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(INSTANCE_STATE_POPULAR_JSON_KEY)) {
                mPopularJson = savedInstanceState.getString(INSTANCE_STATE_POPULAR_JSON_KEY);
            }

            if (savedInstanceState.containsKey(INSTANCE_STATE_TOP_RATED_JSON_KEY)) {
                mTopRatedJson = savedInstanceState.getString(INSTANCE_STATE_TOP_RATED_JSON_KEY);
            }

            if (savedInstanceState.containsKey(INSTANCE_STATE_SORT_CRITERIA_KEY)) {
                System.out.println("Point 2");
                int sortCriteria = savedInstanceState.getInt(INSTANCE_STATE_SORT_CRITERIA_KEY);

                switch (sortCriteria) {
                    case SORT_CRITERIA_POPULAR_ID:
                        mCurrentSort = Sort.POPULAR;
                        System.out.println("Point 4");
                        if (RuntimeCache.popularMovies != null) {
                            System.out.println("Point 5");
                            taskFinishedWithArray(RuntimeCache.popularMovies);
                        }
                        else {
                            System.out.println("Point 6");
                            reloadDataSource();
                        }

                        break;

                    case SORT_CRITERIA_TOP_RATED_ID:
                        System.out.println("Point 7");
                        mCurrentSort = Sort.TOP_RATED;

                        if (RuntimeCache.topRatedMovies != null) {
                            System.out.println("Point 8");
                            taskFinishedWithArray(RuntimeCache.topRatedMovies);
                        }
                        else {
                            System.out.println("Point 9");
                            reloadDataSource();
                        }

                        break;

                    case SORT_CRITERIA_FAVORITES_ID:
                        mCurrentSort = Sort.FAVORITES;
                        reloadDataSource();
                        break;

                    default:
                        System.out.println("Point 10");
                        Log.e(TAG, "Sort criteria ID is invalid: " + sortCriteria);
                        mCurrentSort = Sort.POPULAR;
                        reloadDataSource();
                        break;
                }
            }

            if (savedInstanceState.containsKey(INSTANCE_STATE_PREVIOUS_INDEX)) {
                mSelectedIndex = savedInstanceState.getInt(INSTANCE_STATE_PREVIOUS_INDEX);
            }
            else {
                mSelectedIndex = 0;
            }
        }
        else {
            mCurrentSort = Sort.POPULAR;
            refreshDataSource();
            mSelectedIndex = 0;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, LinearLayoutManager.VERTICAL);

        // https://stackoverflow.com/questions/2795833/check-orientation-on-android-phone
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT &&
                !mIsTabletFormFactor) {
            layoutManager.setSpanCount(2);
        }
        else {
            if (!mIsTabletFormFactor) {
                layoutManager.setSpanCount(4);
            }
            else {
                layoutManager.setSpanCount(2);
            }
        }

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mProgressBar.setVisibility(View.INVISIBLE);

        showMovieRecyclerView(true);
        hideTabletDetail(true);
    }

    /**
     * This method disables and enables either the RecyclerView or the error message TextView
     * depending on the boolean passed to it.
     *
     * @param visible A boolean representing if the RecyclerView should be hidden and the
     *                error TextView should be displayed.
     */
    private void showMovieRecyclerView(boolean visible) {
        mRecyclerView.setVisibility((visible)? View.VISIBLE : View.INVISIBLE);
        mErrorTextView.setVisibility((visible)? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * This method initiates data reloading. If downloaded data exists previously, it skips
     * the download and goes straight to parsing data.
     */
    private void refreshDataSource() {
        hideTabletDetail(true);

        mProgressBar.setVisibility(View.VISIBLE);

        if (mCurrentSort == Sort.POPULAR || mCurrentSort == Sort.TOP_RATED) {
            if (mCurrentSort == Sort.POPULAR) {
                if (mPopularJson == null) {
                    new MoviesAsyncTask(this).execute(mCurrentSort);
                }
                else {
                    reloadDataSource();
                }
            }
            else {
                if (mTopRatedJson == null) {
                    new MoviesAsyncTask(this).execute(mCurrentSort);
                }
                else {
                    reloadDataSource();
                }
            }
        }
        else {
            // Favorites
            reloadDataSource();
        }
    }

    /**
     * This method parses downloaded information into an Adapter for use with the RecyclerView
     * in this Activity.
     */
    private void reloadDataSource() {
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mProgressBar.setVisibility(View.VISIBLE);

        String selectedJson;

        if (mCurrentSort == Sort.POPULAR || mCurrentSort == Sort.TOP_RATED) {
            switch (mCurrentSort) {
                case POPULAR:
                    selectedJson = mPopularJson;
                    break;

                case TOP_RATED:
                    selectedJson = mTopRatedJson;
                    break;

                default:
                    selectedJson = "";
                    break;
            }

            if (selectedJson == null || TextUtils.isEmpty(selectedJson)) return;

            if (currentTask != null) {
                currentTask.cancel(true);
                currentTask = null;
            }

            currentTask = new ParseJsonTask(this).execute(selectedJson);
        }
        else if (mCurrentSort == Sort.FAVORITES) {
            // Favorites
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
        }
    }

    /**
     * This method hides the detail layout on a tablet form factor.
     *
     * @param shouldHide A boolean representing if the detail view should hide.
     */
    private void hideTabletDetail(boolean shouldHide) {
        if (mIsTabletFormFactor) {
            mThumbImageView.setVisibility(shouldHide? View.INVISIBLE : View.VISIBLE);
            mTitleTextView.setVisibility(shouldHide? View.INVISIBLE : View.VISIBLE);
            mReleaseDateTextView.setVisibility(shouldHide? View.INVISIBLE : View.VISIBLE);
            mRatingTextView.setVisibility(shouldHide? View.INVISIBLE : View.VISIBLE);
            mSynopsisTextView.setVisibility(shouldHide? View.INVISIBLE : View.VISIBLE);
            mLayoutDetailButtons.setVisibility(shouldHide? View.INVISIBLE : View.VISIBLE);
            mFab.setVisibility(shouldHide? View.INVISIBLE : View.VISIBLE);
        }
    }

    /**
     * This method is called after JSON has finished downloading and then calls methods for
     * parsing.
     *
     * @param json A String representing the downloaded JSON data.
     */
    @Override
    public void taskFinishedWithJson(String json) {
        switch (mCurrentSort) {
            case POPULAR:
                mPopularJson = json;
                break;

            case TOP_RATED:
                mTopRatedJson = json;
                break;
        }

        reloadDataSource();
    }

    /**
     * This method is called after JSON has finished parsing and sets the new Adapter to the
     * RecyclerView.
     *
     * @param movies An array of Movie objects representing the parsed JSON data source.
     */
    @Override
    public void taskFinishedWithArray(Movie[] movies) {
        if (movies != null) {
            if (mCurrentSort == Sort.POPULAR) {
                RuntimeCache.popularMovies = movies;
            }
            else if (mCurrentSort == Sort.TOP_RATED) {
                RuntimeCache.topRatedMovies = movies;
            }
        }

        if (mMovieAdapter == null) {
            mMovieAdapter = new MovieAdapter(this);
            mRecyclerView.setAdapter(mMovieAdapter);
        }

        mMovieAdapter.setMovieData(movies);

        if (movies != null && movies.length > 0) {
            if (mSelectedIndex < 0 || mSelectedIndex >= movies.length) {
                mSelectedIndex = 0;
            }

            if (mIsTabletFormFactor) {
                onMovieClick(movies[mSelectedIndex]);
            }

            hideTabletDetail(false);
        }
        else {
            hideTabletDetail(true);
        }

        mProgressBar.setVisibility(View.INVISIBLE);

        showMovieRecyclerView(true);
    }

    /**
     * This method is called whenever MoviesAsyncTask or ParseJsonTask has failed.
     *
     * @param error A String representing the error passed from MoviesAsyncTask or ParseJsonTask.
     */
    @Override
    public void taskFinishedWithErrorMessage(String error) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT);
        mToast.show();

        showMovieRecyclerView(false);
    }

    /**
     * This method is called whenever a Movie is selected from the RecyclerView.
     *
     * @param selectedMovie A Movie object representing the selected movie.
     */
    @Override
    public void onMovieClick(final Movie selectedMovie) {
        if (mIsTabletFormFactor) {
            mThumbImageView.setImageBitmap(selectedMovie.getThumbnail());
            mTitleTextView.setText(selectedMovie.getTitle());
            mReleaseDateTextView.setText("Released: ".concat(selectedMovie.getReleaseDate()));
            mRatingTextView.setText("Rating: ".concat(selectedMovie.getVoteAverage()));
            mSynopsisTextView.setText(selectedMovie.getPlotSynopsis());

            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isFavorite) {
                        ContentValues values = new ContentValues();
                        values.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID,
                                selectedMovie.getTmdbId());
                        values.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE,
                                selectedMovie.getReleaseDate());
                        values.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE,
                                selectedMovie.getTitle());
                        values.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_AVG,
                                selectedMovie.getVoteAverage());
                        values.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_SYNOPSIS,
                                selectedMovie.getPlotSynopsis());
                        values.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_THUMB_NAME,
                                selectedMovie.getThumbnailAddress());
                        values.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_BACKDROP_NAME,
                                selectedMovie.getBackdropAddress());

                        Uri insertedUri = getContentResolver().insert(
                                FavoritesContract.FavoritesEntry.CONTENT_URI, values);

                        File filesDir = getFilesDir();
                        String imagePath = filesDir.getAbsolutePath().concat("/" +
                                selectedMovie.getThumbnailAddress().replace("/", "") + ".png");
                        String backdropPath = filesDir.getAbsolutePath().concat("/" +
                                selectedMovie.getBackdropAddress().replace("/", "") + ".png");

                        // https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
                        File imageFile = new File(imagePath);
                        FileOutputStream outputStream = null;

                        File backdropFile = new File(backdropPath);
                        FileOutputStream backOutputStream = null;

                        try {
                            outputStream = new FileOutputStream(imageFile);
                            selectedMovie.getThumbnail()
                                    .compress(Bitmap.CompressFormat.PNG, 50, outputStream);

                            backOutputStream = new FileOutputStream(backdropFile);
                            selectedMovie.getBackdrop()
                                    .compress(Bitmap.CompressFormat.PNG, 50, backOutputStream);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        finally {
                            try {
                                if (outputStream != null) {
                                    outputStream.close();
                                }

                                if (backOutputStream != null) {
                                    backOutputStream.close();
                                }
                            }
                            catch (Exception ignored) {  }
                        }

                        mSnackbar = Snackbar.make(
                                v,
                                selectedMovie.getTitle() + " added to favorites",
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        mSnackbar.show();

                        mFab.setImageResource(R.drawable.ic_star_white_24dp);

                        if (insertedUri != null) {
                            selectedMovie.setRowId(
                                    Integer.parseInt(insertedUri.getPathSegments().get(1)));
                            getContentResolver().notifyChange(
                                    FavoritesContract.FavoritesEntry.CONTENT_URI, null);

                            getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null,
                                    MainActivity.this);
                        }

                        isFavorite = true;
                    }
                    else {
                        int rowsDeleted = 0;

                        if (selectedMovie.getRowId() != -1) {
                            rowsDeleted =
                                getContentResolver().delete(
                                        FavoritesContract.FavoritesEntry.CONTENT_URI.buildUpon()
                                                .appendPath(Integer.toString(
                                                        selectedMovie.getRowId())).build(),
                                        null,
                                        null
                                );
                        }

                        getContentResolver().notifyChange(
                                FavoritesContract.FavoritesEntry.CONTENT_URI, null);

                        if (rowsDeleted > 0) {
                            Snackbar.make(v, "Video removed from favorites", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        else {
                            Snackbar.make(v, "Failed to remove from favorites.",
                                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }

                        mFab.setImageResource(R.drawable.ic_star_border_white_24dp);

                        if (mCurrentSort == Sort.FAVORITES) {
                            mThumbImageView.setImageBitmap(null);
                            mReleaseDateTextView.setText("");
                            mRatingTextView.setText("");
                            mSynopsisTextView.setText("");
                            mTitleTextView.setText(getString(R.string.error_select_video));
                        }

                        isFavorite = false;
                    }

                    getSupportLoaderManager().restartLoader(
                            TASK_LOADER_ID, null, MainActivity.this);
                }
            });

            // Check if selected movie exists in favorites.
            if (currentCursor != null && currentCursor.getCount() > 0) {
                currentCursor.moveToFirst();

                isFavorite = false;

                do {
                    if (selectedMovie.getTmdbId() == currentCursor.getInt(
                            currentCursor.getColumnIndex(
                                    FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID))) {
                        isFavorite = true;
                    }
                }
                while (currentCursor.moveToNext());
            }
            else {
                isFavorite = false;
            }

            if (isFavorite) {
                mFab.setImageResource(R.drawable.ic_star_white_24dp);
            }
            else {
                mFab.setImageResource(R.drawable.ic_star_border_white_24dp);
            }

            final ByteArrayOutputStream thumbOutputStream = new ByteArrayOutputStream();
            selectedMovie.getThumbnail().compress(Bitmap.CompressFormat.PNG, 0, thumbOutputStream);

            findViewById(R.id.button_trailers).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, TrailersActivity.class);
                    intent.putExtra(TrailersActivity.KEY_THUMB, thumbOutputStream.toByteArray());
                    intent.putExtra(TrailersActivity.KEY_ID, selectedMovie.getTmdbId());
                    startActivity(intent);
                }
            });

            findViewById(R.id.button_reviews).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ReviewsActivity.class);
                    intent.putExtra(ReviewsActivity.KEY_THUMB, thumbOutputStream.toByteArray());
                    intent.putExtra(ReviewsActivity.KEY_ID, selectedMovie.getTmdbId());
                    startActivity(intent);
                }
            });
        }
        else {
            // https://stackoverflow.com/questions/4352172/how-do-you-pass-images-bitmaps-between-android-activities-using-bundles/7890405#7890405
            ByteArrayOutputStream thumbOutputStream = new ByteArrayOutputStream();
            selectedMovie.getThumbnail().compress(Bitmap.CompressFormat.PNG, 0, thumbOutputStream);

            ByteArrayOutputStream backdropOutputStream = new ByteArrayOutputStream();
            selectedMovie.getBackdrop().compress(Bitmap.CompressFormat.JPEG, 35,
                    backdropOutputStream);

            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.KEY_THUMBNAIL, thumbOutputStream.toByteArray());
            intent.putExtra(MovieDetailActivity.KEY_BACKDROP, backdropOutputStream.toByteArray());
            intent.putExtra(MovieDetailActivity.KEY_TITLE, selectedMovie.getTitle());
            intent.putExtra(MovieDetailActivity.KEY_PLOT_SYNOPSIS, selectedMovie.getPlotSynopsis());
            intent.putExtra(MovieDetailActivity.KEY_RELEASE_DATE, selectedMovie.getReleaseDate());
            intent.putExtra(MovieDetailActivity.KEY_VOTE_AVERAGE, selectedMovie.getVoteAverage());
            intent.putExtra(MovieDetailActivity.KEY_TMDB_ID, selectedMovie.getTmdbId());
            intent.putExtra(MovieDetailActivity.KEY_THUMBNAIL_NAME,
                    selectedMovie.getThumbnailAddress());
            intent.putExtra(MovieDetailActivity.KEY_BACKDROP_NAME,
                    selectedMovie.getBackdropAddress());

            // Check if selected movie exists in favorites.
            if (currentCursor != null && currentCursor.getCount() > 0 &&
                    !selectedMovie.getFavorite()) {
                currentCursor.moveToFirst();

                boolean hasFavorite = false;

                do {
                    if (selectedMovie.getTmdbId() == currentCursor.getInt(
                            currentCursor.getColumnIndex(
                                    FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID))) {
                        hasFavorite = true;

                        intent.putExtra(MovieDetailActivity.KEY_ID, currentCursor.getInt(
                                currentCursor.getColumnIndex(FavoritesContract.FavoritesEntry._ID)
                        ));
                    }
                }
                while (currentCursor.moveToNext());

                intent.putExtra(MovieDetailActivity.KEY_IS_FAVORITE, hasFavorite);
            }
            else {
                intent.putExtra(MovieDetailActivity.KEY_IS_FAVORITE, selectedMovie.getFavorite());
            }

            if (selectedMovie.getRowId() != -1) {
                intent.putExtra(MovieDetailActivity.KEY_ID, selectedMovie.getRowId());
            }

            startActivity(intent);
        }
    }

    /**
     * This method handles setting the state of the last selected position.
     *
     * @param position An int representing the last selected movie.
     */
    @Override
    public void positionSelected(int position) {
        mSelectedIndex = position;
    }

    /**
     * This method is called to persist state information when a configuration change occurs.
     *
     * @param outState A Bundle containing the state information.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int sortCriteria;

        switch (mCurrentSort) {
            case TOP_RATED:
                sortCriteria = SORT_CRITERIA_TOP_RATED_ID;
                break;

            case FAVORITES:
                sortCriteria = SORT_CRITERIA_FAVORITES_ID;
                break;

            case POPULAR:
            default:
                sortCriteria = SORT_CRITERIA_POPULAR_ID;
                break;
        }

        outState.putString(INSTANCE_STATE_POPULAR_JSON_KEY, mPopularJson);
        outState.putString(INSTANCE_STATE_TOP_RATED_JSON_KEY, mTopRatedJson);
        outState.putInt(INSTANCE_STATE_SORT_CRITERIA_KEY, sortCriteria);
        outState.putInt(INSTANCE_STATE_PREVIOUS_INDEX, mSelectedIndex);
    }

    /**
     * This method creates an async Loader containing information from a database.
     *
     * @param id The ID of the Loader.
     * @param args A Bundle representing arguments.
     * @return A Loader for retrieving a Cursor.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mCursor = null;

            @Override
            protected void onStartLoading() {
                if (currentTask != null) {
                    currentTask.cancel(true);
                    currentTask = null;
                }

                if (mCursor != null) {
                    deliverResult(mCursor);
                }
                else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                }
                catch (Exception ex) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    ex.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                super.deliverResult(data);
            }
        };
    }

    /**
     * This method is called whenever a Loader finishes its tasks.
     *
     * @param loader The Loader that has called this callback.
     * @param data A Cursor containing the data in the database.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mCurrentSort == Sort.FAVORITES) {
            DatabaseUtils.dumpCursor(data);

            Movie[] movies = new Movie[data.getCount()];

            if (data.moveToFirst()) {
                int index = 0;

                do {
                    Movie currentMovie = new Movie();
                    currentMovie.setRowId(data.getInt(data.getColumnIndex(
                            FavoritesContract.FavoritesEntry._ID)));
                    currentMovie.setTmdbId(data.getInt(data.getColumnIndex(
                            FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID)));
                    currentMovie.setTitle(data.getString(data.getColumnIndex(
                            FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE)));
                    currentMovie.setPlotSynopsis(data.getString(data.getColumnIndex(
                            FavoritesContract.FavoritesEntry.COLUMN_MOVIE_SYNOPSIS)));
                    currentMovie.setReleaseDate(data.getString(data.getColumnIndex(
                            FavoritesContract.FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE)));
                    currentMovie.setVoteAverage(data.getString(data.getColumnIndex(
                            FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_AVG)));
                    currentMovie.setThumbnailAddress(data.getString(data.getColumnIndex(
                            FavoritesContract.FavoritesEntry.COLUMN_MOVIE_THUMB_NAME)));
                    currentMovie.setBackdropAddress(data.getString(data.getColumnIndex(
                            FavoritesContract.FavoritesEntry.COLUMN_MOVIE_BACKDROP_NAME)));
                    currentMovie.setFavorite(true);

                    try {
                        File filesDir = getFilesDir();
                        String imagePath = filesDir.getAbsolutePath().concat("/" +
                                currentMovie.getThumbnailAddress().replace("/", "") + ".png");
                        String backdropPath = filesDir.getAbsolutePath().concat("/" +
                                currentMovie.getBackdropAddress().replace("/", "") + ".png");

                        FileInputStream inputStream = new FileInputStream(new File(imagePath));
                        Bitmap thumbBitmap = BitmapFactory.decodeStream(inputStream);

                        FileInputStream backInputStream = new FileInputStream(
                                new File(backdropPath));
                        Bitmap backdropBitmap = BitmapFactory.decodeStream(backInputStream);

                        currentMovie.setThumbnail(thumbBitmap);
                        currentMovie.setBackdrop(backdropBitmap);

                        inputStream.close();
                    }
                    catch (FileNotFoundException ex) {
                        Log.d(TAG, "Image not found." + ex.getMessage());
                    }
                    catch (IOException ex) {
                        Log.d(TAG, "IO Exception caught.");
                    }
                    catch (Exception ex) {
                        Log.d(TAG, "Exception caught.");
                    }

                    movies[index] = currentMovie;
                    index++;
                }
                while (data.moveToNext());
            }

            taskFinishedWithArray(movies);
        }

        currentCursor = data;

        if (mCurrentSort == Sort.FAVORITES && currentCursor.getCount() > 0) {
            hideTabletDetail(false);
        }
    }

    /**
     * This method is called whenever a Loader resets. Unimplemented.
     *
     * @param loader The Loader that was reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Unimplemented.
    }

    /**
     * This method handles the revisiting of the Activity. This method is overridden to reload
     * the data when the Activity is revisited.
     */
    @Override
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }
}
