package apps.codette.geobuy.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import apps.codette.forms.Product;
import apps.codette.geobuy.R;
import apps.codette.geobuy.SearchResultActivity;

/**
 * Created by user on 08-04-2018.
 */

public class SearchTextProductAdapter extends RecyclerView.Adapter<SearchTextProductAdapter.SearchTextViewHolder>  {

    private Context mCtx;
    private List<Product> products;
    int distance;

    public SearchTextProductAdapter(Context mCtx, List<Product> products, int distance){
        this.mCtx = mCtx;
        this.products = products;
        this.distance = distance;
    }

    @Override
    public SearchTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.search_item, null);
        return new SearchTextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchTextViewHolder holder, int position) {
        final Product product = products.get(position);
        Log.i("product", product.toString());
        holder.textViewTitle.setText(product.getTitle());
        holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToProductDetails(product);
            }
        });
    }

    private void moveToProductDetails(Product product) {
        Intent intent = new Intent(mCtx, SearchResultActivity.class);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {}.getType();
        String json = gson.toJson(products, type);
        intent.putExtra("products", json);
        mCtx.startActivity(intent);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class SearchTextViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;

        public SearchTextViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.nearby_trending_products);
        }
    }
}
