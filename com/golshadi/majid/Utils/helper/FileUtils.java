package com.golshadi.majid.Utils.helper;

import android.util.Log;

import java.io.*;

public class FileUtils {

    public static File create(String folder, String fileName){
        File file = new File(
                address(folder, fileName));
        Log.d("---------------------------------------", "Create file address: " + address(folder, fileName));
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static void forceCreate(String folder, String fileName){
        File dirs = new File(folder);
        dirs.mkdirs();

        File file = new File(
                address(folder, fileName));

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(String folder, String fileName){
        File file = new File(
                address(folder, fileName));
        file.delete();
    }

    public static long size(String folder, String fileName){
        File file = new File(
                address(folder, fileName));
        return file.length();
    }

    public static FileOutputStream getOutputStream(String folder, String fileName){
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(
                    address(folder, fileName));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return fileOut;
    }

    public static FileInputStream getInputStream(String folder, String fileName){
        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(
                    address(folder, fileName));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return fileIn;
    }

    public static String address(String folder, String file){
        return folder+"/"+file;
    }
}
