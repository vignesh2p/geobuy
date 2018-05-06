package apps.codette.geobuy;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import apps.codette.forms.Organization;
import apps.codette.forms.Product;
import apps.codette.geobuy.Constants.GeobuyConstants;
import apps.codette.geobuy.adapters.NearByBusinessAdapter;
import apps.codette.geobuy.adapters.SearchTextProductAdapter;
import apps.codette.utils.CClocation;
import apps.codette.utils.RestCall;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    private List<Product> products;

    EditText searchText;

    TextView filteredLocation;

    private List<Product> trendingProducts;

    private boolean switchFilter;

    DiscreteSeekBar discreteNormal;

    List<Organization> organizations;

    int distance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        hideView(findViewById(R.id.empty_layout));
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        ImageView back = findViewById(R.id.search_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSearch();
            }
        });
        manageSwitchFilter();
        manageNearByBusinessView();
        manageTrendingProductsView();
        manageSearch();
        manageFilterByLocation();
        hideView(discreteNormal);
        hideView(filteredLocation);
    }

    private void manageSwitchFilter() {
        Switch filter_by_distance_switch = findViewById(R.id.filter_by_distance_switch);
        switchFilter = filter_by_distance_switch.isChecked();
        filter_by_distance_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchFilter = b;
                if(!b){
                    hideView(discreteNormal);
                    hideView(filteredLocation);
                } else {
                    showView(discreteNormal);
                    showView(filteredLocation);
                }
                String text = searchText.getText().toString();
                if(text.length() > 0) {
                    getProducts(text);
                    getOrgs(text);
                }
            }
        });
    }


    private void manageFilterByLocation() {
        discreteNormal = findViewById(R.id.filter_location);
        distance = 5;
        discreteNormal.setProgress(distance);
        filteredLocation = findViewById(R.id.filtered_location);
        filteredLocation.setText("("+distance +" Km)");
        discreteNormal.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                distance = seekBar.getProgress();
                getProducts(searchText.getText().toString());
                getOrgs(searchText.getText().toString());
                Log.i("discreteNormal", ""+distance);
            }
        });
    }

    private void manageSearch() {
        searchText = findViewById(R.id.geobuy_search);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if(text.length() > 0) {
                    getProducts(text);
                    getOrgs(text);
                }
                else
                {
                    Log.i("trendingProducts ",""+trendingProducts.size());
                    if(trendingProducts != null) {
                        updateProductsInRecyclerView(trendingProducts);
                    } else
                        manageTrendingProductsView();
                }
            }
        });


    }

    private void getOrgs(String text) {

        RequestParams params = new RequestParams();
        params.put("searchkey", text);
        if(switchFilter) {
            Location location = CClocation.getLocation(this, this);
            if(location != null) {
                Number num = location.getLatitude();
                params.put("maxlattitude", (Number) (location.getLatitude() + (distance * 0.0043352)));
                params.put("maxlongitude", (Number) (location.getLongitude() + (distance * 0.0043352)));
                params.put("minlattitude", (Number) (location.getLatitude() - (distance * 0.0043352)));
                params.put("minlongitude", (Number) (location.getLongitude() - (distance * 0.0043352)));
            }
        }
        RestCall.post("orgsSearch", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("orgsSearch responseBody",new String(responseBody));
                Gson gson = new Gson();
                Type type = new TypeToken<List<Organization>>() {}.getType();
                organizations = gson.fromJson(new String(responseBody), type);
                formOrgs(organizations);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                toast(getResources().getString(R.string.try_later));
            }
        });

    }

    private void getProducts(String text) {
        TextView tv = findViewById(R.id.filtered_location);
        tv.setText("("+distance +" Km)");
        if(switchFilter) {
            Location location = CClocation.getLocation(this, this);
            if(location != null) {
                RequestParams params = new RequestParams();
                params.put("searchkey", text);
                Number num = location.getLatitude();
                params.put("maxlattitude", (Number) (location.getLatitude() + (distance * 0.0043352)));
                params.put("maxlongitude", (Number) (location.getLongitude() + (distance * 0.0043352)));
                params.put("minlattitude", (Number) (location.getLatitude() - (distance * 0.0043352)));
                params.put("minlongitude", (Number) (location.getLongitude() - (distance * 0.0043352)));

                RestCall.post("productsSearch", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        formProducts(new String(responseBody));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        toast(getResources().getString(R.string.try_later));
                    }
                });
            }
        } else {
            RequestParams params = new RequestParams();
            params.put("searchkey", text);
            RestCall.post("productsSearch", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    formProducts(new String(responseBody));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    toast(getResources().getString(R.string.try_later));
                }
            });
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void formProducts(String productsJson) {
        JSONObject jsonObject = null;
        try {
            products = null;
            //jsonObject = new JSONObject(productsJson);
            Log.i("PresponseBody",productsJson);
            Gson gson = new Gson();
            Type type = new TypeToken<List<Product>>() {}.getType();
            products = gson.fromJson(productsJson, type);
            updateProductsInRecyclerView(products);
        } catch (Exception e) {
            toast(getResources().getString(R.string.try_later));
        }

    }


    private void manageTrendingProductsView() {

        trendingProducts = new ArrayList<Product>();
        RequestParams requestParams = new RequestParams();
        RestCall.get("trendings", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                formTrendings(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                toast(getResources().getString(R.string.try_later));
            }
        });
        //updateProductsInRecyclerView(products);
    }

    private void formTrendings(String trendingJson) {
            trendingProducts = null;
            Gson gson = new Gson();
            Type type = new TypeToken<List<Product>>() {}.getType();
            trendingProducts = gson.fromJson(trendingJson, type);
            updateProductsInRecyclerView(trendingProducts);
    }



    private void updateProductsInRecyclerView(List<Product> products) {
        if(products != null && !products.isEmpty()) {
            hideView(findViewById(R.id.empty_layout));
            RecyclerView rvP = findViewById(R.id.nearby_products__recycler_view);
            showView(rvP);
            SearchTextProductAdapter rbP = new SearchTextProductAdapter(this, products,distance);
            LinearLayoutManager ll = new LinearLayoutManager(this);
            rvP.setLayoutManager(ll);
            rvP.setAdapter(rbP);
            rbP.notifyDataSetChanged();
        } else {
            hideView(findViewById(R.id.nearby_products__recycler_view));
            showView(findViewById(R.id.empty_layout));
            String searchT = searchText.getText().toString();
            if(searchT != null && !searchT.isEmpty()) {
                TextView tv = findViewById(R.id.no_prod);
                tv.setText("No products matching \""+searchT+"\" in \" "+distance+"km \" ");
            }

        }

    }


    private void manageNearByBusinessView() {
        organizations = GeobuyConstants.NEAR_BY_ORGS;
        formOrgs(organizations);

    }

    private void formOrgs(List<Organization> organizations){
        android.widget.LinearLayout ll = findViewById(R.id.business_search_layout);
        if(organizations != null && !organizations.isEmpty()){

            showView(ll);
            RecyclerView rv = findViewById(R.id.nearby_business__recycler_view);
            NearByBusinessAdapter rbA = new NearByBusinessAdapter(this,organizations, getImage("store"));
            LinearLayoutManager HorizontalLayout  = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(HorizontalLayout);
            rv.setAdapter(rbA);
        } else{
            hideView(ll);
        }

    }

    private void closeSearch() {
        finish();
    }

    public int getImage(String imageName) {
        int drawableResourceId = this.getResources().getIdentifier(imageName, "drawable", this.getPackageName());
        return drawableResourceId;
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
}
