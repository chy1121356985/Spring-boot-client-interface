package com.renderg.system.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.renderg.system.constant.FileType;
import com.renderg.system.service.IClientInterFaceService;
import com.renderg.system.utils.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static com.renderg.system.utils.CompressUtils.toZip;


/**
 * @author chy
 * 执行 linux 命令
 */

@Component
public class RunTimeUtils {

    public static Logger logger = LoggerFactory.getLogger(RunTimeUtils.class);

    @Autowired
    private RarUtils rarUtils;


    /**
     * copy
     * 1.判断copy对象是文件还是目录
     * 2.判断当前copy对象在目标位置是否已经存在，如果存在 重命名后进行copy
     * 3。
     *
     * @return
     */

    public static boolean ClientCopy(JSONArray jsonArray, Integer cluster_id) {
        String basePath = Constants.BASEPATH;

        //查询出对应的用户
        Integer identity = null;
        try {
            //获取转换用户id
            JSONObject userMap = Constants.userJson;
            identity = Integer.valueOf(userMap.get("identity").toString());
        } catch (Exception e) {
        }
        //id取整
        int user_Group = Math.round(identity.intValue() / 500) * 500;

        //匹配对应的bean
        IClientInterFaceService clientInterFaceService = SpringUtils.getBean(IClientInterFaceService.class);

        //查询cluster_id对应的目录名
        String clusterName = clientInterFaceService.selectclusterNameById(cluster_id);

        //拼接资产后半部分
        basePath = basePath.replace("clusterName", String.valueOf(clusterName));
        basePath = basePath.replace("user_Group", String.valueOf(user_Group));
        basePath = basePath.replace("user_Id", String.valueOf(identity));


        //资产后缀
        String finalBasePath = basePath;

        jsonArray.forEach(e -> {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(e));
            //拼接完整资产地址
            String from_path = finalBasePath + jsonObject.getString("from_path");
            File from_pathFile = new File(from_path);

            //完整目标地址
            String to_path = finalBasePath + jsonObject.getString("to_path");
            File to_pathFile = new File(to_path);

            try {
                //判断目标是否为目录
                if (from_pathFile.isDirectory()) {
                    FileUtils.copy(from_pathFile, to_pathFile);
                } else {
                    //否则为文件
                    FileUtils.copyFile(from_pathFile, to_pathFile);
                }
                logger.info("复制成功   " + from_path + "-->>>>>>>>" + to_path);
            } catch (Exception Exception) {
                logger.info("复制失败   " + from_path + "-->>>>>>>>" + to_path);
                Exception.printStackTrace();
            }
        });


//        if (jsonArray.size()>0){
//            ArrayList<String> params = new ArrayList<>();
//            params.clear();
//            params.add("cp");
//            params.add("-r");
//            jsonArray.forEach(e->{
//                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(e));
//                String from_path = finalBasePath +jsonObject.getString("from_path") ;
//                params.add(from_path);
//            });
//            jsonArray.forEach(e->{
//                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(e));
//                String from_path = finalBasePath +jsonObject.getString("to_path")+"/";
//                params.add(from_path);
//            });
//            Process process = ProcessBuilder(params);
//            for (Object o : jsonArray) {
//                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(o));
//                File from_path = new File(finalBasePath + jsonObject.getString("from_path"));
//                File to_path = new File(finalBasePath + jsonObject.getString("to_path"));
//                try {
//                    logger.info("from_path>>>>>>>>>>>>>>>>>>>"+finalBasePath + jsonObject.getString("from_path"));
//                    logger.info("to_path>>>>>>>>>>>>>>>>>>>"+finalBasePath + jsonObject.getString("to_path"));
//                    Files.copy(from_path.toPath(),to_path.toPath().resolve(from_path.getName()), StandardCopyOption.REPLACE_EXISTING);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

//        }

