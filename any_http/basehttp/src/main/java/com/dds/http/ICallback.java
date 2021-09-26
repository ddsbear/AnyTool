package com.dds.http;

/**
 * Created by dds on 2018/4/23.
 */

public interface ICallback<T> {

    void onSuccess(T result);

    void onFailure(int code, Throwable t);
}
