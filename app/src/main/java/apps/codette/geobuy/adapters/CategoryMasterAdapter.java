package apps.codette.geobuy.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import apps.codette.forms.Category;
import apps.codette.forms.CategoryMaster;
import apps.codette.geobuy.R;
import apps.codette.utils.RestCall;
import cz.msebera.android.httpclient.Header;

/**
 * Created by user on 27-03-2018.
 */

public class CategoryMasterAdapter  extends RecyclerView.Adapter<CategoryMasterAdapter.CategoryMasterViewHolder> {

    Context ctx;
    List<CategoryMaster> categoriesMaster;
    List<Category> categories ;

    public CategoryMasterAdapter(Context ctx, List<CategoryMaster> categoriesMaster, List<Category> categories) {
        this.categoriesMaster = categoriesMaster;
        this.ctx = ctx;
        this.categories = categories;
    }




    @Override
    public CategoryMasterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.category_set, null);
        return new CategoryMasterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryMasterViewHolder holder, int position) {
       // Log.i("onBindViewHolder","categories.get(position).getName()" +categoriesMaster.get(position).getName());
        CategoryMaster categoryMaster = categoriesMaster.get(position);
        holder.textViewTitle.setText(categoryMaster.getName());
        String key = categoryMaster.getKey();
        formUiforCategory(categories, holder.recyclerView, key);
       /* RestCall.get("categories", new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                formUiforCategory(new String(responseBody), holder.recyclerView);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
               // pd.dismiss();
            }
        });*/

    }


    private void formUiforCategory(List<Category> cts, RecyclerView recyclerView, String key) {
        try {
            List<Category> fileterdCategory = new ArrayList<Category>();
            for(Category category : cts)
            {
                Log.i(key, category.getCategory());
                if(category.getCategory().equalsIgnoreCase(key)) {
                    fileterdCategory.add(category);
                }
            }
            CategoryAdapter categoryAdapter = new CategoryAdapter(ctx, fileterdCategory);
            recyclerView.setLayoutManager(new GridLayoutManager(ctx, 3));
            recyclerView.setAdapter(categoryAdapter);
        } catch (Exception e) {

        }
    }
    @Override
    public int getItemCount() {
        return categoriesMaster.size();
    }

    class CategoryMasterViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        RecyclerView recyclerView;

        public CategoryMasterViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.category_head);
            recyclerView = itemView.findViewById(R.id.category_grid_view);
        }
    }
}
