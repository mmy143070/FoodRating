package com.example.FoodRating;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MyDishActivity extends AppCompatActivity {
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dish);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle!=null){
            username = bundle.getString("username");
        }
        MyDishFragment fragment = MyDishFragment.newInstance(username, username);

        // 获取FragmentManager和FragmentTransaction
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // 将fragment添加到容器中
        transaction.add(R.id.fragment_container_view, fragment);
        transaction.commit();

    }
}