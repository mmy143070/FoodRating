package com.example.FoodRating.database;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MongoDB {
    private Response ApiConnect(String json, String action, boolean sync) {
        final Response[] response1 = new Response[1];
        @SuppressLint("SdCardPath") Cache cache = new Cache(new File("/data/data/com.example.login"), 100 * 1024 * 1024);
        OkHttpClient client = new OkHttpClient().newBuilder().cache(cache)
                .build();
        RequestBody body = RequestBody.create(json.getBytes(StandardCharsets.UTF_8));
        Request request = new Request.Builder()
                .get()
                .url("https://us-east-2.aws.data.mongodb-api.com/app/data-bwxcu/endpoint/data/v1/action/" + action)
                .addHeader("Content-Type", "application/ejson")
                .addHeader("apiKey", "rdX9XZwcsHKZm0qpBk3j6yOoASMBNWbNmNwvjmvmhnpvSqsAHcKpCwJDeebMNr6Y")
//                            .addHeader("Accept", "application/json")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                response1[0] = response;
            }
        });
        if (sync) {
            while (response1[0] == null) {
                // 如果为空，让线程睡眠一秒
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return response1[0];
    }


    public Response InsertOne(String collection, String document, boolean sync) {
        String json = "{\n" +
                "    \"dataSource\": \"Cluster0\",\n" +
                "    \"database\": \"androidDB\",\n" +
                "    \"collection\": \"" + collection + "\",\n" +
                "    \"document\": " + document + "}";
        return ApiConnect(json, "insertOne", sync);
    }


    public Response FindOne(String collection, String filter, boolean sync) {
        String json = "{\n" +
                "    \"dataSource\": \"Cluster0\",\n" +
                "    \"database\": \"androidDB\",\n" +
                "    \"collection\": \"" + collection + "\",\n" +
                "    \"filter\": " + filter + "}";
        return ApiConnect(json, "findOne", sync);
    }


    public Response FindAll(String collection, String filter, boolean sync) {
        String json = "{\n" +
                "    \"dataSource\": \"Cluster0\",\n" +
                "    \"database\": \"androidDB\",\n" +
                "    \"collection\": \"" + collection + "\",\n" +
                "    \"filter\": " + filter + "}";
        return ApiConnect(json, "find", sync);
    }

    public Response UpdateOne(String collection, String filter, String update, boolean sync, boolean upsert){
        String json = "{\n" +
                "    \"dataSource\": \"Cluster0\",\n" +
                "    \"database\": \"androidDB\",\n" +
                "    \"collection\": \"" + collection + "\",\n" +
                "    \"filter\":  " + filter +" ,\n" +
                "    \"update\": " +update+
                "      ,\n" +
                "    \"upsert\": \""+upsert+"\"\n" +
                "  }";
        return ApiConnect(json, "updateOne",sync);
    }

    public Response InsertOneComment(String collection, String filter, String update, boolean sync, boolean upsert){
        String json = "{\n" +
                "    \"dataSource\": \"Cluster0\",\n" +
                "    \"database\": \"androidDB\",\n" +
                "    \"collection\": \"" + collection + "\",\n" +
                "    \"filter\":  " + filter +" ,\n" +
                "    \"update\": {\n" +
                "      \"$addToSet\":{\"comments\": " +update+"}"+
                "      },\n" +
                "    \"upsert\": \""+upsert+"\"\n" +
                "  }";
        return ApiConnect(json, "updateOne",sync);
    }
    public Response DeleteOne(String collection, String filter, boolean sync) {
        String json = "{\n" +
                "    \"dataSource\": \"Cluster0\",\n" +
                "    \"database\": \"androidDB\",\n" +
                "    \"collection\": \"" + collection + "\",\n" +
                "    \"filter\": " + filter + "}";
        return ApiConnect(json, "deleteOne", sync);
    }

}
