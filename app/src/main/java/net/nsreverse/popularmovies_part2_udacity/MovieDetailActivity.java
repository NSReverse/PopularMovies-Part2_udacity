package net.nsreverse.popularmovies_part2_udacity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.nsreverse.popularmovies_part2_udacity.data.provider.FavoritesContract.FavoritesEntry;
import net.nsreverse.popularmovies_part2_udacity.model.Movie;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Activity that handles displaying detailed information about a selected Movie.
 *
 * @author Robert
 * Created on 6/2/2017.
 */
public class MovieDetailActivity extends AppCompatActivity {

    // Constants for passing data from MainActivity
    public static final String KEY_ID = "ID";
    public static final String KEY_TMDB_ID = "TMDB_ID";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_THUMBNAIL = "THUMBNAIL";
    public static final String KEY_BACKDROP = "BACKDROP";
    public static final String KEY_RELEASE_DATE = "RELEASE_DATE";
    public static final String KEY_VOTE_AVERAGE = "VOTE_AVERAGE";
    public static final String KEY_PLOT_SYNOPSIS = "SYNOPSIS";
    public static final String KEY_THUMBNAIL_NAME = "THUMB_NAME";
    public static final String KEY_BACKDROP_NAME = "BACK_NAME";

    public static final String KEY_IS_FAVORITE = "FAVORITE";

    private int id;
    private int tmdbId;
    private String thumbName;
    private String backdropName;
    private boolean isFavorite;

    private Bitmap thumbnailBitmap;
    private Bitmap backdropBitmap;

    private Snackbar mSnackbar;

