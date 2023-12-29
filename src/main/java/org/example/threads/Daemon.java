package org.example.threads;

import com.yandex.disk.rest.json.Resource;
import org.example.Main;
import org.example.StorageController;

import java.util.ArrayList;
import java.util.List;

public class Daemon implements Runnable{
    private static final Object mutex = Main.mutex;
    private static final int delay = Integer.parseInt(Main.properties.getProperty("update_delay"));


    @Override
    public void run() {
        try {
            while(true){
                synchronized (mutex) {
                    if (!StorageController.getDiskInfo().toString().equals(StorageController.info.toString())){
                        mutex.notify();
                    }
                }
                Thread.sleep(delay);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
