package com.renderg.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class CoolImageVo {

    String job_id;
    @JsonProperty("img_path")
    String path;
    @JsonProperty("width_index")
    Integer c;
    @JsonProperty("height_index")
    Integer r;
    @JsonProperty("region_left")
    Integer x;
    @JsonProperty("region_top")
    Integer y;
    @JsonProperty("seconds")
    String s;
    @JsonProperty("width")
    Integer w;
    @JsonProperty("height")
    Integer h;
    @JsonProperty("camera")
    String cam;
    String data;

    @Tolerate
    public CoolImageVo() {
    }



}





