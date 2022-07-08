package com.renderg.system.controller;

import com.renderg.system.service.ClientZipProgressService;
import com.renderg.system.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author chy
 * @date 2022年6月24日10:12:21
 */

@RestController
@RequestMapping("/client")
public class ClientZipProgressController {

    @Autowired
    private ClientZipProgressService clientZipProgressService;

    @ApiOperation(value = "解压进度")
    @GetMapping("/zip/progress")
    public R ZipProgress(@RequestParam(value = "zipId") String zipId) throws IOException {
        Integer progress = clientZipProgressService.ZipProgress(zipId);
        if (progress == 100) {
            return R.ok().data("state", "解压完成");
        }
        if (progress==0){
            return R.error().message("解压失败");
        }else {
            return R.ok().data("progress", progress);
        }
    }

}
