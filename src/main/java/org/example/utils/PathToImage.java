package org.example.utils;

import org.example.Main;
import org.example.StorageController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

public class PathToImage {
    public static File getPathImage(boolean onlyDirs)  {
        wight = 0;
        height = 0;
        String path = Main.properties.getProperty("location_to_sync").toString();
        File directory = new File(path);
        if (directory.isDirectory()) {
            BufferedImage image = new BufferedImage(10000, 10000, BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString(directory.getName(), 20, 20);
            printRecursiveTree(directory, 40, 0, g, onlyDirs);
            g.dispose();
            BufferedImage croppedImage = image.getSubimage(0, 20, wight, height); // Обрезаем изображение по заданным размерам
            try {
                File output = new File("tree.png");
                ImageIO.write(croppedImage, "png", output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Путь не указан или не является директорией.");
        }
        return new File("tree.png");
    }
    private static int wight;
    private static int height;
    private static String dirStr = "\\---";
    private static String fileStr = "|---";
    private static String tempName = "";

    private static int printRecursiveTree(File directory, int y, int level, Graphics g, boolean onlyDirs) {
        File[] files = directory.listFiles();
        List<File> sortedList = new ArrayList<>();
        List<File> listOfDirs = new ArrayList<>();
        for(File file : files){
            if(file.isDirectory())
                listOfDirs.add(file);
            if(file.isFile() && !onlyDirs)
                sortedList.add(file);
        }
        for (File file : listOfDirs){
            sortedList.add(file);
        }
        for (File file : sortedList) {
            int x =  level * 50;
            y += 20;
            if (file.isDirectory()) {
                g.setColor(Color.DARK_GRAY); // Устанавливаем цвет для папок
            } else {
                g.setColor(Color.GRAY); // Устанавливаем цвет для файлов
            }
            if (file.isDirectory())
                tempName = dirStr + file.getName();
            else
                tempName = fileStr + file.getName();
            wight = Math.max((tempName).length() * 12 + x, wight);
            g.drawString(tempName, x, y);
            if (file.isDirectory()) {
                y = printRecursiveTree(file, y, level + 1, g, onlyDirs);
            }
            height = Math.max(height, y);
        }
        return y;
    }

}
