package com.example.SpringKafkaRedis.controller;

import com.example.SpringKafkaRedis.model.Model;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.TreeSet;

@RestController
@RequestMapping("/user")
public class Controller {
    Logger log = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private RedisCommands<String,String> commands;

    private String kafkaTopicName;
    private String kafkaTopicKey;
    private KafkaTemplate<String, String> kafkaTemplate;

    public Controller(@Value("${kafka.topic.name}") String kafkaTopicName,
                                       KafkaTemplate<String, String> kafkaTemplate,
                                       @Value("${kafka.topic.key}")  String kafkaTopicKey)
    {
        this.kafkaTopicName = kafkaTopicName;
        this.kafkaTopicKey = kafkaTopicKey;
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/{username}")
    private Model user(@PathVariable String username) {
        log.info("Get data for username: {}", username);
        kafkaTemplate.send(kafkaTopicName, kafkaTopicKey, username);
        final Model response = new Model();
        List<String> privileges = commands.lrange(username, 0L, 1000L);
        response.setRoles(new TreeSet<>(privileges));
        return response;
    }
}
