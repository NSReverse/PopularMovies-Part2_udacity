package net.nsreverse.popularmovies_part2_udacity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.nsreverse.popularmovies_part2_udacity.model.Review;

/**
 * This class converts an array of Review objects for a RecyclerView.
 *
 * @author Robert
 * Created on 6/4/2017.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    @SuppressWarnings("unused")
    private static final String TAG = ReviewsAdapter.class.getSimpleName();

    private Review[] mReviewData;

    /**
     * This method creates a new ViewHolder.
     *
     * @param parent A ViewGroup where the View will inflate.
     * @param viewType Unused int.
     * @return A new ViewHolder for displaying data.
     */
    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        boolean attachImmediately = false;

        View view = inflater.inflate(R.layout.item_review, parent, attachImmediately);
        return new ReviewsAdapterViewHolder(view);
    }

    /**
     * This class defines the behavior of ViewHolders in the RecyclerView.
     */
    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView authorTextView;
        TextView contentTextView;

        /**
         * This constructor sets up a listener and is the basis for making new
         * ReviewsAdapterViewHolder objects.
         *
         * @param v The View that is contained in this ViewHolder.
         */
        public ReviewsAdapterViewHolder(View v) {
            super(v);

            authorTextView = (TextView)v.findViewById(R.id.text_view_author);
            contentTextView = (TextView)v.findViewById(R.id.text_view_content);
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
    public void onBindViewHolder(ReviewsAdapterViewHolder holder, int position) {
        Review currentReview = mReviewData[position];

        holder.authorTextView.setText("Reviewed By: ".concat(currentReview.getReviewer()));
        holder.contentTextView.setText(currentReview.getContent());
    }

    /**
     * This method gets the size of the data source.
     *
     * @return An int representing the size of the data source.
     */
    @Override
    public int getItemCount() {
        return mReviewData.length;
    }

    /**
     * This method sets the data source of this Adapter.
     *
     * @param data An array of Review objects to set to this Adapter.
     */
    public void setReviewData(Review[] data) {
        mReviewData = data;
        notifyDataSetChanged();
    }
}
