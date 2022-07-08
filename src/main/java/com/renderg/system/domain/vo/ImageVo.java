package com.renderg.system.domain.vo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class ImageVo {
    String job_id;
    String task_id;
    String path;
    String child_user_id;

    @Tolerate
    public ImageVo() {
    }
}
