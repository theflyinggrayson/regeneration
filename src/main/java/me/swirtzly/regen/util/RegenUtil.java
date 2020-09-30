package me.swirtzly.regen.util;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.Random;

public class RegenUtil {

    public static Random RAND = new Random();

    public static float randFloat(float min, float max) {
        return RAND.nextFloat() * (max - min) + min;
    }

    public static boolean doesHaveInternet() {
        try {
            Socket socket = new Socket("www.google.com", 80);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getJsonFromURL(String URL) {
        java.net.URL url = null;
        try {
            url = new URL(URL);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = bufferedReaderToString(in);
            line = line.replace("<pre>", "");
            line = line.replace("</pre>", "");
            return line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String bufferedReaderToString(BufferedReader e) {
        StringBuilder builder = new StringBuilder();
        String aux = "";

        try {
            while ((aux = e.readLine()) != null) {
                builder.append(aux);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return builder.toString();
    }

    public static byte[] fileToBytes(File file) {
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

}
