package com.example.FoodRating;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.FoodRating.database.MongoDB;
import com.example.FoodRating.entity.BigBinder;
import com.example.FoodRating.entity.Comment;
import com.example.FoodRating.util.ImageUtil;
import com.example.FoodRating.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class DetailActivity2 extends AppCompatActivity {
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private ArrayList<Comment> comments;
    private TextView dishName, describe;
    private String s_dishName, s_desc, s_username, s_image;
    private Button post, delete;
    private Spinner spinner;
    private EditText text;
    private String score = "";
    private MongoDB mongoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);
        mongoDB = new MongoDB();
        dishName = findViewById(R.id.dish_name);
        describe = findViewById(R.id.detail_information);
//        spinner = findViewById(R.id.spinner);
//        text = findViewById(R.id.comment);
//        post = findViewById(R.id.post);
        delete = findViewById(R.id.delete);
        ImageView imageView = findViewById(R.id.detail_image);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteDish();

            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            s_username = bundle.getString("username");
            comments = bundle.getParcelableArrayList("comments");
            s_dishName = bundle.getString("foodName");
            s_desc = bundle.getString("desc");
            BigBinder bigBinder = (BigBinder) bundle.getBinder("image");
            s_image = bigBinder.getData();
        }
        dishName.setText(s_dishName);
        describe.setText(s_desc);
        Bitmap image = ImageUtil.Base64toBitmap(s_image);
        imageView.setImageBitmap(image);
        listView = findViewById(R.id.detail_lv);
        simpleAdapter = new SimpleAdapter(this, getData(), R.layout.comment_item_layout, new String[]{"name", "score", "comment"}, new int[]{R.id.detail_name, R.id.detail_score, R.id.detail_comment});

        listView.setAdapter(simpleAdapter);

        String[] items = {"5", "4", "3", "2", "1"};

//        Spinner spinner = findViewById(R.id.spinner);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                score = adapterView.getItemAtPosition(i).toString();
//                Log.i("tag", score);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
    }

    private void DeleteDish() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher_foreground);
        builder.setTitle("确定删除吗");
        final View view = getLayoutInflater().inflate(R.layout.dialog, null);
        view.findViewById(R.id.u_text).setVisibility(View.GONE);
        view.findViewById(R.id.u_spinner).setVisibility(View.GONE);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToastUtil.show(DetailActivity2.this,"删除中");
                Response response = mongoDB.DeleteOne("meals", "{\"name\":\"" + s_dishName + "\"}", true);
                if(response.isSuccessful()){
                    ToastUtil.show(DetailActivity2.this,"删除成功");
                }
                dialog.cancel();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();


    }

//    private void postComment() {
////        s_username = "test";
//        String commentText = text.getText().toString();
//        if (commentText.equals("")){
//            ToastUtil.show(this, "评论不能为空");
//        }else if(score.equals("")){
//            ToastUtil.show(this, "请选择评分");
//        } else {
//            String json = "{\n" +
//                    "      \"username\": \""+s_username+"\",\n" +
//                    "      \"score\": \""+score+"\",\n" +
//                    "      \"text\": \""+commentText+"\"\n" +
//                    "    }";
//            String filter = "{\n" +
//                    "        \"name\": \""+s_dishName+"\"\n" +
//                    "        }";
//            ToastUtil.show(this, "上传中");
//            Response response = mongoDB.InsertOneComment("meals",filter,json,true,true);
//            if(response.isSuccessful()){
//                ToastUtil.show(this, "发表成功");
//            }
//        }
//    }

    private List<Map<String, Object>> getData() {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> scores = new ArrayList<>();
        ArrayList<String> texts = new ArrayList<>();
        List<Map<String, Object>> list = new ArrayList<>();
        if (comments.size() != 0) {

            for (Comment comment : comments) {
                names.add(comment.getUsername());
                scores.add(comment.getScore());
                texts.add(comment.getText());
            }
//        String [] names={"水果1","水果2","水果3"};
//        String [] scores={"4","3","5"};
//        String [] comments={"sdadasdasdasd","qwetrwqefwqefqw","afafdwsfgwegf"};


            for (int j = 0; j < comments.size(); j++) {
                Map map = new HashMap();
                map.put("name", names.get(j));
                map.put("score", scores.get(j));
                map.put("comment", texts.get(j));
                list.add(map);
            }
            Log.i("tag", list.toString());
        }
        return list;
    }
}