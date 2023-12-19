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
            System.out.println("Daemon is working");

//            info = StorageController.getDiskInfo();
//            StorageController.prev = info;
//            StorageController.inTrash = StorageController.getInTrash();
            while(true){
                synchronized (mutex) {
                    if (!StorageController.getDiskInfo().toString().equals(StorageController.info.toString())){
                        System.out.println("Я сработал демон");
                        System.out.println(StorageController.getDiskInfo());
                        System.out.println(StorageController.info);
                        System.out.println("*************************");
                        mutex.notify();
                    }
                }
                Thread.sleep(delay);

            }

        } catch (Exception e){
            System.out.println(e);
        }
    }
}
