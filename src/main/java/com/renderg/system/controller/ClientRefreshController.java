package com.renderg.system.controller;

import com.renderg.system.utils.AjaxResult;
import com.renderg.system.utils.RunTimeUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/client/refresh")
public class ClientRefreshController {



    @RequestMapping(value = "",method = RequestMethod.GET)
    public void refresh() throws IOException {
        RunTimeUtils.refresh();
    }
}
