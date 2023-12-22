package org.example;

import api.longpoll.bots.exceptions.VkApiException;
import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.exceptions.WrongMethodException;
import com.yandex.disk.rest.json.Resource;
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

    private static ResourcesArgs.Builder build;
    public static List<Resource> info;

    public static String chosenPath = "";

    private StorageController(){}

    public static void init(){
        Credentials credentials = new Credentials("fedor", properties.getProperty("yandex_token"));
        restClient = new RestClient(credentials);
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
                + "Авто очистка корзины после загрузки файлов на лок. хранилище: " + ((StorageController.getAutoCleanUpValue() ? "включено" : "отключено") + "\n"
                + "Выбранный путь для сохранения файлов: " + (chosenPath.length() == 0 ? "/" : chosenPath));
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
        chosenPath = "";
    }
    private static void saveFileFromYandex(String path) throws ServerException, IOException {
        File file = new File(mainPath + "\\" + chosenPath + path);
        if (file.exists())
            file.delete();
        System.out.println(mainPath + "\\" + chosenPath + path);
        restClient.downloadFile(path,
                new File(mainPath + "\\"+ chosenPath + path),
                listener);
    }
    public static void deleteFromYandex() throws ServerIOException, IOException {
        deleteFromYandexMain();
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

    public static List<String> findSimilarFiles(List<String> names) {
        List<String> list = new ArrayList<>();
        List<String> result = new ArrayList<>();
        File directory = new File(mainPath);
        for (String name: names) {
            if (directory.isDirectory()) {
                getFilesAndFoldersList(directory, list, mainPath);
                for (String file : list) {
                    file = file.substring(mainPath.length(), file.length());
                    if (file.contains(name) && !result.contains(file))
                        result.add(file);
                }
            }
        }
        return result;

    }
    private static void getFilesAndFoldersList(File directory, List<String> filesAndFolders, String basePath) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                filesAndFolders.add(file.getAbsolutePath());
                getFilesAndFoldersList(file, filesAndFolders, basePath);
            } else {
                filesAndFolders.add(file.getAbsolutePath());
            }
        }
    }

    public static List<String> findSimilarPath(String name) {
        List<String> list = new ArrayList<>();
        List<String> result = new ArrayList<>();
        File directory = new File(mainPath);
        if (directory.isDirectory()) {
            getFoldersList(directory, list, mainPath);
            for (String folder : list) {
                folder = folder.substring(mainPath.length(), folder.length());
                if(folder.contains(name))
                    result.add(folder);
            }
        }
        return result;

    }
    private static void getFoldersList(File directory, List<String> folders, String path) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                String folderPath = path + file.getName() + "/";
                folders.add(folderPath);
                getFoldersList(file, folders, folderPath);
            }
        }
    }
    public static List<String> uploadPaths;
    public static void uploadFilesToYandex() throws ServerException, IOException {
        System.out.println("111" + uploadPaths);
        for (String path: uploadPaths) {
            uploadFile(path);
        }
        uploadPaths = null;
    }
    public static void deleteFromStorage() {
        for (String path: uploadPaths) {
            deleteFileFromStorage(path);
        }
        uploadPaths = null;
    }
    private static void deleteFileFromStorage(String path) {
        File file = new File(mainPath + path);
        if(file.exists()) {
            //добавить удаление папок
            file.delete();
        }
    }
    private static void uploadFile(String path) throws ServerException, IOException {
        System.out.println(path);
        //добавить загрузку папок
        restClient.uploadFile(restClient.getUploadLink(path.substring(path.lastIndexOf("\\") + 1), true),
                true,
                new File(mainPath + path.substring(path.lastIndexOf("/") + 1)),
                listener);
    }


}
