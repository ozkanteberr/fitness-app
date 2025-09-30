package com.fitness.aiservice.service.impl;

import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {
    @RabbitListener(queues = "activity.queue")
    public void processMessage(Activity activity){
        log.info("Received activity for processing:{}",activity.getId());
    }
}
