package org.semul.budny.captcha;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Captcha {
    public final static String CATALOG_PATH = System.getProperty("user.dir") + "/.temp/image/captha/";

    public static void catalogExistCheck() {
        File catalog = new File(CATALOG_PATH);

        if (!catalog.exists()) {
            catalog.mkdirs();
        }
    }

    public static String save(String imageUrl) {
        System.out.print(">>> Download captcha ");

        catalogExistCheck();

        BufferedInputStream inputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            Calendar currentDate = new GregorianCalendar();
            String imageName = currentDate.getTime().toString().replace(' ', '_');

            inputStream = new BufferedInputStream(new URL(imageUrl).openStream());
            fileOutputStream = new FileOutputStream(CATALOG_PATH + imageName + ".jpeg");

            byte[] data = new byte[1024];
            int count;

            while ((count = inputStream.read(data, 0, 1024)) != -1) {
                fileOutputStream.write(data, 0, count);
                fileOutputStream.flush();
            }

            System.out.println("~ Done!");

            return CATALOG_PATH + imageName + ".jpeg";
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void delete(String imagePath) {
        File image = new File(imagePath);

        if (image.exists()) {
            image.delete();
        }
    }
}
