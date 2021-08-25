package co.kr.itforone.peertalk.retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface chkTotalAPI {

    @FormUrlEncoded
    @POST("bbs/chk_total.php")
    Call<chkTotalModel> getList(

            @Field("mb_id") String mb_id,
            @Field("number") String number,
            @Field("name") String name

    );

}
