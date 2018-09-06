package apps.codette.geobuy.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import apps.codette.forms.Organization;
import apps.codette.forms.Product;
import apps.codette.geobuy.BusinessActivity;
import apps.codette.geobuy.ProductDetailsActivity;
import apps.codette.geobuy.R;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;

/**
 * Created by user on 25-03-2018.
 */

public class ProductOrgsAdapter extends RecyclerView.Adapter<ProductOrgsAdapter.ProductOrgsHolder> {

    private Context mCtx;
    private List<Product> products;
    private String orgId;
    ProductDetailsActivity productDetailsActivity;
    SessionManager sessionManager;
    Map<String, ?> userDetails;


    int drawable ;
    public ProductOrgsAdapter(List<Product> products, String orgId, ProductDetailsActivity productDetailsActivity) {
        this.mCtx = productDetailsActivity.getApplicationContext();
        this.products = products;
        this.orgId = orgId;
        this.productDetailsActivity = productDetailsActivity;
    }


    @Override
    public ProductOrgsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.product_select_orgs, null);
        return new ProductOrgsHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductOrgsHolder holder, int position) {
        final Product product = products.get(position);
        sessionManager = new SessionManager(mCtx);
        userDetails = sessionManager.getUserDetails();
        if(product.getOrgid().equalsIgnoreCase(this.orgId)) {
            holder.product_org_card.setBackground(mCtx.getResources().getDrawable(R.drawable.selected));
            holder.product_org.setTextColor(mCtx.getResources().getColor(R.color.colorPrimary));
            holder.product_price.setTextColor(mCtx.getResources().getColor(R.color.colorPrimary));
        }
        holder.product_org.setText(product.getOrgname());
        holder.product_org_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadProductDetails(product.getMasterid(), product.getOrgid());
            }
        });
        holder.product_org_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                moveToOrgProfile(product.getOrgid());
                return false;
            }
        });

        holder.view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToOrgProfile(product.getOrgid());
            }
        });

        if(product.getOffer() != 0) {
            float discount = (Float.valueOf(product.getOffer()) /100) *product.getPrice();
            holder.product_price.setText("₹ "+Math.round(product.getPrice()-discount));
        } else {
            holder.product_price.setText("₹ "+Math.round(product.getPrice()));
        }
        if(userDetails.get("lat") != null)
            setOrgDistance(holder.view_profile, product.getOrgid());

    }



    private void setOrgDistance(final TextView view_profile, String orgId) {
        String lat = (String) userDetails.get("lat");
        String lon = (String) userDetails.get("lon");
        StringBuffer query = new StringBuffer("getDistance");
        query.append("?orgId="+orgId);
        query.append("&lat2="+lat);
        query.append("&lon2="+lon);

        RestCall.get(query.toString(), new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    view_profile.setText(jsonObject.get("distance").toString() + " | "+ jsonObject.get("duration").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //toast(mCtx.getResources().getString(R.string.try_later));
            }
        });
    }

    private void moveToOrgProfile(String orgid) {
        Intent intent = new Intent(mCtx, BusinessActivity.class);
        intent.putExtra("orgid", orgid);
        mCtx.startActivity(intent);
    }

    private void loadProductDetails(String id, String orgId) {
        this.productDetailsActivity.getProductDetails(id, orgId);
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }




    class ProductOrgsHolder extends RecyclerView.ViewHolder {

        CardView product_org_card;
        TextView product_org;
        TextView product_price;
        TextView view_profile;

        public ProductOrgsHolder(View itemView) {
            super(itemView);
            product_org_card = itemView.findViewById(R.id.product_org_card);
            product_org = itemView.findViewById(R.id.product_org);
            product_price = itemView.findViewById(R.id.product_price);
            view_profile = itemView.findViewById(R.id.view_profile);
        }
    }
}
