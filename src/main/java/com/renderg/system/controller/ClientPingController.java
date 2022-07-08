package com.renderg.system.controller;

import com.renderg.system.utils.AjaxResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/ping")
public class ClientPingController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public AjaxResult ping() {

        return AjaxResult.success();
    }
}
