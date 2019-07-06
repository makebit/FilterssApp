package com.makebit.filterss.restful_api.callbacks;

import com.makebit.filterss.models.SQLOperation;

import retrofit2.Response;

public interface SQLOperationCallback {
    public void onLoad(Response<SQLOperation> response);
    public void onFailure(Throwable t);
}
