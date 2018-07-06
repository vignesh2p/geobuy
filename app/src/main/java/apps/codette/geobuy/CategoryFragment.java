package apps.codette.geobuy;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

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
import apps.codette.forms.Organization;
import apps.codette.geobuy.adapters.CategoryAdapter;
import apps.codette.geobuy.adapters.CategoryMasterAdapter;
import apps.codette.utils.RestCall;
import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressDialog pd;
    CategoryMasterAdapter categoryAdapter;


    private OnFragmentInteractionListener mListener;

    List<CategoryMaster> cts  = null;

    List<Category> categories = null;

    MainActivity mainActivity;

    public CategoryFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
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
        // Inflate the layout for this fragment
        mainActivity = (MainActivity) this.getActivity();
        mainActivity.setModule("CATEGORYFRAGMENT");
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = view.findViewById(R.id.category_card_view);
       // gridView.setItemAnimator(new DefaultItemAnimator());
       /* List<Category> cts =  new ArrayList<Category>();
        recyclerView2 = view.findViewById(R.id.fashion_grid_view);*/
        pd = new ProgressDialog(this.getContext());
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        getCategoryFromDB();
        return view;
    }

    private void getCategoryFromDB() {
        RequestParams requestParams = new RequestParams();
        //requestParams.put("isBanner", false);
        RestCall.get("categories", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                getCategory(new String(responseBody));
                // pd.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(pd != null)
                    pd.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });
    }

    private void toast(String msg) {
        if(mainActivity.getModule().equalsIgnoreCase("CATEGORYFRAGMENT"))
            Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void formUiforCategory(String categoryJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<CategoryMaster>>() {}.getType();
        cts  = gson.fromJson(categoryJson, type);
        categoryAdapter = new CategoryMasterAdapter(this.getContext(), cts, categories);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter( categoryAdapter );
        categoryAdapter.notifyDataSetChanged();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void getCategory(String categoryJson) {
        JSONObject jsonObject = null;
        //List<Category> categories = null;
        try {
            jsonObject = new JSONObject(categoryJson);
            Gson gson = new Gson();
            Type type = new TypeToken<List<Category>>() {}.getType();
            categories  = gson.fromJson(jsonObject.get("data").toString(), type);
        } catch (JSONException e) {
            e.printStackTrace();
            if(pd != null)
                pd.dismiss();
            toast(getResources().getString(R.string.try_later));
        }
        categoryAdapter = new CategoryMasterAdapter(this.getContext(), new ArrayList<CategoryMaster>(), categories);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter( categoryAdapter );

        RestCall.get("categorymaster", new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               // toast(new String(responseBody));
                formUiforCategory(new String(responseBody));
                pd.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(pd != null)
                    pd.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
}
