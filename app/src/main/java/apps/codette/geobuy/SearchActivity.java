package apps.codette.geobuy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import apps.codette.forms.GeobuySearch;
import apps.codette.forms.Organization;
import apps.codette.forms.Product;
import apps.codette.geobuy.Constants.GeobuyConstants;
import apps.codette.geobuy.adapters.NearByBusinessAdapter;
import apps.codette.geobuy.adapters.SearchTextProductAdapter;
import apps.codette.utils.CClocation;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements LocationListener {

    private List<GeobuySearch> products;

    EditText searchText;

    TextView filteredLocation;

    private List<GeobuySearch> trendingProducts;

    private boolean switchFilter;

    DiscreteSeekBar discreteNormal;

    List<Organization> organizations;

    int distance = 0;

    LocationManager locationManager;
    // The minimum distance to change Updates in meters

    Switch filter_by_distance_switch;

    SessionManager sessionManager;

    LinearLayout filter_location_ll;

    TextView location_text;

    ImageView location_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        hideView(findViewById(R.id.empty_layout));
        filter_location_ll = findViewById(R.id.filter_location_ll);
        location_text=findViewById(R.id.location_text);
        location_image = findViewById(R.id.location_image);
        location_image.setOnClickListener(locationSelectListener());
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
      //  manageTrendingProductsView();
        manageSearch();
        manageFilterByLocation();
       // hideView(discreteNormal);
        hideView(filteredLocation);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sessionManager = new SessionManager(this);
    }

    private void manageSwitchFilter() {
        filter_by_distance_switch = findViewById(R.id.filter_by_distance_switch);
        switchFilter = filter_by_distance_switch.isChecked();
        filter_by_distance_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchFilter = b;
                if (!b) {
                    hideView(filter_location_ll);
                    hideView(filteredLocation);
                } else {
                    showView(filter_location_ll);
                    showView(filteredLocation);
                    Map<String, ?> userdetails = sessionManager.getUserDetails();
                    String lat = (String) userdetails.get("lat");
                    String lon = (String) userdetails.get("lon");
                    String place = (String) userdetails.get("place");
                    if (lat == null || lon == null) {
                        openPlacePicker();
                    } else{
                        location_text.setText(place);
                    }
                }
                String text = searchText.getText().toString();
                if (text.length() > 0) {
                    getProductsAndOrgs(text);
                }
            }
        });
    }

    private void getProductsAndOrgs(String text) {
        if (switchFilter) {
            Map<String, ?> userdetails = sessionManager.getUserDetails();
            String lat = (String) userdetails.get("lat");
            String lon = (String) userdetails.get("lon");
            if (lat != null && lon != null) {
                searchProductsBasedOnLocation(Double.parseDouble(lat), Double.parseDouble(lon));
                searchOrgsBasedOnLocaion(Double.parseDouble(lat), Double.parseDouble(lon));
            } else {
                openPlacePicker();

                /*if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    location = getLastBestLocation();
                  //  toast(location.getLatitude() + "  " + location.getLongitude());

                    searchProductsBasedOnLocation(location.getLatitude(), location.getLongitude());
                    searchOrgsBasedOnLocaion(location.getLatitude(), location.getLongitude());
                } else {
                    requestToTurnOnGPS();
                }*/

            }
        } else {
            getProducts(text);
            getOrgs(text);
        }
    }


    private void manageFilterByLocation() {
        discreteNormal = findViewById(R.id.filter_location);
        distance = 5;
        discreteNormal.setProgress(distance);
        filteredLocation = findViewById(R.id.filtered_location);
        filteredLocation.setText("(" + distance + " Km)");
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
                filteredLocation.setText("(" + distance + " Km)");
                getProductsAndOrgs(searchText.getText().toString());
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
                if (text.length() > 0) {
                    getProductsAndOrgs(text);
                } else {
                    /*Log.i("trendingProducts ", "" + trendingProducts.size());
                    if (trendingProducts != null) {
                        updateProductsInRecyclerView(trendingProducts);
                    } else
                        manageTrendingProductsView();*/
                }
            }
        });


    }

    private void getOrgs(String text) {

        RequestParams params = new RequestParams();
        params.put("searchkey", text);
        RestCall.post("orgsSearch", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("orgsSearch responseBody", new String(responseBody));
                Gson gson = new Gson();
                Type type = new TypeToken<List<Organization>>() {
                }.getType();
                organizations = gson.fromJson(new String(responseBody), type);
                formOrgs(organizations);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                toast(getResources().getString(R.string.try_later));
            }
        });
    }


    private void getS(Status status) throws IntentSender.SendIntentException {
        status.startResolutionForResult(this, 1000);
    }

    private void getProducts(String text) {
        TextView tv = findViewById(R.id.filtered_location);
        tv.setText("(" + distance + " Km)");
        // else {
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
        //}
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void formProducts(String productsJson) {
        JSONObject jsonObject = null;
        try {
            products = null;
            //jsonObject = new JSONObject(productsJson);
            Log.i("PresponseBody", productsJson);
            Gson gson = new Gson();
            Type type = new TypeToken<List<GeobuySearch>>() {
            }.getType();
            products = gson.fromJson(productsJson, type);
            updateProductsInRecyclerView(products);
        } catch (Exception e) {
            toast(getResources().getString(R.string.try_later));
        }

    }


    private void manageTrendingProductsView() {

        trendingProducts = new ArrayList<GeobuySearch>();
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
        Type type = new TypeToken<List<GeobuySearch>>() {}.getType();
        trendingProducts = gson.fromJson(trendingJson, type);
        updateProductsInRecyclerView(trendingProducts);
    }


    private void updateProductsInRecyclerView(List<GeobuySearch> products) {
        if (products != null && !products.isEmpty()) {
            hideView(findViewById(R.id.empty_layout));
            RecyclerView rvP = findViewById(R.id.nearby_products__recycler_view);
            showView(rvP);
            SearchTextProductAdapter rbP = new SearchTextProductAdapter(this, products, distance);
            LinearLayoutManager ll = new LinearLayoutManager(this);
            rvP.setLayoutManager(ll);
            rvP.setAdapter(rbP);
            rbP.notifyDataSetChanged();
        } else {
            hideView(findViewById(R.id.nearby_products__recycler_view));
            showView(findViewById(R.id.empty_layout));
            String searchT = searchText.getText().toString();
            if (searchT != null && !searchT.isEmpty()) {
                TextView tv = findViewById(R.id.no_prod);
                tv.setText("No products matching \"" + searchT + "\" in \" " + distance + "km \" ");
            }

        }

    }


    private void manageNearByBusinessView() {
        organizations = GeobuyConstants.NEAR_BY_ORGS;
        formOrgs(organizations);

    }

    private void formOrgs(List<Organization> organizations) {
        android.widget.LinearLayout ll = findViewById(R.id.business_search_layout);
        if (organizations != null && !organizations.isEmpty()) {

            showView(ll);
            RecyclerView rv = findViewById(R.id.nearby_business__recycler_view);
            NearByBusinessAdapter rbA = new NearByBusinessAdapter(this, organizations, getImage("store"));
            LinearLayoutManager HorizontalLayout = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(HorizontalLayout);
            rv.setAdapter(rbA);
        } else {
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


    private void showView(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);

        }

    }

    private void hideView(View... views) {
        for (View v : views) {
            v.setVisibility(View.GONE);

        }

    }


    private void requestToTurnOnGPS() {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                //.addConnectionCallbacks(this)
                //.addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
//                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        goToCurrentLocationInMap();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            getS(status);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private void goToCurrentLocationInMap() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    GeobuyConstants.MIN_TIME_BW_UPDATES,
                    GeobuyConstants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    Location location;

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        searchProductsBasedOnLocation(location.getLatitude(), location.getLongitude());
        searchOrgsBasedOnLocaion(location.getLatitude(), location.getLongitude());
    }

    private void searchOrgsBasedOnLocaion(double lat, double lon) {
        RequestParams params = new RequestParams();
        params.put("searchkey", searchText.getText().toString());
        params.put("maxlattitude", (Number) (lat + (distance * 0.0043352)));
        params.put("maxlongitude", (Number) (lon + (distance * 0.0043352)));
        params.put("minlattitude", (Number) (lat - (distance * 0.0043352)));
        params.put("minlongitude", (Number) (lon - (distance * 0.0043352)));
        RestCall.post("orgsSearch", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("orgsSearch responseBody", new String(responseBody));
                Gson gson = new Gson();
                Type type = new TypeToken<List<Organization>>() {
                }.getType();
                organizations = gson.fromJson(new String(responseBody), type);
                formOrgs(organizations);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                toast(getResources().getString(R.string.try_later));
            }
        });
    }

    private void searchProductsBasedOnLocation(double lat, double lon) {

        RequestParams params = new RequestParams();
        params.put("searchkey", searchText.getText().toString());
        params.put("maxlattitude", (Number) (lat + (distance * 0.0043352)));
        params.put("maxlongitude", (Number) (lon + (distance * 0.0043352)));
        params.put("minlattitude", (Number) (lat - (distance * 0.0043352)));
        params.put("minlongitude", (Number) (lon - (distance * 0.0043352)));

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

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        toast(s);
    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
           /* case GeobuyConstants.REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        requestToTurnOnGPS();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        filter_by_distance_switch.setChecked(false);
                        Toast.makeText(this, "Location not enabled", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;*/

            case GeobuyConstants.PLACE_PICKER_REQUEST :
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Place place = PlacePicker.getPlace(data, this);
                        LatLng latLng = place.getLatLng();
                        updateGeobuyLocation(latLng.latitude, latLng.longitude, place);
                        searchProductsBasedOnLocation(latLng.latitude, latLng.longitude);
                        searchOrgsBasedOnLocaion(latLng.latitude, latLng.longitude);
                        location_text.setText(place.getName());
                        break;
                    }
                    default:
                    {
                        filter_by_distance_switch.setChecked(false);
                        break;
                    }
                }
                break;

        }

    }

    /**
     * @return the last know best location
     */
    private Location getLastBestLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            toast("Not granted");
        }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }


    private void updateGeobuyLocation(double lat, double lon, Place place){
        SharedPreferences.Editor editor = sessionManager.getEditor();
        editor.putString("lat", String.valueOf(lat));
        editor.putString("lon", String.valueOf(lon));
        editor.putString("place", String.valueOf(place.getName()));
        sessionManager.put(editor);
    }

    private View.OnClickListener locationSelectListener(){
        final Activity activity = this;
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlacePicker();
            }
        };
    }

    private void openPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        builder.setLatLngBounds(GeobuyConstants.GEOBUY_LAT_LNG_BOUNDS);
        try {
            startActivityForResult(builder.build(this), GeobuyConstants.PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
}
