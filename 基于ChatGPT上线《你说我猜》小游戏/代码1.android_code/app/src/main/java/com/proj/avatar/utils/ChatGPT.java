package com.proj.avatar.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.proj.avatar.zego.RTCMngr;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGPT {

    //这里填写部署了对接chatgpt的服务器iP
    public static final String IP = "192.168.31.192:8888";


    private static final String TAG = "ChatGPT";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    OkHttpClient client = new OkHttpClient();
    private String url;
    private static ChatGPT mInstance;

    private ChatGPT(String ip) {
        this.url = "http://" + ip + "/ask";
    }

    public static ChatGPT getInstance() {
        if (null == mInstance) {
            synchronized (RTCMngr.class) {
                if (null == mInstance) {
                    mInstance = new ChatGPT(IP);
                }
            }
        }
        return mInstance;
    }

    public void setURL(String url) {
        this.url = "http://" + url;
    }

    Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private void req(String url, CB cb) {
//        Log.e(TAG, "url____>>" + url);
        post(url, "", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Something went wrong
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    try {
                        JSONObject obj = new JSONObject(responseStr);
                        int state = obj.getInt("state");
                        String txt = obj.getString("txt");
                        cb._complete(state == 0, txt);
                    } catch (Exception e) {
                        cb._complete(false, "服务器返回数据格式错误！");

                    }
                } else {
                    // Request not successful
                    cb._complete(false, "网络请求失败！");
                }
            }
        });
    }


    public void ask(String q, CB cb) {
        String url = this.url + "?ask=" + q;
        req(url, cb);
    }
}
