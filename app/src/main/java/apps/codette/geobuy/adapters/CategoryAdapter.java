package apps.codette.geobuy.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import apps.codette.forms.Category;
import apps.codette.geobuy.ProductDetailsActivity;
import apps.codette.geobuy.R;
import apps.codette.geobuy.SearchResultActivity;
import apps.codette.geobuy.SubCategoryActivty;

/**
 * Created by user on 25-03-2018.
 */

public class CategoryAdapter  extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context mCtx;
    private List<Category> categories;

    public CategoryAdapter(Context ctxt, List<Category> categories) {
        this.mCtx = ctxt;
        this.categories = categories;
    }


    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.category, null);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        final Category category = categories.get(position);
        Glide.with(mCtx)
                .load(category.getImage())
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openProductsForCategory(category.getId());
                Gson gson = new Gson();
                Type type = new TypeToken<Category>() {}.getType();
                String json = gson.toJson(category, type);
                openSubCategory(json);
            }
        });

        holder.textViewTitle.setText(category.getTittle());
    }

    private void openSubCategory(String json) {
        Intent intent = new Intent(mCtx, SubCategoryActivty.class);
        intent.putExtra("category", json);
        mCtx.startActivity(intent);
    }

    private void openProductsForCategory(String categoryId) {
        Intent intent = new Intent(mCtx, SearchResultActivity.class);
        intent.putExtra("categoryId", categoryId);
        mCtx.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }




    class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        ImageView imageView;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.category_text);
            imageView = itemView.findViewById(R.id.category_image);
        }
    }
}
