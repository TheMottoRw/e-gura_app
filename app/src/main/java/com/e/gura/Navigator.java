package com.e.gura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.e.gura.pages.Account;
import com.e.gura.pages.Cart;
import com.e.gura.pages.Category;
import com.e.gura.pages.Erent;
import com.e.gura.pages.Home;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class Navigator extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public FragmentManager fm;
    public FragmentTransaction ft;
    public final Handler handler = new Handler();
    public int page = 0;
    public Runnable runnable;
    public int delay = 3000;
    public Helper helper;
    public DrawerLayout drawer;
    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);
        helper = new Helper(getApplicationContext());
         bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //Customer toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Navigation drawer
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        Intent intent = getIntent();
        if (intent.hasExtra("cart"))
            setUpDefaultFragment("cart");
        else if (intent.hasExtra("category"))
            setUpDefaultFragment("category");
        else if (intent.hasExtra("subcategory")) setUpDefaultFragment("subcategory");
        else if (intent.hasExtra("profile")) setUpDefaultFragment("profile");
        else setUpDefaultFragment("");//default is Home


    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, delay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.home:
                    selectedFragment = new Home();
                    break;
                case R.id.cart:
                    selectedFragment = new Cart();
                    break;
                case R.id.category:
                    selectedFragment = new Category();
                    break;
                case R.id.account:
                    selectedFragment = new Account();
                    break;
                case R.id.erent:
                    selectedFragment = new Erent();
                    break;
                default:
                    selectedFragment = new Home();
                    break;
            }
            fm = getFragmentManager();
            ft = fm.beginTransaction();
            ft.replace(R.id.frame_container, selectedFragment).commit();
            return true;
        }
    };

    void setUpDefaultFragment(String defaults) {
        Fragment selectedFragment;

        if (defaults.isEmpty()) selectedFragment = new Home();
        else if (defaults.equals("cart")) selectedFragment = new Cart();
        else if (defaults.equals("category")) selectedFragment = new Category();
        else if (defaults.equals("profile")) selectedFragment = new Account();
        else if (defaults.equals("subcategory")) {
            selectedFragment = new Home();
            Intent intentdata = getIntent();
            Bundle bundle = new Bundle();
            bundle.putString("subcategory", intentdata.getStringExtra("subcategory"));
            selectedFragment.setArguments(bundle);
        } else selectedFragment = new Home();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, selectedFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        android.app.Fragment fragment = new Home();
        if (id == R.id.nav_home) {
            // Handle the camera action
            fragment = new Home();

            ft.replace(R.id.frame_container, fragment);
        } else if (id == R.id.nav_cart) {
            fragment = new Cart();
            bottomNavigationView.setSelectedItemId(R.id.cart);
        } else if (id == R.id.nav_buy) {
            fragment = new Category();
            bottomNavigationView.setSelectedItemId(R.id.category);
        } else if (id == R.id.nav_sell) {
            fragment = new Account();
            bottomNavigationView.setSelectedItemId(R.id.account);
            Intent intent;
            if (helper.getUserId().equals("0")) {
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("upload", "product");
                Toast.makeText(getApplicationContext(), "You should login to continue", Toast.LENGTH_LONG).show();
            } else {
                intent = new Intent(getApplicationContext(), UploadProduct.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment).commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
