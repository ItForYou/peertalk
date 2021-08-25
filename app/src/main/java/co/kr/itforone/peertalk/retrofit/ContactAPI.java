package co.kr.itforone.peertalk.retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ContactAPI {

    @FormUrlEncoded
    @POST("bbs/upload_contacts.php")
    Call<contactModel>  getList(

            @Field("names") String names,
            @Field("numbers") String numbers,
            @Field("mb_id") String mb_id

    );

}
