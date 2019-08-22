package com.luckyaf.kommon.http.internal;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2019-08-05
 */
public class BandWidthLimiter {
    /* KB */
    private static Long KB = 1024L;

    /* The smallest count chunk length in bytes */
    private static Long CHUNK_LENGTH = 1024L;

    /* How many bytes will be sent or receive */
    private int bytesWillBeSentOrReceive = 0;

    /* When the last piece was sent or receive */
    private long lastPieceSentOrReceiveTick = System.nanoTime();

    /* Default rate is 1024KB/s */
    private int maxRate = 1024;

    /* Time cost for sending CHUNK_LENGTH bytes in nanoseconds */
    private long timeCostPerChunk = (1000000000L * CHUNK_LENGTH)
            / (this.maxRate * KB);

    /**
     * Initialize a BandwidthLimiter object with a certain rate.
     *
     * @param maxRate the download or upload speed in KBytes
     */
    public BandWidthLimiter(int maxRate) {
        this.setMaxRate(maxRate);
    }

    /**
     * Set the max upload or download rate in KB/s. maxRate must be grater than
     * 0. If maxRate is zero, it means there is no bandwidth limit.
     *
     * @param maxRate If maxRate is zero, it means there is no bandwidth limit.
     * @throws IllegalArgumentException
     */
    public synchronized void setMaxRate(int maxRate)
            throws IllegalArgumentException {
        if (maxRate < 0) {
            throw new IllegalArgumentException("maxRate can not less than 0");
        }
        this.maxRate = maxRate < 0 ? 0 : maxRate;
        if (maxRate == 0) {
            this.timeCostPerChunk = 0;
        } else {
            this.timeCostPerChunk = (1000000000L * CHUNK_LENGTH)
                    / (this.maxRate * KB);
        }
    }

    /**
     * Next 1 byte should do bandwidth limit.
     */
    public synchronized void limitNextBytes() {
        this.limitNextBytes(1);
    }

    /**
     * Next len bytes should do bandwidth limit
     *
     * @param len
     */
    public synchronized void limitNextBytes(int len) {
        this.bytesWillBeSentOrReceive += len;

        /* We have sent CHUNK_LENGTH bytes */
        while (this.bytesWillBeSentOrReceive > CHUNK_LENGTH) {
            long nowTick = System.nanoTime();
            long missedTime = this.timeCostPerChunk
                    - (nowTick - this.lastPieceSentOrReceiveTick);
            if (missedTime > 0) {
                try {
                    Thread.sleep(missedTime / 1000000,
                            (int) (missedTime % 1000000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.bytesWillBeSentOrReceive -= CHUNK_LENGTH;
            this.lastPieceSentOrReceiveTick = nowTick
                    + (missedTime > 0 ? missedTime : 0);
        }
    }
}

