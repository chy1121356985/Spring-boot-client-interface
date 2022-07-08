package com.renderg.system.event;

import com.alibaba.fastjson.JSONArray;
import org.springframework.context.ApplicationEvent;

public class ClientEvent extends ApplicationEvent {

    JSONArray jsonArray;
    String stauts;
    Integer cluster_id;
    String zipId;

    public ClientEvent(Object source, JSONArray jsonArray, String stauts, Integer cluster_id, String zipId) {
        super(source);
        this.jsonArray = jsonArray;
        this.stauts = stauts;
        this.cluster_id = cluster_id;
        this.zipId = zipId;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public String getStauts() {
        return stauts;
    }

    public void setStauts(String stauts) {
        this.stauts = stauts;
    }

    public Integer getCluster_id() {
        return cluster_id;
    }

    public void setCluster_id(Integer cluster_id) {
        this.cluster_id = cluster_id;
    }

    public String getZipId() {
        return zipId;
    }

    public void setZipId(String zipId) {
        this.zipId = zipId;
    }
}
