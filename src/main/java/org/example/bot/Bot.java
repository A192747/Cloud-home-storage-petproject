package org.example.bot;

import api.longpoll.bots.LongPollBot;
import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.model.events.messages.MessageNew;
import com.google.gson.JsonObject;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import org.example.Main;
import org.example.StorageController;
import org.example.threads.Supervisor;
import org.example.utils.PathToImage;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Bot extends LongPollBot {

    private static final Properties properties = Main.properties;
    public static BotStatus status = BotStatus.MAIN;
    private static final int user_id = Integer.parseInt(properties.getProperty("user_vk_id"));
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
                sendMessage("Очистка корзины началась");
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
                if(Main.locationToSync.isDirectory()) {
                    if(Main.locationToSync.listFiles().length > 0) {
                        sendMessage("Напишите название папок или файлов, которые хотите загрузить на я.диск\n" +
                                        "*Удобнее загружать папку целиком на диск*",
                                PathToImage.getPathImage(false));
                    } else {
                        status = BotStatus.MAIN;
                        sendMessage("Нет файлов для загрузки!");
                    }
                } else {
                    status = BotStatus.MAIN;
                    sendMessage("Не возможно выбрать файлы. Проверьте правильность указанного пути!");
                }
            }
            case "getPathForDirsImage" -> {
                status = BotStatus.SELECT_DIR;
                if(Main.locationToSync.isDirectory()) {
                    if (Main.locationToSync.listFiles().length > 0) {
                        sendMessage("Напишите название папки, чтобы файлы из я.диска сохранились именно в неё",
                                PathToImage.getPathImage(true));
                    } else {
                        status = BotStatus.MAIN;
                        sendMessage("Так как в папке, в которую сохраняются файлы нет папок для выбора, будет использоваться" +
                                " путь по умолчанию: /");
                    }
                } else {
                    status = BotStatus.MAIN;
                    sendMessage("Не возможно выбрать файлы. Проверьте правильность указанного пути!");
                }
            }
            case "setDefaultPath" -> {
                status = BotStatus.MAIN;
                StorageController.chosenPath = "";
                sendMessage("Путь сохранения файлов сброшен на стандартный: /");
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

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static List<String> paths = new ArrayList<>();
    private static List<String> filesPaths;
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
                        if (obj != null) {
                            handle(obj);
                            return;
                        }
                        List<String> list = StorageController.findSimilarFiles(List.of(text.split(", ")));
                        System.out.println(list);
                        StringBuilder answer = new StringBuilder();
                        if(list.size() > 1) {
                            answer.append("Нашлось несколько вариантов. Напишите цифры необходимых вам файлов/папок:\n");
                            status = BotStatus.SELECT_FILES_NUMBER;
                        } else {
                            if (list.isEmpty()) {
                                answer.append("Файлов или папки с таким названием не существует!\n");
                                sendMessage(answer.toString());
                                invokeMethod("getPathImage");
                                return;
                            } else {
                                answer.append("Нашелся подходящий вариант:\n");
                                status = BotStatus.WHAT_NEXT;
                            }
                        }
                        int counter = 0;
                        filesPaths = list;
                        for (String path : list) {
                            counter++;
                            if(list.size() == 1)
                                answer.append(path).append("\n");
                            else
                                answer.append(counter).append(" ").append(path).append("\n");
                        }
                        if (filesPaths.size() == 1) {
                            StorageController.uploadPaths = filesPaths;
                            answer.append("Файл выбран");
                        }
                        sendMessage(answer.toString());
                    }
                    case SELECT_FILES_NUMBER -> {
                        if (obj != null) {
                            handle(obj);
                            return;
                        }
                        String[] numbers = text.split(", ");
                        StorageController.uploadPaths = new ArrayList<>();
                        if(text.equals("Все")) {
                            StorageController.uploadPaths = filesPaths;
                        } else {
                            for (String num : numbers) {
                                if (isNumeric(num)
                                        && 0 < Integer.parseInt(num)
                                        && Integer.parseInt(num) <= filesPaths.size()) {
                                    StorageController.uploadPaths.add(filesPaths.get(Integer.parseInt(num) - 1));
                                } else {
                                    sendMessage("Введите число в пределах от 1 до " + paths.size());
                                    return;
                                }
                            }
                        }
                        status = BotStatus.WHAT_NEXT;
                        StringBuilder paths = new StringBuilder("");
                        for (String str : StorageController.uploadPaths) {
                            paths.append(str).append("\n");
                        }
                        sendMessage("Выбраны следующие файлы: \n" + paths);
                    }
                    case WHAT_NEXT -> {
                        if ((obj == null || !obj.has("key"))) {
                            handle(obj);
                            return;
                        }
                        status = BotStatus.MAIN;
                        switch (obj.get("key").getAsString()) {
                            case "upload" -> new Thread(() -> {
                                try {
                                    String answer = obj.get("answer").getAsString();
                                    sendMessage("Началась загрузка файлов");
                                    StorageController.uploadFilesToYandex();
                                    sendMessage(answer);
                                } catch (ServerException | IOException | VkApiException e) {
                                    throw new RuntimeException(e);
                                }
                            }).start();
                            case "delete" -> new Thread(() -> {
                                try {
                                    String answer = obj.get("answer").getAsString();
                                    sendMessage("Началось удаление файлов");
                                    StorageController.deleteFromStorage();
                                    sendMessage(answer);
                                } catch (VkApiException e) {
                                    throw new RuntimeException(e);
                                }
                            }).start();
                            case "cansel" -> {
                                StorageController.uploadPaths = null;
                                sendMessage(obj.get("answer").getAsString());
                            }
                        }
                    }
                    case SELECT_DIR -> {
                        if (obj != null) {
                            handle(obj);
                            return;
                        }
                        List<String> list = StorageController.findSimilarPath(text);
                        StringBuilder answer = new StringBuilder("");
                        if(list.size() > 1) {
                            answer.append("Нашлось несколько вариантов. Напишите цифру необходимого вам пути:\n");
                            status = BotStatus.SELECT_DIR_NUMBER;
                        } else {
                            if (list.isEmpty()) {
                                answer.append("Папки с таким названием не существует!\n");
                                sendMessage(answer.toString());
                                invokeMethod("getPathForDirsImage");
                                return;
                            } else {
                                answer.append("Нашелся подходящий вариант:\n");
                                status = BotStatus.MAIN;
                            }
                        }
                        int counter = 0;
                        paths = list;
                        for (String path : list) {
                            counter++;
                            if(list.size() == 1)
                                answer.append(path + "\n");
                            else
                                answer.append(counter + " " + path + "\n");
                        }
                        if(paths.size() == 1) {
                            StorageController.chosenPath = paths.get(0);
                            answer.append("Папка выбрана. Можете загружать файлы на яндекс диск");
                        }
                        sendMessage(answer.toString());

                    }
                    case SELECT_DIR_NUMBER -> {
                        if (obj != null) {
                            handle(obj);
                            return;
                        }
                        if(isNumeric(text)
                                && 0 < Integer.valueOf(text)
                                && Integer.valueOf(text) <= paths.size()) {
                            StorageController.chosenPath = paths.get(Integer.valueOf(text) - 1);
                            status = BotStatus.MAIN;
                            sendMessage("Папка выбрана. Можете загружать файлы на яндекс диск");
                        } else {
                            sendMessage("Введите число в пределах от 1 до " + paths.size());
                        }
                    }
                    case SAVE_QUESTION -> {
                        synchronized (Main.mutexWaitAnswer) {
                            if ((obj == null || !obj.has("key"))) {
                                handle(obj);
                                return;
                            }
                            if (obj.has("key")) {
                                switch (obj.get("key").getAsString()) {
                                    case "save" -> {

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
                try {
                    sendMessage("Я сломался");
                    throw new RuntimeException(e);
                } catch (VkApiException ex) {
                    throw new RuntimeException(e);
                }

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
