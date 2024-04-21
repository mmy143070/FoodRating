package com.example.FoodRating;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.FoodRating.database.MongoDB;
import com.example.FoodRating.entity.BigBinder;
import com.example.FoodRating.entity.Comment;
import com.example.FoodRating.entity.Dish;
import com.example.FoodRating.util.ImageUtil;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyDishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyDishFragment extends Fragment implements AdapterView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String username;
    SimpleAdapter simpleAdapter;
    ListView listView;
    MongoDB mongoDB;
    private ArrayList<Dish> dishArrayList;
    private String desc, s_image;
    List<Map<String, Object>> list;
    String staffName;
    Response response;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private AVLoadingIndicatorView loadingView;
    private ArrayAdapter<Map<String, Object>> arrayAdapter;


    public MyDishFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyDishFragment newInstance(String param1, String p_staffName) {
        MyDishFragment fragment = new MyDishFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, p_staffName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_PARAM1);
            staffName = getArguments().getString(ARG_PARAM2);
        }
        mongoDB = new MongoDB();
        arrayAdapter = new ArrayAdapter<Map<String, Object>>(getActivity(), R.layout.dish_item_layout, R.id.meal_name) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent); //调用父类的getView方法
                ImageView imageView = (ImageView) view.findViewById(R.id.meal_image); //获取ImageView实例
                TextView scoreView = (TextView) view.findViewById(R.id.meal_score); //获取TextView实例
                TextView staffView = (TextView) view.findViewById(R.id.staff_name); //获取TextView实例
                TextView nameView = (TextView) view.findViewById(R.id.meal_name); //获取TextView实例
                Map<String, Object> item = getItem(position); //获取当前项的数据
                imageView.setImageBitmap((Bitmap) item.get("image")); //为ImageView设置图片
                scoreView.setText((String) item.get("score")); //为TextView设置评分
                staffView.setText((String) item.get("staff")); //为TextView设置员工
                nameView.setText((String) item.get("name")); //为TextView设置员工
                return view; //返回子项布局
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        // 在后台线程中获取数据并更新UI
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    // 在主线程中显示动画
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("tag", "zhaunquan");
                            loadingView.show(); // 启动动画
                        }
                    });
                    List<Map<String, Object>> list = getData();
                    // 在主线程中更新UI
                    if (isAdded()) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 更新SimpleAdapter的数据源
                                arrayAdapter.clear();
                                arrayAdapter.addAll(list);
                                arrayAdapter.notifyDataSetChanged();
                                loadingView.hide(); // 停止动画
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_dish, container, false);
        loadingView = view.findViewById(R.id.loading_view2);
//        loadingView.show(); // 启动动画
        listView = view.findViewById(R.id.listview);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
//        loadingView.hide(); // 停止动画
        return view;
    }


    public List<Map<String, Object>> getData() {
        String json;

        if (Objects.equals(staffName, "")) {
            response = mongoDB.FindAll("meals", "{}", true);
        } else {
            response = mongoDB.FindAll("meals", "{\"staff_name\":\"" + staffName + "\"}", true);
        }

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

//        String [] names={"水果1","水果2","水果3","水果4","水果5","水果6","水果7"};
//        String [] names={};
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> scores = new ArrayList<>();
        ArrayList<Bitmap> images = new ArrayList<>();
        ArrayList<String> staffs = new ArrayList<>();
        for (Dish dish : dishArrayList) {
            System.out.println(dish.getName() + " " + dish.get_id());
            names.add(dish.getName());
            scores.add(dish.getAvr_score());
            images.add(ImageUtil.Base64toBitmap(dish.getImage()));
            staffs.add(dish.getStaffName());
        }
//        int [] images={R.drawable.ic_launcher_foreground,R.drawable.ic_launcher_foreground,R.drawable.ic_launcher_foreground,R.drawable.ic_launcher_foreground};
//        String [] scores={"1","2","3","4.2345","5533","666","76676"};
        List<Map<String, Object>> list = new ArrayList<>();
        for (int j = 0; j < dishArrayList.size(); j++) {
            Map map = new HashMap();
            map.put("name", names.get(j));
            map.put("image", images.get(j));
            map.put("score", scores.get(j));
            map.put("staff", staffs.get(j));
            list.add(map);
        }
        Log.i("tag", list.toString());
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i("tag", String.valueOf(i));
        Map map = (Map) listView.getAdapter().getItem(i);
        String name = (String) map.get("name");
        ArrayList<Comment> comments = null;
        for (Dish dish : dishArrayList) {
            if (Objects.equals(dish.getName(), name)) {
                comments = (ArrayList<Comment>) dish.getComments();
                desc = dish.getDesc();
                s_image = dish.getImage();
            }
        }

//        Log.i("tag","position:"+i+"text"+comments.get(0).getText() );
        assert comments != null;
        comments.toArray();
        BigBinder bigData = new BigBinder(s_image);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("foodName", name);
        bundle.putString("desc", desc);
        bundle.putBinder("image", bigData);
        bundle.putParcelableArrayList("comments", comments);
        Intent intent = new Intent(getContext(), MyDishDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}