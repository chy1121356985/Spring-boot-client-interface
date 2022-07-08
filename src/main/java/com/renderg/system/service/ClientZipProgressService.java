package com.renderg.system.service;

import java.io.IOException;


public interface ClientZipProgressService {

    /**
     * 计算压缩包完成进度
     * @param zipId
     * @return 完成进度百分比
     */
    Integer ZipProgress(String zipId) throws IOException;
}
