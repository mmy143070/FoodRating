package com.example.FoodRating;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.FoodRating.database.MongoDB;
import com.example.FoodRating.entity.BigBinder;
import com.example.FoodRating.entity.BigBinderComment;
import com.example.FoodRating.entity.Comment;
import com.example.FoodRating.util.ImageUtil;
import com.example.FoodRating.util.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private ArrayList<Comment> comments;
    private TextView dishName, describe;
    private String s_dishName, s_desc, s_username, s_image, path = null, ima_string;
    private Button post, select;
    private Spinner spinner;
    private EditText text;
    private String score = "";
    private MongoDB mongoDB;
    private boolean isStudent;
    private ImageView commentImage;
    Uri uri = null;
    byte[] ima_byte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mongoDB = new MongoDB();
        dishName = findViewById(R.id.dish_name);
        describe = findViewById(R.id.detail_information);
        spinner = findViewById(R.id.spinner);
        text = findViewById(R.id.comment);
        post = findViewById(R.id.post);
        select = findViewById(R.id.select_image);
        commentImage = findViewById(R.id.post_comment_image);
        ImageView dishImage = findViewById(R.id.detail_image);
        post.setOnClickListener(this);
        select.setOnClickListener(this);

//        post.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    postComment();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            s_username = bundle.getString("username");
            BigBinderComment bigBinderComment = (BigBinderComment) bundle.getBinder("comments");
            comments = bigBinderComment.getData();
            s_dishName = bundle.getString("foodName");
            s_desc = bundle.getString("desc");
            BigBinder bigBinder = (BigBinder) bundle.getBinder("image");
            s_image = bigBinder.getData();
            isStudent = bundle.getBoolean("isStudent");
        }
        if (!isStudent) {
            findViewById(R.id.liner_post).setVisibility(View.GONE);
            findViewById(R.id.linear_post_image).setVisibility(View.GONE);
        }
        dishName.setText(s_dishName);
        describe.setText(s_desc);
        Bitmap image = ImageUtil.Base64toBitmap(s_image);
        dishImage.setImageBitmap(image);
        listView = findViewById(R.id.detail_lv);
        simpleAdapter = new SimpleAdapter(this, getData(), R.layout.comment_item_layout, new String[]{"name", "score", "comment", "image"}, new int[]{R.id.detail_name, R.id.detail_score, R.id.detail_comment, R.id.comment_image});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view.getId() == R.id.comment_image) {
                    ImageView imageView = (ImageView) view;
                    if (data!="") {
                        imageView.setImageBitmap((Bitmap) data);
                    }else{
                        imageView.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
                return false;
            }
        });
        listView.setAdapter(simpleAdapter);

        String[] items = {"5", "4", "3", "2", "1"};

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                score = adapterView.getItemAtPosition(i).toString();
                Log.i("tag", score);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void postComment() throws IOException {
//        s_username = "test";
        String commentText = text.getText().toString();
        if (commentText.equals("")) {
            ToastUtil.show(this, "Comment cannot be blank");
        } else if (score.equals("")) {
            ToastUtil.show(this, "Please select score");
        }  else {
            String json = "{\n" +
                    "      \"username\": \"" + s_username + "\",\n" +
                    "      \"score\": \"" + score + "\",\n" +
                    "      \"text\": \"" + commentText + "\",\n" +
                    "      \"image\": \"" + ima_string + "\"\n" +
                    "    }";
            String filter = "{\n" +
                    "        \"name\": \"" + s_dishName + "\"\n" +
                    "        }";
            ToastUtil.show(this, "Uploading");
            Response response = mongoDB.InsertOneComment("meals", filter, json, true, true);

            Response response2 = mongoDB.FindOne("meals", "{\n" +
                    "      \"name\": \"" + s_dishName + "\"\n" +
                    "    },\n" +
                    "    \"projection\": {\"comments\": 1}", true);
            // 导入gson库
            Gson gson = new Gson();

// 定义一个JSON字符串
            String jsonString = response2.body().string();

// 将JSON字符串解析为一个JsonObject对象
            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

// 从JsonObject对象中获取名为"comments"的JsonArray对象
            JsonArray jsonArray = jsonObject.getAsJsonObject("document").getAsJsonArray("comments");

// 创建一个List<Integer>对象，用于存储score的值
            List<Integer> scoreList = new ArrayList<>();

// 遍历JsonArray对象，从每个元素中提取名为"score"的值，并将其添加到List<Integer>对象中
            for (JsonElement element : jsonArray) {
                // 获取每个元素的JsonObject对象
                JsonObject commentObject = element.getAsJsonObject();
                // 获取每个元素的score值，并转换为int类型
                int score = commentObject.get("score").getAsInt();
                // 将score值添加到List<Integer>对象中
                scoreList.add(score);
            }
            int sum = 0;
            for (Integer num : scoreList) {
                sum += num;
            }
            double average = (double) sum / scoreList.size();
            DecimalFormat df = new DecimalFormat("#.#");
            String s = df.format(average);
            Response response3 = mongoDB.UpdateOne("meals", "{ \"name\": \"" + s_dishName + "\"}", "{ \"$set\": { \"avr_score\": \"" + s + "\" } }", true, false);
            if (response.isSuccessful() && response3.isSuccessful()) {
                ToastUtil.show(this, "Upload successfully");
            }
// 打印List<Integer>对象
            System.out.println(average);
        }
    }

    private List<Map<String, Object>> getData() {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> scores = new ArrayList<>();
        ArrayList<String> texts = new ArrayList<>();
        ArrayList<Bitmap> images = new ArrayList<>();
        List<Map<String, Object>> list = new ArrayList<>();
        if (comments.size() != 0) {

            for (Comment comment : comments) {
                names.add(comment.getUsername());
                scores.add(comment.getScore());
                texts.add(comment.getText());
                images.add(ImageUtil.Base64toBitmap(comment.getImage()));

            }
//        String [] names={"水果1","水果2","水果3"};
//        String [] scores={"4","3","5"};
//        String [] comments={"sdadasdasdasd","qwetrwqefwqefqw","afafdwsfgwegf"};


            for (int j = 0; j < comments.size(); j++) {
                Map map = new HashMap();
                map.put("name", names.get(j));
                map.put("score", scores.get(j));
                map.put("comment", texts.get(j));
                map.put("image", images.get(j));
                list.add(map);
            }
            Log.i("tag", list.toString());
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post:
                try {
                    postComment();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.select_image:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0x01);
                break;
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
            commentImage.setImageURI(uri);
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