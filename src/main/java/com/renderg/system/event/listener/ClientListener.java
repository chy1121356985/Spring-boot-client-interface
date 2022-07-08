package com.renderg.system.event.listener;

import com.renderg.system.event.ClientEvent;
import com.renderg.system.utils.RunTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@EnableAsync
public class ClientListener implements ApplicationListener<ClientEvent> {

    @Autowired
    private RunTimeUtils runTimeUtils;

    @Override
    @Async
    public void onApplicationEvent(ClientEvent clientEvent) {

        switch (clientEvent.getStauts()) {
            case "copy":
                RunTimeUtils.ClientCopy(clientEvent.getJsonArray(), clientEvent.getCluster_id());
                break;
            case "MV":
                RunTimeUtils.ClientMV(clientEvent.getJsonArray(), clientEvent.getCluster_id());
                break;
            case "UNZIP":
                runTimeUtils.CilentZIP(clientEvent.getJsonArray(), clientEvent.getCluster_id(), clientEvent.getZipId());
                break;
            case "Compress":
                RunTimeUtils.Compress(clientEvent.getJsonArray(), clientEvent.getCluster_id());
                break;
            default:
        }
    }
}
