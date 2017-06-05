package net.nsreverse.popularmovies_part2_udacity.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static net.nsreverse.popularmovies_part2_udacity.data.provider.FavoritesContract.FavoritesEntry.TABLE_NAME;

/**
 * This class is the content provider for access to the Favorites database.
 *
 * @author Robert
 * Created on 6/3/2017.
 */
public class FavoritesContentProvider extends ContentProvider {
    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;

    private FavoritesDbHelper mFavoritesDbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * This method creates a new Uri matcher to detect the path that the provider should take
     * when executing an operation.
     *
     * @return A UriMatcher for matching Uris passed to the provider.
     */
    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(FavoritesContract.AUTHORITY,
                FavoritesContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(FavoritesContract.AUTHORITY,
                FavoritesContract.PATH_FAVORITES + "/#", FAVORITES_WITH_ID);

        return uriMatcher;
    }

    /**
     * The main entry point for the class.
     *
     * @return A boolean representing if onCreate has successfully setup the class instance.
     */
    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavoritesDbHelper = new FavoritesDbHelper(context);
        return true;
    }

    /**
     * This method inserts a row into the database.
     *
     * @param uri The Uri representing where the data will be stored in the database.
     * @param values Key/Value pairs representing the contents of a row to be inserted.
     * @return A Uri representing the path of the stored row.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITES:
                long id = db.insert(TABLE_NAME, null, values);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(
                            FavoritesContract.FavoritesEntry.CONTENT_URI, id);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (getContext() != null) {
            assert getContext() != null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /**
     * This method queries the provider for the contents of the database.
     *
     * @param uri A Uri for querying data from the database.
     * @param projection String array for representing projection in a query.
     * @param selection String representing selection constraint in a query.
     * @param selectionArgs String array representing the corresponding args for selection.
     * @param sortOrder String representing which column to order the results.
     * @return A Cursor representing the data queried from the database.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        final SQLiteDatabase db = mFavoritesDbHelper.getReadableDatabase();

        int match  = sUriMatcher.match(uri);
        Cursor returnedCursor;

        switch (match) {
            case FAVORITES:
                returnedCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (getContext() != null) {
            assert getContext() != null;
        }

        returnedCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnedCursor;
    }

    /**
     * This method removes one or more rows from the database.
     *
     * @param uri Uri representing the path of the data to be deleted.
     * @param selection A String representing selection constraint in the query.
     * @param selectionArgs String array representing selection args in a query.
     * @return An int representing the number of deleted rows.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int tasksDeleted;

        switch (match) {
            case FAVORITES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{ id });
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted > 0) {
            if (getContext() != null) {
                assert getContext() != null;
            }

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    /**
     * This method updates a row in the database. Unimplemented.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * This method gets the MIME type for data located with a Uri. Unimplemented.
     */
    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
