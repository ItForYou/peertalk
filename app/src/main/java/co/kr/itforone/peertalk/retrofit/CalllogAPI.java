package co.kr.itforone.peertalk.retrofit;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface CalllogAPI {

    @FormUrlEncoded
   // test_post/
    @POST("bbs/upload_conlist_v2.php")
    Call<calllogModel>  getList(

            @Field("mb_id") String mb_id,
            @Field("number") String number,
            @Field("type") String type,
            @Field("name") String name,
            @Field("date") String date,
            @Field("duration") String duration

    );

}