    /**
     * This method is the main entry point for this Activity.
     *
     * @param savedInstanceState A Bundle of persisted data from a configuration change.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFavorite) {
                    String title = getIntent().getStringExtra(KEY_TITLE);
                    String releaseDate = getIntent().getStringExtra(KEY_RELEASE_DATE);
                    String averageRating = getIntent().getStringExtra(KEY_VOTE_AVERAGE);
                    String plotSynopsis = getIntent().getStringExtra(KEY_PLOT_SYNOPSIS);

                    ContentValues values = new ContentValues();
                    values.put(FavoritesEntry.COLUMN_MOVIE_ID, tmdbId);
                    values.put(FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
                    values.put(FavoritesEntry.COLUMN_MOVIE_TITLE, title);
                    values.put(FavoritesEntry.COLUMN_MOVIE_VOTE_AVG, averageRating);
                    values.put(FavoritesEntry.COLUMN_MOVIE_SYNOPSIS, plotSynopsis);
                    values.put(FavoritesEntry.COLUMN_MOVIE_THUMB_NAME, thumbName);
                    values.put(FavoritesEntry.COLUMN_MOVIE_BACKDROP_NAME, backdropName);

                    getContentResolver().insert(FavoritesEntry.CONTENT_URI, values);

                    File filesDir = getFilesDir();
                    String imagePath = filesDir.getAbsolutePath().concat("/" + thumbName.replace("/", "") + ".png");
                    String backdropPath = filesDir.getAbsolutePath().concat("/" + backdropName.replace("/", "") + ".png");

                    // https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
                    File imageFile = new File(imagePath);
                    FileOutputStream outputStream = null;

                    File backdropFile = new File(backdropPath);
                    FileOutputStream backOutputStream = null;

                    try {
                        outputStream = new FileOutputStream(imageFile);
                        thumbnailBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);

                        backOutputStream = new FileOutputStream(backdropFile);
                        backdropBitmap.compress(Bitmap.CompressFormat.PNG, 50, backOutputStream);
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

                    mSnackbar = Snackbar.make(view, title + " added to favorites", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    mSnackbar.show();

                    fab.setImageResource(R.drawable.ic_star_white_24dp);

                    isFavorite = true;
                }
                else {
                    int rowsDeleted =
                            getContentResolver().delete(FavoritesEntry.CONTENT_URI.buildUpon()
                                .appendPath(Integer.toString(id)).build(),
                                    null,
                                    null
                    );

                    getContentResolver().notifyChange(FavoritesEntry.CONTENT_URI, null);

                    if (rowsDeleted > 0) {
                        Snackbar.make(view, "Video removed from favorites", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    else {
                        Snackbar.make(view, "Failed to remove from favorites.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                    fab.setImageResource(R.drawable.ic_star_border_white_24dp);

                    isFavorite = false;
                }
            }
        });

        // https://developer.android.com/training/implementing-navigation/ancestral.html
        if (getSupportActionBar() != null) {
            assert getSupportActionBar() != null;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            id = getIntent().getIntExtra(KEY_ID, -1);
            tmdbId = getIntent().getIntExtra(KEY_TMDB_ID, -1);
            thumbName = getIntent().getStringExtra(KEY_THUMBNAIL_NAME);
            backdropName = getIntent().getStringExtra(KEY_BACKDROP_NAME);

            isFavorite = getIntent().getBooleanExtra(KEY_IS_FAVORITE, false);

            if (isFavorite) {
                fab.setImageResource(R.drawable.ic_star_white_24dp);
            }
            else {
                fab.setImageResource(R.drawable.ic_star_border_white_24dp);
            }

            ImageView thumbImageView = (ImageView)findViewById(R.id.image_view_thumbnail);
            TextView titleTextView = (TextView)findViewById(R.id.text_view_title);
            TextView releaseDateTextView = (TextView)findViewById(R.id.text_view_release_date);
            TextView averageRatingTextView = (TextView)findViewById(R.id.text_view_rating);
            TextView synopsisTextView = (TextView)findViewById(R.id.text_view_synopsis);

            // https://stackoverflow.com/questions/4352172/how-do-you-pass-images-bitmaps-between-android-activities-using-bundles/7890405#7890405
            final byte[] thumbnailArray = getIntent().getByteArrayExtra(KEY_THUMBNAIL);
            final Bitmap thumbnail = BitmapFactory.decodeByteArray(thumbnailArray, 0, thumbnailArray.length);

            Bitmap backdrop;

            byte[] backdropArray = getIntent().getByteArrayExtra(KEY_BACKDROP);
            if (backdropArray != null) {
                backdrop = BitmapFactory.decodeByteArray(backdropArray, 0, backdropArray.length);
            }
            else {
                backdrop = thumbnail;
            }

            String title = getIntent().getStringExtra(KEY_TITLE);
            String releaseDate = getIntent().getStringExtra(KEY_RELEASE_DATE);
            String averageRating = getIntent().getStringExtra(KEY_VOTE_AVERAGE);
            String plotSynopsis = getIntent().getStringExtra(KEY_PLOT_SYNOPSIS);

            getSupportActionBar().setTitle(title);
            thumbImageView.setImageBitmap(thumbnail);
            releaseDateTextView.setText(getString(R.string.released) + " " + releaseDate);
            averageRatingTextView.setText(getString(R.string.rating) + " " + averageRating);
            synopsisTextView.setText(plotSynopsis);

            ImageView backdropImageView = (ImageView)findViewById(R.id.image_view_backdrop);
            backdropImageView.setImageBitmap(backdrop);

            titleTextView.setVisibility(View.GONE);

            thumbnailBitmap = thumbnail;
            backdropBitmap = backdrop;

            findViewById(R.id.button_trailers).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MovieDetailActivity.this, TrailersActivity.class);
                    intent.putExtra(TrailersActivity.KEY_THUMB, thumbnailArray);
                    intent.putExtra(TrailersActivity.KEY_ID, tmdbId);
                    startActivity(intent);
                }
            });

            findViewById(R.id.button_reviews).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MovieDetailActivity.this, ReviewsActivity.class);
                    intent.putExtra(ReviewsActivity.KEY_THUMB, thumbnailArray);
                    intent.putExtra(ReviewsActivity.KEY_ID, tmdbId);
                    startActivity(intent);
                }
            });
        }
        else {
            finish();
        }
    }

    /**
     * This method handles when an item in the ActionBar is selected.
     *
     * @param item A MenuItem representing the item selected.
     * @return A boolean representing if the action was handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mSnackbar != null && mSnackbar.isShown()) {
                mSnackbar.dismiss();
                mSnackbar = null;
            }

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
