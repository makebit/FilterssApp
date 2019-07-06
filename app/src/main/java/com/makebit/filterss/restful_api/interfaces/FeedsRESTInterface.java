package com.makebit.filterss.restful_api.interfaces;

import com.makebit.filterss.models.Feed;
import com.makebit.filterss.models.SQLOperation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FeedsRESTInterface {
    @GET("v1/feeds")
    public Call<List<Feed>> getAllFeeds(@Query("lang") String lang);

    @GET("v1/feeds")
    public Call<List<Feed>> getFilteredFeedsBySearch(@Query("search") String searchFilter);

    @GET("v1/feeds")
    public Call<List<Feed>> getFilteredFeedsByCategory(@Query("category") String category);

    @GET("v1/feeds")
    public Call<List<Feed>> getFilteredFeedsBySearchAndCategory(@Query("lang") String lang,
                                                                @Query("search") String searchFilter,
                                                                @Query("category") String category);
}
