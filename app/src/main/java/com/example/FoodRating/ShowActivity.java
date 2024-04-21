package com.example.FoodRating;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class ShowActivity extends AppCompatActivity implements View.OnClickListener{
    ViewPager2 viewPager;
    private LinearLayout llMain, llMy;
    private ImageView ivMain, ivMy, ivCurrent;
    private String username;
    private boolean isStudent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPager();
        initTable();


//        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.navigation_home:
//                        replaceFragment(new ScrollingFragment());
//                        break;
//                    case R.id.navigation_account:
//                        replaceFragment(new AccountFragment());
//                        break;
//                }
//                return false;
//            }
//        });
    }

    private void initTable() {
        llMain = findViewById(R.id.id_tab_main);
        llMain.setOnClickListener(this);
        llMy = findViewById(R.id.id_tab_my);
        llMy.setOnClickListener(this);
        ivMain = findViewById(R.id.tab_iv_main);
        ivMy = findViewById(R.id.tab_iv_my);
        ivMain.setSelected(true);
        ivCurrent = ivMain;
    }

    private void initPager() {
        viewPager = findViewById(R.id.viewpager);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle!=null){
            username = bundle.getString("username");
            isStudent = bundle.getBoolean("isStudent");
        }
        if (username ==null){
            username="qwe";
        }
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(MainFragment.newInstance(username,"",isStudent));
        fragments.add(MyFragment.newInstance(username,isStudent));
        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                changeTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    private void changeTab(int position) {
        ivCurrent.setSelected(false);
        switch (position){
            case R.id.id_tab_main:
                viewPager.setCurrentItem(0);
            case 0:
                ivMain.setSelected(true);
                ivCurrent = ivMain;
                break;
            case R.id.id_tab_my:
                viewPager.setCurrentItem(1);
            case 1:
                ivMy.setSelected(true);
                ivCurrent = ivMy;
                break;
        }
    }

    @Override
    public void onClick(View view) {
        changeTab(view.getId());
    }
    //    private void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.framelayout, fragment);
//        transaction.commit();
//    }
}