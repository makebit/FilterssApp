package com.makebit.filterss.restful_api.callbacks;

import com.makebit.filterss.models.SQLOperation;

public interface SQLOperationCallbackLocal {
    public void onLoad(SQLOperation sqlOperation);
    public void onFailure(Throwable t);
}
