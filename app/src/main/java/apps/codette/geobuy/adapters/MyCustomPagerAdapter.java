package apps.codette.geobuy.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import apps.codette.geobuy.R;

/**
 * Created by user on 06-04-2018.
 */

public class  MyCustomPagerAdapter extends PagerAdapter{
    Context context;
    String images[];
    LayoutInflater layoutInflater;


    public MyCustomPagerAdapter(Context context, String images[]) {
        this.context = context;
        this.images = images;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
