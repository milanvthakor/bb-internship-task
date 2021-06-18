package com.milan.dukan.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient instance = null;
    private final AuthApi authApi;

    public RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(AuthApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        authApi = retrofit.create(AuthApi.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public AuthApi getAuthApi() {
        return authApi;
    }
}
