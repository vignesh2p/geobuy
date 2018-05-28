package apps.codette.geobuy;

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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;

import apps.codette.geobuy.Constants.OrgService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


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

    Menu menu;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        manageBottomToolBar();
        setViewSelected(1);

        manageSearchBar();
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
        homeView = findViewById(R.id.home_button);
        homeText = findViewById(R.id.home_button_text);
        homeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(3);
            }
        });

        categoryView = findViewById(R.id.category_button);
        categoryText = findViewById(R.id.category_text);
        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(2);
            }
        });

        nearByView = findViewById(R.id.nearby_button);
        nearByText = findViewById(R.id.nearby_button_text);
        nearByView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(1);
            }
        });

        primeView = findViewById(R.id.prime_button);
        primeText = findViewById(R.id.prime_button_text);
        primeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(4);
            }
        });

        accountView = findViewById(R.id.account_button);
        accountText = findViewById(R.id.account_button_text);
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
                    CategoryFragment categoryFragment = new CategoryFragment();
                    fm.beginTransaction().replace(R.id.dashboard_content, categoryFragment).commit();
                    Drawable mIcon= ContextCompat.getDrawable(this, R.drawable.category_primary);
                    categoryView.setImageDrawable(mIcon);
                    categoryText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                }
                case 3 : {
                    hideView(welcomeBarLayout);
                    showView(appBarLayout);
                    Drawable mIcon= ContextCompat.getDrawable(this, R.drawable.home_primary);
                    homeView.setImageDrawable(mIcon);
                    homeText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                }
                case 4 : {
                    hideView(welcomeBarLayout);
                    showView(appBarLayout);
                    Drawable mIcon= ContextCompat.getDrawable(this, R.drawable.prime_primary);
                    primeView.setImageDrawable(mIcon);
                    primeText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                }
                case 5 : {
                    hideView(appBarLayout);
                    showView(welcomeBarLayout);
                    FragmentManager fm = getSupportFragmentManager();
                    UserFragment userFragment = new UserFragment();
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
            primeView.setImageDrawable(pIcon);
            primeText.setTextColor(getResources().getColor(R.color.black_overlay));
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.item_samplebadge) {
            Intent intent = new Intent(this, CartActivity.class);
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
        if (requestCode == 1000){
            mapFragment.onActivityResult(requestCode, resultCode, data);
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        if(menu != null) {
            int count = new OrgService().getCartItems(this);
            ActionItemBadge.update(menu.findItem(R.id.item_samplebadge), count);
        }

    }
}
