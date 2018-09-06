package apps.codette.geobuy.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import apps.codette.forms.Organization;
import apps.codette.geobuy.BusinessActivity;
import apps.codette.geobuy.R;

/**
 * Created by user on 25-03-2018.
 */

public class NearByBusinessAdapter extends RecyclerView.Adapter<NearByBusinessAdapter.BusinessViewHolder> {

    private Context mCtx;
    private List<Organization> business;

    int drawable ;
    public NearByBusinessAdapter(Context ctxt, List<Organization> business, int drawable ) {
        this.mCtx = ctxt;
        this.business = business;
        this.drawable = drawable;
    }


    @Override
    public BusinessViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.horizontal_item, null);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BusinessViewHolder holder, int position) {
        final Organization busns = business.get(position);
        if(busns.getImages() != null)
            Glide.with(mCtx).load(busns.getImages()[0]).into(holder.imageView);
        else
            Glide.with(mCtx).load(drawable).into(holder.imageView);
        holder.textViewTitle.setText(busns.getOrgname());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToOrgDetails(busns.getOrgid());
            }
        });
        holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToOrgDetails(busns.getOrgid());
            }
        });
        holder.nearby_business_address.setText(busns.getOrgaddress());
    }



    private void moveToOrgDetails(String orgid) {
        Intent intent = new Intent(mCtx, BusinessActivity.class);
        intent.putExtra("orgid", orgid);
        mCtx.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return business.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }




    class BusinessViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        ImageView imageView;
        TextView nearby_business_address;

        public BusinessViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.nearby_business_name);
            imageView = itemView.findViewById(R.id.nearby_business_image);
            nearby_business_address = itemView.findViewById(R.id.nearby_business_address);
        }
    }
}
