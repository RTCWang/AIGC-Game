package com.proj.avatar.zego.runonserver;

import android.net.Uri;


public class KeyCenter {

    // 控制台地址: https://console.zego.im/dashboard
    // 可以在控制台中获取APPID，并将APPID设置为long型，例如：APPID = 123456789L.
    public static long APP_ID = ;  //这里填写APPID
    public static String APP_SIGN = ;
    // 在控制台找到ServerSecret，并填入如下
    public static String SERVER_SECRET =  ; //这里填写服务器端密钥
    // 鉴权服务器的地址
    public final static String BACKEND_API_URL = "https://aieffects-api.zego.im?Action=DescribeAvatarLicense";


    public static String avatarLicense = null;
    public static String getURL(String authInfo) {
        Uri.Builder builder = Uri.parse(BACKEND_API_URL).buildUpon();
        builder.appendQueryParameter("AppId", String.valueOf(APP_ID));
        builder.appendQueryParameter("AuthInfo", authInfo);

        return builder.build().toString();
    }


}
