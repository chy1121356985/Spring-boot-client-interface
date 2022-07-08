package com.renderg.system.controller;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.renderg.system.domain.vo.ImageVo;
import com.renderg.system.utils.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientImageController {

    private static final Logger log = LoggerFactory.getLogger(ClientImageController.class);

//    @Autowired
//    private HttpServletResponse response;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/job/task/thumb/image",method = RequestMethod.GET,produces = {MediaType.IMAGE_JPEG_VALUE,MediaType.IMAGE_PNG_VALUE})
    public byte[] image(ImageVo imageVo,HttpServletResponse resp) throws IOException {
        TokenUtils.token();
        if (Constants.userJson.isEmpty()){
            resp.setStatus(403);
            return null;
        }
        String thump_Path = "/mnt/thumb/%s/%s/%s/%s";

        String base64Str ="data:/%s;base64,";
        Integer identity = null;
        try {
            JSONObject userMap = Constants.userJson;
            identity = Integer.valueOf(userMap.get("identity").toString());
        }catch (Exception e){
            resp.setStatus(403);
            return null;
//            return AjaxResult.error("token验证失败");
        }
        //查看缩略图 （考虑子账号）
        //先判断子账号id是否为空
        String child_user_id = imageVo.getChild_user_id();
        String job_id = imageVo.getJob_id();
        String path = imageVo.getPath();
        String task_id = imageVo.getTask_id();
        if (StringUtils.isNotEmpty(child_user_id)){
            Object o = redisTemplate.opsForValue().get("user:"+String.valueOf(identity));
            List<JSONObject> userGroup =new ArrayList<>();
            if (o!=null){
                Object o1 = redisTemplate.opsForValue().get("user:group:"+String.valueOf(o));
                userGroup = JSON.parseArray(String.valueOf(o1), JSONObject.class);
            }
            final boolean[] verify = {false};
            userGroup.forEach(e->{
                if (e.getInteger("id").equals(Integer.valueOf(child_user_id))){
                    verify[0] =true;
                    return;
                }
            });
            if (userGroup.size()>0&& verify[0]){
                //说明是登陆账号的子账号
                identity=Integer.valueOf(child_user_id);
                int ceil = (int) Math.ceil((identity/500)*500);
                thump_Path=String.format(thump_Path,ceil,identity,job_id,path );
            }else {
                resp.setStatus(400);
                return null;
//                return AjaxResult.error("请确认子账号id是否正确");
            }
        }else {
            int ceil = (int) Math.ceil((identity/500)*500);
            thump_Path=String.format(thump_Path,ceil,identity,job_id,path );
        }

        ServletOutputStream ops =null;
        try {
            String[] split = path.split("\\.");
            log.info(thump_Path);
            File file = new File(thump_Path);
            log.info(file.getName());
            FileInputStream fis = new FileInputStream(file);
//            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
//
//            byte[] b = new byte[1024];
//
//            int n;
//            while ((n=fis.read(b))!=-1){
//                bos.write(b,0,n);
//            }
//            fis.close();
//            byte[] data = bos.toByteArray();
//            bos.close();
            byte[] bytes = IOUtils.toByteArray(fis);
            log.info(String.valueOf(bytes));
            return bytes;
//            return AjaxResult.success(base64Str+encode);
        }catch (Exception e){
            log.info(e.getMessage());
            resp.setStatus(500,e.getMessage());
            PrintWriter writer = resp.getWriter();
            writer.write(e.getMessage());
            resp.setCharacterEncoding("utf-8");
            return null;
//            return AjaxResult.error("图片获取失败");
        }finally {
            if (ops!=null){
            ops.flush();
            ops.close();
            }
        }
        //为空则直接拿图片 走逻辑

        //不为空
        // 先根据用户id 拿去对应的群组id
        // 根据群组id获取 对应的的群组信息
        //判断子账号是否在群组信息中，在 拿图片，不在则返回错误信息


        //根据用户id    (id/500 )*500取整 获取到他的文件组
    }
}
