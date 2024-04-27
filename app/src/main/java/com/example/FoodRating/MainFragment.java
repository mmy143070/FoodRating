package com.example.FoodRating;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.FoodRating.database.MongoDB;
import com.example.FoodRating.entity.BigBinder;
import com.example.FoodRating.entity.BigBinderComment;
import com.example.FoodRating.entity.Comment;
import com.example.FoodRating.entity.Dish;
import com.example.FoodRating.util.ImageUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final int REQUEST_IMAGE_CAPTURE = 123;


    // TODO: Rename and change types of parameters
    private String username;
    private ArrayAdapter<Map<String, Object>> arrayAdapter;
    ListView listView;
    MongoDB mongoDB;
    private ArrayList<Dish> dishArrayList;
    private String desc, s_image;
    List<Map<String, Object>> list;
    String staffName;
    Response response;
    boolean isStudent,fromCamera=false;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private AVLoadingIndicatorView loadingView;
    private SearchView searchView;
    private ImageButton imageButton;
    InputImage image;
    String result2;
    private Uri uriFilePath;

    public MainFragment() {
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
    public static MainFragment newInstance(String param1, String p_staffName, Boolean isStudent) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, p_staffName);
        args.putBoolean(ARG_PARAM3, isStudent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 关闭 URI 暴露检测
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        if (getArguments() != null) {
            username = getArguments().getString(ARG_PARAM1);
            staffName = getArguments().getString(ARG_PARAM2);
            isStudent = getArguments().getBoolean(ARG_PARAM3);
        }
        mongoDB = new MongoDB();
//        list = getData();
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
                if (Objects.equals(item.get("score"), "-1")) {
                    scoreView.setText("None");
                } else {
                    scoreView.setText((String) item.get("score")); //为TextView设置评分
                }
                staffView.setText((String) item.get("staff")); //为TextView设置员工
                nameView.setText((String) item.get("name"));
                return view; //返回子项布局
            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();
        if (fromCamera){
            fromCamera=false;
            return;
        }
        // 在后台线程中获取数据并更新UI
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // 在主线程中显示动画
                if (isAdded()) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingView.show(); // 启动动画

                        }
                    });
                    list = getData();
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
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        loadingView = view.findViewById(R.id.loading_view);
//        loadingView.show(); // 启动动画
        listView = view.findViewById(R.id.listview);
        imageButton = view.findViewById(R.id.im_btn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File mainDirectory = new File(Environment.getExternalStorageDirectory(), "MyFolder/tmp");
                if (!mainDirectory.exists()) mainDirectory.mkdirs();
                uriFilePath = Uri.fromFile(new File(mainDirectory, "IMG_" ));
                takePictureLauncher.launch(uriFilePath);

            }
        });
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filterData(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterData(s);
                return false;
            }
        });
