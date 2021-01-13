package co.kr.itforone.peertalk.retrofit;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface RetrofitAPI {

    @GET("upload_contacts.php")
    Call<responseModel>  getList(
            @QueryMap Map<String,String> option
    );


}
