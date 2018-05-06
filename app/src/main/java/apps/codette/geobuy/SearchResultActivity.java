package apps.codette.geobuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        int categoryId = extras.getIntExtra("categoryId",0 );
        if( categoryId != 0) {
            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setIndeterminate(true);
            pd.setMessage("Loading");
            pd.show();
            RequestParams requestParams = new RequestParams();
            requestParams.put("categoryid",categoryId);
            RestCall.post("getProductsByCategory", requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Product>>() {}.getType();
                    List<Product> products = gson.fromJson(new String (responseBody), type);
                    formProducts(products);
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
            List<Product> products = gson.fromJson(json, type);
            formProducts(products);
        }


    }

    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_LONG).show();
    }

    private void formProducts(List<Product> products) {
        if( pd != null)
            pd.dismiss();

        if(products != null && !products.isEmpty()){
            showView(findViewById(R.id.product_results));
            hideView(findViewById(R.id.empty_layout));
            RecyclerView rv = findViewById(R.id.product_results);
            ProductAdapter rbP = new ProductAdapter(this, products);
            LinearLayoutManager ll = new LinearLayoutManager(this);
            rv.setLayoutManager(ll);
            rv.setAdapter(rbP);
            rbP.notifyDataSetChanged();
        } else {
            showView(findViewById(R.id.empty_layout));
            hideView(findViewById(R.id.product_results));
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
