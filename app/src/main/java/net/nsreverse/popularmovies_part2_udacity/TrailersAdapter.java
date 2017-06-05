package net.nsreverse.popularmovies_part2_udacity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.nsreverse.popularmovies_part2_udacity.model.Trailer;

/**
 * This class converts an array of Trailer objects for a RecyclerView.
 *
 * @author Robert
 * Created on 6/4/2017.
 */
public class TrailersAdapter
        extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    @SuppressWarnings("unused")
    private static final String TAG = TrailersAdapter.class.getSimpleName();

    private Trailer[] mTrailerData;

    private TrailerOnClickHandler mClickHandler;

    public TrailersAdapter(Context context) {
        mClickHandler = (TrailerOnClickHandler)context;
    }

    /**
     * This method creates a new ViewHolder.
     *
     * @param parent A ViewGroup where the View will inflate.
     * @param viewType Unused int.
     * @return A new ViewHolder for displaying data.
     */
    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        boolean attachImmediately = false;

        View view = inflater.inflate(R.layout.item_trailer, parent, attachImmediately);
        return new TrailersAdapterViewHolder(view);
    }

    /**
     * This class defines the behavior of ViewHolders in the RecyclerView.
     */
    class TrailersAdapterViewHolder extends RecyclerView.ViewHolder
                                    implements View.OnClickListener {

        TextView titleTextView;
        TextView siteTextView;

        /**
         * This constructor sets up a listener and is the basis for making new
         * ReviewsAdapterViewHolder objects.
         *
         * @param v The View that is contained in this ViewHolder.
         */
        public TrailersAdapterViewHolder(View v) {
            super(v);

            titleTextView = (TextView)v.findViewById(R.id.text_view_trailer_name);
            siteTextView = (TextView)v.findViewById(R.id.text_view_trailer_site);

            v.setOnClickListener(this);
        }

        /**
         * This method handles when this View is clicked.
         *
         * @param v A View object representing the clicked View.
         */
        @Override
        public void onClick(View v) {
            int clickedIndex = getAdapterPosition();

            Trailer currentTrailer = mTrailerData[clickedIndex];
            mClickHandler.onTrailerSelected(currentTrailer, clickedIndex);
        }
    }

    /**
     * This method binds data to a ViewHolder.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position indicating both the position of the RecyclerView and the
     *                 position of the array of the data which will bind to this holder.
     */
    @Override
    public void onBindViewHolder(TrailersAdapterViewHolder holder, int position) {
        Trailer currentTrailer = mTrailerData[position];

        holder.titleTextView.setText(currentTrailer.getName());
        holder.siteTextView.setText(currentTrailer.getIsYoutube()? "YouTube" : "Unknown");
    }

    /**
     * This method gets the size of the data source.
     *
     * @return An int representing the size of the data source.
     */
    @Override
    public int getItemCount() {
        return mTrailerData.length;
    }

    /**
     * This method sets the data source of this Adapter.
     *
     * @param data An array of Review objects to set to this Adapter.
     */
    public void setTrailerData(Trailer[] data) {
        mTrailerData = data;
        notifyDataSetChanged();
    }

    /**
     * An interface that an Activity should implement in order to handle click events.
     */
    interface TrailerOnClickHandler {

        /**
         * This method is called whenever data is selected.
         *
         * @param selectedTrailer A Trailer object representing the selected data.
         * @param position The position in the RecyclerView that was clicked.
         */
        void onTrailerSelected(Trailer selectedTrailer, int position);
    }
}
