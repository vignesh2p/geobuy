package apps.codette.geobuy.adapters;

import android.content.Context;
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

import java.util.List;

import apps.codette.forms.Organization;
import apps.codette.forms.Product;
import apps.codette.geobuy.ProductDetailsActivity;
import apps.codette.geobuy.R;

/**
 * Created by user on 25-03-2018.
 */

public class ProductOrgsAdapter extends RecyclerView.Adapter<ProductOrgsAdapter.ProductOrgsHolder> {

    private Context mCtx;
    private List<Product> products;
    private String orgId;
    ProductDetailsActivity productDetailsActivity;


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
        Log.i("Orgid",""+product.getOrgid() +"  "+this.orgId);
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

        if(product.getOffer() != 0) {
            float discount = (Float.valueOf(product.getOffer()) /100) *product.getPrice();
            holder.product_price.setText("₹ "+(product.getPrice()-discount));
        } else {
            holder.product_price.setText("₹ "+product.getPrice());
        }

    }

    private void loadProductDetails(int id, String orgId) {
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

        public ProductOrgsHolder(View itemView) {
            super(itemView);
            product_org_card = itemView.findViewById(R.id.product_org_card);
            product_org = itemView.findViewById(R.id.product_org);
            product_price = itemView.findViewById(R.id.product_price);

        }
    }
}
