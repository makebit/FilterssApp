package com.makebit.filterss.restful_api.callbacks;

import com.makebit.filterss.models.Collection;

import java.util.List;

import retrofit2.Response;

public interface CollectionCallback {
    public void onLoad(Response<List<Collection>> response);
    public void onFailure(Throwable t);
}
