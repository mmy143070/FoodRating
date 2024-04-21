package com.example.FoodRating;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.FoodRating.database.MongoDB;
import com.example.FoodRating.entity.User;
import com.example.FoodRating.entity.UserDocument;
import com.example.FoodRating.util.ToastUtil;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private Handler handler = new Handler(Looper.getMainLooper());
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private EditText username, password;
    private RadioGroup identity;
    private boolean isStudent = false;
    private boolean isStaff = false;
    private Gson gson;
    private MongoDB mongoDB;
    private int code;
    private Context ctx= this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
//        findViewById(R.id.test).setOnClickListener(this);
        gson = new Gson();
        username = findViewById(R.id.lusername);
        password = findViewById(R.id.lpassword);
        identity = findViewById(R.id.lidentity);
        mongoDB = new MongoDB();
        okHttpClient = new OkHttpClient();
        identity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.lstu_button:
                        isStudent = true;
                        isStaff = false;
                        break;
                    case R.id.lsta_button:
                        isStudent = false;
                        isStaff = true;
                        break;
                }
            }
        });
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            //跳转到注册页面
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.login) {
            String lu = username.getText().toString();
            String lps = password.getText().toString();
            if (lu.equals("")) {
                ToastUtil.show(this, "账号不能为空");
            } else if (lps.equals("")) {
                ToastUtil.show(this, "密码不能为空");
            } else if (!isStaff && !isStudent) {
                ToastUtil.show(this, "请选择身份");
            } else {
                User user;
                user = new User(lu, lps, isStudent);
                String juser = gson.toJson(user);
                Log.i("tag", juser);
                ToastUtil.show(ctx, "登录中请稍后");
                Response response = mongoDB.FindOne("users", juser,true);
//                Log.i("tag12", String.valueOf(response.body().string()));
                code = response.code();
                UserDocument document;
                try {
                    document = gson.fromJson(response.body().string(), UserDocument.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (document.getUser() == null) {
                    ToastUtil.show(ctx, "登录失败，请检查用户名和密码");
                } else {
                    ToastUtil.show(ctx, "登录成功，跳转中");
                    Bundle bundle = new Bundle();
                    bundle.putString("username", lu);
                    bundle.putBoolean("isStudent",isStudent);
                    Intent intent = new Intent(ctx, ShowActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (code == 200) {
//                            ToastUtil.show(ctx, "登录成功");
//                            Intent intent = new Intent(ctx, ShowActivity.class);
//                            startActivity(intent);
//                        } else {
//                            ToastUtil.show(ctx, "登录失败，请检查用户名和密码");
//                        }
//                    }
//                });

//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                        Log.i("tag12", String.valueOf(response.body().string()));
//                        code = response.code();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (code == 200) {
//                                    ToastUtil.show(ctx, "登录成功");
//                                    Intent intent = new Intent(ctx, ShowActivity.class);
//                                    startActivity(intent);
//                                } else {
//                                    ToastUtil.show(ctx, "登录失败，请检查用户名和密码");
//                                }
//                            }
//                        });
//
//                    }
//                });


            }
        }
//        } else if (v.getId() == R.id.test) {
////                    Request request = new Request.Builder().url("https://www.httpbin.org/get?a=1&b=2").build();
//            OkHttpClient client = new OkHttpClient().newBuilder()
//                    .build();
//            MediaType mediaType = MediaType.parse("application/json");
//            RequestBody body = RequestBody.create(mediaType, "{\n    \"collection\":\"shipwrecks\",\n    \"database\":\"sample_geospatial\",\n    \"dataSource\":\"Cluster0\",\n    \"projection\": {\"_id\": 1}\n}");
//            Request request = new Request.Builder()
//                    .url("https://us-east-2.aws.data.mongodb-api.com/app/data-bwxcu/endpoint/data/v1/action/findOne")
//                    .method("POST", body)
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("Access-Control-Request-Headers", "*")
//                    .addHeader("api-key", "rdX9XZwcsHKZm0qpBk3j6yOoASMBNWbNmNwvjmvmhnpvSqsAHcKpCwJDeebMNr6Y")
//                    .build();
////                        Response response = client.newCall(request).execute();
//            Call call = okHttpClient.newCall(request);
//
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//                }
//
//                @Override
//                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//
//                    Log.i("tag", response.body().string());
//                }
//            });
//
//
////            new Thread(){
////                @Override
////                public void run() {
////                    try {
////                        String context = OkHttpUtils.getInstance().doGet("https://www.httpbin.org/get/");
////                        System.out.println(context);
//////                        OkHttpClient client = new OkHttpClient().newBuilder()
//////                                .build();
//////                        MediaType mediaType = MediaType.parse("application/json");
//////                        RequestBody body = RequestBody.create(mediaType, "{\n    \"collection\":\"shipwrecks\",\n    \"database\":\"sample_geospatial\",\n    \"dataSource\":\"Cluster0\",\n    \"projection\": {\"_id\": 1}\n}");
//////                        Request request = new Request.Builder()
//////                                .url("https://us-east-2.aws.data.mongodb-api.com/app/data-bwxcu/endpoint/data/v1/action/findOne")
//////                                .method("POST", body)
//////                                .addHeader("Content-Type", "application/json")
//////                                .addHeader("Access-Control-Request-Headers", "*")
//////                                .addHeader("api-key", "rdX9XZwcsHKZm0qpBk3j6yOoASMBNWbNmNwvjmvmhnpvSqsAHcKpCwJDeebMNr6Y")
//////                                .build();
//////                        Response response = client.newCall(request).execute();
////                        handler.post(new Runnable() {
////                            @Override
////                            public void run() {
////                                toast = context;
////                            }
////                        });
////                    }catch (Exception e){
////                        e.printStackTrace();
////                    }
////                    super.run();
////                }
////            }.start();
////            ToastUtil.show(this, toast);
//        }
    }
}