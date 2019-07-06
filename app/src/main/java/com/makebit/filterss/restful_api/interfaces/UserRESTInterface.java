package com.makebit.filterss.restful_api.interfaces;

import com.makebit.filterss.models.Collection;
import com.makebit.filterss.models.Multifeed;
import com.makebit.filterss.models.SQLOperation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface UserRESTInterface {
    /*********************** User - Feeds *********************************/
    @FormUrlEncoded
    @PUT("v1/user/feeds")
    public Call<SQLOperation> addUserFeed(@Query("token") String token,
                                          @Field("feed") int feed,
                                          @Field("multifeed") int multifeed);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "v1/user/feeds", hasBody = true)
    public Call<SQLOperation> deleteUserFeed(@Query("token") String token,
                                             @Field("feed") int feed,
                                             @Field("multifeed") int multifeed);

    @FormUrlEncoded
    @PUT("v1/user/feed/")
    Call<SQLOperation> createNewFeed(@Query("token") String token,
                                     @Field("multifeed") int multifeed,
                                     @Field("title") String title,
                                     @Field("link") String link);


    /*********************** User - Multifeeds *********************************/
    @GET("v1/user/multifeeds")
    public Call<List<Multifeed>> getUserMultifeeds(@Query("token") String token);

    @FormUrlEncoded
    @PUT("v1/user/multifeeds")
    public Call<SQLOperation> addUserMultifeed(@Query("token") String token,
                                               @Field("title") String title,
                                               @Field("color") int color,
                                               @Field("rating") int rating);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "v1/user/multifeeds", hasBody = true)
    public Call<SQLOperation> deleteUserMultifeed(@Query("token") String token,
                                                  @Field("multifeed") int multifeed);

    @FormUrlEncoded
    @PATCH("v1/user/multifeeds")
    public Call<SQLOperation> updateUserMultifeed(@Query("token") String token,
                                                  @Field("multifeed") int multifeed,
                                                  @Field("title") String title,
                                                  @Field("color") int color,
                                                  @Field("rating") int rating);

    /*********************** User - Collections *********************************/
    @GET("v1/user/collections")
    public Call<List<Collection>> getUserCollections(@Query("token") String token);

    @FormUrlEncoded
    @PUT("v1/user/collections")
    public Call<SQLOperation> addUserCollection(@Query("token") String token,
                                                @Field("title") String title,
                                                @Field("color") int color);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "v1/user/collections", hasBody = true)
    public Call<SQLOperation> deleteUserCollection(@Query("token") String token,
                                                   @Field("collection") int collection);

    @FormUrlEncoded
    @PATCH("v1/user/collections")
    public Call<SQLOperation> updateUserCollection(@Query("token") String token,
                                                   @Field("collection") int collection,
                                                   @Field("title") String newTitle,
                                                   @Field("color") int newColor);

    /*********************** User - SavedArticles *********************************/
    @FormUrlEncoded
    @PUT("v1/user/collection/article")
    public Call<SQLOperation> addArticleToCollection(
            @Query("token") String token,
            @Field("title") String title,
            @Field("description") String description,
            @Field("link") String link,
            @Field("img_link") String img_link,
            @Field("pub_date") String pub_date,
            @Field("feed") int feed,
            @Field("collection") int collection);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "v1/user/collection/article", hasBody = true)
    public Call<SQLOperation> deleteArticleFromCollection(@Query("token") String token,
                                                          @Field("article") String article,
                                                          @Field("collection") int collection);


    /*********************** User - ReadArticles *********************************/
    @FormUrlEncoded
    @PUT("v1/articles/opened")
    public Call<SQLOperation> addUserOpenedArticle(@Query("token") String token,
                                                   @Field("article") String article);

    @FormUrlEncoded
    @PUT("v1/articles/read")
    public Call<SQLOperation> addUserReadArticle(@Query("token") String token,
                                                 @Field("article") String article);

    @FormUrlEncoded
    @PUT("v1/articles/feedback")
    public Call<SQLOperation> addUserFeedbackArticle(@Query("token") String token,
                                                     @Field("article") String article,
                                                     @Field("vote") int vote);

}
