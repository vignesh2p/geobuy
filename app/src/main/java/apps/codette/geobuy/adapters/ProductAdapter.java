package apps.codette.geobuy.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import apps.codette.forms.Organization;
import apps.codette.forms.Product;
import apps.codette.geobuy.ProductDetailsActivity;
import apps.codette.geobuy.R;
import apps.codette.utils.SessionManager;

/**
 * Created by user on 25-03-2018.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Product> products;
    Dialog productDetailDialog;
    boolean isVertical;
    String orgId;


    public ProductAdapter(Context ctxt, List<Product> products, boolean isVertical, String orgId) {
        this.mCtx = ctxt;
        this.products = products;
        this.isVertical = isVertical;
        this.orgId = orgId;
    }



    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = null;


        if(isVertical)
            view =  inflater.inflate(R.layout.product_item, null);
        else
            view =  inflater.inflate(R.layout.product_grid_item, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        final Product product = (products.get(position).getProductDetails() != null ? products.get(position).getProductDetails().get(0) : products.get(position));

        if(product.getImage() != null)
            Glide.with(mCtx)
                .load(product.getImage()[0])
                .into(holder.imageView);
        holder.productTitle.setText(product.getTitle());
        if(product.getRating() != 0 ) {
            String avgRating = String.valueOf(product.getRating());
            avgRating = (avgRating.length() >3 ? avgRating.substring(0,3) : avgRating );
            holder.productRating.setText("" + avgRating);
        } else {
            holder.productRating.setVisibility(View.INVISIBLE);
            holder.product_no_rating.setVisibility(View.VISIBLE);
        }
      //  if(product.getProductDetails() != null && product.getProductDetails().size() > 0) {
            //if (product.getProductDetails() != null ) {
                float discount = (Float.valueOf(product.getOffer()) / 100) * product.getPrice();
                holder.productPrice.setText("₹ " + Math.round((product.getPrice() - discount)));
            /*} else {
                holder.productPrice.setText("₹ " + product.getProductDetails().get(0).getPrice());
            }*/
            holder.preferred_org.setText(product.getOrgname());
       /* } else {
            holder.productPrice.setText("₹ " + product.getPrice());
            holder.preferred_org.setText(product.getOrgname());
        }*/
      //  holder.productPrice.setText("₹"+product.getProductDetails().get(0).getPrice());

        holder.product_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orgId == null)
                    moveToProductDetailActivity(product.getId());
                else
                    moveToProductDetailActivity(product.getMasterid());
            }
        });

        holder.product_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orgId == null)
                    moveToProductDetailActivity(product.getId());
                else
                    moveToProductDetailActivity(product.getMasterid());
            }
        });

       /* holder.check_nearby.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openProductDetailsPopup(product);
            }
        });*/

        //setOnclick(product.getId(), holder.selectProduct);
    }

    Dialog productOrgDialog;

    private void openProductDetailsPopup(final Product product) {
        List<Product> products =  product.getProductDetails();
        productOrgDialog = new Dialog(mCtx);
        productOrgDialog.setContentView(R.layout.check_availability_product);
        if(products != null ){
            formOrgs(getOrgs(products), productOrgDialog);
        } else {
            products = new ArrayList<>();
            products.add(product);
            formOrgs(getOrgs(products), productOrgDialog);
        }
        Button close = productOrgDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePopup();
            }
        });
        productOrgDialog.show();
    }

    private void closePopup() {
        productOrgDialog.dismiss();
    }

    private List<Organization> getOrgs(List<Product> products) {
        List<Organization> orgs = new ArrayList<>();
        for(Product pr : products)
            orgs.add(new Organization(pr.getOrgname(), pr.getOrgid()));
        return orgs;
    }

    public int getImage(String imageName) {
        int drawableResourceId = mCtx.getResources().getIdentifier(imageName, "drawable", mCtx.getPackageName());
        return drawableResourceId;
    }

    private void formOrgs(List<Organization> organizations, Dialog productOrgDialog){
        android.widget.LinearLayout ll = productOrgDialog.findViewById(R.id.business_search_layout);
        if(organizations != null && !organizations.isEmpty()){
            RecyclerView rv = productOrgDialog.findViewById(R.id.nearby_business__recycler_view);
            NearByBusinessAdapter rbA = new NearByBusinessAdapter(mCtx,organizations, getImage("store"));
            LinearLayoutManager HorizontalLayout  = new LinearLayoutManager(mCtx, LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(HorizontalLayout);
            rv.setAdapter(rbA);
        }
    }

    private void moveToProductDetailActivity(String id) {
        Intent intent = new Intent(mCtx, ProductDetailsActivity.class);

        intent.putExtra("productId", id);
        intent.putExtra("orgId", orgId);
        mCtx.startActivity(intent);
    }

    /*private void setOnclick ( final int id, View... views){
        for(View v: views){

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToProductDetailActivity(id);
                }
            });
        }
    }*/

    @Override
    public int getItemCount() {
        if(products != null)
            return products.size();
        else
            return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }




    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productTitle;
        ImageView imageView;
        TextView productRating;
        TextView productPrice;
        TextView product_no_rating;
        LinearLayout checkNearbyAvailablity;
        CardView product_card;
      //  LinearLayout check_nearby;
        TextView preferred_org;
        RelativeLayout product_relative_layout;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.productTitle);
            imageView = itemView.findViewById(R.id.product_image);
            productRating = itemView.findViewById(R.id.product_rating);
            product_no_rating = itemView.findViewById(R.id.product_no_rating);
            productPrice = itemView.findViewById(R.id.product_price);
           // checkNearbyAvailablity = itemView.findViewById(R.id.check_nearby);
            product_card = itemView.findViewById(R.id.product_card);
           // check_nearby = itemView.findViewById(R.id.check_nearby);
            preferred_org = itemView.findViewById(R.id.preferred_org);
            product_relative_layout = itemView.findViewById(R.id.product_relative_layout);
        }


    }
}
