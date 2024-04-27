package com.example.FoodRating;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.FoodRating.database.MongoDB;
import com.example.FoodRating.entity.Comment;
import com.example.FoodRating.entity.Dish;
import com.example.FoodRating.util.ImageUtil;
import com.example.FoodRating.util.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Response;

public class MyCommentsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private MongoDB mongoDB;
    private String json, score,username;
    private ArrayList<Dish> dishArrayList;
    private List<Comment> comments;
    private ListView listView;
    private SimpleAdapter simpleAdapter; // 把SimpleAdapter声明为一个成员变量
    private AVLoadingIndicatorView loadingView; // 声明一个LoadingView对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comments);
        loadingView = findViewById(R.id.loading_view);
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            username = bundle.getString("username");
        }
        mongoDB = new MongoDB();
        listView = findViewById(R.id.mc_lv);
        ExecutorService executorService = Executors.newSingleThreadExecutor(); // 创建一个ExecutorService对象
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // 在主线程中显示动画
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingView.show(); // 启动动画
                    }
                });
                List<Map<String, Object>> list = getData(); // 获取数据
                // 在主线程中更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 更新SimpleAdapter的数据源
                        simpleAdapter = new SimpleAdapter(MyCommentsActivity.this, list, R.layout.mycomment_item_layout, new String[]{"name", "score", "comment", "image"}, new int[]{R.id.my_cm_name, R.id.my_cm_score, R.id.my_cm_text, R.id.my_comment_image});
                        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                            @Override
                            public boolean setViewValue(View view, Object data, String textRepresentation) {
                                if (view.getId() == R.id.my_comment_image) {
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
                        listView.setOnItemClickListener(MyCommentsActivity.this);
                        loadingView.hide(); // 停止动画
                    }
                });
            }
        });


    }



    public List<Map<String, Object>> getData() {
        Response response = mongoDB.FindAll("meals", "{}", true);
        try {
            json = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new Gson();
        TypeToken<Map<String, List<Dish>>> mapType = new TypeToken<Map<String, List<Dish>>>() {
        };
        Map<String, List<Dish>> stringMap = gson.fromJson(json, mapType);
        Collection<List<Dish>> dishes = stringMap.values();
        dishArrayList = (ArrayList<Dish>) dishes.toArray()[0];
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> scores = new ArrayList<>();
        ArrayList<String> texts = new ArrayList<>();
        ArrayList<Bitmap> images = new ArrayList<>();
        for (Dish dish : dishArrayList) {
            System.out.println(dish.getComments());
            comments = dish.getComments();
            for (Comment comment : comments) {
                if (Objects.equals(comment.getUsername(), username)) {
                    System.out.println(comment.getText());
                    names.add(dish.getName());
                    scores.add(comment.getScore());
                    texts.add(comment.getText());
                    images.add(ImageUtil.Base64toBitmap(comment.getImage()));
                }
            }
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (int j = 0; j < texts.size(); j++) {
            Map map = new HashMap();
            map.put("name", names.get(j));
            map.put("score", scores.get(j));
            map.put("comment", texts.get(j));
            map.put("image", images.get(j));
            list.add(map);
        }
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Map map = (Map) listView.getAdapter().getItem(i);
        String dishName = (String) map.get("name");
        UpdateClick(view, dishName, username,this);
    }

    private void UpdateClick(View v, String dishName, String username, Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        builder.setIcon(R.drawable.food);
        builder.setTitle("Change comment");
        final View view = getLayoutInflater().inflate(R.layout.dialog, null);
        builder.setView(view);
        String[] items = {"5", "4", "3", "2", "1"};

        Spinner spinner = view.findViewById(R.id.u_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                score = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        builder.setPositiveButton("modify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText u_text = (EditText) view.findViewById(R.id.u_text);
                String text = u_text.getText().toString();
                String filter = "{ \"name\": \""+dishName+"\", \"comments.username\": \""+username+"\" }";
                String update = "{ \"$set\": { \"comments.$.text\": \""+text+"\",\"comments.$.score\": \""+score+"\" } }";
                ToastUtil.show(ctx,"Under modification");
                Response response = mongoDB.UpdateOne("meals", filter, update, true, true);
                if (response.isSuccessful()){
                    ToastUtil.show(ctx,"Modified successfully");
                }
                dialog.cancel();
            }
        });
        builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filter = "{\"name\": \"" + dishName + "\"}";
                String update = "{ \"$pull\": { \"comments\": { \"username\": \"" + username + "\" } } }";
                ToastUtil.show(ctx,"deleting");
                Response response = mongoDB.UpdateOne("meals", filter, update, true, true);
                if (response.isSuccessful()){
                    ToastUtil.show(ctx,"Delete successful");
                }
                dialog.cancel();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}