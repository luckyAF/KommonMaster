package com.luckyaf.kommon.http.internal;

import com.google.gson.reflect.TypeToken;
import com.luckyaf.kommon.http.SmartHttp;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.observers.DeferredScalarDisposable;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 类描述：
 *
 * @author Created by luckyAF on 2019-06-21
 */
public final class ObservableHttp<T> extends Observable<T> {
    private OkHttpClient mOkHttpClient;
    private Request mRequest;
    private Parser<T> mParser;

    private Call mCall;

    public ObservableHttp(@NonNull OkHttpClient client, @Nullable Request request, @NonNull Parser<T> parser) {
        this.mOkHttpClient = client;
        this.mRequest = request;
        this.mParser = parser;
    }

    @Override
    public void subscribeActual(Observer<? super T> observer) {
        HttpDisposable d = new HttpDisposable(observer);
        observer.onSubscribe(d);
        if (d.isDisposed()) {
            return;
        }
        T value;
        try {
            value = ObjectHelper.requireNonNull(execute(), "返回数据为空");
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            if (!d.isDisposed()) {
                observer.onError(e);
            } else {
                RxJavaPlugins.onError(e);
            }
            return;
        }
        d.complete(value);
    }



    /**
     * 执行请求
     * @return 返回数据
     * @throws Exception 异常
     */
    private T execute() throws Exception {
        mCall = mOkHttpClient.newCall(mRequest);
        Response response =  mCall.execute();
        return  this.mParser.onParse(response);
    }


    class HttpDisposable extends DeferredScalarDisposable<T> {

        /**
         * Constructs a DeferredScalarDisposable by wrapping the Observer.
         *
         * @param downstream the Observer to wrap, not null (not verified)
         */
        HttpDisposable(Observer<? super T> downstream) {
            super(downstream);
        }

        @Override
        public void dispose() {
            cancelRequest(mCall);
            super.dispose();
        }
    }


    //关闭请求
    private void cancelRequest(Call call) {
        if (call != null && !call.isCanceled()){
            call.cancel();
        }
    }
}
