package com.hsproject.actlogger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener,
        BehaviorFragment.OnFragmentInteractionListener,
        StatisticsFragment.OnFragmentInteractionListener,
        ActsettingFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    // FrameLayout에 각 메뉴의 Fragment를 바꿔 줌
    private FragmentManager fragmentManager = getSupportFragmentManager();
    // 4개의 메뉴에 들어갈 Fragment들
    private BehaviorFragment behaviorfragment = new BehaviorFragment();
    private StatisticsFragment statisticsfragment = new StatisticsFragment();
    private ActsettingFragment actsettingfragment = new ActsettingFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 왼쪽 툴바
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // 왼쪽 툴바 아이템 선택 리스너 등록
        navigationView.setNavigationItemSelectedListener(this);

        // 아래쪽 메뉴
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        // 첫 화면 지정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, behaviorfragment).commitAllowingStateLoss();
        // 아래쪽 툴바 아이템이 선택 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        boolean isLeftNavSelected = true;
        int id = item.getItemId();

        // 왼쪽 툴바 선택(Activity Change)
        switch (id) {
            case R.id.nav_gpstest: { // GPS 테스트 액티비티 실행
                Intent intent = new Intent(MainActivity.this, GpsTestActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.nav_share: {

                break;
            }
            case R.id.nav_send: {

                break;
            }
            default:{
                isLeftNavSelected = false;
            }
        }

        if(isLeftNavSelected) {
            Log.d(TAG,"ActivityTransaction");
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        //아래쪽 툴바 선택(Fragment Change)
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Log.d(TAG,"FragmentTransaction");
        switch (id) {
            case R.id.navigation_behavior: {
                transaction.replace(R.id.frame_layout, behaviorfragment).commitAllowingStateLoss();
                break;
            }
            case R.id.navigation_statistics: {
                transaction.replace(R.id.frame_layout, statisticsfragment).commitAllowingStateLoss();
                break;
            }
            case R.id.navigation_actsetting: {
                transaction.replace(R.id.frame_layout, actsettingfragment).commitAllowingStateLoss();
                break;
            }
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
}
