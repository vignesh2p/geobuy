package apps.codette.geobuy;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
//import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import apps.codette.forms.Banner;
import apps.codette.forms.Organization;
import apps.codette.geobuy.Constants.GeobuyConstants;
import apps.codette.geobuy.adapters.BannerAdapter;
import apps.codette.geobuy.adapters.MyCustomPagerAdapter;
import apps.codette.utils.CClocation;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;
import me.relex.circleindicator.CircleIndicator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GoogleMap mMap;

    //ProgressDialog pd;

    List<Organization> organizations = null;
    private OnFragmentInteractionListener mListener;

    private static final int ACESS_FINE_LOCATION_CODE = 1;

    Dialog businessDetailsDialog;

    LatLng latLng;

    TextView filter_distance;

    int distance = 5;

    DiscreteSeekBar filter_location;

    LatLngBounds platLngBounds;

    SessionManager sessionManager;

    ProgressDialog progressDialog;

    MainActivity mainActivity;


    TextView location_text;

    ImageView location_image;

    View nearby_map;


    public MapFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) this.getActivity();
        mainActivity.setModule("MAPFRAGMENT");
        View view = null;
        view = inflater.inflate(apps.codette.geobuy.R.layout.fragment_map, container, false);
        sessionManager = new SessionManager(this.getContext());
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        //Switch map_view_active_switch = view.findViewById(R.id.map_view_active_switch);
        location_text = view.findViewById(R.id.location_text);
        location_image =  view.findViewById(R.id.location_image);
        nearby_map = view.findViewById(R.id.nearby_map);
        updateLocation(location_text);
        location_image.setOnClickListener(locationSelectListener());
        /*Switch map_view_switch = view.findViewById(R.id.map_view_switch);

        map_view_switch.setChecked(true);
        map_view_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    compoundButton.setText("Map View");
                } else {
                    compoundButton.setText("List View");
                }
            }
        });*/
        /*map_view_active_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    compoundButton.setText("Open Shops");
                } else {
                    compoundButton.setText("Open / Closed");
                }
            }
        });*/

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(apps.codette.geobuy.R.id.nearby_map);
        mapFragment.getMapAsync(this);
        businessDetailsDialog = new Dialog(this.getContext());
        return view;
    }

    private View.OnClickListener locationSelectListener(){
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
            startActivityForResult(builder.build(mainActivity), GeobuyConstants.PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void updateLocation(TextView textView) {
        Map<String, ?> map = sessionManager.getUserDetails();
        if(map != null && map.get("lat") != null && map.get("place") != null){
            String place = map.get("place").toString();
            textView.setText(place);
            //getLocation(Float.valueOf(lat), Float.valueOf(lon), textView);
        } else {
            textView.setText("Select location");
        }
        textView.setOnClickListener(locationSelectListener());
    }

    private void getLocation(float lat, float lon, final TextView textView) {
        StringBuffer query = new StringBuffer("getDistance");
        query.append("?lat1="+lat);
        query.append("&lon1="+lon);
        query.append("&lat2=12.9007");
        query.append("&lon2=80.1969");
        RestCall.get(query.toString(), new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    String location = jsonObject.get("origin").toString();
                    textView.setText(location.split(",")[0]+","+location.split(",")[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void ShowPopup(Marker marker) {
        LatLng latLng = marker.getPosition();
        Log.i("latLng", latLng.latitude + "");
        Log.i("latLng", latLng.longitude + "");
        TextView txtclose;

        businessDetailsDialog.setContentView(R.layout.business_details_popup);
        txtclose = (TextView) businessDetailsDialog.findViewById(R.id.txtclose);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                businessDetailsDialog.dismiss();
            }
        });
        businessDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        for (Organization org : organizations) {
            if (org.getOrgLat() == latLng.latitude && latLng.longitude == org.getOrgLon()) {
                assignOrgDetails(org);
            }
        }

    }

    private void assignOrgDetails(final Organization org) {

        TextView btv = businessDetailsDialog.findViewById(R.id.business_name);
        TextView bdtv = businessDetailsDialog.findViewById(R.id.business_detail);
        //TextView ftv = businessDetailsDialog.findViewById(R.id.followers_count);
        //TextView ptv = businessDetailsDialog.findViewById(R.id.products_count);
        TextView etv = businessDetailsDialog.findViewById(R.id.business_email);
        TextView ntv = businessDetailsDialog.findViewById(R.id.business_phone);
        //final Button btnfollow = businessDetailsDialog.findViewById(R.id.btnfollow);
        Button org_view_profile = businessDetailsDialog.findViewById(R.id.org_view_profile);
        org_view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToOrgProfile(org.getOrgid());
            }
        });
        btv.setText(org.getOrgname());
        CircleIndicator indicator = (CircleIndicator) businessDetailsDialog.findViewById(R.id.indicator);

        ViewPager viewPager = (ViewPager) businessDetailsDialog.findViewById(R.id.business_view_pager);
        String images[] = org.getImages();
        if (images != null && images.length > 0) {
            MyCustomPagerAdapter myCustomPagerAdapter = new MyCustomPagerAdapter(this.getActivity(), images);
            viewPager.setAdapter(myCustomPagerAdapter);
            indicator.setViewPager(viewPager);
        }
        bdtv.setText(org.getOrgaddress());
       /* if(org.getProducts()!= null && org.getProducts().size()  > 0) {
            ptv.setText(org.getProducts().size()+"");
        } else
            ptv.setText(0+"");
        */
       /* String[] followers = org.getFollowers();
        if (org.getFollowers() != null) {
            //ftv.setText(org.getFollowers().length+"");

            Map<String, ?> userDetails = sessionManager.getUserDetails();
            String email = (String) userDetails.get("useremail");
            List<String> foll = Arrays.asList(followers);
            if (foll.contains(email)) {
                btnfollow.setText("Following");
                btnfollow.setTextColor(getResources().getColor(R.color.white));
                btnfollow.setBackground(getResources().getDrawable(R.drawable.selectedbutton));
                btnfollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        followOrg(org.getOrgid(), btnfollow, false);
                    }
                });
            } else {
                btnfollow.setText("Follow");
                btnfollow.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnfollow.setBackground(getResources().getDrawable(R.drawable.selected));
                btnfollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        followOrg(org.getOrgid(), btnfollow, true);
                    }
                });
            }
        } else {
            //ftv.setText(0 + "");
            btnfollow.setText("Follow");
            btnfollow.setTextColor(getResources().getColor(R.color.colorPrimary));
            btnfollow.setBackground(getResources().getDrawable(R.drawable.selected));
            btnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    followOrg(org.getOrgid(), btnfollow, true);
                }
            });
        }*/
        if (org.getOrgemail() != null)
            etv.setText(org.getOrgemail());
        else
            etv.setText("-");

        if (org.getOrgphoneno() != null)
            ntv.setText(org.getOrgphoneno());
        else
            ntv.setText("-");

        businessDetailsDialog.show();
    }

    private void moveToOrgProfile(String orgid) {
        Intent intent = new Intent(this.getContext(), BusinessActivity.class);
        intent.putExtra("orgid", orgid);
        this.startActivity(intent);
    }

    private void followOrg(final String orgid, final Button btnfollow, final boolean follow) {
        Map<String, ?> userDetails = sessionManager.getUserDetails();
        String email = (String) userDetails.get("useremail");
        RequestParams requestParams = new RequestParams();
        requestParams.put("orgid", orgid);
        requestParams.put("follower", email);
        requestParams.put("follow", follow);
        progressDialog.show();
        RestCall.post("followOrg", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (follow) {
                    btnfollow.setText("Following");
                    btnfollow.setTextColor(getResources().getColor(R.color.white));
                    btnfollow.setBackground(getResources().getDrawable(R.drawable.selectedbutton));
                    btnfollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            followOrg(orgid, btnfollow, false);
                        }
                    });
                } else {
                    btnfollow.setText("Follow");
                    btnfollow.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btnfollow.setBackground(getResources().getDrawable(R.drawable.selected));
                    btnfollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            followOrg(orgid, btnfollow, true);
                        }
                    });
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    PendingResult<LocationSettingsResult> result;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
       // updateGeobuyLocation(location.getLatitude(), location.getLongitude());
        nativeupdatePosition(location.getLatitude(), location.getLongitude(), true);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this.getActivity(), apps.codette.geobuy.R.raw.mapstyle));

            if (!success) {
                Log.e("MF", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MF", "Can't find style. Error: ", e);
        }
        Map<String, ?> userdetails = sessionManager.getUserDetails();
        String lat = (String) userdetails.get("lat");
        String lon = (String) userdetails.get("lon");
        String place = (String) userdetails.get("place");

        if(lat != null && lon != null) {
            //toast(lat+"   "+lon);
            nativeupdatePosition(Double.parseDouble(lat), Double.parseDouble(lon), false);
        } else {

            /*if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                requestToTurnOnGPS();

            }*/
            //openPlacePicker();
        }

    }

    LocationManager locationManager;
    // The minimum distance to change Updates in meters


    private void goToCurrentLocationInMap() {
        try {
            if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

        locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    GeobuyConstants.MIN_TIME_BW_UPDATES,
                GeobuyConstants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void nativeupdatePosition(double lattitude, double longitude, boolean animate) {
    //    if (location != null) {
            RequestParams params = new RequestParams();
            // Add a marker in Sydney and move the camera

            LatLng pos = new LatLng(lattitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(pos);
            markerOptions.title("Your location");
            // Drawable dr = getResources().getDrawable(R.drawable.gpsl);
            if(isCurrentFragment()) {
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.gps);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 85, 85, false);

                //  BitmapDescriptor d = BitmapDescriptorFactory.fromResource(R.drawable.gps);
                BitmapDescriptor d = BitmapDescriptorFactory.fromBitmap(smallMarker);
                markerOptions.icon(d);
                markerOptions.draggable(true);
                //  mMap.addMarker(markerOptions);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(pos)      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        // .bearing(90)                // Sets the orientation of the camera to east
                        //  .tilt(50)                   // Sets the tilt of the camera to 30 degrees
                        .build();
               // if(animate)// Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        ShowPopup(marker);
                        return true;
                    }
                });

                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                        RequestParams params = new RequestParams();
                        params.put("maxlattitude", latLngBounds.northeast.latitude);
                        params.put("maxlongitude", latLngBounds.northeast.longitude);
                        params.put("minlattitude", latLngBounds.southwest.latitude);
                        params.put("minlongitude", latLngBounds.southwest.longitude);
                        if (platLngBounds != null) {
                            if (!platLngBounds.contains(new LatLng(latLngBounds.northeast.latitude, latLngBounds.southwest.longitude))) {
                                getOrgsByLocation(params);
                            }
                        } else {
                            platLngBounds = latLngBounds;
                            getOrgsByLocation(params);
                        }
                    }
                });
            }

       // }
    }


    private void getOrgsByLocation(RequestParams params) {
       /* pd = new ProgressDialog(this.getContext());
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        pd.show();*/
        RestCall.post("storesByPosition", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String jsonString = new String(responseBody);
                //toast(new String(responseBody));
                updateUIOnOrganisations(jsonString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
               /* if(pd != null)
                    pd.dismiss();*/
                toast(getResources().getString(R.string.try_later));
            }
        });
    }

    private void updateUIOnOrganisations(String jsonString) {
        try {
            //toast("Success");

            JSONObject jsonObject = new JSONObject(jsonString);
            Log.i("responseBody", jsonObject.get("data").toString());
            Gson gson = new Gson();
            Type type = new TypeToken<List<Organization>>() {
            }.getType();
            organizations = gson.fromJson(jsonObject.get("data").toString(), type);
            if (organizations != null && !organizations.isEmpty()) {
                GeobuyConstants.NEAR_BY_ORGS = organizations;
                for (Organization organization : organizations) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(organization.getOrgLat(), organization.getOrgLon());
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.store);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);
                    markerOptions.position(latLng);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    markerOptions.snippet(organization.getOrgphoneno());
                    //markerOptions.zIndex(5) .anchor(0.5f, 1);
                    markerOptions.title(organization.getOrgname());
                    markerOptions.draggable(false);
                    markerOptions.visible(true);
                    mMap.addMarker(markerOptions);
                }
            } else {
                toast("No Sellers found in nearby areas");
            }
            // pd.hide();
        } catch (Exception ex) {
            ex.printStackTrace();
            // pd.hide();
            toast("Failure :: " + ex.getMessage());
        }

    }


    private void setLatLng(LatLng position) {
        latLng = position;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Log.i("requestCode", "" + requestCode);
        //Checking the request code of our request
        if (requestCode == ACESS_FINE_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this.getContext(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.getContext(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void requestToTurnOnGPS() {


        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                   return;
        }
        mMap.setMyLocationEnabled(true);

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addApi(LocationServices.API)
                //.addConnectionCallbacks(this)
                //.addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
       // locationRequest.setInterval(5 * 1000);
       // locationRequest.setFastestInterval(2 * 1000);
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

    private void getS(Status status) throws IntentSender.SendIntentException {
        status.startResolutionForResult(this.getActivity(), GeobuyConstants.REQUEST_LOCATION);
    }


    private void toast(String text) {
        if(mainActivity.getModule().equalsIgnoreCase("MAPFRAGMENT")) {
            Snackbar.make(nearby_map, text, Snackbar.LENGTH_SHORT).show();
                   // .setAction("Action", null).show();
           // Toast.makeText(this.getContext(), "" + text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            /*case GeobuyConstants.REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        requestToTurnOnGPS ();
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(getActivity(), "Location not enabled", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;*/
            case GeobuyConstants.PLACE_PICKER_REQUEST :
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        Place place = PlacePicker.getPlace(data, this.mainActivity);
                        LatLng latLng = place.getLatLng();
                        updateGeobuyLocation(latLng.latitude, latLng.longitude, place);
                        location_text.setText(place.getName());
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latLng)      // Sets the center of the map to Mountain View
                                .zoom(17)                   // Sets the zoom
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;

        }

    }
    private boolean isCurrentFragment(){
        return mainActivity.getModule().equalsIgnoreCase("MAPFRAGMENT");
    }
    private void updateGeobuyLocation(double lat, double lon, Place place){
        SharedPreferences.Editor editor = sessionManager.getEditor();
        editor.putString("lat", String.valueOf(lat));
        editor.putString("lon", String.valueOf(lon));
        editor.putString("place", String.valueOf(place.getName()));
        sessionManager.put(editor);
    }
}
