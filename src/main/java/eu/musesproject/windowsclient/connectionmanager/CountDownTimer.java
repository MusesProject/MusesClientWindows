package eu.musesproject.windowsclient.connectionmanager;

import java.util.concurrent.TimeUnit;

/**
 * Created by q on 6/3/15.
 */
public abstract class CountDownTimer {
    private int requestTimeoutSeconds;

    public CountDownTimer(int requestTimeoutSeconds) {
        this.requestTimeoutSeconds = requestTimeoutSeconds;
    }

    public void start() {
        try {
            TimeUnit.MILLISECONDS.sleep(requestTimeoutSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onFinish();
    }

    abstract void onFinish();

    public abstract void cancel();
}