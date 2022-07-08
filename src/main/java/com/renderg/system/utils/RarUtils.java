package com.renderg.system.utils;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.renderg.system.constant.FileType;
import com.renderg.system.event.ExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;


@Component
public class RarUtils {

    public Logger logger = LoggerFactory.getLogger(FileEventUtils.class);

    @Autowired
    private RedisUtil redisUtil;

    public List<String> unPackZip(File zipFile, String unpackFolder, String zipId) {
        List<String> fileNames = new ArrayList<>();
        String fileEncoding = null;
        ZipFile zipFiles = null;
        try {
            fileEncoding = checkEncoding(zipFile);
            //文件数量
            String file = zipFile.toString();
            zipFiles = new ZipFile(file, Charset.forName(fileEncoding));
            redisUtil.set("zip:fileSum:" + zipId, zipFiles.size(), 7200L);

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String fileEncoding1 = (fileEncoding != null) ? fileEncoding : "UTF-8";
        try (ZipArchiveInputStream zais = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile), 4096), fileEncoding1)) {
            ZipArchiveEntry entry = null;
            String substring = zipFile.getName().substring(0, zipFile.getName().lastIndexOf("."));
            File tmpFileTop = new File(unpackFolder, substring);
            tmpFileTop.mkdirs();
            while ((entry = zais.getNextZipEntry()) != null) {
                //遍历压缩包，如果进行有选择解压，可在此处进行过滤
                File tmpFile = new File(unpackFolder + "/" + substring, entry.getName());
                if (entry.isDirectory()) {
                    tmpFile.mkdirs();
                } else {
                    fileNames.add(entry.getName());
                    File file = new File(tmpFile.getAbsolutePath());
                    if (!file.exists()) {
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                    }
                    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(tmpFile), 4096)) {
                        IOUtils.copy(zais, os);
                        os.flush();
                        os.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileNames;
    }

    //判断字符编码
    public String checkEncoding(File file) throws IOException {
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


    public void testRar(File rarFileName, String unpackFolder, String zipId) throws IOException {

        String from_fileName = null;
        String substring = null;

        RandomAccessFile randomAccessFile = null;
        IInArchive iInArchive = null;

        from_fileName = rarFileName.getName();
        substring = from_fileName.substring(0, from_fileName.lastIndexOf("."));

        randomAccessFile = new RandomAccessFile(rarFileName, "r");
        iInArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
        int[] in = new int[iInArchive.getNumberOfItems()];
        for (int i = 0; i < in.length; i++) {
            in[i] = i;
        }
        logger.info(in.length+"length");
        redisUtil.set("zip:fileSum:" + zipId, in.length - 1, 7200L);
        iInArchive.extract(in, false, new ExtractCallback(iInArchive, "366", unpackFolder+"/"+substring+"/"));
        randomAccessFile.close();


    }


//    public List<String> unPackRar(File rarFileName, String unpackFolder, String zipId) {
//        Integer fileSum = 0;
//        List<String> fileNames = new ArrayList<>();
//        try (Archive a = new Archive(new FileInputStream(rarFileName))) {
//            FileHeader fh = a.nextFileHeader();
//            String zipName = rarFileName.getName().substring(0, rarFileName.getName().lastIndexOf("."));
//            new File(unpackFolder, zipName).mkdirs();
//            while (fh != null) {
//                //遍历压缩包，如果进行有选择解压，可在此处进行过滤
//                File file;
//                if (existZH(fh.getFileNameW())) {
//                    //周一  根据父类文件夹生成子文件夹
//                    file = new File(unpackFolder + "/" + zipName + File.separator + fh.getFileNameW());
//                } else {
//                    file = new File(unpackFolder + "/" + zipName + File.separator + fh.getFileNameString());
//                }
//                if (fh.isDirectory()) {
//                    file.mkdirs();
//                } else {
//                    String absolutePath = file.getAbsolutePath();
//                    if (absolutePath.contains("\\")) {
//                        file = new File(absolutePath.replace("\\", File.separator));
//                    }
//                    if (!file.exists()) {
//                        // 相对路径可能多级，可能需要创建父目录.
//                        if (!file.getParentFile().exists()) {
//                            file.getParentFile().mkdirs();
//                        }
//                        if (!file.createNewFile()) {
//                        }
//                    }
//                    try (FileOutputStream os = new FileOutputStream(file)) {
//                        a.extractFile(fh, os);
//                    }
//                    fileNames.add(file.getName());
//                }
//                fileSum++;
//                fh = a.nextFileHeader();
//            }
//            redisUtil.set("zip:fileSum:" + zipId, fileSum - 1, 7200L);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return fileNames;
//    }

    public boolean existZH(String str) {
        //是否存在中文的正则表达式
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    public List<String> unPackTar(File file, String unpackFolder, String zipId) {
        List<String> fileNames = new ArrayList<>();
        Integer fileSum = 0;
        try (FileInputStream inputStream = new FileInputStream(file);
             TarArchiveInputStream iStream = new TarArchiveInputStream(inputStream);
             BufferedInputStream bis = new BufferedInputStream(iStream);) {
            TarArchiveEntry entry = iStream.getNextTarEntry();
            String zipName = file.getName().substring(0, file.getName().lastIndexOf("."));
            while (entry != null) {
                //遍历压缩包，如果进行有选择解压，可在此处进行过滤
                File unpackFolderFile = new File(unpackFolder + "/" + zipName);
                File tmpFile = new File(unpackFolder + "/" + zipName, entry.getName());
                if (!unpackFolderFile.exists()) {
                    unpackFolderFile.mkdirs();
                }
                if (entry.isDirectory()) {
                    tmpFile.mkdirs();
                } else {
                    fileNames.add(entry.getName());
                    try (OutputStream out = new FileOutputStream(tmpFile)) {
                        int length;
                        byte[] b = new byte[2048];
                        while ((length = bis.read(b)) != -1) {
                            out.write(b, 0, length);
                        }
                        out.flush();
                    }
                }
                fileSum++;
                entry = (TarArchiveEntry) iStream.getNextEntry();
            }
            redisUtil.set("zip:fileSum:" + zipId, fileSum - 1, 7200L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    public List<String> unPackTarGz(File file, final String unpackFolder, String zipId) {
        List<String> fileNames = new ArrayList<>();
        String from_fileName = file.getName();
        int i = from_fileName.lastIndexOf(".", from_fileName.lastIndexOf(".") - 1);
        String zipName = from_fileName.substring(0, i);
        Integer fileSum = 0;

        try (FileInputStream fileInputStream = new FileInputStream(file);
             GZIPInputStream iStream = new GZIPInputStream(fileInputStream);
             ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream("tar", iStream);
             BufferedInputStream bis = new BufferedInputStream(in)) {
            TarArchiveEntry entry;
            while ((entry = (TarArchiveEntry) in.getNextEntry()) != null) {
                //遍历压缩包，如果进行有选择解压，可在此处进行过滤
                File unpackFolderFile = new File(unpackFolder + "/" + zipName);
                if (!unpackFolderFile.exists()) {
                    unpackFolderFile.mkdirs();
                }
                if (entry.getName().contains(".tar")) {
                    unPackTar(file, unpackFolder, zipId);
                    break;
                }
                File tmpFile = new File(unpackFolder + "/" + zipName, entry.getName());

                if (!unpackFolderFile.exists()) {
                    unpackFolderFile.mkdirs();
                }
                if (entry.isDirectory()) {
                    tmpFile.mkdirs();
                } else {
                    fileNames.add(entry.getName());
                    try (OutputStream out = new FileOutputStream(tmpFile)) {
                        int length;
                        byte[] b = new byte[2048];
                        while ((length = bis.read(b)) != -1) {
                            out.write(b, 0, length);
                        }
                        out.flush();
                    }
                }
                fileSum++;
            }
            redisUtil.set("zip:fileSum:" + zipId, fileSum - 1, 7200L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileNames;
    }


    /**
     * 获取文件真实类型
     *
     * @param file 要获取类型的文件。
     * @return 文件类型枚举。
     */
    public FileType getFileType(File file) {
        FileInputStream inputStream = null;
        String from_fileName = null;
        String zipType = null;

        try {
            inputStream = new FileInputStream(file);
            byte[] head = new byte[4];
            if (-1 == inputStream.read(head)) {
                return FileType.UNKNOWN;
            }
            int headHex = 0;
            for (byte b : head) {
                headHex <<= 8;
                headHex |= b;
            }
            from_fileName = file.getName();
            zipType = from_fileName.substring(from_fileName.lastIndexOf(".") + 1);
            logger.info(zipType + "zipType");
            if ("tar".equals(zipType)) {
                headHex = 0x776f7264;
            }
            logger.info(file + "--headHex--" + headHex);
            switch (headHex) {
                case 0x504B0304:
                    return FileType.ZIP;
                case 0x776f7264:
//                case 1667791214:
                    return FileType.TAR;
                case -0x51:
                    return FileType._7Z;
                case 0x425a6839:
                    return FileType.BZ2;
                case -0x74f7f8:
                    return FileType.GZ;
                case 0x52617221:
                    return FileType.RAR;
                default:
                    return FileType.UNKNOWN;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return FileType.UNKNOWN;
    }


    /**
     * 构建目录
     *
     * @param outputDir 输出目录
     * @param subDir    子目录
     */
    public void createDirectory(String outputDir, String subDir) {
        File file = new File(outputDir);
        //子目录不为空
        if (!(subDir == null || subDir.trim().equals(""))) {
            file = new File(outputDir + File.separator + subDir);
        }
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.mkdirs();
        }
    }


    /**
     * 解压缩bz2文件
     *
     * @param file       压缩包文件
     * @param targetPath 目标文件夹
     * @param delete     解压后是否删除原压缩包文件
     */
    public void decompressBZ2(File file, String targetPath, boolean delete) {
        FileInputStream fis = null;
        OutputStream fos = null;
        BZip2CompressorInputStream bis = null;
        String suffix = ".bz2";
        try {
            fis = new FileInputStream(file);
            bis = new BZip2CompressorInputStream(fis);
            // 创建输出目录
            createDirectory(targetPath, null);
            File tempFile = new File(targetPath + File.separator + file.getName().replace(suffix, ""));
            fos = new FileOutputStream(tempFile);

            int count;
            byte data[] = new byte[2048];
            while ((count = bis.read(data)) != -1) {
                fos.write(data, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 解压缩tar.bz2文件
     *
     * @param file       压缩包文件
     * @param targetPath 目标文件夹
     * @param delete     解压后是否删除原压缩包文件
     */
    public void decompressTarBz2(File file, String targetPath, boolean delete) {
        FileInputStream fis = null;
        OutputStream fos = null;
        BZip2CompressorInputStream bis = null;
        TarInputStream tis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BZip2CompressorInputStream(fis);
            tis = new TarInputStream(bis, 1024 * 2);
            // 创建输出目录
            createDirectory(targetPath, null);
            TarEntry entry;
            while ((entry = tis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    createDirectory(targetPath, entry.getName()); // 创建子目录
                } else {
                    fos = new FileOutputStream(new File(targetPath + File.separator + entry.getName()));
                    int count;
                    byte data[] = new byte[2048];
                    while ((count = tis.read(data)) != -1) {
                        fos.write(data, 0, count);
                    }
                    fos.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (tis != null) {
                    tis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 解压缩tar.gz文件
     *
     * @param file       压缩包文件
     * @param targetPath 目标文件夹
     * @param delete     解压后是否删除原压缩包文件
     */
    public void decompressTarGz(File file, String targetPath, boolean delete) {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        GZIPInputStream gzipIn = null;
        TarInputStream tarIn = null;
        OutputStream out = null;
        try {
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            gzipIn = new GZIPInputStream(bufferedInputStream);
            tarIn = new TarInputStream(gzipIn, 1024 * 2);

            // 创建输出目录
            createDirectory(targetPath, null);

            TarEntry entry = null;
            while ((entry = tarIn.getNextEntry()) != null) {
                if (entry.isDirectory()) { // 是目录
                    createDirectory(targetPath, entry.getName()); // 创建子目录
                } else { // 是文件
                    File tempFIle = new File(targetPath + File.separator + entry.getName());
                    createDirectory(tempFIle.getParent() + File.separator, null);
                    out = new FileOutputStream(tempFIle);
                    int len = 0;
                    byte[] b = new byte[2048];

                    while ((len = tarIn.read(b)) != -1) {
                        out.write(b, 0, len);
                    }
                    out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (tarIn != null) {
                    tarIn.close();
                }
                if (gzipIn != null) {
                    gzipIn.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压缩gz文件
     *
     * @param file       压缩包文件
     * @param targetPath 目标文件夹
     * @param delete     解压后是否删除原压缩包文件
     */
    public void decompressGz(File file, String targetPath, boolean delete) {
        FileInputStream fileInputStream = null;
        GZIPInputStream gzipIn = null;
        OutputStream out = null;
        String suffix = ".gz";
        try {
            fileInputStream = new FileInputStream(file);
            gzipIn = new GZIPInputStream(fileInputStream);
            // 创建输出目录
            createDirectory(targetPath, null);

            File tempFile = new File(targetPath + File.separator + file.getName().replace(suffix, ""));
            out = new FileOutputStream(tempFile);
            int count;
            byte data[] = new byte[2048];
            while ((count = gzipIn.read(data)) != -1) {
                out.write(data, 0, count);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (gzipIn != null) {
                    gzipIn.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 解压缩7z文件
     *
     * @param file       压缩包文件
     * @param targetPath 目标文件夹
     * @param delete     解压后是否删除原压缩包文件
     */
    public void decompress7Z(File file, String targetPath, boolean delete, String zipId) {
        SevenZFile sevenZFile = null;
        OutputStream outputStream = null;
        //压缩包前缀
        String substring = null;
        Integer fileSum = 0;
        try {
            sevenZFile = new SevenZFile(file);
            // 创建输出目录
            substring = file.getName().substring(0, file.getName().lastIndexOf("."));
            createDirectory(targetPath, substring);
            SevenZArchiveEntry entry;

            while ((entry = sevenZFile.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    // 创建子目录
                    createDirectory(targetPath + "/" + substring, entry.getName());
                } else {
                    outputStream = new FileOutputStream(new File(targetPath + "/" + substring + File.separator + entry.getName()));
                    int len = 0;
                    byte[] b = new byte[2048];
                    while ((len = sevenZFile.read(b)) != -1) {
                        outputStream.write(b, 0, len);
                    }
                    outputStream.flush();
                    outputStream.close();
                }

                fileSum++;
            }
            redisUtil.set("zip:fileSum:" + zipId, fileSum - 1, 7200L);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (sevenZFile != null) {
                    sevenZFile.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
