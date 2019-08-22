package com.luckyaf.kommon.http.internal;

import com.luckyaf.kommon.http.callback.ReadCallback;

import java.io.IOException;
import java.io.InputStream;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2019-08-05
 */
public class LimitInputStream extends InputStream {

    private InputStream is;
    private ReadCallback mListener;
    private BandWidthLimiter bandWidthLimiter;

    public LimitInputStream(InputStream is, int limitSpeed, ReadCallback listener) {
        this.is = is;
        mListener = listener;
        bandWidthLimiter = new BandWidthLimiter(limitSpeed);
    }

    @Override
    public int read() throws IOException {
        if (this.bandWidthLimiter != null) {
            this.bandWidthLimiter.limitNextBytes();
        }
        int read = this.is.read();
        mListener.call(read);
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (bandWidthLimiter != null) {
            bandWidthLimiter.limitNextBytes(len);
        }
        int read = this.is.read(b, off, len);
        mListener.call(read);
        return read;
    }
}
