package com.example.FoodRating;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.FoodRating.database.MongoDB;
import com.example.FoodRating.entity.User;
import com.example.FoodRating.entity.UserDocument;
import com.example.FoodRating.util.ToastUtil;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {


    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //初始化获取的布局元素
        initiate();
    }

    //获取注册按钮，预先定义，下方输入框同理
    private Button register;
    private EditText username, password, password2;
    private int code;
    private Context ctx = this;
    private MongoDB mongoDB;
    private RadioGroup identity;
    private boolean isStudent = true;
    private boolean isStaff = false;
    private boolean isOK = false;


    //初始化获取的布局元素
    public void initiate() {
        username = findViewById(R.id.rusername);
        password = findViewById(R.id.rpassword);
        password2 = findViewById(R.id.rpassword2);
        register = findViewById(R.id.mregister);
        identity = findViewById(R.id.identity);
        findViewById(R.id.check).setOnClickListener(this);
        findViewById(R.id.mregister).setOnClickListener(this);
        findViewById(R.id.fh).setOnClickListener(this);
        password.setOnFocusChangeListener(this);
        gson = new Gson();
        mongoDB = new MongoDB();
        identity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.stu_button:
                        isStudent = true;
                        isStaff = false;
                        break;
                    case R.id.sta_button:
                        isStudent = false;
                        isStaff = true;
                        break;
                }
            }
        });
    }

    public void check() throws IOException {
        User user;
        String name = username.getText().toString();
        user = new User(name, isStudent);
        String juser = gson.toJson(user);
        Log.i("tag", juser);
        ToastUtil.show(ctx, "检查中请稍后");
        Response response = mongoDB.FindOne("users", juser, true);
        Log.i("tag", String.valueOf(response.code()));
        String j = response.body().string();
        Log.i("tag", j);
        UserDocument document = gson.fromJson(j, UserDocument.class);
        Log.i("tag", String.valueOf(document.getUser() == null));

        if (document.getUser() == null) {
            ToastUtil.show(ctx, "用户名可用");
            isOK = true;
        } else {
            ToastUtil.show(ctx, "用户名不可用");
            isOK = false;
        }
    }

    //注册逻辑实现
    public void register() {
        String ru = username.getText().toString();
        String rps = password.getText().toString();
        String rps2 = password2.getText().toString();
        User user;
        if (ru.equals("")) {
            ToastUtil.show(this, "账号不能为空");
        } else if (rps.equals("")) {
            ToastUtil.show(this, "密码不能为空");
        } else if (!rps.equals(rps2)) {
            ToastUtil.show(this, "密码不一致");
        } else if (!isStaff && !isStudent) {
            ToastUtil.show(this, "请选择身份");
        } else if (!isOK) {
            ToastUtil.show(this, "用户名未检查或不可用");
        } else {
            user = new User(ru, rps, isStudent);
            String juser = gson.toJson(user);
            Log.i("tag", juser);
//            OkHttpClient client = new OkHttpClient().newBuilder()
//                    .build();
            ToastUtil.show(ctx, "注册中请稍后");
            Response response = mongoDB.InsertOne("users", juser, true);
            // 使用enqueue方法发送异步post请求
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    // 处理失败的情况
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    // 处理成功的情况
            Log.i("tag", String.valueOf(response.code()));
//                    code = response.code();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (code == 201) {
//                                ToastUtil.show(ctx, "注册成功");
//                                register.setEnabled(false);
//                                register.setTextColor(0xFFD0EFC6);
//                            } else {
//                                ToastUtil.show(ctx, "注册失败，请稍后重试");
//                            }
//                        }
//                    });
//                }
//            });

            code = response.code();

            if (code == 201) {
                ToastUtil.show(ctx, "注册成功");
                register.setEnabled(false);
                register.setTextColor(0xFFD0EFC6);
            } else {
                ToastUtil.show(ctx, "注册失败，请稍后重试");
            }

        }
    }

//    private void extracted(String juser) {
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        String json = "{\n" +
//                "    \"dataSource\": \"Cluster0\",\n" +
//                "    \"database\": \"androidDB\",\n" +
//                "    \"collection\": \"users\",\n" +
//                "    \"document\": "+ juser +"}";
////                    MediaType mediaType = MediaType.parse("application/ejson");
//        RequestBody body = RequestBody.create(json.getBytes(StandardCharsets.UTF_8 ));
//        Request request = new Request.Builder()
//                .get()
//                .url("https://us-east-2.aws.data.mongodb-api.com/app/data-bwxcu/endpoint/data/v1/action/insertOne")
//                .addHeader("Content-Type", "application/ejson")
//                .addHeader("apiKey", "rdX9XZwcsHKZm0qpBk3j6yOoASMBNWbNmNwvjmvmhnpvSqsAHcKpCwJDeebMNr6Y")
////                            .addHeader("Accept", "application/json")
//                .post(body)
//                .build();
//        ToastUtil.show(ctx, "注册中请稍后");
//
//// 使用enqueue方法发送异步post请求
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // 处理失败的情况
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                // 处理成功的情况
//                Log.i("tag", String.valueOf(response.code()));
//                code = response.code();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (code==201) {
//                            ToastUtil.show(ctx, "注册成功");
//                            register.setEnabled(false);
//                            register.setTextColor(0xFFD0EFC6);
//                        } else {
//                            ToastUtil.show(ctx, "注册失败，请稍后重试");
//                        }
//                    }
//                });
//            }
//        });
//    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    //监听按钮点击事件
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mregister) {
            register();
        } else if (v.getId() == R.id.check) {
            try {
                check();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (v.getId() == R.id.fh) {
            finish();
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
}