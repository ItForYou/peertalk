package co.kr.itforone.peertalk.retrofit;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface RetrofitAPI {

    @FormUrlEncoded
    @POST("bbs/load_info.php")
    Call<responseModel>  getList(
            @Field("mb_id") String mb_id,
            @Field("number") String number
    );


}
