package com.makebit.filterss.restful_api.callbacks;

import com.makebit.filterss.models.ArticlesScores;

import java.util.List;

import retrofit2.Response;

public interface ArticlesScoresCallback {
    public void onLoad(Response<List<ArticlesScores>> response);
    public void onFailure(Throwable t);
}
