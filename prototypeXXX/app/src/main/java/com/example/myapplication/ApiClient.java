package com.example.myapplication;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiClient {
    // Login
    @GET("login.php")
    Observable<LoginModel> getLogin(@Query("Mail_address") String mail,
                                    @Query("password") String pass);
    @POST("request_join.php")
    @FormUrlEncoded
    Observable<LoginModel> getRegister(@Field("Mail_address") String mail,
                                    @Field("Password") String pass,
                                       @Field("User_name") String userName);
}


