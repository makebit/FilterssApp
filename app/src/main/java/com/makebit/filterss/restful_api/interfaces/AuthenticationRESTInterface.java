package com.makebit.filterss.restful_api.interfaces;

import com.makebit.filterss.models.SQLOperation;
import com.makebit.filterss.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthenticationRESTInterface {
    @GET("v1/auth/login")
    public Call<User> getUserAuthentication(@Query("email") String email,
                                            @Query("password") String password);

    @FormUrlEncoded
    @POST("v1/auth/password/change")
    public Call<SQLOperation> changeUsersPassword(@Field("email") String email,
                                                  @Field("password") String password);

    @FormUrlEncoded
    @POST("v1/auth/login/google")
    public Call<User> getUserAuthenticationGoogle(@Field("google_token") String googleToken);


    @FormUrlEncoded
    @POST("v1/auth/registration")
    public Call<SQLOperation> registerNewUser(@Field("name") String name,
                                              @Field("surname") String surname,
                                              @Field("email") String email,
                                              @Field("password") String password,
                                              @Field("locale") String locale);
}