package apps.codette.geobuy;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import apps.codette.ProductReviewsFragment;
import apps.codette.forms.Organization;
import apps.codette.forms.Product;
import apps.codette.forms.Rating;
import apps.codette.forms.Review;
import apps.codette.geobuy.Constants.GeobuyConstants;
import apps.codette.geobuy.Constants.OrgService;
import apps.codette.geobuy.adapters.MyCustomPagerAdapter;
import apps.codette.geobuy.adapters.NearByBusinessAdapter;
import apps.codette.geobuy.adapters.ProductHighlightsAdapter;
import apps.codette.geobuy.adapters.ProductOrgsAdapter;
import apps.codette.geobuy.adapters.ReviewsAdapter;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;
import me.relex.circleindicator.CircleIndicator;

public class ProductDetailsActivity extends AppCompatActivity  implements MenuItem.OnMenuItemClickListener{

    Dialog rate_Review_dialog;
    ProgressDialog pd;
    Button add_to_cart;
    SessionManager sessionManager;
    Menu menu;
    Map<String, ?> userDetails;
    String highLightsJson;
    String reviewsJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUserDetails();

        Intent intent = getIntent();
        final String productId = intent.getStringExtra("productId");
        final String orgId = intent.getStringExtra("orgId");
        getProductDetails(productId, orgId);

    }




    private void moveProductToCart(Product productDetail) {
        if(userDetails.get("useremail") != null) {
            StringBuilder products = new StringBuilder();
            String productId = productDetail.getId();
            String[] cartProducts = null;
            int length=1;
            if(userDetails.get("cart") != null && !userDetails.get("cart").toString().isEmpty()) {
                String cart = (String) userDetails.get("cart");
                if(!cart.contains(productId))
                    products.append(cart+","+productId);
                else {
                    products.append(cart);
                    int quanity = productDetail.getQuanity();
                    productDetail.setQuanity(++quanity);
                }
            }  else {
                products.append(productId);
            }
            SharedPreferences.Editor editor = sessionManager.getEditor();
            editor.putString("cart", products.toString());
            sessionManager.put(editor);
            String user = (String) userDetails.get("useremail");
            syncWithUserCart(productDetail, user);
        } else {
            Intent intent = new Intent(this, SigninActivity.class);
            startActivity(intent);
        }

    }

    private void syncWithUserCart(Product product, String user) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("cart",new Gson().toJson(new Product(product.getId(), product.getQuanity())));
        requestParams.put("useremail",user);
        int count = new OrgService().getCartItems(this);
        ActionItemBadge.update(menu.findItem(R.id.item_samplebadge), count);
        toast(getResources().getString(R.string.product_added_to_cart));
        RestCall.post("addToCart", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //toast(getResources().getString(R.string.review_done));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                toast(getResources().getString(R.string.try_later));
            }
        });
    }

    private void manageAllTextViews(Product product) {
       // TextView product_rating = (TextView) findViewById(R.id.product_rating_text);
       // TextView product_rating_review = (TextView) findViewById(R.id.product_rating_reviews);
        TextView product_tittlr = (TextView) findViewById(R.id.product_tittle);
        TextView product_price_amount = (TextView) findViewById(R.id.product_price_amount);
        TextView product_offer = (TextView) findViewById(R.id.product_offer);
        TextView product_price_old = (TextView) findViewById(R.id.product_price_old);
        TextView product_total_rating = (TextView) findViewById(R.id.product_total_rating);
      //  TextView no_of_ratings = (TextView) findViewById(R.id.no_of_ratings);
        product_price_old.setPaintFlags(product_price_old.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        List<Rating> ratings = product.getRatings();
        float rating = product.getRating();
        String ratingReviews ="";
        product_total_rating.setText(""+rating);
    //    product_rating.setText(""+rating);
        if(ratings != null && !ratings.isEmpty()) {
          //  no_of_ratings.setText(ratings.size()+" ratings");
            ratingReviews = ratingReviews +ratings.size()+" ratings";
        } else {
         //   no_of_ratings.setText(0 +" ratings");
            ratingReviews = ratingReviews + "0 ratings";
        }
        if(product.getReviews() != null && !product.getReviews().isEmpty()) {
            ratingReviews = ratingReviews +", "+ product.getReviews().size() +" reviews";
        } else
            ratingReviews = ratingReviews + ", 0 reviews";

        if(product.getOffer() != 0) {
            float discount = (Float.valueOf(product.getOffer()) /100) *product.getPrice();
            product_price_amount.setText("₹"+(product.getPrice()-discount));
            product_price_old.setText(""+product.getPrice());
            product_offer.setText(""+product.getOffer()+"% off");
        } else {
            product_price_amount.setText("₹"+product.getPrice());
            product_price_old.setText("");
            product_offer.setText("");
        }
        product_tittlr.setText(product.getTitle());
     //   product_rating_review.setText(ratingReviews);
    }

    public void getProductDetails(final String productId,final String orgId) {
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        pd.show();
        RequestParams requestParams = new RequestParams();
        requestParams.put("id",productId);
        RestCall.post("productDetails", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                formProduct(new String(responseBody), orgId);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(pd != null)
                    pd.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });
    }
    Product productDetail;

    private void formProduct(String json, String orgId) {
        Gson gson = new Gson();
        Type type = new TypeToken<Product>() {}.getType();
        Product product = gson.fromJson(json, type);
        if(orgId ==  null)
            productDetail = product.getProductDetails().get(0);
        else {
            List<Product> productList = product.getProductDetails();
            for(Product product1 : productList)
                if(product1.getOrgid().equalsIgnoreCase(orgId)){
                    productDetail = product1;
                }
        }

        Log.i("product",product.toString());
        Log.i("productDetail",productDetail.toString());
        formProductHighLights(productDetail.getHighlights());
        formReviews(productDetail.getReviews());
        formImages(productDetail.getImage());
        formProductOrgs(product.getProductDetails(),productDetail.getOrgid());
      //  manageRateAndReview(productDetail.getId(), productDetail.getOrgid(), productDetail.getReviews());
        manageAllTextViews(productDetail);
        pd.dismiss();
        add_to_cart = findViewById(R.id.add_to_cart);
        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveProductToCart(productDetail);
            }
        });
        assignTabLayout();
    }

    private void formProductOrgs(List<Product> productDetails, String orgId) {
        if(productDetails != null && !productDetails.isEmpty()) {
            RecyclerView rv = findViewById(R.id.products_orgs_recyclerview);
            ProductOrgsAdapter adapter =  new ProductOrgsAdapter( productDetails, orgId, this);
            LinearLayoutManager HorizontalLayout  = new LinearLayoutManager(ProductDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(HorizontalLayout);
            rv.setAdapter(adapter);
        }
    }


    private void formOrgs(List<Organization> organizations){
        android.widget.LinearLayout ll = findViewById(R.id.business_search_layout);
        if(organizations != null && !organizations.isEmpty()){
            showView(ll);
            RecyclerView rv = findViewById(R.id.nearby_business__recycler_view);
            NearByBusinessAdapter rbA = new NearByBusinessAdapter(this,organizations, getImage("store"));
            LinearLayoutManager HorizontalLayout  = new LinearLayoutManager(ProductDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(HorizontalLayout);
            rv.setAdapter(rbA);
        } else{
            hideView(ll);
        }
    }


    public int getImage(String imageName) {
        int drawableResourceId = this.getResources().getIdentifier(imageName, "drawable", this.getPackageName());
        return drawableResourceId;
    }

    private void formImages(String[] images) {
        String img[] = {"https://firebasestorage.googleapis.com/v0/b/pingme-191816.appspot.com/o/test%2Fproduct_nil.gif?alt=media&token=a6ae5d11-d8e7-47f7-a0fd-7d7ea7ea87cb"};
        if(images !=  null && images.length > 0) {
            img = images;
        }
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        ViewPager viewPager = (ViewPager) findViewById(R.id.product_viewPager);
        MyCustomPagerAdapter myCustomPagerAdapter = new MyCustomPagerAdapter(this, img);
        viewPager.setAdapter(myCustomPagerAdapter);
        indicator.setViewPager(viewPager);
    }

    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }

    private void manageRateAndReview(final String productId, final String orgId, final List<Review> reviews) {
        Button rateAndReviewButton = findViewById(R.id.rate_and_review_button);
        rateAndReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageRateAndReviewPopup(productId, orgId, reviews);
            }
        });
    }

    private void manageRateAndReviewPopup(final String productId, final String orgId, final List<Review> reviews) {
        rate_Review_dialog = new Dialog(this);
        rate_Review_dialog.setContentView(R.layout.rate_review_popup);
        final RatingBar ratingBar =(RatingBar) rate_Review_dialog.findViewById(R.id.product_rating_bar);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float rating = ratingBar.getRating();
                TextView tv = rate_Review_dialog.findViewById(R.id.product_rating_bar_text);
                tv.setText(rating+"");
                return false;
            }
        });

        Button submitButton = rate_Review_dialog.findViewById(R.id.review_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRateAndReview(productId, orgId, reviews);
                rate_Review_dialog.dismiss();
            }
        });
        Button cancelButton = rate_Review_dialog.findViewById(R.id.review_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rate_Review_dialog.dismiss();
            }
        });
        rate_Review_dialog.show();
    }
    List<Review> newReviews = null;

    private void submitRateAndReview(final String productId, final String orgId, final List<Review> reviews) {
        if(reviews != null)
            newReviews = new ArrayList<Review>(reviews);
        else
            newReviews = new ArrayList<Review>();
        RatingBar ratingBar =(RatingBar) rate_Review_dialog.findViewById(R.id.product_rating_bar);
        EditText shrtText = (EditText) rate_Review_dialog.findViewById(R.id.short_review);
        EditText reviewText = (EditText) rate_Review_dialog.findViewById(R.id.detailed_review);
        SimpleDateFormat ft = new SimpleDateFormat ("dd, MMM YY");
        String time = ft.format(new Date());

        RequestParams requestParams = new RequestParams();
        requestParams.put("id",productId);
        requestParams.put("table", GeobuyConstants.ORG_PRODUCTS_TABLE);
        requestParams.put("ratings",ratingBar.getRating());
        requestParams.put("heading",shrtText.getText());
        requestParams.put("review",reviewText.getText());
        requestParams.put("time",time);
        requestParams.put("user",(String)userDetails.get("username"));
        requestParams.put("useremail",(String)userDetails.get("useremail"));
        Review review = new Review(UUID.randomUUID().toString(), shrtText.getText().toString(),reviewText.getText().toString(), time,ratingBar.getRating(),"user" );
        newReviews.add(review);
        RestCall.post("rateAndReview", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                toast(getResources().getString(R.string.review_done));
                formReviews(newReviews);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(pd != null)
                    pd.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });
    }


    private void formReviews(List<Review> reviews) {
        if(reviews != null) {
            reviewsJson = new Gson().toJson(reviews);
        }
        /*RecyclerView rv = (RecyclerView) findViewById(R.id.product_reviews_recyclerview);
        if(reviews != null) {
            ReviewsAdapter adapter = new ReviewsAdapter(this, reviews);
            LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(this);
            rv.setLayoutManager(linearLayoutManager);
            rv.setAdapter(adapter);
          //  rv.setNestedScrollingEnabled(false);
        } else
            hideView(rv);*/

    }

    private void formProductHighLights(List<String> highLights) {
        Gson json = new Gson();
        highLightsJson = json.toJson(highLights);
        /*
        LinearLayout ll = findViewById(R.id.products_highlight_layout);
        RecyclerView rv = (RecyclerView) findViewById(R.id.product_highlights_recyclerview);
        if(highLights != null) {
            showView(ll);
            ProductHighlightsAdapter adapter = new ProductHighlightsAdapter(this, highLights);
            LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(this);
            rv.setLayoutManager(linearLayoutManager);
            rv.setAdapter(adapter);
        } else {
            hideView(ll);
        }
        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_UP);*/
        //rv.setNestedScrollingEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        Drawable dr = getResources().getDrawable(R.drawable.kart);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        // Scale it to 50 x 50
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 75, 75, true));
        // Set your new, scaled drawable "d"
        BadgeStyle badgeStyle = ActionItemBadge.BadgeStyles.DARK_GREY.getStyle();
        MenuItem menuItem = menu.findItem(R.id.item_samplebadge);
        menuItem.setOnMenuItemClickListener(this);
        OrgService orgService = new OrgService();
        int count = orgService.getCartItems(this);
        ActionItemBadge.update(this, menuItem,  d, badgeStyle, count);
        return true;
    }

    private void moveToCart() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }


    private void showView (View... views){
        for(View v: views){
            v.setVisibility(View.VISIBLE);

        }

    }
    private void hideView (View... views){
        for(View v: views){
            v.setVisibility(View.GONE);

        }
    }



    @Override
    public void onResume(){
        super.onResume();
        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUserDetails();
        // put your code here...
        if(menu != null) {
            int count = new OrgService().getCartItems(this);
            ActionItemBadge actionItemBadge = new ActionItemBadge();
            actionItemBadge.update(menu.findItem(R.id.item_samplebadge), count);

        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
        return false;
    }

    private SectionsPagerAdapter  mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private void assignTabLayout() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);


        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.i("sectionNumber",position + 1+"");
            position = position+1;
            Fragment fragment;
            if(position == 1) {
                ProductHighlights productHighlights = new ProductHighlights();
                Bundle bundle = new Bundle();
                bundle.putString("highlights", highLightsJson);
                productHighlights.setArguments(bundle);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //setting margins around imageimageview
                params.height=300; //left, top, right, bottom
                mViewPager.setLayoutParams(params);
                return productHighlights;
            } else if(position == 2) {
                ProductReviewsFragment productReviewsFragment = new ProductReviewsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("reviews", reviewsJson);
                productReviewsFragment.setArguments(bundle);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //setting margins around imageimageview
                params.height=500; //left, top, right, bottom
                mViewPager.setLayoutParams(params);
                return productReviewsFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }

}
