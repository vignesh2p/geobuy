package apps.codette.utils;

/**
 * Created by user on 22-03-2018.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;

import android.app.Service;
import android.os.IBinder;


public abstract class CClocation implements LocationListener {

    Context ctx;

    public CClocation(Context ctx, Activity activity) {
        this.ctx = ctx;
        this.activity = activity;
    }

    Activity activity;

    @Override
    public void onLocationChanged(Location loc) {
        Log.d("activity", "RLOC: onLocationChanged");
        location = loc;
        invokeNativeCode(loc);

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("activity", "RLOC: onProviderDisabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        LocationManager locManager = (LocationManager) myAndroidContext.getSystemService(Context.LOCATION_SERVICE);

        Log.d("activity", "RLOC: onProviderEnabled");
        if (ActivityCompat.checkSelfPermission(this.ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return;
        }
        Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            double latitude = 0;
            double longitude = 0;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("activity", "RLOC: onProviderEnabled " + latitude + " " + longitude);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("activity", "RLOC: onStatusChanged");

    }

    static LocationListener locationListener = null;
    private static final String TAG = "CClocation";
    static Context myAndroidContext;
    static CClocation instance;

    static public void setContext(Context c) {
        Log.d("activity", "RLOC: setContext");
        myAndroidContext = c;
    }

    public void run(Context my) {
        Log.d("activity", "RLOC: run");
        myAndroidContext = my;
    }


    public static void helloWorld() {
        Log.v("InternetConnection", "HELLO WORLD");
    }


    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public Location getLocation2() {
        try {
            locationManager = (LocationManager) myAndroidContext.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Toast.makeText(this.ctx, "isGPSEnabled :: "+isGPSEnabled+"  isNetworkEnabled "+isNetworkEnabled, Toast.LENGTH_SHORT).show();
            Log.i("d" , "isGPSEnabled "+isGPSEnabled);
            Log.i("d" , "isNetworkEnabled "+isNetworkEnabled);
            if (!isGPSEnabled && !isNetworkEnabled) {
                ActivityCompat.requestPermissions(this.activity,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this.ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                      //  return TODO;
                       // Toast.makeText(this.ctx, "No Network permission", Toast.LENGTH_LONG).show();
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("activity", "LOC Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Log.d("activity", "LOC by Network");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                           // Toast.makeText(this.ctx, "longitude latitude"+latitude+" :: "+longitude, Toast.LENGTH_LONG).show();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("activity", "RLOC: GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                               // Toast.makeText(this.ctx, "getLastKnownLocation :: "+location, Toast.LENGTH_SHORT).show();
                                Log.d("activity", "RLOC: loc by GPS");
                                nativeupdatePosition(location);
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Toast.makeText(this.ctx, "longitude latitude"+latitude+" :: "+longitude, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this.ctx, "location is null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    ActivityCompat.requestPermissions(this.activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }


                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.d("Network", "Network");

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                nativeupdatePosition(location);
                                // Toast.makeText(this.ctx, "getLastKnownLocation :: "+location, Toast.LENGTH_SHORT).show();
                                Log.d("activity", "RLOC: loc by GPS");

                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Toast.makeText(this.ctx, "longitude latitude"+latitude+" :: "+longitude, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this.ctx, "location is null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("activity", "RLOC: Location xx "+latitude+" "+longitude);

        return location;
    }


    static CClocation truc;


    public static Location getLocation(Context ctx, Activity activity)
    {
        Log.d("activity", "getLocation");
        setContext(ctx);
        //truc = new CClocation(ctx, activity);

        return truc.getLocation2();
   }

    public abstract void nativeupdatePosition(Location loc);

    public void invokeNativeCode(Location loc) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Toast.makeText(this.ctx, "invokeNativeCode latitude"+latitude+" :: "+longitude, Toast.LENGTH_SHORT).show();
        nativeupdatePosition(loc);
    }

}