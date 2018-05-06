package apps.codette.geobuy.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import apps.codette.geobuy.ProductDetailsActivity;
import apps.codette.geobuy.R;

public class ProductHighlightsAdapter  extends RecyclerView.Adapter<ProductHighlightsAdapter.ProductHighlightsHolder> {



    private Context mCtx;
    List<String> texts;

    public ProductHighlightsAdapter(Context mCtx, List<String> texts){
        this.mCtx = mCtx;
        this.texts = texts;
    }

    @Override
    public ProductHighlightsAdapter.ProductHighlightsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.product_highlight, null);
        return new ProductHighlightsAdapter.ProductHighlightsHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductHighlightsAdapter.ProductHighlightsHolder holder, int position) {
        String text = texts.get(position);
        holder.textViewTitle.setText(text);
    }

    private void moveToProductDetails() {
        Intent intent = new Intent(mCtx, ProductDetailsActivity.class);
        mCtx.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return texts.size();
    }

    class ProductHighlightsHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;

        public ProductHighlightsHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.products_highlight);
        }
    }
}
