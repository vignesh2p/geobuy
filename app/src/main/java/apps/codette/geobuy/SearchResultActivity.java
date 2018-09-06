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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apps.codette.forms.Product;
import apps.codette.geobuy.Constants.SortBy;
import apps.codette.geobuy.adapters.ProductAdapter;
import apps.codette.geobuy.adapters.SearchTextProductAdapter;
import apps.codette.utils.RestCall;
import cz.msebera.android.httpclient.Header;

public class SearchResultActivity extends AppCompatActivity {

    ProgressDialog pd;

    ImageView grid_view;

    ImageView list_view;

    List<Product> products;

    Spinner sortBy;

    ImageView filter_icon;
    String subcategory;
    boolean isFirst = true, isVertical;
    String categoryId;
    String brand;
    String[] productIds;
    String selecetdBrands;

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
        categoryId = extras.getStringExtra("categoryId");
        subcategory = extras.getStringExtra("subcategory");
        brand = extras.getStringExtra("brand");
        productIds = extras.getStringArrayExtra("productIds");
        manageSortBy();
      //  if( categoryId != null || productIds != null) {
            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setIndeterminate(true);
            pd.setMessage("Loading");

            RequestParams requestParams = new RequestParams();
            if(categoryId != null)
                requestParams.put("category", categoryId);
            if(productIds != null)
                requestParams.put("productIds", productIds);
            if(subcategory != null)
                requestParams.put("subcategory", subcategory);
            if(brand != null)
                requestParams.put("brand", brand);

            getProducts(requestParams);
        /*} else {
            String json = extras.getStringExtra("products");
            Gson gson = new Gson();
            Type type = new TypeToken<List<Product>>() {}.getType();
            products = gson.fromJson(json, type);
            formProducts(products, true);
        }*/
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

    private void getProducts(RequestParams requestParams) {
        pd.show();
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
    }


    private void manageSortBy() {
        sortBy = findViewById(R.id.sortBy);
        List<String> values = new ArrayList<>();
        values.add("Popularity");
        values.add("Price low to high");
        values.add("Price high to low");
        values.add("Rating");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, R.layout.spinner_item,
                        values); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sortBy.setAdapter(spinnerArrayAdapter);
        sortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if(!isFirst) {
                    if(position ==0)
                        sortProducts(SortBy.POPULARITY);
                    else  if(position ==1)
                        sortProducts(SortBy.PRICELOWTOHIGH);
                    else  if(position ==2)
                        sortProducts(SortBy.PRICEHIGHTOLOW);
                    else  if(position ==3)
                        sortProducts(SortBy.RATING);
                }
                isFirst = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        filter_icon = findViewById(R.id.filter_icon);
        filter_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity(subcategory);
            }
        });
    }

    private void goToFilterActivity(String subcategory) {
        Intent intent = new Intent(this, FilterActivity.class);
        intent.putExtra("subcategory", subcategory);
        intent.putExtra("brands", selecetdBrands);
        startActivityForResult(intent, 100);
    }

    private void sortProducts(SortBy sort) {
        if(products != null && !products.isEmpty()) {
            Collections.sort(products, new ProductComparator(sort));
            formProducts(products, isVertical);
        }

    }

    private void showInListView() {
        RecyclerView rv = findViewById(R.id.product_results);
        ProductAdapter rbP = new ProductAdapter(this, products, true, null);
        LinearLayoutManager ll = new LinearLayoutManager(this);
        rv.setLayoutManager(ll);
        rv.setAdapter(rbP);
        rbP.notifyDataSetChanged();
    }

    private void showInGridView() {
        formProducts(products, false);
        /*RecyclerView rv = findViewById(R.id.product_results);
        ProductAdapter rbP = new ProductAdapter(this, products, false, null);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setAdapter(rbP);
        rbP.notifyDataSetChanged();*/
    }

    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }

    private void formProducts(List<Product> products, boolean isVertical) {
        if( pd != null)
            pd.dismiss();
        this.isVertical = isVertical;
        if(products != null && !products.isEmpty()){
            showView(findViewById(R.id.product_results_ll));
            hideView(findViewById(R.id.empty_layout));
            RecyclerView rv = findViewById(R.id.product_results);
            ProductAdapter rbP = new ProductAdapter(this, products, isVertical, null);
            if(isVertical){
               LinearLayoutManager ll = new LinearLayoutManager(this);
               rv.setLayoutManager(ll);
            } else{
                rv.setLayoutManager(new GridLayoutManager(this, 2));
            }

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            switch (resultCode) {
                case RESULT_OK:{
                    RequestParams requestParams = new RequestParams();
                    selecetdBrands = data.getStringExtra("brands");
                    requestParams.put("brand",selecetdBrands);
                    if(categoryId != null)
                        requestParams.put("category", categoryId);
                    if(subcategory != null)
                        requestParams.put("subcategory", subcategory);
                    if(brand != null)
                        requestParams.put("brand", brand);
                    getProducts(requestParams);
                    break;
                }
            }

        }
    }
}
