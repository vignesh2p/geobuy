package apps.codette.geobuy.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import apps.codette.forms.Review;
import apps.codette.geobuy.ProductDetailsActivity;
import apps.codette.geobuy.R;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {



    private Context mCtx;
    List<Review> reviews;

    public ReviewsAdapter(Context mCtx, List<Review> reviews){
        this.mCtx = mCtx;
        this.reviews = reviews;
    }

    @Override
    public ReviewsAdapter.ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.review_layout, null);
        return new ReviewsAdapter.ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ReviewHolder holder, int position) {
        Review review = reviews.get(position);
        if(review.getRatings() <= 2) {
            holder.product_review_layout.setBackgroundColor(mCtx.getResources().getColor(R.color.darkRed));
        } else if (review.getRatings() >= 3.5) {
            holder.product_review_layout.setBackgroundColor(mCtx.getResources().getColor(R.color.darkGreen));
        } else  {
            holder.product_review_layout.setBackgroundColor(mCtx.getResources().getColor(R.color.darkYellow));
        }
        holder.product_rating.setTextColor(mCtx.getResources().getColor(R.color.white));
        holder.product_rating.setText(review.getRatings()+"");
        holder.product_review.setText(review.getHeading());
        holder.product_review_comment.setText(review.getReview());
        holder.product_review_user.setText(review.getUser());
        holder.product_review_time.setText(review.getTime());
    }

    private void moveToProductDetails() {
        Intent intent = new Intent(mCtx, ProductDetailsActivity.class);
        mCtx.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        if(reviews != null)
            return reviews.size();
        else
            return 0;
    }

    class ReviewHolder extends RecyclerView.ViewHolder {

        TextView product_rating;
        TextView product_review;
        TextView product_review_comment;
        TextView product_review_user;
        TextView product_review_time;
        LinearLayout product_review_layout;


        public ReviewHolder(View itemView) {
            super(itemView);
            product_rating = itemView.findViewById(R.id.product_rating);
            product_review = itemView.findViewById(R.id.product_review);
            product_review_comment = itemView.findViewById(R.id.product_review_comment);
            product_review_user = itemView.findViewById(R.id.product_review_user);
            product_review_time = itemView.findViewById(R.id.product_review_time);
            product_review_layout = itemView.findViewById(R.id.product_review_layout);
        }
    }
}
