package com.renderg.system.utils;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;

/**
 * 解压zip
 */
public class UnZip  implements UnCompress{
    public static Logger logger = LoggerFactory.getLogger(UnZip.class);
    public static void main (String[]args){
        // unZip("C:\\Users\\86166\\Desktop\\rr\\测试.zip", "C:\\Users\\86166\\Desktop\\zip\\测试", "gbk");
        System.out.println("zip解压完毕");
    }
    private String inputFile;
    private String destDirPath;

    public UnZip(String inputFile, String destDirPath) {
        this.inputFile = inputFile;
        this.destDirPath = destDirPath;
    }

    @Override
    public void unCompress() {
        try {
            unZip(inputFile, destDirPath, "gbk");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static  boolean unZip(String zipFileName, String extPlace, String encode) {
        try {
            return unZipFiles(zipFileName, extPlace, encode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解压zip格式文件到指定位置
     *
     * @param zipFileName
     * @param extPlace
     * @return
     */
    private static boolean unZipFiles(String zipFileName, String extPlace, String encode) {
        try {
            (new File(extPlace)).mkdirs();
            File file = new File(zipFileName);
            ZipFile zipFile = new ZipFile(zipFileName, encode);

            if ((!file.exists()) && (file.length() <= 0)) {
                throw new Exception("要解压文件不存在");
            }
            logger.info("解压的文件为-》》》》》》》》》》》》》》》》》》》》"+zipFileName);
            logger.info("解压的地址为-》》》》》》》》》》》》》》》》》》》》"+extPlace);

            String strPath, gbkPath, strtemp;

            File tempFile = new File(extPlace);
            strPath = tempFile.getAbsolutePath();
            Enumeration<ZipEntry> e = zipFile.getEntries();
            while (e.hasMoreElements()) {
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;
                try {
                    ZipEntry zipEnt = e.nextElement();
                    gbkPath = zipEnt.getName();
                    if (zipEnt.isDirectory()) {
                        strtemp = strPath + File.separator + gbkPath;
                        File dir = new File(strtemp);
                        logger.info("createing:"+dir.getName());
                        dir.mkdirs();
                        continue;
                    } else {
                        //读写文件
                        InputStream is = zipFile.getInputStream(zipEnt);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        gbkPath = zipEnt.getName();
                        logger.info("inflating:"+gbkPath);
                        strtemp = strPath + File.separator + gbkPath;
                        //建目录
                        String strsubdir = gbkPath;

                        for (int i = 0; i < strsubdir.length(); i++) {
                            if (strsubdir.substring(i, i + 1).endsWith("/") || strsubdir.substring(i, i + 1).endsWith("\\")) {
                                String temp = strPath + File.separator + strsubdir.substring(0, i);
                                File subdir = new File(temp);
                                if (!subdir.exists()) {
                                    subdir.mkdir();
                                    logger.info("createing:"+strsubdir);
                                }
                            }
                        }

                        fos = new FileOutputStream(strtemp);
                        bos = new BufferedOutputStream(fos);
                        int c;
                        while ((c = bis.read()) != -1) {
                            bos.write((byte) c);
                        }
//                        bos.close();
//                        fos.close();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }finally {
                    if(bos!=null){
                        bos.close();
                    }
                    if(fos!=null){
                        fos.close();
                    }
                }

            }
            logger.info("解压成功-》》》》》》》》》》》》》》》》》》》》");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
