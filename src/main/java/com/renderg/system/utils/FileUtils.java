package com.renderg.system.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

    public static Logger logger = LoggerFactory.getLogger(FileEventUtils.class);


    /**
     * 复制文件
     * 从源路径到目标文件夹路径，文件名保持一致
     * 如果目标文件夹不存在则自动创建
     * 如果文件已经存在则自动编号-copy n
     *
     * @param srcFile 源文件绝对路径
     * @param dstDir  目标文件夹绝对路径
     * @return 是否成功复制文件
     */
    public static boolean copyFile(File srcFile, File dstDir) {
        //文件不存在或地址是文件夹  不处理
        if (!srcFile.exists() || srcFile.isDirectory()) {
            return false;
        }
        //粘贴地址不存在 创建文件夹
        if (!dstDir.exists()) {
            dstDir.mkdirs();
        }
        //获取文件名
        String oldFileName = srcFile.getName();
        //\w+匹配数字和字母下划线的多个字符
        String regular = "\\.\\w+";
        Pattern suffixPattern = Pattern.compile(regular);
        Matcher matcher = suffixPattern.matcher(oldFileName);

        String nameBody;
        String suffix;
        if (matcher.find()) {
            //找到起始索引的位置 截取文件名.前面的
            nameBody = oldFileName.substring(0, matcher.start());
            //截取文件名
            suffix = oldFileName.substring(matcher.start());
        } else {
            nameBody = oldFileName;
            suffix = "";
        }
        int fileNumber = 0;
        //创建文件路径
        File newFile = new File(dstDir, oldFileName);
        //判断文件是否已存在
        while (newFile.exists()) {
            fileNumber++;
            String newFileName = nameBody + "-copy" + fileNumber + suffix;
            newFile = new File(dstDir, newFileName);
        }
        try {
            //创建输入流
            FileChannel fileIn = new FileInputStream(srcFile).getChannel();
            //创建输出流
            FileChannel fileOut = new FileOutputStream(newFile).getChannel();
            logger.info(fileIn.size() + "______");
            long size = fileIn.size();
            for (long left = size; left > 0; ) {
                left = left - fileIn.transferTo(size - left, left, fileOut);
            }

            fileIn.close();
            fileOut.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 复制文件或文件夹
     * 如果目标文件夹不存在则自动创建
     * 如果文件或文件夹已经存在则自动编号-copy n
     *
     * @param src    源文件或文件夹绝对路径
     * @param dstDir 目标文件夹绝对路径
     * @return 是否成功复制文件或文件夹
     */
    public static boolean copy(File src, File dstDir) {
        //文件不存在直接返回
        if (!src.exists()) {
            return false;
        }
        //文件不存在 进行下一步操作
        if (!dstDir.exists()) {
            //新建文件夹
            dstDir.mkdirs();
        }
        //是否是文件
        if (src.isFile()) {
            //调用复制文件
            copyFile(src, dstDir);
        } else {
            //获取文件夹名
            String oldSrcName = src.getName();
            int srcNumber = 0;
            //拷贝地址+复制文件夹名 (一个新的路径)
            File newSrcDir = new File(dstDir, oldSrcName);
            //判断路径是否是文件
            while (newSrcDir.exists()) {
                srcNumber++;
                String newSrcName = oldSrcName + "-copy" + srcNumber;
                newSrcDir = new File(dstDir, newSrcName);
            }
            //新建复制路径文件夹
            newSrcDir.mkdirs();
            for (File srcSub : src.listFiles()) {
                // 递归复制源文件夹下子文件和文件夹
                copy(srcSub, newSrcDir);
            }
        }
        return true;
    }


    /**
     * 移动(剪切)文件
     *
     * @param srcFile
     * @param dstDir
     * @return
     */
    public static boolean moveFile(File srcFile, File dstDir) {
        if (!srcFile.exists() || srcFile.isDirectory()) {
            return false;
        }
        if (!dstDir.exists()) {
            dstDir.mkdirs();
        }
        String oldFileName = srcFile.getName();
        File dstFile = new File(dstDir, oldFileName);
        if (srcFile.renameTo(dstFile)) {// 直接重命名绝对路径速度更快
            return true;
        } else {// 文件已经存在，需要自动编号复制再删除源文件
            copyFile(srcFile, dstDir);
            srcFile.delete();
        }
        return true;
    }

    /**
     * 移动文件或文件夹
     * 如果目标文件夹不存在则自动创建
     * 如果文件或文件夹已经存在则自动编号-copy n
     *
     * @param src    源文件或文件夹绝对路径
     * @param dstDir 目标文件夹绝对路径
     * @return 是否成功移动文件或文件夹
     */
    public static boolean move(File src, File dstDir) {
        if (!src.exists()) {
            return false;
        }
        if (!dstDir.exists()) {
            dstDir.mkdirs();
        }
        if (src.isFile()) {// 文件
            moveFile(src, dstDir);
        } else {// 文件夹
            String oldSrcName = src.getName();
            int srcNumber = 0;
            File newSrcDir = new File(dstDir, oldSrcName);
            while (newSrcDir.exists()) {
                srcNumber++;
                String newSrcName = oldSrcName + "-copy" + srcNumber;
                newSrcDir = new File(dstDir, newSrcName);
            }
            newSrcDir.mkdirs();
            for (File srcSub : src.listFiles()) {
                move(srcSub, newSrcDir);// 递归移动源文件夹下子文件和文件夹
            }
            src.delete();
        }
        return true;
    }

    /**
     * 删除文件或文件夹
     *
     * @param src 源文件或文件夹绝对路径
     * @return 是否成功删除文件或文件夹
     */
    public static boolean delete(File src) {
        if (!src.exists()) {
            return false;
        }
        if (src.isFile()) {
            src.delete();
        } else {
            for (File srcSub : src.listFiles()) {
                delete(srcSub);// 递归删除源文件夹下子文件和文件夹
            }
            src.delete();
        }
        return true;
    }


}
