package apps.codette.geobuy.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import apps.codette.geobuy.ImageViewActivity;
import apps.codette.geobuy.R;

/**
 * Created by user on 06-04-2018.
 */

public class  MyCustomPagerAdapter extends PagerAdapter{
    Context context;
    String images[];
    LayoutInflater layoutInflater;
    boolean doOpen=true;


    public MyCustomPagerAdapter(Context context, String images[]) {
        this.context = context;
        this.images = images;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDoOpen(boolean value) {
        doOpen = value;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.business_image, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        //imageView.setImageResource(images[position]);


        Log.i("url", images[position]);
        Glide.with(context)
                .load(images[position])
                .fitCenter()
                .into(imageView);

        container.addView(itemView);
        /*Glide.with(context)
                .load(images[position])
                .into(holder.imageView);*/
        //listening to image click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToImageActivity();
            }
        });

        return itemView;
    }

    private void goToImageActivity(){
        if(doOpen) {
            Intent intent = new Intent(context, ImageViewActivity.class);
            intent.putExtra("images", images);
            context.startActivity(intent);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
