package apps.codette.geobuy;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

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
        if(selectedPos != position) {
          //  Toast.makeText(this, "TESTTTt", Toast.LENGTH_LONG).show();
            deSelectOthers(position);
            switch(position) {
                case 1 : {
                    FragmentManager fm = getSupportFragmentManager();
                    MapFragment mapFragment = new MapFragment();
                    fm.beginTransaction().replace(R.id.dashboard_content, mapFragment).commit();
                    Drawable mIcon= ContextCompat.getDrawable(this, R.drawable.nearby_primary);
                    nearByView.setImageDrawable(mIcon);
                    nearByText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                }
                case 2 : {
                    FragmentManager fm = getSupportFragmentManager();
                    CategoryFragment categoryFragment = new CategoryFragment();
                    fm.beginTransaction().replace(R.id.dashboard_content, categoryFragment).commit();
                    Drawable mIcon= ContextCompat.getDrawable(this, R.drawable.category_primary);
                    categoryView.setImageDrawable(mIcon);
                    categoryText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                }
                case 3 : {
                    Drawable mIcon= ContextCompat.getDrawable(this, R.drawable.home_primary);
                    homeView.setImageDrawable(mIcon);
                    homeText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                }
                case 4 : {
                    Drawable mIcon= ContextCompat.getDrawable(this, R.drawable.prime_primary);
                    primeView.setImageDrawable(mIcon);
                    primeText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                }
                case 5 : {
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
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
    }

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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
