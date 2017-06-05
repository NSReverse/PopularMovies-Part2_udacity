package net.nsreverse.popularmovies_part2_udacity.data;

import net.nsreverse.popularmovies_part2_udacity.model.Movie;

/**
 * This class works as a container for data sources during runtime to help conserve bandwidth as
 * well as make the UI work with API-based data more efficiently.
 *
 * @author Robert
 * Created on 6/4/2017.
 */
public class RuntimeCache {
    public static Movie[] popularMovies;
    public static Movie[] topRatedMovies;
}
