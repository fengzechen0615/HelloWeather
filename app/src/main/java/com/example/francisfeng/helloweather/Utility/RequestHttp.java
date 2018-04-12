package com.example.francisfeng.helloweather.Utility;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RequestHttp {

    // 利用OkHttp，完成服务器获取数据，调用sendOkHttpRequest()方法，传入请求地址，并注册一个回调处理服务器响应
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

}
