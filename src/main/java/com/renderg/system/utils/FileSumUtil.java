package com.renderg.system.utils;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author chy
 * 获取目录下文件和文件夹数量
 * @date 2022年6月24日10:08:29
 */
@Component
public class FileSumUtil {

    //文件数
    public int fileNum;
    //文件夹数量
    public int directoryNum;

    public int getFileNum() {
        return fileNum;
    }

    public void setFileNum(int fileNum) {
        this.fileNum = fileNum;
    }

    public int getDirectoryNum() {
        return directoryNum;
    }

    public void setDirectoryNum(int directoryNum) {
        this.directoryNum = directoryNum;
    }

    /**
     * 获取文件下包含的文件和目录
     *
     * @param file 文件路径
     */
    public int show(File file) {
        int sum = 0;
        File[] files = file.listFiles();
        if (null != files) {
            for (int i = 0; i < files.length; i++) {
                String result = files[i].isFile() ? "一个文件" : "一个目录";
                if ("一个目录".equals(result)) {
                    directoryNum++;
                    show(files[i]);
                } else {
                    fileNum++;
                }
            }
        }
        sum = fileNum + directoryNum;
        return sum;
    }


    /**
     * 判断字符编码
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String checkEncoding(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        byte[] b = new byte[3];
        try {
            int i = in.read(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
        if (b[0] == -1 && b[1] == -2) {
            return "UTF-16";
        } else if (b[0] == -2 && b[1] == -1) {
            return "Unicode";
        } else if (b[0] == -17 && b[1] == -69 && b[2] == -65) {
            return "UTF-8";
        } else {
            return "GBK";
        }
    }
}
