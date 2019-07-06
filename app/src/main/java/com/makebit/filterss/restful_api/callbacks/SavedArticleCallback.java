package com.makebit.filterss.restful_api.callbacks;

import com.makebit.filterss.models.SavedArticle;

import java.util.List;

public interface SavedArticleCallback {
    public void onLoad(List<SavedArticle> savedArticles);
    public void onFailure();
}
