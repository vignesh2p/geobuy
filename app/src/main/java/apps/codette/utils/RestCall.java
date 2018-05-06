package apps.codette.utils;


import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

public class RestCall {
    //private static final String BASE_URL = "http://server-dot-pingme-191816.appspot.com/";
    private static final String BASE_URL = "https://geobuy-viki19nesh.c9users.io/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    private static SyncHttpClient sclient = new SyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
       // client.addHeader("Accept", "application/json");
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void sGet(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        //sclient.addHeader("Accept", "application/json");
        sclient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, Object object) {
        client.addHeader("Accept", "application/json");
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.i(url,params.toString());
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.i(url,params.toString());
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.i(url,params.toString());
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        Log.i("relativeUrl",BASE_URL + relativeUrl);
        return BASE_URL + relativeUrl;
    }
}