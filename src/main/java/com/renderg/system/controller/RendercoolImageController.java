package com.renderg.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.renderg.system.domain.vo.CoolImageVo;
import com.renderg.system.utils.AjaxResult;
import com.renderg.system.utils.Constants;
import com.renderg.system.utils.TokenUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

@RestController
@RequestMapping("/api")
public class RendercoolImageController {

    private static final Logger log = LoggerFactory.getLogger(ClientImageController.class);

    @RequestMapping(value = "/v1/sketch/thumb/tiles/image/{random}", method = RequestMethod.GET)
    public AjaxResult image(CoolImageVo imageVo, HttpServletResponse resp) throws IOException {

        String job_id = null;
        String path = null;
        Integer width_index = null;
        Integer height_index = null;
        Integer region_left = null;
        Integer region_top = null;
        String seconds = null;
        Integer width = null;
        Integer height = null;
        String camera = null;

        Integer identity = null;
        CoolImageVo image = new CoolImageVo();

        TokenUtils.token();
        if (Constants.userJson.isEmpty()) {
            resp.setStatus(403);
            return null;
        }
        String thump_Path = "/mnt/thumb/%s/%s/%s/%s";
        String base64Str = "data:image/jpg;base64,";

        try {
            JSONObject userMap = Constants.userJson;
            //用户id
            identity = Integer.valueOf(userMap.get("identity").toString());
        } catch (Exception e) {
            resp.setStatus(403);
            return null;
//            return AjaxResult.error("token验证失败");
        }
        try {
            job_id = imageVo.getJob_id();
            path = imageVo.getPath();
            width_index = imageVo.getC();
            height_index = imageVo.getR();
            region_left = imageVo.getX();
            region_top = imageVo.getY();
            seconds = imageVo.getS();
            width = imageVo.getW();
            height = imageVo.getH();
            camera = imageVo.getCam();
        } catch (Exception e) {
            resp.setStatus(500);
        }


        int ceil = (int) Math.ceil((identity / 500) * 500);
        //字符替换
        thump_Path = String.format(thump_Path, ceil, identity, job_id, path);


        ServletOutputStream ops = null;
        try {
            log.info(thump_Path);
            File file = new File(thump_Path);
            log.info(file.getName());
            String base = Base64.encodeBase64String(RendercoolImageController.fileToByte(file));

            image.setJob_id(job_id);
            image.setC(width_index);
            image.setR(height_index);
            image.setX(region_left);
            image.setY(region_top);
            image.setS(seconds);
            image.setW(width);
            image.setH(height);
            image.setCam(camera);
            image.setData(base64Str + base);
            log.info(job_id + path);
            return AjaxResult.success(image);


        } catch (Exception e) {
            log.info(e.getMessage());
            resp.setStatus(500, e.getMessage());
            PrintWriter writer = resp.getWriter();
            writer.write(e.getMessage());
            resp.setCharacterEncoding("utf-8");
            return null;
        } finally {
            if (ops != null) {
                ops.flush();
                ops.close();
            }
        }
    }

    /**
     * 文件File类型转byte[]
     *
     * @param file
     * @return
     */
    private static byte[] fileToByte(File file) {
        byte[] fileBytes = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fileBytes = new byte[(int) file.length()];
            fis.read(fileBytes);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileBytes;
    }
}
