package co.kr.itforone.peertalk.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    public static RetrofitAPI getApiService(){

        return getRetrofit().create(RetrofitAPI.class);

    }

    private static Retrofit getRetrofit(){
        Gson gson = new GsonBuilder().setLenient().create();
        return new Retrofit.Builder()
                .baseUrl("http://itforone.co.kr/~peertalk/bbs/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }


}
