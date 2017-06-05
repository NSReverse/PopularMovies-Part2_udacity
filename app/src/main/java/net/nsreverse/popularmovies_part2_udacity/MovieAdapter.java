package net.nsreverse.popularmovies_part2_udacity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.nsreverse.popularmovies_part2_udacity.model.Movie;


/**
 * This class works to adapt the data source onto the RecyclerView.
 *
 * Created by Robert on 5/26/2017.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    @SuppressWarnings("unused")
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private Movie[] mMovieData;
    private MovieAdapterOnClickHandler mClickHandler;

    /**
     * Constructor for making a new MovieAdapter.
     *
     * @param mClickHandler An Object that implements MovieAdapterOnClickHandler to handle clicks.
     */
    public MovieAdapter(MovieAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    /**
     * This method creates new MovieAdapterViewHolders.
     *
     * @param parent The ViewGroup this holder will be a part of.
     * @param viewType Unused int.
     * @return A new MovieAdapterViewHolder containing the inflated view.
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        boolean attachImmediately = false;

        View view = inflater.inflate(R.layout.item_movie, parent, attachImmediately);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * This method sets the data source to the ViewHolder.
     *
     * @param holder The holder to set the thumbnail to.
     * @param position An int representing the position in the RecyclerView.
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie currentMovie = mMovieData[position];

        if (currentMovie.getThumbnail() != null) {
            holder.thumbnailImageView.setImageBitmap(currentMovie.getThumbnail());
        }
        else {
            holder.thumbnailImageView.setImageResource(R.drawable.ic_image_black_24dp);
        }
    }

    /**
     * This method gets the count of items to be displayed in the RecyclerView.
     *
     * @return An int representing the underlying data source.
     */
    @Override
    public int getItemCount() {
        if (mMovieData != null) {
            return mMovieData.length;
        }

        return 0;
    }

    /**
     * This method sets a new data source to the adapter and then notifies of changes.
     *
     * @param movieData The data source to set to the adapter.
     */
    public void setMovieData(Movie[] movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    /**
     * This class defines the behavior of ViewHolders in the RecyclerView.
     */
    class MovieAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView thumbnailImageView;

        /**
         * This constructer sets up a listener and is the basis for making new
         * MovieAdapterViewHolders.
         *
         * @param v The View that is contained in this ViewHolder.
         */
        public MovieAdapterViewHolder(View v) {
            super(v);
            thumbnailImageView = (ImageView)v.findViewById(R.id.image_view_thumbnail);
            v.setOnClickListener(this);
        }

        /**
         * This method passes a click event to the click handler.
         *
         * @param v The View that was clicked.
         */
        @Override
        public void onClick(View v) {
            int clickedIndex = getAdapterPosition();
            Movie currentMovie = mMovieData[clickedIndex];
            mClickHandler.onMovieClick(currentMovie);
            mClickHandler.positionSelected(getAdapterPosition());
        }
    }

    /**
     * This interface passes data to the Object that is currently stored as mClickHandler and
     * mClickHandler should implement this to recieve click information.
     */
    interface MovieAdapterOnClickHandler {
        void onMovieClick(Movie selectedMovie);
        void positionSelected(int position);
    }
}