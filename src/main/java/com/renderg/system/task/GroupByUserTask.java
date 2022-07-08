package com.renderg.system.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.renderg.system.service.IClientInterFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Configuration
@EnableScheduling
@EnableAsync
public class GroupByUserTask {
    /**
     * 获取群组信息
     */

    @Autowired
    private IClientInterFaceService clientInterFaceService;
    @Autowired
    private RedisTemplate redisTemplate;

    //每5分钟轮询一次
    @Scheduled(cron = "0 0/5 * * * ?")
    public void GroupByUserTask(){
        //查询users表有效用户
        ArrayList<JSONObject>  userByCag =  clientInterFaceService.selectUserByCagID();

        userByCag.forEach(e->{
            Integer group_id = e.getInteger("group_id");
            ArrayList<JSONObject>  userGroup =  clientInterFaceService.selectUserByGroupId(group_id);
            JSONArray userGroup_JSONArray = new JSONArray();
            userGroup_JSONArray.addAll(userGroup);
            redisTemplate.opsForValue().set("user:group:"+group_id,userGroup_JSONArray.toJSONString());
            redisTemplate.opsForValue().set("user:"+e.getInteger("id"),group_id);
        });
    }
}
