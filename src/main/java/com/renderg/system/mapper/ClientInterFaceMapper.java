package com.renderg.system.mapper;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.renderg.system.event.Users;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
@Component
public interface ClientInterFaceMapper {

    @DS("slave")
    ArrayList<JSONObject> selectUserByCagID();
    @DS("slave")
    ArrayList<JSONObject> selectUserByGroupId(Integer group_id);
    @DS("slave")
    String selectclusterNameById(Integer cluster_id);

    void insertAsperaFileEvents(JSONObject asperaFileEvents);

    ArrayList<Users> selectByGroupId(String ip);

}
