package apps.codette.geobuy.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import apps.codette.forms.GeobuySearch;
import apps.codette.forms.Product;
import apps.codette.geobuy.R;
import apps.codette.geobuy.SearchResultActivity;

/**
 * Created by user on 08-04-2018.
 */

public class SearchTextProductAdapter extends RecyclerView.Adapter<SearchTextProductAdapter.SearchTextViewHolder>  {

    private Context mCtx;
    private List<GeobuySearch> products;
    int distance;

    public SearchTextProductAdapter(Context mCtx, List<GeobuySearch> products, int distance){
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
        final GeobuySearch product = products.get(position);
        Log.i("product", product.toString());
        if(product.getBrand() != null && product.getSubcategory() != null ) {
            holder.textViewTitle.setText(product.getBrandTitle());
            showView(holder.in);
            showView(holder.category);
            holder.category.setText(product.getSubcategoryTitle());
        }
        else if(product.getBrand() != null && product.getCategory() != null ) {
            holder.textViewTitle.setText(product.getBrandTitle());
            showView(holder.in);
            showView(holder.category);
            holder.category.setText(product.getCategoryTitle());

        }
        else if(product.getProduct() != null ) {
            holder.textViewTitle.setText(product.getText());
        }

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  moveToProductDetails(product);
                getProducts(product.getProduct(), product.getBrand(), product.getCategory(), product.getSubcategory());
            }
        });
    }

    private void showView (View... views){
        for(View v: views){
            v.setVisibility(View.VISIBLE);
        }
    }

    private void getProducts(String productId, String brand, String cat, String subcat) {
        Intent intent = new Intent(mCtx, SearchResultActivity.class);
        if(productId != null) {
            String[] arry = new String[1];
            arry[0] = productId;
            intent.putExtra("productIds", arry);
        }if(brand != null)
            intent.putExtra("brand", brand);
        if(cat != null)
            intent.putExtra("categoryId", cat);
        if(subcat != null)
            intent.putExtra("subcategory", subcat);
        mCtx.startActivity(intent);
    }

    private void moveToProductDetails(Product product) {
        Intent intent = new Intent(mCtx, SearchResultActivity.class);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {}.getType();
        List<Product> prods = new ArrayList<Product>();
        prods.add(product);
        String json = gson.toJson(prods, type);
        intent.putExtra("products", json);
        mCtx.startActivity(intent);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class SearchTextViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView category, in;
        CardView cardview;

        public SearchTextViewHolder(View itemView) {
            super(itemView);
            cardview = itemView.findViewById(R.id.cardview);
            textViewTitle = itemView.findViewById(R.id.nearby_trending_products);
            category = itemView.findViewById(R.id.category);
            in = itemView.findViewById(R.id.in);
        }
    }
}
