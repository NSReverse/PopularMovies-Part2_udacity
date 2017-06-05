package net.nsreverse.popularmovies_part2_udacity.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.nsreverse.popularmovies_part2_udacity.data.provider.FavoritesContract.FavoritesEntry;


/**
 * This class handles common database maintenance operations on a table.
 *
 * @author Robert
 * Created on 6/3/2017.
 */
public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int VERSION = 1;

    /**
     * This is the basic constructor to create a new FavoritesDbHelper instance.
     *
     * @param context The Context to pass to the database helper.
     */
    FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * This method handles the creation of the database. A query is executed against the
     * SQLiteDatabase to create the table.
     *
     * @param db A SQLiteDatabase object to execute a query on.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                FavoritesEntry._ID + " INTEGER PRIMARY KEY, " +
                FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_VOTE_AVG + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_THUMB_NAME + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_BACKDROP_NAME + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    /**
     * This method handles the upgrading of the database structure whenever a database version
     * changes.
     *
     * @param db A SQLiteDatabase object to execute queries on.
     * @param oldVersion An int representing the old version of the database.
     * @param newVersion An int representing the new version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
