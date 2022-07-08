package com.renderg.system.service.impl;

import com.renderg.system.event.Users;
import com.renderg.system.mapper.ClientInterFaceMapper;
import com.renderg.system.service.ClientIpService;
import com.renderg.system.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author chy
 */
@Service
public class ClientIpServiceImpl implements ClientIpService {

    @Autowired
    private ClientInterFaceMapper clientInterFaceMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Object findIp(String ip) {

        Object user_id = null;

        user_id = redisUtil.get(ip);
        String[] user_ids = null;
        if (user_id == null) {
            ArrayList<Users> users = null;

            users = clientInterFaceMapper.selectByGroupId(ip);
            int size = 0;
            if (users.size() > 0) {
                for (int i = 0; i < users.size(); i++) {
                    if ((users.get(i)) != null) {
                        size++;
                    }
                }
            } else {
                return null;
            }
            user_ids = new String[size];
            if (users.size() > 0) {
                for (int i = 0; i < users.size(); i++) {
                    if ((users.get(i)) != null) {
                        user_ids[i] = users.get(i).getUser_id();
                    }
                }
                redisUtil.set(ip, user_ids, 7200L);
            }

        } else {
            return user_id;
        }
        return user_ids;


    }

}
