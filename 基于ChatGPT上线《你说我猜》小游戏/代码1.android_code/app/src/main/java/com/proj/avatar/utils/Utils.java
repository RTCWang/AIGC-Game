package com.proj.avatar.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
    public final static String TAG = "Utils";

    public static void toast(Activity activity, String msg) {

        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }




    public static Object getJsonWithDef(JSONObject json, String key, Object defVal) {
        String[] keyArr = key.split("/");
        Object out = defVal;
        try {
            for (int i = 0; i < keyArr.length; ++i) {
                String k = keyArr[i];
                if (i == keyArr.length - 1) out = json.get(k);
                else {
                    if (k.endsWith("]")) {
                        int left = k.indexOf('[');
                        String sk = k.substring(0, left);
                        String cond = k.substring(left + 1, k.length() - 1);
                        String[] condArr = cond.split("=");
                        JSONArray arr = json.getJSONArray(sk);
                        boolean isFound = false;
                        int len = arr.length();
                        for (int j = 0; j < len; ++j) {
                            JSONObject item = arr.getJSONObject(j);
                            if (item.get(condArr[0]).toString().equals(condArr[1])) {
                                json = item;
                                isFound = true;
                                break;
                            }
                        }
                        if (!isFound) {
                            Log.e(TAG, cond + " is not found in " + arr.toString());
                            return defVal;
                        }
                    } else {
                        json = (JSONObject) json.get(k);
                    }
                }
            }
        } catch (JSONException e) {
            return defVal;
        }
        return out;
    }

    /**
     * 在Json获取指定key的val，字符串类型，
     */
    public static String getStr(JSONObject json, String key, String defVal) {
        Object obj = getJsonWithDef(json, key, defVal);
        return obj.toString();
    }

    /**
     * 在Json获取指定key的val，整数类型，
     */
    public static int getInt(JSONObject json, String key, int defVal) {
        Object obj = getJsonWithDef(json, key, defVal);
        return (Integer) obj;
    }

}
