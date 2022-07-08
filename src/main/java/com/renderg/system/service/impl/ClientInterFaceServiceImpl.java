package com.renderg.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.renderg.system.mapper.ClientInterFaceMapper;
import com.renderg.system.service.IClientInterFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ClientInterFaceServiceImpl implements IClientInterFaceService {

    @Autowired
    private ClientInterFaceMapper clientInterFaceMapper;

    @Override
    public ArrayList<JSONObject> selectUserByCagID() {
       return clientInterFaceMapper.selectUserByCagID();
    }

    @Override
    public ArrayList<JSONObject> selectUserByGroupId(Integer group_id) {
        return clientInterFaceMapper.selectUserByGroupId(group_id);
    }

    @Override
    public String selectclusterNameById(Integer cluster_id) {
        return clientInterFaceMapper.selectclusterNameById(cluster_id);
    }

    public void insertAsperaFileEvents(JSONObject asperaFileEvents) {
        clientInterFaceMapper.insertAsperaFileEvents(asperaFileEvents);
    }
}
