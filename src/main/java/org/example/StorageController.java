package org.example;

import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.methods.VkBotsMethods;
import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Resource;
import org.example.bot.MyKeyboard;
import org.example.status.BotStatus;
import org.example.status.ListStatus;
import org.example.status.StorageStatus;
import org.example.utils.Listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class StorageController {
    public static List<Resource> needDownloadFiles;

    private static Listener listener = new Listener();
    private static final Properties properties = Main.properties;
    private static RestClient restClient;
    private static String mainPath = properties.getProperty("location_to_sync");

    public static StorageStatus status;
    public static ListStatus listStatus;
    private static ResourcesArgs.Builder build;
    public static List<Resource> info;

    private StorageController(){}

    public static void init(){
        Credentials credentials = new Credentials("fedor", properties.getProperty("yandex_token"));
        restClient = new RestClient(credentials);
        status = StorageStatus.NONE;
        listStatus = ListStatus.NONE;
        build = new ResourcesArgs.Builder();
        build.setPath("/");
        info = new ArrayList<>();
    }
    public static void createSubdirectories(String path) {
        System.out.println(mainPath + path.substring(0, path.lastIndexOf("/")));
        File dir = new File(mainPath + path.substring(0, path.lastIndexOf("/")));
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    public static long getYandexFreeStorageSize() throws ServerIOException, IOException {
        return restClient.getDiskInfo().getTotalSpace();
    }
    public static long getYandexUsedStorageSize() throws ServerIOException, IOException {
        return restClient.getDiskInfo().getUsedSpace();
    }

    public static String getAllStorageInfo() throws ServerIOException, IOException, VkApiException {
        return "Занятого места на я.диске около: " + Math.round(getYandexUsedStorageSize() / Math.pow(1024, 2))+ "мб\n"
                + "Свободного места на я.диске около: " + getYandexFreeStorageSize() / Math.pow(1024, 3) + "гб\n"
                + "Авто очистка корзины после загрузки файлов на лок. хранилище: " + (StorageController.getAutoCleanUpValue() ? "включено" : "отключено");
    }
    public static void saveFromYandex() throws ServerException, IOException {
        String path;
        for(Resource res : needDownloadFiles) {
            path = res.getPath().getPath();
            System.out.println(path);
            if(!res.isDir() && path.split("/").length > 2)
                createSubdirectories(path);
            saveFileFromYandex(path);
        }
    }
    private static void saveFileFromYandex(String path) throws ServerException, IOException {
        File file = new File(mainPath + path);
        if (file.exists())
            file.delete();
        restClient.downloadFile(path,
                new File(mainPath + path),
                listener);
    }
    public static void deleteFromYandex() throws ServerIOException, IOException {
        deleteFromYandexMain();
        //можно вынести как отдельную функцию по очищению корзины, если выключена авто очистка
        //deleteFromYandexTrash();
    }
    private static void deleteFromYandexMain() throws ServerIOException, IOException {
        String path;
        List<Resource> list = restClient.getResources(build.build()).getResourceList().getItems();
        for(Resource res : list) {
            path = res.getPath().getPath();
            deleteFileFromYandex(path);
        }
    }
    private static boolean autoCleanUp = true;
    public static void controlYandexAutoCleanUpTrash(){
        autoCleanUp = !autoCleanUp;
    }

    public static boolean getAutoCleanUpValue() {
        return autoCleanUp;
    }

    public static void deleteFromYandexTrash() throws ServerIOException, IOException {
        String path;
        List<Resource> list = restClient.getTrashResources(build.build()).getResourceList().getItems();
        System.out.println(list);
        for(Resource res : list) {
            path = res.getPath().getPath();
            System.out.println(path);
            restClient.deleteFromTrash(path);
        }
    }
    private static void deleteFileFromYandex(String path) throws ServerIOException, IOException {
        restClient.delete(path, autoCleanUp);
    }
    public static List<Resource> getDiskInfo() throws ServerIOException, IOException {
        return restClient.getLastUploadedResources(build.build()).getItems();
    }

}
