package com.makebit.filterss.restful_api.callbacks;

import com.makebit.filterss.models.Multifeed;

import java.util.List;

import retrofit2.Response;

public interface MultifeedCallback {
    public void onLoad(Response<List<Multifeed>> response);
    public void onFailure(Throwable t);
}
