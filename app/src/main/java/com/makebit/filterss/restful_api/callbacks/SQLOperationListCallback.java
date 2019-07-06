package com.makebit.filterss.restful_api.callbacks;

import com.makebit.filterss.models.SQLOperation;

import java.util.List;

public interface SQLOperationListCallback {
    public void onLoad(List<SQLOperation> sqlOperationList);
    public void onFailure();
}
