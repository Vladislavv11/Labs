package com.project;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public static final String ROUTING_KEY = "queue-path";
    public void getConsumer() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(ROUTING_KEY,false,false,false,null);

        int prefetchCount = 1;
        channel.basicQos(prefetchCount, true);

        // Створюємо пул воркерів
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Перевизначаємо функцію handle, щоб вона була здатна обробляти повідомлення по одному
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("Received message = "+message);

            try {
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (Exception e) {

                System.out.println("Error processing message: " + e.getMessage());


                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
            }
        };

        //воркери у окремих потоках
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    channel.basicConsume(ROUTING_KEY, false, deliverCallback, consumerTag -> {});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}

