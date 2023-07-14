package com.proj.avatar.zego.runonserver;


import com.proj.avatar.utils.CB;
import com.proj.avatar.utils.HttpsRequest;
import com.proj.avatar.utils.Utils;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.SecureRandom;

/***
 * 实际线上应用应将此代码应在服务器端运行，
 * 出于Demo原因，这里全部在端上运行
 * */
public class RunOnServer {
    private static String mUserId;
    private static String mRoomId;

    /**
     * 此函数应该放在服务器端执行，以防止泄露ServerSecret
     */
    public static String getToken(String userId, String roomId) {

        TokenEntity tokenEntity = new TokenEntity(KeyCenter.APP_ID, userId, roomId, 60 * 60, 1, 1);

        String token = TokenUtils.generateToken04(tokenEntity);
        mUserId = userId;
        mRoomId = roomId;
        return token;
    }

    public static String reToken() {

        TokenEntity tokenEntity = new TokenEntity(KeyCenter.APP_ID, mUserId, mRoomId, 60 * 60, 1, 1);

        String token = TokenUtils.generateToken04(tokenEntity);
        return token;

    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        //把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];
            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString();
    }

    private static String getPublicArgs() {

        //生成16进制随机字符串(16位)
        byte[] bytes = new byte[8];
        //使用SecureRandom获取高强度安全随机数生成器
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(bytes);
        String signatureNonce = bytesToHex(bytes);
        long timestamp = System.currentTimeMillis() / 1000L;
        String signature = GenerateSignature(KeyCenter.APP_ID, signatureNonce, KeyCenter.SERVER_SECRET, timestamp);
        return "AppId=" + KeyCenter.APP_ID +
                "&SignatureNonce=" + signatureNonce +
                "&Timestamp=" + timestamp +
                "&Signature=" + signature + "&SignatureVersion=2.0&IsTest=false";
    }


    public static void checkRoomId(String roomId, CB cb) {
        String url = "https://rtc-api.zego.im/?Action=DescribeUserNum&RoomId[]=" + roomId +
                "&" + getPublicArgs();
        HttpsRequest.getJSON(url, new HttpsRequest.OnJSONCallback() {
            @Override
            public void onJSON(JSONObject json) {
                int code = Utils.getInt(json, "Code", -1);
                String msg = Utils.getStr(json, "Message", "网络请求异常");

                if (code == 0) {
                    int userCount = Utils.getInt(json, "Data/UserCountList[RoomId=" + roomId + "]/UserCount", -1);

                    if (userCount >= 0) {
                        cb._complete(true, userCount + "");
                    } else {
                        cb._complete(false, "网络请求异常");
                    }
                } else {
                    cb._complete(false, msg);
                }
            }

            @Override
            public void onError(Exception e) {
                cb._complete(false, "网络异常");

            }
        });


    }

    // Signature=md5(AppId + SignatureNonce + ServerSecret + Timestamp)
    private static String GenerateSignature(long appId, String signatureNonce, String serverSecret, long timestamp) {
        String str = String.valueOf(appId) + signatureNonce + serverSecret + String.valueOf(timestamp);
        String signature = "";
        try {
            //创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            //计算后获得字节数组
            byte[] bytes = md.digest(str.getBytes("utf-8"));
            //把数组每一字节换成16进制连成md5字符串
            signature = bytesToHex(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

}
