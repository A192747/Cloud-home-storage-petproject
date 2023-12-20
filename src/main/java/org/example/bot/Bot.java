package org.example.bot;

import api.longpoll.bots.LongPollBot;
import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.methods.VkBotsMethods;
import api.longpoll.bots.model.events.messages.MessageNew;
import com.google.gson.JsonObject;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import org.example.Main;
import org.example.StorageController;
import org.example.threads.Supervisor;
import org.example.status.BotStatus;
import org.example.utils.PathToImage;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Bot extends LongPollBot {

    private static final Properties properties = Main.properties;
    public static BotStatus status = BotStatus.MAIN;
    private static int user_id = Integer.parseInt(properties.getProperty("user_vk_id"));
    public void startSupervisor(){
        Thread thread = new Thread (new Supervisor(vk, properties));
        thread.start();
    }

    private void sendMessage(String str) throws VkApiException {
        vk.messages.send()
                .setMessage(str)
                .setPeerId(user_id)
                .setKeyboard(MyKeyboard.getKeyboard(status))
                .execute();
    }
    private void sendMessage(String str, File photo) throws VkApiException {
        vk.messages.send()
                .setMessage(str)
                .setPeerId(user_id)
                .setKeyboard(MyKeyboard.getKeyboard(status))
                .addPhoto(photo)
                .execute();
    }
    private void getAllStorageInfo() throws VkApiException, ServerIOException, IOException {
        sendMessage(StorageController.getAllStorageInfo());
    }
    private void invokeMethod(String name) throws VkApiException, ServerIOException, IOException {
        switch (name) {
            case "getAllStorageInfo" -> {
                StorageController.info = new ArrayList<>();
                getAllStorageInfo();
            }
            case "controlYandexAutoCleanUpTrash" -> {
                StorageController.controlYandexAutoCleanUpTrash();
                sendMessage("Авто очищение корзины после загрузки файлов " + (StorageController.getAutoCleanUpValue()
                        ? "включено" : "отключено"));
            }
            case "deleteFromYandexTrash" -> {
                new Thread(() -> {
                    try {
                        StorageController.deleteFromYandexTrash();
                        sendMessage("Корзина очищена");
                    } catch (Exception e) {
                        System.out.println("Не удалось очистить корзину");
                        throw new RuntimeException(e);
                    }
                }).start();
            }
            case "getPathImage" -> {
                status = BotStatus.SELECT_A_FILE;
                sendMessage("Напишите название папок или файлов, которые хотите загрузить на я.диск\n" +
                                "*Удобнее загружать папку целиком на диск*",
                        PathToImage.getPathImage());
            }

        }
    }

    private void handle(JsonObject obj) throws InvocationTargetException, IllegalAccessException, VkApiException, ServerIOException, IOException {
        if(obj == null){
            sendMessage("Я вас не понял");
            return;
        }
        if(obj.has("function_name")) {
            String name = obj.get("function_name").getAsString();
            invokeMethod(name);
            return;
        }
        if(obj.has("next_status")) {
            String nextStatus = obj.get("next_status").getAsString();
            status = Arrays.stream(BotStatus.values())
                    .filter(elem -> elem.toString().equals(nextStatus))
                    .toList().get(0);
        }
        if(obj.has("answer")) {
            sendMessage(obj.get("answer").getAsString());
        }
    }
    @Override
    public void onMessageNew(MessageNew messageNew) {
        if (messageNew.getMessage().getFromId() == user_id) {
            try {
                String text = messageNew.getMessage().getText();
                JsonObject obj;
                if(messageNew.getMessage().getPayload() != null)
                    obj = messageNew.getMessage().getPayload().getAsJsonObject();
                else
                    obj = null;
                switch (status) {
                    case MAIN, SETTINGS ->
                        handle(obj);
                    case SELECT_A_FILE -> {
                        //Надо добавить
                    }
                    case SAVE_QUESTION -> {
                        synchronized (Main.mutexWaitAnswer) {
                            if (obj.has("key")) {
                                switch (obj.get("key").getAsString()) {
                                    case "save" -> {
//                                        new Thread(() ->{
//                                            try {
                                        sendMessage("Загрузка началась");
                                        StorageController.saveFromYandex();

                                        status = BotStatus.MAIN;
                                        sendMessage("Файлы загружены");
                                        StorageController.deleteFromYandex();
                                        if(StorageController.getAutoCleanUpValue()) {
                                            sendMessage("Яндекс диск очищен");
                                        }

                                        StorageController.info = new ArrayList<>();
                                        Thread.sleep(500);
                                        Main.mutexWaitAnswer.notify();
//                                            }catch (ServerException | VkApiException | IOException | InterruptedException e) {
//                                                throw new RuntimeException(e);
//                                            }
//                                        }).start();

                                    }
                                    case "update" -> {
                                        StorageController.info = new ArrayList<>();
                                        status = BotStatus.MAIN;
                                        Thread.sleep(500);
                                        Main.mutexWaitAnswer.notify();
                                    }
                                    case "cansel" -> {
                                        StorageController.info = StorageController.needDownloadFiles;
                                        //StorageController.needDownloadFiles = null;
                                        status = BotStatus.MAIN;
                                        sendMessage("Отмена");
                                        Thread.sleep(500);
                                        Main.mutexWaitAnswer.notify();
                                    }
                                }
                            } else {
                                sendMessage("Я вас не понял");
                            }
                        }
                    }
                }

            } catch(VkApiException | ServerException | IOException | InvocationTargetException |
                    IllegalAccessException | InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getAccessToken() {
        return properties.getProperty("vk_token");
    }


//        Credentials credentials = new Credentials("fedor", properties.getProperty("yandex_token"));
//        RestClient restClient = new RestClient(credentials);
//
//        ResourcePath path;
//        for (Resource res: list) {
//            System.out.println(res.getName());
//        }
//        ResourcesArgs.Builder builder = new ResourcesArgs.Builder();
//        builder.setPath("/newFolder");
//        path = restClient.getResources(builder.build()).getResourceList().getItems().get(0).getPath();
//        System.out.println(path);
//        Listener listener = new Listener();
//
//        //СКАЧИВАНИЕ ФАЙЛА С ЯНДЕКС ДИСКА
//        //restClient.downloadFile(path.getPath(), new File(properties.getProperty("location_to_sync") + "ss.pdf"), listener);
//        System.out.println("файл скачан");
//
//        //ЗАГРУЗКА ФАЙЛА НА ЯНДЕКС ДИСК
//        System.out.println(path.getPath().substring(0,15));
//        restClient.uploadFile(restClient.getUploadLink(path.getPath().substring(0,15), true),
//                true,
//                new File(properties.getProperty("location_to_sync") + "ss.pdf"),
//                listener);
//
//
//        System.out.println("файл загружен!");


}
