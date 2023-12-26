package org.example;

import api.longpoll.bots.exceptions.VkApiException;
import org.example.bot.Bot;
import org.example.bot.MyKeyboard;
import org.example.utils.JsonWorker;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static final Object mutex = new Object();
    public static final Object mutexWaitAnswer = new Object();
    public static Properties properties;
    public static File locationToSync;
    public static void main(String[] args) {
        try {

            initProperties();
            JsonWorker.init();
            MyKeyboard.init();

            Bot bot = new Bot();
            //запустили супервизор
            bot.startSupervisor();
            //запустили бота
            bot.startPolling();

        } catch (IOException | VkApiException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initProperties() throws IOException {
        File file = new File("config.properties");
        properties = new Properties();
        properties.load(new FileReader(file));
        StorageController.init();
        locationToSync = new File(properties.getProperty("location_to_sync").toString());
    }

}

