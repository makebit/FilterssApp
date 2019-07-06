package com.makebit.filterss.restful_api.callbacks;

import com.makebit.filterss.models.Article;

import java.util.List;

public interface ArticleCallback {
    public void onLoad(List<Article> articles);
    public void onFailure();
}
