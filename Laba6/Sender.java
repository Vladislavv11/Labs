package com.project;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Sender {
    public static final String ROUTING_KEY = "queue-path";

    public void send(String path, int size) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.queueDeclare(ROUTING_KEY, false, false, false, null);

            Map<String, Object> message = new HashMap<>();
            message.put("path", path);
            message.put("size", size);

            String messageString = new Gson().toJson(message);
            channel.basicPublish("", ROUTING_KEY, false, null, messageString.getBytes());

            System.out.println(messageString + "\nhas been sent");
        }
    }
}
