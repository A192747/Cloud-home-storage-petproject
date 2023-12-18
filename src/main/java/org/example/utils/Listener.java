package org.example.utils;

import com.yandex.disk.rest.ProgressListener;

public class Listener implements ProgressListener {

    @Override
    public void updateProgress(long l, long l1) {

    }

    @Override
    public boolean hasCancelled() {
        return false;
    }
}