        return false;
    }


    /**
     * MV
     *
     * @return
     */

    public static boolean ClientMV(JSONArray jsonArray, Integer cluster_id) {

        String basePath = Constants.BASEPATH;


        //查询出对应的用户
        Integer identity = null;
        try {
            JSONObject userMap = Constants.userJson;
            identity = Integer.valueOf(userMap.get("identity").toString());
        } catch (Exception e) {
        }
        //计算用户id取整
        int user_Group = Math.round(identity.intValue() / 500) * 500;

        //链接Bean
        IClientInterFaceService clientInterFaceService = SpringUtils.getBean(IClientInterFaceService.class);
        //拼接资产路径前缀
        String clusterName = clientInterFaceService.selectclusterNameById(cluster_id);
        basePath = basePath.replace("clusterName", String.valueOf(clusterName));
        basePath = basePath.replace("user_Group", String.valueOf(user_Group));
        basePath = basePath.replace("user_Id", String.valueOf(identity));

        //路径前缀复制
        String finalBasePath = basePath;
//        jsonArray.forEach(e->{
//            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(e));
//            String from_path = finalBasePath +jsonObject.getString("from_path") ;
//            File from_pathFile = new File(from_path);
//            String to_path = finalBasePath +jsonObject.getString("to_path");
//            File to_pathFile = new File(to_path);// 目标地址
//
//            try {
//                if (from_pathFile.isDirectory()){
//                    FileUtils.move(from_pathFile,to_pathFile);
//                }else {
//                    FileUtils.moveFile(from_pathFile,to_pathFile);
//                }
//                logger.info("移动成功   "+ from_path+"-->>>>>>>>"+to_path);
//            } catch (Exception Exception) {
//                logger.info("移动失败   "+ from_path+"-->>>>>>>>"+to_path);
//                Exception.printStackTrace();
//            }
//        });
        if (jsonArray.size() > 0) {
            for (Object o : jsonArray) {
                //资产路径转换为json对象
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(o));
                //拼接完整的原路径地址
                File from_path = new File(finalBasePath + jsonObject.getString("from_path"));
                //拼接完整的新地址
                File to_path = new File(finalBasePath + jsonObject.getString("to_path"));
                try {
                    logger.info("from_path>>>>>>>>>>>>>>>>>>>" + finalBasePath + jsonObject.getString("from_path"));
                    logger.info("to_path>>>>>>>>>>>>>>>>>>>" + finalBasePath + jsonObject.getString("to_path"));
                    //将文件移动或重命名为目标文件。 原路径  目标路径
                    Files.move(from_path.toPath(), to_path.toPath().resolve(from_path.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return false;
    }


    /**
     * 压缩文件
     *
     * @return
     */

    public static boolean Compress(JSONArray jsonArray, Integer cluster_id) {
        String basePath = Constants.BASEPATH;
        //查询出对应的用户
        Integer identity = null;
        try {
            JSONObject userMap = Constants.userJson;
            identity = Integer.valueOf(userMap.get("identity").toString());
        } catch (Exception e) {
        }
        int user_Group = Math.round(identity.intValue() / 500) * 500;

        IClientInterFaceService clientInterFaceService = SpringUtils.getBean(IClientInterFaceService.class);

        //拼接路径前缀
        String clusterName = clientInterFaceService.selectclusterNameById(cluster_id);
        basePath = basePath.replace("clusterName", String.valueOf(clusterName));
        basePath = basePath.replace("user_Group", String.valueOf(user_Group));
        basePath = basePath.replace("user_Id", String.valueOf(identity));
        ArrayList<File> files = new ArrayList<>();
        File to_pathFile = null;
        String from_path = null;

        //final FileOutputStream[] to_pathOPS = new FileOutputStream[1];
        String finalBasePath = basePath;
        logger.info(finalBasePath);


        for (Object e : jsonArray) {
            //把资产路径转换成json对象
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(e));

            //获取原路径
            from_path = finalBasePath + jsonObject.getString("from_path");
            File from_pathFile = new File(from_path);
            //获取目标路径
            String to_path = finalBasePath + jsonObject.getString("to_path");
            System.out.println(to_path);
            //压缩文件 不能压缩文件夹
            String[] split = from_path.split("\\.");
            // 目标地址

            String fileName = from_pathFile.getName();
            if (from_pathFile.isDirectory()) {
                to_pathFile = new File(to_path + "/" + fileName + ".zip");
            } else {
                to_pathFile = new File(to_path + "/" + fileName.substring(0, fileName.lastIndexOf(".")) + ".zip");
            }

            files.add(from_pathFile);

        }
        try {
            FileOutputStream to_pathOPS = new FileOutputStream(to_pathFile);

            System.out.println(files.size());
            if (files.size() == 1) {
                toZip(from_path, to_pathOPS, true);

            } else {
                toZip(files, to_pathOPS);
            }
            to_pathOPS.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }


        return false;
    }

    /**
     * 解压文件
     *
     * @param jsonArray
     * @return
     */

    public boolean CilentZIP(JSONArray jsonArray, Integer cluster_id, String zipId) {

        String basePath = Constants.BASEPATH;
        //查询出对应的用户
        Integer identity = null;

        try {
            JSONObject userMap = Constants.userJson;
            identity = Integer.valueOf(userMap.get("identity").toString());
        } catch (Exception e) {
        }
        int user_Group = Math.round(identity.intValue() / 500) * 500;

        IClientInterFaceService clientInterFaceService = SpringUtils.getBean(IClientInterFaceService.class);
        String clusterName = clientInterFaceService.selectclusterNameById(cluster_id);
        basePath = basePath.replace("clusterName", String.valueOf(clusterName));
        basePath = basePath.replace("user_Group", String.valueOf(user_Group));
        basePath = basePath.replace("user_Id", String.valueOf(identity));
        try {
            String finalBasePath = basePath;
            jsonArray.forEach(e -> {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(e));
                String from_path = finalBasePath + jsonObject.getString("from_path");

                String to_path = finalBasePath + jsonObject.getString("to_path");

                File from_pathFile = new File(from_path);
                //压缩包类型
                FileType fileType = rarUtils.getFileType(from_pathFile);
                logger.info("fileType:" + fileType);

                switch (fileType) {
                    case ZIP:
                        rarUtils.unPackZip(from_pathFile, to_path, zipId);
                        break;
                    case RAR:
                        try {
                            rarUtils.testRar(from_pathFile, to_path, zipId);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        break;
                    case TAR:
                        rarUtils.unPackTar(from_pathFile, to_path, zipId);
                        break;
                    case GZ:
                        rarUtils.unPackTarGz(from_pathFile, to_path, zipId);
                        break;
                    case _7Z:
                        rarUtils.decompress7Z(from_pathFile, to_path, false, zipId);
                        break;
                    case BZ2:
                        rarUtils.decompressBZ2(from_pathFile, to_path, false);
                        break;
                    default:
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    /*public static Process ProcessBuilder(ArrayList params) {
        ProcessBuilder processBuilder = new ProcessBuilder(params);
        //通知进程生成器是否合并标准错误和标准输出。
        processBuilder.redirectErrorStream(true);
        Process process=null;
        try {
//            processBuilder.directory(new File(path));
            process = processBuilder.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            String line = "";
            while ((line = br.readLine()) != null) {

                logger.info(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return process;
    }
*/

    public static void refresh() throws IOException {

        String cmd = "supervisorctl restart  client-interface";
        Process exec = Runtime.getRuntime().exec(cmd);
    }


}
