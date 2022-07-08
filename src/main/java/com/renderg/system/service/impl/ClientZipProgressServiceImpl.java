package com.renderg.system.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.renderg.system.constant.FileType;
import com.renderg.system.service.ClientZipProgressService;
import com.renderg.system.utils.FileEventUtils;
import com.renderg.system.utils.FileSumUtil;
import com.renderg.system.utils.RarUtils;
import com.renderg.system.utils.RedisUtil;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;


/**
 * @author chy
 */
@Service
public class ClientZipProgressServiceImpl implements ClientZipProgressService {

    public static Logger logger = LoggerFactory.getLogger(FileEventUtils.class);


    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FileSumUtil fileSumUtil;

    @Autowired
    private RarUtils rarUtils;

    @Override
    public Integer ZipProgress(String zipId) throws IOException {
        //redis中获取文件路径
        Object paths = redisUtil.get("zip:" + zipId);
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(paths));
        //原路径
        String from_path = jsonObject.getString("all_from_path");
        //目标路径
        String to_path = jsonObject.getString("all_to_path");

        File from_path_file = new File(from_path);
        //判断压缩包类型
        FileType from_fileType = rarUtils.getFileType(from_path_file);
        //压缩包文件数
        Integer from_file_size = 0;
        //已解压文件数
        Integer to_file_size = 0;

        String from_fileName = null;
        //文件名
        String substring = null;
        //压缩到路径
        File to_file_path = null;
        //条目数
        Object fileSum = null;

        Integer per = 0;
        fileSum = redisUtil.get("zip:fileSum:" + zipId);
        logger.info(fileSum + "fileSum");

        if (fileSum == null) {
            switch (from_fileType) {
                //ZIP
                case ZIP:
                    String encode = FileSumUtil.checkEncoding(from_path_file);
                    ZipFile zipFile = new ZipFile(from_path, Charset.forName(encode));
                    redisUtil.set("zip:fileSum:" + zipId, zipFile.size() - 1, 7200L);
                    break;
                //ARA
//                case RAR:
//                    try (Archive archive = new Archive(new FileInputStream(from_path))) {
//                        if (null != archive) {
//                            FileHeader fileHeader = archive.nextFileHeader();
//                            while (null != fileHeader) {
//                                from_file_size++;
//                                fileHeader = archive.nextFileHeader();
//                            }
//                            redisUtil.set("zip:fileSum:" + zipId, from_file_size - 1, 7200L);
//                        }
//                    } catch (Exception e) {
//                        logger.info(e + "获取压缩包条目数异常");
//                    }
//                    break;
                //TAR
                case TAR:
                    try (FileInputStream inputStream = new FileInputStream(from_path);
                         TarArchiveInputStream iStream = new TarArchiveInputStream(inputStream);
                         BufferedInputStream bis = new BufferedInputStream(iStream)) {
                        TarArchiveEntry entry;
                        while ((entry = (TarArchiveEntry) iStream.getNextEntry()) != null) {
                            from_file_size++;
                            System.out.println("1");
                        }
                        redisUtil.set("zip:fileSum:" + zipId, from_file_size - 1, 7200L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                //TAR.GZ
                case GZ:
                    try (FileInputStream fileInputStream = new FileInputStream(from_path);
                         GZIPInputStream iStream = new GZIPInputStream(fileInputStream);
                         ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream("tar", iStream);
                         BufferedInputStream bis = new BufferedInputStream(in)) {
                        TarArchiveEntry entry;
                        while ((entry = (TarArchiveEntry) in.getNextEntry()) != null) {
                            from_file_size++;
                        }
                        redisUtil.set("zip:fileSum:" + zipId, from_file_size - 1, 7200L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                //7Z
                case _7Z:
                    SevenZFile sevenZFile = null;
                    try {
                        sevenZFile = new SevenZFile(new File(from_path));
                        while (sevenZFile.getNextEntry() != null) {
                            from_file_size++;
                        }
                        redisUtil.set("zip:fileSum:" + zipId, from_file_size - 1, 7200L);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    break;
                default:
                    return null;
            }

        } else {
            from_file_size = Integer.parseInt(fileSum.toString());
            logger.info(from_file_size + "from_file_size");
        }


        //GZ压缩包单独处理文件名
        if ("GZ".equals(from_fileType + "")) {
            from_fileName = from_path_file.getName();
            int i = from_fileName.lastIndexOf(".", from_fileName.lastIndexOf(".") - 1);
            substring = from_fileName.substring(0, i);
            logger.info(substring + "");
            to_file_path = new File(to_path + "/" + substring);
        } else {
            from_fileName = from_path_file.getName();
            substring = from_fileName.substring(0, from_fileName.lastIndexOf("."));
            to_file_path = new File(to_path + "/" + substring);
        }

        logger.info(to_file_path + "to_file_path");
        to_file_size = fileSumUtil.show(to_file_path);
        logger.info(to_file_size + "to_file_size");
        fileSumUtil.setFileNum(0);
        fileSumUtil.setDirectoryNum(0);
        if (to_file_size == 0) {
            logger.info("from_path" + "---解压失败");
            return 0;
        }
        if (from_file_size.equals(to_file_size)) {
            logger.info(from_path + "---解压成功");
            return 100;
        } else {
            // 参数一：被除数;参数2：除数;参数三:小数点后保留的位数，舍入模式为四舍五入
            per = NumberUtil.div(to_file_size + "", from_file_size + "", 2).multiply(BigDecimal.valueOf(100)).intValue();
        }
        logger.info("解压进度---:"+per);
        return per;
    }
}
