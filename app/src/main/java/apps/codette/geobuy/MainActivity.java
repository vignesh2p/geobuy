package apps.codette.geobuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import apps.codette.forms.CategoryMaster;
import apps.codette.geobuy.Constants.GeobuyConstants;
import apps.codette.geobuy.Constants.OrgService;
import apps.codette.geobuy.adapters.CategoryMasterAdapter;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private String module;

    private LinearLayout homeLayout;
    private LinearLayout accountll;
    private LinearLayout nearbyll;
    private LinearLayout categoryll;

    private ImageView homeView;
    private TextView homeText;

    private ImageView categoryView;
    private TextView categoryText;

    private ImageView nearByView;
    private TextView nearByText;


    private ImageView primeView;
    private TextView primeText;

    private ImageView accountView;
    private TextView accountText;

    private int selectedPos;

    MapFragment mapFragment;

    CategoryFragment categoryFragment;

    HomeFragment homeFragment;

    UserFragment userFragment;

    SessionManager sessionManager;
    Menu menu;
    Map<String, ?> userDetails;

    NavigationView navigationView;

    private boolean isRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Geobuy");
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        manageBottomToolBar();
        setViewSelected(1);

        manageSearchBar();
        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUserDetails();
    }

    private void manageSearchBar() {
        Button searchButton = findViewById(R.id.geobuy_search);
        searchButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               moveToSearch();
           }
        });
    }

    private void moveToSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void manageBottomToolBar() {
        homeLayout = findViewById(R.id.home_layout);
        homeView = findViewById(R.id.home_button);
        homeText = findViewById(R.id.home_button_text);
        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(3);
            }
        });
        homeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(3);
            }
        });

        categoryll = findViewById(R.id.category_ll);
        categoryView = findViewById(R.id.category_button);
        categoryText = findViewById(R.id.category_text);
        categoryll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(2);
            }
        });
        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(2);
            }
        });

        nearbyll = findViewById(R.id.nearby_ll);
        nearByView = findViewById(R.id.nearby_button);
        nearByText = findViewById(R.id.nearby_button_text);
        nearbyll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(1);
            }
        });
        nearByView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(1);
            }
        });
        /*primeView = findViewById(R.id.prime_button);
        primeText = findViewById(R.id.prime_button_text);
        primeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(4);
            }
        });
*/
        accountll = findViewById(R.id.account_ll);
        accountView = findViewById(R.id.account_button);
        accountText = findViewById(R.id.account_button_text);
        accountll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(5);
            }
        });
        accountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(5);
            }
        });
    }

    private void setViewSelected(int position) {
        //
        LinearLayout appBarLayout = findViewById(R.id.default_app_bar);
        LinearLayout welcomeBarLayout = findViewById(R.id.welcometoolbarLaout);
        if(selectedPos != position) {
          //  Toast.makeText(this, "TESTTTt", Toast.LENGTH_LONG).show();
            deSelectOthers(position);
            switch(position) {
                case 1 : {
                    hideView(welcomeBarLayout);
                    showView(appBarLayout);
                    FragmentManager fm = getSupportFragmentManager();
                    if(mapFragment == null)
                        mapFragment = new MapFragment();
                    fm.beginTransaction().replace(R.id.dashboard_content, mapFragment).commit();
                    Drawable mIcon= ContextCompat.getDrawable(this, R.drawable.nearby_primary);
                    nearByView.setImageDrawable(mIcon);
                    nearByText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                }
                case 2 : {
                    hideView(welcomeBarLayout);
                    showView(appBarLayout);
                    FragmentManager fm = getSupportFragmentManager();
                    if(categoryFragment == null)
                        categoryFragment = new CategoryFragment();
                    fm.beginTransaction().replace(R.id.dashboard_content, categoryFragment).commit();
                    Drawable mIcon= ContextCompat.getDrawable(this, R.drawable.category_primary);
                    categoryView.setImageDrawable(mIcon);
                    categoryText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                }
                case 3 : {
                    hideView(welcomeBarLayout);
                    showView(appBarLayout);
                    FragmentManager fm = getSupportFragmentManager();
                    if(homeFragment == null)
                        homeFragment = new HomeFragment();
                    fm.beginTransaction().replace(R.id.dashboard_content, homeFragment).commit();
                    Drawable mIcon= ContextCompat.getDrawable(this, R.drawable.home_primary);
                    homeView.setImageDrawable(mIcon);
                    homeText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                }
                case 4 : {
                    hideView(welcomeBarLayout);
                    showView(appBarLayout);
                    Drawable mIcon= ContextCompat.getDrawable(this, R.drawable.prime_primary);
                   /* primeView.setImageDrawable(mIcon);
                    primeText.setTextColor(getResources().getColor(R.color.colorPrimary));*/
                    break;
                }
                case 5 : {
                    hideView(appBarLayout);
                    showView(welcomeBarLayout);
                    FragmentManager fm = getSupportFragmentManager();
                    if(userFragment == null)
                        userFragment = new UserFragment();
                    fm.beginTransaction().replace(R.id.dashboard_content, userFragment).commit();
                    Drawable mIcon= ContextCompat.getDrawable(this, R.drawable.account_primary);
                    accountView.setImageDrawable(mIcon);
                    accountText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                }

            }
            selectedPos = position;
        }

    }

    private void deSelectOthers(int selectedPosition){
        if(selectedPosition != 1) {
            Drawable mIcon= ContextCompat.getDrawable(this, R.drawable.nearbyblack);
            nearByView.setImageDrawable(mIcon);
            nearByText.setTextColor(getResources().getColor(R.color.black_overlay));
        }

        if(selectedPosition != 2) {
            Drawable cIcon= ContextCompat.getDrawable(this, R.drawable.category_black);
            categoryView.setImageDrawable(cIcon);
            categoryText.setTextColor(getResources().getColor(R.color.black_overlay));
        }

        if(selectedPosition != 3) {
            Drawable hIcon= ContextCompat.getDrawable(this, R.drawable.home_black);
            homeView.setImageDrawable(hIcon);
            homeText.setTextColor(getResources().getColor(R.color.black_overlay));
        }

        if(selectedPosition != 4) {
            Drawable pIcon= ContextCompat.getDrawable(this, R.drawable.prime_black);
            /*primeView.setImageDrawable(pIcon);
            primeText.setTextColor(getResources().getColor(R.color.black_overlay));*/
        }

        if(selectedPosition != 5) {
            Drawable aIcon= ContextCompat.getDrawable(this, R.drawable.account_black);
            accountView.setImageDrawable(aIcon);
            accountText.setTextColor(getResources().getColor(R.color.black_overlay));
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Read your drawable from somewhere
        Drawable dr = getResources().getDrawable(R.drawable.kart);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        // Scale it to 50 x 50
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 75, 75, true));
        // Set your new, scaled drawable "d"
        BadgeStyle badgeStyle = ActionItemBadge.BadgeStyles.DARK_GREY.getStyle();
        MenuItem menuItem = menu.findItem(R.id.item_samplebadge);
        OrgService orgService = new OrgService();
        int count = orgService.getCartItems(this);
        ActionItemBadge.update(this, menuItem,  d, badgeStyle, count);
        //getCategoryMaster();
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.item_samplebadge) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        } else if( id == R.id.my_cart) {
            if(userDetails.get("useremail") != null) {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, SigninActivity.class);
                startActivity(intent);
            }
        } else if(id == R.id.my_orders) {
            if(userDetails.get("useremail") != null) {
                Intent intent = new Intent(this, OrderDetailsActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, SigninActivity.class);
                startActivity(intent);
            }
        } else if(id == R.id.wish_list) {
            if(userDetails.get("useremail") != null) {
                Intent intent = new Intent(this, WishListActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, SigninActivity.class);
                startActivity(intent);
            }

        } else if(id == R.id.notifications || id ==R.id.action_notifications) {
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_samplebadge: {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_notifications: {
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GeobuyConstants.REQUEST_LOCATION){
            mapFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == GeobuyConstants.HOME_REQUEST_LOCATION){
            homeFragment.onActivityResult(requestCode, resultCode, data);
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        sessionManager = null;
        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUserDetails();
        // put your code here...
        if(menu != null) {
            int count = new OrgService().getCartItems(this);
            ActionItemBadge actionItemBadge = new ActionItemBadge();
            actionItemBadge.update(menu.findItem(R.id.item_samplebadge), count);
        }
    }




    private void formUiforCategory(String categoryJson) {
        toast(categoryJson);
        Gson gson = new Gson();
        Type type = new TypeToken<List<CategoryMaster>>() {}.getType();
        List<CategoryMaster> cts  = gson.fromJson(categoryJson, type);
        SubMenu subMenu = menu.addSubMenu("Categories");
        int counter =0;
        for(CategoryMaster categoryMaster : cts) {
            subMenu.add(counter, Menu.FIRST + counter, Menu.FIRST, categoryMaster.getName());
            counter++;
        }
        navigationView.invalidate();
      //  invalidateOptionsMenu();

    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public  void setModule(String module){ this.module =module; }
    public String getModule(){return module;}

    public void setRunning(boolean isRunning) {this.isRunning = isRunning;}
    public boolean isBannerRunning() {return isRunning;}
}
