package com.renderg.system.kafka;

import com.alibaba.fastjson.JSONObject;
import com.renderg.system.controller.ClientImageController;
import com.renderg.system.service.impl.ClientInterFaceServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class KafkaListen {

    private static final Logger logger = LoggerFactory.getLogger(KafkaListen.class);

    @Autowired
    private ClientInterFaceServiceImpl clientInterFaceService;

    @KafkaListener(id = "aspera-file-event-java", topics = "aspera-file-events")
    public void listen(String input) {
        JSONObject asperaFileEvents = JSONObject.parseObject(input);


        String user = asperaFileEvents.getString("user");
        if (user.equals("wlcb")){
        //51
            asperaFileEvents.put("user_id",51);
            asperaFileEvents.put("cluster_id",22);
        }else{
            String[] userByarr = user.split("_");
            if (userByarr.length>3){
                asperaFileEvents.put("user_id",Integer.valueOf(userByarr[2]));
                asperaFileEvents.put("cluster_id",Integer.valueOf(userByarr[1]));
            }
        }
        LocalDateTime now = LocalDateTime.now();
        asperaFileEvents.put("create_time", Timestamp.valueOf(now));
        clientInterFaceService.insertAsperaFileEvents(asperaFileEvents);
//        logger.info("input value: {}" , input);
    }

}
