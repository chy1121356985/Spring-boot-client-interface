package com.renderg.system.controller;


import com.alibaba.fastjson.JSONObject;
import com.renderg.system.utils.AjaxByFilesResult;
import com.renderg.system.utils.FileEventUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/client/files")
public class ClientInterFaceController {


    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private FileEventUtils fileEventUtils;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/copy",method = RequestMethod.POST)
    public AjaxByFilesResult copy(@RequestBody JSONObject paths){
        return fileEventUtils.fileEvent(paths,"copy");
    }
    @RequestMapping(value = "/move",method = RequestMethod.POST)
    public AjaxByFilesResult move(@RequestBody JSONObject paths){
        return fileEventUtils.fileEvent(paths,"MV");
    }

    @RequestMapping(value = "/unzip",method = RequestMethod.POST)
    public AjaxByFilesResult unzip(@RequestBody JSONObject paths){
        return fileEventUtils.fileEvent(paths,"UNZIP");
    }


    @RequestMapping(value = "/compress",method = RequestMethod.POST)
    public AjaxByFilesResult compress(@RequestBody JSONObject paths){
        return fileEventUtils.fileEvent(paths,"Compress");
    }








    public static void main(String[] args) {
        int ceil = (int) Math.ceil((5132/500)*500);
        System.out.println(ceil);
    }
}
