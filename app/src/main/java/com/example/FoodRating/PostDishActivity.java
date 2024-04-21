package com.example.FoodRating;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.FoodRating.database.MongoDB;
import com.example.FoodRating.entity.Comment;
import com.example.FoodRating.entity.Dish;
import com.example.FoodRating.util.ToastUtil;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import okhttp3.Response;


public class PostDishActivity extends AppCompatActivity implements View.OnClickListener {
    Button imageSelect, postButton;
    EditText foodName, intro;
    ImageView imageView;
    String username = "test", path = null,ima_string;
    MongoDB mongoDB;
    Uri uri = null;
    byte[] ima_byte;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_dish);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            username = bundle.getString("username");
        }
        mongoDB = new MongoDB();
        imageSelect = findViewById(R.id.ima_cho);
        postButton = findViewById(R.id.post_post);
        foodName = findViewById(R.id.post_dish_name);
        intro = findViewById(R.id.post_intro);
        imageView = findViewById(R.id.post_image);
        imageSelect.setOnClickListener(this);
        postButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ima_cho:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0x01);
                break;
            case R.id.post_post:
                try {
                    PostDish();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }

    private void PostDish() throws IOException, JSONException {
        String s_name = foodName.getText().toString();
        String s_intro = intro.getText().toString();
        if (s_name.equals("")) {
            ToastUtil.show(this, "请输入食物名");
        } else if (s_intro.equals("")) {
            ToastUtil.show(this, "请输入食物介绍");
        } else if (uri == null) {
            ToastUtil.show(this, "请选择食物图片");
        } else {
            List<Comment> commentList = new ArrayList<>();
            Dish dish = new Dish(s_name, s_intro, ima_string, "-1", commentList, username);
            Gson gson = new Gson();
            String json = gson.toJson(dish);
            Log.i("tag", json);
            ToastUtil.show(this,"发布中，请稍后");
            Response response1 = mongoDB.InsertOne("meals",json,true);


            if (response1.isSuccessful()){
                ToastUtil.show(this,"发布成功！");
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x01) {
            // 选择到图片的uri
            uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(column_index);
                cursor.close();
            }

            Log.i("tag", uri.toString());
            imageView.setImageURI(uri);
            try {
                ima_byte = GetPictureData(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ima_string = Base64.getEncoder().encodeToString(ima_byte);
                Log.i("tag", ima_string);
//            Bitmap bm = ImageUtil.Base64toBitmap(ima_string);
//            imageView.setImageBitmap(bm);


        }
    }



    public byte[] GetPictureData(String path) throws IOException {
        // 创建一个 FileInputStream 对象，指定要读取的图片文件
        FileInputStream fis = new FileInputStream(path);
// 创建一个字节数组，大小为图片文件的长度
        byte[] data = new byte[fis.available()];
// 从 FileInputStream 对象中读取图片文件的内容到字节数组中
        fis.read(data);
// 关闭 FileInputStream 对象
        fis.close();
        return data;
    }


}