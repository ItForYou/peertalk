package co.kr.itforone.peertalk.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    public static RetrofitAPI getApiService(){

        return getRetrofit().create(RetrofitAPI.class);

    }

    public static Retrofit getRetrofit(){
        Gson gson = new GsonBuilder().setLenient().create();
        return new Retrofit.Builder()
                .baseUrl("https://itforone.co.kr/~peertalk/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }


}
