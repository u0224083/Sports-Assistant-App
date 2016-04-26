package com.videumcorp.desarrolladorandroid.navigationdrawerandroiddesignsupportlibrary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBar actionBar;
    TextView textView;
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }

        setupNavigationDrawerContent(navigationView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        textView = (TextView) findViewById(R.id.textView);
                        startButton = (Button)findViewById(R.id.start_button);

                        switch (menuItem.getItemId()) {
                            case R.id.item_navigation_drawer_inbox:
                                menuItem.setChecked(true);
                                textView.setText("　　一般人對於運動的了解，不外乎促進健康、減重，甚至拓展社交生活。但運動對於身心產生的長期影響，可能遠遠超出妳的想像。\n" +
                                        "　　這個APP可以計算你運動時所花費的時間、速度和消耗的卡路里，還可以紀錄您所跑步的路徑，但時速4km/hr以下無法計算。\n" +
                                        "操作說明 : 進入後請開啟GPS->抓到您的座標->按下”時間開始”\n結束後按下”時間停止”上面就會顯示您這次運動消耗的卡路里。");
                                textView.setTextColor(Color.BLACK);
                                textView.setTextSize(20);
                                startButton.setVisibility(View.VISIBLE);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_starred:
                                menuItem.setChecked(true);

                                startButton.setVisibility(View.INVISIBLE);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent intentToTable = new Intent(MainActivity.this, TableActivity.class);
                                startActivity(intentToTable);
                                return true;

                            case R.id.item_navigation_drawer_drafts:
                                menuItem.setChecked(true);

                                startButton.setVisibility(View.INVISIBLE);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent intentToMe = new Intent(MainActivity.this, Me.class);
                                startActivity(intentToMe);
                                return true;

                            case R.id.item_just_for_test:
                                menuItem.setChecked(true);

                                startButton.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "Launching " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent intent2 = new Intent(MainActivity.this, MapActivity.class);
                                startActivity(intent2);
                                return true;
                        }
                        return true;
                    }
                });
    }

    public void setOnButtonClick(View view) {
        Intent intentToMap = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intentToMap);
    }
}

