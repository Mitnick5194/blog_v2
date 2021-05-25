package com.baihua.utils;

import org.springframework.util.StringUtils;

import java.io.File;

public class FileUtils {


    private FileUtils() {

    }

    /**
     * 文件夹不存在则创建
     */
    public static void createFolderIfNotExist(String path) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        createFolder(file);


    }

    static private void createFolder(File file) {
        if (file.exists()) {
            return;
        }
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            createFolder(parentFile);
        }
        file.mkdir();
    }

    public static void main(String[] args) {
        String path = "E:\\test\\test\\1\\2\\3\\4";
        createFolderIfNotExist(path);
    }
}
