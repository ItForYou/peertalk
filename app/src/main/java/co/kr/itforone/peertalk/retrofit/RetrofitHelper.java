package co.kr.itforone.peertalk.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    public static RetrofitAPI getApiService(){

        return getRetrofit().create(RetrofitAPI.class);

    }

    public static Retrofit getRetrofit(){

        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build();

        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://itforone.co.kr/~peertalk/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }

}
