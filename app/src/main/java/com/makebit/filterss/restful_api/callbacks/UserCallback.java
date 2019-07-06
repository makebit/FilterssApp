package com.makebit.filterss.restful_api.callbacks;

import com.makebit.filterss.models.User;

import retrofit2.Response;

public interface UserCallback {
    public void onLoad(Response<User> response);
    public void onFailure(Throwable t);
}
