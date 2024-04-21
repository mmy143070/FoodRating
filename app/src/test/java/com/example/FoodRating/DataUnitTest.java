package com.example.FoodRating;

import com.example.FoodRating.database.MongoDB;
import com.example.FoodRating.entity.Comment;
import com.example.FoodRating.entity.Dish;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Response;

public class DataUnitTest {
    String json;
    private ArrayList<Dish> dishArrayList;
    List<Comment> comments;
    MongoDB mongoDB;
    @Test
    public List<Map<String,Object>> getData() {
        mongoDB = new MongoDB();
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
        for (Dish dish : dishArrayList) {
            System.out.println(dish.getComments());
            comments= dish.getComments();
            for(Comment comment : comments){
                if (Objects.equals(comment.getUsername(), "qwe")){
                    System.out.println(comment.getText());
                    names.add(dish.getName());
                    scores.add(comment.getScore());
                    texts.add(comment.getText());
                }
            }
        }
        List<Map<String,Object>> list= new ArrayList<>();
        for(int j=0;j<comments.size();j++){
            Map map = new HashMap();
            map.put("name", names.get(j));
            map.put("score",scores.get(j));
            map.put("comment", texts.get(j));
            list.add(map);
        }
        return list;
    }
}


