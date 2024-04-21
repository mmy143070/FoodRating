package com.example.FoodRating;

import com.example.FoodRating.entity.Dish;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JsonUnitTest {
    @Test
    public void Json64Test() {
        String json = "{\n" +
                "    \"documents\": [\n" +
                "        {\n" +
                "            \"_id\": \"65a10d5d8163b3d481fd8e3e\",\n" +
                "            \"name\": \"麻婆豆腐\",\n" +
                "            \"image\": \"123\",\n" +
                "            \"avr_score\": \"5.0\",\n" +
                "            \"comments\": [\n" +
                "                {\n" +
                "                    \"username\": \"123\",\n" +
                "                    \"score\": \"5.0\",\n" +
                "                    \"text\": \"good\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"username\": \"456\",\n" +
                "                    \"score\": \"5.0\",\n" +
                "                    \"text\": \"perfect\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"_id\": \"65a111628163b3d481fd8e3f\",\n" +
                "            \"name\": \"土豆丝\",\n" +
                "            \"image\": \"1234\",\n" +
                "            \"avr_score\": \"4.5\",\n" +
                "            \"comments\": [\n" +
                "                {\n" +
                "                    \"username\": \"123\",\n" +
                "                    \"score\": \"5.0\",\n" +
                "                    \"text\": \"good\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"username\": \"456\",\n" +
                "                    \"score\": \"4.0\",\n" +
                "                    \"text\": \"not bad\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        Gson gson = new Gson();
        TypeToken<Map<String, List<Dish>>> mapType = new TypeToken<Map<String, List<Dish>>>(){};
        Map<String, List<Dish>> stringMap = gson.fromJson(json, mapType);
        Collection<List<Dish>> dishes = stringMap.values();
        ArrayList<Dish> dishArrayList = (ArrayList<Dish>) dishes.toArray()[0];
        for (Dish dish: dishArrayList){
            System.out.println(dish.getName() + " " + dish.get_id());
        }

        Dish dish1 = (Dish) dishArrayList.toArray()[0];
        System.out.println (dish1.toString());
    }
}
