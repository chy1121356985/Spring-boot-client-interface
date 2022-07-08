package com.renderg.system.utils;


import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.Claim;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


public class TokenUtils {

    public static void token() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();


            String authorization = request.getHeader("Authorization");
            if (StringUtils.isNotEmpty(authorization) && authorization.startsWith(Constants.TOKEN_PREFIX)) {
                authorization = authorization.replace(Constants.TOKEN_PREFIX, "");
            }
         String sync = HttpUtils.sendPost("http://10.6.6.83:3006/system/renderg/t?Authorization="+authorization, "", authorization);
        String replace = sync.replace("=", ":");
        Constants.userJson = JSONObject.parseObject(replace);
        if (Constants.userJson.isEmpty()) {
            new Exception().printStackTrace();
        }
    }


    public static Map<String, Claim> getStringToMap(String str) {
        // 判断str是否有值
        if (null == str || "".equals(str)) {
            return null;
        }
        // 根据&截取
        String[] strings = str.split("',");
        // 设置HashMap长度
        int mapLength = strings.length;
        Map<String, Claim> map = new HashMap<>(mapLength);
        // 循环加入map集合
        for (String string : strings) {
            // 截取一组字符串
            String[] strArray = string.split(":");
            // strArray[0]为KEY strArray[1]为值
//            map.put(strArray[0], strArray[1]);
        }
        return map;
    }
}
