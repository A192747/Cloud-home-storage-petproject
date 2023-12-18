package org.example.bot;

import api.longpoll.bots.LongPollBot;
import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.model.events.messages.MessageNew;
import com.yandex.disk.rest.exceptions.ServerException;
import org.example.Main;
import org.example.StorageController;
import org.example.threads.Supervisor;
import org.example.status.BotStatus;

import java.io.*;
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
    @Override
    public void onMessageNew(MessageNew messageNew) {
        if (messageNew.getMessage().getFromId() == user_id) {
            try {
                String text = messageNew.getMessage().getText();
                switch (status) {
                    case MAIN -> {

                    }
                    case SAVE_QUESTION -> {
                        synchronized (Main.mutexWaitAnswer) {
                            switch (text) {
                                case "Сохранить" -> {
                                    sendMessage("Загрузка началась");
                                    StorageController.saveFromYandex();
                                    sendMessage("Файлы загружены");
                                    StorageController.deleteFromYandex();
                                    status = BotStatus.MAIN;
                                    sendMessage("Яндекс диск отчищен");
                                    Main.mutexWaitAnswer.notify();
                                }
                                case "Отмена" -> {
                                    StorageController.needDownloadFiles = null;
                                    status = BotStatus.MAIN;
                                    sendMessage("Отмена");
                                    Main.mutexWaitAnswer.notify();
                                }
                                default -> {
                                    sendMessage("Я вас не понял");
                                }
                            }
                        }
                    }
                }

            } catch(VkApiException | ServerException | IOException e){
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
