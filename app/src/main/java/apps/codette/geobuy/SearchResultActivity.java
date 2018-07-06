package apps.codette.geobuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.util.List;

import apps.codette.forms.Product;
import apps.codette.geobuy.adapters.ProductAdapter;
import apps.codette.geobuy.adapters.SearchTextProductAdapter;
import apps.codette.utils.RestCall;
import cz.msebera.android.httpclient.Header;

public class SearchResultActivity extends AppCompatActivity {

    ProgressDialog pd;

    ImageView grid_view;

    ImageView list_view;

    List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.back_toolbar);
        Intent extras = getIntent();
       /* String searchkey = extras.getStringExtra("searchkey");
        int distance = extras.getIntExtra("distance",5);*/
        ImageView back = findViewById(R.id.search_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSearch();
            }
        });
        String categoryId = extras.getStringExtra("categoryId");
        String[] productIds = extras.getStringArrayExtra("productIds");
        if( categoryId != null || productIds != null) {
            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setIndeterminate(true);
            pd.setMessage("Loading");
            pd.show();
            RequestParams requestParams = new RequestParams();
            if(categoryId != null)
                requestParams.put("category", categoryId);
            else if(productIds != null)
                requestParams.put("productIds", productIds);
            RestCall.post("getProductsByCategory", requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Product>>() {}.getType();
                    products = gson.fromJson(new String (responseBody), type);
                    formProducts(products, true);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(pd != null)
                        pd.dismiss();
                    toast(getResources().getString(R.string.try_later));
                }
            });
        } else {
            String json = extras.getStringExtra("products");
            Gson gson = new Gson();
            Type type = new TypeToken<List<Product>>() {}.getType();
            products = gson.fromJson(json, type);
            formProducts(products, true);
        }
        grid_view = findViewById(R.id.grid_view);
        list_view = findViewById(R.id.list_view);
        grid_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideView(grid_view);
                showView(list_view);
                showInGridView();
            }
        });
        list_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideView(list_view);
                showView(grid_view);
                showInListView();
            }
        });

    }

    private void showInListView() {
        RecyclerView rv = findViewById(R.id.product_results);
        ProductAdapter rbP = new ProductAdapter(this, products, true);
        LinearLayoutManager ll = new LinearLayoutManager(this);
        rv.setLayoutManager(ll);
        rv.setAdapter(rbP);
        rbP.notifyDataSetChanged();
    }

    private void showInGridView() {
        RecyclerView rv = findViewById(R.id.product_results);
        ProductAdapter rbP = new ProductAdapter(this, products, false);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setAdapter(rbP);
        rbP.notifyDataSetChanged();
    }

    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }

    private void formProducts(List<Product> products, boolean isVertical) {
        if( pd != null)
            pd.dismiss();

        if(products != null && !products.isEmpty()){
            showView(findViewById(R.id.product_results_ll));
            hideView(findViewById(R.id.empty_layout));
            RecyclerView rv = findViewById(R.id.product_results);
            ProductAdapter rbP = new ProductAdapter(this, products, isVertical);
            LinearLayoutManager ll = new LinearLayoutManager(this);
            rv.setLayoutManager(ll);
            rv.setAdapter(rbP);
            rbP.notifyDataSetChanged();
        } else {
            showView(findViewById(R.id.empty_layout));
            hideView(findViewById(R.id.product_results_ll));
            Button searchAgain = findViewById(R.id.search_again);
            searchAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeSearch();
                }
            });
        }

    }



    private void showView (View... views){
        for(View v: views){
            v.setVisibility(View.VISIBLE);

        }

    }
    private void hideView (View... views){
        for(View v: views){
            v.setVisibility(View.GONE);

        }

    }

    private void closeSearch() {
        finish();
    }

}
