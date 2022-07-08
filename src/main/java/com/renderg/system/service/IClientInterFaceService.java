package com.renderg.system.service;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

public interface IClientInterFaceService {


    ArrayList<JSONObject> selectUserByCagID();

    ArrayList<JSONObject> selectUserByGroupId(Integer group_id);

    String selectclusterNameById(Integer cluster_id);

}
