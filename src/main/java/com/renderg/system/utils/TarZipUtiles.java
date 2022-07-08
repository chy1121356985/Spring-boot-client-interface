package com.renderg.system.utils; /**
 * @author wzj
 * @date 2020/5/10 15:05
 * @version 1.0
 */

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;

/**
 * auther wzj
 * date 2020/5/10 15:05
 */
@Component
public class TarZipUtiles {

    public static Logger logger = LoggerFactory.getLogger(TarZipUtiles.class);

    public static void listFile(File dir, String ParentFileName) {
        File[] files = dir.listFiles();   //列出所有的子文件
        for (File file : files) {
            //如果是文件，则输出文件名字
            if (file.isFile()) {
                //如果是压缩文件就解压
                if (file.getName().endsWith(".zip")) {
                    String absolutePath = file.getAbsolutePath();
                    //System.out.println("absolutePath = " + absolutePath);
                    work(absolutePath, ParentFileName);
                } else if (file.getName().endsWith(".rar")) {
                    String absolutePath = file.getAbsolutePath();
                    unRarFile(absolutePath, ParentFileName);

                }

            } else if (file.isDirectory())//如果是文件夹，则输出文件夹的名字，并递归遍历该文件夹
            {
                //递归遍历
                listFile(file, ParentFileName);
            }
        }
    }

    //解压程序
    public static void work(String inputFileName, String ParentFileName) {
        logger.info("解压文件的地址====:" + inputFileName);
        try {
            File srcFile = new File(ParentFileName);
            //解压到的路径
            File parentFile = srcFile.getParentFile();
            if (srcFile.exists()) {
                Project prj = new Project();
                Expand expand = new Expand();
                expand.setProject(prj);
                expand.setSrc(srcFile);
                expand.setDest(new File(parentFile.toString()));
                expand.setEncoding("GBK");
                expand.execute();
                //删除压缩文件
                srcFile.delete();
            }
        } catch (Exception e) {
            logger.error("出现问题的压缩文件地址:" + inputFileName);
        }
    }

    //解压rar文件
    public static void unRarFile(String srcRarPath, String ParentFileName) {
        logger.info("解压rar文件的地址为:" + srcRarPath);
        if (!srcRarPath.toLowerCase().endsWith(".rar")) {
            System.out.println("非rar文件！");
            return;
        }
        File file = new File(ParentFileName);
        File parentFile = file.getParentFile();
        File dstDiretory = new File(parentFile.toString());
        if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
            dstDiretory.mkdirs();
        }
        Archive a = null;
        try {
            a = new Archive(new File(ParentFileName));
            if (a != null) {
                // a.getMainHeader().print(); // 打印文件信息.
                FileHeader fh = a.nextFileHeader();
                while (fh != null) {
                    // 防止文件名中文乱码问题的处理
                    String fileName = fh.getFileNameW().isEmpty() ? fh
                            .getFileNameString() : fh.getFileNameW();
                    if (fh.isDirectory()) { // 文件夹
                        File fol = new File(parentFile.toString() + File.separator
                                + fileName);
                        fol.mkdirs();
                    } else { // 文件
                        File out = new File(parentFile.toString() + File.separator
                                + fileName.trim());
                        try {
                            if (!out.exists()) {
                                if (!out.getParentFile().exists()) {// 相对路径可能多级，可能需要创建父目录.
                                    out.getParentFile().mkdirs();
                                }
                                out.createNewFile();
                            }
                            FileOutputStream os = new FileOutputStream(out);
                            a.extractFile(fh, os);
                            os.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    fh = a.nextFileHeader();
                }
                a.close();
                file.delete();
            }
        } catch (Exception e) {
            logger.error("解压rar出现问题的数据地址为:" + srcRarPath);
        }
    }

}