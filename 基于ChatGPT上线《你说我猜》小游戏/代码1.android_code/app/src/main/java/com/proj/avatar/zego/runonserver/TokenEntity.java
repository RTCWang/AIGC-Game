package com.proj.avatar.zego.runonserver;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.Random;

public class TokenEntity {

    protected final static GsonBuilder builder = new GsonBuilder();
    protected final static Gson gson = builder.create();
    public long app_id;
    public String user_id;
    public long ctime;
    public long expire;
    public int nonce;
    public String payload;

    public TokenEntity(long appId, String userId, String roomId, long maxValidSeconds, int allowLogin, int allowPublish) {
        this.app_id = appId;
        this.user_id = userId;
        this.payload = new Payload(roomId, allowLogin, allowPublish).toString();

        long nowTime = System.currentTimeMillis() / 1000;
        this.expire = nowTime + maxValidSeconds;
        this.ctime = nowTime;
        this.nonce = new Random().nextInt();

    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public static class Privilege {
        @SerializedName("1")
        public int allowLogin;
        @SerializedName("2")
        public int allowPublish;

        public Privilege(int allowLogin, int allowPublish) {
            this.allowLogin = allowLogin;
            this.allowPublish = allowPublish;
        }

        @Override
        public String toString() {
            return gson.toJson(this);
        }
    }

    public static class Payload {
        public String room_id; // 房间id，限制用户只能登录特定房间
        public Privilege privilege;
        public String[] stream_id_list;

        public Payload(String roomId, int allowLogin, int allowPublish) {
            this.room_id = roomId;
            this.stream_id_list = null;
            this.privilege = new Privilege(allowLogin, allowPublish);
        }

        @Override
        public String toString() {
            return gson.toJson(this);
        }
    }


}
