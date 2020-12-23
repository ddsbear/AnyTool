package com.dds.library.net;

import com.utils.library.net.HttpRequest;
import com.utils.library.net.HttpRequestPresenter;
import com.utils.library.net.ICallback;

import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by dds on 2019/9/4.
 * android_shuai@163.com
 */
public class RxHttpRequestPresenter extends HttpRequestPresenter {

    private RxHttpRequestPresenter(HttpRequest httpRequest) {
        super(httpRequest);
    }


    public Observable get(String url, Map<String, String> params) {
        return Observable.create(emitter -> httpRequest.get(url, params, new ICallback<String>() {
            @Override
            public void onSuccess(String result) {
                emitter.onNext(result);
                emitter.onComplete();
            }

            @Override
            public void onFailure(int code, Throwable t) {
                emitter.onError(t);
            }
        }));

    }

    public Observable post(String url, Map<String, String> params) {
        return Observable.create(emitter -> httpRequest.post(url, params,
                new ICallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        emitter.onNext(result);
                        emitter.onComplete();
                    }

                    @Override
                    public void onFailure(int code, Throwable t) {
                        emitter.onError(t);
                        emitter.onComplete();
                    }
                }));

    }

}
