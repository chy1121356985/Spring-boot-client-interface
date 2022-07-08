package com.renderg.system.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.renderg.system.event.ClientEvent;
import com.renderg.system.service.IClientInterFaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileEventUtils {


    public static Logger logger = LoggerFactory.getLogger(FileEventUtils.class);
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private IClientInterFaceService clientInterFaceService;

    @Autowired
    private RedisUtil redisUtil;


    //路径+动作
    public AjaxByFilesResult fileEvent(JSONObject paths, String stauts) {


        //获取token
        TokenUtils.token();

        //token为空直接返回异常
        if (Constants.userJson.isEmpty()) {
            return AjaxByFilesResult.error(403, "token异常");
        }

        //调用静态资产路径前缀
        String basePath = Constants.BASEPATH;

        //获前端数据进行解析
        JSONArray paths_Arr = paths.getJSONArray("paths");
        //查询出对应的用户
        Integer identity = null;
        try {
            //获取用户id
            JSONObject userMap = Constants.userJson;
            //转换为int类型
            identity = Integer.valueOf(userMap.get("identity").toString());
        } catch (Exception e) {
            return AjaxByFilesResult.error("token验证失败");
        }
        //计算路径所在的目录
        int user_Group = Math.round(identity.intValue() / 500) * 500;
        Integer cluster_id = null;
        //获取cluster_id值（传输线路）
        cluster_id = paths.getInteger("cluster_id");
        //根据传输线路查询数据库对应的文件夹名
        String clusterName = clientInterFaceService.selectclusterNameById(cluster_id);
        //更新资产路径前缀
        basePath = basePath.replace("clusterName", String.valueOf(clusterName));
        basePath = basePath.replace("user_Group", String.valueOf(user_Group));
        basePath = basePath.replace("user_Id", String.valueOf(identity));

        String finalBasePath = basePath;
        //打印路径前缀
        logger.info(basePath);
        final String[] errmsg = {""};
        final int[] err_int = {0};
        final boolean[] err = {false};
        final boolean[] errByreturn = {false};
        final String[] zipId = {null};
        JSONArray path_Arr = new JSONArray();
        JSONArray errPaths = new JSONArray();

        paths_Arr.forEach(e -> {
            err[0] = false;
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(e));

            //资产原路径
            String from_path = finalBasePath + jsonObject.getString("from_path");
            //资产路径后缀
            String raw_from_path = jsonObject.getString("from_path");
            //资产要拷贝的地址后缀
            String to_path = jsonObject.getString("to_path");
            //把路径对象添加到json中
            path_Arr.add(jsonObject);
            //目标路径
            File file = new File(from_path + "");
            //完整路径
//            jsonObject.put("all_from_path",from_path);
//            jsonObject.put("all_to_path",finalBasePath+to_path);
            //检测原文件或文件夹是否存在
            if (!file.exists()) {
                err[0] = true;
                errByreturn[0] = true;
                JSONObject jsonError = new JSONObject();
                jsonError.put("code", "404");
                jsonError.put("reason", "Not Found");
                jsonError.put("user_message", raw_from_path + "   原文件不存在");
                jsonObject.put("error", jsonError);
            }
            //判断文件地址时候为目录
            if (file.isDirectory()) {
                //判断是否为复制粘贴
                if (stauts.equals("copy")) {
                    //资产原地后缀
                    jsonObject.put("from_path", raw_from_path + "/");
                }
                //考虑资产是否有与目标地址一致
                if (raw_from_path.equals(to_path)) {
                    if (!err[0]) {
                        err[0] = true; // 考虑目标地址已存在
                        errByreturn[0] = true;
                        JSONObject jsonError = new JSONObject();
                        jsonError.put("code", "500");
                        jsonError.put("reason", "already exist ");
                        jsonError.put("user_message", raw_from_path + "   already exist");
                        jsonObject.put("error", jsonError);
                    }
                }
            }
            //判断粘贴地址下时候有相同的文件夹或文件
            file = new File(finalBasePath + to_path + "/" + file.getName() + "");
            logger.info(stauts);
            if (stauts.equals("UNZIP")) {
                if (file.isDirectory()) {
                    //考虑文件是否存在
                    if (!file.getParentFile().exists()) {
                        if (!err[0]) {
                            err[0] = true; // 考虑目标地址不存在
                            errByreturn[0] = true; // 考虑目标地址不存在
                            JSONObject jsonError = new JSONObject();
                            jsonError.put("code", "404");
                            jsonError.put("reason", "Not Found");
                            jsonError.put("user_message", to_path + "   No such file or directory");
                            jsonObject.put("error", jsonError);
                        }
                    } else if (file.exists()) {
                        if (!err[0]) {
                            err[0] = true; // 考虑目标地址已存在
                            errByreturn[0] = true;
                            JSONObject jsonError = new JSONObject();
                            jsonError.put("code", "500");
                            jsonError.put("reason", "already exist ");
                            if (!to_path.equals("/")) {
                                jsonError.put("user_message", to_path + "/" + file.getName() + "   already exist");
                            } else {
                                jsonError.put("user_message", to_path + file.getName() + "   already exist");
                            }
                            jsonObject.put("error", jsonError);
                        }
                    }

                }
            } else {
                //考虑文件是否存在
                if (!file.getParentFile().exists()) {
                    if (!err[0]) {
                        err[0] = true; // 考虑目标地址不存在
                        errByreturn[0] = true; // 考虑目标地址不存在
                        JSONObject jsonError = new JSONObject();
                        jsonError.put("code", "404");
                        jsonError.put("reason", "Not Found");
                        jsonError.put("user_message", to_path + "   No such file or directory");
                        jsonObject.put("error", jsonError);
                    }
                } else if (file.exists()) {
                    if (!err[0]) {
                        err[0] = true; // 考虑目标地址已存在
                        errByreturn[0] = true;
                        JSONObject jsonError = new JSONObject();
                        jsonError.put("code", "500");
                        jsonError.put("reason", "目标地址文件已存在 ");
                        if (!to_path.equals("/")) {
                            jsonError.put("user_message", to_path + "/" + file.getName() + "   目标地址文件已存在");
                        } else {
                            jsonError.put("user_message", to_path + file.getName() + "   目标地址文件已存在");
                        }
                        jsonObject.put("error", jsonError);
                    }
                }
            }

            //以上异常情况都不存在的情况下，保存原地址和粘贴地址
//            if (!err[0]) {
//                jsonObject.put("from_path_all", from_path);//- /mnt/wlcb/assets/500/616/input/c/xxx/xxx
//                jsonObject.put("to_path_all", finalBasePath + to_path);//- /mnt/wlcb/assets/500/616/input/c/xxx/xxx
//            }


            if ("UNZIP".equals(stauts) || "Compress".equals(stauts)) {
                if (jsonObject.getString("error") == null) {
                    //压缩包动作随机生成zipId
                    zipId[0] = RandomUtil.getFourBitRandom();
                    redisUtil.set("zip:" + zipId[0], jsonObject, 7200L);
                    jsonObject.put("all_from_path", from_path);
                    jsonObject.put("all_to_path", finalBasePath + to_path);
                    jsonObject.put("zipId", zipId[0]);
                }
            }

            //保存数据
            errPaths.add(jsonObject);
        });
        JSONArray returnPaths = new JSONArray();
        JSONArray succeedPaths = new JSONArray();
        errPaths.forEach(e -> {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(e));
//            jsonObject.put("from_path_all", "");
//            jsonObject.put("to_path_all", "");

            //添加正确存储数据
            if (jsonObject.getJSONObject("error") == null) {
                succeedPaths.add(jsonObject);
            }
            //记录每一条操作数据
            returnPaths.add(jsonObject);
        });


        //new 动作+传输线路
        ClientEvent clientEvent = new ClientEvent(this, succeedPaths, stauts, cluster_id, zipId[0]);
        try {
            applicationContext.publishEvent(clientEvent);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxByFilesResult.error();
        }
        if (errByreturn[0]) {
            //异常情况
            return AjaxByFilesResult.error("拷贝失败", returnPaths);
        }
        return AjaxByFilesResult.success(returnPaths);
    }
}
