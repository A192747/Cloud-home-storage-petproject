package org.example.threads;

import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.methods.VkBotsMethods;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Resource;
import org.example.Main;
import org.example.bot.Bot;
import org.example.StorageController;
import org.example.bot.MyKeyboard;
import org.example.bot.BotStatus;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Supervisor implements Runnable {
    private static final Object mutex = Main.mutex;
    private static final Object mutexWaitAnswer = Main.mutexWaitAnswer;
    private VkBotsMethods Vk;
    private Properties properties;
    public Supervisor(VkBotsMethods vk, Properties prop) {
        Vk = vk;
        properties = prop;
    }
    @Override
    public void run() {
        try {
            //запустили демона, который постоянно просматривает есть ли изменения на яндекс диске
            Thread daemon = new Thread(new Daemon());
            daemon.setDaemon(true);
            daemon.start();
            while (true) {
                synchronized (mutex) {
                    mutex.wait();
                    Bot.status = BotStatus.SAVE_QUESTION;
                    Vk.messages.send()
                            .setPeerId(Integer.parseInt(properties.getProperty("user_vk_id")))
                            .setMessage("На диске обнаружены новые файлы. Убедитесь, что все файлы успешно загрузились " +
                                    "на яндекс диск, затем нажмите на кнопку Сохранить")
                            .setKeyboard(MyKeyboard.getKeyboard(Bot.status))
                            .execute();
                    synchronized (mutexWaitAnswer){
                        mutexWaitAnswer.wait();
                    }
                }

            }
        } catch (InterruptedException | VkApiException e) { //| ServerIOException | IOException e) {
            e.printStackTrace();
        }
    }
}