//        loadingView.hide(); // 停止动画
        String[] items = {"Ranked from high to low", "Ranked from low to high"};

        Spinner spinner = view.findViewById(R.id.rank_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String score = adapterView.getItemAtPosition(i).toString();
                switch (i) {
                    case 0:
                        sortByScore();
                        break;
                    case 1:
                        sortByScoreReverse();
                        break;
                }
//                Log.i("tag", String.valueOf(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

//    private ActivityResultLauncher<Void> launcherPreview = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), result -> {
//        // 在回调中处理拍照的结果，result为Bitmap对象，表示拍照后的预览图
//        if (result != null) {
//            image = InputImage.fromBitmap(result, 0);
//            TextRecognizer recognizer =
//                    TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
////            TextRecognizer recognizer =
////                    TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
//            Task<Text> resultText =
//                    recognizer.process(image)
//                            .addOnSuccessListener(new OnSuccessListener<Text>() {
//                                @Override
//                                public void onSuccess(Text visionText) {
//                                    // Task completed successfully
//                                    result2 = visionText.getText();
//                                    Log.i("tag",result2);
//                                }
//                            })
//                            .addOnFailureListener(
//                                    new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            // Task failed with an exception
//                                            // ...
//                                        }
//                                    });
//        }
//    });
ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
        new ActivityResultContracts.TakePicture(),
        result -> {
            // Handle the result of taking a picture
            if (result) {
                // The picture was saved successfully
                // Do something with the Uri of the image
                try {
                    image = InputImage.fromFilePath(getActivity(), uriFilePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

//                TextRecognizer recognizer =
//                        TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
            TextRecognizer recognizer =
                    TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                Task<Text> resultText =
                        recognizer.process(image)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text visionText) {
                                        // Task completed successfully
                                        result2 = visionText.getText();
                                        Log.i("tag",result2);
                                        filterData(result2);
                                        fromCamera =true;
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        });
            }
        }
);



    private void sortByScoreReverse() {
        if (list != null) {
            List<Map<String, Object>> doubleList = list;
            for (Map<String, Object> item : doubleList) {
                item.put("score", Double.parseDouble((String) item.get("score")));
            }
            Collections.sort(doubleList, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    // 从两个Map对象中分别获取评分
                    double score1 = (double) o1.get("score");
                    double score2 = (double) o2.get("score");
                    // 按照评分的降序排序，返回不同的值
                    if (score1 > score2) {
                        return 1;
                    } else if (score1 < score2) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            for (Map<String, Object> item : doubleList) {
                item.put("score", String.valueOf(item.get("score")));
            }
            // 更新ArrayAdapter的数据源，使用filteredData替换mData
            arrayAdapter.clear();
            arrayAdapter.addAll(doubleList);
            // 通知ListView刷新显示
            arrayAdapter.notifyDataSetChanged();
        }
    }

    private void sortByScore() {
        if (list != null) {
            List<Map<String, Object>> doubleList = list;
            for (Map<String, Object> item : doubleList) {
                item.put("score", Double.parseDouble((String) item.get("score")));
            }
            Collections.sort(doubleList, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    // 从两个Map对象中分别获取评分
                    double score1 = (double) o1.get("score");
                    double score2 = (double) o2.get("score");
                    // 按照评分的降序排序，返回不同的值
                    if (score1 > score2) {
                        return -1;
                    } else if (score1 < score2) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            for (Map<String, Object> item : doubleList) {
                item.put("score", String.valueOf(item.get("score")));
            }
            // 更新ArrayAdapter的数据源，使用filteredData替换mData
            arrayAdapter.clear();
            arrayAdapter.addAll(doubleList);
            // 通知ListView刷新显示
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public void filterData(String query) {
        // 创建一个新的数据集，用于存放符合搜索条件的数据
        List<Map<String, Object>> filteredData = new ArrayList<>();
        // 如果搜索关键词为空，就显示所有数据
//        Log.i("tag", String.valueOf(query.length()));
        if (query == null || query.length() == 0) {
            filteredData = list;
        } else if (list != null) {
            // 遍历原始数据集，对每个项的name属性进行判断，如果包含搜索关键词，就将该项添加到filteredData中
            for (Map<String, Object> item : list) {
                String name = (String) item.get("name");
                if (name.contains(query)) {
                    filteredData.add(item);
                }
            }
        }
        // 更新ArrayAdapter的数据源，使用filteredData替换mData
        arrayAdapter.clear();
        arrayAdapter.addAll(filteredData);
        // 通知ListView刷新显示
        arrayAdapter.notifyDataSetChanged();
    }


    public List<Map<String, Object>> getData() {
        String json;
        Log.i("tag", "jinrudata");
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

        assert comments != null;
        comments.toArray();
        BigBinder bigData = new BigBinder(s_image);
        BigBinderComment comment = new BigBinderComment(comments);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("foodName", name);
        bundle.putString("desc", desc);
        bundle.putBinder("image", bigData);
        bundle.putBinder("comments", comment);
        bundle.putBoolean("isStudent", isStudent);
//        bundle.putParcelableArrayList("comments", comments);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}