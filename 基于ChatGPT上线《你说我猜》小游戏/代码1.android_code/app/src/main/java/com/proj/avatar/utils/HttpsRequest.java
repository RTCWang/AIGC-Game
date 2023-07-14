package com.proj.avatar.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpsRequest {
    private final static String TAG = "HttpsRequest";

    public interface OnJSONCallback {
        void onJSON(JSONObject json);

        void onError(Exception e);
    }

    public interface OnStrCallback {
        void onStr(JSONObject json);

        void onError(Exception e);
    }

    public static void getJSON(String url, OnJSONCallback cb) {
        Log.e(TAG, url);

        OkHttpClient client = HttpsRequest.getInstance();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cb.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject obj = new JSONObject(res);
                    cb.onJSON(obj);


                } catch (Exception e) {
                            cb.onError(e);

                }
            }
        });
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }


    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static OkHttpClient singleton;

    public static OkHttpClient getInstance() {
        if (singleton == null) {
            synchronized (HttpsRequest.class) {
                if (singleton == null) {
                    singleton = new OkHttpClient();
                    OkHttpClient.Builder mBuilder = new OkHttpClient.Builder()
                            .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
                            .hostnameVerifier(new TrustAllHostnameVerifier());
                    // mBuilder.sslSocketFactory(createSSLSocketFactory());
                    // mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());
                    singleton = mBuilder.build();
                }
            }
        }
        return singleton;
    }
}