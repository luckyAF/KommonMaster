package com.luckyaf.kommon.http.internal;

import java.io.IOException;

import io.reactivex.annotations.NonNull;
import okhttp3.Response;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2019-08-22
 */
public interface Parser<T> {
    /**
     * 数据解析,Http请求成功后回调
     *
     * @param response Http执行结果
     * @return 解析后的对象类型
     * @throws IOException 网络异常、解析异常
     */
    T onParse(@NonNull Response response) throws IOException;

}
