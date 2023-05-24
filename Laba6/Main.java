package com.project;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Main {
    public static final int SIZE_32 = 32;
    public static final int SIZE_64 = 64;
    public static final int SIZE_128 = 128;
    public static final int SIZE_256 = 256;
    public static final int SIZE_512 = 512;

    public static void main(String[] args) throws IOException, TimeoutException {
        ArrayList<Map<String, Object>> imageList = new ArrayList<>();
        Consumer consumer = new Consumer();
        Scanner scanner = new Scanner(System.in);
        Image[] resize = new Image[5];
        String path = "";

        while(!path.equals("0")) {
            System.out.println("Enter path to image");
            path = scanner.next();

            if(path.equals("0"))
                break;
            try {
                BufferedImage bimg = ImageIO.read(new File(path));

                for (int size : new int[]{SIZE_32, SIZE_64, SIZE_128, SIZE_256, SIZE_512}) {
                    Image resizeImg = bimg.getScaledInstance(size, size, Image.SCALE_SMOOTH);


                    String[] parts = path.split("\\.");
                    String fileName = parts[0];
                    String extension = parts[1];

                    String outputFileName = fileName + "_" + size + "." + extension;

                    String outputFilePath = parts[0] + "_" + size + ".png";
                    File outputFile = new File(outputFilePath);
                    ImageIO.write(toBufferedImage(resizeImg), "png", outputFile);

                    Map<String, Object> imageMap = new HashMap<>();
                    imageMap.put("path", outputFileName);
                    imageMap.put("size", size);
                    imageList.add(imageMap);
                }
            } catch (Exception e) {
            }
        }

        try {
            Sender sender = new Sender();
            for (Map<String, Object> imageMap : imageList) {

                sender.send((String) imageMap.get("path"), (int) imageMap.get("size"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        consumer.getConsumer();
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }
}

