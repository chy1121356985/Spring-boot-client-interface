package com.renderg.system.controller;


import com.renderg.system.service.ClientIpService;
import com.renderg.system.utils.Constants;
import com.renderg.system.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chy
 */
@RestController
@RequestMapping("/client/ip")
public class ClientIpController {

    @Autowired
    private ClientIpService clientIpService;

    @ApiOperation(value = "通过ip查询用户id")
    @GetMapping("/findUserId")
    public R findIp(@RequestParam(value = "ip") String ip, HttpServletRequest request) {
        if (Constants.Authorization.equals(request.getHeader("Authorization"))) {
            Object user_ids = clientIpService.findIp(ip);
            if (user_ids==null) {
                return R.error().data("msg", "未查到用户id");
            } else {
                return R.ok().data("user_ids", user_ids);
            }
        } else {
            return R.error().data("msg", "token错误");
        }


    }
}
